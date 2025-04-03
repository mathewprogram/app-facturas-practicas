package LeerFacturas;

import static LeerFacturas.Facturas.obtenerValorCelda;
import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Practicas
 */
public class VentanaExcel extends javax.swing.JFrame {

    private final int idFactura;
    private final int idEmpresa;
    private final int idCliente;
    private final int telefonoOriginal;
    private final String direccionOriginal;
    private final String cuentaOriginal;
    private final ArrayList<String> articuloOriginal = new ArrayList<>();
    private DefaultTableModel model;

    //PERSONALIZAR VENTANA
    public void personalizar_JFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setFont(new Font("Courier New", Font.PLAIN, 12));
        this.setTitle("Modifica y Crea la Factura de nuevo");
        this.setSize(710, 720);
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
    //VENTANA QUE SIRVE PARA MODIFICAR Y ACTUALIZAR LAS FACTURAS Y LUEGO PODER GENERAR LAS FACTURAS EN FORMATO EXCEL

    public VentanaExcel(int IdFactura, int IdEmpresa, int IdCliente) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        personalizar_JFrame();
        idFactura = IdFactura;
        idEmpresa = IdEmpresa;
        idCliente = IdCliente;
        initComponents();
        conseguirDatos(IdFactura, IdEmpresa, IdCliente);
        String telefonoStr = TxtTelefono.getText().trim(); // Eliminamos espacios en blanco

        // Centrar texto en todas las celdas
        DefaultTableCellRenderer centrarRenderer = new DefaultTableCellRenderer();
        centrarRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

        // Aplicar el renderer a todas las columnas
        for (int i = 0; i < TblConceptoImporte.getColumnCount(); i++) {
            TblConceptoImporte.getColumnModel().getColumn(i).setCellRenderer(centrarRenderer);
        }

        if (!telefonoStr.isEmpty()) {
            telefonoOriginal = Integer.parseInt(telefonoStr);
        } else {
            telefonoOriginal = 0; // O algún otro valor por defecto si el teléfono no es obligatorio
        }
        direccionOriginal = TxtDireccion.getText();
        cuentaOriginal = TxtNumeroCuenta.getText();
        for (int i = 0; i < model.getRowCount(); i++) {
            articuloOriginal.add(model.getValueAt(i, 0).toString());
        }

