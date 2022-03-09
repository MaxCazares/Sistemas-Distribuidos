import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultMatrices {

    // static String[] ipNodos = new String[]{"40.71.207.233", "52.152.136.192", "13.90.44.207"};
    static String[] ipNodos = new String[]{"localhost", "localhost", "localhost"};
    static int puerto = 50_000;
    static int N = 8;

    public static class Worker extends Thread{

        int nodo;      
        double[][] tempA = new double[N/2][N];
        double[][] tempB = new double[N/2][N];
        double[][] resC = new double[N/2][N/2];

        Worker(int nodo){
            this.nodo = nodo;
        }

        public void run(){  
            try {
                System.out.println("Servidor " + nodo + " listo");
                // ServerSocket servidor = new ServerSocket(puerto);
                ServerSocket servidor = new ServerSocket(puerto + nodo);
                Socket conexion = servidor.accept();

                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

                for (int i = 0; i < (N / 2); i++) {
                    for (int j = 0; j < N; j++) {
                        tempA[i][j] = entrada.readDouble();
                        tempB[i][j] = entrada.readDouble();
                    }
                } 

                resC = multiplicaMatrices(tempB, tempA);
                
                System.out.println("Matriz A");
                imprimirMatriz(tempA, (N/2), N);

                System.out.println("\nMatriz B");
                imprimirMatriz(tempB, (N/2), N);

                System.out.println("\nMatriz C");
                imprimirMatriz(resC, (N/2), (N/2));

                for (int i = 0; i < (N/2); i++) {
                    for (int j = 0; j < (N/2); j++) {
                        salida.writeDouble(resC[i][j]);
                    }
                }       
                
                servidor.close();

            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("Debe de escribir el numero de nodo como argumento (java MultMatrices <nodo>)");
            System.exit(0);
        }

        int nodo = Integer.parseInt(args[0]);      

        if(nodo == 0){

            double[][] A = new double[N][N];
            double[][] B = new double[N][N];
            double[][] C1 = new double[N][N];
            double[][] C2 = new double[N][N];
            double[][] C3 = new double[N][N];
            double[][] C4 = new double[N][N];

            double[][] A1 = new double[N/2][N];
            double[][] A2 = new double[N/2][N];
            double[][] B1 = new double[N/2][N];
            double[][] B2 = new double[N/2][N];

            try {                
                Socket[] clientes = new Socket[3];                
                for (int i = 0; i < clientes.length; i++) {
                    // clientes[i] = new Socket(ipNodos[i], puerto);
                    clientes[i] = new Socket(ipNodos[i], puerto + i + 1);
                }
                
                // Inicializa A y B
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        A[i][j] = i + 5 * j;
                        B[i][j] = 5 * i - j;
                    }
                }

                // Transpuesta de B
                for (int i = 0; i < N; i++){
                    for (int j = 0; j < i; j++){
                        double x = B[i][j];
                        B[i][j] = B[j][i];
                        B[j][i] = x;
                    }
                }

                // Genera A1 y B2
                for (int i = 0; i < (N / 2); i++) {
                    for (int j = 0; j < N; j++) {
                        A1[i][j] = A[i][j];
                        A2[i][j] = A[i + N / 2][j];
                    }
                }
        
                // Genera B1 y B2
                for (int i = 0; i < (N / 2); i++) {
                    for (int j = 0; j < N; j++) {
                        B1[i][j] = B[i][j];
                        B2[i][j] = B[i + N / 2][j];
                    }
                }

                enviarMatrices(clientes[0], A1, B1);                
                C1 = recibirMatriz(clientes[0]);

                enviarMatrices(clientes[1], A1, B2);                
                C2 = recibirMatriz(clientes[1]);

                enviarMatrices(clientes[2], A2, B1);                
                C3 = recibirMatriz(clientes[2]);

                C4 = multiplicaMatrices(A2, B2);
                
                System.out.println("Matriz C1");
                imprimirMatriz(C1, (N/2), (N/2));

                System.out.println("Matriz C2");
                imprimirMatriz(C2, (N/2), (N/2));

                System.out.println("Matriz C3");
                imprimirMatriz(C3, (N/2), (N/2));

                System.out.println("Matriz C4");
                imprimirMatriz(C4, (N/2), (N/2));

            } catch (Exception e) {
                System.err.println(e);
            }

        }else{
            switch(nodo){
                case 1:
                    Worker servidor = new Worker(1);
                    servidor.start();
    
                break;
    
                case 2:
                    Worker servidor2 = new Worker(2);
                    servidor2.start();
    
                break;
    
                case 3:
                    Worker servidor3 = new Worker(3);
                    servidor3.start();               
    
                break;
    
                default: 
                    System.out.println("Numero de nodo incorrecto");
                break;
            }
        }
    }

    public static double[][] recibirMatriz(Socket cliente) throws Exception {      
        double[][] C = new double[N][N];
        DataInputStream entrada = new DataInputStream(cliente.getInputStream());
        for (int i = 0; i < (N / 2); i++) {
            for (int j = 0; j < (N / 2); j++) {
                C[i][j] = entrada.readDouble();
            }
        }
        return C;
    }

    public static void enviarMatrices(Socket cliente, double[][] A, double[][] B) throws Exception {   
        DataOutputStream salida = new DataOutputStream(cliente.getOutputStream());
        
        for (int i = 0; i < (N/2); i++) {
            for (int j = 0; j < N; j++) {
                salida.writeDouble(A[i][j]);
                salida.writeDouble(B[i][j]);
            }
        }                
    }

    public static void imprimirMatriz(double[][] m, int filas, int columnas) {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println("\n");
        }
    }

    public static double[][] multiplicaMatrices(double[][] A, double[][] B) {
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
}