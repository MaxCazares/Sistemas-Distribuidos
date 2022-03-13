
/*
************************************************
*
*__________________________________________________
* Datos de la práctica.
*__________________________________________________
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
* Practica 03: Multiplicación de matrices usando el paso de mensajes.
* 
************************************************
*/

//Definición de la bibliotecas que hemos de emplear
/*
* Hay que definir los inputs y los outputs del stream, por lo que usaremos
* estas bibliotecas.
*/
import java.io.DataInputStream;
import java.io.DataOutputStream;

/*
* Para las excepciones y para los sockets.
*/
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultMatricesV2 {

    //Nota: Arreglo de las direcciones para nuestras maquinas virtuales
    //Debemos considerar que estan ajustadas con...
    // static String[] ipNodos = new String[]{"52.188.58.21", "13.82.26.88", "40.71.2.80"};
    static String[] ipNodos = new String[]{"localhost", "localhost", "localhost"};
    
    /* 
    * Número de los puertos base y definición del tamaño de la matriz.
    * El programa acorde con los requerimientos debe ser probado con los casos de 
    * N = 8 // N = 1000
    */

    static int puerto = 50_000;
    static int N = 1000;

    // Clase para la parte de heredar los hilos.
    // Se usara para generar los nodos servidores.

    /*
    * En las asesorias usted nos comento que como no estaba
    * especificado la parte de los hilos podiamos o no usarlos
    * , más nosotros hemos decidido su uso ya que nos sentimos mucho
    * más comodos.
    */
    
    public static class workerMatrixMul extends Thread{
        /*
        * Número de nodo de cada uno de los servers 
        * y matrices temporales que realizaran nuestros movimientos y procesos
        * de las matrices correspondientes.
        */
        String ipAdd;
        int nodo;      
        double[][] tempA = new double[N/2][N];
        double[][] tempB = new double[N/2][N];
        double[][] resC = new double[N/2][N/2];

        //workerMatrixMul, debe tener los siguientes parametros
        /*
        * La ip, el nodo, A para el manejo de los valores de la matriz 
        * en turno y B sigue una estrucutra identica.
        */
        workerMatrixMul(String ip, int nodo, double[][] A, double[][] B){
            //Definición del nodo
            this.nodo = nodo;
            //Definición para las matrices
            this.tempA = A;
            this.tempB = B;
            //Nota == Definición del ipAdd
            this.ipAdd = ip;
        }

        public void run(){  
            try {
                //Manejo de la conexión del servidor, debemos pasar el ipAdd
                //Definamos el puerto + estructura del nodo como los parametros
                //para el Socket
                /*ServerSocket servidor = new ServerSocket(puertoDef);*/
                Socket conexion = new Socket(this.ipAdd, 50_000 + this.nodo);

                //Def para la entrada y salida de los Streams
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

                //Def del auxiliar
                double[][] auxC = new double[N/2][N/2];

                //Manejo y envio de la matriz
                //Parametros ==> salida, temp(A,B)//
                try{
                    enviarMatriz(salida, tempA);
                    enviarMatriz(salida, tempB);
                    //Paso del auxiliar, especificamente la longitud
                    //Notar la forma del manejo por favor, para no producir fallos.
                    int n = auxC.length;
                    int m = auxC[0].length;
                    //Recibimos la Matrix, con la entrada y los aux
                    auxC = recibirMatriz(entrada, n, m);
                }catch (Exception e){
                    //En caso de...
                    System.out.println (e.getMessage());
                }
                this.resC = auxC;
                if(N == 8){
                    imprimirMatriz(auxC);
                }
                //Cierre de la conexión.
                conexion.close();

            } catch (IOException e) {
                //En caso de...
                System.err.println(e);
            }
        }
    }

    public static void main(String[] args) {        
        //Mensaje en caso de que los parametros no sean adecuados o cuando
        //los parametros ingresados son los adecuados.
        if(args.length != 1){
            System.out.println("--------------------------------------------------");
            System.out.println("Por favor, debe de escribir el numero de nodo como un argumento en la consola (java MultMatrices <nodo>)");
            System.out.println("--------------------------------------------------");
            System.exit(0);
        }
        
        if(args.length == 0){
            System.out.println("--------------------------------------------------");
            System.out.println("No escribio ningun parametro.");
            System.out.println("--------------------------------------------------");
            System.exit(0);
        }
        
        //Def nodo y lo parseamos
        int nodo = Integer.parseInt(args[0]);      
        if(args.length == 1){
            if(nodo == 0 || nodo == 1 || nodo == 2 || nodo == 3){
                System.out.println("--------------------------------------------------");
                System.out.println("Parametro ingresado de manera correcta");
                System.out.println("--------------------------------------------------");

            }else{
                System.out.println("--------------------------------------------------");
                System.out.println("Parametro fuera del rango de nodos, deben ir de 0-3");
                System.out.println("--------------------------------------------------");
                System.exit(0);
            }
        }     


        //Lógica del nodo 0 si tenemos como valor el 0
        if(nodo == 0){
            //Declaraciones como double considenrando los requerimientos
            //básicos de la práctica en el moodle.
            //Para AB
            double[][] A = new double[N][N];
            double[][] B = new double[N][N];

            //Para las Cs
            double[][] C1 = new double[N][N];
            double[][] C2 = new double[N][N];
            double[][] C3 = new double[N][N];
            double[][] C4 = new double[N][N];
            double[][] C = new double[N][N];

            //Para las ABs
            double[][] A1 = new double[N/2][N];
            double[][] A2 = new double[N/2][N];
            double[][] B1 = new double[N/2][N];
            double[][] B2 = new double[N/2][N];

            /* 
            * Inicialización para la A y B con for
            */
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    A[i][j] = i + 5 * j;
                    B[i][j] = 5 * i - j;
                }
            }

            /* 
            * transpuesta de la B...
            */
            for (int i = 0; i < N; i++){
                for (int j = 0; j < i; j++){
                    double x = B[i][j];
                    B[i][j] = B[j][i];
                    B[j][i] = x;
                }
            }

            /* 
            * Generación de A1 Y A2...
            */
            for (int i = 0; i < (N / 2); i++) {
                for (int j = 0; j < N; j++) {
                    A1[i][j] = A[i][j];
                    A2[i][j] = A[i + N / 2][j];
                }
            }
    
            /* 
            * Generación de B1 y B2...
            */
            for (int i = 0; i < (N / 2); i++) {
                for (int j = 0; j < N; j++) {
                    B1[i][j] = B[i][j];
                    B2[i][j] = B[i + N / 2][j];
                }
            }

            try {       
                //Manejo del workerMatrixMul, Nota: podriamos intentar hacer el paso de los
                //paremtros sin que sea manual, pero en si no afecta mucho...         
                workerMatrixMul Nodo1 = new workerMatrixMul(ipNodos[0], 1, A1, B1);
                workerMatrixMul Nodo2 = new workerMatrixMul(ipNodos[1], 2, A1, B2);
                workerMatrixMul Nodo3 = new workerMatrixMul(ipNodos[2], 3, A2, B1);

                //Iniciamos nuestros nodos
                Nodo1.start();
                Nodo2.start();
                Nodo3.start();

                //Hacemos los join de los nodos
                Nodo1.join();
                Nodo2.join();
                Nodo3.join();

                //importante para ... entre ello multiplicar las matrices A2 y B2
                C1 = Nodo1.resC;
                C2 = Nodo2.resC;
                C3 = Nodo3.resC;
                C4 = multiplicarMatrices(A2, B2);

                //Operaciones base en Cs y C principal.
                for(int i = 0; i < C1.length; i++){
                    for(int j = 0; j < C1[0].length; j++){
                        C[i][j] = C1[i][j];
                    }
                }
                for(int i = 0; i < C2.length; i++){
                    for(int j = 0; j < C2.length; j++){
                        C[i][j + C2.length] = C2[i][j];
                    }
                }
                for(int i = 0; i < C3.length; i++){
                    for(int j = 0; j < C3[0].length; j++){
                        C[i + C3.length][j] = C3[i][j];
                    }
                }
                for(int i = 0; i < C4.length; i++){
                    for(int j = 0 ; j < C4.length; j++){
                        C[i + C4.length][j + C4.length] = C4[i][j];
                    }
                }

                if(N == 8){
                    System.out.println("Matriz C: ");
                    imprimirMatriz(C);
                }

                //Definimos el checkSum
                double checkSum = 0;

                //Usamos el obtenerSuma, con parametro de C
                checkSum += obtenerSuma(C);

                //Imprimimos nuestro checkSum
                System.out.println("--------------------------------------------------");
                System.out.println("CheckSum total ==> " + checkSum);
                System.out.println("--------------------------------------------------");

            } catch (Exception e) {
                System.err.println(e);
            }

        }else{
            try {
                //Hacemos la definición de nuestro Server Socket, pero debemos pasar puerto
                //que definimos y en adición el nodo
                ServerSocket NodoServer = new ServerSocket(50000 + nodo);

                //Hay que hacer el accept
                Socket conexion = NodoServer.accept();

                //Definimos la entrada y la salida de nuestro Stream, considerando la conexión
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

                //Def para A y B, con tempC
                double[][] A = new double[N/2][N];
                double[][] B = new double[N/2][N];
                double[][] tempC = new double[N][N];

                //Recibimiento de las Matrices.
                try{
                    int n = A.length;
                    int m = A[0].length;
                    A = recibirMatriz(entrada, n ,m);
                    B = recibirMatriz(entrada, n, m);
                }catch (Exception e){
                    System.out.println (e.getMessage());
                }
                
                /*
                * Hay que usar el tempC para contenerla
                * y multiplicarlas , despues basta con imprimir
                * como se aprecia.
                */

                if(N == 8){
                    tempC = multiplicarMatrices(A, B);
                    System.out.println("--------------------------------------------------");
                    System.out.println("Matriz A");
                    System.out.println("--------------------------------------------------");
                    imprimirMatriz(A);
    
                    System.out.println("--------------------------------------------------");
                    System.out.println("\nMatriz B");
                    System.out.println("--------------------------------------------------");
                    imprimirMatriz(B);
    
                    System.out.println("--------------------------------------------------");
                    System.out.println("\nMatriz C");
                    System.out.println("--------------------------------------------------");
                    imprimirMatriz(tempC);
                }

                try{
                    enviarMatriz(salida, tempC);
                }catch (Exception e){
                    System.out.println (e.getMessage());
                }

                //Se debe cerrar
                NodoServer.close();
            } catch (IOException ex) {
                //En caso de...
                System.out.println(ex.getLocalizedMessage());
            }
        }
    }

    //Trabajo con recibirMatriz
    public static double[][] recibirMatriz(DataInputStream entrada,int n, int m) throws Exception {      
        double[][] C = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m ; j++) {
                C[i][j] = entrada.readDouble();
            }
        }
        return C;
    }

    //Trabajo con enviarMatriz
    public static void enviarMatriz(DataOutputStream salida, double[][] C) throws Exception{
        int n = C.length;
        int m = C[0].length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                salida.writeDouble(C[i][j]);
            }
        }                
    }

    //Trabajo con imprimirMatriz
    public static void imprimirMatriz(double[][] matriz) {
        int n = matriz.length;
        int m = matriz[0].length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println("\n");
        }
    }

    //Trabajo con obtenerSuma
    public static double obtenerSuma(double[][] matriz) {
        int n = matriz.length;
        int m = matriz[0].length;
        double suma = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                suma+=matriz[i][j];
            }
        }
        return suma;
    }

    //Trabajo con multiplicarMatrices
    public static double[][] multiplicarMatrices(double[][] A, double[][] B) {
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