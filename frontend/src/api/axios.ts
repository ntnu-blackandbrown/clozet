import axios from 'axios'

// Create axios instance with base configuration
const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080', // adjust to your backend URL
  withCredentials: true, // Enable cookies with all requests
})

// Flag to prevent multiple refresh requests
let isRefreshing = false
// Queue of failed requests to retry after token refresh
interface QueueItem {
  resolve: (value?: unknown) => void;
  reject: (error: any) => void;
}
let failedQueue: QueueItem[] = []

// Process failed queue - either retry all requests or reject them
const processQueue = (error: any, token = null) => {
  console.log(`Processing queue with ${failedQueue.length} requests. Error: ${error ? 'Yes' : 'No'}`)
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve()
    }
  })
  failedQueue = []
}

// Response interceptor for handling 401 errors
axiosInstance.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config

    // If error is not 401 or request has already been retried, reject immediately
    if (error.response?.status !== 401 || originalRequest._retry) {
      return Promise.reject(error)
    }

    console.log('🔑 Token expired (401 error). Original request:', originalRequest.url)

    // Flag to prevent infinite retry loops
    originalRequest._retry = true

    // If already refreshing, queue this request
    if (isRefreshing) {
      console.log('⏳ Refresh already in progress, queueing request to:', originalRequest.url)
      return new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject })
      })
        .then(() => {
          console.log('✅ Retrying request after queue processing:', originalRequest.url)
          return axiosInstance(originalRequest)
        })
        .catch(err => {
          console.log('❌ Failed to retry request after queue processing:', originalRequest.url)
          return Promise.reject(err)
        })
    }

    isRefreshing = true
    console.log('🔄 Starting token refresh')

    try {
      // Call refresh token endpoint
      console.log('📤 Calling refresh token endpoint')
      await axiosInstance.post('/api/auth/refresh-token')
      console.log('📥 Token refreshed successfully')

      // Process queued requests
      processQueue(null)

      // Retry the original request
      console.log('🔁 Retrying original request to:', originalRequest.url)
      return axiosInstance(originalRequest)
    } catch (refreshError) {
      // If refresh fails, process queue with error and reject
      console.error('❌ Token refresh failed:', refreshError)
      processQueue(refreshError)
      return Promise.reject(refreshError)
    } finally {
      isRefreshing = false
      console.log('🏁 Token refresh process completed')
    }
  }
)

export default axiosInstance
