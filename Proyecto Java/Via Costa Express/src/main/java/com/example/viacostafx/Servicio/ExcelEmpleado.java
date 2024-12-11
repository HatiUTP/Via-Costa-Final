package com.example.viacostafx.Servicio;

import com.example.viacostafx.Modelo.EmpleadosModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelEmpleado {

    public void exportEmpleados(List<EmpleadosModel> empleados, String filePath) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Empleados");

        // Crear un estilo de celda para el título
        CellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);

        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleCellStyle.setFont(titleFont);

        // Crear la fila del título
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue("Tabla de Empleados - VIA COSTA");
        titleCell.setCellStyle(titleCellStyle);

        // Unir celdas para centrar el título
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 7));

        // Crear una fila para la fecha
        Row dateRow = sheet.createRow(1);
        Cell dateCell = dateRow.createCell(7);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(new Date());
        dateCell.setCellValue("Fecha: " + currentDate);

        // Estilo para la fecha
        CellStyle dateCellStyle = workbook.createCellStyle();
        Font dateFont = workbook.createFont();
        dateFont.setFontHeightInPoints((short) 10);
        dateCellStyle.setFont(dateFont);
        dateCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        dateCell.setCellStyle(dateCellStyle);

        // Crear un estilo común para todas las celdas (cabecera y datos)
        CellStyle borderedCellStyle = workbook.createCellStyle();
        borderedCellStyle.setBorderBottom(BorderStyle.THIN);
        borderedCellStyle.setBorderTop(BorderStyle.THIN);
        borderedCellStyle.setBorderLeft(BorderStyle.THIN);
        borderedCellStyle.setBorderRight(BorderStyle.THIN);
        borderedCellStyle.setAlignment(HorizontalAlignment.CENTER);
        borderedCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font commonFont = workbook.createFont();
        commonFont.setFontHeightInPoints((short) 12);
        borderedCellStyle.setFont(commonFont);

        // Crear un estilo específico para el encabezado
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.cloneStyleFrom(borderedCellStyle);
        headerCellStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short) 12);
        headerCellStyle.setFont(headerFont);

        // Crear la fila de encabezados
        Row headerRow = sheet.createRow(2);
        String[] headers = {"ID", "Nombre", "Apellido", "DNI", "Teléfono", "Usuario", "Contraseña"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i + 1); // Comenzar en columna B
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Agregar los datos
        int rowNum = 3; // Comenzar en la fila 3
        for (EmpleadosModel empleado : empleados) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 1; // Comenzar en columna B

            // ID
            Cell cell = row.createCell(colNum++);
            cell.setCellValue(empleado.getId());
            cell.setCellStyle(borderedCellStyle);

            // Nombre
            cell = row.createCell(colNum++);
            cell.setCellValue(empleado.getNombre());
            cell.setCellStyle(borderedCellStyle);

            // Apellido
            cell = row.createCell(colNum++);
            cell.setCellValue(empleado.getApellido());
            cell.setCellStyle(borderedCellStyle);

            // DNI
            cell = row.createCell(colNum++);
            cell.setCellValue(empleado.getDNI());
            cell.setCellStyle(borderedCellStyle);

            // Teléfono
            cell = row.createCell(colNum++);
            cell.setCellValue(empleado.getTelefono());
            cell.setCellStyle(borderedCellStyle);

            // Usuario
            cell = row.createCell(colNum++);
            cell.setCellValue(empleado.getUsuario());
            cell.setCellStyle(borderedCellStyle);

            // Contraseña
            cell = row.createCell(colNum++);
            cell.setCellValue(empleado.getContrasenia());
            cell.setCellStyle(borderedCellStyle);
        }

        // Ajustar columnas automáticamente
        for (int i = 1; i <= headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Guardar el archivo
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
