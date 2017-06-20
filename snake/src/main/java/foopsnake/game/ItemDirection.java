package foopsnake.game;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

public class ItemDirection extends Item {
	static Color color = new Color(0.0f, 0.2f, 0.8f);
	public ItemDirection() {
		super(new Vector2f(0.0f,0.0f), color);
	}
	public ItemDirection(Vector2f position) {
		super(position, color);
	}
	@Override
	public void doEffect(Snake snake) {
	}
}
