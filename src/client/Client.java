package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import server.Peer;

public class Client {

    public Client() {
    }

    public void backup(String peer_ap, String file_name, int replication){

    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(null);
            Peer stub = (Peer) registry.lookup("Hello");
            String response = stub.backup(0, "", 0);
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}   