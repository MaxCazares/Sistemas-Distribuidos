import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Servidor {

    static class Worker extends Thread{

        Socket conexion;
        Worker(Socket conexion){
            this.conexion = conexion;
        }

        public void run() {
            try {
                
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

                int n = entrada.readInt();
                System.out.println(n);

                double x = entrada.readDouble();
                System.out.println(x);

                byte[] buffer = new byte[4];
                entrada.read(buffer, 0, 4);
                System.out.println(new String(buffer, "UTF-8"));                

                salida.write("HOLA".getBytes());

                byte[] a = new byte[5*8];
                entrada.read(a, 0, 5*8);

                ByteBuffer b = ByteBuffer.wrap(a);
                for (int i = 0; i < 5; i++) {
                    System.out.println(b.getDouble());
                }

                conexion.close();

            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
    public static void main(String[] args) throws Exception {

        ServerSocket servidor = new ServerSocket(50000);
        System.out.println("Servidor listo ...");
        
        while(true){
            Socket conexion = servidor.accept();
            Worker w = new Worker(conexion);
            w.start();
            servidor.close();
        }
    }
}