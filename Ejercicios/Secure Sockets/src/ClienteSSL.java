import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

public class ClienteSSL {
    public static void main(String[] args) throws IOException, InterruptedException {
        
        SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket conexion = cliente.createSocket("localhost", 50_000);

        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

        salida.writeDouble(789.987);
        Thread.sleep(1000);
        conexion.close();

    }
}