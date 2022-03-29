import java.rmi.Naming;

public class ServidorMatricesRMI {
    public static void main(String[] args) throws Exception {

        if(args.length < 1){
            System.out.println("Se necesita un numero entre 1 y 4 para ejecutar el servidor");
            System.exit(1);
        }else 
            if(args.length > 1){
                System.out.println("Solo se necesita un numero entre 1 y 4 para ejecutar el servidor");
                System.exit(1);
            }else{
                if(Integer.parseInt(args[0]) < 1 || Integer.parseInt(args[0]) > 4){
                    System.out.println("Solo se necesita un numero entre 1 y 4 para ejecutar el servidor");
                    System.exit(1);
                }else{
                    System.out.println("Iniciando servidor " + args[0] + " ...");
                }
            }

        String url = "rmi://localhost/nodo" + args[0];
        ClaseMatricesRMI obj = new ClaseMatricesRMI();
        Naming.rebind(url, obj);
    }
}