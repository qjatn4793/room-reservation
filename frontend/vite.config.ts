import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
    plugins: [react()],
    server: {
        port: 5173,
        proxy: {
            // 프론트의 /api 요청을 백엔드(8080)로 프록시
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                // 백엔드가 /api로 시작한다면 rewrite 불필요
                // rewrite: path => path.replace(/^\/api/, '')
            }
        }
    }
})