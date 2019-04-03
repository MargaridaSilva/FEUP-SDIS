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
        this.stub = (Peer) registry.lookup(peer_ap);
    }


    public static void main(String[] args) throws RemoteException, NotBoundException {

        String peer_ap = args[0];
        String sub_protocol = new String(args[1]).toUpperCase();

        TestApp testapp = new TestApp(peer_ap);

        switch(sub_protocol){
            case "BACKUP":
                String file_name = args[2];
                int replication = Integer.parseInt(args[3]);
                testapp.backup(file_name, replication);
                break;
        }
    }

    private void backup(String file_name, int replication) {
        try {
            String response = this.stub.backup(0, "", 0);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
};