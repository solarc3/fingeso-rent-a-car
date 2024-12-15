import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: 'http://localhost:3000', // Ajusta según tu backend
  headers: {
    'Content-Type': 'application/json',
  }
});

export default axiosInstance;
