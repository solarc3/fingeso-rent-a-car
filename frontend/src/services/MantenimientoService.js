import axiosInstance from './axiosConfig';

export const useMantenimientoService = () => {
  const obtenerHistorial = async (vehiculoId) => {
    try {
      const {data} = await axiosInstance.get(`/api/mantenimiento/historial/${vehiculoId}`);
      return data;
    } catch (error) {
      console.error('Error obteniendo historial:', error);
      throw error;
    }
  };
  const reportarFalla = async (fallaData) => {
    try {
      console.log('Enviando datos de falla:', fallaData);
      const response = await axiosInstance.post('/api/vehiculo/falla', {
        vehiculoId: fallaData.vehiculoId,
        tipo: fallaData.tipo,
        severidad: fallaData.severidad,
        descripcion: fallaData.descripcion,
        reportadoPorId: fallaData.reportadoPorId
      });

      console.log('Respuesta del servidor:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error en reportarFalla:', error);
      throw new Error(error.response?.data || 'Error al reportar la falla');
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
  const resolverFalla = async (fallaId, solucion, tecnicoId) => {
    try {
      const {data} = await axiosInstance.post(`/api/mantenimiento/${fallaId}/resolver`, {
        solucion,
        tecnicoId
      });
      return data;
    } catch (error) {
      console.error('Error resolviendo falla:', error);
      throw new Error(error.response?.data || 'Error al resolver falla');
    }
  };
  const obtenerHistorialFallas = async (vehiculoId) => {
    try {
      const {data} = await axiosInstance.get(`/api/mantenimiento/fallas/${vehiculoId}`);
      return data;
    } catch (error) {
      console.error('Error obteniendo historial de fallas:', error);
      throw error;
    }
  };
  return {
    obtenerHistorial,
    reportarFalla,
    resolverFalla,
    obtenerHistorialFallas,
    obtenerMantenimientos,
    programarMantenimiento,
    actualizarEstado
  };
};
