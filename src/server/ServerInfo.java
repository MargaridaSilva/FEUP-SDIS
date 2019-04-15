package server;

import channel.Channel;

public class ServerInfo {
    
    private static ServerInfo info = null;

    public static ServerInfo init(String protocol_ver, int server_id, Channel mc, Channel mdb, Channel mdr) { 
        info = new ServerInfo(protocol_ver, server_id, mc, mdb, mdr); 
        return info; 
    } 

    public static ServerInfo getInstance() { 
        if(info == null){
            //throw error
        }
        return info; 
    } 

    public String protocol_ver;
    public int server_id;
    public Channel mc;
    public Channel mdb;
    public Channel mdr;

    ServerInfo(String protocol_ver, int server_id, Channel mc, Channel mdb, Channel mdr){
        this.protocol_ver = protocol_ver;
        this.server_id = server_id;
        this.mc = mc;
        this.mdb = mdb;
        this.mdr = mdr;
    }
    
    void set_protocol_ver(String version) {
    	this.protocol_ver = version;
    }

}

