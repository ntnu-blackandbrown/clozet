import axios from 'axios'

// Enable cookies with all requests
axios.defaults.withCredentials = true

// Base URL configuration
axios.defaults.baseURL = 'https://clozet-backend-083bdce61007.herokuapp.com/' // adjust to your backend URL

export default axios
