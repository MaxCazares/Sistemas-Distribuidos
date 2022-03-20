import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class cliente2 {
    public static void main(String[] args) throws Exception {

        // uno();
        Socket conexion = null;

        while (true) {
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

        } catch (Exception e) {
        System.err.println("Esperando al servidor");
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

    public static void imprimirMatriz(double[][] m, int N) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println("\n");
        }
    }

    public static void imprimirMediaMatriz(double[][] m, int N) {
        for (int i = 0; i < (N / 2); i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println("\n");
        }
    }

    public static double[][] multiplicaMatrices(double[][] A, double[][] B, int N) {
        double[][] C = new double[N/2][N/2];

        for (int i = 0; i < (N/2); i++){
            for (int j = 0; j < (N/2); j++){
                for (int k = 0; k < N; k++){
                    C[i][j] += A[i][k] * B[j][k];
                }
            }
        }
        return C;
    }

    public static void uno() {
        int N = 4;
        double[][] A = new double[N][N];
        double[][] B = new double[N][N];
        double[][] C = new double[N/2][N/2];
        double[][] A1 = new double[N / 2][N];
        double[][] A2 = new double[N / 2][N];
        double[][] B1 = new double[N / 2][N];
        double[][] B2 = new double[N / 2][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = i + 5 * j;
                B[i][j] = 5 * i - j;
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                double x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        }

        for (int i = 0; i < (N / 2); i++) {
            for (int j = 0; j < N; j++) {
                A1[i][j] = A[i][j];
                A2[i][j] = A[i + N / 2][j];
            }
        }

        for (int i = 0; i < N / 2; i++) {
            for (int j = 0; j < N; j++) {
                B1[i][j] = B[i][j];
                B2[i][j] = B[i + N / 2][j];
            }
        }

        C = multiplicaMatrices(A1, B2, N);

        System.out.println("Matriz A");
        imprimirMatriz(A, N);
        System.out.println("\n");

        System.out.println("Matriz A1");
        imprimirMediaMatriz(A1, N);
        System.out.println("\n");

        System.out.println("Matriz A2");
        imprimirMediaMatriz(A2, N);
        System.out.println("\n");

        System.out.println("Matriz B");
        imprimirMatriz(B, N);
        System.out.println("\n");

        System.out.println("Matriz B1");
        imprimirMediaMatriz(B1, N);
        System.out.println("\n");

        System.out.println("Matriz B2");
        imprimirMediaMatriz(B2, N);
        System.out.println("\n");

        System.out.println("Matriz C");
        imprimirMediaMatriz(C, N);
        System.out.println("\n");
    }
}