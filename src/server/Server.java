package server;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.Remote;

class Server implements Peer{

    Server(){

    }

    public static void main(String[] args) {

        try{

            Server server = new Server();

            Peer stub = (Peer) UnicastRemoteObject.exportObject(server, 0);
        }catch(Exception e){}
    }

}