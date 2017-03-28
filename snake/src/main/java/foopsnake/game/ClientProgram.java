package foopsnake.game;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientProgram extends Listener {
	static Client client;
	static String hostip;
	static int tcpPort = 27960, udpPort = 27960;
	
	public static void init(String ip) throws Exception {
		client = new Client();
		client.getKryo().register(PacketMessage.class);
		hostip = ip;
		
		client.start();
		
		client.connect(5000, hostip, tcpPort, udpPort);
		
		client.addListener(new ClientProgram());
	}
	
	public void received(Connection c, Object p) {
		if (p instanceof PacketMessage) {
			PacketMessage packetMessage = (PacketMessage) p;
			System.out.println("Client received message: "+packetMessage.message);
		}
	}
}
