# seed-rooms.ps1
# ? 파일 인코딩은 UTF-8 (가능하면 UTF-8 with BOM) 로 저장!

# ===== 설정 =====
$BaseUrl = "http://localhost:8080"  # 게이트웨이
$Stays   = 100000
$Rooms   = 12000000

$ErrorActionPreference = "Stop"

[Console]::OutputEncoding = [Text.Encoding]::UTF8

function Invoke-JsonPost {
    param(
        [string]$Url,
        [hashtable]$Body
    )

    $json = $Body | ConvertTo-Json -Depth 5
    Write-Host "=== POST $Url ==="
    Write-Host "JSON BODY => $json"

    try {
        $resp = Invoke-RestMethod `
            -Method Post `
            -Uri $Url `
            -ContentType "application/json; charset=utf-8" `
            -Body $json

        return $resp
    }
    catch [System.Net.WebException] {
        $resp = $_.Exception.Response
        if ($resp -ne $null) {
            $stream = $resp.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($stream, [Text.Encoding]::UTF8)
            $body   = $reader.ReadToEnd()
            Write-Host "!!! HTTP ERROR $($resp.StatusCode) $($resp.StatusDescription)"
            Write-Host "Response Body => $body"
        }
        throw
    }
}

Write-Host "=== Seeding via GATEWAY ==="

# ?? 실제 생성된 stay id 들을 담을 리스트
$stayIds = New-Object System.Collections.Generic.List[long]

