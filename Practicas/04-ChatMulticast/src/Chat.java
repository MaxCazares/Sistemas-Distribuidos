
/*
************************************************
*
*_______________________________________________
* Datos de la práctica.
*_______________________________________________
*
* Materia: Desarrollo de Sistemas distribuidos
*
* Profesor: Carlos Pineda Guerrero
*
* Alumnos:
*           Cazares Martínez Maximiliano.
*           Chavarría Vázquez Luis Enrique.
*           Cipriano Damián Sebastián.
*
* Equipo 2
*
* Grupo: 4CV11
*
* Practica 04: Chat Multicast
* 
************************************************
*/


//Importamos nuestra bibliotecas.
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Chat {

    static class Worker extends Thread{

        public void run() {
            
                try {
                    System.setProperty("java.net.preferIPv4Stack", "true");
                    MulticastSocket socket = new MulticastSocket(50_000);
                    InetSocketAddress grupo = new InetSocketAddress(InetAddress.getByName("230.0.0.0"), 50_000);
                    NetworkInterface netInter = NetworkInterface.getByName("em1");
                    socket.joinGroup(grupo, netInter);

                    while(true){
                        byte[] buffer = recibe_mensaje_multicast(socket, 128);
                        String mensaje = (new String (buffer,"UTF-8")).replaceAll("\0","");
                        System.out.println("\n\n" + mensaje + "\n");
                        
                        System.out.print("Ingrese el mensaje a enviar: ");
                    }

                } catch (Exception e) {
                    System.err.println(e);
                }
        }
    }
    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");

        //Mensaje en caso de que los parametros no sean adecuados o cuando
        //los parametros ingresados son los adecuados.
        if(args.length != 1){
            System.out.println("\n--------------------------------------------------");
            System.out.println("Por favor, debe escribir el nombre del usuario.");
            System.out.println("--------------------------------------------------\n");

            if(args.length == 0){
                System.out.println("***No escribi\u00f3 ning\u00fan par\u00e1metro.");
                System.exit(0);

            }else if(args.length > 1){
                System.out.println("***Ingres\u00f3 par\u00e1metros adicionales.");
                System.exit(0);
            }
            System.exit(0);

        }else{
            System.out.println("\n--------------------------------------------------");
            System.out.println("Ha accedido al chat");
            System.out.println("--------------------------------------------------\n");
        }

        new Worker().start();
        String nombre = args[0];

        System.out.print("Ingrese el mensaje a enviar: ");
        Scanner in = new Scanner(System.in);
        String mensaje = "";
        while(true){
            mensaje = nombre + " dice " + in.nextLine();
            byte buffer[] = mensaje.getBytes(StandardCharsets.UTF_8);
            envia_mensaje_multicast(buffer, "230.0.0.0", 50_000);
        }

    }

    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
    }

    
    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }
}