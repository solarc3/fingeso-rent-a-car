package com.rentacar.backend.services;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.rentacar.backend.dto.ReporteVentasDTO;
import com.rentacar.backend.entities.*;
import com.rentacar.backend.entities.VehiculoEntity.EstadoVehiculo;
import com.rentacar.backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final MantenimientoRepository mantenimientoRepository;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository,
                          SucursalRepository sucursalRepository, VehiculoRepository vehiculoRepository, MantenimientoRepository mantenimientoRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.sucursalRepository = sucursalRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.mantenimientoRepository = mantenimientoRepository;
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
        //vehiculo.setEstado(EstadoVehiculo.ARRENDADO);
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

    public byte[] generarReporteVentasPDF(String fechaInicio, String fechaFin) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Configure PDF
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(0, 35, 73));
        Paragraph title = new Paragraph("Reporte de Ventas", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Add date range
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, new Color(87, 87, 87));
        Paragraph dateRange = new Paragraph(
                String.format("Período: %s - %s", fechaInicio, fechaFin),
                dateFont
        );
        dateRange.setAlignment(Element.ALIGN_CENTER);
        dateRange.setSpacingAfter(20);
        document.add(dateRange);

        // Get relevant reservations
        List<ReservaEntity> reservas = reservaRepository.findByFechaInicioBetweenAndEstadoIn(
                LocalDateTime.parse(fechaInicio + "T00:00:00"),
                LocalDateTime.parse(fechaFin + "T23:59:59"),
                Arrays.asList(ReservaEntity.EstadoReserva.COMPLETADA, ReservaEntity.EstadoReserva.CONFIRMADA)
        );

        // Calculate net earnings
        BigDecimal ingresoTotal = reservas.stream()
                .map(ReservaEntity::getCosto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate maintenance costs
        BigDecimal costoMantenimiento = BigDecimal.valueOf(mantenimientoRepository.findByFechaRealizadaBetween(
                        LocalDateTime.parse(fechaInicio + "T00:00:00"),
                        LocalDateTime.parse(fechaFin + "T23:59:59")
                ).stream()
                .map(MantenimientoEntity::getCosto)
                .filter(Objects::nonNull)
                .reduce(0.0, Double::sum));

        BigDecimal costoMantenimientoDecimal = new BigDecimal(costoMantenimiento.toString());
        BigDecimal ingresoNeto = ingresoTotal.subtract(costoMantenimientoDecimal);

        // Add financial summary
        addFinancialSummary(document, ingresoTotal, costoMantenimientoDecimal, ingresoNeto);

        // Add reservations table
        addReservationsTable(document, reservas);

        document.close();
        return out.toByteArray();
    }

    private void addFinancialSummary(Document document, BigDecimal ingresoTotal,
                                     BigDecimal costoMantenimiento, BigDecimal ingresoNeto) throws DocumentException {

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(0, 35, 73));
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, new Color(33, 33, 33));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);
        table.setSpacingAfter(20);

        // Add rows
        addTableRow(table, "Ingreso Total:", formatCurrency(ingresoTotal), headerFont);
        addTableRow(table, "Costo Mantenimiento:", formatCurrency(costoMantenimiento), headerFont);
        addTableRow(table, "Ingreso Neto:", formatCurrency(ingresoNeto), headerFont);

        document.add(table);
    }

    private void addReservationsTable(Document document, List<ReservaEntity> reservas)
            throws DocumentException {

        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(255, 255, 255));
        Font tableContentFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(33, 33, 33));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

        // Add headers
        String[] headers = {"Fecha Inicio", "Fecha Fin", "Vehículo", "Sucursal", "Costo"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, tableHeaderFont));
            cell.setBackgroundColor(new Color(0, 35, 73));
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Add data
        for (ReservaEntity reserva : reservas) {
            table.addCell(new Phrase(formatDateTime(reserva.getFechaInicio()), tableContentFont));
            table.addCell(new Phrase(formatDateTime(reserva.getFechaFin()), tableContentFont));
            table.addCell(new Phrase(getVehicleDescription(reserva.getVehiculo()), tableContentFont));
            table.addCell(new Phrase(reserva.getSucursal().getNombre(), tableContentFont));
            table.addCell(new Phrase(formatCurrency(reserva.getCosto()), tableContentFont));
        }

        document.add(table);
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("$%,.0f", amount);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private String getVehicleDescription(VehiculoEntity vehiculo) {
        return String.format("%s %s (%s)",
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getPatente()
        );
    }

    private void addTableRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorderWidth(0);
        labelCell.setPadding(5);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorderWidth(0);
        valueCell.setPadding(5);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

}