# 1) stays -> /api/stays
$locations = @(
    # 서울특별시
    "서울 종로구 (Jongno-gu, Seoul)",
    "서울 중구 (Jung-gu, Seoul)",
    "서울 용산구 (Yongsan-gu, Seoul)",
    "서울 성동구 (Seongdong-gu, Seoul)",
    "서울 광진구 (Gwangjin-gu, Seoul)",
    "서울 동대문구 (Dongdaemun-gu, Seoul)",
    "서울 중랑구 (Jungnang-gu, Seoul)",
    "서울 성북구 (Seongbuk-gu, Seoul)",
    "서울 강북구 (Gangbuk-gu, Seoul)",
    "서울 도봉구 (Dobong-gu, Seoul)",
    "서울 노원구 (Nowon-gu, Seoul)",
    "서울 은평구 (Eunpyeong-gu, Seoul)",
    "서울 서대문구 (Seodaemun-gu, Seoul)",
    "서울 마포구 (Mapo-gu, Seoul)",
    "서울 양천구 (Yangcheon-gu, Seoul)",
    "서울 강서구 (Gangseo-gu, Seoul)",
    "서울 구로구 (Guro-gu, Seoul)",
    "서울 금천구 (Geumcheon-gu, Seoul)",
    "서울 영등포구 (Yeongdeungpo-gu, Seoul)",
    "서울 동작구 (Dongjak-gu, Seoul)",
    "서울 관악구 (Gwanak-gu, Seoul)",
    "서울 서초구 (Seocho-gu, Seoul)",
    "서울 강남구 (Gangnam-gu, Seoul)",
    "서울 송파구 (Songpa-gu, Seoul)",
    "서울 강동구 (Gangdong-gu, Seoul)",

    # 인천광역시
    "인천 중구 (Jung-gu, Incheon)",
    "인천 동구 (Dong-gu, Incheon)",
    "인천 미추홀구 (Michuhol-gu, Incheon)",
    "인천 연수구 (Yeonsu-gu, Incheon)",
    "인천 남동구 (Namdong-gu, Incheon)",
    "인천 부평구 (Bupyeong-gu, Incheon)",
    "인천 계양구 (Gyeyang-gu, Incheon)",
    "인천 서구 (Seo-gu, Incheon)",
    "인천 강화군 (Ganghwa-gun, Incheon)",
    "인천 옹진군 (Ongjin-gun, Incheon)",

    # 경기
    "경기도 수원시 영통구 (Yeongtong-gu, Suwon-si, Gyeonggi-do)",
    "경기도 수원시 팔달구 (Paldal-gu, Suwon-si, Gyeonggi-do)",
    "경기도 성남시 분당구 (Bundang-gu, Seongnam-si, Gyeonggi-do)",
    "경기도 성남시 수정구 (Sujeong-gu, Seongnam-si, Gyeonggi-do)",
    "경기도 성남시 중원구 (Jungwon-gu, Seongnam-si, Gyeonggi-do)",
    "경기도 고양시 일산서구 (Ilsanseo-gu, Goyang-si, Gyeonggi-do)",
    "경기도 고양시 일산동구 (Ilsandong-gu, Goyang-si, Gyeonggi-do)",
    "경기도 용인시 수지구 (Suji-gu, Yongin-si, Gyeonggi-do)",
    "경기도 용인시 기흥구 (Giheung-gu, Yongin-si, Gyeonggi-do)",
    "경기도 용인시 처인구 (Cheoin-gu, Yongin-si, Gyeonggi-do)",
    "경기도 안양시 동안구 (Dongan-gu, Anyang-si, Gyeonggi-do)",
    "경기도 안양시 만안구 (Manan-gu, Anyang-si, Gyeonggi-do)",
    "경기도 부천시 (Bucheon-si, Gyeonggi-do)",
    "경기도 안산시 단원구 (Danwon-gu, Ansan-si, Gyeonggi-do)",
    "경기도 안산시 상록구 (Sangnok-gu, Ansan-si, Gyeonggi-do)",
    "경기도 남양주시 (Namyangju-si, Gyeonggi-do)",
    "경기도 의정부시 (Uijeongbu-si, Gyeonggi-do)",
    "경기도 파주시 (Paju-si, Gyeonggi-do)",
    "경기도 김포시 (Gimpo-si, Gyeonggi-do)",
    "경기도 광주시 (Gwangju-si, Gyeonggi-do)",
    "경기도 하남시 (Hanam-si, Gyeonggi-do)",
    "경기도 평택시 (Pyeongtaek-si, Gyeonggi-do)",
    "경기도 시흥시 (Siheung-si, Gyeonggi-do)",
    "경기도 군포시 (Gunpo-si, Gyeonggi-do)",
    "경기도 의왕시 (Uiwang-si, Gyeonggi-do)",
    "경기도 오산시 (Osan-si, Gyeonggi-do)",
    "경기도 이천시 (Icheon-si, Gyeonggi-do)",
    "경기도 여주시 (Yeoju-si, Gyeonggi-do)",
    "경기도 양평군 (Yangpyeong-gun, Gyeonggi-do)",
    "경기도 가평군 (Gapyeong-gun, Gyeonggi-do)",
    "경기도 연천군 (Yeoncheon-gun, Gyeonggi-do)",

    # 부산광역시
    "부산 해운대구 (Haeundae-gu, Busan)",
    "부산 수영구 (Suyeong-gu, Busan)",
    "부산 남구 (Nam-gu, Busan)",
    "부산 동래구 (Dongnae-gu, Busan)",
    "부산 부산진구 (Busanjin-gu, Busan)",
    "부산 북구 (Buk-gu, Busan)",
    "부산 사하구 (Saha-gu, Busan)",
    "부산 사상구 (Sasang-gu, Busan)",
    "부산 영도구 (Yeongdo-gu, Busan)",
    "부산 중구 (Jung-gu, Busan)",
    "부산 서구 (Seo-gu, Busan)",
    "부산 금정구 (Geumjeong-gu, Busan)",
    "부산 강서구 (Gangseo-gu, Busan)",
    "부산 기장군 (Gijang-gun, Busan)",

    # 대구광역시
    "대구 수성구 (Suseong-gu, Daegu)",
    "대구 중구 (Jung-gu, Daegu)",
    "대구 동구 (Dong-gu, Daegu)",
    "대구 서구 (Seo-gu, Daegu)",
    "대구 남구 (Nam-gu, Daegu)",
    "대구 북구 (Buk-gu, Daegu)",
    "대구 달서구 (Dalseo-gu, Daegu)",
    "대구 달성군 (Dalseong-gun, Daegu)",

    # 광주광역시
    "광주 동구 (Dong-gu, Gwangju)",
    "광주 서구 (Seo-gu, Gwangju)",
    "광주 남구 (Nam-gu, Gwangju)",
    "광주 북구 (Buk-gu, Gwangju)",
    "광주 광산구 (Gwangsan-gu, Gwangju)",

    # 대전광역시
    "대전 유성구 (Yuseong-gu, Daejeon)",
    "대전 서구 (Seo-gu, Daejeon)",
    "대전 중구 (Jung-gu, Daejeon)",
    "대전 동구 (Dong-gu, Daejeon)",
    "대전 대덕구 (Daedeok-gu, Daejeon)",

    # 울산광역시
    "울산 남구 (Nam-gu, Ulsan)",
    "울산 중구 (Jung-gu, Ulsan)",
    "울산 동구 (Dong-gu, Ulsan)",
    "울산 북구 (Buk-gu, Ulsan)",
    "울산 울주군 (Ulju-gun, Ulsan)",

    # 세종특별자치시
    "세종특별자치시 보람동 (Boram-dong, Sejong-si)",
    "세종특별자치시 한솔동 (Hansol-dong, Sejong-si)",
    "세종특별자치시 다정동 (Dajeong-dong, Sejong-si)",

    # 강원특별자치도
    "강원도 춘천시 (Chuncheon-si, Gangwon-do)",
    "강원도 원주시 (Wonju-si, Gangwon-do)",
    "강원도 강릉시 (Gangneung-si, Gangwon-do)",
    "강원도 속초시 (Sokcho-si, Gangwon-do)",
    "강원도 동해시 (Donghae-si, Gangwon-do)",
    "강원도 삼척시 (Samcheok-si, Gangwon-do)",
    "강원도 홍천군 (Hongcheon-gun, Gangwon-do)",
    "강원도 평창군 (Pyeongchang-gun, Gangwon-do)",
    "강원도 정선군 (Jeongseon-gun, Gangwon-do)",
    "강원도 고성군 (Goseong-gun, Gangwon-do)",
    "강원도 양양군 (Yangyang-gun, Gangwon-do)",

    # 충청북도
    "충북 청주시 상당구 (Sangdang-gu, Cheongju-si, Chungbuk)",
    "충북 청주시 서원구 (Seowon-gu, Cheongju-si, Chungbuk)",
    "충북 청주시 흥덕구 (Heungdeok-gu, Cheongju-si, Chungbuk)",
    "충북 청주시 청원구 (Cheongwon-gu, Cheongju-si, Chungbuk)",
    "충북 충주시 (Chungju-si, Chungbuk)",
    "충북 제천시 (Jecheon-si, Chungbuk)",
    "충북 진천군 (Jincheon-gun, Chungbuk)",
    "충북 음성군 (Eumseong-gun, Chungbuk)",
    "충북 단양군 (Danyang-gun, Chungbuk)",

    # 충청남도
    "충남 천안시 동남구 (Dongnam-gu, Cheonan-si, Chungnam)",
    "충남 천안시 서북구 (Seobuk-gu, Cheonan-si, Chungnam)",
    "충남 아산시 (Asan-si, Chungnam)",
    "충남 공주시 (Gongju-si, Chungnam)",
    "충남 보령시 (Boryeong-si, Chungnam)",
    "충남 서산시 (Seosan-si, Chungnam)",
    "충남 당진시 (Dangjin-si, Chungnam)",
    "충남 논산시 (Nonsan-si, Chungnam)",
    "충남 예산군 (Yesan-gun, Chungnam)",
    "충남 서천군 (Seocheon-gun, Chungnam)",
    "충남 태안군 (Taean-gun, Chungnam)",

    # 전라북도
    "전북 전주시 완산구 (Wansan-gu, Jeonju-si, Jeonbuk)",
    "전북 전주시 덕진구 (Deokjin-gu, Jeonju-si, Jeonbuk)",
    "전북 군산시 (Gunsan-si, Jeonbuk)",
    "전북 익산시 (Iksan-si, Jeonbuk)",
    "전북 정읍시 (Jeongeup-si, Jeonbuk)",
    "전북 남원시 (Namwon-si, Jeonbuk)",
    "전북 김제시 (Gimje-si, Jeonbuk)",
    "전북 완주군 (Wanju-gun, Jeonbuk)",
    "전북 무주군 (Muju-gun, Jeonbuk)",
    "전북 진안군 (Jinan-gun, Jeonbuk)",

    # 전라남도
    "전남 목포시 (Mokpo-si, Jeonnam)",
    "전남 여수시 (Yeosu-si, Jeonnam)",
    "전남 순천시 (Suncheon-si, Jeonnam)",
    "전남 광양시 (Gwangyang-si, Jeonnam)",
    "전남 나주시 (Naju-si, Jeonnam)",
    "전남 여천군 돌산읍 (Dolsan-eup, Jeonnam)",
    "전남 해남군 (Haenam-gun, Jeonnam)",
    "전남 강진군 (Gangjin-gun, Jeonnam)",
    "전남 고흥군 (Goheung-gun, Jeonnam)",
    "전남 보성군 (Boseong-gun, Jeonnam)",
    "전남 담양군 (Damyang-gun, Jeonnam)",
    "전남 곡성군 (Gokseong-gun, Jeonnam)",
    "전남 구례군 (Gurye-gun, Jeonnam)",
    "전남 신안군 (Sinan-gun, Jeonnam)",

    # 경상북도
    "경북 포항시 남구 (Nam-gu, Pohang-si, Gyeongbuk)",
    "경북 포항시 북구 (Buk-gu, Pohang-si, Gyeongbuk)",
    "경북 경주시 (Gyeongju-si, Gyeongbuk)",
    "경북 구미시 (Gumi-si, Gyeongbuk)",
    "경북 김천시 (Gimcheon-si, Gyeongbuk)",
    "경북 안동시 (Andong-si, Gyeongbuk)",
    "경북 영주시 (Yeongju-si, Gyeongbuk)",
    "경북 영천시 (Yeongcheon-si, Gyeongbuk)",
    "경북 경산시 (Gyeongsan-si, Gyeongbuk)",
    "경북 문경시 (Mungyeong-si, Gyeongbuk)",
    "경북 울진군 (Uljin-gun, Gyeongbuk)",
    "경북 울릉군 (Ulleung-gun, Gyeongbuk)",

    # 경상남도
    "경남 창원시 의창구 (Uichang-gu, Changwon-si, Gyeongnam)",
    "경남 창원시 성산구 (Seongsan-gu, Changwon-si, Gyeongnam)",
    "경남 창원시 마산합포구 (Masan Happo-gu, Changwon-si, Gyeongnam)",
    "경남 창원시 마산회원구 (Masan Hoewon-gu, Changwon-si, Gyeongnam)",
    "경남 창원시 진해구 (Jinhae-gu, Changwon-si, Gyeongnam)",
    "경남 김해시 (Gimhae-si, Gyeongnam)",
    "경남 양산시 (Yangsan-si, Gyeongnam)",
    "경남 진주시 (Jinju-si, Gyeongnam)",
    "경남 통영시 (Tongyeong-si, Gyeongnam)",
    "경남 거제시 (Geoje-si, Gyeongnam)",
    "경남 사천시 (Sacheon-si, Gyeongnam)",
    "경남 남해군 (Namhae-gun, Gyeongnam)",
    "경남 하동군 (Hadong-gun, Gyeongnam)",

    # 제주특별자치도
    "제주특별자치도 제주시 (Jeju-si, Jeju-do)",
    "제주특별자치도 서귀포시 (Seogwipo-si, Jeju-do)",
    "제주특별자치도 애월읍 (Aewol-eup, Jeju-do)",
    "제주특별자치도 성산읍 (Seongsan-eup, Jeju-do)",
    "제주특별자치도 한림읍 (Hallim-eup, Jeju-do)"
)

