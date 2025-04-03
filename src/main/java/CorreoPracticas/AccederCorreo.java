package CorreoPracticas;

//PON EL CORREO BIEN XD

import LeerFacturas.Facturas;
import java.io.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class AccederCorreo {

    public static void main(String[] args){
        // Configuración de las propiedades para IMAP (recibir correos)
        String host = "imap.gmail.com";  // Servidor IMAP de Gmail 
        final String username = "progamacionesctructurada@gmail.com";  // Tu correo de Gmail QUE NADIE TOQUE EL CORREO!!!
        final String password = "vqln qydw rzog sgyz";  // Tu contraseña de Gmail (usa una contraseña de aplicación si tienes verificación en 2 pasos) 
        //DA MENOS PROBLEMAS CON ESTA CONTRASEÑA( LA DE LA APLICACIÓN) LA NORMAL ES 12345678A@ 

        // Configurar las propiedades de la sesión IMAP
        Properties properties = new Properties();
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");  // Activar SSL

        try {
            // Crear una sesión de JavaMail con la autenticación de tu cuenta
                Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);  // Autenticación con tu correo y contraseña
                }
            });

            try ( // Conectarse al servidor de correo IMAP
                Store store = session.getStore("imap")) {
                store.connect(host, username, password);

                // Obtener la bandeja de entrada
                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);  // Abrir la bandeja de entrada en solo lectura

                // Obtener los mensajes de la bandeja de entrada
                Message[] messages = inbox.getMessages();
                //después de Message 
                for (Message message : messages) {
                    // Verificar si el mensaje tiene adjuntos
                    if (message.isMimeType("multipart/*")) {
                        MimeMultipart multipart = (MimeMultipart) message.getContent();

                        // Iterar sobre las partes del mensaje
                        for (int i = 0; i < multipart.getCount(); i++) {
                            MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);

                            // Verificar si es un archivo adjunto
                            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                // Verificar que el archivo sea un .xlsx
                                String fileName = part.getFileName();
                                if (fileName.endsWith(".xlsx")) {
                                    // Definir la ruta donde quieres guardar el archivo
                                    String saveDirectory = "C:\\Users\\Practicas\\Documents\\NetBeansProjects\\FacturasExcel\\src\\main\\java\\facturasGuardadas\\";
                                    String archivo = saveDirectory + fileName;
                                    File file = new File(archivo);

                                    // Descargar el archivo
                                    part.saveFile(file);
                                    System.out.println("Archivo descargado: " + file.getAbsolutePath());
                                    Facturas.guardarDatos(archivo);
                                }
                            }
                        }
                    }
                }

                // Cerrar la conexión y la bandeja de entrada
                inbox.close(false);
            }

        } catch (IOException | MessagingException e) {
            System.out.println(e);
        }
    }
}
