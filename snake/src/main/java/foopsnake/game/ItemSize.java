package foopsnake.game;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

public class ItemSize extends Item {
	static Color color = new Color(0.8f, 0.0f, 0.0f);
	public ItemSize() {
		super(new Vector2f(0.0f,0.0f), color);
	}
	public ItemSize(Vector2f position) {
		super(position, color);
	}
	@Override
	public void doEffect(Snake snake) {
		snake.addBodyPart();
	}

}
