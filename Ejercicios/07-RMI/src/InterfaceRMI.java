import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceRMI extends Remote {
    public String mayusculas(String s) throws RemoteException;
    public int suma(int a, int b) throws RemoteException;
    public long chechsum(int[][] m) throws RemoteException;
}