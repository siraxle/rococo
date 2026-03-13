import { defineConfig } from 'vite';
import { sveltekit } from '@sveltejs/kit/vite';

export default defineConfig({
    plugins: [sveltekit()],
    server: {
        port: 3000,
        proxy: {
            // Прокси для API запросов
            '/api': {
                target: 'http://localhost:8081',
                changeOrigin: true
            },
            // Прокси для изображений
            '/images': {
                target: 'http://localhost:8081',
                changeOrigin: true
            }
        }
    }
});