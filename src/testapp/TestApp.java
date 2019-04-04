package testapp;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import server.Peer;

class TestApp {

    private String peer_ap;
    private Registry registry;
    private Peer stub;

    public TestApp(String peer_ap) throws RemoteException, NotBoundException {
        this.peer_ap = peer_ap;
        this.registry = LocateRegistry.getRegistry(null);
        System.out.println(peer_ap);
        this.stub = (Peer) registry.lookup(peer_ap);
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {

        int peer_ap = Integer.parseInt(args[0]);
        String peer_ap_str = args[0];
        String sub_protocol = new String(args[1]).toUpperCase();

        TestApp testapp = new TestApp(peer_ap_str);

        switch(sub_protocol){
            case "BACKUP":
                String file_name = args[2];
                int replication = Integer.parseInt(args[3]);
                testapp.backup(file_name, replication);
                break;
        }
    }

    private void backup(String filename, int replication) {
        try {
            String response = this.stub.backup(filename, replication);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
};