for ($i = 1; $i -le $Stays; $i++) {
    $locIndex = Get-Random -Minimum 0 -Maximum $locations.Count
    $loc = $locations[$locIndex]

    $body = @{
        name     = "GoodStay Stay $i"
        location = $loc
    }

    $resp = Invoke-JsonPost "$BaseUrl/api/stays" $body
    Write-Host " -> Response: $($resp | ConvertTo-Json -Depth 5)"

    # ? 여기서 id를 리스트에 저장
    $stayIds.Add($resp.id)
}

Write-Host "총 Stay 개수: $($stayIds.Count)"
Write-Host "예시 stayIds: $stayIds[0], $stayIds[1] ..."

# 2) rooms -> /api/rooms
if ($stayIds.Count -eq 0) {
    throw "Stay가 하나도 없습니다. rooms를 생성할 수 없습니다."
}

for ($i = 1; $i -le $Rooms; $i++) {
    # ? 실제 존재하는 stayId 중에서 골라 쓰기
    $index   = ($i - 1) % $stayIds.Count
    $stayId  = $stayIds[$index]

    $maxPeople = Get-Random -Minimum 1 -Maximum 6
    $price     = (Get-Random -Minimum 5 -Maximum 31) * 10000

    $body = @{
        stayId    = $stayId
        name      = "Standard Room $i"
        maxPeople = $maxPeople
        price     = $price
    }

    $resp = Invoke-JsonPost "$BaseUrl/api/rooms" $body
    Write-Host " -> Response: $($resp | ConvertTo-Json -Depth 5)"
}

Write-Host "=== Seed DONE ==="
