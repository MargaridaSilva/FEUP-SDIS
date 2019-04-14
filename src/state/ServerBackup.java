package state;
import java.util.concurrent.TimeUnit;

import utilities.FileSystem;

public class ServerBackup implements Runnable {
	
	ServerState st = new ServerState();
	
	@Override
	public void run() {
		while(true) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileSystem.getInstance().save_server_state(st);
		}
		
	}	
}