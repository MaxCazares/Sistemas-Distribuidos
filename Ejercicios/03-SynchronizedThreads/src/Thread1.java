public class Thread1 extends Thread {

    // Declaring variable of type Class1
    PrintTest test;

    // Constructor for class1
    public Thread1(PrintTest p) {
        test = p;
    }

    // run() method of this class for
    // entry point for thread1
    public void run() {
        // Calling method 1 as in above class
        test.printThread(1);
    }
}
