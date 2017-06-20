package foopsnake.game;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/** 
 * This class renders and updates the game and 
 * handles the communication to the host.
 * 
 * @author johannes
 *
 */
public class SnakeGameClient extends BasicGameState {
	private Snake snake[];
	private Border border;
	private Direction[] direction;
	private int playerId;
	private List<Item> itemList = new LinkedList<>();
	boolean running = false;
	boolean init = false;
	boolean lost = false;
	boolean won = false;

	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
	}
	
	/**
	 * server sends data to initialize the snake
	 * @param p
	 */
	public void initialize(InitPackage p) {
		snake = new Snake[p.getNumberOfPlayers()];
		direction = new Direction[p.getNumberOfPlayers()];
		border = new Border(p.getBorderSize());	
		for(int i = 0; i < p.getNumberOfPlayers(); i++) {
			snake[i] = new Snake(p.getSize()[i], p.getPosition()[i], p.getBorderSize(),false);
			direction[i] = Direction.UP;
			snake[i].setColor((float)Math.random(), (float)Math.random(), (float)Math.random());
		}
		playerId = p.getPlayerId();		 
		ClientProgram.sendStart(new StartPackage());
		init = true;
	}
	
	/**
	 * start the game
	 */
	public void start() {
		running = true;
	}
	
	/**
	 * stop the game
	 */
	public void stop() {
		running = false;
	}
	
	public void removePlayer(int id) {
		snake[id] = null;
		if (id == playerId) {
			//Lost
			lost = true;
		}
	}
	public void youWon() {
		won = true;
	}
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		if(init) {
			border.draw(g);
			for(int i = 0; i < snake.length; i++) {
				if(snake[i] != null) {
					snake[i].draw();
				}
			}
			itemList.forEach((item)->{
				item.draw(g);
			});
		}
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		if(won) {
			((LostWonScreen)sbg.getState(GameStates.LostWonScreen)).setWon(true);
			sbg.enterState(GameStates.LostWonScreen);
			running = false;
		}
		if(lost) {
			((LostWonScreen)sbg.getState(GameStates.LostWonScreen)).setWon(false);
			sbg.enterState(GameStates.LostWonScreen);
			running = false;
		}
		if(running) {
			Input input = gc.getInput();
			for(int i = 0; i < snake.length; i++) {
				if(snake[i] != null) {
					snake[i].update(direction[i],delta);
				}
			}
			if(snake[playerId].isSnakeBitingSnake(snake[playerId])) {
				snake[playerId].setHealth(snake[playerId].getHealth() - delta * 0.05f);
				snake[playerId].stopMoving();
			} 
			for(int i = 0; i < snake.length; i++) {
				for(int j = 0; j < snake.length; j++) {
					if(i == j) continue;
					if(snake[i] == null || snake[j] == null) continue;
					if(snake[i].isSnakeBitingSnake(snake[j])) {
						snake[i].setHealth(snake[i].getHealth() + delta * 0.05f);
						snake[j].setHealth(snake[j].getHealth() - delta * 0.05f);
						snake[i].setSpeedBuff(5000, 2.0f);
						snake[i].stopMoving();
					} 
				}
			}
			
			if(input.isKeyPressed(Input.KEY_LEFT)){
				direction[playerId] = Direction.LEFT;
	             ClientProgram.sendDirection(direction[playerId]);
			}
			if(input.isKeyPressed(Input.KEY_RIGHT)){
				direction[playerId] = Direction.RIGHT;
				ClientProgram.sendDirection(direction[playerId]);
			}
			if(input.isKeyPressed(Input.KEY_UP)){
				direction[playerId] = Direction.UP;
	            ClientProgram.sendDirection(direction[playerId]);
			}
			if(input.isKeyPressed(Input.KEY_DOWN)){
				direction[playerId] = Direction.DOWN;
	            ClientProgram.sendDirection(direction[playerId]);
			}
			
		}
	}
	
	public void setSnake(SnakeDataPackage snakeData) {
		if(running) {
			snake[snakeData.getPlayerId()].setSnake(snakeData);
			direction[snakeData.getPlayerId()] = snakeData.getLastDirection();
		}
	}
	
	public void removeItem(int id) {
		Item item = new ItemSize();
		item.setId(id);
		itemList.remove(item);
	}
	
	public void addNewItem(Item item) {
		itemList.add(item);
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return GameStates.SnakeGameClient;
	}

}
