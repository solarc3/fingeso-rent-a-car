import axiosInstance from './axiosConfig';

export const useUsuarioService = () => {
  const crearUsuario = async (usuario) => {
    // POST /api/usuario/crear
    // @RequestBody UsuarioEntity usuario
    try {
      const {data} = await axiosInstance.post('/api/usuario/crear', {
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
      const {data} = await axiosInstance.get('/api/usuario/trabajadores');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerAdministradores = async () => {
    // GET /api/usuario/administradores
    try {
      const {data} = await axiosInstance.get('/api/usuario/administradores');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerArrendatarios = async () => {
    // GET /api/usuario/arrendatarios
    try {
      const {data} = await axiosInstance.get('/api/usuario/arrendatarios');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const eliminarUsuario = async (id) => {
    // DELETE /api/usuario/eliminar
    // @RequestParam Long id
    try {
      const {data} = await axiosInstance.delete('/api/usuario/eliminar', {
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
      const {data} = await axiosInstance.put('/api/usuario/actualizar', usuario);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };
  const login = async (rut, password) => {
    // POST /api/usuario/login
    // @RequestBody Map<String, String> credentials
    try {
      const {data} = await axiosInstance.post('/api/usuario/login', {
        rut,
        password
      });
      return data;
    } catch (error) {
      if (error.response) {
        // Si el servidor respondió con un estado de error
        if (error.response.status === 403) {
          throw new Error('Usuario en lista negra');
        } else if (error.response.status === 401) {
          throw new Error('Credenciales inválidas');
        }
      }
      throw error.response?.data || error;
    }
  };

  return {
    login,
    crearUsuario,
    obtenerTrabajadores,
    obtenerAdministradores,
    obtenerArrendatarios,
    eliminarUsuario,
    actualizarUsuario
  };
};
