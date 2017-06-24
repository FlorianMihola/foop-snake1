package foopsnake.game;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.gui.TextField;


public class ConnectionScreen extends BasicGameState {

	private TextField ipHost;
	private TrueTypeFont trueTypeFont;
	private MouseOverArea connectButton;
	private MouseOverArea hostButton;
	private MouseOverArea startButton;
	private boolean mousePressed = false;
	private boolean showCTH = false;
	private boolean showSGH = false;
	
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		java.awt.Font font = new java.awt.Font("Verdana", java.awt.Font.BOLD, 20);
		trueTypeFont = new TrueTypeFont(font, true);
		ipHost = new TextField(gc,trueTypeFont,300,100,175,30);
		ipHost.setAcceptingInput(true);
		
		connectButton = new MouseOverArea(gc, new Image("src/main/resources/button-red.png"), 500,90);
		hostButton = new MouseOverArea(gc, new Image("src/main/resources/button-red.png"), 300,180);
		startButton = new MouseOverArea(gc, new Image("src/main/resources/button-red.png"), 300,270);
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.setBackground(new Color(255,255,255));
		g.clear();
		// render some text to the screen
		trueTypeFont.drawString(300.0f, 50.0f, "Connect to a host:", Color.green);
		trueTypeFont.drawString(300.0f, 150.0f, "Be the host:", Color.green);
		trueTypeFont.drawString(300.0f, 240.0f, "Start the game", Color.green);
		
		if (showCTH) {
			trueTypeFont.drawString(560.0f, 100.0f, "Connecting to host...", Color.green);
		}
		if (showSGH){
			trueTypeFont.drawString(400.0f, 200.0f, "You are now host...", Color.green);
		}
		connectButton.render(gc, g);
		hostButton.render(gc, g);
		startButton.render(gc, g);
		ipHost.render(gc, g);
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		Input input = gc.getInput();
		
		if(connectButton.isMouseOver()) {
			if(input.isMouseButtonDown(input.MOUSE_LEFT_BUTTON)) {
				mousePressed = true;
			} else {
				if (mousePressed) {
					showCTH = true;
					try {
						sbg.enterState(GameStates.SnakeGameClient);
						ClientProgram.init(ipHost.getText(),(SnakeGameClient)sbg.getState(GameStates.SnakeGameClient));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		if(hostButton.isMouseOver()) {
			if(input.isMouseButtonDown(input.MOUSE_LEFT_BUTTON)) {
				mousePressed = true;
			} else {
				if (mousePressed) {
					showSGH = true;
					try {
						ServerProgram.init();
						ServerProgram.setHost();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		if(startButton.isMouseOver()) {
			if(input.isMouseButtonDown(input.MOUSE_LEFT_BUTTON)) {
				mousePressed = true;
			} else {
				if (mousePressed) {
					try {
						sbg.enterState(GameStates.SnakeGameClient);
						ClientProgram.init(ipHost.getText(),(SnakeGameClient)sbg.getState(GameStates.SnakeGameClient));
						Thread.sleep(500);
						ServerProgram.startGame();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		if(!input.isMouseButtonDown(input.MOUSE_LEFT_BUTTON)) {
			mousePressed = false;
		} 
	}

	@Override
	public int getID() {
		return GameStates.ConnectionScreen;
	}

}
