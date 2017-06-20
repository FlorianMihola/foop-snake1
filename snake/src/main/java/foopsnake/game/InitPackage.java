package foopsnake.game;

import org.newdawn.slick.geom.Vector2f;

/**
 * Package holds information about size and position of the snake
 * @author johannes
 *
 */
public class InitPackage {
	private int size[];
	private Vector2f position[];
	private int numberOfPlayers;
	private int playerId;
	private int borderSize;
	
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}
	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}
	public int[] getSize() {
		return size;
	}
	public void setSize(int[] size) {
		this.size = size;
	}
	public Vector2f[] getPosition() {
		return position;
	}
	public void setPosition(Vector2f[] position) {
		this.position = position;
	}
	public void setBorderSize(int size) {
		this.borderSize = size;
	}
	public int getBorderSize() {
		return this.borderSize;
	}
	
	
	
}
