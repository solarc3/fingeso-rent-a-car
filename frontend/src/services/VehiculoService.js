import axios from 'axios';

import axiosInstance from './axiosConfig';

export const useVehiculoService = () => {
  const obtenerVehiculos = async () => {
    try {
      const {data} = await axiosInstance.get('/api/vehiculo/listing');
      return data;
    } catch (error) {
      console.error('Error al obtener vehículos:', error.response || error);
      throw error;
    }
  };
  const crearVehiculo = async (vehiculo) => {
    try {
      const {data} = await axios.post('/api/vehiculo/crear', vehiculo);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };
  const obtenerVehiculosDisponiblesEnSucursal = async (idSucursal) => {
    try {
      const {data} = await axiosInstance.get('/api/vehiculo/listing/enSucursal', {
        params: {
          idSucursal // Nombre del parametro debe coincidir con @RequestParam
        }
      });
      return data;
    } catch (error) {
      console.error('Error al obtener vehículos por sucursal:', error);
      throw error;
    }
  };

  const eliminarVehiculo = async (id) => {
    try {
      const {data} = await axios.delete(`/api/vehiculo/eliminar?id=${id}`);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const actualizarVehiculo = async (vehiculo) => {
    try {
      const {data} = await axios.put('/api/vehiculo/actualizar', vehiculo);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  return {
    obtenerVehiculos,
    obtenerVehiculosDisponiblesEnSucursal,
    crearVehiculo,
    eliminarVehiculo,
    actualizarVehiculo
  };
};
