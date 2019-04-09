package protocol;

import java.net.DatagramPacket;
import protocol.ProtocolMessage;

class MessageHandler implements Runnable {
	DatagramPacket packet;
	
	 public MessageHandler(DatagramPacket packet) {
		this.packet = packet; 
	 }

	@Override
	public void run() {
		ProtocolMessage message =  new ProtocolMessage(this.packet);
		 switch(message.type) {
			 case PUTCHUNK:
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