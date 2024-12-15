import axios from 'axios';

export const useUsuarioService = () => {
  const crearUsuario = async (usuario) => {
    try {
      const {data} = await axios.post('/api/usuario/crear', usuario);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerTrabajadores = async () => {
    try {
      const {data} = await axios.get('/api/usuario/trabajadores');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerAdministradores = async () => {
    try {
      const {data} = await axios.get('/api/usuario/administradores');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerArrendatarios = async () => {
    try {
      const {data} = await axios.get('/api/usuario/arrendatarios');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const eliminarUsuario = async (id) => {
    try {
      const {data} = await axios.delete(`/api/usuario/eliminar?id=${id}`);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const actualizarUsuario = async (id, usuario) => {
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
