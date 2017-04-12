package foopsnake.game;

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
	Snake snake[];
	Direction[] direction;
	int playerId;
	boolean running = false;

	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
	}
	
	/**
	 * server sends data to initialize the snake
	 * @param p
	 */
	public void initialize(InitPackage p) {
		snake = new Snake[p.getNumberOfPlayers()];
		direction = new Direction[p.getNumberOfPlayers()];
		for(int i = 0; i < p.getNumberOfPlayers(); i++) {
			snake[i] = new Snake(p.getSize()[i], p.getPosition()[i],false);
			direction[i] = Direction.UP;
		}
		playerId = p.getPlayerId();
		ClientProgram.sendStart(new StartPackage());
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
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		if(running) {
			for(int i = 0; i < snake.length; i++) {
				snake[i].draw();
			}
			
		}
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		if(running) {
			Input input = gc.getInput();
			for(int i = 0; i < snake.length; i++) {
				snake[i].update(direction[i],delta);
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
			if(input.isKeyPressed(Input.KEY_SPACE)){
	            snake[playerId].addBodyPart();
			}
		}
	}
	
	public void setSnake(SnakeDataPackage snakeData) {
		snake[snakeData.getPlayerId()].setSnake(snakeData);
		direction[snakeData.getPlayerId()] = snakeData.getLastDirection();
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return GameStates.SnakeGameClient;
	}

}
