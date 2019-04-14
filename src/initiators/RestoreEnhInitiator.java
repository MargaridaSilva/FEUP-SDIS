package initiators;

import channel.TCPServer;
import utilities.Utilities;

public class RestoreEnhInitiator extends RestoreInitiator implements Runnable {
	String filename;
	
	public RestoreEnhInitiator(String filename){
		super(filename);
	}

	@Override
	public void run() {
        TCPServer tcp_socket = new TCPServer(Utilities.TCP_PORT);
        tcp_socket.startReceive();
		this.run_common();
	}
}