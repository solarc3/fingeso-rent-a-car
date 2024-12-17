package com.rentacar.backend.services;

import com.rentacar.backend.entities.*;
import com.rentacar.backend.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class MantenimientoService {
    private final MantenimientoRepository mantenimientoRepository;
    private final HistorialVehiculoRepository historialRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public MantenimientoService(
        MantenimientoRepository mantenimientoRepository,
        HistorialVehiculoRepository historialRepository,
        VehiculoRepository vehiculoRepository,
        UsuarioRepository usuarioRepository
                               ) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.historialRepository = historialRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<MantenimientoEntity> obtenerMantenimientosVehiculo(Long vehiculoId) {
        VehiculoEntity vehiculo = vehiculoRepository.findById(vehiculoId)
            .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        return mantenimientoRepository.findByVehiculo(vehiculo);
    }

    @Transactional
    public MantenimientoEntity actualizarEstadoMantenimiento(Long mantenimientoId, String nuevoEstado) {
        if (!Arrays.asList("PENDIENTE", "EN_PROCESO", "COMPLETADO", "CANCELADO")
            .contains(nuevoEstado)) {
            throw new IllegalArgumentException("Estado inválido: " + nuevoEstado);
        }

        MantenimientoEntity mantenimiento = mantenimientoRepository.findById(mantenimientoId)
            .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));

        String estadoAnterior = mantenimiento.getEstado();
        mantenimiento.setEstado(nuevoEstado);

        // si se completa el mantenimiento
        if (nuevoEstado.equals("COMPLETADO")) {
            mantenimiento.setFechaRealizada(LocalDateTime.now());
        }

        // registrar en el historial
        registrarHistorial(
            mantenimiento.getVehiculo(),
            "ACTUALIZACION_MANTENIMIENTO",
            "Estado de mantenimiento actualizado: " + estadoAnterior + " -> " + nuevoEstado,
            estadoAnterior,
            nuevoEstado,
            null // o el usuario que realiza el cambio si está disponible
                          );

        // si el mantenimiento se completa o cancela, actualizar el estado del vehiculo
        if (nuevoEstado.equals("COMPLETADO")) {
            VehiculoEntity vehiculo = mantenimiento.getVehiculo();
            vehiculo.setEstado(VehiculoEntity.EstadoVehiculo.DISPONIBLE);
            vehiculoRepository.save(vehiculo);

            // registrar el cambio de estado del vehiculo
            registrarHistorial(
                vehiculo,
                "CAMBIO_ESTADO",
                "Vehículo disponible después de mantenimiento",
                "EN_MANTENCION",
                "DISPONIBLE",
                null
                              );
        }

        return mantenimientoRepository.save(mantenimiento);
    }

    private void registrarHistorial(
        VehiculoEntity vehiculo,
        String tipoEvento,
        String descripcion,
        String estadoAnterior,
        String estadoNuevo,
        UsuarioEntity registradoPor
                                   ) {
        HistorialVehiculoEntity historial = new HistorialVehiculoEntity();
        historial.setVehiculo(vehiculo);
        historial.setFecha(LocalDateTime.now());
        historial.setTipoEvento(tipoEvento);
        historial.setDescripcion(descripcion);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setRegistradoPor(registradoPor);

        historialRepository.save(historial);
    }

    @Transactional
    public MantenimientoEntity programarMantenimiento(
        Long vehiculoId,
        LocalDateTime fechaProgramada,
        String tipoMantenimiento,
        String descripcion,
        Double costoEstimado,
        Long tecnicoId
                                                     ) {
        // Validaciones
        if (vehiculoId == null) {
            throw new IllegalArgumentException("ID de vehículo es requerido");
        }

        if (fechaProgramada == null) {
            throw new IllegalArgumentException("Fecha programada es requerida");
        }

        if (fechaProgramada.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha programada debe ser futura");
        }

        if (tipoMantenimiento == null || tipoMantenimiento.trim()
            .isEmpty()) {
            throw new IllegalArgumentException("Tipo de mantenimiento es requerido");
        }

        // Buscar vehiculo
        VehiculoEntity vehiculo = vehiculoRepository.findById(vehiculoId)
            .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        // Buscar tecnico si fue especificado
        UsuarioEntity tecnico = null;
        if (tecnicoId != null) {
            tecnico = usuarioRepository.findById(tecnicoId)
                .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));

            // Validar que el técnico sea un trabajador
            if (!tecnico.getRol()
                .equals(UsuarioEntity.RolUsuario.TRABAJADOR)) {
                throw new IllegalArgumentException("El usuario asignado debe ser un técnico");
            }
        }

        // Crear mantenimiento
        MantenimientoEntity mantenimiento = new MantenimientoEntity();
        mantenimiento.setVehiculo(vehiculo);
        mantenimiento.setFechaProgramada(fechaProgramada);
        mantenimiento.setTipoMantenimiento(tipoMantenimiento);
        mantenimiento.setDescripcion(descripcion);
        mantenimiento.setCosto(costoEstimado);
        mantenimiento.setTecnico(tecnico);
        mantenimiento.setEstado("PENDIENTE");

        // Actualizar estado del vehiculo
        vehiculo.setEstado(VehiculoEntity.EstadoVehiculo.EN_MANTENCION);
        vehiculoRepository.save(vehiculo);

        // Registrar en el historial
        registrarHistorial(
            vehiculo,
            "MANTENIMIENTO_PROGRAMADO",
            "Mantenimiento programado: " + descripcion,
            "DISPONIBLE",
            "EN_MANTENCION",
            tecnico
                          );

        // Guardar y retornar el mantenimiento
        return mantenimientoRepository.save(mantenimiento);
    }

    public List<HistorialVehiculoEntity> obtenerHistorialVehiculo(Long vehiculoId) {
        VehiculoEntity vehiculo = vehiculoRepository.findById(vehiculoId)
            .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        return historialRepository.findByVehiculoOrderByFechaDesc(vehiculo);
    }
}