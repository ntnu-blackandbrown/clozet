import axios from 'axios'

// Enable cookies with all requests
axios.defaults.withCredentials = true

// Base URL configuration
axios.defaults.baseURL = 'http://localhost:8080' // adjust to your backend URL

export default axios
