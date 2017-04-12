package foopsnake.game;


import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

/**
 * This class renders the snake
 * @author johannes
 *
 */
public class Snake {
	
	public static int TILE_SIZE;
	private boolean server = false;
	
	private SnakeTile head;
	private SnakeTile tail;
	//absolute position of the head
	private Vector2f position;
	//position of the head in a grid
	private Vector2f gridPosition;
	private Image tile;
	private float speed = 0.04f;
	
	//Indicates that the snake changed direction the last update
	private boolean directionChange;
	
	public Vector2f getPosition() {
		return position;
	}
	public Vector2f getGridPosition() {
		return gridPosition;
	}
	public float getSpeed() {
		return speed;
	}
	public int getLastX() {
		return lastX;
	}
	public int getLastY() {
		return lastY;
	}
	public Direction getLastDirection() {
		return lastDirection;
	}
	public int getSize() {
		SnakeTile st = head;
		int i = 1;
		while(st.caudal != null) {
			i++;
			st = st.caudal;
		}
		return i;
	}
	/**
	 * In combination with position of the head, the position
	 * of the other parts can be determined.
	 * 
	 * TODO: sloppy writing, needs fixing
	 * @return
	 */
	public Direction[] getDirections() {
		int i = getSize();
		Direction[] dir = new Direction[i];
		i = 0;
		SnakeTile st = head;
		dir[i] = st.direction;
		st = st.caudal;
		while(st != null) {
			i++;
			dir[i] = st.direction;
			st = st.caudal;
		}
		return dir;
	}

	//Saves the position of the head after the last update
	private int lastX;
	private int lastY;
	private Direction lastDirection;
	
	private class SnakeTile {

		//direction of the tile
		private Direction direction;
		private SnakeTile cranial;
		private SnakeTile caudal;
	}
	
	/**
	 * Initializes a snake of a specific size, facing up
	 * @param size
	 */
	public Snake(int size,Vector2f gridPosition, boolean server) {
		this.server = server;
		if (!server) {
			try {
				tile = new Image("src/main/resources/snake-body.png");
			} catch (SlickException e) {
				System.out.println("Invalid resource");
				e.printStackTrace();	
			}
		}
		
		lastDirection = Direction.UP;
		lastY = (int) (gridPosition.y*TILE_SIZE);
		lastX = (int) (gridPosition.x*TILE_SIZE);
		
		head = new SnakeTile();
		head.direction = Direction.UP;
		position = new Vector2f();
		this.gridPosition = gridPosition;
		
		position.x = gridPosition.x*TILE_SIZE;
		position.y = gridPosition.y*TILE_SIZE;
		head.cranial = null;
		
		SnakeTile c = head;
		if (size > 1) {
			for(int i = 1; i < size; i++) {
				SnakeTile body = new SnakeTile();
				body.cranial = c;
				c.caudal = body;
				c.direction = Direction.UP;
				
				c = body;
			}	
		}
		tail = c;
	}
	
	/**
	 * Returns true when the snake changed direction
	 * @return
	 */
	public boolean directionChange() {
		return directionChange;
	}
	
	/** 
	 * Draw the snake
	 */
	public synchronized void draw() {
		if(!server) {
			SnakeTile c = head;
			int gridPositionX = (int)gridPosition.x;
			int gridPositionY = (int)gridPosition.y;
			float deltaX = position.x - gridPosition.x * TILE_SIZE;					 
			float deltaY =  position.y - gridPosition.y * TILE_SIZE;
			float delta = Math.abs(deltaX + deltaY);
			do {
				tile.draw((int)(gridPositionX*TILE_SIZE+deltaX), (int)(gridPositionY*TILE_SIZE+deltaY));
				if (c.direction == Direction.UP) {
					gridPositionY += 1;
					deltaY = -delta;
					deltaX = 0;
				}
				if (c.direction == Direction.DOWN) {
					gridPositionY -= 1;
					deltaY = delta;
					deltaX = 0;
				}
				if (c.direction == Direction.RIGHT) {
					gridPositionX -= 1;
					deltaX = delta;
					deltaY = 0;
				}
				if (c.direction == Direction.LEFT) {
					gridPositionX += 1;
					deltaX = -delta;
					deltaY = 0;
				}
				c = c.caudal;
			}
			while(c != null);
		}
	}
	
