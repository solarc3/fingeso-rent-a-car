import axiosInstance from "@/services/axiosConfig.js";

export const useReservaService = () => {
  const crearReserva = async (reserva) => {
    try {
      const {data} = await axiosInstance.post('/api/reserva/crear', {
        fechaInicio: reserva.fechaInicio,
        fechaFin: reserva.fechaFin,
        costo: reserva.costo,
        usuario: {
          id: reserva.usuarioId
        },
        vehiculo: {
          id: reserva.vehiculoId
        },
        sucursal: {
          id: reserva.sucursalId
        }
      });

      // Asegurarse de que los datos devueltos tengan la estructura correcta
      return {
        ...data,
        fechaInicio: new Date(data.fechaInicio),
        fechaFin: new Date(data.fechaFin)
      };
    } catch (error) {
      throw new Error(error.response?.data || 'Error al crear la reserva');
    }
  }

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
    // PUT /api/reserva/extender
    // @RequestBody Map<String, Object> nuevaFechaJsonMap
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

  return {
    crearReserva,
    obtenerReservas,
    obtenerReservasPorUsuario,
    actualizarEstado,
    extenderReserva
  };
};
