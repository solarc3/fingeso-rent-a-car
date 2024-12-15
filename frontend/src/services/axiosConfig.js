import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080', // Ajusta según tu backend
  headers: {
    'Content-Type': 'application/json',
  }
});

export default axiosInstance;
