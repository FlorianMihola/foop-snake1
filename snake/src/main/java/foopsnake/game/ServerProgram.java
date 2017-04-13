package foopsnake.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.geom.Vector2f;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class ServerProgram extends Listener{
	static Server server;
	static GameServer game = new GameServer();
	static ArrayList<Connection> clients = new ArrayList<Connection>();
	static int udpPort = 27960, tcpPort = 27960;
	static int readyPlayers = 0;
	
	public static void init() throws Exception {
		System.out.println("Creating the server");
		
		//create server
		server = new Server();
		
		//register packages
		server.getKryo().register(Direction.class);	
		server.getKryo().register(Vector2f.class);
		server.getKryo().register(Direction[].class);
		server.getKryo().register(Vector2f[].class);
		server.getKryo().register(int[].class);
		server.getKryo().register(SnakeDataPackage.class);
		server.getKryo().register(InitPackage.class);
		server.getKryo().register(StartPackage.class);
		
		
		//bind to a port
		server.bind(tcpPort,udpPort);
		
		server.start();
		
		server.addListener(new ServerProgram());
		
		
		System.out.println("Server is running...");
	}
	
	public void connected(Connection c) {
		System.out.println("Client connected: "+c.getRemoteAddressTCP().getHostString());
	
		clients.add(c);
		
		Vector2f pos = new Vector2f(5+clients.size()*5,10);
		int size = 8;
		
		game.snakes.put(c,new Snake(size,pos,true));
		game.directions.put(c, Direction.UP);
	}
	
	public void received(Connection c, Object p) {
		
		if (p instanceof Direction) {
			Direction d = (Direction) p;
			System.out.println("Host received direction...");
			game.directions.put(c, d);
		}
		
		//Wait for the clients to send their start signal
		if (p instanceof StartPackage) {
			readyPlayers++;
			if (readyPlayers == clients.size()) {
				//start the server + send packages to start the games on the clients
				game.start();
				for(int i = 0; i < clients.size(); i++) {
					clients.get(i).sendTCP(new StartPackage());
				}
			}
		}
			
	}
	
	public static void startGame() {
		
		//Send packages to initialize all clients
		
		InitPackage p = new InitPackage();
		p.setNumberOfPlayers(clients.size());
		Vector2f[] position = new Vector2f[clients.size()];
		int[] size = new int[clients.size()];
		for (int i = 0; i < clients.size(); i++) {
			size[i] = game.snakes.get(clients.get(i)).getSize();
			position[i] = game.snakes.get(clients.get(i)).getGridPosition();
		}
		p.setPosition(position);
		p.setSize(size);
		for (int i = 0; i < clients.size(); i++) {
			p.setPlayerId(i);
			clients.get(i).sendUDP(p);
		}
	}
	
	public static void sendSnakeData(Connection c,SnakeDataPackage data) {
		//send the data to all clients
		data.setPlayerId(clients.indexOf(c));
		for(int i = 0; i < clients.size(); i++) {
			clients.get(i).sendUDP(data);
		}
		
	}
	

	public void disconnected(Connection c) {
		System.out.println("A client disconnected");
		clients.remove(c);
	}
	
	/**
	 * This class does all the calculations of the game
	 * and sends it to the clients to synchronize.
	 * 
	 * @author johannes
	 *
	 */
	private static class GameServer extends Thread{

		private HashMap<Connection,Direction> directions = new HashMap<>();
	    private HashMap<Connection,Snake> snakes = new HashMap<>();
	    
	    private long lastTime = System.currentTimeMillis();
	    private long ms = 0;
	    
		@Override public void run() {
			
			while(true) {
				long delta = System.currentTimeMillis() - lastTime;
				lastTime = System.currentTimeMillis();
				ms+=delta;
				
				for (Map.Entry<Connection, Snake> entry : snakes.entrySet()) {
					entry.getValue().update(directions.get(entry.getKey()), (int)delta);	    
				}
				for (Map.Entry<Connection, Snake> entry : snakes.entrySet()) {
				//Every 1.5 seconds we send the current location of the snakes to the clients
						if((ms >= 100) || (entry.getValue().directionChange()))  {
						
							SnakeDataPackage data = new SnakeDataPackage();
							data.setGridPosition(entry.getValue().getGridPosition());
							data.setLastDirection(entry.getValue().getLastDirection());
							data.setLastX(entry.getValue().getLastX());
							data.setLastY(entry.getValue().getLastY());
							data.setPosition(entry.getValue().getPosition());
							data.setSpeed(entry.getValue().getSpeed());
							data.setTileDirections(entry.getValue().getDirections());
							
							ServerProgram.sendSnakeData(entry.getKey(),data);   
						}	
				}
				if(ms >= 100) {
					ms = 0;
				}
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
		  }
		
	}
}
