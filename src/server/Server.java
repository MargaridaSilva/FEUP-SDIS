package server;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import utilities.Utilities;

class Server implements Peer {

    Server() {

    }

    // protocol version, the server id, service access point, MC, MDB, MDR
    public static void main(String[] args) {

        int protocol_ver = Integer.parseInt(args[0]);
        int server_id = Integer.parseInt(args[1]);
        String service_ap = args[2];

        // String mc_addr = args[3];
        // int mc_port = Integer.parseInt(args[4]);

        // String mdb_addr = args[5];
        // int mdb_port = Integer.parseInt(args[6]);

        // String mdr_addr = args[7];
        // int mdr_port = Integer.parseInt(args[8]);

        String mc_addr = Utilities.mc_addr;
        int mc_port = Utilities.mc_port;

        String mdb_addr = Utilities.mdb_addr;
        int mdb_port = Utilities.mdb_port;

        String mdr_addr = Utilities.mdr_addr;
        int mdr_port = Utilities.mdr_port;


        try {

            Server server = new Server();

            Peer stub = (Peer) UnicastRemoteObject.exportObject(server, 0);
            
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind(service_ap, stub);

        } catch (Exception e) {
        }
    }

    @Override
    public String backup(int peer_ap, String filename, int replication) throws RemoteException {
        return "backup";
        
    }

    @Override
    public String restore(int peer_ap, String filename) throws RemoteException {
        return filename;

    }

    @Override
    public String delete(int peer_ap, String filename) throws RemoteException {
        return filename;

    }

    @Override
    public String reclaim(int peer_ap, int arg) throws RemoteException {
        return null;

    }

    @Override
    public String state(int peer_ap) throws RemoteException {
        return null;

    }

}