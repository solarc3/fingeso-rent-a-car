export default class Cliente {
  constructor(id_cliente = null, nombre = '', direccion = '', email = '', telefono = '') {
    this.id_cliente = id_cliente;
    this.nombre = nombre;
    this.direccion = direccion;
    this.email = email;
    this.telefono = telefono;
  }
}
