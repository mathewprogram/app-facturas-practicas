package LeerFacturas;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane; // Importa esta clase para usar JOptionPane
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class VentanaBusquedaFactura extends javax.swing.JFrame {
    // GUARDAREMOS LOS IDs EN UN ARRAYLIST Para depués Recorrerlos
    private ArrayList<Integer> idFactura_a = new ArrayList<>();
    private ArrayList<Integer> idEmpresa_a = new ArrayList<>();
    private ArrayList<Integer> idCliente_a = new ArrayList<>();

    public void personalizar_JFrame() {
        //PERSONALIZAMOS LA VENTANA CON EL nombre, tipo de letra...Y Imagen (LOGOTIPO)
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setFont(new Font("Courier New", Font.PLAIN, 12));
        this.setTitle("Ventana Busqueda Facturas");
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        // Cargar la imagen correctamente
        try {
            Image imagen = Toolkit.getDefaultToolkit().getImage("C:/Users/Practicas/Documents/NetBeansProjects/FacturasExcel/src/main/java/LeerFacturas/Logo.png");
            imagen = imagen.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
            this.setIconImage(imagen);

        } catch (Exception e) {
            System.out.println("Error: Imagen no encontrada");
        }
    }

    public VentanaBusquedaFactura() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //LA VENTANA CONSTA DE UN BUSCADOR QUE FILTRA DESDE EL PRIMER COMBO BOX, LUEGO ESCRIBES EN LA CAJA POR 
        //nombre(empresa o cliente), fecha de emisión, número de factura -> 1º combobox y luego buscas enm el txt FACTURA QUIERES BUSCAR
        initComponents();
        personalizar_JFrame();
        iniciarTabla();
        TblFacturas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && TblFacturas.getSelectedRow() != -1) {
                    BtnFactura.setEnabled(true);
                }
            }
        });
        TxtBuscador.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscarElementosTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscarElementosTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CboBuscar1 = new javax.swing.JComboBox<>();
        BtnFactura = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        TblFacturas = new javax.swing.JTable();
        BtnCerrar = new javax.swing.JButton();
        TxtBuscador = new javax.swing.JTextField();
        LblBuscar = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        BtnEliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Buscador de Facturas");

        CboBuscar1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Numero de Factura", "Fecha de Emision", "Empresa", "Cliente" }));
        CboBuscar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CboBuscar1ActionPerformed(evt);
            }
        });

        BtnFactura.setText("BUSCAR FACTURA");
        BtnFactura.setEnabled(false);
        BtnFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnFacturaActionPerformed(evt);
            }
        });

        TblFacturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Empresa", "Cliente", "Numero de Factura", "Fecha de Emisión", "Forma de Pago", "Total €"
            }
        ));
        jScrollPane2.setViewportView(TblFacturas);

        BtnCerrar.setText("CERRAR SESIÓN");
        BtnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCerrarActionPerformed(evt);
            }
        });

        TxtBuscador.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        TxtBuscador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtBuscadorActionPerformed(evt);
            }
        });

        LblBuscar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LblBuscar.setText("BUSCAR DESDE EL 1º FILTRO");

        btnBack.setText("<<<");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        BtnEliminar.setText("Eliminar Cliente");
        BtnEliminar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(LblBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TxtBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(80, 80, 80)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(CboBuscar1, 0, 155, Short.MAX_VALUE)
                            .addComponent(BtnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(80, 80, 80)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BtnFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BtnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2))
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TxtBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LblBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BtnFactura)
                            .addComponent(CboBuscar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BtnCerrar)
                            .addComponent(BtnEliminar)))
                    .addComponent(btnBack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CboBuscar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CboBuscar1ActionPerformed
        /*      Connection conexion = ConexionFacturas.obtenerConexion();

// Verificamos si la conexión es válida
        if (conexion == null) {
            JOptionPane.showMessageDialog(null, "Error: La conexión a la base de datos falló.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

// Obtén el texto seleccionado en el JComboBox y elimine espacios adicionales
        String selectedOption = CboBuscar1.getSelectedItem().toString().trim();

// Imprime el valor seleccionado para depuración
        System.out.println("Valor seleccionado: '" + selectedOption + "'");

// Compara la opción seleccionada (sin importar mayúsculas/minúsculas)
        if ("Numero de Factura".equalsIgnoreCase(selectedOption)) {
            String query = "SELECT NumeroFactura FROM Factura";
            try (PreparedStatement ps = conexion.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

                // Limpiar el JComboBox antes de llenarlo con nuevos datos
                CboDatos.removeAllItems();

                // Agregar los resultados al JComboBox
                while (rs.next()) {
                    CboDatos.addItem(rs.getString("NumeroFactura"));
                }

                // Si no hay resultados, agregar un mensaje
                if (CboDatos.getItemCount() == 0) {
                    CboDatos.addItem("No hay facturas disponibles");
                }
            } catch (SQLException e) {
                // Mostrar un mensaje de error si ocurre una excepción SQL
                JOptionPane.showMessageDialog(null, "Error al ejecutar la consulta SQL: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else if ("Fecha de Emision".equalsIgnoreCase(selectedOption)) {
            String query = "SELECT FechaEmision FROM Factura";
            try (PreparedStatement ps = conexion.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

                // Limpiar el JComboBox antes de llenarlo con nuevos datos
                CboDatos.removeAllItems();

                // Agregar los resultados al JComboBox
                while (rs.next()) {
                    ;
                    CboDatos.addItem(convertirFechaAString(rs.getString("FechaEmision")));
                }

                // Si no hay resultados, agregar un mensaje
                if (CboDatos.getItemCount() == 0) {
                    CboDatos.addItem("No hay fechas disponibles");
                }
            } catch (SQLException e) {
                // Mostrar un mensaje de error si ocurre una excepción SQL
                JOptionPane.showMessageDialog(null, "Error al ejecutar la consulta SQL: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else if ("Empresa".equalsIgnoreCase(selectedOption) || "Cliente".equalsIgnoreCase(selectedOption)) {
            String query = "SELECT Nombre FROM Empresa";
            try (PreparedStatement ps = conexion.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

                // Limpiar el JComboBox antes de llenarlo con nuevos datos
                CboDatos.removeAllItems();

                // Agregar los resultados al JComboBox
                while (rs.next()) {
                    CboDatos.addItem(rs.getString("Nombre"));
                }

                // Si no hay resultados, agregar un mensaje
                if (CboDatos.getItemCount() == 0) {
                    CboDatos.addItem("No hay Nombres de Empresa disponibles");
                }
            } catch (SQLException e) {
                // Mostrar un mensaje de error si ocurre una excepción SQL
                JOptionPane.showMessageDialog(null, "Error al ejecutar la consulta SQL: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
         */
    }//GEN-LAST:event_CboBuscar1ActionPerformed

    private void BtnFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnFacturaActionPerformed
        //BOTÓN EN EL QUE EJECUTAS LA FILA DE LA TABLA SELECCIONADA PARA LLEVARTE A LA VENTANA EXCEL DONDE PODREMOS MODIFICAR DICHA FACTURA
        //ESTE BOTÓN SOLO SE INICICIALIZA CON LA SELECCIÓN DE UNA FILA EN LA TABLA, SI NO NO SE ACTIVA
        //COMO VERÁS AHORA SE USAN DICHOS ARRAYs PARTA ENCONTRAR A TRAVES DE LA SELECCIÓN LA FACTURA
        int selectedRow = TblFacturas.getSelectedRow();
        if (selectedRow != -1) {
            if ((TblFacturas.getValueAt(selectedRow, 0) != null) || (TblFacturas.getValueAt(selectedRow, 1) != null)) {
                int IdFactura = idFactura_a.get(selectedRow);
                int IdEmpresa = idEmpresa_a.get(selectedRow);
                int IdCliente = idCliente_a.get(selectedRow);
                VentanaExcel ventana = new VentanaExcel(IdFactura, IdEmpresa, IdCliente);
                ventana.setVisible(true);
                this.dispose();
            }
        }

    }//GEN-LAST:event_BtnFacturaActionPerformed

    private void BtnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCerrarActionPerformed
       //ESTE DEVUELVE A LA VENTANA DE LOGIN
        VentanaLogin ventana = new VentanaLogin();
        ventana.setVisible(true);
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_BtnCerrarActionPerformed

    private void TxtBuscadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtBuscadorActionPerformed
        
    }//GEN-LAST:event_TxtBuscadorActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        VentanaSelecionarGestor ventana = new VentanaSelecionarGestor();
        ventana.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed

    private void BtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEliminarActionPerformed
            int filaSeleccionada = TblFacturas.getSelectedRow(); // Obtener la fila seleccionada

       if (filaSeleccionada != -1) { // Verificar si hay una fila seleccionada
           try {
               // Obtener el nombre del cliente desde la tabla (ajusta la columna si es necesario)
               String nombreCliente = (String) TblFacturas.getValueAt(filaSeleccionada, 1);

               // Obtener el ID del cliente usando el nombre
               int idCliente = obtenerIdClientePorNombre(nombreCliente);

               if (idCliente == -1) {
                   JOptionPane.showMessageDialog(this, "No se encontró el cliente en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                   return;
               }

               // Confirmar eliminación
               int confirmacion = JOptionPane.showConfirmDialog(this, 
                   "¿Está seguro de que desea eliminar al cliente " + nombreCliente + " y todos los datos relacionados?", 
                   "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

               if (confirmacion == JOptionPane.YES_OPTION) {
                   // Llamar al método eliminarCliente para eliminar las facturas y luego el cliente
                   String mensaje = eliminarCliente(idCliente);
                   JOptionPane.showMessageDialog(this, mensaje);
               }

           } catch (Exception e) {
               JOptionPane.showMessageDialog(this, "Error al obtener el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
           }
       } else {
           JOptionPane.showMessageDialog(this, "Seleccione una fila.", "Advertencia", JOptionPane.WARNING_MESSAGE);
       }
    }//GEN-LAST:event_BtnEliminarActionPerformed

    //Metodo para eliminar un cliente
    private int obtenerIdClientePorNombre(String nombreCliente) {
        Connection connection = ConexionFacturas.obtenerConexion();
        String query = "SELECT idEmpresa FROM empresa WHERE LOWER(TRIM(nombre)) = LOWER(TRIM(?))";  // Comparar ignorando mayúsculas/minúsculas y espacios en blanco

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, nombreCliente.trim());  // Elimina espacios en blanco antes de la consulta
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("idEmpresa");  // Devolvemos el 'id' del cliente encontrado
            }
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }

        return -1; // Retornar -1 si no se encontró el cliente
    }

    // Método para eliminar al cliente y todo lo relacionado con él
    private String eliminarCliente(int idCliente) {
        Connection connection = ConexionFacturas.obtenerConexion();

        try {
            // Comienza una transacción
            connection.setAutoCommit(false);

            // Eliminar todas las facturas relacionadas con el cliente
            String queryFacturas = "DELETE FROM factura WHERE IdEmpresa = ?";
            try (PreparedStatement psFacturas = connection.prepareStatement(queryFacturas)) {
                psFacturas.setInt(1, idCliente);
                psFacturas.executeUpdate();
            }

            // Eliminar el cliente de la tabla empresa
            String queryCliente = "DELETE FROM empresa WHERE idEmpresa = ?";
            try (PreparedStatement psCliente = connection.prepareStatement(queryCliente)) {
                psCliente.setInt(1, idCliente);
                int filasAfectadas = psCliente.executeUpdate();

                // Si se eliminaron filas en empresa
                if (filasAfectadas > 0) {
                    // Hacer commit de la transacción
                    connection.commit();
                    return "Cliente y todos los datos relacionados eliminados correctamente.";
                } else {
                    // Si no se pudo eliminar el cliente
                    return "No se pudo eliminar el cliente.";
                }
            }
        } catch (SQLException e) {
            // En caso de error, hacer rollback de la transacción
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al hacer rollback: " + ex.getMessage());
            }
            return "Error al eliminar los datos relacionados: " + e.getMessage();
        } finally {
            try {
                connection.setAutoCommit(true); // Restaurar la configuración de auto-commit
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }




        
    //ESTE MÉTODO RECUPERA EL NOMBRE DE LA EMPRESA QUE LO CONSEGUIMOS A TRAVÉS DEL ID, LO NECESITAMOS PARA RELLENAR LA TABLA
    private String nombreEmpresa(int IdEmpresa) {
        // Conexión a la base de datos
        Connection conexion = ConexionFacturas.obtenerConexion();
        String queryEmpresa = "SELECT Nombre FROM Empresa WHERE idEmpresa = ?";
        String nombreEmpresa = "";
        try {
            PreparedStatement psE = conexion.prepareStatement(queryEmpresa);
            psE.setInt(1, IdEmpresa);
            ResultSet rsE = psE.executeQuery();

            if (rsE.next()) {
                nombreEmpresa = rsE.getString("Nombre");

            }
        } catch (SQLException e) {

        }
        return nombreEmpresa;
    }
    //ESTE MÉTODO RECUPERA EL NOMBRE DEL CLIENTE QUE LO CONSEGUIMOS A TRAVÉS DEL ID, LO NECESITAMOS PARA RELLENAR LA TABLA
    private String nombreCliente(int IdCliente) {
        // Conexión a la base de datos
        Connection conexion = ConexionFacturas.obtenerConexion();
        String queryEmpresa = "SELECT Nombre FROM Empresa WHERE idEmpresa = ?";
        String nombreCliente = "";
        try {
            PreparedStatement psE = conexion.prepareStatement(queryEmpresa);
            psE.setInt(1, IdCliente);
            ResultSet rsC = psE.executeQuery();

            if (rsC.next()) {
                nombreCliente = rsC.getString("Nombre");

            }
        } catch (SQLException e) {

        }
        return nombreCliente;
    }
    //MÉTODO PARA CONVERTIR LA FECHA DE LA BASE DATOS QUE VIENE EN FORMATO AMERÍCANO. LO PASAMOS AL ESPAÑOL
    public static String convertirFechaAString(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy");

            java.util.Date date = formatoEntrada.parse(fecha.trim());
            return formatoSalida.format(date);
        } catch (ParseException e) {
            System.out.println("Error al convertir fecha: " + fecha);
            return null;
        }
    }
    //ESTE MÉTODO ES NUESTRO BUSCADOR; 1º SELECCIONAS A TRAVÉS DEL COMBOBOX POR QUÉ QUIERES BUSCAR; LUEGO A TRAVES DE LA CAJA txt BUSCAS, SOLO CON UNA LETRA O NÚMERO FILTRAS
    public void buscarElementosTabla() {
        Connection conexion = ConexionFacturas.obtenerConexion();
        String buscador = TxtBuscador.getText();
        String selectedOption = CboBuscar1.getSelectedItem().toString().trim();
        ArrayList<String> buscar_a = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) TblFacturas.getModel();
        model.setRowCount(0);  // Limpiar JTable        
        //BUSCAS POR NUMERO DE FACTURA
        if (selectedOption.equals("Numero de Factura")) {
            String query = "SELECT NumeroFactura FROM Factura";
            try {
                PreparedStatement ps = conexion.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    buscar_a.add(rs.getString("NumeroFactura"));
                    idFactura_a.clear();
                    idEmpresa_a.clear();
                    idCliente_a.clear();
                }
                for (int i = 0; i < buscar_a.size(); i++) {
                    if (buscar_a.get(i).startsWith(buscador)) {
                        String query1 = "SELECT * FROM Factura WHERE NumeroFactura = ?";
                        try (PreparedStatement ps1 = conexion.prepareStatement(query1)) {
                            // Obtener el número de factura seleccionado
                            String numeroFacturaSeleccionado = buscar_a.get(i);
                            System.out.println("Buscando factura con número: " + numeroFacturaSeleccionado);

                            // Establecer el valor del parámetro en la consulta
                            ps1.setString(1, numeroFacturaSeleccionado);

                            // Ejecutar la consulta
                            ResultSet rs1 = ps1.executeQuery();

                            // Verificar si la consulta devuelve resultados
                            if (rs1.next()) {
                                do {
                                    idFactura_a.add(rs1.getInt("IdFactura"));
                                    idEmpresa_a.add(rs1.getInt("IdEmpresa"));
                                    idCliente_a.add(rs1.getInt("IdCliente"));
                                    String nombreCliente = nombreCliente(rs1.getInt("IdCliente"));
                                    String nombreEmpresa = nombreEmpresa(rs1.getInt("IdEmpresa"));
                                    Object[] row = new Object[6];
                                    row[0] = nombreEmpresa;
                                    row[1] = nombreCliente;
                                    row[2] = rs1.getString("NumeroFactura");
                                    String fechaTraducida = convertirFechaAString(rs1.getString("FechaEmision"));
                                    row[3] = fechaTraducida;
                                    row[4] = rs1.getString("FormaPago");
                                    row[5] = rs1.getString("Total");
                                    model.addRow(row);

                                } while (rs.next());
                            } else {

                            }
                            rs1.close();

                        } catch (SQLException e) {
                            System.out.println("ERROR QUERY" + e);
                        }
                        rs.close();

                    }

                }
            } catch (SQLException e) {
                System.out.println("ERROR QUERY" + e);
            }
        //BUSCAR POR FECHA DE EMISIÓN
        } else if (selectedOption.equals("Fecha de Emision")) {
            String query = "SELECT FechaEmision FROM Factura";
            try {
                PreparedStatement ps = conexion.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    buscar_a.add(rs.getString("FechaEmision"));
                    idFactura_a.clear();
                    idEmpresa_a.clear();
                    idCliente_a.clear();
                }
                for (int i = 0; i < buscar_a.size(); i++) {
                    String fechaTraducida = convertirFechaAString(buscar_a.get(i));
                    if (fechaTraducida.startsWith(buscador)) {
                        String query1 = "SELECT * FROM Factura WHERE FechaEmision = ?";
                        try (PreparedStatement ps1 = conexion.prepareStatement(query1)) {
                            // Obtener el número de factura seleccionado
                            String fechaEmisionSeleccionado = buscar_a.get(i);
                            System.out.println("Buscando factura con número: " + fechaEmisionSeleccionado);

                            // Establecer el valor del parámetro en la consulta
                            ps1.setString(1, fechaEmisionSeleccionado);

                            // Ejecutar la consulta
                            ResultSet rs1 = ps1.executeQuery();
                            // Verificar si la consulta devuelve resultados
                            if (rs1.next()) {
                                do {
                                    idFactura_a.add(rs1.getInt("IdFactura"));
                                    idEmpresa_a.add(rs1.getInt("IdEmpresa"));
                                    idCliente_a.add(rs1.getInt("IdCliente"));
                                    String nombreCliente = nombreCliente(rs1.getInt("IdCliente"));
                                    String nombreEmpresa = nombreEmpresa(rs1.getInt("IdEmpresa"));
                                    Object[] row = new Object[6];
                                    row[0] = nombreEmpresa;
                                    row[1] = nombreCliente;
                                    row[2] = rs1.getString("NumeroFactura");
                                    row[3] = fechaTraducida;
                                    row[4] = rs1.getString("FormaPago");
                                    row[5] = rs1.getString("Total");
                                    model.addRow(row);

                                } while (rs.next());
                            } else {

                            }
                            rs1.close();
                        } catch (SQLException e) {
                            System.out.println("ERROR QUERY" + e);
                        }

                    }

                    // Cerrar ResultSet
                    rs.close();
                }
            } catch (SQLException e) {
                System.out.println("ERROR QUERY" + e);
            }
        //BUSQUEDA POR EMPRESA
        } else if (selectedOption.equals("Empresa")) {
            // Consulta para "Nombre de Empresa"
            String query = "SELECT Nombre FROM Empresa";
            try {
                PreparedStatement ps = conexion.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    buscar_a.add(rs.getString("Nombre"));
                    idFactura_a.clear();
                    idEmpresa_a.clear();
                    idCliente_a.clear();
                }
                rs.close();
                for (int i = 0; i < buscar_a.size(); i++) {
                    if (buscar_a.get(i).startsWith(buscador)) {
                        String query1 = "SELECT idEmpresa FROM Empresa WHERE Nombre = ?";
                        try (PreparedStatement ps1 = conexion.prepareStatement(query1)) {
                            // Obtener el nombre de la empresa seleccionada
                            String nombreEmpresa = buscar_a.get(i);
                            System.out.println("Buscando empresa: " + nombreEmpresa);  // Depuración

                            // Establecer el valor del parámetro en la consulta
                            ps1.setString(1, nombreEmpresa);

                            // Ejecutar la consulta para obtener el ID de la empresa
                            ResultSet rs1 = ps1.executeQuery();

                            // Si encontramos la empresa, buscamos las facturas asociadas
                            if (rs1.next()) {
                                int idEmpresa = rs1.getInt("idEmpresa");

                                // Ahora realizamos una consulta para obtener las facturas de esa empresa
                                String facturaQuery = "SELECT * FROM Factura WHERE IdEmpresa = ?";
                                try (PreparedStatement facturaPs = conexion.prepareStatement(facturaQuery)) {
                                    facturaPs.setInt(1, idEmpresa);
                                    ResultSet facturaRs = facturaPs.executeQuery();
                                    // Si hay resultados de facturas, los mostramos en la JTable
                                    while (facturaRs.next()) {
                                        idFactura_a.add(facturaRs.getInt("IdFactura"));
                                        idEmpresa_a.add(facturaRs.getInt("IdEmpresa"));
                                        idCliente_a.add(facturaRs.getInt("IdCliente"));
                                        String nombreCliente1 = nombreCliente(facturaRs.getInt("IdCliente"));
                                        String nombreEmpresa1 = nombreEmpresa(facturaRs.getInt("IdEmpresa"));
                                        Object[] row = new Object[6];  // Cambia el tamaño del array según las columnas de tu tabla
                                        row[0] = nombreEmpresa1;
                                        row[1] = nombreCliente1;
                                        row[2] = facturaRs.getString("NumeroFactura");
                                        String fechaTraducida = convertirFechaAString(facturaRs.getString("FechaEmision"));
                                        row[3] = fechaTraducida;
                                        row[4] = facturaRs.getString("FormaPago");
                                        row[5] = facturaRs.getString("Total");

                                        model.addRow(row);  // Añadir fila a la tabla
                                    }

                                    // Si no se encontraron facturas, mostrar un mensaje
                                    if (model.getRowCount() == 0) {

                                    }

                                    facturaRs.close();
                                }
                                rs1.close();
                            } else {

                            }

                        } catch (SQLException e) {
                            System.out.println("ERROR QUERY" + e);

                        }
                    }
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al ejecutar la consulta SQL: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        //BÚSQUEDA POR CLIENTE
        } else if (selectedOption.equals("Cliente")) {
            // Consulta para "Nombre de Empresa"
            String query = "SELECT Nombre FROM Empresa";
            try {
                PreparedStatement ps = conexion.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    buscar_a.add(rs.getString("Nombre"));
                    idFactura_a.clear();
                    idEmpresa_a.clear();
                    idCliente_a.clear();
                }
                rs.close();
                for (int i = 0; i < buscar_a.size(); i++) {
                    if (buscar_a.get(i).startsWith(buscador)) {
                        String query1 = "SELECT idEmpresa FROM Empresa WHERE Nombre = ?";
                        try (PreparedStatement ps1 = conexion.prepareStatement(query1)) {
                            // Obtener el nombre de la empresa seleccionada
                            String nombreCliente = buscar_a.get(i);

                            // Establecer el valor del parámetro en la consulta
                            ps1.setString(1, nombreCliente);

                            // Ejecutar la consulta para obtener el ID de la empresa
                            ResultSet rs1 = ps1.executeQuery();

                            // Si encontramos la empresa, buscamos las facturas asociadas
                            if (rs1.next()) {
                                int IdCliente = rs1.getInt("idEmpresa");

                                // Ahora realizamos una consulta para obtener las facturas de esa empresa
                                String facturaQuery = "SELECT * FROM Factura WHERE IdCliente = ?";
                                try (PreparedStatement facturaPs = conexion.prepareStatement(facturaQuery)) {
                                    facturaPs.setInt(1, IdCliente);
                                    ResultSet facturaRs = facturaPs.executeQuery();
                                    idFactura_a.clear();
                                    idEmpresa_a.clear();
                                    idCliente_a.clear();
                                    // Si hay resultados de facturas, los mostramos en la JTable
                                    while (facturaRs.next()) {
                                        idFactura_a.add(facturaRs.getInt("IdFactura"));
                                        idEmpresa_a.add(facturaRs.getInt("IdEmpresa"));
                                        idCliente_a.add(facturaRs.getInt("IdCliente"));
                                        String nombreCliente1 = nombreCliente(facturaRs.getInt("IdCliente"));
                                        String nombreEmpresa1 = nombreEmpresa(facturaRs.getInt("IdEmpresa"));
                                        Object[] row = new Object[6];  // Cambia el tamaño del array según las columnas de tu tabla
                                        row[0] = nombreEmpresa1;
                                        row[1] = nombreCliente1;
                                        row[2] = facturaRs.getString("NumeroFactura");
                                        String fechaTraducida = convertirFechaAString(facturaRs.getString("FechaEmision"));
                                        row[3] = fechaTraducida;
                                        row[4] = facturaRs.getString("FormaPago");
                                        row[5] = facturaRs.getString("Total");

                                        model.addRow(row);  // Añadir fila a la tabla
                                    }

                                    // Si no se encontraron facturas, mostrar un mensaje
                                    if (model.getRowCount() == 0) {

                                    }

                                    facturaRs.close();

                                }
                                rs1.close();
                            } else {

                            }

                        } catch (SQLException e) {
                            System.out.println("ERROR QUERY" + e);

                        }
                    }
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al ejecutar la consulta SQL: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    // INCIAS LA TABLA EN 0; Y LA RELLENAS CON UN SELECT, COMO COMPROBARÁS EL NOMBRE DEL CLIENTE o el de la EMPRESA, LO CONSEGUIMOS A TRAVÉS DE LOS IDs
    private void iniciarTabla() {
        Connection conexion = ConexionFacturas.obtenerConexion();
        String query = "SELECT * FROM Factura";
        try {
            DefaultTableModel model = (DefaultTableModel) TblFacturas.getModel();
            PreparedStatement ps = conexion.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            idFactura_a.clear();
            idEmpresa_a.clear();
            idCliente_a.clear();
            model.setRowCount(0);
            while (rs.next()) {
                
                idEmpresa_a.add(rs.getInt("IdEmpresa"));
                idFactura_a.add(rs.getInt("IdFactura"));
                idCliente_a.add(rs.getInt("IdCliente"));
                String nombreCliente1 = nombreCliente(rs.getInt("IdCliente"));
                String nombreEmpresa1 = nombreEmpresa(rs.getInt("IdEmpresa"));
                Object[] row = new Object[6];  // Cambia el tamaño del array según las columnas de tu tabla
                row[0] = nombreEmpresa1;
                row[1] = nombreCliente1;
                row[2] = rs.getString("NumeroFactura");
                String fechaTraducida = convertirFechaAString(rs.getString("FechaEmision"));
                row[3] = fechaTraducida;
                row[4] = rs.getString("FormaPago");
                row[5] = rs.getString("Total");
                model.addRow(row);
            }
        rs.close();
        } catch (SQLException e) {
            System.out.println("ERROR QUERY" + e);
        }
        
    }

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaBusquedaFactura().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnCerrar;
    private javax.swing.JButton BtnEliminar;
    private javax.swing.JButton BtnFactura;
    private javax.swing.JComboBox<String> CboBuscar1;
    private javax.swing.JLabel LblBuscar;
    private javax.swing.JTable TblFacturas;
    private javax.swing.JTextField TxtBuscador;
    private javax.swing.JButton btnBack;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}




