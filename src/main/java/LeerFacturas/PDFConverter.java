package LeerFacturas;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.borders.Border;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PDFConverter {

    public static List<File> convertirExcelAPDF(String archivosExcel) throws IOException {
    List<File> archivosPDF = new ArrayList<>();
    String[] rutasExcel = archivosExcel.split(";");

    for (String rutaExcel : rutasExcel) {
        File archivoExcel = new File(rutaExcel.trim());
        File archivoPDF = new File(archivoExcel.getParent(), archivoExcel.getName().replaceAll("\\.xlsx$", ".pdf"));

        try (FileInputStream fis = new FileInputStream(archivoExcel);
             Workbook workbook = new XSSFWorkbook(fis);
             PdfWriter writer = new PdfWriter(archivoPDF);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Configurar márgenes del documento
            document.setMargins(20, 20, 20, 20);

            Sheet sheet = workbook.getSheetAt(0);

            // **Agregar logo**
            String rutaLogo = "C:/Users/Practicas/Documents/NetBeansProjects/FacturasExcel/src/main/java/LeerFacturas/CeforaLogo.png";
            ImageData imageData = ImageDataFactory.create(rutaLogo);
            Image image = new Image(imageData).scaleToFit(150, 75).setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(image);

            // Espaciado
            document.add(new Paragraph("\n"));

            // **Título Factura**
            document.add(new Paragraph("FACTURA")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

           // **Datos de la Factura en la misma fila**
            Table tableFactura = new Table(UnitValue.createPercentArray(new float[]{1, 0.75f, 2.5f, 1.25f, 1})).useAllAvailableWidth();

            // "Nº FACTURA:" a la izquierda
            tableFactura.addCell(celdaTitulo("Nº FACTURA:"));
            tableFactura.addCell(celdaDato(obtenerValorCelda(sheet.getRow(2).getCell(3)))); 

            // Espacio en blanco (celda vacía sin bordes)
            Cell espacioVacio = new Cell().setBorder(Border.NO_BORDER);  // Celda vacía sin bordes
            tableFactura.addCell(espacioVacio);

            // "FECHA EMISIÓN:" a la derecha
            tableFactura.addCell(celdaTitulo("FECHA EMISIÓN:"));
            tableFactura.addCell(celdaDato(obtenerValorCelda(sheet.getRow(2).getCell(6))));

            document.add(tableFactura);


            // Espaciado
            document.add(new Paragraph("\n"));

            // **Datos del Cliente**
            document.add(new Paragraph("CLIENTE")
                .setFontSize(14)
                .setBold()
                .setUnderline()
                .setTextAlignment(TextAlignment.LEFT));

            Table tableCliente = new Table(UnitValue.createPercentArray(new float[]{3, 7})).useAllAvailableWidth();
            tableCliente.addCell(celdaTitulo("NOMBRE:"));
            tableCliente.addCell(celdaDato(obtenerValorCelda(sheet.getRow(6).getCell(3))));
            tableCliente.addCell(celdaTitulo("C.I.F.:"));
            tableCliente.addCell(celdaDato(obtenerValorCelda(sheet.getRow(7).getCell(3))));
            tableCliente.addCell(celdaTitulo("TELÉFONO:"));
            tableCliente.addCell(celdaDato(obtenerValorCelda(sheet.getRow(7).getCell(5))));
            tableCliente.addCell(celdaTitulo("FAX:"));
            tableCliente.addCell(celdaDato(obtenerValorCelda(sheet.getRow(7).getCell(7))));
            tableCliente.addCell(celdaTitulo("DIRECCIÓN:"));
            tableCliente.addCell(celdaDato(obtenerValorCelda(sheet.getRow(8).getCell(3))));
            tableCliente.addCell(celdaTitulo("POBLACIÓN:"));
            tableCliente.addCell(celdaDato(obtenerValorCelda(sheet.getRow(9).getCell(3))));
            tableCliente.addCell(celdaTitulo("PROVINCIA:"));
            tableCliente.addCell(celdaDato(obtenerValorCelda(sheet.getRow(9).getCell(5))));
            tableCliente.addCell(celdaTitulo("C.P.:"));
            tableCliente.addCell(celdaDato(obtenerValorCelda(sheet.getRow(9).getCell(7))));
            tableCliente.addCell(celdaTitulo("E-MAIL:"));
            tableCliente.addCell(celdaDato(obtenerValorCelda(sheet.getRow(10).getCell(3))));
            document.add(tableCliente);

            // Espaciado
            document.add(new Paragraph("\n"));

            // **Concepto e Importe en la misma fila**
            document.add(new Paragraph("DETALLE")
                .setFontSize(14)
                .setBold()
                .setUnderline()
                .setTextAlignment(TextAlignment.LEFT));

            // **Datos del Concepto y Importe**
            Table tableConcepto = new Table(UnitValue.createPercentArray(new float[]{1, 2, 0.2f, 1, 1})).useAllAvailableWidth();

            // Ajusta el espacio entre CONCEPTO y IMPORTE
            tableConcepto.addCell(celdaTitulo("CONCEPTO:"));
            tableConcepto.addCell(celdaDato(obtenerValorCelda(sheet.getRow(13).getCell(1))));

            // Espacio en blanco (celda vacía sin bordes)
            Cell espacioVacio1 = new Cell().setBorder(Border.NO_BORDER);  // Celda vacía sin bordes
            tableConcepto.addCell(espacioVacio1);

            tableConcepto.addCell(celdaTitulo("IMPORTE:"));
            tableConcepto.addCell(celdaDato(obtenerValorCelda(sheet.getRow(13).getCell(6))));

            document.add(tableConcepto);

            // Espaciado
            document.add(new Paragraph("\n"));

            // **Forma de pago**
            String formaDePagoTexto = obtenerValorCelda(sheet.getRow(15).getCell(1));
            String formaDePagoValor = "";
            if (formaDePagoTexto != null && formaDePagoTexto.contains(":")) {
                formaDePagoValor = formaDePagoTexto.split(":", 2)[1].trim();
            }
            // **(Con color verde)
            document.add(new Paragraph("Forma de pago: " + formaDePagoValor)
                .setFontSize(16)
                .setBold()
                .setFontColor(new DeviceRgb(0, 128, 0)) // Color verde
                .setTextAlignment(TextAlignment.LEFT));

            // **Total y Cuenta Bancaria**
            Table tableTotales = new Table(UnitValue.createPercentArray(new float[]{3, 7})).useAllAvailableWidth();
            tableTotales.addCell(celdaTitulo("TOTAL:"));
            tableTotales.addCell(celdaDato(obtenerValorCelda(sheet.getRow(15).getCell(6))));
            tableTotales.addCell(celdaTitulo("Nº DE CUENTA:"));
            tableTotales.addCell(celdaDato(obtenerValorCelda(sheet.getRow(16).getCell(2))));
            document.add(tableTotales);

            // Espaciado
            document.add(new Paragraph("\n"));

            // **Agregar el sello**
            String rutaSello = "C:/Users/Practicas/Documents/NetBeansProjects/FacturasExcel/src/main/java/LeerFacturas/firmaCefora.png";
            ImageData selloData = ImageDataFactory.create(rutaSello);
            Image sello = new Image(selloData).scaleToFit(100, 50).setHorizontalAlignment(HorizontalAlignment.RIGHT);
            document.add(sello);

            // **Nota Legal**
            Paragraph notaIVA = new Paragraph("% IVA ENSEÑANZA EXENTO, SEGÚN ARTÍCULO 20.9 DE LA LEY DE 28 DE DICIEMBRE DEL IMPUESTO SOBRE EL VALOR AÑADIDO (BOE 29 DE DICIEMBRE)")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic();
            document.add(notaIVA);

            archivosPDF.add(archivoPDF);
        }
    }
    return archivosPDF;
}

// Método auxiliar para crear celdas de títulos con estilo
private static Cell celdaTitulo(String texto) {
    return new Cell()
        .add(new Paragraph(texto).setBold())
        .setBackgroundColor(new DeviceGray(0.85f)); // 0.85f da un gris claro
}

// Método auxiliar para crear celdas de datos con estilo
private static Cell celdaDato(String texto) {
    return new Cell().add(new Paragraph(texto));
}



    /**
     * Convierte el valor de una celda de Excel en un String manejando distintos tipos de datos.
     */
    private static String obtenerValorCelda(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) {
            return "";  // Si la celda es null, devolver una cadena vacía
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";  // Si la celda está en blanco, devolver cadena vacía
            default:
                return " ";  // Para cualquier otro tipo, devolver espacio en blanco
        }
    }

}
