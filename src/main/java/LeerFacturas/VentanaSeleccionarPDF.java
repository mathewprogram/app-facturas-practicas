package LeerFacturas;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class VentanaSeleccionarPDF extends javax.swing.JFrame {

    private DefaultTableModel modelMostrarArchivos;

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

    public VentanaSeleccionarPDF() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        personalizar_JFrame();
        initComponents();
        listarArchivosExcel();
        tblListado.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblListado.setRowSelectionAllowed(true);
        this.setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblListado = new javax.swing.JTable();
        btnConvertirEInsertar = new javax.swing.JButton();
        lblTitulo = new javax.swing.JLabel();
        btnAtras = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblListado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tblListado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ficheros disponibles"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblListado.setToolTipText("");
        jScrollPane1.setViewportView(tblListado);

        btnConvertirEInsertar.setText("Convertir");
        btnConvertirEInsertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConvertirEInsertarActionPerformed(evt);
            }
        });

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("Listado de archivos disponibles para su conversion e insercion a la BD");

        btnAtras.setText("<<<");
        btnAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtrasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
            .addComponent(lblTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(137, 137, 137)
                .addComponent(btnAtras)
                .addGap(18, 18, 18)
                .addComponent(btnConvertirEInsertar, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConvertirEInsertar)
                    .addComponent(btnAtras))
                .addGap(13, 13, 13))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void listarArchivosExcel() {
        String ruta = "C:\\Users\\Practicas\\Documents\\NetBeansProjects\\FacturasExcel\\src\\main\\java\\facturasGuardadas";
        File carpeta = new File(ruta);

        if (carpeta.exists() && carpeta.isDirectory()) {
            File[] archivos = carpeta.listFiles((dir, name) -> name.toLowerCase().endsWith(".xlsx"));

            if (archivos != null) {
                DefaultTableModel modelo = (DefaultTableModel) tblListado.getModel();
                modelo.setRowCount(0); // Limpiar la tabla antes de actualizar

                for (File archivo : archivos) {
                    modelo.addRow(new Object[]{archivo.getName()});
                }
            }
        } else {
            System.out.println("La carpeta especificada no existe o no es un directorio válido.");
        }
    }


    private void btnConvertirEInsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConvertirEInsertarActionPerformed
        // Obtener el índice de la fila seleccionada en la tabla
        int filaSeleccionada = tblListado.getSelectedRow();

        if (filaSeleccionada == -1) { // Si no se seleccionó ninguna fila
            System.out.println("Por favor, seleccione un archivo de la tabla.");
            return;
        }

        // Obtener el nombre del archivo seleccionado
        String nombreArchivo = (String) tblListado.getValueAt(filaSeleccionada, 0);

        // Construir la ruta completa del archivo seleccionado
        String carpeta = "C:/Users/Practicas/Documents/NetBeansProjects/FacturasExcel/src/main/java/facturasGuardadas";
        String rutaArchivoExcel = carpeta + "/" + nombreArchivo;

        // Verificar si el archivo realmente existe antes de continuar
        File archivoSeleccionado = new File(rutaArchivoExcel);
        if (!archivoSeleccionado.exists()) {
            System.out.println("El archivo seleccionado no existe.");
            return;
        }

        // Buscar el archivo .txt (asumo que hay un TXT relacionado en la misma carpeta)
        String rutaDestinoTXT = LeerFacturaPDF.buscarArchivoPorExtension(carpeta, ".txt");

        // Verificar si se encontró el archivo TXT necesario
        if (rutaDestinoTXT == null) {
            System.out.println("No se encontró ningún archivo TXT.");
            return;
        }

        // Convertir el archivo Excel a PDF
        try {
            List<File> archivosPDF = PDFConverter.convertirExcelAPDF(rutaArchivoExcel);
            if (archivosPDF.isEmpty()) {
                System.out.println("No se pudo convertir el archivo Excel a PDF.");
                return;
            }

            // Tomar el primer archivo PDF generado
            File archivoPDF = archivosPDF.get(0);
            System.out.println("Archivo PDF generado: " + archivoPDF.getAbsolutePath());

            // Leer el PDF y extraer los datos
            String contenidoPDF = LeerFacturaPDF.leerPDF(archivoPDF.getAbsolutePath());
            if (contenidoPDF == null) {
                System.out.println("No se pudo leer el archivo PDF.");
                return;
            }

            // Extraer los datos de la factura y guardarlos en el archivo .txt
            LeerFacturaPDF.extraerDatosFacturaYGuardar(contenidoPDF, rutaDestinoTXT);

        } catch (IOException e) {
            System.out.println("Error al convertir el archivo Excel a PDF: " + e.getMessage());
        }
    }//GEN-LAST:event_btnConvertirEInsertarActionPerformed

    private void btnAtrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtrasActionPerformed
        //VUELVE A LA VENTANA DE SELECCION(SI QUIERES MODIFICAR FACTURA, INSERTAR FACTURA O ENVIAR FACTURA) DE GESTÓN
        VentanaSelecionarGestor ventana = new VentanaSelecionarGestor();
        ventana.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnAtrasActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaSeleccionarPDF.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaSeleccionarPDF.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaSeleccionarPDF.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaSeleccionarPDF.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaSeleccionarPDF().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtras;
    private javax.swing.JButton btnConvertirEInsertar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTable tblListado;
    // End of variables declaration//GEN-END:variables
}
