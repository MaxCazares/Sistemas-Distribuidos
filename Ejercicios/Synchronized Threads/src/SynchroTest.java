// import src.PrintTest;
// import src.Thread1;
// import src.Thread2;

public class SynchroTest {
    
    // Main driver method
    public static void main(String[] args) {

        // Creating object of class 1 inside main() method
        PrintTest p = new PrintTest();

        // Passing the same object of class PrintTest to
        // both threads
        Thread1 t1 = new Thread1(p);
        Thread2 t2 = new Thread2(p);


        // Start executing the threads
        // using start() method
        t1.start();
        t2.start();

        // This will print both the threads simultaneously
    }
}