package protocol;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import state.ServerState;
import utilities.FileSystem;

public class LeaseManager {
	private static final ScheduledExecutorService nextLease = Executors.newScheduledThreadPool(10);
	private static int LEASE_VALID_DUR = 10; //secs
	private static int LEASE_RENEWAL = 1; // secs
	
	
	public static void make_lease(String file_id) {
		 ServerState.start_leasing(file_id);

        try {
            Thread.sleep(5);
        } catch (InterruptedException ignored) {
        }

        if (!ServerState.is_leased(file_id)) {
            try {
				Protocol.lease(file_id);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

        nextLease.schedule(() -> check_lease(file_id), LEASE_VALID_DUR, TimeUnit.SECONDS);
    }
	
	private static void check_lease(String file_id) {
		if (ServerState.is_leased(file_id)) {
            nextLease.schedule(() -> make_lease(file_id), LEASE_RENEWAL, TimeUnit.SECONDS);
        } else {
            handle_leasing_expiry(file_id);
        }
   }

	
	public static void handle_leasing_expiry(String file_id) {
		ServerState.stop_leasing(file_id);
		FileSystem.getInstance().delete_file(file_id);
	}
	
	
	
}