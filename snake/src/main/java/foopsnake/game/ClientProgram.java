package foopsnake.game;

import org.newdawn.slick.geom.Vector2f;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientProgram extends Listener {
	static Client client;
	static String hostip;
	static int tcpPort = 27960, udpPort = 27960;
	
	static SnakeGameClient sgc;
	
	public static void init(String ip,SnakeGameClient snake) throws Exception {
		client = new Client();
		
		client.getKryo().register(Direction.class);
		client.getKryo().register(Vector2f.class);
		client.getKryo().register(Direction[].class);
		client.getKryo().register(Vector2f[].class);
		client.getKryo().register(int[].class);
		client.getKryo().register(SnakeDataPackage.class);
		client.getKryo().register(InitPackage.class);
		client.getKryo().register(StartPackage.class);
		hostip = ip;
		
		client.start();
		
		client.connect(5000, hostip, tcpPort, udpPort);
		
		client.addListener(new ClientProgram());
		
		sgc = snake;
	}
	
	public void received(Connection c, Object p) {
		if (p instanceof SnakeDataPackage) {
			SnakeDataPackage data = (SnakeDataPackage) p;
			//System.out.println("Client receives snake data...");
			sgc.setSnake(data);
			
		}
		if (p instanceof InitPackage) {
			InitPackage initPackage = (InitPackage) p;
			sgc.initialize(initPackage);
		}
		if (p instanceof StartPackage) {
			sgc.start();
		}
	}

	public static void sendDirection(Direction d) {
		System.out.println("Client "+ client.getRemoteAddressTCP() +"sends direction...");
		client.sendUDP(d);
	}
	
	/**
	 * Client sends signal that he is ready to start
	 * @param p
	 */
	public static void sendStart(StartPackage p) {
		client.sendTCP(p);
	}
	
	
}
