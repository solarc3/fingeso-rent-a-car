export default class Reserva {
  constructor(
    id = null,
    fechaInicio = '',
    fechaFin = '',
    costo = 0,
    estado = 'PENDIENTE',
    usuario = null,
    vehiculo = null,
    sucursalRetiro = null,
    sucursalDevolucion = null,
  ) {
    this.id = id;
    this.fechaInicio = fechaInicio;
    this.fechaFin = fechaFin;
    this.costo = costo;
    this.estado = estado; // PENDIENTE, CONFIRMADA, EN_PROGRESO, COMPLETADA, CANCELADA
    this.usuario = usuario;
    this.vehiculo = vehiculo;
    this.sucursalRetiro = sucursalRetiro;
    this.sucursalDevolucion = sucursalDevolucion
  }
}
