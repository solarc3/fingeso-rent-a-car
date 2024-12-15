import axios from 'axios';

export const useSucursalService = () => {
  const crearSucursal = async (sucursal) => {
    try {
      const {data} = await axios.post('/api/sucursal/crear', sucursal);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const listarSucursales = async () => {
    try {
      const {data} = await axios.get('/api/sucursal/listar');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerSucursalPorId = async (id) => {
    try {
      const {data} = await axios.get(`/api/sucursal/obtenerPorId?id=${id}`);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const actualizarSucursal = async (id, sucursal) => {
    try {
      const {data} = await axios.put(`/api/sucursal/actualizar?id=${id}`, sucursal);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const eliminarSucursal = async (id) => {
    try {
      const {data} = await axios.delete(`/api/sucursal/eliminar?id=${id}`);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  return {
    crearSucursal,
    listarSucursales,
    obtenerSucursalPorId,
    actualizarSucursal,
    eliminarSucursal
  };
};
