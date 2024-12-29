package com.rentacar.backend.services;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.rentacar.backend.dto.ReporteVentasDTO;
import com.rentacar.backend.entities.ReservaEntity;
import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.entities.UsuarioEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.entities.VehiculoEntity.EstadoVehiculo;
import com.rentacar.backend.repositories.ReservaRepository;
import com.rentacar.backend.repositories.SucursalRepository;
import com.rentacar.backend.repositories.UsuarioRepository;
import com.rentacar.backend.repositories.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final VehiculoRepository vehiculoRepository;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository,
                          SucursalRepository sucursalRepository, VehiculoRepository vehiculoRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.sucursalRepository = sucursalRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<ReservaEntity> obtenerReservas() {
        return reservaRepository.findAll();
    }

    public List<ReservaEntity> obtenerReservasDeUsuario(UsuarioEntity usuario) {
        return reservaRepository.findByUsuario(usuario);
    }

    public List<ReservaEntity> obtenerReservasDeVehiculo(VehiculoEntity vehiculo) {
        return reservaRepository.findByVehiculo(vehiculo);
    }

    public ReservaEntity extenderReserva(Long reservaId, LocalDateTime nuevaFechaFin) {
        Optional<ReservaEntity> reserva = reservaRepository.findById(reservaId);
        if (reserva.isEmpty()) {
            throw new NoSuchElementException("Reserva no encontrada");
        }

        // Comprobar periodo 30 dias
        ReservaEntity r = reserva.get();
        Duration treintaDias = Duration.ofDays(30);
        Duration periodoArriendo = Duration.between(r.getFechaFin(), nuevaFechaFin);

        if (periodoArriendo.compareTo(treintaDias) > 0) {
            throw new RuntimeException("Periodo superior a 30 dias");
        }

        r.setFechaFin(nuevaFechaFin);
        return reservaRepository.save(r);
    }

    @Transactional
    public ReservaEntity crearReserva(LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                      BigDecimal costo, Long usuarioID, Long vehiculoID,
                                      Long sucursalID, Long sucursalDevolucionID) {
        // Verificar disponibilidad
        if (!verificarDisponibilidad(vehiculoID, fechaInicio, fechaFin)) {
            throw new RuntimeException("Vehículo no disponible en las fechas seleccionadas");
        }

        // Crear reserva
        ReservaEntity reserva = new ReservaEntity();
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        reserva.setCosto(costo);
        reserva.setEstado(ReservaEntity.EstadoReserva.PENDIENTE); // Usar el enum
        reserva.setUsuario(usuarioRepository.findById(usuarioID)
                               .orElseThrow());
        reserva.setVehiculo(vehiculoRepository.findById(vehiculoID)
                                .orElseThrow());
        reserva.setSucursal(sucursalRepository.findById(sucursalID)
                                .orElseThrow());
        reserva.setSucursalDevolucion(sucursalRepository.findById(sucursalDevolucionID)
                                          .orElseThrow());

        // Actualizar estado del vehiculo
        VehiculoEntity vehiculo = reserva.getVehiculo();
        vehiculo.setEstado(EstadoVehiculo.ARRENDADO);
        vehiculoRepository.save(vehiculo);

        return reservaRepository.save(reserva);
    }

    public ReservaEntity actualizarEstado(Long reservaId, ReservaEntity.EstadoReserva nuevoEstado) {
        ReservaEntity reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reserva.setEstado(nuevoEstado);
        return reservaRepository.save(reserva);
    }

    public List<ReservaEntity> obtenerPorEstado(ReservaEntity.EstadoReserva estado) {
        return reservaRepository.findByEstado(estado);
    }

    // Método para verificar disponibilidad
    public boolean verificarDisponibilidad(Long vehiculoId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<ReservaEntity> reservasExistentes = reservaRepository.findByVehiculoAndFechasSuperpuestas(
            vehiculoId, fechaInicio, fechaFin
                                                                                                      );
        return reservasExistentes.isEmpty();
    }

    private ReporteVentasDTO generarDatosReporte() {
        ReporteVentasDTO reporte = new ReporteVentasDTO();

        // Calcular totales
        List<ReservaEntity> reservas = reservaRepository.findAll();
        reporte.setTotalReservas(reservas.size());
        reporte.setIngresoTotal(reservas.stream()
                .map(ReservaEntity::getCosto)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Calcular reservas por estado
        Map<String, Integer> reservasPorEstado = reservas.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getEstado().toString(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        reporte.setReservasPorEstado(reservasPorEstado);

        // Agregar más estadísticas...

        return reporte;
    }

    public byte[] generarReporteVentasPDF() throws Exception {
        ReporteVentasDTO datos = generarDatosReporte();
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
        Paragraph title = new Paragraph("Reporte de Ventas", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // Resumen general
        document.add(new Paragraph("Resumen General"));
        document.add(new Paragraph("Total de Reservas: " + datos.getTotalReservas()));
        document.add(new Paragraph("Ingreso Total: $" + datos.getIngresoTotal()));

        // Tabla de reservas por estado
        PdfPTable estadosTable = new PdfPTable(2);
        estadosTable.addCell("Estado");
        estadosTable.addCell("Cantidad");
        datos.getReservasPorEstado().forEach((estado, cantidad) -> {
            estadosTable.addCell(estado);
            estadosTable.addCell(cantidad.toString());
        });
        document.add(estadosTable);

        // Más secciones del reporte...

        document.close();
        return out.toByteArray();
    }

}