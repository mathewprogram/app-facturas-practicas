package LeerFacturas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Validaciones {
    //COMPRUEBA QUE EL IMPORTE SEA UN DOBLE
    public static boolean validarImporte(String importe) {
        try {
            Double.valueOf(importe);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    //PATRÓN PARA VALORAR EL USO DE DIGITOS (PARA TELÉFONO...)
    public static boolean validarSoloDigitos(String valor) {
        String regex = "^[0-9]+$";
        if (valor.matches(regex)) {
            return true;
        } else {
            return false;
        }
    }
    //es verdadero si el Login existe y es correcto
    public static boolean validarLogin(String login, ArrayList<String> loginArray) {
        for (int i = 0; i < loginArray.size(); i++) {
            String nombreComparativo = loginArray.get(i);
            if (nombreComparativo.equals(login)) {
                return false;
            }
        }
        return true;
    }
    //FILTRO PARA NÚMERO DE FACTURA, PUSIMOS UN PATRÓN BASTANTE GENÉRICO
    public static boolean validarNumeroFactura(String numeroFactura) {
        String patron = "^\\d+-\\d+$";
        if (numeroFactura.matches(patron)) {
            return true;
        } else {
            return false;
        }

    }
    //MÉTODO PARA COMPROBAR QUE LA FACTURA Y EL NUMERO DE FACTURA NO SE REPITAN EN UNA MISMA EMPRESA ...
    public static boolean buscarFacturaDuplicada(String numeroFactura) {
        Connection conexion = ConexionFacturas.obtenerConexion();
        if (conexion != null) {
            try {
                String query = "SELECT idEmpresa FROM Empresa WHERE Nombre = ? AND CIF = ?";
                PreparedStatement ps = conexion.prepareStatement(query);
                ps.setObject(1, "Cefora");
                ps.setObject(2, "B82620683");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int idEmpresa = rs.getInt("idEmpresa");
                    String queryNumero = "SELECT NumeroFactura FROM Factura WHERE IdEmpresa = ?";
                    PreparedStatement ps1 = conexion.prepareStatement(queryNumero);
                    ps1.setObject(1, idEmpresa);
                    ResultSet rs1 = ps1.executeQuery();
                    while (rs1.next()) {
                        String numero_Factura = rs1.getString("NumeroFactura");
                        if (numero_Factura.equals(numeroFactura)) {
                            return true;
                        }
                    }
                }

            } catch (SQLException e) {
                System.out.println("Error QUERY" + e);
            }

        } else {
            System.out.println("Error de Conexión");
        }
        return false;
    }
}
