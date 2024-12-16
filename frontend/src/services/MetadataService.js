import axiosInstance from './axiosConfig';

export const useMetadataService = () => {
  const getVehicleBrands = async () => {
    try {
      const {data} = await axiosInstance.get('/api/metadata/vehicle-brands');
      console.log('Raw brands data:', data);
      return data.map(marca => ({
        nombre: marca,
        valor: marca
      }));
    } catch (error) {
      console.error('Error fetching brands:', error);
      throw error;
    }
  };

  const getVehicleTypes = async () => {
    const {data} = await axiosInstance.get('/api/metadata/vehicle-types');
    return Object.entries(data).map(([valor, texto]) => ({
      texto,
      valor
    }));
  };

  const getTransmissionTypes = async () => {
    const {data} = await axiosInstance.get('/api/metadata/transmission-types');
    return Object.entries(data).map(([valor, texto]) => ({
      texto,
      valor
    }));
  };

  return {
    getVehicleBrands,
    getVehicleTypes,
    getTransmissionTypes
  };
};
