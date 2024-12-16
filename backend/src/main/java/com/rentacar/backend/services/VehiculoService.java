package com.rentacar.backend.services;

import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.repositories.ReservaRepository;
import com.rentacar.backend.repositories.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class VehiculoService {
    private final VehiculoRepository vehiculoRepository;
    private final ReservaRepository reservaRepository;

    @Autowired
    public VehiculoService(VehiculoRepository vehiculoRepository, ReservaRepository reservaRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.reservaRepository = reservaRepository;
    }

    /**
     * Permite crear un nuevo vehículo y almacenarlo en la base de datos.
     *
     * @param marca   marca del fabricante
     * @param modelo  modelo específico del vehículo
     * @param acriss  código ACRISS asociado (Categoría, Carrocería, Transmisión, Combustible/Aire)
     * @param patente patente del vehículo
     * @param precio  precio de arriendo
     * @return Entidad Vehículo creada
     */
    public VehiculoEntity crearVehiculo(String marca, String modelo, String acriss,
                                        String patente, BigDecimal precio) {
        // Sanitización de datos

        // Patentes en Chile -> 2 letras, 2 letras o 2 números, 2 números
        final String CL_PLATE_REGEX = "^[A-Z]{2}([A-Z]{2}|[0-9]{2})[0-9]{2}$";
        Pattern patternClPlate = Pattern.compile(CL_PLATE_REGEX);

        // Verificar formato ACRISS, formato patente, y valor positivo del precio
        if (!validarAcriss(acriss) || !patternClPlate.matcher(patente.toUpperCase())
            .matches()
            || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Parámetros de vehículo inválidos");
        }

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
     *
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
        return patternAcriss.matcher(acriss.toUpperCase())
            .matches();
    }

    /**
     * Permite obtener todos los vehículos en la base de datos.
     *
     * @return Lista de todos los vehículos
     */
    public List<VehiculoEntity> obtenerVehiculos() {
        List<VehiculoEntity> vehiculos = vehiculoRepository.findAll();
        // Opcionalmente, puedes filtrar los vehículos que no tienen sucursal
        return vehiculos.stream()
            .filter(v -> v.getSucursal() != null)
            .collect(Collectors.toList());
    }

    public VehiculoEntity obtenerVehiculoPorId(Long id) {
        Optional<VehiculoEntity> vehiculo = vehiculoRepository.findById(id);
        if (vehiculo.isEmpty()) throw new NoSuchElementException("Vehiculo no encontrado");
        return vehiculo.get();
    }

    /**
     * Permite obtener un vehículo con una patente determinada.
     *
     * @param patente Patente del vehículo a buscar
     * @return Vehículo con la patente buscada, si existe.
     */
    public Optional<VehiculoEntity> obtenerVehiculoPorPatente(String patente) {
        return vehiculoRepository.findByPatente(patente);
    }

    /**
     * Permite obtener todos los vehículos de una marca determinada.
     *
     * @param marca Marca de los vehículos
     * @return Lista de vehículos de esa marca
     */
    public List<VehiculoEntity> obtenerVehiculosPorMarca(String marca) {
        return vehiculoRepository.findByMarca(marca);
    }

    /**
     * Permite obtener todos los vehículos en base a uno o más caracteres del código ACRISS
     * Deben haber '_' para representar que no importa ese caracter en particular
     * (Por ejemplo, acriss = "__ME" buscaría todos los vehículos eléctricos con transmisión manual)
     *
     * @param acriss Código ACRISS
     * @return Lista de vehículos con código ACRISS acorde a lo pedido
     */
    public List<VehiculoEntity> obtenerVehiculosPorAcriss(String acriss) {
        if (!validarAcriss(acriss)) {
            throw new IllegalArgumentException("Código ACRISS no válido");
        }
        return vehiculoRepository.findByAcrissLike(acriss.toUpperCase());
    }

    /**
     * Permite obtener todos los vehículos asociados a cierta sucursal.
     *
     * @param sucursal Sucursal a buscar
     * @return Lista de vehículos en esa sucursal
     */
    public List<VehiculoEntity> obtenerVehiculosPorSucursal(SucursalEntity sucursal) {
        List<VehiculoEntity> v = vehiculoRepository.findBySucursal(sucursal);
        if (v.isEmpty()) throw new RuntimeException("No hay vehiculos en la sucursal " + sucursal.getNombre());
        return v;
    }

    /**
     * Permite obtener todos los vehículso cuyo precio esté dentro de un rango dado
     *
     * @param min Precio mínimo de arriendo
     * @param max Precio máximo de arriendo
     * @return Lista de vehículos con precio de arriendo entre min y max
     */
    public List<VehiculoEntity> obtenerVehiculosConPrecioEntre(BigDecimal min, BigDecimal max) {
        if (min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio inválido");
        }

        return vehiculoRepository.findByPrecioArriendoBetween(min, max);
    }

    /**
     * Permite obtener todos los vehículos que se encuentren en cierto estado
     *
     * @param estado Estado de los vehículos
     * @return Lista de vehículos en ese estado
     */
    public List<VehiculoEntity> obtenerVehiculosPorEstado(String estado) {
        List<VehiculoEntity> v = vehiculoRepository.findByEstado(VehiculoEntity.EstadoVehiculo.valueOf(estado));
        if (v.isEmpty()) throw new RuntimeException("No se encontraron vehiculos en ese estado");
        return v;
    }

    /**
     * Permite actualizar el estado de un vehículo dado su ID
     *
     * @param vehiculoId ID del vehículo a actualizar
     * @param estado     Estado nuevo del vehículo
     * @return Vehículo actualizado con ese nuevo estado
     */
    public VehiculoEntity actualizarEstadoVehiculoPorId(Long vehiculoId, String estado) {
        VehiculoEntity vehiculo = vehiculoRepository.findById(vehiculoId)
            .orElseThrow();
        vehiculo.setEstado(VehiculoEntity.EstadoVehiculo.valueOf(estado));
        return vehiculoRepository.save(vehiculo);
    }

    /**
     * Permite actualizar el precio de arriendo de un vehiculo dado su ID
     *
     * @param vehiculoId ID del vehiculo a actualizar
     * @param precio     Precio nuevo del vehiculo
     * @return Vehiculo actualizado con ese nuevo precio
     */
    public VehiculoEntity actualizarPrecioArriendoVehiculoPorId(Long vehiculoId, BigDecimal precio) {
        VehiculoEntity vehiculo = vehiculoRepository.findById(vehiculoId)
            .orElseThrow();
        vehiculo.setPrecioArriendo(precio);
        return vehiculoRepository.save(vehiculo);
    }

    /**
     * Permite eliminar un vehículo de la base de datos dado su ID
     *
     * @param vehiculoId ID del vehículo a eliminar
     */
    public void eliminarVehiculoPorId(Long vehiculoId) {
        VehiculoEntity vehiculo = vehiculoRepository.findById(vehiculoId)
            .orElseThrow();
        if (!reservaRepository.findByVehiculo(vehiculo)
            .isEmpty()) {
            throw new RuntimeException("El vehiculo" + vehiculoId + "tiene reservas pendientes");
        } else {
            vehiculoRepository.deleteById(vehiculoId);
        }
    }

    public List<String> obtenerMarcasUnicas() {
        return vehiculoRepository.findDistinctMarcas();
    }

    public Map<String, String> obtenerTiposVehiculo() {
        Map<String, String> tipos = new LinkedHashMap<>();
        tipos.put("ECMR", "Sedán Económico");
        tipos.put("ICMR", "Sedán Intermedio");
        tipos.put("CFAR", "SUV Compacta");
        tipos.put("IFAR", "SUV Mediana");
        tipos.put("PFAR", "Camioneta");
        tipos.put("XDAR", "Deportivo");
        tipos.put("LDAR", "Lujo");
        tipos.put("HDAR", "Híbrido/Eléctrico");
        tipos.put("MVAR", "Minivan");
        return tipos;
    }

    public Map<String, String> obtenerTiposTransmision() {
        Map<String, String> tipos = new LinkedHashMap<>();
        tipos.put("M", "Manual");
        tipos.put("A", "Automática");
        return tipos;
    }
}
