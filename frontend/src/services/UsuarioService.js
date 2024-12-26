import axiosInstance from './axiosConfig';

export const useUsuarioService = () => {
  const crearUsuario = async (usuario) => {
    try {
      const {data} = await axiosInstance.post('/api/usuario/crear', {
        rut: usuario.rut,
        nombre: usuario.nombre,
        apellido: usuario.apellido,
        password: usuario.password,
        rol: usuario.rol,
        sucursalId: usuario.sucursalId
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
      const {data} = await axiosInstance.get('/api/usuario/trabajadores');
      return data;
    } catch (error) {
      throw new Error(error.response?.data || 'Error al obtener trabajadores');
    }
  };

  const obtenerAdministradores = async () => {
    try {
      const {data} = await axiosInstance.get('/api/usuario/administradores');
      return data;
    } catch (error) {
      throw new Error(error.response?.data || 'Error al obtener administradores');
    }
  };

  const obtenerArrendatarios = async () => {
    try {
      const {data} = await axiosInstance.get('/api/usuario/arrendatarios');
      return data;
    } catch (error) {
      throw new Error(error.response?.data || 'Error al obtener arrendatarios');
    }
  };

  const eliminarUsuario = async (id) => {
    try {
      const response = await axiosInstance.delete(`/api/usuario/eliminar`, {
        params: { id }
      });

      if (response.status === 200) {
        return response.data;
      }

      throw new Error(response.data?.error || 'Error al eliminar usuario');
    } catch (error) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Error al eliminar usuario');
    }
  };

  const actualizarUsuario = async (id, userData) => {
    try {
      const usuarioDTO = {
        nombre: userData.nombre,
        apellido: userData.apellido,
        rol: userData.rol,
        estaEnListaNegra: userData.estaEnListaNegra
      };

      const { data } = await axiosInstance.put(`/api/usuario/actualizar?id=${id}`, usuarioDTO);
      return data;
    } catch (error) {
      throw new Error(error.response?.data || 'Error al actualizar usuario');
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
