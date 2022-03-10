import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultMatrices {

    // Arreglo con las direcciones de las maquinas virtuales
    // static String[] ipNodos = new String[]{"40.71.207.233", "52.152.136.192", "13.90.44.207"};
    static String[] ipNodos = new String[] { "localhost", "localhost", "localhost" };

    // Número de puerto base y tamaño de las matrices
    static int puerto = 50_000;
    static int N = 1000;

    // Clase que hereda los hilos. Se usara para generar los nodos servidores
    public static class Worker extends Thread {

        // Número de nodo de cada servidor y matrices temporales que realizaran la multiplicación
        int nodo;
        double[][] tempA = new double[N / 2][N];
        double[][] tempB = new double[N / 2][N];
        double[][] resC = new double[N / 2][N / 2];

        Worker(int nodo) {
            this.nodo = nodo;
        }

        public void run() {
            try {
                // Creación del servidor para aceptar las matrices del nodo 0
                System.out.println("Servidor " + nodo + " listo");
                // ServerSocket servidor = new ServerSocket(puerto);
                ServerSocket servidor = new ServerSocket(puerto + nodo);
                Socket conexion = servidor.accept();

                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                
                // Lectura de las matrices desde el nodo 0
                for (int i = 0; i < (N / 2); i++) {
                    for (int j = 0; j < N; j++) {
                        tempA[i][j] = entrada.readDouble();
                        tempB[i][j] = entrada.readDouble();
                    }
                }

                // Multiplicación de las matrices
                resC = multiplicarMatrices(tempB, tempA);

                // Escritura de las matrices al nodo 0
                for (int i = 0; i < (N / 2); i++) {
                    for (int j = 0; j < (N / 2); j++) {
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
        
        // Comprobación de los parametros requeridos para la ejecución
        if (args.length != 1) {
            System.out.println("Debe de escribir el numero de nodo como argumento (java MultMatrices <nodo>)");
            System.exit(0);
        }

        int nodo = Integer.parseInt(args[0]);

        // Lógica del nodo 0
        if (nodo == 0) {

            // Declaración de las matrices double
            double[][] A = new double[N][N];
            double[][] B = new double[N][N];
            double[][] C = new double[N][N];

            double[][] A1 = new double[N / 2][N];
            double[][] A2 = new double[N / 2][N];
            double[][] B1 = new double[N / 2][N];
            double[][] B2 = new double[N / 2][N];

            double[][] C1 = new double[N/2][N/2];
            double[][] C2 = new double[N/2][N/2];
            double[][] C3 = new double[N/2][N/2];
            double[][] C4 = new double[N/2][N/2];

            // Declaración de una variable double para almacenar el checksum de la matriz C
            double checksum = 0.0;

            try {
                // Declaración de un arreglo de sockets para conectarse a los servidores
                Socket[] clientes = new Socket[3];
                for (int i = 0; i < clientes.length; i++) {
                    // clientes[i] = new Socket(ipNodos[i], puerto);
                    clientes[i] = new Socket(ipNodos[i], puerto + i + 1);
                }

                // Inicialización de las matrices A y B
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        A[i][j] = i + 5 * j;
                        B[i][j] = 5 * i - j;
                    }
                }

                // Generación de la matriz traspuesta de B
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < i; j++) {
                        double x = B[i][j];
                        B[i][j] = B[j][i];
                        B[j][i] = x;
                    }
                }

                // Separación de la matriz A en A1 y A2
                for (int i = 0; i < (N / 2); i++) {
                    for (int j = 0; j < N; j++) {
                        A1[i][j] = A[i][j];
                        A2[i][j] = A[i + N / 2][j];
                    }
                }

                // Separación de la matriz B en B1 y B2
                for (int i = 0; i < (N / 2); i++) {
                    for (int j = 0; j < N; j++) {
                        B1[i][j] = B[i][j];
                        B2[i][j] = B[i + N / 2][j];
                    }
                }

                // Envío de las matrices a cada nodo servidor
                enviarMatrices(clientes[0], A1, B1);
                C1 = recibirMatriz(clientes[0]);

                enviarMatrices(clientes[1], A1, B2);
                C2 = recibirMatriz(clientes[1]);

                enviarMatrices(clientes[2], A2, B1);
                C3 = recibirMatriz(clientes[2]);

                // Cálculo del cuadrante C4 de la matriz C
                C4 = multiplicarMatrices(A2, B2);

                // Agrupamiento de los 4 cuadrantes en la matriz C
                for (int i = 0; i < N / 2; i++){
                    for (int j = 0; j < N / 2; j++) {
                        C[i][j] = C1[i][j];
                        C[i][j + N / 2] = C2[i][j];
                        C[i + N / 2][j] = C3[i][j];
                        C[i + N / 2][j + N / 2] = C4[i][j];
                    }
                }                

                // Impresión en consola de las matrices A, B y C cuando N es 8
                if(N == 8){                  
                    System.out.println("Matriz A");
                    imprimirMatriz(A, N, N);

                    System.out.println("Matriz B");
                    imprimirMatriz(B, N, N);

                    System.out.println("Matriz C");
                    imprimirMatriz(C, N, N);
                }

                // Cálculo e impresión del checksum de la matriz C
                checksum = calcularChecksum(C);                
                System.out.println("Este es el checksum: " + checksum);

            } catch (Exception e) {
                System.err.println(e);
            }

        } else {
            // Switch encargado de iniciar cada nodo servidor
            switch (nodo) {
                case 1:
                    try {
                        Worker servidor = new Worker(1);
                        servidor.start();
                        servidor.join();
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                    break;

                case 2:
                    try {
                        Worker servidor = new Worker(2);
                        servidor.start();
                        servidor.join();
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                    break;

                case 3:
                    try {
                        Worker servidor = new Worker(3);
                        servidor.start();
                        servidor.join();
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                    break;

                default:
                    System.out.println("Numero de nodo incorrecto");
                    break;
            }
        }
    }

    // Función para calcular el checksum de la matriz C
    public static double calcularChecksum(double[][] M) {
        double checksum = 0.0;
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M.length; j++) {
                checksum += M[i][j];
            }
        }
        return checksum;
    }

    // Función para recibir la matriz resultante desde cada nodo servidor
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

    // Función para enviar las dos matrices a multiplicar a cada nodo servidor
    public static void enviarMatrices(Socket cliente, double[][] A, double[][] B) throws Exception {
        DataOutputStream salida = new DataOutputStream(cliente.getOutputStream());

        for (int i = 0; i < (N / 2); i++) {
            for (int j = 0; j < N; j++) {
                salida.writeDouble(A[i][j]);
                salida.writeDouble(B[i][j]);
            }
        }
    }

    // Función para imprimir cualquier matriz
    public static void imprimirMatriz(double[][] m, int filas, int columnas) {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println("\n");
        }
    }

    // Función para calcular el producto de dos matrices
    public static double[][] multiplicarMatrices(double[][] A, double[][] B) {
        double[][] C = new double[N / 2][N / 2];

        for (int i = 0; i < (N / 2); i++) {
            for (int j = 0; j < (N / 2); j++) {
                for (int k = 0; k < N; k++) {
                    C[i][j] += A[i][k] * B[j][k];
                }
            }
        }
        return C;
    }
}