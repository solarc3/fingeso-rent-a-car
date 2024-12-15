export default class Vehiculo {
  constructor(
    id = null,
    marca = '',
    modelo = '',
    acriss = '',
    patente = '',
    anio = 0,
    precioArriendo = 0,
    sucursal = null,
    disponible = true,
    estado = 'DISPONIBLE',
    valoraciones = []
  ) {
    this.id = id;
    this.marca = marca;
    this.modelo = modelo;
    this.acriss = acriss;
    this.patente = patente;
    this.anio = anio;
    this.precioArriendo = precioArriendo;
    this.sucursal = sucursal;
    this.disponible = disponible;
    this.estado = estado; // DISPONIBLE, NO_DISPONIBLE, EN_MANTENCION, EN_REPARACION
    this.valoraciones = valoraciones;
  }
}