	/**
	 * Update position of snake
	 * @param direction direction of head
	 */
	public synchronized void update(Direction direction,int delta) {
		
		Direction newHeadDirection = null;
		boolean gridChange = false; //Indicates that the snake moved one tile
		if(lastDirection == Direction.UP) {
			position.y -= speed*delta;
			//check if new grid position reached
			if ((int)position.y <= lastY - TILE_SIZE) {				
				gridPosition.y -= 1;
				position.y = gridPosition.y *  TILE_SIZE;
				lastY = (int)position.y;
				newHeadDirection = Direction.UP;
				gridChange = true;
			}
		}
		if(lastDirection == Direction.RIGHT) {
			position.x += speed*delta;
			//check if new grid position reached
			if ((int)position.x >= lastX + TILE_SIZE) {	
				gridPosition.x += 1;
				position.x = gridPosition.x *  TILE_SIZE;
				lastX = (int)position.x;
				newHeadDirection = Direction.RIGHT;
				gridChange = true;
			}
		}
		if(lastDirection == Direction.DOWN) {
			position.y += speed*delta;
			//check if new grid position reached
			if ((int)position.y >= lastY + TILE_SIZE) {
				gridPosition.y += 1;
				position.y = gridPosition.y *  TILE_SIZE;
				lastY = (int)position.y;
				newHeadDirection = Direction.DOWN;
				gridChange = true;
			}
		}
		if(lastDirection == Direction.LEFT) {
			position.x -= speed*delta;
			//check if new grid position reached
			if ((int)position.x <= lastX - TILE_SIZE) {
				gridPosition.x -= 1;
				position.x = gridPosition.x *  TILE_SIZE;
				lastX = (int)position.x;
				newHeadDirection = Direction.LEFT;
				gridChange = true;
			}
		}
		//Apply changes to other parts of the snake
		if(gridChange) {
			SnakeTile c = tail;
			while(c.cranial != null) {
				c.direction = c.cranial.direction;
				c = c.cranial;
			}
			head.direction = newHeadDirection;
		}
		
		
		//we don't want the snake to change its direction when
		//it didn't reach a new grid position
		directionChange = false;
		if(gridChange) {
			//it cannot go down, if it goes up and so on
			if(direction == Direction.UP && lastDirection != Direction.DOWN) {
				lastDirection = Direction.UP;
				directionChange = true;
			} 
			if(direction == Direction.DOWN && lastDirection != Direction.UP) {
				lastDirection = Direction.DOWN;
				directionChange = true;
			}
			if(direction == Direction.RIGHT && lastDirection != Direction.LEFT) {
				lastDirection = Direction.RIGHT;
				directionChange = true;
			}
			if(direction == Direction.LEFT && lastDirection != Direction.RIGHT) {
				lastDirection = Direction.LEFT;
				directionChange = true;
			}
		}
	}
	
	/**
	 * Set properties of the snake
	 * 
	 * Note: implementation for communication with host is problematic
	 *  	 when clients have different resolution.
	 * 
	 * @param bodyParts position of every part of the snake
	 */
	public synchronized void setSnake(SnakeDataPackage snakeData) {	
		position = snakeData.getPosition();
		gridPosition = snakeData.getGridPosition();
		lastX = snakeData.getLastX();
		lastY = snakeData.getLastY();
		speed = snakeData.getSpeed();
		lastDirection = snakeData.getLastDirection();
		SnakeTile c = head;
		for(int i = 0; i < snakeData.getTileDirections().length; i++) {
			c.direction = snakeData.getTileDirections()[i];
			if ((c.caudal == null) && (i+1 < snakeData.getTileDirections().length)) {
				c.caudal = new SnakeTile();
				c.caudal.cranial = c;
				tail = c.caudal;
			}
			c = c.caudal;
		}
	}
	
	/**
	 * Increases the size of the snake by one.
	 */
	public synchronized void addBodyPart() {
		SnakeTile p = new SnakeTile();
		p.caudal = null;
		p.cranial = tail;
		tail.caudal = p;
		tail = p;
		
		p.direction = p.cranial.direction;
	}

}
	
