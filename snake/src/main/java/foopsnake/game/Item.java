package foopsnake.game;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public abstract class Item {
	
	private Color color;
	private Vector2f position;
	private static int counter = 0;
	private int id;
	
	protected Item(Vector2f position, Color color) {
		this.position = position;
		this.color = color;
		id = counter;
		counter++;
	}
	
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillOval(position.x*Game.TILE_SIZE+(Game.TILE_SIZE/2-Game.TILE_SIZE/3), position.y*Game.TILE_SIZE+(Game.TILE_SIZE/2-Game.TILE_SIZE/3), Game.TILE_SIZE/1.5f, Game.TILE_SIZE/1.5f);
	}
	
	public abstract void doEffect(Snake snake);
	
	public Vector2f getPosition() {
		return position;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof Item) {
			if (this.id == ((Item) o).id) {
				return true;
			}
		}
		return false;
	}
	
	
}
