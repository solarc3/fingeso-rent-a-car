import axiosInstance from "@/services/axiosConfig.js";

export const crearSucursal = async (sucursal) => {
  try {
    const { data } = await axiosInstance.post('/api/sucursal/crear', sucursal);
    return data;
  } catch (error) {
    throw error.response.data;
  }
};

export const listarSucursales = async () => {
  try {
    const { data } = await axiosInstance.get('/api/sucursal/listar');
    return data; // data should be an array of objects with { id, nombre }
  } catch (error) {
    throw error.response.data;
  }
};

export const obtenerSucursalPorId = async (id) => {
  if (!id) {
    throw new Error('ID de sucursal requerido');
  }
  try {
    const { data } = await axiosInstance.get('/api/sucursal/obtenerPorId', {
      params: { id: Number(id) }
    });
    return data;
  } catch (error) {
    console.error('Error obteniendo sucursal:', error);
    throw new Error(error.response?.data || 'Error al obtener la sucursal');
  }
};

export const actualizarSucursal = async (id, sucursal) => {
  try {
    const { data } = await axiosInstance.put('/api/sucursal/actualizar', sucursal, {
      params: { id }
    });
    return data;
  } catch (error) {
    throw error.response.data;
  }
};

export const eliminarSucursal = async (id) => {
  try {
    const { data } = await axiosInstance.delete('/api/sucursal/eliminar', {
      params: { id }
    });
    return data;
  } catch (error) {
    throw error.response.data;
  }
};

export const agregarEmpleado = async (idEmpleado, idSucursal) => {
  try {
    const { data } = await axiosInstance.put('/api/sucursal/agregarEmpleado', null, {
      params: {
        IdEmpleado: idEmpleado,
        IdSucursal: idSucursal
      }
    });
    return data;
  } catch (error) {
    throw error.response.data;
  }
};

export const agregarVehiculo = async (idVehiculo, idSucursal) => {
  try {
    const { data } = await axiosInstance.put('/api/sucursal/agregarVehiculo', null, {
      params: {
        IdVehiculo: idVehiculo,
        IdSucursal: idSucursal
      }
    });
    return data;
  } catch (error) {
    throw error.response.data;
  }
};
