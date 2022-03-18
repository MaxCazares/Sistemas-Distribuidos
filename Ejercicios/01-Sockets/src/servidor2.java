import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class servidor2 {
    public static void main(String[] args) {
        try {
            ServerSocket servidor = new ServerSocket(50000);
            System.out.println("Servidor listo ...");

            Socket conexion = servidor.accept();
            System.out.println("Cliente conectado desde el puerto: " + 50000);

            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            
            long inicio = System.currentTimeMillis();

            firstExercise(entrada, 10000);
            // secondExercise(entrada, 10000);
/*
            int N = 10;
            double[][] A = new double[N][N];
            double[][] B = new double[N][N];

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    A[i][j] = entrada.readDouble();
                    B[i][j] = entrada.readDouble();
                }
            }

            imprimirMatriz(A, N);
            System.out.println("\n\n");
            imprimirMatriz(B, N);          
*/  

            long fin = System.currentTimeMillis();

            System.out.println("Tiempo: " + (fin - inicio));

            conexion.close();
            servidor.close();

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void firstExercise(DataInputStream entrada, int numbers) throws Exception {
        double[] x = new double[numbers];
        for (int i = 0; i < x.length; i++) {
            x[i] = entrada.readDouble();
        }
    }

    public static void secondExercise(DataInputStream entrada, int numbers) throws Exception {
        byte[] a = new byte[numbers * 8];
        entrada.read(a, 0, numbers * 8);
        ByteBuffer b = ByteBuffer.wrap(a);

        // for (int i = 0; i < numbers; i++) {
        //     System.out.println(b.getDouble());
        // }
        System.out.println(b);
    }

    public static void imprimirMatriz(double[][] m, int N){
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println("\n");
        }
    }
}