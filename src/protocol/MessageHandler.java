package protocol;

import java.net.DatagramPacket;
import protocol.ProtocolMessage;

class MessageHandler{
	 public MessageHandler(DatagramPacket packet) {
		 ProtocolMessage message =  new ProtocolMessage(packet);
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