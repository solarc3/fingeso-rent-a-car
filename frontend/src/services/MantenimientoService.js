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
      const response = await axiosInstance.post(`/api/mantenimiento/fallas/reportar`, {
        vehiculoId: fallaData.vehiculoId,
        tipo: fallaData.tipo,
        severidad: fallaData.severidad,
        descripcion: fallaData.descripcion,
        reportadoPorId: fallaData.reportadoPorId
      });

      return response.data;
    } catch (error) {
      console.error('Error reportando falla:', error);
      throw new Error(error.response?.data || 'Error al reportar falla');
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
