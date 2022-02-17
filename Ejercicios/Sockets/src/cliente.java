import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class cliente {
    public static void main(String[] args) throws Exception{        
        try {

            Socket conexion = new Socket("localhost", 50000);
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());

            salida.writeInt(123);
            salida.writeDouble(123.321);
            salida.writeUTF("hola");
            salida.write("mundo".getBytes());

            byte[] buffer = new byte[4];
            // entrada.read(buffer, 0, 4);
            read(entrada, buffer, 0, 4);
            System.out.println(new String(buffer, "UTF-8"));

            ByteBuffer b = ByteBuffer.allocate(5*8);
            b.putDouble(1.1);
            b.putDouble(1.2);
            b.putDouble(1.3);
            b.putDouble(1.4);
            b.putDouble(1.5);
            byte[] a = b.array();
            salida.write(a);

            Thread.sleep(1000);
            conexion.close();

        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    public static void read(DataInputStream f, byte[] b, int posicion, int longitud) {
        try {
            while(longitud > 0){
                int n = f.read(b, posicion, longitud);
                posicion += n;
                longitud -= n;
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}