package LeerFacturas;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.apache.commons.compress.utils.IOUtils;

public class Facturas {
    //FUNCIONA PARA RECORRER Y SELECCIONAR(INSERTAR Y SELECT) CADA DATO DE LA BASE DE DATOS CREADA----> FUNCIONA A TRAVÉS DE MÉTODOS
    // LOS DATOS SE RECOGEN DE UN ARCHIVO EXCEL .xlsx
    public static void guardarDatos(String archivo) {
        //
        if (archivo.equals("")) {
            archivo = "C:\\Users\\Practicas\\Documents\\NetBeansProjects\\FacturasExcel\\src\\main\\java\\LeerFacturas\\FacturaJorge.xlsx";
        }
        File inputFile = new File(archivo);
        File outputFile = new File("C:\\Users\\Practicas\\Documents\\NetBeansProjects\\FacturasExcel\\src\\main\\java\\LeerFacturas\\salida.txt");
        // CADA DATO SE RECOGEN EN UN ARRAY CON TAMAÑOS, DEPENDIENDO DE SU ESPECIFICACIÓN EN LA BBDD
        Object[] empresa = new Object[2];
        Object[] factura = new Object[4];
        Object[] direccion = new Object[5];
        Object[] contacto = new Object[3];
        Object[] articulo = new Object[2];
        ArrayList<Object> articulos = new ArrayList<>();
        // ESTAS SON COORDENADAS QUE RECORREREMOS PARA ENCONTRAR LAS CELDAS Y SU INFORMACION DETERMINANTE
        int[] filas = {2, 6, 7, 8, 9, 10};
        int[] columnas = {3, 5, 6, 7};
        int filaPosicion = 12;
        int columnaFormaPago = 1;
        int columnaTotal = 6;
        int IdFactura = 0;
        try (FileInputStream fis = new FileInputStream(inputFile); Workbook workbook = new XSSFWorkbook(fis); FileWriter writer = new FileWriter(outputFile)) {

            Sheet sheet = workbook.getSheetAt(0);

            int indiceEmpresa = 0, indiceFactura = 0, indiceDireccion = 0, indiceContacto = 0;
            
            // Cargar la imagen en memoria
            FileInputStream fisImagen = new FileInputStream("C:\\Users\\Practicas\\Documents\\NetBeansProjects\\FacturasExcel\\src\\main\\java\\LeerFacturas\\CeforaLogo.png");
            byte[] bytes = IOUtils.toByteArray(fisImagen);
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            fisImagen.close();

            // Obtener el dibujo de la hoja
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            CreationHelper helper = workbook.getCreationHelper();

            // Crear el anclaje de la imagen (Fila 1 a 2, Columnas F, G, H)
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(5); // Columna F (índice 5)
            anchor.setCol2(8); // Columna H (índice 8)
            anchor.setRow1(0); // Fila 1 (índice 0)
            anchor.setRow2(2); // Fila 2 (índice 2)

            // Insertar la imagen en el archivo Excel
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            pict.resize(1.0); // Ajustar el tamaño para que no ocupe más espacio

            // Guardar los cambios en el archivo Excel
            try (FileOutputStream fos = new FileOutputStream(inputFile)) {
                workbook.write(fos);
            }
            workbook.close();

            
            for (int fila : filas) {
                Row row = sheet.getRow(fila);
                if (row != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int col : columnas) {
                        Cell cell = row.getCell(col);
                        String valor = (cell != null) ? obtenerValorCelda(cell) : "";
                        sb.append(valor).append("\t");

                        if ((fila == 6 && col == 3) || (fila == 7 && col == 3)) {
                            if (indiceEmpresa < empresa.length) {
                                empresa[indiceEmpresa++] = valor;
                            }
                        }
                        if ((fila == 2 && col == 3) || (fila == 2 && col == 6) || (fila == 17 && col == 1) || (fila == 17 && col == 6)) {
                            if (indiceFactura < factura.length) {
                                factura[indiceFactura++] = valor;
                            }
                        }
                        if ((fila == 8 && col == 3) || (fila == 9 && col == 3) || (fila == 9 && col == 5) || (fila == 9 && col == 7)) {
                            if (indiceDireccion < direccion.length) {
                                direccion[indiceDireccion++] = valor;
                            }
                        }
                        if ((fila == 7 && col == 5) || (fila == 10 && col == 3)) {
                            if (indiceContacto < contacto.length) {
                                contacto[indiceContacto++] = valor;
                            }
                        }
                    }
                    writer.write(sb.toString().trim() + "\n");
                    System.out.println(sb.toString().trim());
                }
            }
            //CODIGO BUCLE ARTICULOS --> RECORRES LAS FILAS POR LA MISMA COLUMNA DESDE EL NOMBRE (CONCEPTO) HASTA ENCONTRAR (FORMA DE PAGO)
            Row row = sheet.getRow(filaPosicion);
            Cell cell = row.getCell(columnaFormaPago);
            String FormaPago = (cell != null) ? obtenerValorCelda(cell) : "";
            String Total = "";
            while (!FormaPago.contains("Forma de pago")) {
                row = sheet.getRow(filaPosicion);
                if (row != null) {
                    cell = row.getCell(columnaFormaPago);
                    FormaPago = (cell != null) ? obtenerValorCelda(cell) : "";
                    if (!FormaPago.equals("CONCEPTO") && !FormaPago.equals("") && !FormaPago.contains("Forma de pago")) {
                        articulos.add(FormaPago);
                        cell = row.getCell(columnaTotal);
                        FormaPago = (cell != null) ? obtenerValorCelda(cell) : "";
                        articulos.add(FormaPago);
                    }
                }
                filaPosicion++;
                if (FormaPago.contains("Forma de pago")) {
                    break;
                }
            }
            factura[2] = FormaPago;
            System.out.println(factura[2]);
            System.out.println(filaPosicion);
            //OBTENER CUENTA BANCARIA, SABIENDO QUE LA CUENTA ESTA JUSTO ABAJO DE LA CELDA QUE PERTENECE A LA FORMA DE PAGO,
            // EN FORMA DE PAGO FILTRAREMOS ATRAVES DE UN MÉTODO EL CONCEPTO MÁS ALLA DE LOS :
            row = sheet.getRow(filaPosicion);
            cell = row.getCell(columnaFormaPago);
            String CuentaBancaria = (cell != null) ? obtenerValorCelda(cell) : "";
            String Cuenta = "";
            if (CuentaBancaria.matches(".*\\d.*")) { //.*\\d.* CONTIENE SOLO DIGITOS
                Cuenta = obtenerParteDespuesDeLosPuntos(CuentaBancaria);
            } else {
                row = sheet.getRow(filaPosicion);
                cell = row.getCell(columnaFormaPago + 1);
                CuentaBancaria = (cell != null) ? obtenerValorCelda(cell) : "";
                Cuenta = CuentaBancaria;
            }
            System.out.println("Cuenta: " + CuentaBancaria);
            filaPosicion--;
            Row row2 = sheet.getRow(filaPosicion);
            Cell cell2 = row2.getCell(columnaTotal);
            Total = (cell2 != null) ? obtenerValorCelda(cell2) : "";
            while (filaPosicion < sheet.getPhysicalNumberOfRows()) { // Asegura que filaPosicion no sea mayor que el número total de filas
                row2 = sheet.getRow(filaPosicion);
                if (row2 != null) {
                    cell2 = row2.getCell(columnaTotal);
                    Total = (cell2 != null) ? obtenerValorCelda(cell2) : "";

                    if (Total == null || Total.equals("")) {
                        filaPosicion++;  // Solo incrementa si la celda está vacía
                    } else {
                        break; // Si encontramos un valor válido, salimos del bucle
                    }
                } else {
                    filaPosicion++;  // Si la fila es null, incrementamos filaPosicion
                }
            }
            //CON ESTE IF RECORREMOS HASTA QUE ENCONTREMOS "TOTAL" PARTA LOCALIZAR ESA CELDA(COLUMNA)
            //Y SABER QUE EN LA SIGUIENTE OBTENDREMOS EL DATO TOTAL EN NÚMERO
            if (Total != null && Total.equals("TOTAL")) {
                row2 = sheet.getRow(filaPosicion);
                if (row2 != null) {
                    cell2 = row2.getCell(columnaTotal + 1);
                    Total = (cell2 != null) ? obtenerValorCelda(cell2) : "";
                }
            }

            factura[3] = Total;
            System.out.println(factura[3]);
            System.out.println(filaPosicion);

            ///////////////////////////////////////////

            System.out.println("Resultados guardados en salida.txt");

            imprimirDatos(empresa, "Empresa");
            imprimirDatos(factura, "Factura");
            imprimirDatos(direccion, "Direccion");
            imprimirDatos(contacto, "Contacto");
            Connection conexion = ConexionFacturas.obtenerConexion();

            if (conexion == null) {
                System.out.println("Error: No se pudo establecer conexión con la base de datos.");
                return;
            }
            //AQUÍ COMPROBAREMOS LAS CONDICIONES DE QUE SI ESTAN LOS CAMPOS LLENOS , PUEDAS INSERTAR EN LA BBDD
            //TRAS COMPROBAMOS LLAMAMOS A LOS METODOS PARA SU INSERCCIÓN
            if (noEstanVacios(empresa)) {
                insertarEmpresa(conexion, empresa);
                int IdCefora = obtenerIdCefora(conexion, empresa);
                int IdCliente = obtenerIdCliente(conexion, empresa);
                direccion[4] = IdCliente;

                if (noEstanVacios(factura)) {
                    insertarFactura(conexion, factura, IdCliente, IdCefora, FormaPago, Total);
                    IdFactura = obtenerIdFactura(conexion, factura, IdCliente);

                    if (noEstanVacios(direccion)) {
                        insertarDireccion(conexion, direccion);
                    }
                    if (!contacto[0].toString().equals("")) {
                        insertarContacto(conexion, contacto, IdCliente);
                    }
                    if (!Cuenta.equals("")) {
                        insertarCuentaBancaria(conexion, Cuenta, IdCefora);
                    }
                    for (int i = 0; i < articulos.size(); i++) {
                        if (i % 2 == 0 || i == 0) {
                            articulo[0] = articulos.get(i);
                        } else {
                            articulo[1] = articulos.get(i);
                            articulo[1] = limpiarTotal(articulo[1].toString());
                            if (noEstanVacios(articulo)) {
                                insertarArticulos(conexion, articulo, IdFactura);
                            }
                        }
                    }
                    String nombreArchivo = inputFile.getName();
                    if(!nombresArchivos(conexion, nombreArchivo, IdCliente)){
                        insertarArchivos(conexion, nombreArchivo, IdCliente);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error al procesar el archivo: " + e.getMessage());
        }
    }
    //FUNCIÓN PARA INSERTAR LOS ARTÍCULOS
    public static void insertarArticulos(Connection conexion, Object[] articulo, int IdFactura) {
        String queryInsert = "INSERT INTO Articulos (Producto, Importe, Factura_idFactura) VALUES (?, ?, ?)";
        try {
            PreparedStatement psInsert = conexion.prepareStatement(queryInsert);
            psInsert.setObject(1, articulo[0]);
            psInsert.setObject(2, articulo[1]);
            psInsert.setObject(3, IdFactura);
            psInsert.executeUpdate();
            System.out.println("Los Articulos se han insertado correctamente");
        } catch (SQLException e) {
            System.out.println("ERROR INSERT ARTICULOS: " + e.getMessage());
        }
    }

    //FUNCIÓN PARA INSERTAR LA EMPRESA PERO ANTES COMPROBAR QUE DICHA EMPRESA NO ESTÉ DUPLICADA COMPROBANDO NOMBRE Y CIF
    public static void insertarEmpresa(Connection conexion, Object[] empresa) {
        String queryCheck = "SELECT COUNT(*) FROM EMPRESA WHERE Nombre = ? AND CIF = ?";
        String queryInsert = "INSERT INTO EMPRESA(Nombre, CIF) VALUES (?, ?)";

        try (PreparedStatement psCheck = conexion.prepareStatement(queryCheck); PreparedStatement psInsert = conexion.prepareStatement(queryInsert)) {

            psCheck.setObject(1, empresa[0]);
            psCheck.setObject(2, empresa[1]);

            ResultSet rs = psCheck.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                psInsert.setObject(1, empresa[0]);
                psInsert.setObject(2, empresa[1]);
                psInsert.executeUpdate();
                System.out.println("OK INSERT: Empresa agregada");
            } else {
                System.out.println("Empresa ya existe en la base de datos, no se inserta.");
            }

        } catch (SQLException e) {
            System.out.println("ERROR EMPRESA: " + e.getMessage());
        }
    }
    //ESTE ID ES MUY IMPORTANTE PORQUE VA SER DETERMINANTE A LA HORA DE GENERAR FACTURAS FUTURAS... SOBRE TODO PARA SABER DE QUIEN ESTAS GENERANDO FUTUROS ENVIOS DE ELLAS
    public static int obtenerIdCefora(Connection conexion, Object[] empresa) {
        String query = "SELECT idEmpresa FROM Empresa WHERE Nombre = ? AND CIF = ?";
        int IdCefora = 0;
        try {
            PreparedStatement ps = conexion.prepareStatement(query);

            ps.setObject(1, "Cefora");
            ps.setObject(2, "B82620683");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                IdCefora = rs.getInt("idEmpresa");
            }

        } catch (SQLException e) {
            System.out.println("ERROR QUERY SELECT" + e);
        }
        return IdCefora;
    }
    //ESTA MÉTODO COMPRUEBA EL NUMERO DE CUENTA QUE NO SE REPITA
    //DESPUÉS INSERTA EN LA BASE DE DATOS Y EN EL EXCEL
    public static void insertarCuentaBancaria(Connection conexion, String Cuenta, int IdCefora) {
        String queryCheck = "SELECT COUNT(*) FROM EmpresaCuentas WHERE NumeroCuenta = ?";
        String queryInsert = "INSERT INTO EmpresaCuentas(NumeroCuenta, Empresa_idEmpresa) VALUES (?, ?)";
        try (PreparedStatement psCheck = conexion.prepareStatement(queryCheck); PreparedStatement psInsert = conexion.prepareStatement(queryInsert)) {
            psCheck.setObject(1, Cuenta);
            ResultSet rs = psCheck.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                psInsert.setObject(1, Cuenta);
                psInsert.setObject(2, IdCefora);
                psInsert.executeUpdate();
                System.out.println("OK INSERT: Cuenta agregada correctamente");
            } else {
                System.out.println("Cuenta ya existe en la base de datos, no se inserta.");
            }

        } catch (SQLException e) {
            System.out.println("ERROR QUERY" + e);
        }

    }
    //ESTE METODO ES IMPORTANTE PORQUE DICHAS FACTURAS SERÁN LA CLAVE DEL PROGRAMA, ES LA TABLA PRINCIPAL CON LA QUE NOS MOVEREMOS
    //AL FIN Y AL CABO LAS FACTURAS SON LO QUE LEEREMOS Y GENERAREMOS.
    //MISMO PROCESO VERIFICAR SU EXISTENCIA Y LUEGO INSERTAR
    public static void insertarFactura(Connection conexion, Object[] factura, int IdCliente, int IdEmpresa, String FormaPago, String Total) {
        String queryCheck = "SELECT COUNT(*) FROM Factura WHERE TRIM(NumeroFactura) = TRIM(?)";
        String queryInsert = "INSERT INTO Factura(NumeroFactura, FechaEmision, IdCliente,IdEmpresa, FormaPago, Total) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement psCheck = conexion.prepareStatement(queryCheck); PreparedStatement psInsert = conexion.prepareStatement(queryInsert)) {

            // Imprimir la consulta para depuración
            System.out.println("Verificando si la factura ya existe: " + factura[0]);

            psCheck.setObject(1, factura[0].toString().trim());
            ResultSet rs = psCheck.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {  // Si encuentra una fila, la factura ya existe
                System.out.println("Factura ya existe en la base de datos, no se inserta.");
                return;
            }

            // Procede con la inserción porque no se encontró la factura
            psInsert.setObject(1, factura[0].toString().trim());

            Date fechaConvertida = convertirFecha(factura[1]);
            if (fechaConvertida != null) {
                psInsert.setDate(2, fechaConvertida);
            } else {
                System.out.println("ERROR: Fecha de emisión inválida.");
                return;
            }
            psInsert.setObject(3, IdCliente);

            psInsert.setObject(4, IdEmpresa);
            String fp = obtenerParteDespuesDeLosPuntos(FormaPago);
            psInsert.setObject(5, fp);
            double total = limpiarTotal(Total);
            psInsert.setObject(6, total);
            psInsert.executeUpdate();
            System.out.println("OK INSERT: Factura agregada");

        } catch (SQLException e) {
            System.out.println("ERROR FACTURA: " + e.getMessage());
        }
    }
    //IMPORTANTE ANTES DE ACTUALIZAR, COMPROBAR QUE LOS CAMPOS SON CORRECTOS, LUEGO HACER LA ACTUALIZACIÓN
    public static void ActualizarFormaPago(Connection conexion, Object[] factura, String FormaPago, int IdFactura) {
        String queryCheck = "SELECT COUNT(*) FROM Factura WHERE TRIM(NumeroFactura) = TRIM(?)";
        String queryUpdate = "UPDATE Factura SET FormaPago = ? WHERE IdFactura = ?";
        try (PreparedStatement psCheck = conexion.prepareStatement(queryCheck); PreparedStatement psInsert = conexion.prepareStatement(queryUpdate)) {

            // Imprimir la consulta para depuración
            System.out.println("Verificando si la factura ya existe: " + factura[0]);

            psCheck.setObject(1, factura[0].toString().trim());
            ResultSet rs = psCheck.executeQuery();
            obtenerParteDespuesDeLosPuntos(FormaPago);
            psInsert.setObject(1, FormaPago);
            psInsert.setObject(2, IdFactura);
            psInsert.executeUpdate();

        } catch (SQLException e) {
            System.out.println("ERROR  de Actualizacion FORMA DE PAGO: " + e.getMessage());
        }

    }
    //CONSEGUIMOS EL ID FACTURAS PARA ACCEDER A LAS MISMAS 
    public static int obtenerIdFactura(Connection conexion, Object[] factura, int IdCliente) {
        String querySelect = "SELECT IdFactura FROM Factura WHERE IdCliente = ? AND NumeroFactura = ?";
        int IdFactura = 0;
        try {
            PreparedStatement psInsert = conexion.prepareStatement(querySelect);
            psInsert.setObject(1, IdCliente);
            psInsert.setObject(2, factura[0]);// NUMERO DE FACTURA
            ResultSet rs = psInsert.executeQuery();
            if (rs.next()) {
                IdFactura = rs.getInt("IdFactura");
            }
        } catch (SQLException e) {
            System.out.println();
        }
        return IdFactura;
    }
    //COMPROBACIÓN DE SI EXISTE Y POSTERIORMENTE INSERTAR LA DIRECCION
    public static void insertarDireccion(Connection conexion, Object[] direccion) {
        String queryCheck = "SELECT COUNT(*) FROM Direccion WHERE Direccion = ? AND CodigoPostal = ?";
        String queryInsert = "INSERT INTO Direccion (Direccion, CodigoPostal, Poblacion, Provincia, Empresa_idEmpresa) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement psCheck = conexion.prepareStatement(queryCheck); PreparedStatement psInsert = conexion.prepareStatement(queryInsert)) {

            psCheck.setObject(1, direccion[0]);
            psCheck.setObject(2, direccion[3]);

            ResultSet rs = psCheck.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                psInsert.setObject(1, direccion[0]);
                psInsert.setObject(2, direccion[3]);
                psInsert.setObject(3, direccion[1]);
                psInsert.setObject(4, direccion[2]);
                psInsert.setObject(5, direccion[4]);
                psInsert.executeUpdate();
                System.out.println("OK INSERT: Direccion agregada");
            } else {
                System.out.println("Direccion ya existe en la base de datos, no se inserta.");
            }

        } catch (SQLException e) {
            System.out.println("ERROR DIRECCION: " + e.getMessage());
        }
    }
    //COMPROBACIÓN EN SI CONTIENE EL TELEFONO Y BIEN PUESTO Y LA POSTERIOR INSERCCION EN LA BBDD
    public static void insertarContacto(Connection conexion, Object[] contacto, int IdCliente) {
        String queryCheck = "SELECT COUNT(*) FROM Contacto WHERE Telefono = ?";
        String queryInsert = "INSERT INTO Contacto (Telefono, Email, Empresa_idEmpresa) VALUES (?, ?, ?)";
        try (PreparedStatement psCheck = conexion.prepareStatement(queryCheck); PreparedStatement psInsert = conexion.prepareStatement(queryInsert)) {

            psCheck.setObject(1, contacto[0]);

            ResultSet rs = psCheck.executeQuery();
            contacto[2] = IdCliente;
            if (rs.next() && rs.getInt(1) == 0) {
                psInsert.setObject(1, contacto[0]);
                psInsert.setObject(2, contacto[1]);
                psInsert.setObject(3, contacto[2]);
                psInsert.executeUpdate();
                System.out.println("OK INSERT: Contacto agregada");
            } else {
                System.out.println("Contacto ya existe en la base de datos, no se inserta.");
            }

        } catch (SQLException e) {
            System.out.println("ERROR CONTACTO: " + e.getMessage());
        }
    }
    //LOS DATOS SERAN IMPRESOS DEPENDIENDO DEL TIPO
    public static void imprimirDatos(Object[] datos, String tipo) {
        System.out.println("\nDatos de " + tipo + ":");
        for (Object dato : datos) {
            System.out.println(dato);
        }
    }
    // LA FECHA LA TRANSFORMAREMOS A FORMATO ESPAÑOL
    public static Date convertirFecha(Object fechaObj) {
        if (fechaObj == null) {
            return null;
        }
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date date = formatoEntrada.parse(fechaObj.toString().trim());
            return new java.sql.Date(date.getTime());
        } catch (ParseException e) {
            System.out.println("Error al convertir fecha: " + fechaObj);
            return null;
        }
    }
    //PARA RECORRER LAS TABLAS SIEMPRE TENDREMOS EN FACTURAS LOS IDS, POR LO TANTO HAY UN MÉTODO PARA OBTENER LOS IDS
    public static int obtenerIdCliente(Connection conexion, Object[] empresa) {
        String query = "SELECT idEmpresa FROM Empresa WHERE Nombre = ? AND CIF = ?";
        int IdCliente = 0;
        try {
            PreparedStatement ps = conexion.prepareStatement(query);

            ps.setObject(1, empresa[0]);
            ps.setObject(2, empresa[1]);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                IdCliente = rs.getInt("idEmpresa");
            }

        } catch (SQLException e) {
            System.out.println("ERROR QUERY SELECT" + e);
        }
        return IdCliente;
    }
    //MÉTODO QUE NOS AYUDA A OBTENER LA FORMA DE PAGO
    public static String obtenerParteDespuesDeLosPuntos(Object valor) {
        if (valor == null) {
            return "";
        }
        String strValor = valor.toString();
        if (strValor.contains(":")) {
            String[] partes = strValor.split(":");
            return partes.length > 1 ? partes[1].trim() : strValor;
        }
        return strValor;
    }
    //LIMPIAR TOTAL CONTIENE EL NUMERO SIN $ O €
    public static Double limpiarTotal(Object totalObj) {
        if (totalObj == null) {
            return null;
        }

        String total = totalObj.toString().trim();

        // Validar si el total contiene caracteres numéricos
        if (!total.matches(".*\\d.*")) {
            System.out.println("Error: El total no contiene números válidos -> " + total);
            return null;
        }

        // Remover caracteres no numéricos excepto puntos y comas
        total = total.replaceAll("[^0-9,.]", "").replace(",", ".");

        try {
            return Double.parseDouble(total);
        } catch (NumberFormatException e) {
            System.out.println("Error al convertir total a número: " + total);
            return null;
        }
    }
    //DEPENDIENDO DEL VALOR DE LA CELDA PUEDE SER DE UN TIPO O OTRO, PUEDE HABER VALORES NULOS...
    public static String obtenerValorCelda(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // Verifica si es un número entero (por ejemplo, un código postal)
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    return sdf.format(cell.getDateCellValue());
                } else {
                    double numValue = cell.getNumericCellValue();
                    // Si es un número entero (sin decimales), lo convertimos a String sin decimales
                    if (numValue == Math.floor(numValue)) {
                        return String.valueOf((int) numValue);  // Convertir a entero
                    }
                    return String.valueOf(numValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    //ESTE MÉTODO COMPRUEBA QUE SE RECORREN LOS DATOS DE UN ARRAY Y NO ESTÁN VACIOS
    public static boolean noEstanVacios(Object[] lista) {
        for (int i = 0; i < lista.length; i++) {
            if (lista[i] == null || lista[i].toString().equals("")) {
                return false;
            }
        }
        return true;
    }
    // LOS ARCHIVOS ES IMPORTANTE PARA LAS PROXIMAS FUNCIONES, DAR VERDADERO IMPLICA QUE EL ARCHIVO ESTE DUPLICADO
    public static boolean nombresArchivos(Connection conexion, String nombreArchivo, int IdCliente) {
        String query = "SELECT * FROM Archivos";
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String nombreA = rs.getString("NombreArchivo");
                int idEmpresa = rs.getInt("Empresa_idEmpresa");
                if (nombreA.equals(nombreArchivo) && idEmpresa == IdCliente) {
                    System.out.println("El archivo esta duplicado");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR QUERY" + e);
        }
        return false;
    }
    //INSERTA ARCHIVOS EN LA BBDD
    public static void insertarArchivos(Connection conexion, String nombreArchivo, int IdCliente) {
        String query = "INSERT INTO Archivos (NombreArchivo, Empresa_idEmpresa) VALUES (? ,?)";
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setObject(1, nombreArchivo);
            ps.setObject(2, IdCliente);
            int resultado = ps.executeUpdate();
            if (resultado > 0) {
                System.out.println("LOS ARCHIVOS SE INSERTARON CORRECTAMENTE");
            }
        } catch (SQLException e) {
            System.out.println("ERROR QUERY" + e);
        }

    }
}
