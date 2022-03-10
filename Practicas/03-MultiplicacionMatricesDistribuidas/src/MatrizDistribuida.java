import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.net.ServerSocket;
import java.net.Socket;

public class MatrizDistribuida {
    static int numNodos = 3;
    static String[] ip = new String[] { "localhost", "localhost", "localhost" };
    static int N = 8;
    static double Checksum = 0;
    static int puerto = 20000;
  
    static double[][] A = new double[N][N];
    static double[][] B = new double[N][N];
    static double[][] C = new double[N][N];
    static double[][] C1 = new double[N][N];
    static double[][] C2 = new double[N][N];
    static double[][] C3 = new double[N][N];
    static double[][] C4 = new double[N][N];

    static double[][] A1 = new double[N / 2][N];
    static double[][] A2 = new double[N / 2][N];
    static double[][] B1 = new double[N / 2][N];
    static double[][] B2 = new double[N / 2][N];


    public static class Worker extends Thread {
        int nodo;
        double[][] auxA = new double[N / 2][N];
        double[][] auxB = new double[N / 2][N];
        double[][] auxC = new double[N / 2][N / 2];

        Worker(int nodo) {
            this.nodo = nodo;
        }

        public void run() {
            try {
                System.out.println("Iniciando Nodo " + this.nodo);
                ServerSocket server = new ServerSocket(puerto + nodo);
                Socket conexion = server.accept();
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                auxA = recibirNBR(entrada, N / 2, N);
                auxB = recibirNBR(entrada, N / 2, N);

                auxC = multiplicaMatriz(auxA, auxB, N / 2, N);
                enviarNB(salida, auxC, N / 2, N / 2);

                server.close();
            } catch (Exception e) {
                System.err.println(e);
            }

        }

    }

