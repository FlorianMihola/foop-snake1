package foopsnake.game;

import org.newdawn.slick.Color;
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
		client.getKryo().register(ItemPackage.class);
		client.getKryo().register(LostWinnerPackage.class);
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
		if (p instanceof LostWinnerPackage) {
			LostWinnerPackage lostWinnerPackage = (LostWinnerPackage)p;
			if(lostWinnerPackage.isWon()) {
				sgc.youWon();
			}else {
				sgc.removePlayer(lostWinnerPackage.getId());
			}
		}
		if (p instanceof ItemPackage) {
			ItemPackage itemPackage = (ItemPackage)p;
			Item item = null;
			if (itemPackage.isRemove()) {
				//Remove item
				sgc.removeItem(itemPackage.getId());
			} else {
				//add item
				String type = itemPackage.getItemType();
				switch(type) {
				case "ItemSize" : item = new ItemSize(itemPackage.getPosition()); break;
				case "ItemSpeed" : item = new ItemSpeed(itemPackage.getPosition()); break;
				case "ItemInvincible" : item = new ItemInvincible(itemPackage.getPosition()); break;
				case "ItemDirection" : item = new ItemDirection(itemPackage.getPosition()); break;
				}
				item.setId(itemPackage.getId());
				sgc.addNewItem(item);
			}
			
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
