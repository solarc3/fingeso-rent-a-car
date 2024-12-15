import axiosInstance from './axiosConfig';

export const useUsuarioService = () => {
  const crearUsuario = async (usuario) => {
    try {
      const {data} = await axiosInstance.post('/api/usuario/crear', {
        rut: usuario.rut,
        nombre: usuario.nombre,
        apellido: usuario.apellido,
        password: usuario.password,
        rol: 'ARRENDATARIO', // default
        sucursalId: usuario.sucursalId // opcional
      });
      return data;
    } catch (error) {
      if (error.response) {
        switch (error.response.status) {
          case 400:
            throw new Error(error.response.data || 'Datos invalidos');
          default:
            throw new Error('Error en el servidor');
        }
      }
      throw error;
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
  const login = async (rut, password, rol) => {
    try {
      const {data} = await axiosInstance.post('/api/usuario/login', {
        rut,
        password,
        rol
      });
      return data;
    } catch (error) {
      if (error.response) {
        switch (error.response.status) {
          case 403:
            throw new Error('Usuario en lista negra');
          case 401:
            throw new Error('Credenciales inválidas o rol no autorizado');
          default:
            throw new Error('Error en el servidor');
        }
      }
      throw error;
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