        seleccionarArticulo();
    }

    //OBTENER EL IDs ARTÍCULOS 
    public static int obtenerIdArticulos(int idFactura, String articulo) {
        int idArticulo = 0;
        Connection conexion = ConexionFacturas.obtenerConexion();

        String query = "SELECT idArticulos FROM Articulos WHERE Factura_IdFactura = ? AND Producto = ?";

        try {
            PreparedStatement ps = conexion.prepareStatement(query);

            ps.setObject(1, idFactura);
            ps.setObject(2, articulo);

            ResultSet rs = ps.executeQuery();

        } catch (SQLException e) {
            System.out.println("ERROR QUERY SELECT" + e);
        }
        return idArticulo;
    }

    //BORRA LOS ARTÍCULOS SEGÚN SU ID
    private void borrarArticulos() {
        Connection conexion = ConexionFacturas.obtenerConexion();
        String queryBorrarArticulo = "DELETE FROM Articulos WHERE Factura_idFactura = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(queryBorrarArticulo);
            ps.setObject(1, idCliente);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "ARTICULO ELIMINADO correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "NO SE PUEDO ELIMINAR ARTICULO", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            System.out.println("ERROR QUERY DELETE" + e);
        }

    }

    //MÉTODO PARA ELIMINAR DESCUENTOSARTICULOS YA QUE ESA TABLA DE LA BASE DE DATOS HA DESAPARECIDO, LO TENGO POR SI ACASO
    private void borrarArticulosDescuentoArticulos(int idArticulo) {
        Connection conexion = ConexionFacturas.obtenerConexion();
        String queryBorrarArticulo = "DELETE FROM DescuentosArticulos WHERE Articulos_idARTICULOS = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(queryBorrarArticulo);
            ps.setObject(1, idArticulo);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "ARTICULO ELIMINADO correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            System.out.println("ERROR QUERY DELETE" + e);
        }

    }

    //MÉTODO PARA ELIMINAR IMPUESTOSARTICULOS YA QUE ESA TABLA DE LA BASE DE DATOS HA DESAPARECIDO, LO TENGO POR SI ACASO
    private void borrarArticulosImpuestosArticulos(int idArticulo) {
        Connection conexion = ConexionFacturas.obtenerConexion();
        String queryBorrarArticulo = "DELETE FROM ImpuestosArticulos WHERE Articulos_idARTICULOS = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(queryBorrarArticulo);
            ps.setObject(1, idArticulo);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "ARTICULO ELIMINADO correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            System.out.println("ERROR QUERY DELETE" + e);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LblNumeroFactura = new javax.swing.JLabel();
        TxtNumeroFactura = new javax.swing.JTextField();
        LblFechaEmision = new javax.swing.JLabel();
        TxtFechaEmision = new javax.swing.JTextField();
        LblCliente = new javax.swing.JLabel();
        LblNombre = new javax.swing.JLabel();
        LblCif = new javax.swing.JLabel();
        TxtCif = new javax.swing.JTextField();
        LblTelefono = new javax.swing.JLabel();
        TxtTelefono = new javax.swing.JTextField();
        LblDireccion = new javax.swing.JLabel();
        TxtDireccion = new javax.swing.JTextField();
        LblPoblacion = new javax.swing.JLabel();
        TxtPoblacion = new javax.swing.JTextField();
        LblProvincia = new javax.swing.JLabel();
        TxtNombre = new javax.swing.JTextField();
        TxtProvincia = new javax.swing.JTextField();
        LblCodigoPostal = new javax.swing.JLabel();
        TxtCodigoPostal = new javax.swing.JTextField();
        LblEmail = new javax.swing.JLabel();
        LblFormaPago = new javax.swing.JLabel();
        TxtFormaPago = new javax.swing.JTextField();
        LblCuenta = new javax.swing.JLabel();
        TxtNumeroCuenta = new javax.swing.JTextField();
        LblTotal = new javax.swing.JLabel();
        TxtTotal = new javax.swing.JTextField();
        BtnArticulos = new javax.swing.JButton();
        CmdModificarFactura = new javax.swing.JButton();
        TxtEmail = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        TblConceptoImporte = new javax.swing.JTable();
        TxtConcepto = new javax.swing.JTextField();
        TxtImporte = new javax.swing.JTextField();
        lblImporte = new javax.swing.JLabel();
        lblConcepto = new javax.swing.JLabel();
        BtnBorrarArticulos = new javax.swing.JButton();
        BtnModificarArticulos = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        BtnVolver = new javax.swing.JButton();
        CmdCrearFactura = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        LblNumeroFactura.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblNumeroFactura.setText("Nº DE FACTURA");
        LblNumeroFactura.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        LblNumeroFactura.setPreferredSize(null);

        TxtNumeroFactura.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TxtNumeroFactura.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TxtNumeroFactura.setEnabled(false);
        TxtNumeroFactura.setPreferredSize(null);

        LblFechaEmision.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblFechaEmision.setText("FECHA DE EMISIÓN");
        LblFechaEmision.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        LblFechaEmision.setPreferredSize(null);

        TxtFechaEmision.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TxtFechaEmision.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TxtFechaEmision.setEnabled(false);
        TxtFechaEmision.setPreferredSize(null);
        TxtFechaEmision.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtFechaEmisionActionPerformed(evt);
            }
        });

        LblCliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LblCliente.setText("CLIENTE");
        LblCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        LblCliente.setPreferredSize(null);

        LblNombre.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblNombre.setText("NOMBRE *");
        LblNombre.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        LblCif.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblCif.setText("CIF *");
        LblCif.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        TxtCif.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TxtCif.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TxtCif.setPreferredSize(null);

        LblTelefono.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblTelefono.setText("TELÉFONO *");
        LblTelefono.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        TxtTelefono.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TxtTelefono.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TxtTelefono.setPreferredSize(null);

        LblDireccion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblDireccion.setText("DIRECCIÓN *");
        LblDireccion.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        TxtDireccion.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TxtDireccion.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TxtDireccion.setPreferredSize(null);

        LblPoblacion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblPoblacion.setText("POBLACIÓN *");
        LblPoblacion.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        TxtPoblacion.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TxtPoblacion.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TxtPoblacion.setPreferredSize(null);
        TxtPoblacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtPoblacionActionPerformed(evt);
            }
        });

        LblProvincia.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblProvincia.setText("PROVINCIA *");
        LblProvincia.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        TxtNombre.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TxtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtNombreActionPerformed(evt);
            }
        });

        TxtProvincia.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        LblCodigoPostal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblCodigoPostal.setText("CODIGO POSTAL *");

        TxtCodigoPostal.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TxtCodigoPostal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtCodigoPostalActionPerformed(evt);
            }
        });

        LblEmail.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblEmail.setText("EMAIL");

        LblFormaPago.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblFormaPago.setText("FORMA DE PAGO *");

        TxtFormaPago.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        LblCuenta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblCuenta.setText("Nº DE CUENTA * ");

        TxtNumeroCuenta.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        LblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        LblTotal.setText("TOTAL");

        TxtTotal.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TxtTotal.setEnabled(false);

        BtnArticulos.setText("AÑADIR ARTÍCULO ");
        BtnArticulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnArticulosActionPerformed(evt);
            }
        });

        CmdModificarFactura.setText("MODIFICAR FACTURA");
        CmdModificarFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmdModificarFacturaActionPerformed(evt);
            }
        });

        TxtEmail.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        TblConceptoImporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "CONCEPTO", "IMPORTE"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(TblConceptoImporte);

        TxtConcepto.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        TxtImporte.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        lblImporte.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImporte.setText("IMPORTE  NUEVO");

        lblConcepto.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblConcepto.setText("CONCEPTO NUEVO");

        BtnBorrarArticulos.setText("BORRAR ARTÍCULO ");
        BtnBorrarArticulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBorrarArticulosActionPerformed(evt);
            }
        });

        BtnModificarArticulos.setText("MODIFICAR ARTÍCULO ");
        BtnModificarArticulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnModificarArticulosActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SE MARCAN CON * LOS CAMPOS OBLIGATORIOS");

        BtnVolver.setText("<<<");
        BtnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnVolverActionPerformed(evt);
            }
        });

        CmdCrearFactura.setText("CREAR FACTURA");
        CmdCrearFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmdCrearFacturaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 77, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BtnModificarArticulos)
                        .addGap(18, 18, 18)
                        .addComponent(CmdModificarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BtnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BtnArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BtnBorrarArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(CmdCrearFactura)
                .addGap(47, 47, 47))
            .addGroup(layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LblCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LblFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TxtNumeroCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(TxtConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TxtFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblImporte)
                            .addComponent(LblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(TxtImporte, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                            .addComponent(TxtTotal))))
                .addContainerGap(56, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(65, 65, 65)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(LblDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(LblCif, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(LblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(LblPoblacion))
                                    .addComponent(LblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(TxtEmail, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                                    .addComponent(TxtCif, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(TxtPoblacion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(TxtDireccion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(TxtNombre))
                                .addGap(58, 58, 58)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(LblTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(LblFechaEmision, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(LblCodigoPostal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(LblNumeroFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(LblProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TxtProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(TxtTelefono, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                        .addComponent(TxtCodigoPostal, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(TxtFechaEmision, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(TxtNumeroFactura, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(219, 219, 219)
                                .addComponent(LblCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LblCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LblNombre)
                    .addComponent(TxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LblNumeroFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TxtNumeroFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LblFechaEmision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TxtFechaEmision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TxtCodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LblCodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LblTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TxtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TxtCif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LblProvincia)
                            .addComponent(TxtProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TxtDireccion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LblDireccion))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TxtPoblacion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LblPoblacion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LblCif, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TxtImporte, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblConcepto)
                        .addComponent(TxtConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblImporte)))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LblFormaPago)
                    .addComponent(TxtFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TxtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LblCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TxtNumeroCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnBorrarArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnModificarArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CmdModificarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CmdCrearFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TxtFechaEmisionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtFechaEmisionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtFechaEmisionActionPerformed

    private void TxtPoblacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtPoblacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtPoblacionActionPerformed

    private void TxtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtNombreActionPerformed

    private void TxtCodigoPostalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtCodigoPostalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtCodigoPostalActionPerformed
    //A TRAVÉS DE LOS IDs CONSEGUIREMOS TODOS LOS DATOS, SABIENDO QUE CON DIFERENTES SELECT SE CONSIGUEN
    private void conseguirDatos(int IdFactura, int IdEmpresa, int IdCliente) {
        Connection conexion = ConexionFacturas.obtenerConexion();
        String queryFactura = "SELECT NumeroFactura,FechaEmision,Total,FormaPago FROM Factura WHERE IdFactura = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(queryFactura);
            ps.setObject(1, IdFactura);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TxtNumeroFactura.setText(rs.getString("NumeroFactura"));
                String fecha = VentanaBusquedaFactura.convertirFechaAString(rs.getString("FechaEmision"));
                TxtFechaEmision.setText(fecha);
                TxtTotal.setText(rs.getString("Total"));
                TxtFormaPago.setText(rs.getString("FormaPago"));
            }
            String queryEmpresa = "SELECT Nombre,CIF FROM Empresa WHERE IdEmpresa = ?";
            PreparedStatement ps2 = conexion.prepareStatement(queryEmpresa);
            ps2.setObject(1, IdCliente);
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                TxtNombre.setText(rs2.getString("Nombre"));
                TxtCif.setText(rs2.getString("CIF"));
            }
            String queryContactos = "SELECT Telefono,Email FROM Contacto WHERE Empresa_idEmpresa = ?";
            PreparedStatement ps7 = conexion.prepareStatement(queryContactos);
            ps7.setObject(1, IdEmpresa);
            ResultSet rs7 = ps7.executeQuery();
            while (rs7.next()) {
                TxtTelefono.setText(rs7.getString("Telefono"));
                TxtEmail.setText(rs7.getString("Email"));
            }

            String queryContacto = "SELECT Telefono,Email FROM Contacto WHERE Empresa_idEmpresa = ?";
            PreparedStatement ps3 = conexion.prepareStatement(queryContacto);
            ps3.setObject(1, IdCliente);
            ResultSet rs3 = ps3.executeQuery();
            while (rs3.next()) {
                TxtTelefono.setText(rs3.getString("Telefono"));
                TxtEmail.setText(rs3.getString("Email"));
            }
            // Realiza la consulta para obtener los resultados
            String queryArticulos = "SELECT Producto, Importe FROM Articulos WHERE Factura_IdFactura = ?";
            PreparedStatement ps4 = conexion.prepareStatement(queryArticulos);
            ps4.setObject(1, IdFactura);
            ResultSet rs4 = ps4.executeQuery();

            // Establecer el modelo de la tabla y las columnas (Si no se ha hecho antes)
            model = (DefaultTableModel) TblConceptoImporte.getModel();
            if (model == null) {
                // Si el modelo es nulo, inicialízalo
                model = new DefaultTableModel();
                model.addColumn("Producto");
                model.addColumn("Importe");
                TblConceptoImporte.setModel(model);
            }

            // Verificar si el ResultSet tiene resultados
            if (!rs4.isBeforeFirst()) {
                System.out.println("No se encontraron resultados.");
            } else {
                // Limpiar la tabla antes de agregar nuevos datos
                model.setRowCount(0);

                // Recorrer los resultados y agregar filas a la tabla
                while (rs4.next()) {
                    // Obtener los datos de la consulta
                    String producto = rs4.getString("Producto");
                    Double importe = rs4.getDouble("Importe");

                    // Verificar si los valores son nulos o tienen algún valor inesperado
                    if (producto == null) {
                        producto = "Desconocido";  // Valor predeterminado si 'Producto' es nulo
                    }
                    if (importe == null) {
                        importe = 0.0;  // Valor predeterminado si 'Importe' es nulo
                    }

                    // Agregar una nueva fila a la tabla
                    model.addRow(new Object[]{producto, importe});
                }
            }

            String queryDireccion = "SELECT Direccion, CodigoPostal, Provincia, Poblacion FROM Direccion WHERE Empresa_idEmpresa = ?";
            PreparedStatement ps5 = conexion.prepareStatement(queryDireccion);
            ps5.setObject(1, IdCliente);
            ResultSet rs5 = ps5.executeQuery();
            while (rs5.next()) {
                String direccion = rs5.getString("Direccion");
                String codigoPostal = rs5.getString("CodigoPostal");
                String provincia = rs5.getString("Provincia");
                String poblacion = rs5.getString("Poblacion");

                TxtDireccion.setText(direccion);
                TxtCodigoPostal.setText(codigoPostal);
                TxtProvincia.setText(provincia);
                TxtPoblacion.setText(poblacion);
            }

            String queryDireccion2 = "SELECT Direccion, CodigoPostal, Provincia, Poblacion FROM Direccion WHERE Empresa_idEmpresa = ?";
            PreparedStatement ps8 = conexion.prepareStatement(queryDireccion2);
            ps8.setObject(1, IdEmpresa);
            ResultSet rs8 = ps8.executeQuery();
            while (rs8.next()) {
                String direccion = rs8.getString("Direccion");
                String codigoPostal = rs8.getString("CodigoPostal");
                String provincia = rs8.getString("Provincia");
                String poblacion = rs8.getString("Poblacion");

                TxtDireccion.setText(direccion);
                TxtCodigoPostal.setText(codigoPostal);
                TxtProvincia.setText(provincia);
                TxtPoblacion.setText(poblacion);
            }

            String queryCuenta = "SELECT NumeroCuenta FROM EmpresaCuentas WHERE Empresa_idEmpresa = ?";
            PreparedStatement ps6 = conexion.prepareStatement(queryCuenta);
            ps6.setObject(1, IdEmpresa);
            ResultSet rs6 = ps6.executeQuery();
            while (rs6.next()) {
                TxtNumeroCuenta.setText(rs6.getString("NumeroCuenta"));
            }
        } catch (SQLException e) {
            System.out.println("ERROR QUERY FACTURA" + e);
        }
    }

    //ACTUALIZA EL TOTAL SEGÚN EL IMPORTE DE LOS ARTÍCULOS
    private void actualizarTotal() {
        double total = 0.0;
        model = (DefaultTableModel) TblConceptoImporte.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            Object articuloImporte = model.getValueAt(i, 1);
            if (articuloImporte instanceof Number) {
                Number number = (Number) articuloImporte;
                total += number.doubleValue();
            }
        }
        DecimalFormat formato = new DecimalFormat("#.00");
        String totalFormateado = formato.format(total);
        TxtTotal.setText(totalFormateado);
    }

    //SELECCIONA DE LA TABLA Y MUESTRA EN CAJAS TXT EL CONCEPTO Y EL IMPORTE 
    private void seleccionarArticulo() {
        ListSelectionModel model = TblConceptoImporte.getSelectionModel();
        model.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = TblConceptoImporte.getSelectedRow();
                if (filaSeleccionada != -1) {
                    TxtConcepto.setText(TblConceptoImporte.getValueAt(filaSeleccionada, 0).toString());
                    TxtImporte.setText(TblConceptoImporte.getValueAt(filaSeleccionada, 1).toString());
                }
            }
        });
    }


    private void BtnArticulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnArticulosActionPerformed
        //ESTE BOTÓN COMPRIUEBA QUE EL IMPORTE Y EL CONSCEPTO SEA CORRECTO; SI ESTO ES CIERTO SE ACTUALIZA LA TABLA 
        String concepto = TxtConcepto.getText();
        String importe = TxtImporte.getText();
        if (Validaciones.validarImporte(importe) == true) {
            DefaultTableModel model = (DefaultTableModel) TblConceptoImporte.getModel();
            Double importeDouble = Double.valueOf(importe);
            Object[] fila = {concepto, importeDouble};
            model.addRow(fila);
            actualizarTotal();
            JOptionPane.showMessageDialog(this, "Articulo INSERTADO en la tabla", "OK", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error al insertar Articulo en la tabla", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_BtnArticulosActionPerformed

    private void BtnBorrarArticulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBorrarArticulosActionPerformed
        //BORRA LOS ARTÍCULOS DE LA TABLA
        model = (DefaultTableModel) TblConceptoImporte.getModel();
        int selectedIndex = TblConceptoImporte.getSelectedRow();

        // Verificar si hay más de una fila en la tabla
        if (model.getRowCount() > 1) {
            if (selectedIndex != -1) {  // Si se ha seleccionado una fila válida
                model.removeRow(selectedIndex);  // Eliminar la fila seleccionada
                JOptionPane.showMessageDialog(this, "Artículo borrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                TxtConcepto.setText("");
                TxtImporte.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al borrar artículo. No se ha seleccionado ningún artículo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No puedes borrar la última fila de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        actualizarTotal();
    }//GEN-LAST:event_BtnBorrarArticulosActionPerformed

    private void BtnModificarArticulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnModificarArticulosActionPerformed
        // Primero, obtener el índice de la fila seleccionada
        int selectedIndex = TblConceptoImporte.getSelectedRow();

        if (selectedIndex != -1) {  // Verificar si hay una fila seleccionada
            // Obtener los valores desde los campos de texto
            String conceptoModificado = TxtConcepto.getText();  // Nuevo valor para el concepto
            String importeTexto = TxtImporte.getText();  // Obtener el valor del importe como texto

            // Verificar que el importe sea un número válido
            try {
                Double importeModificado = Double.valueOf(importeTexto);  // Convertir el texto a Double

                // Actualizar los valores en la tabla
                DefaultTableModel model = (DefaultTableModel) TblConceptoImporte.getModel();
                System.out.println(selectedIndex);
                model.setValueAt(conceptoModificado, selectedIndex, 0);  // Modificar la columna 0 (Concepto)
                model.setValueAt(importeModificado, selectedIndex, 1);  // Modificar la columna 1 (Importe)

                // Mostrar mensaje de éxito
                JOptionPane.showMessageDialog(this, "Artículo modificado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarTotal();
            } catch (NumberFormatException e) {
                // Si el importe no es un número válido
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un importe válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Si no se seleccionó ninguna fila
            JOptionPane.showMessageDialog(this, "No se ha seleccionado un artículo para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_BtnModificarArticulosActionPerformed

    private void BtnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnVolverActionPerformed
        //VOLVER A LA VENTANA DE BUSQUEDA FACTURA PARA PODER MODIFICAR OTRA FACTURA
        VentanaBusquedaFactura ventana = new VentanaBusquedaFactura();
        ventana.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_BtnVolverActionPerformed

    private void CmdModificarFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmdModificarFacturaActionPerformed
        //MODIFICA ACTUALIZANDO LOS DATOS EN LA BASE DE DATOS, ESTOS DATOS SE COMPRUEBAN Y SE VALIDAN QUE SON CORRECTOS PREVIAMENTE
        String nombreCliente = TxtNombre.getText();
        String CIF = TxtCif.getText();
        String direccion = TxtDireccion.getText();
        String telefono = TxtTelefono.getText();
        String poblacion = TxtPoblacion.getText();
        String provincia = TxtProvincia.getText();
        String codigoPostal = TxtCodigoPostal.getText();
        String email = TxtEmail.getText();
        String formaPago = TxtFormaPago.getText();
        String cuenta = TxtNumeroCuenta.getText();
        String total = TxtTotal.getText();

        if (!nombreCliente.equals("") && !CIF.equals("") && !direccion.equals("") && !telefono.equals("") && !poblacion.equals("") && !provincia.equals("") && !codigoPostal.equals("") && !email.equals("") && !formaPago.equals("") && !cuenta.equals("") && !total.equals("")) {
            boolean esValidoTelefono = Validaciones.validarSoloDigitos(telefono);
            if (esValidoTelefono) {
                Connection conexion = ConexionFacturas.obtenerConexion();
                String queryFactura = "UPDATE Factura SET Total = ?, FormaPago = ? WHERE IdFactura = ?";
                try {
                    Double totalLimpio = Facturas.limpiarTotal(total);
                    PreparedStatement ps = conexion.prepareStatement(queryFactura);
                    ps.setObject(1, totalLimpio);
                    ps.setObject(2, formaPago);
                    ps.setObject(3, idFactura);
                    ps.executeUpdate();

                    String queryEmpresa = "UPDATE Empresa SET Nombre = ?, CIF = ? WHERE IdEmpresa = ?";
                    PreparedStatement ps2 = conexion.prepareStatement(queryEmpresa);
                    ps2.setObject(1, nombreCliente);
                    ps2.setObject(2, CIF);
                    ps2.setObject(3, idCliente);
                    ps2.executeUpdate();

                    String queryContacto = "UPDATE Contacto SET Telefono = ?, Email = ? WHERE Empresa_idEmpresa = ? AND Telefono = ?";
                    PreparedStatement ps3 = conexion.prepareStatement(queryContacto);
                    ps3.setObject(1, telefono);
                    ps3.setObject(2, email);
                    ps3.setObject(3, idCliente);
                    ps3.setObject(4, telefonoOriginal);
                    ps3.executeUpdate();
                    for (int i = 0; i < articuloOriginal.size(); i++) {
                        String articulos = articuloOriginal.get(i);
                        int idArticulo = obtenerIdArticulos(idFactura, articulos);
                        borrarArticulosDescuentoArticulos(idArticulo);
                        borrarArticulosImpuestosArticulos(idArticulo);
                    }
                    borrarArticulos();
                    // recorrer tabla de artículos                    
                    model = (DefaultTableModel) TblConceptoImporte.getModel();

                    // Recorre todas las filas del modelo
                    for (int i = 0; i < model.getRowCount(); i++) {
                        // Obtén el producto de la primera columna (índice 0)
                        String producto = model.getValueAt(i, 0).toString();

                        // Obtén el importe de la segunda columna (índice 1)
                        double importe = Double.parseDouble(model.getValueAt(i, 1).toString());

                        // Asumiendo que tienes el idFactura, lo puedes obtener o ya tenerlo definido
                        // (esto depende de tu lógica, puede ser algo que se pase al método o se obtenga de otra parte)
                        // Ahora ejecutamos la actualización con los valores obtenidos
                        String queryArticulos = "INSERT INTO Articulos (Producto, Importe, Factura_idFactura) VALUES (?, ?, ?)";
                        PreparedStatement ps4 = conexion.prepareStatement(queryArticulos);

                        // Establecemos los valores en la consulta
                        ps4.setObject(1, producto);
                        ps4.setObject(2, importe);
                        ps4.setObject(3, idFactura);

                        // Ejecutamos la actualización
                        ps4.executeUpdate();  // Usamos executeUpdate para ejecutar una actualización en lugar de executeQuery
                    }
                    String queryDireccion = "UPDATE Direccion SET Direccion = ?, CodigoPostal = ?, Provincia = ?, Poblacion = ? WHERE Empresa_idEmpresa = ? AND Direccion = ?";
                    PreparedStatement ps5 = conexion.prepareStatement(queryDireccion);
                    ps5.setObject(1, direccion);
                    ps5.setObject(2, codigoPostal);
                    ps5.setObject(3, provincia);
                    ps5.setObject(4, poblacion);
                    ps5.setObject(5, idCliente);
                    ps5.setObject(6, direccionOriginal);

                    ps5.executeUpdate();

                    String queryCuenta = "UPDATE EmpresaCuentas SET NumeroCuenta = ? WHERE Empresa_idEmpresa = ? AND NumeroCuenta = ?";
                    PreparedStatement ps6 = conexion.prepareStatement(queryCuenta);
                    ps6.setObject(1, cuenta);
                    ps6.setObject(2, idEmpresa);
                    ps6.setObject(3, cuentaOriginal);

                    ps6.executeUpdate();
                    JOptionPane.showMessageDialog(this, "ACTUALIZADO CORRECTAMENTE", "OK", JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error al ACTUALIZAR. No se ha seleccionado ningún artículo." + e, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un teléfono válido.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese todos los campos obligatórios.", "Warning", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_CmdModificarFacturaActionPerformed

    private void CmdCrearFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmdCrearFacturaActionPerformed
        //CREA LA FACTURA, GENERANDO UN EXCEL(.XLSX)
        Connection conexion = ConexionFacturas.obtenerConexion();
        File inputFile = new File("C:\\Users\\Practicas\\Documents\\NetBeansProjects\\FacturasExcel\\src\\main\\java\\LeerFacturas\\FacturaVacia.xlsx");
        File outputFile = new File("C:\\Users\\Practicas\\Documents\\NetBeansProjects\\FacturasExcel\\src\\main\\java\\facturasGuardadas\\ESPECIALFactura.xlsx");

        String numeroFactura = TxtNumeroFactura.getText();
        String fechaEmision = TxtFechaEmision.getText();
        String nombreCliente = TxtNombre.getText();
        String CIF = TxtCif.getText();
        String direccion = TxtDireccion.getText();
        String telefono = TxtTelefono.getText();
        String poblacion = TxtPoblacion.getText();
        String provincia = TxtProvincia.getText();
        String codigoPostal = TxtCodigoPostal.getText();
        String email = TxtEmail.getText();
        String formaPago = TxtFormaPago.getText();
        String cuenta = TxtNumeroCuenta.getText();
        String total = TxtTotal.getText();

        try {
            FileInputStream fis = new FileInputStream(inputFile);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(2);
            Cell cell = row.getCell(3);
            cell.setCellValue(numeroFactura);
            row = sheet.getRow(2);
            cell = row.getCell(6);
            cell.setCellValue(fechaEmision);
            row = sheet.getRow(6);
            cell = row.getCell(3);
            cell.setCellValue(nombreCliente);
            row = sheet.getRow(7);
            cell = row.getCell(3);
            cell.setCellValue(CIF);
            row = sheet.getRow(7);
            cell = row.getCell(5);
            cell.setCellValue(telefono);
            row = sheet.getRow(8);
            cell = row.getCell(3);
            cell.setCellValue(direccion);
            row = sheet.getRow(9);
            cell = row.getCell(3);
            cell.setCellValue(poblacion);
            row = sheet.getRow(9);
            cell = row.getCell(5);
            cell.setCellValue(provincia);
            row = sheet.getRow(9);
            cell = row.getCell(7);
            cell.setCellValue(codigoPostal);
            row = sheet.getRow(10);
            cell = row.getCell(3);
            cell.setCellValue(email);
            for (int i = 0; i < model.getRowCount(); i++) {
                String concepto = model.getValueAt(i, 0).toString();
                String importe = model.getValueAt(i, 1).toString();

                // Si estamos en la primera fila, la fila 13
                if (i == 0) {
                    row = sheet.getRow(13);  // Fila 13
                    if (row == null) {
                        row = sheet.createRow(13);  // Crear la fila si no existe
                    }

                    // Crear celdas para el concepto (columna 1)
                    cell = row.getCell(1);
                    if (cell == null) {
                        cell = row.createCell(1);  // Crear la celda si no existe
                    }
                    cell.setCellValue(concepto);

                    // Crear celdas para el importe (columna 6)
                    cell = row.getCell(6);
                    if (cell == null) {
                        cell = row.createCell(6);  // Crear la celda si no existe
                    }
                    String importeEuros = importe + " €";
                    cell.setCellValue(importeEuros);

                } else {
                    // Desplazar las filas hacia abajo y crear una nueva fila
                    sheet.shiftRows(13 + i, sheet.getLastRowNum(), 1);  // Desplaza las filas hacia abajo
                    row = sheet.createRow(13 + i);  // Crea una nueva fila para cada iteración

                    // Combinar las celdas de concepto (columna 1 a 5) y de importe (columna 6 a 7)
                    sheet.addMergedRegion(new CellRangeAddress(13 + i, 13 + i, 1, 5));  // Combina columnas 1 a 5 para el concepto
                    sheet.addMergedRegion(new CellRangeAddress(13 + i, 13 + i, 6, 7));  // Combina columnas 6 a 7 para el importe

                    // Crear un estilo de celda (border, negrita, centrado)
                    CellStyle cellStyle = workbook.createCellStyle();

                    // Definir borde
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);

                    // Centrado del texto
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                    // Estilo en negrita
                    org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                    font.setBold(true);  // Establece el texto en negrita

                    // Configurar la tipografía Arial y el tamaño 9
                    font.setFontName("Arial");
                    font.setFontHeightInPoints((short) 9);  // Establecer el tamaño de fuente a 9

                    cellStyle.setFont(font);

                    // Crear celdas para el concepto (columna 1 a 5) y aplicar estilo
                    for (int j = 1; j <= 5; j++) {
                        cell = row.createCell(j);
                        cell.setCellValue(concepto);
                        cell.setCellStyle(cellStyle);  // Aplicar el estilo
                    }

                    // Crear celdas para el importe (columna 6 a 7) y aplicar estilo
                    for (int j = 6; j <= 7; j++) {
                        cell = row.createCell(j);
                        String importeEuros = importe + " €";
                        cell.setCellValue(importeEuros);
                        cell.setCellStyle(cellStyle);  // Aplicar el estilo
                    }
                }
            }

            int filaPosicion = 12;  // Filas desde las que comenzamos
            int columnaFormaPago = 1;  // Columna de la "Forma de pago" 
            int columnaTotal = 6;  // Columna del "Total"
            String formaPago2 = "";
            String cuenta2;
            // Buscar "Forma de pago"
            while (filaPosicion < sheet.getPhysicalNumberOfRows()) {
                row = sheet.getRow(filaPosicion);
                if (row != null) {
                    // Leer la celda en la columna de forma de pago
                    cell = row.getCell(columnaFormaPago);
                    formaPago2 = (cell != null) ? obtenerValorCelda(cell) : "";

                    // Imprimir para depuración
                    System.out.println("Fila " + filaPosicion + ", Forma de pago: " + formaPago2);

                    // Verificar si encontramos "Forma de pago"
                    if (formaPago2.contains("Forma de pago:")) {
                        break;
                    }
                }
                filaPosicion++;
            }

            // Si encontramos "Forma de pago", obtener el valor correspondiente
            if (!formaPago2.isEmpty() && formaPago2.contains("Forma de pago:")) {
                row = sheet.getRow(filaPosicion);
                cell = row.getCell(columnaFormaPago);  // Suponiendo que el valor está en la celda siguiente
                formaPago2 = (cell != null) ? obtenerValorCelda(cell) : "";
                String formaPagoTotal = formaPago2 + " " + formaPago;
                cell.setCellValue(formaPagoTotal);
                System.out.println("Forma de pago encontrada: " + formaPagoTotal);
            }

            // Obtener cuenta bancaria
            row = sheet.getRow(filaPosicion + 1);
            cell = row.getCell(columnaFormaPago);  // Celda donde esperas encontrar la cuenta
            cuenta2 = (cell != null) ? obtenerValorCelda(cell) : "";

            // Depuración para cuenta
            System.out.println("Cuenta bancaria: " + cuenta2);

            // Si no contiene dígitos, busca la siguiente celda
            row = sheet.getRow(filaPosicion + 1);
            cell = row.getCell(columnaFormaPago + 1);  // Celda siguiente
            cell.setCellValue(cuenta);
            System.out.println("Cuenta bancaria: " + cuenta);

            // Buscar el totaL
            String total2;
            filaPosicion--;  // Retroceder una fila para buscar el total
            row = sheet.getRow(filaPosicion);
            cell = row.getCell(columnaTotal - 1);  // Celda donde debería estar el total
            total2 = (cell != null) ? obtenerValorCelda(cell) : "";

            // Depuración para total
            System.out.println("Total encontrado: " + total);

            while (filaPosicion < sheet.getPhysicalNumberOfRows()) {  // Asegurarse de no exceder el número de filas
                row = sheet.getRow(filaPosicion);
                if (row != null) {
                    cell = row.getCell(columnaTotal - 1);
                    total2 = (cell != null) ? obtenerValorCelda(cell) : "";
                    // Si la celda está vacía, seguimos buscando
                    if (total2.equals("TOTAL")) {
                        break;  // Solo avanzamos si la celda está vacía
                    }
                } else {
                    // Si la fila es null, seguimos al siguiente
                }
                filaPosicion++;
            }

            System.out.println("Total encontrado: " + total2);
            if (total2 != null && total2.equals("TOTAL")) {
                row = sheet.getRow(filaPosicion);
                if (row != null) {
                    cell = row.getCell(columnaTotal);  // Celda siguiente al "TOTAL"
                    String totalEuros = total + " €";
                    cell.setCellValue(totalEuros);
                }
            }

            System.out.println("Total final: " + total);
            String firma = "";
            while (filaPosicion < sheet.getPhysicalNumberOfRows()) {
                row = sheet.getRow(filaPosicion);
                if (row != null) {
                    // Leer la celda en la columna de forma de pago
                    cell = row.getCell(columnaFormaPago);
                    firma = (cell != null) ? obtenerValorCelda(cell) : "";

                    // Imprimir para depuración
                    System.out.println("Fila " + filaPosicion + ", Firma: " + firma);

                    // Verificar si encontramos "Forma de pago"
                    if (firma.contains("FIRMA")) {
                        break;
                    }

                }
                filaPosicion++;

            }
            System.out.println("Fila " + filaPosicion + ", Firma: " + firma);
            //INSERTAR IMAGEN EN LA FACTURA EN LA POSICICION DE LA CELDA FIRMA + 1
            // Si encontramos la celda de la firma, insertar la imagen en la fila siguiente
            try {
                int firmaPosicion = filaPosicion + 1; // Posición después de la celda "FIRMA"
                int columnaFirma = 1; // Columna donde se coloca la imagen

                // Ruta de la imagen
                File imagenFile = new File("C:\\Users\\Practicas\\Documents\\NetBeansProjects\\FacturasExcel\\src\\main\\java\\LeerFacturas\\firmaCefora.png");
                FileInputStream imageInputStream = new FileInputStream(imagenFile);
                byte[] imageBytes = IOUtils.toByteArray(imageInputStream);
                imageInputStream.close();

                // Insertar la imagen en el libro de Excel
                int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
                CreationHelper helper = workbook.getCreationHelper();
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = helper.createClientAnchor();

                // Definir la posición de la imagen
                anchor.setCol1(columnaFirma); // Columna donde se coloca la imagen
                anchor.setRow1(firmaPosicion); // Fila donde se coloca
                anchor.setCol2(columnaFirma + 2); // Define el ancho ocupando 2 columnas
                anchor.setRow2(firmaPosicion + 5); // Define la altura ocupando 5 filas

                // Insertar la imagen y ajustar tamaño
                Picture picture = drawing.createPicture(anchor, pictureIdx);
                picture.resize(); // Ajusta la imagen automáticamente

            } catch (IOException ex) {
                System.out.println("NO SE INSERTO LA IMAGEN: " + ex);
            }

            // ✅ Guardar el archivo correctamente DESPUÉS de insertar la imagen
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
                System.out.println("Factura generada con éxito.");
            } catch (IOException e) {
                System.out.println("Error al guardar el archivo: " + e);
            }

        } catch (IOException e) {
            System.out.println(e);
        }

    }//GEN-LAST:event_CmdCrearFacturaActionPerformed

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
            java.util.logging.Logger.getLogger(VentanaExcel.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaExcel.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaExcel.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaExcel.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaExcel(0, 0, 0).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnArticulos;
    private javax.swing.JButton BtnBorrarArticulos;
    private javax.swing.JButton BtnModificarArticulos;
    private javax.swing.JButton BtnVolver;
    private javax.swing.JButton CmdCrearFactura;
    private javax.swing.JButton CmdModificarFactura;
    private javax.swing.JLabel LblCif;
    private javax.swing.JLabel LblCliente;
    private javax.swing.JLabel LblCodigoPostal;
    private javax.swing.JLabel LblCuenta;
    private javax.swing.JLabel LblDireccion;
    private javax.swing.JLabel LblEmail;
    private javax.swing.JLabel LblFechaEmision;
    private javax.swing.JLabel LblFormaPago;
    private javax.swing.JLabel LblNombre;
    private javax.swing.JLabel LblNumeroFactura;
    private javax.swing.JLabel LblPoblacion;
    private javax.swing.JLabel LblProvincia;
    private javax.swing.JLabel LblTelefono;
    private javax.swing.JLabel LblTotal;
    private javax.swing.JTable TblConceptoImporte;
    private javax.swing.JTextField TxtCif;
    private javax.swing.JTextField TxtCodigoPostal;
    private javax.swing.JTextField TxtConcepto;
    private javax.swing.JTextField TxtDireccion;
    private javax.swing.JTextField TxtEmail;
    private javax.swing.JTextField TxtFechaEmision;
    private javax.swing.JTextField TxtFormaPago;
    private javax.swing.JTextField TxtImporte;
    private javax.swing.JTextField TxtNombre;
    private javax.swing.JTextField TxtNumeroCuenta;
    private javax.swing.JTextField TxtNumeroFactura;
    private javax.swing.JTextField TxtPoblacion;
    private javax.swing.JTextField TxtProvincia;
    private javax.swing.JTextField TxtTelefono;
    private javax.swing.JTextField TxtTotal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblConcepto;
    private javax.swing.JLabel lblImporte;
    // End of variables declaration//GEN-END:variables
}
