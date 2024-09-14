import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
     
    },
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'lodash'], 
        },
      },
    },
    chunkSizeWarningLimit: 1000, 
  },
  assetsInclude: ['**/*.png', '**/*.PNG', '**/*.jpg', '**/*.jpeg', '**/*.svg', '**/*.gif','**/*.WEBP','**/*.webp'], 
  server: {
    host: true, 
    port: 5174, 
    hmr: {
      overlay: false, 
    },
  },
});
