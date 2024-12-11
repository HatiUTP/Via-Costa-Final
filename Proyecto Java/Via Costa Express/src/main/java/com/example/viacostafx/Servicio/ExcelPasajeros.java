package com.example.viacostafx.Servicio;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.viacostafx.Auxiliar.PasajeroData;
import com.example.viacostafx.Modelo.ChoferModel;

public class ExcelPasajeros {

    private CellStyle crearEstiloBordes(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        estilo.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        estilo.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        estilo.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        estilo.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setWrapText(true);
        return estilo;
    }

    public void exportPasajeros(List<PasajeroData> pasajeros, String rutaArchivo,
                               String origen, String destino, LocalDateTime fechaHora,
                               String placaBus, String categoria, List<ChoferModel> choferes) {
        try (Workbook libro = new XSSFWorkbook()) {
            Sheet hoja = libro.createSheet("Lista de Pasajeros");

            // Estilo título principal
            CellStyle estiloTitulo = libro.createCellStyle();
            estiloTitulo.setAlignment(HorizontalAlignment.CENTER);
            estiloTitulo.setVerticalAlignment(VerticalAlignment.CENTER);
            estiloTitulo.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            estiloTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font fuenteTitulo = libro.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setColor(IndexedColors.WHITE.getIndex());
            fuenteTitulo.setFontHeightInPoints((short) 16);
            estiloTitulo.setFont(fuenteTitulo);

            // Título principal
            Row filaTitulo = hoja.createRow(0);
            filaTitulo.setHeight((short) 800);
            Cell celdaTitulo = filaTitulo.createCell(0);
            celdaTitulo.setCellValue("LISTA DE PASAJEROS - VIA COSTA EXPRESS");
            celdaTitulo.setCellStyle(estiloTitulo);
            hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            // Estilo para subtítulos
            CellStyle estiloSubtitulo = crearEstiloBordes(libro);
            Font fuenteSubtitulo = libro.createFont();
            fuenteSubtitulo.setBold(true);
            fuenteSubtitulo.setFontHeightInPoints((short) 11);
            estiloSubtitulo.setFont(fuenteSubtitulo);

            // Información del viaje
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

            Row filaInfo1 = hoja.createRow(2);
            filaInfo1.setHeight((short) 400);
            Cell celdaOrigen = filaInfo1.createCell(0);
            celdaOrigen.setCellValue("Origen: " + origen);
            celdaOrigen.setCellStyle(estiloSubtitulo);
            hoja.addMergedRegion(new CellRangeAddress(2, 2, 0, 2));

            Cell celdaDestino = filaInfo1.createCell(3);
            celdaDestino.setCellValue("Destino: " + destino);
            celdaDestino.setCellStyle(estiloSubtitulo);
            hoja.addMergedRegion(new CellRangeAddress(2, 2, 3, 6));

            Row filaInfo2 = hoja.createRow(3);
            filaInfo2.setHeight((short) 400);
            Cell celdaFecha = filaInfo2.createCell(0);
            celdaFecha.setCellValue("Fecha: " + fechaHora.format(formatoFecha));
            celdaFecha.setCellStyle(estiloSubtitulo);
            hoja.addMergedRegion(new CellRangeAddress(3, 3, 0, 2));

            Cell celdaHora = filaInfo2.createCell(3);
            celdaHora.setCellValue("Hora: " + fechaHora.format(formatoHora));
            celdaHora.setCellStyle(estiloSubtitulo);
            hoja.addMergedRegion(new CellRangeAddress(3, 3, 3, 6));

            Row filaInfo3 = hoja.createRow(4);
            filaInfo3.setHeight((short) 400);
            Cell celdaBus = filaInfo3.createCell(0);
            celdaBus.setCellValue("Bus: " + placaBus);
            celdaBus.setCellStyle(estiloSubtitulo);
            hoja.addMergedRegion(new CellRangeAddress(4, 4, 0, 2));

            Cell celdaCategoria = filaInfo3.createCell(3);
            celdaCategoria.setCellValue("Categoría: " + categoria);
            celdaCategoria.setCellStyle(estiloSubtitulo);
            hoja.addMergedRegion(new CellRangeAddress(4, 4, 3, 6));

            // Información de choferes
            Row filaChoferes = hoja.createRow(5);
            filaChoferes.setHeight((short) 400);
            StringBuilder strChoferes = new StringBuilder("Choferes: ");
            for (int i = 0; i < choferes.size(); i++) {
                strChoferes.append(choferes.get(i).getNombre());
                if (i < choferes.size() - 1) {
                    strChoferes.append(", ");
                }
            }
            Cell celdaChoferes = filaChoferes.createCell(0);
            celdaChoferes.setCellValue(strChoferes.toString());
            celdaChoferes.setCellStyle(estiloSubtitulo);
            hoja.addMergedRegion(new CellRangeAddress(5, 5, 0, 6));

            // Estilo para encabezados de tabla
            CellStyle estiloEncabezado = crearEstiloBordes(libro);
            estiloEncabezado.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font fuenteEncabezado = libro.createFont();
            fuenteEncabezado.setBold(true);
            fuenteEncabezado.setColor(IndexedColors.WHITE.getIndex());
            fuenteEncabezado.setFontHeightInPoints((short) 11);
            estiloEncabezado.setFont(fuenteEncabezado);

            // Encabezados de tabla
            Row filaEncabezados = hoja.createRow(7);
            filaEncabezados.setHeight((short) 500);
            String[] encabezados = {"N°", "Nombre", "DNI", "Teléfono", "Email", "N° Asiento", "Precio"};
            for (int i = 0; i < encabezados.length; i++) {
                Cell celda = filaEncabezados.createCell(i);
                celda.setCellValue(encabezados[i]);
                celda.setCellStyle(estiloEncabezado);
            }

            // Estilo para datos
            CellStyle estiloDatos = crearEstiloBordes(libro);
            
            // Estilo especial para precios
            CellStyle estiloPrecio = libro.createCellStyle();
            estiloPrecio.cloneStyleFrom(estiloDatos);
            DataFormat formato = libro.createDataFormat();
            estiloPrecio.setDataFormat(formato.getFormat("\"S/\"#,##0.00"));

            // Datos de pasajeros
            int numFila = 8;
            int contador = 1;
            for (PasajeroData pasajero : pasajeros) {
                Row fila = hoja.createRow(numFila++);
                fila.setHeight((short) 400);

                Cell celdaNumero = fila.createCell(0);
                celdaNumero.setCellValue(contador++);
                celdaNumero.setCellStyle(estiloDatos);

                Cell celdaNombre = fila.createCell(1);
                celdaNombre.setCellValue(pasajero.getNombre());
                celdaNombre.setCellStyle(estiloDatos);

                Cell celdaDni = fila.createCell(2);
                celdaDni.setCellValue(pasajero.getDni());
                celdaDni.setCellStyle(estiloDatos);

                Cell celdaTelefono = fila.createCell(3);
                celdaTelefono.setCellValue(pasajero.getTelefono());
                celdaTelefono.setCellStyle(estiloDatos);

                Cell celdaEmail = fila.createCell(4);
                celdaEmail.setCellValue(pasajero.getEmail());
                celdaEmail.setCellStyle(estiloDatos);

                Cell celdaAsiento = fila.createCell(5);
                celdaAsiento.setCellValue(pasajero.getAsiento());
                celdaAsiento.setCellStyle(estiloDatos);

                Cell celdaPrecio = fila.createCell(6);
                celdaPrecio.setCellValue(pasajero.getPrecio());
                celdaPrecio.setCellStyle(estiloPrecio);
            }

            // Establecer anchos de columna
            hoja.setColumnWidth(0, 2500);  // N°
            hoja.setColumnWidth(1, 8000);  // Nombre
            hoja.setColumnWidth(2, 4000);  // DNI
            hoja.setColumnWidth(3, 5000);  // Teléfono
            hoja.setColumnWidth(4, 8000);  // Email
            hoja.setColumnWidth(5, 3500);  // N° Asiento
            hoja.setColumnWidth(6, 4000);  // Precio

            // Agregar filtros
            hoja.setAutoFilter(new CellRangeAddress(7, 7, 0, 6));

            // Congelar panel
            hoja.createFreezePane(0, 8);

            // Guardar archivo
            try (FileOutputStream salida = new FileOutputStream(rutaArchivo)) {
                libro.write(salida);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el archivo Excel: " + e.getMessage());
        }
    }
}