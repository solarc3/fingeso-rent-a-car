import {ref} from 'vue';
import axiosInstance from "@/services/axiosConfig.js";

export function useSucursalService() {
  const loading = ref(false);
  const error = ref(null);

  const crearSucursal = async (sucursal) => {
    loading.value = true;
    error.value = null;
    try {
      const {data} = await axiosInstance.post('/api/sucursal/crear', sucursal);
      return data;
    } catch (err) {
      error.value = err.response?.data || 'Error al crear la sucursal';
      throw error.value;
    } finally {
      loading.value = false;
    }
  };

  const listarSucursales = async () => {
    loading.value = true;
    error.value = null;
    try {
      const {data} = await axiosInstance.get('/api/sucursal/listar');
      return data;
    } catch (err) {
      error.value = err.response?.data || 'Error al listar las sucursales';
      throw error.value;
    } finally {
      loading.value = false;
    }
  };

  const obtenerSucursalPorId = async (id) => {
    if (!id) {
      throw new Error('ID de sucursal requerido');
    }
    loading.value = true;
    error.value = null;
    try {
      const {data} = await axiosInstance.get('/api/sucursal/obtenerPorId', {
        params: {id: Number(id)}
      });
      return data;
    } catch (err) {
      error.value = err.response?.data || 'Error al obtener la sucursal';
      throw error.value;
    } finally {
      loading.value = false;
    }
  };

  const actualizarSucursal = async (id, sucursal) => {
    loading.value = true;
    error.value = null;
    try {
      const {data} = await axiosInstance.put('/api/sucursal/actualizar', sucursal, {
        params: {id}
      });
      return data;
    } catch (err) {
      error.value = err.response?.data || 'Error al actualizar la sucursal';
      throw error.value;
    } finally {
      loading.value = false;
    }
  };

  const eliminarSucursal = async (id) => {
    loading.value = true;
    error.value = null;
    try {
      const {data} = await axiosInstance.delete('/api/sucursal/eliminar', {
        params: {id}
      });
      return data;
    } catch (err) {
      error.value = err.response?.data || 'Error al eliminar la sucursal';
      throw error.value;
    } finally {
      loading.value = false;
    }
  };

  const agregarEmpleado = async (idEmpleado, idSucursal) => {
    loading.value = true;
    error.value = null;
    try {
      const {data} = await axiosInstance.put('/api/sucursal/agregarEmpleado', null, {
        params: {
          IdEmpleado: idEmpleado,
          IdSucursal: idSucursal
        }
      });
      return data;
    } catch (err) {
      error.value = err.response?.data || 'Error al agregar el empleado';
      throw error.value;
    } finally {
      loading.value = false;
    }
  };

  const agregarVehiculo = async (idVehiculo, idSucursal) => {
    loading.value = true;
    error.value = null;
    try {
      const {data} = await axiosInstance.put('/api/sucursal/agregarVehiculo', null, {
        params: {
          IdVehiculo: idVehiculo,
          IdSucursal: idSucursal
        }
      });
      return data;
    } catch (err) {
      error.value = err.response?.data || 'Error al agregar el vehículo';
      throw error.value;
    } finally {
      loading.value = false;
    }
  };

  return {
    loading,
    error,
    crearSucursal,
    listarSucursales,
    obtenerSucursalPorId,
    actualizarSucursal,
    eliminarSucursal,
    agregarEmpleado,
    agregarVehiculo
  };
}
