package Game;

import Game.State.GameState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

/**
 * StateManager: Creates the needed states and initializes them
 * @authors Daniel, Emery, Leon
 */
public class StateManager extends StateBasedGame {
    public static final int GAME=0; //used for setting the state ids
    
    /**
     * default constructor which creates and adds the needed states and starts the menu
     */
    public StateManager(){
        super("ShmupBros");
        addState(new GameState(StateManager.GAME));
        enterState(GAME);
    }
    
    /**
     * Unused
     * @param gc 
     */
    @Override public void initStatesList(GameContainer gc) {
        //initializes all the states (Not used)
    }
}