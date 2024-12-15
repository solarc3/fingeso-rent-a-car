export default class Usuario {
  constructor(
    id = null,
    rut = '',
    nombre = '',
    apellido = '',
    estaEnListaNegra = false,
    rol = 'ARRENDATARIO',
    valoraciones = [],
    reservas = [],
    sucursal = null
  ) {
    this.id = id;
    this.rut = rut;
    this.nombre = nombre;
    this.apellido = apellido;
    this.estaEnListaNegra = estaEnListaNegra;
    this.rol = rol; // ARRENDATARIO, TRABAJADOR, ADMINISTRADOR
    this.valoraciones = valoraciones;
    this.reservas = reservas;
    this.sucursal = sucursal;
  }

  isAdministrador() {
    return this.rol === 'ADMINISTRADOR';
  }

  isTrabajador() {
    return this.rol === 'TRABAJADOR';
  }

  isArrendatario() {
    return this.rol === 'ARRENDATARIO';
  }
}
