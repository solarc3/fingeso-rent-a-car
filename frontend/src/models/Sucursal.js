export default class Sucursal {
  constructor(
    id = null,
    nombre = '',
    direccion = '',
    telefono = '',
    email = '',
    empleados = [],
    vehiculos = [],
    reservas = []
  ) {
    this.id = id;
    this.nombre = nombre;
    this.direccion = direccion;
    this.telefono = telefono;
    this.email = email;
    this.empleados = empleados;
    this.vehiculos = vehiculos;
    this.reservas = reservas;
  }
}
