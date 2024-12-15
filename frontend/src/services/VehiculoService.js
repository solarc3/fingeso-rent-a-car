import axios from 'axios';

export const useVehiculoService = () => {
  const obtenerVehiculos = async () => {
    // GET /api/vehiculo/listing
    try {
      const {data} = await axios.get('/api/vehiculo/listing');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const crearVehiculo = async (vehiculo) => {
    // POST /api/vehiculo/crear
    // @RequestBody VehiculoEntity vehiculo
    try {
      const {data} = await axios.post('/api/vehiculo/crear', {
        marca: vehiculo.marca,
        modelo: vehiculo.modelo,
        acriss: vehiculo.acriss,
        patente: vehiculo.patente,
        precioArriendo: vehiculo.precioArriendo
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const obtenerVehiculosDisponiblesEnSucursal = async (id) => {
    // GET /api/vehiculo/listing/enSucursal
    // @RequestParam Long id
    try {
      const {data} = await axios.get('/api/vehiculo/listing/enSucursal', {
        params: {id}
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const eliminarVehiculo = async (id) => {
    // DELETE /api/vehiculo/eliminar
    // @RequestParam Long id
    try {
      const {data} = await axios.delete('/api/vehiculo/eliminar', {
        params: {id}
      });
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const actualizarVehiculo = async (vehiculo) => {
    // PUT /api/vehiculo/actualizar
    // @RequestBody VehiculoEntity vehiculo
    try {
      const {data} = await axios.put('/api/vehiculo/actualizar', vehiculo);
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  return {
    obtenerVehiculos,
    crearVehiculo,
    obtenerVehiculosDisponiblesEnSucursal,
    eliminarVehiculo,
    actualizarVehiculo
  };
};
