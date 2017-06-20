package foopsnake.game;


import org.newdawn.slick.Color;
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
	private float speed = 0.08f;
	private boolean hit = false;
	private boolean borderhit = false;
	private long timeSpeedBuff = 0;
	private float speedBuff = 1.0f;
	private long invincibleTime = 0;
	private long inverseDirectionTime = 0;
	private boolean removedBodyPart = false;
	
	private int borderSize;
	private float health = 100.0f;
	private float maxHealth = 100.0f;
	private Color color;
	
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
	public Snake(int size,Vector2f gridPosition, int borderSize, boolean server) {
		this.color = new Color(1,0,0);
		this.borderSize = borderSize;
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
	
	public void setColor(float r, float g, float b) {
		this.color = new Color(r,g,b);
	}
	
	/**
	 * Returns true when the snake changed direction
	 * @return
	 */
	public boolean directionChange() {
		return directionChange;
	}
	
	public int getBoderSize() {
		return this.borderSize;
	}
	
	public long getSpeedBuffTime() {
		return timeSpeedBuff;
	}
	
	public float getSpeedBuff() {
		return speedBuff;
	}
	public long getDirectionBuffTime() {
		return inverseDirectionTime;
	}
	public long getInvincibleBuffTime() {
		return invincibleTime;
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
				if(c == head) {
					tile.drawFlash((int)(gridPositionX*TILE_SIZE+deltaX), (int)(gridPositionY*TILE_SIZE+deltaY),TILE_SIZE, TILE_SIZE, color.darker(1-(health/maxHealth)+0.3f));
				} else {
					if (invincibleTime > 0) {
						tile.drawFlash((int)(gridPositionX*TILE_SIZE+deltaX), (int)(gridPositionY*TILE_SIZE+deltaY),TILE_SIZE, TILE_SIZE, new Color(1.0f,0.9f,0.0f));
					}else {
						tile.drawFlash((int)(gridPositionX*TILE_SIZE+deltaX), (int)(gridPositionY*TILE_SIZE+deltaY),TILE_SIZE, TILE_SIZE, color.darker(1-(health/maxHealth)));
					}
					
				}
				
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
	
	public void setHealth(float health) {
		if(health > maxHealth) {
			this.health = maxHealth;
		} else {
			this.health = health;
		}
		
	}
	public float getHealth() {
		return this.health;
	}
	
	public void inverseDirection(long time) {
		this.inverseDirectionTime = time;
	}
	
	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
	}
	
	/**
	 * Update position of snake
	 * @param direction direction of head
	 */
	public synchronized void update(Direction direction, int delta) {
		
		if (timeSpeedBuff > 0) {
			timeSpeedBuff -= delta;
		} else {
			timeSpeedBuff = 0;
			speedBuff = 1.0f;
		}
		
		if (invincibleTime > 0) {
			invincibleTime -= delta;
		} else {
			invincibleTime = 0;
		}
		if (inverseDirectionTime > 0) {
			inverseDirectionTime -= delta;
		} else {
			inverseDirectionTime = 0;
		}
		
		if(health < maxHealth) {
			removeBodyPart();	
		}
		
		Direction newHeadDirection = null;
		boolean gridChange = false; //Indicates that the snake moved one tile
		if(lastDirection == Direction.UP) {
			position.y -= speed*delta*speedBuff;
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
			position.x += speed*delta*speedBuff;
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
			position.y += speed*delta*speedBuff;
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
			position.x -= speed*delta*speedBuff;
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
		snakeHittingBorder(delta);
		if(borderhit) {
			hit = true;
		}
		if (hit) {
			//If you hit the border, you can navigate the snake to another direction,
			//we act like it got to the next grid
			gridChange = true;
		}
		if (inverseDirectionTime > 0) {
			switch(direction) {
			case UP: direction = Direction.DOWN; break;
			case DOWN: direction = Direction.UP; break;
			case RIGHT: direction = Direction.LEFT; break;
			case LEFT: direction = Direction.RIGHT; break;
			}
		}
		if(gridChange) {
			//it cannot go down, if it goes up and so on
			if(direction == Direction.UP && (lastDirection != Direction.DOWN || hit)) {
				lastDirection = Direction.UP;
				directionChange = true;
			} 
			if(direction == Direction.DOWN && (lastDirection != Direction.UP  || hit)) {
				lastDirection = Direction.DOWN;
				directionChange = true;
			}
			if(direction == Direction.RIGHT && (lastDirection != Direction.LEFT || hit)) {
				lastDirection = Direction.RIGHT;
				directionChange = true;
			}
			if(direction == Direction.LEFT && (lastDirection != Direction.RIGHT || hit)) {
				lastDirection = Direction.LEFT;
				directionChange = true;
			}
		}
		hit = false;
	}
	
	/**
	 * Buff the speed of the snake for a certain period of time. 
	 * @param time in ms
	 * @param buffFactor 
	 */
	public void setSpeedBuff(int time, float buffFactor) {
		this.speedBuff = buffFactor;
		this.timeSpeedBuff = time;
	}
	
	public boolean isSnakeBitingSnake(Snake snake2) {
	
		Vector2f positionHead = gridPosition.copy();
		Vector2f tileSnake2 = snake2.gridPosition.copy();
		boolean self = false;
		if(this.head == snake2.head) {
			self = true;
		}
		SnakeTile c = snake2.tail;
		SnakeTile last = null;
		while(c.cranial != null) {
			SnakeTile d = new SnakeTile();
			d.direction = c.cranial.direction;
			d.caudal = last;
			if(d.caudal != null) {
				d.caudal.cranial = d;
			}
			last = d;
			c = c.cranial;
		}
		SnakeTile d = new SnakeTile();
		d.direction = snake2.lastDirection;
		d.caudal = last;
		d.caudal.cranial = d;
		c = d;
		switch(this.lastDirection) {
		case UP : positionHead.y -= 1; break;
		case DOWN : positionHead.y += 1; break;
		case LEFT : positionHead.x -= 1; break;
		case RIGHT : positionHead.x += 1; break;
		}
		while(c != null) {
			if(!snake2.borderhit) {
				switch(c.direction) {
				case UP : tileSnake2.y -= 1; break;
				case DOWN : tileSnake2.y += 1; break;
				case LEFT : tileSnake2.x -= 1; break;
				case RIGHT : tileSnake2.x += 1; break;
				}
			}
			if((int)tileSnake2.distance(positionHead) == 0 && (c.cranial != null || !self)) {
				return true;
			}
			if(!snake2.borderhit) {
				switch(c.direction) {
				case UP : tileSnake2.y += 1; break;
				case DOWN : tileSnake2.y -= 1; break;
				case LEFT : tileSnake2.x += 1; break;
				case RIGHT : tileSnake2.x -= 1; break;
				}
			}
			c = c.caudal;
			if (c != null) {
				switch(c.direction) {
				case UP : tileSnake2.y += 1; break;
				case DOWN : tileSnake2.y -= 1; break;
				case LEFT : tileSnake2.x += 1; break;
				case RIGHT : tileSnake2.x -= 1; break;
				}
			}
		}
		
		return false;
	}
	
	public boolean isCoordinateOnSnake(Vector2f coordinate) {
		SnakeTile c = head;
		Vector2f position = gridPosition.copy();
		while(c != null) {
			if(coordinate.distance(position) == 0.0f) {
				return true;
			}
			c = c.caudal;
			if (c != null) {
				switch(c.direction) {
				case UP : position.y += 1; break;
				case DOWN : position.y -= 1; break;
				case LEFT : position.x += 1; break;
				case RIGHT : position.x -= 1; break;
				}
			}
		}
		return false;
	}
	
	public void stopMoving() {
		hit = true;
		position.x = gridPosition.x * TILE_SIZE;
		position.y = gridPosition.y * TILE_SIZE;
	}
	
	public int getBorderSize() {
		return this.borderSize;
	}
	
	public void dealDamage(float damage) {
		if(invincibleTime == 0) {
			health -= damage;
			removedBodyPart = false;
		}
	}
	
	private boolean snakeHittingBorder(long delta) {
		borderhit = false;
	    if(position.x < borderSize) {
	  		position.x = borderSize;
	  		dealDamage(delta * 0.05f);
	  		borderhit = true;
	    }
	    if(position.x > Game.WIDTH - borderSize*2) {
	    	position.x = Game.WIDTH - borderSize*2;
	    	dealDamage(delta * 0.05f);
	    	borderhit = true;
	    }
	    if(position.y > Game.HEIGHT - borderSize*2) {
	    	position.y = Game.HEIGHT - borderSize*2;
	    	dealDamage(delta * 0.05f);
	    	borderhit = true;
	    }
	    if(position.y < borderSize) {
	    	position.y = borderSize;
	    	dealDamage(delta * 0.05f);
	    	borderhit = true;
	    }
	    return borderhit;
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
		health = snakeData.getHealth();
		speedBuff = snakeData.getSpeedBuff();
		inverseDirectionTime = snakeData.getDirectionBuffTime();
		invincibleTime = snakeData.getInvincibleBuffTime();
		timeSpeedBuff = snakeData.getSpeedBuffTime();
		SnakeTile c = head;
		for(int i = 0; i < snakeData.getTileDirections().length; i++) {
			c.direction = snakeData.getTileDirections()[i];
			if  (i+1 < snakeData.getTileDirections().length) {
				c.caudal = new SnakeTile();
				c.caudal.cranial = c;
				tail = c.caudal;
			}
			c = c.caudal;
		}
	}
	public void increaseSpeed(float increase) {
		speed += increase;
	}
	
	public void setInvincible(long time) {
		this.invincibleTime = time;
	}
	
	/**
	 * Increases the size of the snake by one.
	 */
	public synchronized void addBodyPart() {
		//Increase max health
		maxHealth += 10;
		health += 10;
		
		SnakeTile p = new SnakeTile();
		p.caudal = null;
		p.cranial = tail;
		tail.caudal = p;
		tail = p;
		
		p.direction = p.cranial.direction;
	}
	
	public synchronized void removeBodyPart() {
		if(getSize() > 8 && !removedBodyPart) {
			maxHealth -= 10;
			tail.cranial.caudal = null;
			tail = tail.cranial;
			removedBodyPart = true;
		}
	}

}
	
