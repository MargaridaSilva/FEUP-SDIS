package initiators;

import protocol.Protocol;
import utilities.Utilities;

public class DeleteInitiator implements Runnable{
	String filename;
	
	public DeleteInitiator(String filename) {
		this.filename = filename;
	}

	@Override
	public void run() {
		try{
            String file_id = Utilities.generateIdentifier(Utilities.FILES_DIR + filename);
            Protocol.delete(file_id);

        }catch(Exception e){
            e.printStackTrace();
        }
	}
}