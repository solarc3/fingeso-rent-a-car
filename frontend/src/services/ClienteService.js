import axios from './axiosConfig';
import Cliente from '../models/Cliente';

export default class ClienteService {
  static async createCliente(cliente) {
    try {
      const {data} = await axios.post('/api/clientes/', cliente);
      return new Cliente(
        data.id_cliente,
        data.nombre,
        data.direccion,
        data.email,
        data.telefono
      );
    } catch (error) {
      console.error('Error al crear cliente:', error);
      throw error;
    }
  }

  static async getClienteById(id) {
    try {
      const {data} = await axios.get(`/api/clientes/${id}`);
      return new Cliente(
        data.id_cliente,
        data.nombre,
        data.direccion,
        data.email,
        data.telefono
      );
    } catch (error) {
      console.error('Error al obtener cliente:', error);
      throw error;
    }
  }

  static async getAllClientes() {
    try {
      const {data} = await axios.get('/api/clientes/');
      return data.map(cliente => new Cliente(
        cliente.id_cliente,
        cliente.nombre,
        cliente.direccion,
        cliente.email,
        cliente.telefono
      ));
    } catch (error) {
      console.error('Error al obtener clientes:', error);
      throw error;
    }
  }

  static async updateCliente(cliente) {
    try {
      const {data} = await axios.put('/api/clientes/', cliente);
      return new Cliente(
        data.id_cliente,
        data.nombre,
        data.direccion,
        data.email,
        data.telefono
      );
    } catch (error) {
      console.error('Error al actualizar cliente:', error);
      throw error;
    }
  }

  static async deleteCliente(id) {
    try {
      await axios.delete(`/api/clientes/${id}`);
    } catch (error) {
      console.error('Error al eliminar cliente:', error);
      throw error;
    }
  }
}
