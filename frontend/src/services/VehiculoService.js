import axiosInstance from "@/services/axiosConfig.js";

export const useVehiculoService = () => {
  const obtenerVehiculos = async () => {
    // GET /api/vehiculo/listing
    try {
      const {data} = await axiosInstance.get('/api/vehiculo/listing');
      return data;
    } catch (error) {
      throw error.response.data;
    }
  };

  const crearVehiculo = async (vehiculo) => {
    try {
      const vehiculoData = {
        marca: vehiculo.marca,
        modelo: vehiculo.modelo,
        acriss: 'ECMR',
        patente: vehiculo.patente,
        precioArriendo: vehiculo.precioArriendo,
        anio: vehiculo.anio || new Date().getFullYear(),
        estado: vehiculo.estado || 'DISPONIBLE',
        // Modificar esta parte
        sucursal: {
          id: Number(vehiculo.sucursal) // Asegurarse que sea un número
        }
      };

      console.log('Enviando datos del vehículo:', vehiculoData);
      const {data} = await axiosInstance.post('/api/vehiculo/crear', vehiculoData);
      return data;
    } catch (error) {
      console.error('Error en crearVehiculo:', error);
      throw error.response?.data || error;
    }
  };
  const obtenerVehiculosDisponiblesEnSucursal = async (id) => {
    // GET /api/vehiculo/listing/enSucursal
    // @RequestParam Long id
    try {
      const {data} = await axiosInstance.get('/api/vehiculo/listing/enSucursal', {
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
      const {data} = await axiosInstance.delete('/api/vehiculo/eliminar', {
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
      const {data} = await axiosInstance.put('/api/vehiculo/actualizar', vehiculo);
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
