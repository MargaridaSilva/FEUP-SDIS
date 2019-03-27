package server;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;

import utilities.Utilities;

class Server implements Peer {

    private MulticastSocket mc;
    private MulticastSocket mdb;
    private MulticastSocket mdr;

    private InetAddress mc_group;
    private InetAddress mdb_group;
    private InetAddress mdr_group;

    private int mc_port;
    private int mdb_port;
    private int mdr_port;

    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;

    private static int CHUNK_SIZE = 64 * 1000;

    // DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),
    // group, 6789);
    // s.send(hi);
    // // get their responses!
    // byte[] buf = new byte[1000];
    // DatagramPacket recv = new DatagramPacket(buf, buf.length);
    // s.receive(recv);

    Server(String mc_addr, int mc_port, String mdb_addr, int mdb_port, String mdr_addr, int mdr_port)
            throws IOException {

        this.mc_group = InetAddress.getByName(mc_addr);
        this.mdb_group = InetAddress.getByName(mdb_addr);
        this.mdr_group = InetAddress.getByName(mdr_addr);

        this.mc = new MulticastSocket(mc_port);
        this.mdb = new MulticastSocket(mdb_port);
        this.mdr = new MulticastSocket(mdr_port);

        this.mc_port = mc_port;
        this.mdb_port = mdb_port;
        this.mdr_port = mdr_port;
    }

    public void putchunk(int version, int sender_id, int file_id, int chunk_num, int replication, byte[] bytes) {

        sendPacket = new DatagramPacket(bytes, bytes.length, this.mdb_group, this.md
        // this.mdb_port);
    }

    public void receive() {
        // mc.joinGroup(mc_group);
        // mdb.joinGroup(mdb_port);
        // mdr.joinGroup(mdr_port);
    }

    public void close() {
        // mc.leaveGroup(mc_group);
        // mdb.leaveGroup(mdb_port);
        // mdr.leaveGroup(mdr_port);
    }

    public void joinGroup() {

    }

    @Override
    public String backup(int peer_ap, String filename, int replication) throws RemoteException {

        // Open file
        // Dividir chunks
        // ciclo com putchunk

        try {
            InputStream inFile = new FileInputStream("../files/file.txt");

            int readBytes = 0;
            byte[] bytes = new byte[CHUNK_SIZE];

            while ((readBytes = inFile.read(bytes, 0, CHUNK_SIZE)) != -1) {
                System.out.println("A");
                System.out.println(new String(bytes));

            }

            inFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Teste";
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
            Server server = new Server(mc_addr, mc_port, mdb_addr, mdb_port, mdr_addr, mdr_port);

            Peer stub = (Peer) UnicastRemoteObject.exportObject(server, 0);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind(service_ap, stub);

        } catch (Exception e) {
        }
    }
}