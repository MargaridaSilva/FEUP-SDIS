package protocol;

import java.io.IOException;

import protocol.ProtocolMessage;
import state.ChunkId;
import state.ServerState;
import server.ServerInfo;
import utilities.FileSystem;

public class MessageHandler implements Runnable {

	byte[] packet;

	public MessageHandler(byte[] packet) {
		this.packet = packet;
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
		}
	}


	private void handle_putchunk(ProtocolMessage message) {
		if(message.sender_id == ServerInfo.getInstance().server_id || 
			ServerState.backup_initiator(message.file_id) ||
			ServerState.get_used_space() + message.body_len > ServerState.max_space){
			return;
		}

		ChunkId chunk_id =  new ChunkId(message.file_id, message.chunk_num);
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

		ChunkId chunk_id =  new ChunkId(message.file_id, message.chunk_num);
		
		if(ServerState.store_contain_chunk(chunk_id)){
			byte[] body = FileSystem.getInstance().read_chunk_backup(message.file_id, message.chunk_num);
			ServerState.getchunk_request(chunk_id);
			Protocol.chunk(chunk_id, body, body.length);
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
		ServerState.remove_file(message.file_id);
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