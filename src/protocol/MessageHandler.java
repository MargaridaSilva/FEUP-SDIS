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
		ProtocolMessage message = new ProtocolMessage(this.packet);
		message.printMessageInfo();
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
		if(message.sender_id == ServerInfo.getInstance().server_id){
			return;
		}

		ChunkId chunk_id =  new ChunkId(message.file_id, message.chunk_num);
		FileSystem.getInstance().save_chunk(message.file_id, message.chunk_num, message.body, message.body_len);
		ServerState.store_log(chunk_id, message.body_len);
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
		
		
	}
	
	private void handle_chunk(ProtocolMessage message) {
		System.out.println("Received Chunk");
		
	}
	
	private void handle_delete(ProtocolMessage message) {
		System.out.println("Received delete");
		FileSystem.getInstance().delete_file(message.file_id);
		ServerState.remove_file(message.file_id);
	}
	
	private void handle_removed(ProtocolMessage message) {
		System.out.println("Received removed");
		
	}

}