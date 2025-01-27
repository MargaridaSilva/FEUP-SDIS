package protocol;

import java.io.IOException;
import java.net.InetAddress;

import protocol.ProtocolMessage;
import state.ChunkId;
import state.ServerState;
import server.ServerInfo;
import utilities.FileSystem;
import utilities.Utilities;

public class MessageHandler implements Runnable {

	byte[] packet;
	InetAddress sender_address;

	public MessageHandler(byte[] packet, InetAddress sender_address) {
		this.packet = packet;
		this.sender_address = sender_address;
	}

	@Override
	public void run() {
		ProtocolMessage message;

		try {
			message = new ProtocolMessage(this.packet);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		System.out.println("Message received");
		message.printMessageInfo();
		System.out.println();
		switch (message.type) {
		case PUTCHUNK:
			handle_putchunk(message);
			break;
		case STORED:
			handle_stored(message);
			break;
		case GETCHUNK:
			handle_getchunk(message);
			break;
		case CHUNK:
			handle_chunk(message);
			break;
		case DELETE:
			handle_delete(message);
			break;
		case REMOVED:
			handle_removed(message);
		case LEASE:
			handle_lease(message);
		case LEASED:
			handle_leased(message);
		default: break;
		}
	}


	private void handle_leased(ProtocolMessage message) {
		if (message.sender_id != ServerInfo.getInstance().server_id) {
			ServerState.confirm_lease(message.file_id);
		}
			
		
	}

	private void handle_lease(ProtocolMessage message) {
		if (FileSystem.getInstance().has_file(message.file_id)) {
			try {
				Protocol.leased(message.file_id);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	private void handle_putchunk(ProtocolMessage message) {
		if(message.sender_id == ServerInfo.getInstance().server_id || 
			ServerState.backup_initiator(message.file_id) ||
			ServerState.get_used_space() + message.body_len > ServerState.max_space){
			return;
		}
		
		ChunkId chunk_id =  new ChunkId(message.file_id, message.chunk_num);
		
		
		if (message.version.equals(Utilities.ENH_VERSION) &&
				ServerState.get_perceived_replication(chunk_id) >= message.replication){
					return;
			}
		
		FileSystem.getInstance().save_chunk_backup(message.file_id, message.chunk_num, message.body, message.body_len);
		ServerState.store_log(chunk_id, message.body_len, message.replication);
		try {
			Protocol.stored(chunk_id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private void handle_stored(ProtocolMessage message) {
		ChunkId chunk_id =  new ChunkId(message.file_id, message.chunk_num);
		ServerState.add_ack(chunk_id, message.sender_id);
	}	
	

	private void handle_getchunk(ProtocolMessage message) {
		if(message.sender_id == ServerInfo.getInstance().server_id){
			return;
		}

		ChunkId chunk_id = new ChunkId(message.file_id, message.chunk_num);
		
		if(ServerState.store_contain_chunk(chunk_id)){
			byte[] body = FileSystem.getInstance().read_chunk_backup(message.file_id, message.chunk_num);
			ServerState.getchunk_request(chunk_id);
			
			switch(message.version){
				case Utilities.STOCK_VERSION:
					Protocol.chunk(chunk_id, body, body.length);
					break;
				case Utilities.ENH_VERSION:
					Protocol.chunk_enh(chunk_id, body, body.length, sender_address);
					break;
			}
			
		} 
	}

	
	private void handle_chunk(ProtocolMessage message) {
		ChunkId chunk_id =  new ChunkId(message.file_id, message.chunk_num);
		
		if(ServerState.getchunk_requested(chunk_id)){
			FileSystem.getInstance().save_chunk_restore(message.file_id, message.chunk_num, message.body, message.body_len);
			ServerState.getchunk_handled(chunk_id);
		}else{
			ServerState.getchunk_delete(chunk_id);
		}

	}

	
	private void handle_delete(ProtocolMessage message) {
		FileSystem.getInstance().delete_file(message.file_id);
	}

	
	private void handle_removed(ProtocolMessage message) {
		if(message.sender_id == ServerInfo.getInstance().server_id){
			return;
		}

		ChunkId chunk_id =  new ChunkId(message.file_id, message.chunk_num);

		ServerState.decrease_chunk_replication(chunk_id, message.sender_id);

		if(ServerState.store_contain_chunk(chunk_id)){

			int perceived_replication = ServerState.get_perceived_replication(chunk_id);
			int desired_replication = ServerState.store_get_chunk_info(chunk_id).getDesiredReplication();

			if(perceived_replication < desired_replication){
				byte[] body = FileSystem.getInstance().read_chunk_backup(message.file_id, message.chunk_num);
				try {
					Protocol.putchunk_with_delay(chunk_id, desired_replication, body, body.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	
	}



}