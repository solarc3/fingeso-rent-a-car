import axios from 'axios';

export const useUsuarioService = () => {
  const crearUsuario = async (usuario) => {
    // POST /api/usuario/crear
    // @RequestBody UsuarioEntity usuario
    try {
      const {data} = await axios.post('/api/usuario/crear', {
        rut: usuario.rut,
        nombre: usuario.nombre,
        apellido: usuario.apellido,
        rol: usuario.rol,
        sucursal: usuario.sucursal
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerTrabajadores = async () => {
    // GET /api/usuario/trabajadores
    try {
      const {data} = await axios.get('/api/usuario/trabajadores');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerAdministradores = async () => {
    // GET /api/usuario/administradores
    try {
      const {data} = await axios.get('/api/usuario/administradores');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerArrendatarios = async () => {
    // GET /api/usuario/arrendatarios
    try {
      const {data} = await axios.get('/api/usuario/arrendatarios');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const eliminarUsuario = async (id) => {
    // DELETE /api/usuario/eliminar
    // @RequestParam Long id
    try {
      const {data} = await axios.delete('/api/usuario/eliminar', {
        params: {id}
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const actualizarUsuario = async (id, usuario) => {
    // PUT /api/usuario/actualizar
    // @RequestParam Long id
    // @RequestBody UsuarioEntity usuario
    try {
      const {data} = await axios.put('/api/usuario/actualizar', usuario, {
        params: {id}
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  return {
    crearUsuario,
    obtenerTrabajadores,
    obtenerAdministradores,
    obtenerArrendatarios,
    eliminarUsuario,
    actualizarUsuario
  };
};
