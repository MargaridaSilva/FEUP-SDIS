package initiators;

import channel.TCPServer;
import utilities.Utilities;

public class RestoreEnhInitiator extends RestoreInitiator implements Runnable {
	
	public RestoreEnhInitiator(String filename){
		super(filename);
		this.version = Utilities.ENH_VERSION;
	}

	@Override
	public void run() {
        TCPServer tcp_socket = new TCPServer(Utilities.TCP_PORT);
        tcp_socket.startReceive();
		this.run_common();
	}
}