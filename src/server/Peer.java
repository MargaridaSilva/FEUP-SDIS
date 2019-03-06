package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

interface Peer extends Remote{

    public void backup(int peer_ap, String filename, int replication) throws RemoteException;
    public void restore(int peer_ap, String filename) throws RemoteException;
    public void delete(int peer_ap, String filename) throws RemoteException;
    public void reclaim(int peer_ap, int arg) throws RemoteException;
    public void state(int peer_ap) throws RemoteException;

}