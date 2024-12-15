import axios from 'axios';

export const useVehiculoService = () => {
  const crearVehiculo = async (vehiculo) => {
    try {
      const {data} = await axios.post('/api/vehiculo/crear', vehiculo);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerVehiculos = async () => {
    try {
      const {data} = await axios.get('/api/vehiculo/listing');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerVehiculosDisponiblesEnSucursal = async (id) => {
    try {
      const {data} = await axios.get(`/api/vehiculo/listing/enSucursal?id=${id}`);
      return data;
    } catch (error) {
      throw error.response.data;
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
    crearVehiculo,
    obtenerVehiculos,
    obtenerVehiculosDisponiblesEnSucursal,
    eliminarVehiculo,
    actualizarVehiculo
  };
};
