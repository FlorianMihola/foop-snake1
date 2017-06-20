package foopsnake.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
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
		
		game.setDaemon(true);
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
		server.getKryo().register(ItemPackage.class);
		server.getKryo().register(LostWinnerPackage.class);
		
		
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
		
		game.snakes.put(c,new Snake(size, pos, 25, true));
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
		p.setBorderSize(game.snakes.get(clients.get(0)).getBorderSize());
		p.setPosition(position);
		p.setSize(size);
		for (int i = 0; i < clients.size(); i++) {
			p.setPlayerId(i);
			clients.get(i).sendUDP(p);
		}
	}
	/**
	 * Send new item to all clients
	 * @param item
	 */
	public static void sendNewItem(Item item) {
		System.out.println("Server sends new item");
		ItemPackage p = new ItemPackage();
		p.setId(item.getId());
		p.setItemType(item.getClass().getSimpleName());
		p.setPosition(item.getPosition());
		for(int i = 0; i < clients.size(); i++) {
			clients.get(i).sendUDP(p);
		}
	}
	/**
	 * Remove item from all clients
	 * @param item
	 */
	public static void sendRemoveItem(Item item) {
		System.out.println("Server removes item");
		ItemPackage p = new ItemPackage();
		p.setId(item.getId());
		p.setRemove(true);
		for(int i = 0; i < clients.size(); i++) {
			clients.get(i).sendUDP(p);
		}
	}
	
	public static void sendLostWon(Connection c, boolean won) {
		int id = clients.indexOf(c);
		System.out.println("Player "+id+"ends game");
		LostWinnerPackage p = new LostWinnerPackage();
		p.setWon(won);
		p.setId(id);
		for(int i = 0; i < clients.size(); i++) {
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
	    private List<ItemTupel> itemList = new LinkedList<>();
	    
	    private long lastTime = System.currentTimeMillis();
	    private long ms = 0;
	    private long gameTime = 0;
	    private long itemSpawns = 0;
	    private boolean sendData = false;
	    private boolean multiplayer = false;
	    
		@Override public void run() {
			if (snakes.size() > 1) {
				multiplayer = true;
			}
			boolean running = true;
			while(running) {
				long delta = System.currentTimeMillis() - lastTime;
				lastTime = System.currentTimeMillis();
				ms+=delta;
				gameTime += delta;
				itemSpawns += delta;
				
				LinkedList<Connection> removeList = new LinkedList<>();
				
				for (Map.Entry<Connection, Snake> entry : snakes.entrySet()) {
					entry.getValue().update(directions.get(entry.getKey()), (int)delta);
					if(gameTime > 30000) {
					}
					itemList.forEach((itemTupel)->{
						if(itemTupel.getItem().getPosition().distance(entry.getValue().getGridPosition()) == 0.0f) {
							System.out.println(itemTupel.getItem().getClass());
							if(itemTupel.getItem().getClass() == ItemDirection.class) {
								for (Map.Entry<Connection, Snake> entry2 : snakes.entrySet()) {
									if (entry != entry2) {							
										entry2.getValue().inverseDirection(6000);
									}
								}
							}
							itemTupel.getItem().doEffect(entry.getValue());
							ServerProgram.sendRemoveItem(itemTupel.getItem());
							itemList.remove(itemTupel);
							sendData = true;
						}
					});
				}
				for (Map.Entry<Connection, Snake> entry : snakes.entrySet()) {
					if(entry.getValue().getHealth() < 0) {
						ServerProgram.sendLostWon(entry.getKey(), false);
						if(snakes.size() == 1) {
							running = false;
						}
						clients.remove(entry.getKey());
						removeList.add(entry.getKey());
					}
				}
				removeList.forEach((connection)->{
					snakes.remove(connection);
				});
				System.out.println(snakes.size());
				if(snakes.size() == 1 && multiplayer) {
					ServerProgram.sendLostWon(clients.get(0), true);
					running = false;
				}
				
				for(Map.Entry<Connection, Snake> entry1 : snakes.entrySet()) {
					for(Map.Entry<Connection, Snake> entry2 : snakes.entrySet()) {
						if(entry1 == entry2) {
							if(entry1.getValue().isSnakeBitingSnake(entry1.getValue())) {
								entry1.getValue().dealDamage(delta * 0.05f);
								entry1.getValue().stopMoving();
								sendData = true;
							} 
						} else {
							if(entry1.getValue().isSnakeBitingSnake(entry2.getValue())) {
								entry1.getValue().setSpeedBuff(5000, 2.0f);
								entry1.getValue().setHealth(entry1.getValue().getHealth() + delta * 0.05f);
								entry2.getValue().dealDamage(delta * 0.05f);
								entry1.getValue().stopMoving();
								sendData = true;
							}
						}			 
					}
				}
				for (Map.Entry<Connection, Snake> entry : snakes.entrySet()) {
				//Every 1.5 seconds we send the current location of the snakes to the clients
						if((ms >= 100) || (entry.getValue().directionChange() || (sendData)))  {
						
							SnakeDataPackage data = new SnakeDataPackage();
							data.setGridPosition(entry.getValue().getGridPosition());
							data.setLastDirection(entry.getValue().getLastDirection());
							data.setLastX(entry.getValue().getLastX());
							data.setLastY(entry.getValue().getLastY());
							data.setPosition(entry.getValue().getPosition());
							data.setSpeed(entry.getValue().getSpeed());
							data.setTileDirections(entry.getValue().getDirections());
							data.setHealth(entry.getValue().getHealth());
							data.setDirectionBuffTime(entry.getValue().getDirectionBuffTime());
							data.setSpeedBuffTime(entry.getValue().getSpeedBuffTime());
							data.setInvincibleBuffTime(entry.getValue().getInvincibleBuffTime());
							data.setSpeedBuff(entry.getValue().getSpeedBuff());
							
							ServerProgram.sendSnakeData(entry.getKey(),data);   
						}	
				}
				//Every 4 seconds an item spawns
				if(itemSpawns >= 4000)  {
					itemSpawns = 0;
					Vector2f position;
					outerLoop:
					{
						int x = (int) ((Math.random()*(Game.WIDTH-Game.TILE_SIZE))/Game.TILE_SIZE);
						int y = (int) ((Math.random()*(Game.HEIGHT-Game.TILE_SIZE))/Game.TILE_SIZE);
						if (x == 0) x++;
						if (y == 0) y++;
						position = new Vector2f(x,y);
						for (Map.Entry<Connection, Snake> entry : snakes.entrySet()) {
							if (entry.getValue().isCoordinateOnSnake(position)) {
								break outerLoop;
							}
						}
					}
					Item item = null;
					switch((int)(Math.random()*4)) {
					case 0 : item = new ItemSize(position); break;
					case 1 : item = new ItemInvincible(position); break;
					case 2 : item = new ItemDirection(position); break;
					default : item = new ItemSpeed(position); break;
					}
					ItemTupel it= new ItemTupel(item, System.currentTimeMillis() + 7000);
					ServerProgram.sendNewItem(item);
					itemList.add(it);
					
				}
				//An item disappears after some time
				itemList.forEach((itemTupel)->{
					if(itemTupel.getTime()-System.currentTimeMillis() <= 0) {
						ServerProgram.sendRemoveItem(itemTupel.getItem());
						itemList.remove(itemTupel);
					}
				});
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
