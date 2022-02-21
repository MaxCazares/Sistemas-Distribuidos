import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocketFactory;

public class ServidorSSL {
    public static void main(String[] args) throws IOException {
        
        SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        ServerSocket socket_servidor = socket_factory.createServerSocket(50_000);
        Socket conexion = socket_servidor.accept();

        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

        double x = entrada.readDouble();
        System.out.println(x);

        conexion.close();
        
    }
}