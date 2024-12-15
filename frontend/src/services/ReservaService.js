import axios from 'axios';

export const useReservaService = () => {
  const crearReserva = async (reserva) => {
    try {
      const {data} = await axios.post('/api/reserva/crear', reserva);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerReservas = async () => {
    try {
      const {data} = await axios.get('/api/reserva/obtener');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerReservasPorUsuario = async (id) => {
    try {
      const {data} = await axios.get(`/api/reserva/obtener/porUsuario?id=${id}`);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const actualizarEstado = async (id, estado) => {
    try {
      const {data} = await axios.put(`/api/reserva/${id}/estado`, estado);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const extenderReserva = async (reservaId, fechaFin) => {
    try {
      const {data} = await axios.put('/api/reserva/extender', {reservaId, fechaFin});
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
