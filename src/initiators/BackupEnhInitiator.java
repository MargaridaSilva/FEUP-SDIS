package initiators;

import utilities.Utilities;

public class BackupEnhInitiator extends BackupInitiator implements Runnable {

	public BackupEnhInitiator(String filename, int replication){
		super(filename, replication);
		this.version = Utilities.ENH_VERSION;
	}
}