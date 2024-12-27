import axiosInstance from "@/services/axiosConfig.js";

export const useVehiculoService = () => {
  const obtenerVehiculos = async () => {
    try {
      const {data} = await axiosInstance.get('/api/vehiculo/listing');
      console.log('Datos crudos del backend:', data);

      return data;
    } catch (error) {
      console.error('Error obteniendo vehículos:', error);
      throw error;
    }
  };

  const crearVehiculo = async (vehiculo) => {
    try {
      const acriss = generateAcrissCode(vehiculo.tipoVehiculo, vehiculo.transmision);

      const vehiculoData = {
        marca: vehiculo.marca,
        modelo: vehiculo.modelo,
        patente: vehiculo.patente,
        anio: vehiculo.anio,
        tipoVehiculo: vehiculo.tipoVehiculo,
        transmision: vehiculo.transmision,
        precioArriendo: vehiculo.precioArriendo,
        estado: vehiculo.estado,
        acriss: acriss,
        sucursalId: vehiculo.sucursal
      };

      console.log('Enviando datos del vehículo:', vehiculoData);
      const {data} = await axiosInstance.post('/api/vehiculo/crear', vehiculoData);
      return data;
    } catch (error) {
      console.error('Error en crearVehiculo:', error);
      throw error.response?.data || error;
    }
  };
  const generateAcrissCode = (tipoVehiculo, transmision) => {
    // Primera letra: Categoría del vehículo (E = Economy)
    // Segunda letra: Tipo de carrocería (C = 2/4 Door)
    // Tercera letra: Transmisión (M = Manual, A = Automatic)
    // Cuarta letra: Combustible/Aire acondicionado (R = Aire acondicionado)

    let code = 'EC';

    // Agregar tipo de transmisión
    code += transmision || 'M';

    // Agregar R por defecto (con aire acondicionado)
    code += 'R';

    return code;
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
    try {
      await axiosInstance.delete('/api/vehiculo/eliminar', {
        params: {id}
      });
    } catch (error) {
      if (error.response?.data) {
        throw new Error(error.response.data);
      }
      throw error;
    }
  };

  const actualizarVehiculo = async (vehiculo) => {
    try {
      const {data} = await axiosInstance.put('/api/vehiculo/actualizar', vehiculo);
      return data;
    } catch (error) {
      throw error.response?.data || error;
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
