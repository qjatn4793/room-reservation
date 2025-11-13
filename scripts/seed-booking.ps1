param(
    [int]   $TotalRequests = 500,                     # 총 요청 수
    [int]   $Concurrency   = 20,                      # 동시에 보낼 최대 개수
    [string]$BookingUrl    = "http://localhost:8082/booking"  # /booking 엔드포인트
)

Write-Host "=== Booking Load Test ===" -ForegroundColor Cyan
Write-Host "TotalRequests = $TotalRequests"
Write-Host "Concurrency   = $Concurrency"
Write-Host "BookingUrl    = $BookingUrl"
Write-Host ""

# 결과를 담을 리스트
$results = New-Object System.Collections.Generic.List[object]

$sw = [System.Diagnostics.Stopwatch]::StartNew()

if ($PSVersionTable.PSVersion.Major -ge 7) {
    Write-Host "PowerShell 7+ detected → using ForEach-Object -Parallel" -ForegroundColor Green

    $results = 1..$TotalRequests | ForEach-Object -Parallel {
        param($url)

        $body = @{
            roomId   = 1
            userId   = "user-$([guid]::NewGuid())"
            checkIn  = "2025-11-20"
            checkOut = "2025-11-22"
            amount   = 150000
        }

        $json = $body | ConvertTo-Json -Depth 5

        try {
            $res = Invoke-RestMethod `
                -Method POST `
                -Uri $url `
                -ContentType "application/json" `
                -Body $json `
                -TimeoutSec 30

            [pscustomobject]@{
                Ok            = $true
                ReservationId = $res.reservationId
                Status        = $res.status
            }
        }
        catch {
            [pscustomobject]@{
                Ok    = $false
                Error = $_.Exception.Message
            }
        }
    } -ArgumentList $BookingUrl -ThrottleLimit $Concurrency

} else {
    Write-Host "PowerShell 5.x detected → using Start-Job" -ForegroundColor Yellow

    $jobScript = {
        param($url)

        $body = @{
            roomId   = 1
            userId   = "user-$([guid]::NewGuid())"
            checkIn  = "2025-11-20"
            checkOut = "2025-11-22"
            amount   = 150000
        }

        $json = $body | ConvertTo-Json -Depth 5

        try {
            $res = Invoke-RestMethod `
                -Method POST `
                -Uri $url `
                -ContentType "application/json" `
                -Body $json `
                -TimeoutSec 30

            [pscustomobject]@{
                Ok            = $true
                ReservationId = $res.reservationId
                Status        = $res.status
            }
        }
        catch {
            [pscustomobject]@{
                Ok    = $false
                Error = $_.Exception.Message
            }
        }
    }

    $jobs = @()

    for ($i = 1; $i -le $TotalRequests; $i++) {

        # 현재 Running 인 job 이 Concurrency 이상이면 잠깐 대기
        while ( ($jobs | Where-Object { $_.State -eq 'Running' }).Count -ge $Concurrency ) {
            Start-Sleep -Milliseconds 50
        }

        $jobs += Start-Job -ScriptBlock $jobScript -ArgumentList $BookingUrl
    }

    # 모든 job 완료 대기
    Write-Host "Waiting for jobs to complete..." -ForegroundColor Cyan
    Wait-Job -Job $jobs | Out-Null

    # 결과 수집
    $results.AddRange( ($jobs | Receive-Job) )

    # job 정리
    Remove-Job -Job $jobs -Force | Out-Null
}

$sw.Stop()

# 통계 집계
$total   = $results.Count
$success = ($results | Where-Object { $_.Ok }).Count
$failed  = $total - $success

$rps = if ($sw.Elapsed.TotalSeconds -gt 0) {
    $total / $sw.Elapsed.TotalSeconds
} else {
    0
}

Write-Host ""
Write-Host "=== Result Summary ===" -ForegroundColor Cyan
Write-Host ("Total Requests : {0}"    -f $total)
Write-Host ("Success        : {0}"    -f $success) -ForegroundColor Green
Write-Host ("Failed         : {0}"    -f $failed)  -ForegroundColor (if ($failed -gt 0) { 'Red' } else { 'Green' })
Write-Host ("Duration       : {0:N2} sec" -f $sw.Elapsed.TotalSeconds)
Write-Host ("Throughput     : {0:N2} req/sec" -f $rps)

if ($failed -gt 0) {
    Write-Host ""
    Write-Host "=== Sample Errors ===" -ForegroundColor Yellow
    $results |
        Where-Object { -not $_.Ok } |
        Select-Object -First 5 |
        Format-Table -AutoSize
}
else {
    Write-Host ""
    Write-Host "No errors detected 🎉" -ForegroundColor Green
}
