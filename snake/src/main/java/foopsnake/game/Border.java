package foopsnake.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.Color;

public class Border {

	private Rectangle rect_top;
	private Rectangle rect_bottom;
	private Rectangle rect_left;
	private Rectangle rect_right;

	public Border(int size) {
		rect_top = new Rectangle(0, 0, Game.WIDTH, size);
		rect_bottom = new Rectangle(0, Game.HEIGHT-size, Game.WIDTH,size);
		rect_left = new Rectangle(0, 0, size, Game.WIDTH);
		rect_right = new Rectangle(Game.WIDTH-size, 0, size, Game.WIDTH);
	}
	
	public void draw(Graphics g) {
		g.setColor(new Color(0,0,0));
		g.fill(rect_top);				
		g.fill(rect_bottom);			
		g.fill(rect_left);			
		g.fill(rect_right);			
	}

}
