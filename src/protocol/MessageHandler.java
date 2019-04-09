package protocol;

import java.io.IOException;

import protocol.ProtocolMessage;
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
			putchunk(message);
			break;
		case STORED:
			stored(message);
			break;
		case GETCHUNK:
			break;
		case CHUNK:
			break;
		case DELETE:
			break;
		case REMOVED:
		}
	}

	private void stored(ProtocolMessage message) {
		System.out.println("Received Stored");
	}

	private void putchunk(ProtocolMessage message) {
		if(message.sender_id == ServerInfo.getInstance().server_id){
			return;
		}

		FileSystem.getInstance().createChunk(message.file_id, message.chunk_num, message.body, message.body_len);
		try {
			Protocol.stored(message.file_id, message.chunk_num);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}