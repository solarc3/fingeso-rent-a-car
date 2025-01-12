package com.rentacar.backend.services;

import ch.qos.logback.classic.Logger;
import com.rentacar.backend.dto.VehiculoDTO;
import com.rentacar.backend.entities.*;
import com.rentacar.backend.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class VehiculoService {
    private final VehiculoRepository vehiculoRepository;
    private final ReservaRepository reservaRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final FallaVehiculoRepository fallaRepository;
    private final HistorialVehiculoRepository historialRepository;

    @Autowired
    public VehiculoService(
        VehiculoRepository vehiculoRepository,
        ReservaRepository reservaRepository,
        SucursalRepository sucursalRepository,
        UsuarioRepository usuarioRepository,
        FallaVehiculoRepository fallaRepository,
        HistorialVehiculoRepository historialRepository
                          ) {
        this.vehiculoRepository = vehiculoRepository;
        this.reservaRepository = reservaRepository;
        this.sucursalRepository = sucursalRepository;
        this.usuarioRepository = usuarioRepository;
        this.fallaRepository = fallaRepository;
        this.historialRepository = historialRepository;
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
                                        String patente, BigDecimal precio, Integer anio,
                                        VehiculoEntity.EstadoVehiculo estado) {
        // Validar precio
        if (precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio inválido");
        }

        // Usar ACRISS por defecto si no se proporciona o es inválido
        String acrissValidado = validarAcriss(acriss) ? acriss : "ECMR";

        // Buscar la sucursal
        //SucursalEntity sucursal = sucursalRepository.findById(sucursalId)
        //    .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada"));

        VehiculoEntity vehiculo = new VehiculoEntity();
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setAcriss(acrissValidado);
        vehiculo.setPatente(patente);
        vehiculo.setPrecioArriendo(precio);
        vehiculo.setAnio(anio);
        vehiculo.setEstado(estado != null ? estado : VehiculoEntity.EstadoVehiculo.DISPONIBLE);
        vehiculo.setDisponible(estado == VehiculoEntity.EstadoVehiculo.DISPONIBLE);

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

    public VehiculoEntity obtenerVehiculoPorId(Long id) {
        Optional<VehiculoEntity> vehiculo = vehiculoRepository.findById(id);
        if (vehiculo.isEmpty()) throw new NoSuchElementException("Vehiculo no encontrado");
        return vehiculo.get();
    }

    /**
     * Permite obtener todos los vehículos asociados a cierta sucursal.
     *
     * @param sucursal Sucursal a buscar
     * @return Lista de vehículos en esa sucursal
     */

    public List<VehiculoEntity> obtenerVehiculosPorSucursal(SucursalEntity sucursal) {
        List<VehiculoEntity> v = sucursal.getVehiculos();
                //vehiculoRepository.findBySucursal(sucursal);
        if (v.isEmpty()) throw new RuntimeException("No hay vehiculos en la sucursal " + sucursal.getNombre());
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

    @Transactional
    public VehiculoEntity reportarFalla(Long vehiculoId, FallaVehiculoEntity falla) {
        // Validaciones
        if (vehiculoId == null || falla == null) {
            throw new IllegalArgumentException("Datos de falla incompletos");
        }

        // Validar el vehículo
        VehiculoEntity vehiculo = vehiculoRepository.findById(vehiculoId)
            .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        // Validar el usuario que reporta
        UsuarioEntity reportadoPor = usuarioRepository.findById(falla.getReportadoPorId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Configurar la falla
        falla.setVehiculo(vehiculo);
        falla.setFechaReporte(LocalDateTime.now());
        falla.setReportadoPor(reportadoPor);

        // Guardar la falla
        fallaRepository.save(falla);

        // Actualizar estado del vehículo
        String estadoAnterior = vehiculo.getEstado()
            .toString();
        vehiculo.setEstado(VehiculoEntity.EstadoVehiculo.EN_MANTENCION);
        vehiculo = vehiculoRepository.save(vehiculo);

        // Registrar en el historial
        HistorialVehiculoEntity historial = new HistorialVehiculoEntity();
        historial.setVehiculo(vehiculo);
        historial.setFecha(LocalDateTime.now());
        historial.setTipoEvento("REPORTE_FALLA");
        historial.setDescripcion(String.format("Falla %s reportada: %s (Severidad: %s)",
                                               falla.getTipo(), falla.getDescripcion(), falla.getSeveridad()));
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(vehiculo.getEstado()
                                     .toString());
        historial.setRegistradoPor(reportadoPor);
        historialRepository.save(historial);

        return vehiculo;
    }

    public List<VehiculoEntity> obtenerTodos() {
        return vehiculoRepository.findAll();
    }

    public VehiculoEntity actualizarVehiculo(VehiculoEntity vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }

}
