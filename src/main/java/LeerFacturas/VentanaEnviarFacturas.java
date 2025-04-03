package LeerFacturas;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import javax.swing.UIManager;






public class VentanaEnviarFacturas extends javax.swing.JFrame {
    //TODO 1ºRELLENAR LOS ELEMENTOS CON EL USO DEL CLIENTE DEBE OBTENER (EL CORREO Y SUS FACTURA COMO ARCHIVOS EXCEL(.XLSX))
    //     2ºPODER BUSCAR EN LA CAJA TXTCLIENTE EL NOMBRE DEL MISMO Y SU CORREO, EN CASO DE NO TENER CORREO PODREMOS ENVIARLO A TRAVÉS DE SU CAJA TXTCORREO
    //     3ºUNA VEZ ENVIADO EL CORREO PODRÁN ASOCIAR ESTE CORREO A LA BASE DE DATOS COMO UN CLIENTE CON ESE MISMO CORREO (TE LO PREGUNTARÁ)

    //declaramos el model de la tabla globalmente
    private DefaultTableModel modelArchivos;
    private DefaultTableModel modelCorreo;


    public void personalizar_JFrame() {
        //PERSONALIZAMOS LA VENTANA CON EL nombre, tipo de letra...Y Imagen (LOGOTIPO)
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setFont(new Font("Courier New", Font.PLAIN, 12));
        this.setTitle("Enviar Facturas");
        this.setSize(730, 740); //si primero establecemos el tamaño de la ventana
        this.setLocationRelativeTo(null); //esta funcion hace que la ventana aparezca centrada en la pantalla
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

    public VentanaEnviarFacturas() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        personalizar_JFrame();
        initComponents();
        tblArchivos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblArchivos.setRowSelectionAllowed(true);
        
        seleccionarCorreo();
        selectCorreo();
        seleccionarArchivoDesdeTabla();
        //Esto nos asegura buscar por elemento 
        txtCliente.getDocument().addDocumentListener(new DocumentListener() {
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
    
    //CADA VEZ QUE BUSQUES EN TXTCLIENTE ENCUENTRA ARCHIVOS Y CORREOS
    public void buscarElementosTabla() {
        Connection conexion = ConexionFacturas.obtenerConexion(); // Conexión con la BBDD
        String nombreCliente = txtCliente.getText().toLowerCase(); // Convertimos a minúsculas para comparar
        String correo = txtCorreo.getText();
        modelArchivos = (DefaultTableModel) tblArchivos.getModel(); // Modelo de la tabla de archivos
        modelCorreo = (DefaultTableModel) tblCorreo.getModel(); // Modelo de la tabla de correos
        modelArchivos.setRowCount(0); // Limpiar tabla
        modelCorreo.setRowCount(0); // Limpiar tabla

        if (conexion != null) {
            try {
                String query = "SELECT Nombre FROM Empresa";
                PreparedStatement ps = conexion.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String cliente = rs.getString("Nombre");

                    // Comparamos convirtiendo a minúsculas
                    if (cliente.toLowerCase().startsWith(nombreCliente)) {
                        String queryCliente = "SELECT idEmpresa FROM Empresa WHERE LOWER(Nombre) = LOWER(?)";
                        int idCliente = obtenerIdCliente(queryCliente, cliente);
                        String queryArchivo = "SELECT NombreArchivo FROM Archivos WHERE Empresa_idEmpresa = ?";
                        String queryCorreo = "SELECT Email FROM Contacto WHERE Empresa_idEmpresa = ?";
                        rellenarArchivos(queryArchivo, idCliente, cliente);
                        rellenarCorreo(queryCorreo, idCliente, cliente);
                    }
                }
            } catch (SQLException e) {
                System.out.println("ERROR QUERY SELECT " + e);
            }
        }
    }

    
    //LLENA LA TABLA DE LOS ARCHIVOS GUARDADOS EN LA BBDD
    public void rellenarArchivos(String query, int idEmpresa, String cliente) {
        Connection conexion = ConexionFacturas.obtenerConexion();//CONEXION CON LA BBDD (BD_FACTURAS)
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setObject(1, idEmpresa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    cliente,
                    rs.getString("NombreArchivo")
                };
                modelArchivos.addRow(row);
            }
        } catch (SQLException e) {
            System.out.println("ERROR QUERY ARCHIVOS" + e);
        }

    }
    //OBTIENES LOS DATOS DE LOS CORREOS, INCLUYE TODOS
    public void rellenarCorreo(String query, int idEmpresa, String correo) {
        Connection conexion = ConexionFacturas.obtenerConexion();//CONEXION CON LA BBDD (BD_FACTURAS)
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setObject(1, idEmpresa);
            ResultSet rs = ps.executeQuery();
            //comprobamos con una variable que los correos vacios salgan en blanco
            String correoVacio = "vacio";
            while (rs.next()) {
                Object[] row = {
                    correo,
                    rs.getString("Email")
                };
                modelCorreo.addRow(row);
                correoVacio = "lleno";

            }
            if (correoVacio.equals("vacio")) {
                Object[] row = {
                    correo,
                    "",};
                modelCorreo.addRow(row);
            }
        } catch (SQLException e) {
            System.out.println("ERROR QUERY CORREO" + e);
        }

    }
    //OBTIENES IDcliente a traves de la query y el nombre
    public int obtenerIdCliente(String query, String nombre) {
        Connection conexion = ConexionFacturas.obtenerConexion();//CONEXION CON LA BBDD (BD_FACTURAS)
        int idCliente = 0;
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setObject(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idCliente = rs.getInt("idEmpresa");
            }
        } catch (SQLException e) {
            System.out.println("ERROR QUERY" + e);
        }
        return idCliente;
    }
    //OBTENER EL CORREO AUTOMATICAMENTE Y QUE PINTE EL CLIENTE Y EL CORREO EN SUS DEBIDOS TXT
    private void seleccionarCorreo() {
        ListSelectionModel model = tblCorreo.getSelectionModel();
        model.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tblCorreo.getSelectedRow();
                if (filaSeleccionada != -1 && tblCorreo.getValueAt(filaSeleccionada, 0) != null && tblCorreo.getValueAt(filaSeleccionada, 1) != null) {
                    txtCorreo.setText(tblCorreo.getValueAt(filaSeleccionada, 1).toString());
                    txtCliente.setText(tblCorreo.getValueAt(filaSeleccionada, 0).toString());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblCorreo = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblArchivos = new javax.swing.JTable();
        lblSeleccionarCorreo = new javax.swing.JLabel();
        lblCliente = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        txtCorreo = new javax.swing.JTextField();
        btnAtras = new javax.swing.JButton();
        btnEnviar = new javax.swing.JButton();
        lblSeleccionarCorreo1 = new javax.swing.JLabel();
        txtArchivos = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        tblCorreo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Cliente", "Correo"
            }
        ));
        jScrollPane1.setViewportView(tblCorreo);

        tblArchivos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Cliente", "Archivos"
            }
        ));
        tblArchivos.setToolTipText("");
        jScrollPane2.setViewportView(tblArchivos);

        lblSeleccionarCorreo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSeleccionarCorreo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSeleccionarCorreo.setText("Correo para enviar");

        lblCliente.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCliente.setText("CLIENTE");

        txtCliente.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        txtCliente.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtCorreo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        btnAtras.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAtras.setText("<<<");
        btnAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtrasActionPerformed(evt);
            }
        });

        btnEnviar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEnviar.setText("ENVIAR FACTURA >>");
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        lblSeleccionarCorreo1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSeleccionarCorreo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSeleccionarCorreo1.setText("Archivo");

        txtArchivos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(btnAtras, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(54, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblSeleccionarCorreo1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtArchivos, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblSeleccionarCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(69, 69, 69))
            .addGroup(layout.createSequentialGroup()
                .addGap(180, 180, 180)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSeleccionarCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtArchivos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSeleccionarCorreo1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAtras, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    private void selectCorreo() {
        tblCorreo.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tblCorreo.getSelectedRow();
                if (filaSeleccionada != -1) {
                    String correoSeleccionado = tblCorreo.getValueAt(filaSeleccionada, 1).toString();
                    txtCorreo.setText(correoSeleccionado);
                }
            }
        });
    }
    
    private void seleccionarArchivoDesdeTabla() {
        ListSelectionModel model = tblArchivos.getSelectionModel();
        model.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int[] filasSeleccionadas = tblArchivos.getSelectedRows();
                if (filasSeleccionadas.length > 0) {
                    StringBuilder rutasArchivos = new StringBuilder();
                    String directorioBase = "C:/Users/Practicas/Documents/NetBeansProjects/FacturasExcel/src/main/java/facturasGuardadas/";

                    for (int fila : filasSeleccionadas) {
                        String nombreArchivo = tblArchivos.getValueAt(fila, 1).toString();
                        File archivo = new File(directorioBase + nombreArchivo);
                        if (archivo.exists()) {
                            rutasArchivos.append(archivo.getAbsolutePath()).append(";");
                        } else {
                            JOptionPane.showMessageDialog(this, "El archivo " + nombreArchivo + " no existe en la ruta especificada.");
                        }
                    }

                    // Eliminar el último punto y coma
                    if (rutasArchivos.length() > 0) {
                        rutasArchivos.setLength(rutasArchivos.length() - 1);
                    }

                    txtArchivos.setText(rutasArchivos.toString());
                }
            }
        });
    }

    private String obtenerNombreClienteDesdeTabla() {
        int filaSeleccionada = tblArchivos.getSelectedRow();
        int columnaNombreCliente = 0; // Ajusta si la columna cambia
        String nombreCliente = "Cliente Desconocido"; // Valor por defecto

        // Si hay una fila seleccionada, intentamos obtener el nombre
        if (filaSeleccionada != -1) {
            Object valorCelda = tblArchivos.getValueAt(filaSeleccionada, columnaNombreCliente);

            if (valorCelda != null && !valorCelda.toString().trim().isEmpty()) {
                nombreCliente = valorCelda.toString().trim();
            }
        } else {
            // Si no hay fila seleccionada, buscamos en la tabla automáticamente
            for (int i = 0; i < tblArchivos.getRowCount(); i++) {
                Object valorCelda = tblArchivos.getValueAt(i, columnaNombreCliente);

                if (valorCelda != null && !valorCelda.toString().trim().isEmpty()) {
                    nombreCliente = valorCelda.toString().trim();
                    break; // Tomamos el primer nombre válido que encontremos
                }
            }
        }

        return nombreCliente;
    }


    
        
    private void btnAtrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtrasActionPerformed
        
        VentanaSelecionarGestor ventana = new VentanaSelecionarGestor();
        ventana.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnAtrasActionPerformed

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
            String destinatario = txtCorreo.getText();
        String archivosAdjuntos = txtArchivos.getText();

        if (destinatario.isEmpty() || archivosAdjuntos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un correo y al menos un archivo.");
            return;
        }

        String[] rutasArchivos = archivosAdjuntos.split(";");

        try {
            enviarCorreo(destinatario, rutasArchivos);
            JOptionPane.showMessageDialog(this, "Correo enviado con éxito.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al enviar el correo: " + e.getMessage());
        }
    }//GEN-LAST:event_btnEnviarActionPerformed
    
    
        /**
        * Método para enviar un correo electrónico con archivos adjuntos mediante Gmail.
        *
        * @param destinatario Dirección de correo electrónico del destinatario.
        * @param archivosAdjuntos Array de rutas de los archivos a adjuntar en el correo.
        * @throws MessagingException Si ocurre un error en el proceso de envío del correo.
        */
        private void enviarCorreo(String destinatario, String[] archivosAdjuntos) throws MessagingException, IOException {
        // Convertir cada archivo Excel a PDF usando la clase PDFConverter
        List<File> archivosPDF = PDFConverter.convertirExcelAPDF(String.join(";", archivosAdjuntos));

        // Credenciales del remitente
        String de = "progamacionesctructurada@gmail.com";  
        String password = "vqln qydw rzog sgyz";  

        // Configuración del servidor SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");  
        props.put("mail.smtp.port", "587");  
        props.put("mail.smtp.auth", "true");  
        props.put("mail.smtp.starttls.enable", "true");  

        // Crear la sesión de correo con autenticación
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(de, password);
            }
        });

        // Crear el mensaje de correo
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(de));  
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));

        // Definir asunto del correo
        boolean esPlural = archivosPDF.size() > 1;
        message.setSubject(esPlural ? "Facturas de MIM." : "Factura de MIM.");

        // Cuerpo del mensaje
        MimeBodyPart mensajeCuerpo = new MimeBodyPart();
        String cuerpoMensaje = String.format(
            "Estimado/a cliente %s,\n\n" +
            "Adjuntamos su%s factura%s correspondiente%s.\n\n" +
            "Por favor, no dude en ponerse en contacto con nosotros si tiene alguna pregunta o necesita información adicional.\n\n" +
            "Atentamente,\n" +
            "Mguel Mateo\n" +
            "MIM Formación\n" +
            "Teléfono: 666931817\n" +
            "Correo: facturasestructuradas@gmail.com\n",
            obtenerNombreClienteDesdeTabla(), 
            esPlural ? "s" : "", esPlural ? "s" : "", esPlural ? "s" : ""
        );
        mensajeCuerpo.setText(cuerpoMensaje);

        // Crear contenedor de mensaje
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mensajeCuerpo);

        // Adjuntar todos los PDFs convertidos
        for (File pdf : archivosPDF) {
            MimeBodyPart adjunto = new MimeBodyPart();
            adjunto.attachFile(pdf);
            multipart.addBodyPart(adjunto);
        }

        // Asignar contenido y enviar
        message.setContent(multipart);
        Transport.send(message);
        System.out.println("Correo enviado correctamente.");
    }


    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaEnviarFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaEnviarFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaEnviarFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaEnviarFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaEnviarFacturas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtras;
    private javax.swing.JButton btnEnviar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblSeleccionarCorreo;
    private javax.swing.JLabel lblSeleccionarCorreo1;
    private javax.swing.JTable tblArchivos;
    private javax.swing.JTable tblCorreo;
    private javax.swing.JTextField txtArchivos;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtCorreo;
    // End of variables declaration//GEN-END:variables

}
