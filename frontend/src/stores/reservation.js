import {defineStore} from 'pinia';
import {useReservaService} from '@/services/ReservaService';

export const useReservationStore = defineStore('reservation', {
  state: () => ({
    reservations: [],
    loading: false,
    error: null
  }),

  actions: {
    async fetchUserReservations(userId) {
      this.loading = true;
      try {
        const reservaService = useReservaService();
        const reservations = await reservaService.obtenerReservasPorUsuario(userId);
        this.reservations = reservations;
        return reservations;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async createReservation(reservationData) {
      this.loading = true;
      try {
        console.log('Creando reserva con datos:', reservationData);
        const reservaService = useReservaService();
        const newReservation = await reservaService.crearReserva({
          fechaInicio: reservationData.fechaInicio,
          fechaFin: reservationData.fechaFin,
          costo: reservationData.costo,
          usuarioId: reservationData.usuarioId,
          vehiculoId: reservationData.vehiculoId,
          sucursalId: reservationData.sucursalId,
          sucursalDevolucionId: reservationData.sucursalDevolucionId
        });

        // Agregar la nueva reserva al estado
        if (this.reservations) {
          this.reservations.push(newReservation);
        } else {
          this.reservations = [newReservation];
        }

        return newReservation;
      } catch (error) {
        console.error('Error en createReservation:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async updateReservationStatus(reservationId, newStatus) {
      this.loading = true;
      try {
        const reservaService = useReservaService();
        const updatedReservation = await reservaService.actualizarEstado(reservationId, newStatus);

        // Actualizar la reserva en el estado local
        const index = this.reservations.findIndex(r => r.id === reservationId);
        if (index !== -1) {
          this.reservations[index] = updatedReservation;
        }

        return updatedReservation;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});
