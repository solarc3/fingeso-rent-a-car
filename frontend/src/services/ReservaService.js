import axiosInstance from "@/services/axiosConfig.js";

export const useReservaService = () => {
  const crearReserva = async (reserva) => {
    try {
      const requestData = {
        fechaInicio: reserva.fechaInicio,
        fechaFin: reserva.fechaFin,
        costo: reserva.costo,
        usuarioId: reserva.usuarioId,
        vehiculoId: reserva.vehiculoId,
        sucursalId: reserva.sucursalId,
        sucursalDevolucionId: reserva.sucursalDevolucionId
      };

      console.log('Enviando datos al servidor:', requestData);

      const {data} = await axiosInstance.post('/api/reserva/crear', requestData);
      return data;
    } catch (error) {
      console.error('Error en crearReserva:', error.response?.data || error);
      throw new Error(error.response?.data || 'Error al crear la reserva');
    }
  };

  const obtenerReservas = async () => {
    // GET /api/reserva/obtener
    try {
      const {data} = await axiosInstance.get('/api/reserva/obtener');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerReservasPorUsuario = async (userId) => {
    try {
      const {data} = await axiosInstance.get('/api/reserva/obtener/porUsuario', {
        params: {id: userId}
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const actualizarEstado = async (id, estado) => {
    try {
      const {data} = await axiosInstance.put(`/api/reserva/${id}/estado`, estado);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const extenderReserva = async (reservaId, fechaFin) => {
    try {
      const {data} = await axiosInstance.put('/api/reserva/extender', {
        reservaId,
        fechaFin
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const generarReporte = async (fechaInicio, fechaFin) => {
    try {
      const response = await axiosInstance.get('/api/reserva/reporte', {
        params: { fechaInicio, fechaFin },
        responseType: 'arraybuffer'
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || 'Error generando reporte';
    }
  };

  const obtenerReservasPorVehiculo = async (vehicleID)=>{
    try {
      const {data} = await axiosInstance.get('api/reserva/obtener/porVehiculo', {
        params: {id: vehicleID}
      });
      return data;
      }catch(error){
        throw error.response.data;
    }
  };

  return {
    crearReserva,
    obtenerReservas,
    obtenerReservasPorUsuario,
    actualizarEstado,
    extenderReserva,
    generarReporte,
    obtenerReservasPorVehiculo
  };
};