    public static void main(String[] args) {

        int nodo = 0;// args

        if (args.length != 1) {
            System.out.println(
                    "Error!! Parametros incorrectos\nCorrecto uso: java MatrizDistribuida <numero entero><ip>");
            System.exit(0);
        } else {
            nodo = Integer.parseInt(args[0]);
            if (nodo > numNodos || nodo < 0) {
                System.out.println("Error!! Nodo incorrecto\nEl nodo debe de ser entre 0 y " + numNodos);
                System.exit(0);
            }
        }

        if (nodo == 0) {
            try {
                
                inicializarMatriz(N, N);

                // for (int i = 0; i < N; i++) {
                //     for (int j = 0; j < N; j++) {
                //         A[i][j] = i + 5 * j;
                //         B[i][j] = 5 * i - j;
                //     }
                // }

                System.out.println("A\n");
                imprimirMatriz(A, N, N);

                System.out.println("B\n");
                imprimirMatriz(B, N, N);
                
                // for (int i = 0; i < N; i++){
                //     for (int j = 0; j < i; j++){
                //         double x = B[i][j];
                //         B[i][j] = B[j][i];
                //         B[j][i] = x;
                //         System.out.println("entra al for");
                //     }
                // }
                
                B = traspuesta(N,N,B);

                System.out.println("B Traspuesta\n");
                imprimirMatriz(B, N, N);

                for (int i = 0; i < N / 2; i++) {
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

                Socket[] cliente = new Socket[3];
                for (int i = 0; i < cliente.length; i++) {
                    cliente[i] = new Socket(ip[i], puerto + i + 1);
                }

                DataOutputStream enviar = new DataOutputStream(cliente[0].getOutputStream());
                DataInputStream recibir = new DataInputStream(cliente[0].getInputStream());
                enviarNB(enviar, A1, N / 2, N);
                enviarNB(enviar, B1, N / 2, N);

                C1 = recibirNBR(recibir, N / 2, N / 2);

                DataOutputStream enviar2 = new DataOutputStream(cliente[1].getOutputStream());
                DataInputStream recibir2 = new DataInputStream(cliente[1].getInputStream());
                enviarNB(enviar2, A1, N / 2, N);
                enviarNB(enviar2, B2, N / 2, N);

                C2 = recibirNBR(recibir2, N / 2, N / 2);

                DataOutputStream enviar3 = new DataOutputStream(cliente[2].getOutputStream());
                DataInputStream recibir3 = new DataInputStream(cliente[2].getInputStream());
                enviarNB(enviar3, A2, N / 2, N);
                enviarNB(enviar3, B1, N / 2, N);

                C3 = recibirNBR(recibir3, N / 2, N / 2);

                C4 = multiplicaMatriz(A2, B2, N / 2, N);

                // System.out.println("C1\n");
                // imprimirMatriz(C1, N / 2, N / 2);
                // System.out.println("C2\n");
                // imprimirMatriz(C2, N / 2, N / 2);
                // System.out.println("C3\n");
                // imprimirMatriz(C3, N / 2, N / 2);
                // System.out.println("C4\n");
                // imprimirMatriz(C4, N / 2, N / 2);

                for (int i = 0; i < N / 2; i++) {
                    for (int j = 0; j < N / 2; j++) {
                        C[i][j] = C1[i][j];
                        C[i][j + N / 2] = C2[i][j];
                        C[i + N / 2][j] = C3[i][j];
                        C[i + N / 2][j + N / 2] = C4[i][j];
                    }
                }

                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        Checksum += C[i][j];
                    }
                }
                // System.out.println("Suma Checksum=" + Checksum);
                // System.out.println("\n");

                // if (N == 8) {
                //     System.out.println("Matriz A ");
                //     imprimirMatriz(A, N, N);
                //     System.out.println("Matriz B ");
                //     imprimirMatriz(B, N, N);
                //     System.out.println("Matriz C ");
                //     imprimirMatriz(C, N, N);
                // }

                for (int i = 0; i < cliente.length; i++) {
                    cliente[i].close();
                }

            } catch (Exception e) {
                System.err.println(e);
            }
        } else {

            Worker servidor = new Worker(nodo);
            servidor.start();

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

    // TRANSPONE UNA MATRIZ
    static double[][] traspuesta(int filas, int columnas, double[][] uno) {
        double[][] M = new double[filas][columnas];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                double x = uno[i][j];
                M[i][j] = uno[j][i];
                M[j][i] = x;
            }
        }
        return M;
    }

    // INICIALIZA LAS MATRICES A Y B
    static void inicializarMatriz(int filas, int columnas) {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                A[i][j] = i + 5 * j;
                B[i][j] = 5 * i - j;
            }
        }
    }

    // MULTIPLICA 2 MATRICES RECTANGULARES DEL MISMO TAMAÑO -> M1[N][M] X M1[N][M] =
    // C[N][N]
    static double[][] multiplicaMatriz(double[][] M1, double[][] M2, int filas, int columnas) {
        double[][] resultado = inicializarMatrizVacia(filas, filas);

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < filas; j++) {
                for (int k = 0; k < columnas; k++) {
                    resultado[i][j] += M1[i][k] * M2[j][k];
                }
            }
        }
        return resultado;
    }

    static double[][] inicializarMatrizVacia(int filas, int columnas) {
        double[][] inicializada = new double[filas][columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                inicializada[i][j] = 0;
            }
        }
        return inicializada;
    }

    // ENVIA UNA MATRIZ DE NuMEROS CON BYTEBUFFER() -> EMPACADOS
    static void enviarNB(DataOutputStream enviar, double[][] M, int filas, int columnas) throws Exception {
        // long inicio = System.currentTimeMillis();
        // Tamaño del ByteBuffer
        ByteBuffer n = ByteBuffer.allocate(filas * columnas * 8);

        // Agregamos los elementos
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                n.putDouble(M[i][j]);
            }
        }

        // Convertimos en arreglo
        byte[] conv = n.array();

        enviar.write(conv); // --->Enviamos
        // long fin = System.currentTimeMillis();
        // System.out.println("Termino en "+ (fin-inicio));
    }

    // RECIBE UNA MATRIZ DE NúMEROS POR BYTEBUFFER() -> EMPACADOS
    static double[][] recibirNBR(DataInputStream recibido, int filas, int columnas) throws Exception {
        double[][] matrizR = new double[filas][columnas];
        // long inicio = System.currentTimeMillis();

        byte[] a = new byte[filas * columnas * 8];
        read(recibido, a, 0, filas * columnas * 8);

        ByteBuffer b = ByteBuffer.wrap(a);

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                matrizR[i][j] = b.getDouble();
            }
        }

        // long fin = System.currentTimeMillis();
        // System.out.println("Termino en "+ (fin-inicio));
        return matrizR;
    }

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }
}
