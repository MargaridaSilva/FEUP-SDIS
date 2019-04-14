package server;

import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import state.ServerState;
import state.ServerBackup;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.IOException;

import utilities.Utilities;
import utilities.FileSystem;
import channel.Channel;
import initiators.*;

class Server implements Peer {

    private ServerInfo server_info;
    public static Executor server_backup = null;
    
    Server(String protocol_ver, int server_id, String mc_addr, int mc_port, String mdb_addr, int mdb_port, String mdr_addr,
            int mdr_port)
            throws IOException {
    	
    	Channel mc = new Channel(mc_addr, mc_port);
        Channel mdb = new Channel(mdb_addr, mdb_port);
        Channel mdr = new Channel(mdr_addr, mdr_port); 

    	ServerInfo.init(protocol_ver, server_id, mc, mdb, mdr);
        this.server_info = ServerInfo.getInstance();
    	
        FileSystem.init(server_id);
        FileSystem.getInstance().handleServerStateBackup();
        FileSystem.getInstance().createPeerFileStructure();
        
    	if (server_backup == null) {
    		server_backup = Executors.newSingleThreadExecutor();
    		server_backup.execute(new ServerBackup());
    	}
    }

    public void listenChannels(){
        try {
            this.server_info.mc.startReceive();
            this.server_info.mdb.startReceive();
            this.server_info.mdr.startReceive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeChannels() {
        try {
            this.server_info.mc.stopReceive();
            this.server_info.mdb.stopReceive();
            this.server_info.mdr.stopReceive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printInfo() {
        System.out.println("Server " + this.server_info.server_id + " is now running.");
        System.out.println("---------------------------------------------\n\n");
    }

    @Override   
    public String backup(String filename, int replication, boolean enh) throws RemoteException {
    	Executors.newSingleThreadExecutor().execute(new BackupInitiator(filename, replication, enh));
        return "OK";
    }

    @Override
    public String restore(String filename) throws RemoteException {
    	Executors.newSingleThreadExecutor().execute(new RestoreInitiator(filename));
		return "OK";
    }

    @Override
    public String delete(String filename) throws RemoteException {
    	Executors.newSingleThreadExecutor().execute(new DeleteInitiator(filename));
        return "OK";

    }

    @Override
    public String reclaim(int max_space) throws RemoteException {
    	Executors.newSingleThreadExecutor().execute(new ReclaimInitiator(max_space));
        return "OK";
    }

    @Override
    public String state() throws RemoteException {
        return ServerState.backup_log_info() + ServerState.store_log_info();
    }

    // protocol version, the server id, service access point, MC, MDB, MDR
    public static void main(String[] args) {

        String protocol_ver = args[0];
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
            Server server = new Server(protocol_ver, server_id, mc_addr, mc_port, mdb_addr, mdb_port, mdr_addr, mdr_port);
            server.listenChannels();
            
            server.printInfo();
            Peer stub = (Peer) UnicastRemoteObject.exportObject(server, 0);

            Registry registry = LocateRegistry.getRegistry(null);
            registry.bind(service_ap, stub);           

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
}