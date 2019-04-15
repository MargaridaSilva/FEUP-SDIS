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
        this.stub = (Peer) registry.lookup(this.peer_ap);
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        String peer_ap = args[0];
        String sub_protocol = new String(args[1]).toUpperCase();

        TestApp testapp = new TestApp(peer_ap);

        switch(sub_protocol){
            case "BACKUP":{
                String filename = args[2];
                int replication = Integer.parseInt(args[3]);
                testapp.backup(filename, replication);
                break;
            }
            case "BACKUPENH":{
                String filename = args[2];
                int replication = Integer.parseInt(args[3]);
                testapp.backup_enh(filename, replication);
                break;
            }
            case "RESTORE":{
                String filename = args[2];
                testapp.restore(filename);
                break;
            }
            case "RESTOREENH":{
                String filename = args[2];
                testapp.restore_enh(filename);
                break;
            }
            case "DELETE": case "DELETEENH": {
                String filename = args[2];
                testapp.delete(filename);
                break;
            }
            case "RECLAIM":{
                int max_space = Integer.parseInt(args[2]);
                testapp.reclaim(max_space);
                break;
            }
            case "STATE":
                testapp.state();
                break;
        }
    }


    private void backup(String filename, int replication) {
        try {
            String response = this.stub.backup(filename, replication);
            System.out.println("Backup: " + response);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void backup_enh(String filename, int replication) {
        try {
            String response = this.stub.backup_enh(filename, replication);
            System.out.println("BackupEnh: " + response);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void restore(String filename) {
        try {
            String response = this.stub.restore(filename);
            System.out.println("Restore: " + response);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void restore_enh(String filename) {
        try {
            String response = this.stub.restore_enh(filename);
            System.out.println("RestoreEnh: " + response);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void delete(String filename) {
        try {
            String response = this.stub.delete(filename);
            System.out.println("Delete: " + response);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void reclaim(int max_space) {
        try {
            String response = this.stub.reclaim(max_space);
            System.out.println("Reclaim: " + response);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void state() {
        try {
            String response = this.stub.state();
            System.out.println(response);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
};