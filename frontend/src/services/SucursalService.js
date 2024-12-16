import axiosInstance from "@/services/axiosConfig.js";

export const useSucursalService = () => {
  const crearSucursal = async (sucursal) => {
    // POST /api/sucursal/crear
    // @RequestBody SucursalEntity sucursal
    try {
      const {data} = await axiosInstance.post('/api/sucursal/crear', {
        nombre: sucursal.nombre,
        direccion: sucursal.direccion,
        telefono: sucursal.telefono,
        email: sucursal.email
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const listarSucursales = async () => {
    try {
      const {data} = await axiosInstance.get('/api/sucursal/listar');
      return data; // data debe ser un array de objetos con { id, nombre }
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerSucursalPorId = async (id) => {
    // GET /api/sucursal/obtenerPorId
    // @RequestParam Long id
    try {
      const {data} = await axiosInstance.get('/api/sucursal/obtenerPorId', {
        params: {id}
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const actualizarSucursal = async (id, sucursal) => {
    // PUT /api/sucursal/actualizar
    // @RequestParam Long id
    // @RequestBody SucursalEntity sucursal
    try {
      const {data} = await axiosInstance.put('/api/sucursal/actualizar', sucursal, {
        params: {id}
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const eliminarSucursal = async (id) => {
    // DELETE /api/sucursal/eliminar
    // @RequestParam Long id
    try {
      const {data} = await axiosInstance.delete('/api/sucursal/eliminar', {
        params: {id}
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const agregarEmpleado = async (idEmpleado, idSucursal) => {
    // PUT /api/sucursal/agregarEmpleado
    // @RequestParam Long IdEmpleado
    // @RequestParam Long IdSucursal
    try {
      const {data} = await axiosInstance.put('/api/sucursal/agregarEmpleado', null, {
        params: {
          IdEmpleado: idEmpleado,
          IdSucursal: idSucursal
        }
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const agregarVehiculo = async (idVehiculo, idSucursal) => {
    // PUT /api/sucursal/agregarVehiculo
    // @RequestParam Long IdVehiculo
    // @RequestParam Long IdSucursal
    try {
      const {data} = await axiosInstance.put('/api/sucursal/agregarVehiculo', null, {
        params: {
          IdVehiculo: idVehiculo,
          IdSucursal: idSucursal
        }
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  return {
    crearSucursal,
    listarSucursales,
    obtenerSucursalPorId,
    actualizarSucursal,
    eliminarSucursal,
    agregarEmpleado,
    agregarVehiculo
  };
};
