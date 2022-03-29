public class MultiplicaMatrices2 {
    
    static int N = 4000;
    static float[][] A = new float[N][N];
    static float[][] B = new float[N][N];
    static float[][] C = new float[N][N];
    public static void main(String[] args) {
        
        long inicio = System.currentTimeMillis();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = i + 2 * j;
                B[i][j] = 3 * i - j;
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                float x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    C[i][j] += A[i][k] * B[j][k];
                }
            }
        }

        float checksum = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                checksum += C[i][j];
            }
        }

        long fin = System.currentTimeMillis();

        // System.out.println("Matriz A");
        // imprimirMatriz(A, N, N);
    
        // System.out.println("Matriz B");
        // imprimirMatriz(B, N, N);

        // System.out.println("Matriz C");
        // imprimirMatriz(C, N, N);

        System.out.println("Tiempo de ejecucion: " + (fin - inicio) + " ms");
        System.out.println("checksum = " + checksum);

    }

    public static void imprimirMatriz(float[][] m, int filas, int columnas) {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println("\n");
        }
    }
}