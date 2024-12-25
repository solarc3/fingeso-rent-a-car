import axiosInstance from './axiosConfig';

export const useUsuarioService = () => {
  const crearUsuario = async (usuario) => {
    try {
      const { data } = await axiosInstance.post('/api/usuario/crear', {
        rut: usuario.rut,
        nombre: usuario.nombre,
        apellido: usuario.apellido,
        password: usuario.password,
        rol: usuario.rol,
        sucursalId: usuario.sucursalId, // opcional
      });
      return data;
    } catch (error) {
      if (error.response) {
        switch (error.response.status) {
          case 400:
            throw new Error(error.response.data || 'Datos inválidos');
          default:
            throw new Error('Error en el servidor');
        }
      }
      throw error;
    }
  };

  const obtenerTrabajadores = async () => {
    try {
      const { data } = await axiosInstance.get('/api/usuario/trabajadores');
      return data;
    } catch (error) {
      throw new Error(error.response?.data || 'Error al obtener trabajadores');
    }
  };

  const obtenerAdministradores = async () => {
    try {
      const { data } = await axiosInstance.get('/api/usuario/administradores');
      return data;
    } catch (error) {
      throw new Error(error.response?.data || 'Error al obtener administradores');
    }
  };

  const obtenerArrendatarios = async () => {
    try {
      const { data } = await axiosInstance.get('/api/usuario/arrendatarios');
      return data;
    } catch (error) {
      throw new Error(error.response?.data || 'Error al obtener arrendatarios');
    }
  };

  const eliminarUsuario = async (id) => {
    try {
      const { data } = await axiosInstance.delete('/api/usuario/eliminar', {
        params: { id },
      });
      return data;
    } catch (error) {
      throw new Error(error.response?.data || 'Error al eliminar usuario');
    }
  };

  const actualizarUsuario = async (id, usuario) => {
    try {
      const { data } = await axiosInstance.put('/api/usuario/actualizar', {
        id,
        rol: usuario.rol,
        esta_en_lista_negra: usuario.esta_en_lista_negra,
        estado: usuario.estado,
      });
      return data;
    } catch (error) {
      throw new Error(error.response?.data || 'Error al actualizar usuario');
    }
  };

  const login = async (rut, password, rol) => {
    try {
      const { data } = await axiosInstance.post('/api/usuario/login', {
        rut,
        password,
        rol,
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
    actualizarUsuario,
  };
};