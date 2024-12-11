package com.rentacar.backend.services;

import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.repositories.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class VehiculoService {
    @Autowired
    private VehiculoRepository vehiculoRepository;

    /**
     * Permite crear un nuevo vehículo y almacenarlo en la base de datos.
     * @param marca marca del fabricante
     * @param modelo modelo específico del vehículo
     * @param acriss código ACRISS asociado (Categoría, Carrocería, Transmisión, Combustible/Aire)
     * @param patente patente del vehículo
     * @param precio precio de arriendo
     * @return Entidad Vehículo creada
     */
    VehiculoEntity crearVehiculo(String marca, String modelo, String acriss,
                                 String patente, BigDecimal precio) {
        // Sanitización de datos

        // Patentes en Chile -> 2 letras, 2 letras o 2 números, 2 números
        final String CL_PLATE_REGEX = "^[A-Z]{2}([A-Z]{2}|[0-9]{2})[0-9]{2}$";
        Pattern patternClPlate = Pattern.compile(CL_PLATE_REGEX);

        // Verificar formato ACRISS, formato patente, y valor positivo del precio
        if (!validarAcriss(acriss) || !patternClPlate.matcher(patente.toUpperCase()).matches()
                || precio.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Parámetros de vehículo inválidos");

        // Almacenar si no hay problemas con lo de arriba
        VehiculoEntity vehiculo = new VehiculoEntity();
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setAcriss(acriss);
        vehiculo.setPatente(patente);
        vehiculo.setPrecioArriendo(precio);
        return vehiculoRepository.save(vehiculo);
    }

    /**
     * Permite verificar si un string es un código ACRISS válido.
     * @param acriss String candidato a verificar
     * @return true si se cumple con el formato ACRISS, false en otro caso
     */
    boolean validarAcriss(String acriss) {
        // Corroborar con tabla ACRISS
        final String ACRISS_REGEX =
                "^[MNEHCDIJRSFGPULWOX]" +       // Categoría / tamaño
                "[BCDWVLSTFJXPQZEMRHYNKG]" +    // Tipo / carrocería
                "[MNCABD]" +                    // Transmisión / manual, automática
                "[RNDQHIECMLSABFVZUX]$";        // Combustible / Aire Acondicionado
        Pattern patternAcriss = Pattern.compile(ACRISS_REGEX);
        return patternAcriss.matcher(acriss.toUpperCase()).matches();
    }


    /**
     * Permite obtener un vehículo con una patente determinada.
     * @param patente Patente del vehículo a buscar
     * @return Vehículo con la patente buscada, si existe.
     */
    Optional<VehiculoEntity> obtenerVehiculoPorPatente(String patente) {
        return vehiculoRepository.findByPatente(patente);
    }

    /**
     * Permite obtener todos los vehículos de una marca determinada.
     * @param marca Marca de los vehículos
     * @return Lista de vehículos de esa marca
     */
    List<VehiculoEntity> obtenerVehiculosPorMarca(String marca) {
        return vehiculoRepository.findByMarca(marca);
    }

    /**
     * Permite obtener todos los vehículos en base a uno o más caracteres del código ACRISS
     * Deben haber '_' para representar que no importa ese caracter en particular
     * (Por ejemplo, acriss = "__ME" buscaría todos los vehículos eléctricos con transmisión manual)
     * @param acriss Código ACRISS
     * @return Lista de vehículos con código ACRISS acorde a lo pedido
     */
    List<VehiculoEntity> obtenerVehiculosPorAcriss(String acriss) {
        if (!validarAcriss(acriss))
            throw new IllegalArgumentException("Código ACRISS no válido");
        return vehiculoRepository.findByAcrissLike(acriss.toUpperCase());
    }

    /**
     * Permite obtener todos los vehículos asociados a cierta sucursal.
     * @param sucursal Sucursal a buscar
     * @return Lista de vehículos en esa sucursal
     */
    List<VehiculoEntity> obtenerVehiculosPorSucursal(SucursalEntity sucursal) {
        return vehiculoRepository.findBySucursal(sucursal);
    }

    /**
     * Permite obtener todos los vehículso cuyo precio esté dentro de un rango dado
     * @param min Precio mínimo de arriendo
     * @param max Precio máximo de arriendo
     * @return Lista de vehículos con precio de arriendo entre min y max
     */
    List<VehiculoEntity> obtenerVehiculosConPrecioEntre(BigDecimal min, BigDecimal max) {
        if (min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Precio inválido");

        return vehiculoRepository.findByPrecioArriendoBetween(min, max);
    }

    /**
     * Permite obtener todos los vehículos que se encuentren en cierto estado
     * @param estado Estado de los vehículos
     * @return Lista de vehículos en ese estado
     */
    List<VehiculoEntity> obtenerVehiculosPorEstado(String estado) {
        return vehiculoRepository.findByEstado(estado);
    }

    /**
     * Permite actualizar el estado de un vehículo dado su ID
     * @param vehiculoId ID del vehículo a actualizar
     * @param estado Estado nuevo del vehículo
     * @return Vehículo actualizado con ese nuevo estado
     */
    VehiculoEntity actualizarEstadoVehiculoPorId(Long vehiculoId, String estado) {
        VehiculoEntity vehiculo = vehiculoRepository.findById(vehiculoId).orElseThrow();
        vehiculo.setEstado(estado);
        return vehiculoRepository.save(vehiculo);
    }

    /**
     * Permite eliminar un vehículo de la base de datos dado su ID
     * @param vehiculoId ID del vehículo a eliminar
     */
    void eliminarVehiculoPorId(Long vehiculoId) {
        vehiculoRepository.findById(vehiculoId).orElseThrow();
        vehiculoRepository.deleteById(vehiculoId);
    }
}
