package foopsnake.game;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class LostWonScreen extends BasicGameState{
	private TrueTypeFont trueTypeFont;
	private boolean won = false;
	
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		java.awt.Font font = new java.awt.Font("Verdana", java.awt.Font.BOLD, 40);
		trueTypeFont = new TrueTypeFont(font, true);
	}
	
	public void setWon(boolean won) {
		this.won = won;
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.setBackground(new Color(255,255,255));
		g.clear();
		// render some text to the screen
		if(won) {
			trueTypeFont.drawString(300.0f, 200.0f, "YOU WON", Color.green);
		} else {
			trueTypeFont.drawString(300.0f, 200.0f, "YOU LOST", Color.red);
		}
		
		
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		Input input = gc.getInput();
		
		if(input.isKeyPressed(Input.KEY_SPACE)){
			if(ServerProgram.isHost()) {	
				((SnakeGameClient) sbg.getState(GameStates.SnakeGameClient)).reset();
				sbg.enterState(GameStates.SnakeGameClient);
				ServerProgram.startGame();
			} else {
				((SnakeGameClient) sbg.getState(GameStates.SnakeGameClient)).reset();
				sbg.enterState(GameStates.SnakeGameClient);
			}
		}
	}

	@Override
	public int getID() {
		return GameStates.LostWonScreen;
	}
}
