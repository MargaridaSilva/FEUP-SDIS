package initiators;

import utilities.Utilities;

public class DeleteEnhInitiator extends DeleteInitiator {
	public DeleteEnhInitiator(String filename, int replication){
		super(filename);
		this.version = Utilities.ENH_VERSION;
	}
}