import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true, // Expose the server to the network
    port: 5174, // Optional: Specify the port if needed
    hmr: {
      overlay: false, // Disable the error overlay
    },
  },
})
