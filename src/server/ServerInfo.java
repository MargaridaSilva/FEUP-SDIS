package server;

import channel.Channel;

public class ServerInfo {
    
    private static ServerInfo info = null;

    public static ServerInfo init(int server_id, Channel mc, Channel mdb, Channel mdr) { 
        info = new ServerInfo(server_id, mc, mdb, mdr); 
        return info; 
    } 

    public static ServerInfo getInstance() { 
        if(info == null){
            //throw error
        }
        return info; 
    } 

    public int server_id;
    public Channel mc;
    public Channel mdb;
    public Channel mdr;

    ServerInfo(int server_id, Channel mc, Channel mdb, Channel mdr){
        this.server_id = server_id;
        this.mc = mc;
        this.mdb = mdb;
        this.mdr = mdr;
    }

}

