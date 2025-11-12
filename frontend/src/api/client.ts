import axios from 'axios'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:8080',
  withCredentials: true
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    // global error hook (e.g., show toast)
    return Promise.reject(err)
  }
)
