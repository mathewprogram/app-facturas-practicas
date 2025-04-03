package LeerFacturas;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class LeerFacturaPDF {

    // Leer el PDF y obtener el texto
    public static String leerPDF(String rutaArchivo) {
        try {
            PDDocument documento = PDDocument.load(new File(rutaArchivo));
            PDFTextStripper stripper = new PDFTextStripper();
            String texto = stripper.getText(documento);
            documento.close();
            return texto;
        } catch (IOException e) {
            System.out.println("Error al leer el archivo PDF: " + e.getMessage());
            return null;
        }
    }

    // Extraer los datos de la factura y guardarlos
    public static void extraerDatosFacturaYGuardar(String contenido, String rutaDestino) {
        String numeroFactura = extraerValor(contenido, "Nº FACTURA: ([\\d-]+)");
        String fechaEmision = extraerValor(contenido, "FECHA EMISIÓN: (\\d{2}/\\d{2}/\\d{4})");

        // Convertir la fecha a formato yyyy-mm-dd antes de guardarla o usarla
        try {
            fechaEmision = convertirFecha(fechaEmision);
        } catch (ParseException e) {
            System.out.println("Error al convertir la fecha: " + e.getMessage());
            return;
        }

        String nombre = extraerValor(contenido, "NOMBRE: (.+)");
        String cif = extraerValor(contenido, "C\\.I\\.F\\.:\\s*([\\w-]+)");
        String telefono = extraerValor(contenido, "TELÉFONO:\\s*(\\d+)");
        String direccion = extraerValor(contenido, "DIRECCIÓN:\\s*(.+)");
        String poblacion = extraerValor(contenido, "POBLACIÓN:\\s*(.+)");
        String provincia = extraerValor(contenido, "PROVINCIA:\\s*(.+)");
        String cp = extraerValor(contenido, "C\\.P\\.:\\s*(\\d+)");
        String email = extraerValor(contenido, "E-MAIL:\\s*([\\w.-]+@[\\w.-]+)");
        String concepto = extraerValor(contenido, "CONCEPTO:\\s*(.*?)\\s*IMPORTE:");
        String formaPago = extraerValor(contenido, "Forma de pago:\\s*(\\w+)");
        String total = extraerValor(contenido, "TOTAL:\\s*([0-9,.]+)");
        total = total.replace(',', '.'); // Reemplazar la coma por un punto
        String numeroCuenta = extraerValor(contenido, "N[º°]?\\s*DE\\s*CUENTA:?\\s*([A-Z]{2}\\d{2}[ \\d]+)");

        Connection connection = ConexionFacturas.obtenerConexion(); // Obtener conexión

        if (connection == null) {
            JOptionPane.showMessageDialog(null, "Error: No se pudo establecer la conexión con la base de datos.");
            return;
        }

        try {
            // Verificar si la factura ya existe en la base de datos
            if (facturaExiste(connection, numeroFactura)) {
                JOptionPane.showMessageDialog(null, "La factura con el número " + numeroFactura + " ya existe en la base de datos.");
                return; // No insertamos la factura si ya existe
            }

            // Verificar si la empresa existe en la base de datos
            if (!empresaExiste(connection, cif)) {
                // Si no existe, insertar la empresa
                int idEmpresa = insertarEmpresa(connection, nombre, cif);

                // Insertar la dirección
                insertarDireccion(connection, idEmpresa, direccion, cp, poblacion, provincia);

                // Insertar el contacto
                insertarContacto(connection, idEmpresa, telefono, email);

                if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
                    System.out.println("Error: El número de cuenta es inválido.");
                } else {
                    insertarCuentaBancaria(connection, idEmpresa, numeroCuenta);
                }

                // Insertar la cuenta bancaria
                insertarCuentaBancaria(connection, idEmpresa, numeroCuenta);

                // Insertar la factura
                int idFactura = insertarFactura(connection, idEmpresa, numeroFactura, fechaEmision, total, formaPago);

                // Insertar los artículos (si se desea)
                List<String> productos = Arrays.asList(concepto.split(",")); // Ejemplo de cómo podría hacerse
                List<Double> importes = Arrays.asList(Double.parseDouble(total)); // Ajusta según los importes de los productos

                insertarArticulos(connection, idFactura, productos, importes);

                // Mostrar mensaje de éxito
                JOptionPane.showMessageDialog(null, "Factura insertada correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "La empresa con el CIF " + cif + " ya existe.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al procesar la factura: " + e.getMessage());
        }

        // Guardar los datos extraídos en un archivo
        String datos = numeroFactura + "\n"
                + fechaEmision + "\n"
                + nombre + "\n"
                + cif + "\n"
                + telefono + "\n"
                + direccion + "\n"
                + poblacion + "\n"
                + provincia + "\n"
                + cp + "\n"
                + email + "\n"
                + concepto + "\n"
                + formaPago + "\n"
                + total + "\n"
                + numeroCuenta;

        guardarEnArchivo(datos, rutaDestino);
    }

// Método para verificar si la factura ya existe en la base de datos
    public static boolean facturaExiste(Connection connection, String numeroFactura) throws SQLException {
        String query = "SELECT COUNT(*) FROM Factura WHERE NumeroFactura = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, numeroFactura);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Método para extraer un valor de texto usando regex
    public static String extraerValor(String texto, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(texto);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "No encontrado";
    }

    // Método para guardar los datos extraídos en un archivo
    public static void guardarEnArchivo(String datos, String rutaDestino) {
        try (FileWriter writer = new FileWriter(rutaDestino)) {
            writer.write(datos);
            System.out.println("Datos guardados en: " + rutaDestino);
        } catch (IOException e) {
            System.out.println("Error al guardar archivo: " + e.getMessage());
        }
    }

    // Métodos para insertar en la base de datos
    public static boolean empresaExiste(Connection connection, String cif) throws SQLException {
        String query = "SELECT COUNT(*) FROM Empresa WHERE CIF = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, cif);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public static int insertarEmpresa(Connection connection, String nombre, String cif) throws SQLException {
        String query = "INSERT INTO Empresa (Nombre, CIF) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) { // Aquí está la corrección
            stmt.setString(1, nombre);
            stmt.setString(2, cif);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    public static void insertarDireccion(Connection connection, int idEmpresa, String direccion, String cp, String poblacion, String provincia) throws SQLException {
        String query = "INSERT INTO Direccion (Direccion, CodigoPostal, Poblacion, Provincia, Empresa_idEmpresa) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, direccion);
            stmt.setString(2, cp);
            stmt.setString(3, poblacion);
            stmt.setString(4, provincia);
            stmt.setInt(5, idEmpresa);
            stmt.executeUpdate();
        }
    }

    public static void insertarContacto(Connection connection, int idEmpresa, String telefono, String email) throws SQLException {
        String query = "INSERT INTO Contacto (Telefono, Email, Empresa_idEmpresa) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, telefono);
            stmt.setString(2, email);
            stmt.setInt(3, idEmpresa);
            stmt.executeUpdate();
        }
    }

    public static void insertarCuentaBancaria(Connection connection, int idEmpresa, String numeroCuenta) throws SQLException {
        System.out.println("Intentando insertar cuenta bancaria: " + numeroCuenta); // Imprime el número de cuenta

        // Verificar si la cuenta ya existe
        if (cuentaExiste(connection, numeroCuenta)) {
            System.out.println("La cuenta bancaria ya está registrada.");
            return; // No insertamos si ya existe
        }

        String query = "INSERT INTO EmpresaCuentas (NumeroCuenta, Empresa_idEmpresa) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, numeroCuenta);
            stmt.setInt(2, idEmpresa);
            stmt.executeUpdate();
            System.out.println("Cuenta bancaria insertada correctamente.");
        }
    }

    public static boolean cuentaExiste(Connection connection, String numeroCuenta) throws SQLException {
        String query = "SELECT COUNT(*) FROM EmpresaCuentas WHERE NumeroCuenta = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, numeroCuenta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retorna true si ya existe
            }
        }
        return false;
    }

    public static int insertarFactura(Connection connection, int idEmpresa, String numeroFactura, String fechaEmision, String total, String formaPago) throws SQLException {
        String query = "INSERT INTO Factura (IdEmpresa, NumeroFactura, FechaEmision, Total, FormaPago) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, idEmpresa);
            stmt.setString(2, numeroFactura);
            stmt.setString(3, fechaEmision);
            stmt.setDouble(4, Double.parseDouble(total));
            stmt.setString(5, formaPago);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idFactura = generatedKeys.getInt(1);

                    // Actualizar el campo IdCliente con el mismo valor que IdFactura
                    String updateQuery = "UPDATE Factura SET IdCliente = ? WHERE IdFactura = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, idFactura);
                        updateStmt.setInt(2, idFactura);
                        updateStmt.executeUpdate();
                    }

                    return idFactura;
                }
            }
        }
        return -1;
    }

    public static void insertarArticulos(Connection connection, int idFactura, List<String> productos, List<Double> importes) throws SQLException {
        String query = "INSERT INTO Articulos (Producto, Importe, Factura_IdFactura) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < productos.size(); i++) {
                stmt.setString(1, productos.get(i));
                stmt.setDouble(2, importes.get(i));
                stmt.setInt(3, idFactura);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    // Método para convertir la fecha de formato DD/MM/YYYY a YYYY-MM-DD
    public static String convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat sdfEntrada = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfSalida = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaDate = sdfEntrada.parse(fecha);
        return sdfSalida.format(fechaDate);
    }

    public static String buscarArchivoPorExtension(String carpeta, String extension) {
        File directorio = new File(carpeta);

        if (!directorio.exists() || !directorio.isDirectory()) {
            System.out.println("La carpeta especificada no existe o no es un directorio.");
            return null;
        }

        File[] archivos = directorio.listFiles((dir, name) -> name.toLowerCase().endsWith(extension));

        if (archivos != null && archivos.length > 0) {
            return archivos[0].getAbsolutePath(); // Retorna la ruta del primer archivo encontrado
        }

        return null; // No se encontró ningún archivo con la extensión
    }

}
