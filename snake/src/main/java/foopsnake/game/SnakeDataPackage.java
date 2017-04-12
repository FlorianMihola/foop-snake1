package foopsnake.game;

import org.newdawn.slick.geom.Vector2f;

public class SnakeDataPackage {
	
	private int playerId;
	
	private Vector2f position;
	private Vector2f gridPosition;
	private float speed = 0.05f;
	
	private int lastX;
	private int lastY;
	private Direction lastDirection;
	
	private Direction[] tileDirections;

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public Vector2f getGridPosition() {
		return gridPosition;
	}

	public void setGridPosition(Vector2f gridPosition) {
		this.gridPosition = gridPosition;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public int getLastX() {
		return lastX;
	}

	public void setLastX(int lastX) {
		this.lastX = lastX;
	}

	public int getLastY() {
		return lastY;
	}

	public void setLastY(int lastY) {
		this.lastY = lastY;
	}

	public Direction getLastDirection() {
		return lastDirection;
	}

	public void setLastDirection(Direction lastDirection) {
		this.lastDirection = lastDirection;
	}

	public Direction[] getTileDirections() {
		return tileDirections;
	}

	public void setTileDirections(Direction[] tileDirections) {
		this.tileDirections = tileDirections;
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}


}
