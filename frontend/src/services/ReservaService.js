import axios from 'axios';

export const useReservaService = () => {
  const crearReserva = async (reserva) => {
    // POST /api/reserva/crear
    // @RequestBody ReservaEntity reserva
    try {
      const {data} = await axios.post('/api/reserva/crear', {
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
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerReservas = async () => {
    // GET /api/reserva/obtener
    try {
      const {data} = await axios.get('/api/reserva/obtener');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerReservasPorUsuario = async (id) => {
    // GET /api/reserva/obtener/porUsuario
    // @RequestParam Long id
    try {
      const {data} = await axios.get('/api/reserva/obtener/porUsuario', {
        params: {id}
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const actualizarEstado = async (id, estado) => {
    // PUT /api/reserva/{id}/estado
    // @PathVariable Long id
    // @RequestBody EstadoReserva estado
    try {
      const {data} = await axios.put(`/api/reserva/${id}/estado`, estado);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const extenderReserva = async (reservaId, fechaFin) => {
    // PUT /api/reserva/extender
    // @RequestBody Map<String, Object> nuevaFechaJsonMap
    try {
      const {data} = await axios.put('/api/reserva/extender', {
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
