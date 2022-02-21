import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class servidor {
    public static void main(String[] args) throws Exception{
        try {

            ServerSocket servidor = new ServerSocket(50000);
            System.out.println("Servidor listo ...");
            
            Socket conexion = servidor.accept();
            System.out.println("Cliente conectado desde el puerto: "+50000);
            
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());

            int n = entrada.readInt();
            System.out.println(n);

            double x = entrada.readDouble();
            System.out.println(x);

            String mensaje = entrada.readUTF();
            System.out.println(mensaje);

            byte[] buffer = new byte[5];
            entrada.read(buffer, 0, 5);
            System.out.println(new String(buffer, "UTF-8"));

            salida.write("java".getBytes());

            byte[] a = new byte[5*8];
            entrada.read(a, 0, 5*8);
            ByteBuffer b = ByteBuffer.wrap(a);

            for (int i = 0; i < 5; i++) {
                System.out.println(b.getDouble());
            }

            conexion.close();  
            servidor.close();

        } catch (Exception e) {
            System.err.println(e);
        }  
    }
}