package server;

import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.net.MulticastSocket;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;

import utilities.Utilities;
import utilities.FileSystem;
import channel.Channel;

class Server implements Peer {

    private int server_id;

    private Channel mc;
    private Channel mdb;
    private Channel mdr;

    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;


    private FileSystem fs;
    
    Server(int server_id, String mc_addr, int mc_port, String mdb_addr, int mdb_port, String mdr_addr, int mdr_port)
            throws IOException {

        this.server_id = server_id;
        this.fs = new FileSystem(this.server_id);
        this.fs.createPeerFileStructure();

        this.mc = new Channel(mc_addr, mc_port);
        this.mdb = new Channel(mdb_addr, mdb_port);
        this.mdr = new Channel(mdr_addr, mdr_port);
    }


    public byte[] createHeaderPutChunk(String version, int sender_id, String file_id, int chunk_num, int replication, byte[] bytes){
    	String message = "PUTCHUNK" + " " + version + " " + sender_id + " " + " " + file_id + " " + chunk_num + " " + replication + "\r\n\r\n";
        return message.getBytes();
    }

    public void putchunk(String version, int sender_id, String file_id, int chunk_num, int replication, byte[] bytes,
            int readBytes) throws IOException {
    	Message message = new Message(version, sender_id, file_id, chunk_num, replication, bytes, readBytes);
        DatagramPacket packet = new DatagramPacket(message.buf, message.buf_len, this.mdb_group, this.mdb_port);
        this.mdb.send(packet);
    }


    private void processMessage(Message message) {
        switch(message.type){
            case "PUTCHUNK": 
                this.fs.createChunk(message.file_id, message.chunk_num, message.body, message.body_len);
                break;
        }
    }

    public void listenChannels(){
        try {
            this.mc.startReceive();
            this.mdb.startReceive();
            this.mdr.startReceive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        // mc.leaveGroup(mc_group);
        // mdb.leaveGroup(mdb_port);
        // mdr.leaveGroup(mdr_port);
    }

    @Override   
    public String backup(String filename, int replication) throws RemoteException {

        try{

            InputStream in_file = new FileInputStream(Utilities.FILES_DIR + filename);
            String file_id = Utilities.generateIdentifier(Utilities.FILES_DIR + filename);

            int readBytes = 0;
            byte[] bytes = new byte[Utilities.CHUNK_SIZE];
            int i = 0;

            while ((readBytes = in_file.read(bytes, 0, Utilities.CHUNK_SIZE)) != -1) {
                System.out.println("Put Chunk");

                putchunk("1.0", this.server_id, file_id, i, replication, bytes, readBytes);

                i++;
            }

            in_file.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        return "OK";
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
            Server server = new Server(server_id, mc_addr, mc_port, mdb_addr, mdb_port, mdr_addr, mdr_port);
            server.receive();

            Peer stub = (Peer) UnicastRemoteObject.exportObject(server, 0);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind(service_ap, stub);

           

        } catch (Exception e) {
        }
    }
}