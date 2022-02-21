import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class cliente2 {
    public static void main(String[] args) throws Exception {

        // principal();

        Socket conexion = null;

        while(true){
            try {
                conexion = new Socket("localhost", 50000);
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
    
                long inicio = System.currentTimeMillis();
        
                firstExercise(salida, 10000);
                // secondExercise(salida, 10000);
        
                long fin = System.currentTimeMillis();
        
                System.out.println("Tiempo: " + (fin - inicio));
        
                conexion.close();
                break;
                
            } catch(Exception e){
                System.err.println(e);
                Thread.sleep(100);                
            }
        }

    }

    public static void firstExercise(DataOutputStream salida, int numbers) throws Exception {
        for (int i = 0; i < numbers; i++) {
            salida.writeDouble(i);
        }
    }

    public static void secondExercise(DataOutputStream salida, int numbers) throws Exception {
        ByteBuffer b = ByteBuffer.allocate(numbers * 8);

        for (int i = 0; i < numbers; i++) {
            b.putDouble(i);
        }

        byte[] a = b.array();
        salida.write(a);
    }
}