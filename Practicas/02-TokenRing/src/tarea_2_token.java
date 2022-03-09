// 1) Certificado autofirmado
// keytool -genkeypair -keyalg RSA -alias certificado_servidor -keystore keystore_servidor.jks -storepass 1234567

// 2) Obtener el certificado contenido en el keystore
// keytool -exportcert -keystore keystore_servidor.jks -alias certificado_servidor -rfc -file certificado_servidor.pem -storepass 1234567

// 3) Creación de un keystore para el cliente
// keytool -import -alias certificado_servidor -file certificado_servidor.pem -keystore keystore_cliente.jks -storepass 123456

// 4) Compilar el programa
// javac Token.java

// 5) Ejecutar los diferentes nodos
// java Token [# de Nodo] [ip del servidor]


//Uso e implementacion de Secure Sockets Layer o SSL
//Nota de Luis = Precisamente es con estas librerias que se implementará la seguridad
import javax.net.ssl.*;

//Para el trabajo con los Sockets
import java.net.*;

//Manejo de la salida y entrada de nuestro Stream
import java.io.*;

//Para cmplementos a la tarea
import javax.swing.*;

//Iniciamos la clase llamada tarea_2_token
class tarea_2_token {
	//Para el uso de los nodos
	static int nodo_unidad_stg;

	//Para la dirección ip
	static String ip_address;

	//Indicar el estado del proceso
	static boolean indicador_de_estado = true;

	//Nota Cipri = Acorde con los requerimientos se necesita un número de 16 bits
	//En Moodle nos indica que el nodo cero debe mandar el valor 0 al nodo 1 de forma inicial.
	static short token_stg = -1; 
	static DataInputStream input_stream_stg;
	static DataOutputStream output_stream_stg;

	
	static class thread_gate extends Thread {
		public void run() {

			//Definición del socket para el server
			Socket conn_server_secure;
			ServerSocket servidor_unit_stg;
			SSLServerSocketFactory ssl_server_socket_factory_stg; 

			// Para las ventanas que mostraremos en el programa
			JFrame jFrame = new JFrame();

			try {

				//Implementacion de la parte de seguridad para el server
				//Nos pide el nombre de nuestro jks dedicado al server
				//Nos pide el pasword que se estipuló
				System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
                System.setProperty("javax.net.ssl.keyStorePassword", "1234567");

				ssl_server_socket_factory_stg = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
				servidor_unit_stg = ssl_server_socket_factory_stg.createServerSocket(40500 + nodo_unidad_stg);
				conn_server_secure = servidor_unit_stg.accept();
				input_stream_stg = new DataInputStream(conn_server_secure.getInputStream());
			
			} catch (Exception e) {

				JOptionPane.showMessageDialog(jFrame, "Error" + e);
				
			}
		}
	}

	public static void main(String[] params_stg) throws Exception {
		// Para las ventanas que mostraremos en el programa
        JFrame jFrame = new JFrame();

		//Implementacion de la parte de seguridad para el el cliente
		System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

		/*Nota para ejecutar debemos hacerlo como "java tarea_2_token numero_nodo"*/

		SSLSocketFactory ssl_client_socket_factory_stg;
		Socket conn_server_secure = null;
		thread_gate thread_gate_stg;

		nodo_unidad_stg = Integer.valueOf(params_stg[0]);
        ip_address = "localhost";
		int new_Port_unidad_calculo = (nodo_unidad_stg + 1) % 3;

		if(params_stg.length > 1){

			System.out.println("---------------------------------");
			System.out.println("Ingreso parametros adicionales, por favor solo ingrese el numero de nodo");
			System.out.println("---------------------------------");
			JOptionPane.showMessageDialog(jFrame, "Error en los parametros en consola");
			System.exit(1);

		}else if(params_stg.length == 0){

			System.out.println("---------------------------------");
			System.out.println("No ingreso ningun valor como parametro, por favor ingrese el numero de nodo");		
			System.out.println("---------------------------------");
			JOptionPane.showMessageDialog(jFrame, "No ingreso ningun parametro en la consola");	
			System.exit(1);

		}else if(params_stg.length == 1){

        	System.out.println("---------------------------------");
			System.out.println("Excelente!!!, el programa se ejecutara correctamente");
			System.out.println("---------------------------------");
			JOptionPane.showMessageDialog(jFrame, "Listo para iniciar!");
		}

		thread_gate_stg = new thread_gate();
		thread_gate_stg.start();

		for(;;){
			try {

				ssl_client_socket_factory_stg = (SSLSocketFactory) SSLSocketFactory.getDefault();
				conn_server_secure = (SSLSocket) ssl_client_socket_factory_stg.createSocket(ip_address, 40500 + new_Port_unidad_calculo);
				break;

			}catch (Exception e) {
				Thread.sleep(500);
			}
		}

		output_stream_stg = new DataOutputStream(conn_server_secure.getOutputStream());
		thread_gate_stg.join();

		/*
		    Ciclo de validacion.
		*/

		while(true) {
			if(nodo_unidad_stg != 0) {                
                token_stg = input_stream_stg.readShort();
                token_stg++;

				//En caso de que queramos forzar que no rebase los 500
				/*
					if(token_stg > 500) {
						break;
					}
				*/

				System.out.println("---------------------------------");
				System.out.println("\n NODO_[" + nodo_unidad_stg + "/" + ip_address + "] \t TOKEN_[" + token_stg + "] \n");
				System.out.println("---------------------------------");
			} else {
				if(indicador_de_estado != true) {
					token_stg = input_stream_stg.readShort();
					token_stg++;

					System.out.println("---------------------------------");
					System.out.println("\n NODO_[" + nodo_unidad_stg + "/" + ip_address +  "] \t TOKEN_[" + token_stg + "] \n");
					System.out.println("---------------------------------");
				}else if(indicador_de_estado) {
					indicador_de_estado = !indicador_de_estado;
				}
			}

			//La condición evalua cuando el nodo es igual o mayor a 500 para el cierre
			if(token_stg >= 500 && nodo_unidad_stg == 0) {
				JOptionPane.showMessageDialog(jFrame, "Se alcanzo el tope establecido!!!");
				break;
			}
			output_stream_stg.writeShort(token_stg);	
		}

		//Debemos cerrar nuestra conexion
		//Debemos cerrar el apartado de la entrada de nuestro input y Output Stream

		conn_server_secure.close();
	    input_stream_stg.close();
		output_stream_stg.close();
	}
}