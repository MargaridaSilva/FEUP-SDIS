package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Peer extends Remote{

    public String backup(String filename, int replication) throws RemoteException;
    public String restore(String filename) throws RemoteException;
    public String delete(String filename) throws RemoteException;
    public String reclaim(int max_space) throws RemoteException;
    public String state() throws RemoteException;

}