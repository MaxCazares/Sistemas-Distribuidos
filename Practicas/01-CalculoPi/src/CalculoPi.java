import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CalculoPi {

    static double Pi = 0;

    public static void main(String[] args) {

        int numNodo = Integer.parseInt(args[0]);

        switch (numNodo) {
            case 0:
                System.out.println("Cliente listo ...");
                try {

                    Mensajero m1 = new Mensajero(50_001);
                    Mensajero m2 = new Mensajero(50_002);
                    Mensajero m3 = new Mensajero(50_003);
                    Mensajero m4 = new Mensajero(50_004);

                    m1.start();
                    m2.start();
                    m3.start();
                    m4.start();

                    m1.join();
                    m2.join();
                    m3.join();
                    m4.join();

                    System.out.println("\nCalculo de Pi: " + Pi);
                    break;

                } catch (Exception e) {
                    System.err.println(e);
                }

            case 1:
                System.out.println("Servidor " + numNodo + " listo ...");
                Servidor servidor1 = new Servidor(1, 50_001);
                servidor1.empieza();
                break;

            case 2:
                System.out.println("Servidor " + numNodo + " listo ...");
                Servidor servidor2 = new Servidor(2, 50_002);
                servidor2.empieza();
                break;

            case 3:
                System.out.println("Servidor " + numNodo + " listo ...");
                Servidor servidor3 = new Servidor(3, 50_003);
                servidor3.empieza();
                break;

            case 4:
                System.out.println("Servidor " + numNodo + " listo ...");
                Servidor servidor4 = new Servidor(4, 50_004);
                servidor4.empieza();
                break;

            default:
                System.out.println("Escriba un numero entre 0 y 4");
                break;

        }
    }

    static class Mensajero extends Thread {

        int puerto;
        static Object obj = new Object();

        Mensajero(int puerto) {
            this.puerto = puerto;
        }

        public void run() {
            try {
                while (true) {
                    try {
                        Socket conexion = new Socket("localhost", puerto);
                        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                        double resultado = entrada.readDouble();

                        synchronized (obj) {
                            Pi += resultado;
                        }

                        System.out.println(puerto + " --- " + Pi);
                        conexion.close();
                        break;

                    } catch (Exception e) {
                        Thread.sleep(100);
                    }
                }
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
    }

    static class Servidor {

        int numNodo, puerto;

        Servidor(int numNodo, int puerto) {
            this.numNodo = numNodo;
            this.puerto = puerto;
        }

        public void empieza() {
            try {
                ServerSocket servidor = new ServerSocket(puerto);
                Socket conexion = servidor.accept();
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

                double resultado = 0;
                double numNodoDouble = Double.valueOf(numNodo);

                for (double i = 0; i < 1_000_000; i++) {
                    resultado = resultado + (4 / ((8 * i) + (2 * (numNodoDouble - 2)) + 3));
                }

                resultado = (numNodo % 2 == 0) ? -resultado : resultado;

                salida.writeDouble(resultado);
                System.out.println(resultado);

                conexion.close();
                servidor.close();

            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
    }
}