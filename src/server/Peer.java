package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Peer extends Remote{

    public String backup(int peer_ap, String filename, int replication) throws RemoteException;
    public String restore(int peer_ap, String filename) throws RemoteException;
    public String delete(int peer_ap, String filename) throws RemoteException;
    public String reclaim(int peer_ap, int arg) throws RemoteException;
    public String state(int peer_ap) throws RemoteException;

}