package protocol;

import java.net.DatagramPacket;
import protocol.ProtocolMessage;
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
			FileSystem.getInstance().createChunk(message.file_id, message.chunk_num, message.body, message.body_len);
			break;
		case STORED:
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
}