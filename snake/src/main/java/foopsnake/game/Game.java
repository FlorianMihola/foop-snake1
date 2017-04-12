package foopsnake.game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A game using Slick2d
 */
public class Game extends StateBasedGame {

    /** Screen width */
    private static final int WIDTH = 800;
    /** Screen height */
    private static final int HEIGHT = 600;
    
    public Game() {
        super("Simple Snake Game");
        this.addState(new ConnectionScreen());
        this.addState(new SnakeGameClient());
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Game());
        app.setDisplayMode(WIDTH, HEIGHT, false);
        app.setForceExit(false);
        app.start();
    }

	@Override
	public void initStatesList(GameContainer gc) throws SlickException {
		gc.getGraphics().setBackground(new Color(255,255,255));
		Image tileSize = new Image("src/main/resources/snake-body.png");
		Snake.TILE_SIZE = tileSize.getWidth();
		this.enterState(GameStates.ConnectionScreen);
	}

}


