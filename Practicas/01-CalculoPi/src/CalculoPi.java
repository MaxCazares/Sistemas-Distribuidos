import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

// Forma de compilarlo: 
// javac CalculoPi.java 
 
// Forma de ejecutarlo: 
// java CalculoPi [numero de nodo]

class CalculoPi {

    static Object obj = new Object();
    static float pi = 0;

    static class Hilo extends Thread {
        Socket conexion;

        Hilo(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            try {
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                float suma = entrada.readFloat();

                synchronized (obj){
                    pi += suma;
                }

                entrada.close();
                conexion.close();

            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        
        int nodo = Integer.valueOf(args[0]);

		if(nodo == 0){
			ServerSocket servidor = new ServerSocket(20000);
            System.out.println("Nodo 0 listo ...");

			Hilo[] v = new Hilo[4];

			for(int i = 0; i < 4; i++){
				Socket conexion = servidor.accept();
				v[i] = new Hilo(conexion);
            	v[i].start();
			}

			for(int i = 0; i < 4; i++){
				v[i].join();
			}

			System.out.println("Calculo de Pi = "+ pi);
            servidor.close();

		}
        else{
			Socket conexion = null;

            while (true){
                try {
                    conexion = new Socket("localhost", 20000);
                    System.out.println("Nodo " + nodo +	" listo ...");
                    

                    break;
                } catch (Exception e) {
                    System.err.println(e);
                }
                Thread.sleep(200);
            }

			DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());	

			float suma = 0;

			for(int i = 0; i < 1_000_000; i++){
				suma += (4.0 / ((8 * i) + (2 * (nodo - 2)) + 3));
			}

			suma = (nodo % 2 == 0) ? -suma : suma;
            System.out.println("Nodo " + nodo + "termino el calculo");                

			salida.writeFloat(suma);
			salida.close();
			conexion.close();
		}
    }
}