package LeerFacturas;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class ConexionFacturas {
    public static Connection obtenerConexion() {
        String url = "jdbc:mysql://localhost:3306/BD_FACTURAS";
        String usuario = "root";
        String clave = "12345678";
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(url, usuario, clave);
        } catch (SQLException e) {
            conexion = null;
        }
        return conexion;
    }
}
