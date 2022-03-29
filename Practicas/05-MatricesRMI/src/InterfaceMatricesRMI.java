import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceMatricesRMI extends Remote {
    public float[][] multiplicarMatrices(float[][] A, float[][] B, int N) throws RemoteException;
}