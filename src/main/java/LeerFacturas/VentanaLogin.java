package LeerFacturas;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

//VENTANA DE ENTRADA , PUEDES SER ADMINISTRADOR(Practicas1 y Practicas2) O GESTOR(Practicas3)
public class VentanaLogin extends javax.swing.JFrame {

        //PERSONALIZAR VENTANA
        public void personalizar_JFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setFont(new Font("Courier New", Font.PLAIN, 12));
        this.setTitle("Iniciar Sesión");
        this.setLocationRelativeTo(null);
        Color color = new Color(153,153,153);
        this.getContentPane().setBackground(color);
        this.setResizable(false);
        this.setSize(300, 285); // Ajustar tamaño de la ventana

        try {
            // Cargar y escalar el icono de la ventana
            ImageIcon icono = new ImageIcon("C:/Users/Practicas/Documents/NetBeansProjects/FacturasExcel/src/main/java/LeerFacturas/Logo.png");
            Image imagenEscalada = icono.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            this.setIconImage(imagenEscalada);
        } catch (Exception e) {
            System.out.println("Error: Imagen no encontrada");
        }

        // Hacer que el panel principal sea transparente para ver el fondo
        ((JPanel) this.getContentPane()).setOpaque(true);
    }


    public VentanaLogin() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initComponents();
        personalizar_JFrame();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LblLogin = new javax.swing.JLabel();
        LblClave = new javax.swing.JLabel();
        TxtLogin = new javax.swing.JTextField();
        CmdEntrar = new javax.swing.JButton();
        TxtClave = new javax.swing.JPasswordField();
        LblClave1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Inicia Sesión");
        setBackground(new java.awt.Color(204, 204, 204));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(new java.awt.Color(153, 153, 153));
        setPreferredSize(new java.awt.Dimension(230, 200));
        setResizable(false);

        LblLogin.setBackground(new java.awt.Color(153, 153, 153));
        LblLogin.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        LblLogin.setForeground(new java.awt.Color(255, 51, 51));
        LblLogin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LblLogin.setText("LOGIN");
        LblLogin.setBorder(new javax.swing.border.MatteBorder(null));

        LblClave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LblClave.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LblClave.setText("Usuario");

        TxtLogin.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        TxtLogin.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        TxtLogin.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        TxtLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtLoginActionPerformed(evt);
            }
        });

        CmdEntrar.setBackground(new java.awt.Color(255, 255, 204));
        CmdEntrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        CmdEntrar.setForeground(new java.awt.Color(51, 204, 0));
        CmdEntrar.setText("ENTRAR");
        CmdEntrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmdEntrarActionPerformed(evt);
            }
        });

        TxtClave.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        TxtClave.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        TxtClave.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        TxtClave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtClaveActionPerformed(evt);
            }
        });

        LblClave1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LblClave1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LblClave1.setText("Contraseña");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LblLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addComponent(CmdEntrar)
                .addGap(105, 105, 105))
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LblClave1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(LblClave, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(TxtLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                            .addComponent(TxtClave))))
                .addGap(35, 35, 35))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(LblLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(LblClave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TxtLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LblClave1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TxtClave, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(CmdEntrar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TxtLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtLoginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtLoginActionPerformed

    private void CmdEntrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmdEntrarActionPerformed
        // Obtener la conexión
        Connection conexion = LeerFacturas.ConexionFacturas.obtenerConexion();

        if (conexion != null) {
            // Declaración de la query
            String query = "SELECT Rol FROM Login WHERE Login = ? AND Clave = ?"; // Ahora filtramos también por clave
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                // Obtener los valores del formulario
                String login = TxtLogin.getText();  // Obtener el valor del campo de texto Login
                String clave = TxtClave.getText();  // Obtener el valor del campo de texto Clave

                // Preparar la sentencia
                ps = conexion.prepareStatement(query);
                ps.setString(1, login); // Establecer el valor de Login
                ps.setString(2, clave); // Establecer el valor de Clave

                // Ejecutar la consulta
                rs = ps.executeQuery();

                // Verificar si hay resultados
                if (rs.next()) {
                    String rol = rs.getString("Rol");

                    // Comparar el rol con "Administrador"
                    if ("Administrador".equals(rol)) {
                        JOptionPane.showMessageDialog(this, "HOLA ADMINISTRADOR", "OK", JOptionPane.INFORMATION_MESSAGE);
                        // Si es Administrador, mostrar la ventana correspondiente
                        VentanaCRUDLogin ventanaCRUD = new VentanaCRUDLogin();
                        ventanaCRUD.setVisible(rootPaneCheckingEnabled);
                        this.dispose();
                    } else {
                        // Si no es Administrador, mostrar mensaje o hacer algo
                        JOptionPane.showMessageDialog(this, "HOLA GESTOR", "OK", JOptionPane.INFORMATION_MESSAGE);
                        VentanaSelecionarGestor ventanaSelecionar = new VentanaSelecionarGestor();
                        ventanaSelecionar.setVisible(rootPaneCheckingEnabled);
                        this.dispose();
                    }
                } else {
                    // Si no se encontró un usuario con ese login y contraseña
                    System.out.println("Login o contraseña incorrectos.");
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Manejo de excepciones
            } finally {
                // Cerrar recursos
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                    if (conexion != null) {
                        conexion.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Login o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_CmdEntrarActionPerformed

    private void TxtClaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtClaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtClaveActionPerformed

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
            java.util.logging.Logger.getLogger(VentanaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaLogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CmdEntrar;
    private javax.swing.JLabel LblClave;
    private javax.swing.JLabel LblClave1;
    private javax.swing.JLabel LblLogin;
    private javax.swing.JPasswordField TxtClave;
    private javax.swing.JTextField TxtLogin;
    // End of variables declaration//GEN-END:variables
}
