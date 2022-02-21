public class Thread2 extends Thread {

    // Declaring variable of type Class1
    PrintTest test;

    // Constructor for class2
    public Thread2(PrintTest p) {
        test = p;
    }

    // run() method of this class for
    // entry point for thread2
    public void run() {
        test.printThread(2);
    }
}