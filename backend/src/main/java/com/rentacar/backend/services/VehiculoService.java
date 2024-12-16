package com.rentacar.backend.services;

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
                                        VehiculoEntity.EstadoVehiculo estado, Long sucursalId) {
        // Validar precio
        if (precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio inválido");
        }

        // Usar ACRISS por defecto si no se proporciona o es inválido
        String acrissValidado = validarAcriss(acriss) ? acriss : "ECMR";

        // Buscar la sucursal
        SucursalEntity sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada"));

        VehiculoEntity vehiculo = new VehiculoEntity();
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setAcriss(acrissValidado);
        vehiculo.setPatente(patente);
        vehiculo.setPrecioArriendo(precio);
        vehiculo.setAnio(anio);
        vehiculo.setSucursal(sucursal);
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

    /**
     * Permite obtener todos los vehículos en la base de datos.
     *
     * @return Lista de todos los vehículos
     */
    public List<VehiculoEntity> obtenerVehiculos() {
        return vehiculoRepository.findAllWithSucursales();
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
     * Permite actualizar el estado de un vehículo dado su ID y validar que no tenga reservas activas
     * @param id ID del vehículo a actualizar
     * @param nuevoEstado Estado nuevo del vehículo
     */
    public void actualizarEstado(Long id, VehiculoEntity.EstadoVehiculo nuevoEstado) {
        VehiculoEntity vehiculo = vehiculoRepository.findById(id)
                .orElseThrow();

        // Validar que no tenga reservas activas
        if (tieneReservasActivas(vehiculo)) {
            throw new RuntimeException("No se puede cambiar el estado - tiene reservas activas");
        }

        vehiculo.setEstado(nuevoEstado);
        vehiculoRepository.save(vehiculo);
    }

    private boolean tieneReservasActivas(VehiculoEntity vehiculo) {
        return !reservaRepository
                .findByVehiculoAndEstado(vehiculo, ReservaEntity.EstadoReserva.EN_PROGRESO)
                .isEmpty();
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
    public VehiculoEntity reportarFalla(Long vehiculoId, String tipo, String severidad,
                                        String descripcion, Long reportadoPorId) {
        // Validaciones más específicas
        if (descripcion == null || descripcion.trim()
                .isEmpty()) {
            throw new IllegalArgumentException("La descripción de la falla es requerida");
        }

        // Validar que el vehículo esté disponible para reportar falla
        VehiculoEntity vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        if (vehiculo.getEstado() == VehiculoEntity.EstadoVehiculo.EN_MANTENCION ||
                vehiculo.getEstado() == VehiculoEntity.EstadoVehiculo.EN_REPARACION) {
            throw new IllegalStateException("El vehículo ya está en mantenimiento o reparación");
        }

        // Verificar si tiene reservas activas
        List<ReservaEntity> reservasActivas = reservaRepository.findByVehiculo(vehiculo)
                .stream()
                .filter(r -> r.getEstado() == ReservaEntity.EstadoReserva.EN_PROGRESO)
                .toList();

        if (!reservasActivas.isEmpty()) {
            throw new IllegalStateException("No se puede reportar falla en un vehículo con reserva activa");
        }
        // Validaciones
        if (vehiculoId == null || tipo == null || severidad == null) {
            throw new IllegalArgumentException("Datos de falla incompletos");
        }

        // Obtener el usuario que reporta
        UsuarioEntity reportadoPor = usuarioRepository.findById(reportadoPorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que el usuario sea trabajador o administrador
        if (!reportadoPor.getRol()
                .equals(UsuarioEntity.RolUsuario.TRABAJADOR) &&
                !reportadoPor.getRol()
                        .equals(UsuarioEntity.RolUsuario.ADMINISTRADOR)) {
            throw new RuntimeException("Usuario no autorizado para reportar fallas");
        }

        // Crear y guardar la falla
        FallaVehiculoEntity falla = new FallaVehiculoEntity();
        falla.setVehiculo(vehiculo);
        falla.setTipo(tipo);
        falla.setSeveridad(severidad);
        falla.setDescripcion(descripcion);
        falla.setFechaReporte(LocalDateTime.now());
        falla.setReportadoPor(reportadoPor);
        fallaRepository.save(falla);

        // Actualizar estado del vehículo
        String estadoAnterior = vehiculo.getEstado()
                .toString();
        vehiculo.setEstado(VehiculoEntity.EstadoVehiculo.EN_MANTENCION);
        vehiculo = vehiculoRepository.save(vehiculo);

        // Registrar en historial
        HistorialVehiculoEntity historial = new HistorialVehiculoEntity();
        historial.setVehiculo(vehiculo);
        historial.setFecha(LocalDateTime.now());
        historial.setTipoEvento("REPORTE_FALLA");
        historial.setDescripcion(String.format("Falla %s reportada: %s (Severidad: %s)",
                tipo, descripcion, severidad));
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(vehiculo.getEstado()
                .toString());
        historial.setRegistradoPor(reportadoPor);
        historialRepository.save(historial);


        return vehiculo;
    }

    // Método adicional para obtener el historial de fallas de un vehículo
    public List<FallaVehiculoEntity> obtenerHistorialFallas(Long vehiculoId) {
        VehiculoEntity vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        return fallaRepository.findByVehiculoOrderByFechaReporteDesc(vehiculo);
    }

    @Transactional
    public VehiculoEntity resolverFalla(Long fallaId, String solucion, Long tecnicoId) {
        FallaVehiculoEntity falla = fallaRepository.findById(fallaId)
                .orElseThrow(() -> new RuntimeException("Falla no encontrada"));

        VehiculoEntity vehiculo = falla.getVehiculo();
        UsuarioEntity tecnico = usuarioRepository.findById(tecnicoId)
                .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));

        // Registrar solución en el historial
        HistorialVehiculoEntity historial = new HistorialVehiculoEntity();
        historial.setVehiculo(vehiculo);
        historial.setFecha(LocalDateTime.now());
        historial.setTipoEvento("RESOLUCION_FALLA");
        historial.setDescripcion("Falla resuelta: " + solucion);
        historial.setEstadoAnterior(vehiculo.getEstado()
                .toString());
        historial.setEstadoNuevo(VehiculoEntity.EstadoVehiculo.DISPONIBLE.toString());
        historial.setRegistradoPor(tecnico);
        historialRepository.save(historial);

        // Actualizar estado del vehículo
        vehiculo.setEstado(VehiculoEntity.EstadoVehiculo.DISPONIBLE);
        return vehiculoRepository.save(vehiculo);
    }
}
