import java.rmi.Naming;
import java.rmi.RemoteException;

public class ClienteMatricesRMI {

    // static int N = 8;
    static int N = 4000;

    static float[][] A = new float[N][N];
    static float[][] B = new float[N][N];
    static float[][] bT = new float[N][N];
    static float[][] C = new float[N][N];

    static float[][] A1 = new float[N/4][N];
    static float[][] A2 = new float[N/4][N];
    static float[][] A3 = new float[N/4][N];
    static float[][] A4 = new float[N/4][N];

    static float[][] B1 = new float[N/4][N];
    static float[][] B2 = new float[N/4][N];
    static float[][] B3 = new float[N/4][N];
    static float[][] B4 = new float[N/4][N];
    
    public static class worker extends Thread{
        float[][] M = new float[N/4][N];
        float[][] alpha, beta, gamma, delta = new float[N/4][N/4];
        InterfaceMatricesRMI nodo;

        public worker(InterfaceMatricesRMI nodo, float[][] M){
            this.nodo = nodo;
            this.M = M;
        }

        public void run(){
                try {                    
                    alpha = nodo.multiplicarMatrices(M, B1, N);
                    beta = nodo.multiplicarMatrices(M, B2, N);
                    gamma = nodo.multiplicarMatrices(M, B3, N);
                    delta = nodo.multiplicarMatrices(M, B4, N);
    
                } catch (RemoteException e) {
                    System.err.println(e);
                }
            }
    }

    public static void main(String[] args) throws Exception {       
        String[] IPnodos = {"localhost", "localhost", "localhost", "localhost"};
        String[] nodosURL = new String[4];
        InterfaceMatricesRMI[] nodos = new InterfaceMatricesRMI[4];

        for (int i = 0; i < 4; i++) {
            nodosURL[i] = "rmi://" + IPnodos[i] + "/nodo" + (i+1);
            nodos[i] = (InterfaceMatricesRMI)Naming.lookup(nodosURL[i]);            
        }

        inicializarMatrices();            
        trasponerB();

        A1 = separarMatriz(A, 0);        
        A2 = separarMatriz(A, N/4);        
        A3 = separarMatriz(A, N/2);        
        A4 = separarMatriz(A, 3*N/4);        

        B1 = separarMatriz(bT, 0);        
        B2 = separarMatriz(bT, N/4);        
        B3 = separarMatriz(bT, N/2);        
        B4 = separarMatriz(bT, 3*N/4);   

        worker nodo1 = new worker(nodos[0], A1);
        worker nodo2 = new worker(nodos[1], A2);
        worker nodo3 = new worker(nodos[2], A3);
        worker nodo4 = new worker(nodos[3], A4);

        nodo1.start();
        nodo2.start();
        nodo3.start();
        nodo4.start();

        nodo1.join();
        nodo2.join();
        nodo3.join();
        nodo4.join();

        // Agrupar las operaciones del nodo 1
        agruparC(nodo1.alpha, 0, 0);
        agruparC(nodo1.beta, 0, N/4);
        agruparC(nodo1.gamma, 0, N/2);
        agruparC(nodo1.delta, 0, 3*N/4);

        // Agrupar las operaciones del nodo 2
        agruparC(nodo2.alpha, N/4, 0);
        agruparC(nodo2.beta, N/4, N/4);
        agruparC(nodo2.gamma, N/4, N/2);
        agruparC(nodo2.delta, N/4, 3*N/4);

        // Agrupar las operaciones del nodo 3
        agruparC(nodo3.alpha, N/2, 0);
        agruparC(nodo3.beta, N/2, N/4);
        agruparC(nodo3.gamma, N/2, N/2);
        agruparC(nodo3.delta, N/2, 3*N/4);

        // Agrupar las operaciones del nodo 4
        agruparC(nodo4.alpha, 3*N/4, 0);
        agruparC(nodo4.beta, 3*N/4, N/4);
        agruparC(nodo4.gamma, 3*N/4, N/2);
        agruparC(nodo4.delta, 3*N/4, 3*N/4);

        if(N == 8){
            System.out.println("Matriz A");
            imprimirMatriz(A, N, N);
    
            System.out.println("Matriz B");
            imprimirMatriz(B, N, N);

            System.out.println("Matriz C");
            imprimirMatriz(C, N, N);
        }

        System.out.println("checksum = " + calcularChecksum(C));
    }

    public static void inicializarMatrices() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = i + 2 * j;
                B[i][j] = 3 * i - j;
                bT[i][j] = 3 * i - j;
            }
        }
    }

    public static void trasponerB() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                float x = bT[i][j];
                bT[i][j] = bT[j][i];
                bT[j][i] = x;
            }
        }
    }

    public static float[][] separarMatriz(float[][] A, int inicio) {
        float M[][] = new float [N/4][N];
        for (int i = 0; i < N/4; i++) {
            for (int j = 0; j < N; j++) {
                M[i][j] = A[i + inicio][j];
            }
        }
        return M;
    }

    public static void agruparC(float[][] M, int inicio, int fin) {
        for (int i = 0; i < N/4; i++){
            for (int j = 0; j < N/4; j++) {
                C[i + inicio][j + fin] = M[i][j];
            }
        } 
    }

    public static float calcularChecksum(float[][] M) {
        float checksum = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                checksum += M[i][j];
            }
        }
        return checksum;
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