package Game.State;

import Game.StateManager;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * MenuState: Used to start and display the main menu 
 * @authors Daniel Finke, Emery Berg, Leon Verhelst
 */
public class Menu extends BasicGameState {
    private final int ID; //holds the current states ID
    private MenuItem start;
    private MenuItem option;
    private MenuItem exit;
    private Image title;
   
    /**
     * Constructor which takes an integer parameter for state ID
     * @param id Current state identifier
     */
    public Menu(int id) {
        ID=id;
    }
    
    /**
     * Return current state's ID
     * @return Current ID for state
     */
    @Override public int getID() {
        return ID;
    }
    
    /**
     * initializes the menu variables
     * @param gc The game container
     * @param sbg  Current game state
     */
    @Override public void init(GameContainer gc, StateBasedGame sbg) {
        //loads images for title
        try {
            title = new Image("Assets/Title.png");
            
        } catch (SlickException e) {
            System.out.println("Error loading title image: " + e.toString());
        }
        
        start = new MenuItem("Assets/Start.png", 125, 225);
        option = new MenuItem("Assets/Options.png", 125, 275);
        exit = new MenuItem("Assets/Exit.png", 125, 325);
    }

    /**
     * takes users input and updates the menu
     * @param gc StateManager Container
     * @param sbg Current StateManager State
     * @param i unused
     */
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int i) {
        Input input = gc.getInput();

        int mouseX = input.getMouseX();
        int mouseY = input.getMouseY();
        
        //checks if mouse button is pressed and mouse is within start button
        if(start.contains(mouseX, mouseY) 
                && input.isMousePressed(Input.MOUSE_LEFT_BUTTON)){
            
            ((GameState)sbg.getState(StateManager.GAME)).connect("224.0.1.100", 3000);
            sbg.enterState(StateManager.GAME); //starts a new game           
        }
        
        if(option.contains(mouseX, mouseY) 
                && input.isMousePressed(Input.MOUSE_LEFT_BUTTON)){
            sbg.enterState(StateManager.OPTIONS);            
        }
        
        if(exit.contains(mouseX, mouseY) 
                && input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
            gc.exit(); //requests an exit
    }
    
    /**
     * renders the menu
     * @param gc Current StateManager Container
     * @param sbg Current StateManager State
     * @param graphics Slick/LJWGL Graphics
     */
    @Override 
    public void render(GameContainer gc, StateBasedGame sbg, Graphics graphics) {        
        title.draw(100, 100);
        start.render(graphics);
        option.render(graphics);
        exit.render(graphics);
        
        graphics.drawString("Controls\nRight Control: Fire\nW: Move Up"
                + "\nS: Move Down\nA: Move Left\nD: Move Right", 700, 400);
    }
}