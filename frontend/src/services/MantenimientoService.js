import axiosInstance from './axiosConfig';

export const useMantenimientoService = () => {
  const obtenerHistorial = async (vehiculoId) => {
    try {
      const {data} = await axiosInstance.get(`/api/mantenimiento/historial/${vehiculoId}`);
      console.log('Respuesta del backend:', data);
      // Si data es un objeto con estructura { cantidad, eventos }
      return data.eventos || data; // Retorna los eventos si viene en esa estructura, o data directamente
    } catch (error) {
      console.error('Error obteniendo historial:', error);
      throw error;
    }
  };

  const obtenerMantenimientos = async (vehiculoId) => {
    try {
      const {data} = await axiosInstance.get(`/api/mantenimiento/mantenimientos/${vehiculoId}`);
      return data;
    } catch (error) {
      console.error('Error obteniendo mantenimientos:', error);
      throw error;
    }
  };

  const programarMantenimiento = async (mantenimientoData) => {
    try {
      const {data} = await axiosInstance.post('/api/mantenimiento/programar', mantenimientoData);
      return data;
    } catch (error) {
      if (error.response) {
        throw new Error(error.response.data || 'Error al programar mantenimiento');
      }
      throw error;
    }
  };

  const actualizarEstado = async (mantenimientoId, nuevoEstado) => {
    try {
      // Enviar el estado como objeto JSON
      const {data} = await axiosInstance.put(`/api/mantenimiento/${mantenimientoId}/estado`, {
        estado: nuevoEstado
      });
      return data;
    } catch (error) {
      console.error('Error actualizando estado:', error);
      throw error;
    }
  };

  return {
    obtenerHistorial,
    obtenerMantenimientos,
    programarMantenimiento,
    actualizarEstado
  };
};
