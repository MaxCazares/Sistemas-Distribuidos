package src;

public class PrintTest extends Thread {

    synchronized public void printThread(int n) {

        // This loop will print the currently executed
        // thread
        for (int i = 1; i <= 10; i++) {
            System.out.println("Thread " + n + " is working...");

            // Try block to check for exceptions
            try {
                // Pause the execution of current thread
                // for 0.600 seconds using sleep() method
                Thread.sleep(600);
            }
            // Catch block to handle the exceptions
            catch (Exception ex) {
                // Overriding existing toString() method and
                // prints exception if occured
                System.out.println(ex.toString());
            }
        }

        // Display message for better readability
        System.out.println("--------------------------");

        try {
            // Pause the execution of current thread
            // for 0.1000 millisecond or 1sec using sleep
            // method
            Thread.sleep(1000);
        }
        catch (Exception ex) {
            // Printing the exception
            System.out.println(ex.toString());
        }
    }
}