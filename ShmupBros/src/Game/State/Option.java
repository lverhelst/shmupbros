package Game.State;

import Game.Entity.Playable;
import Game.StateManager;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
* Options used to display and control the menu state
* @authors Daniel Finke, Emery Berg, Leon Verhelst
*/

public class Option extends BasicGameState {
    private final int ID;
    private MenuItem option;
    private MenuItem name;
    private MenuItem back;
    private MenuItem color;
    private Playable vehicle;
    
    public Option(int id) {
        ID=id;
    }
    
    @Override
    public int getID() {
        return ID;
    }
   
    @Override
    public void init(GameContainer container, StateBasedGame game) {
        option = new MenuItem("Assets/Options.png", 100, 100);
        name = new MenuItem("Assets/name.png", 125, 150);
        back = new MenuItem("Assets/back.png", 125, 200);
        color = new MenuItem("Assets/colors.png", 150, 300);
        
        vehicle = new Playable(32);
    }
    
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) {
        Input input = container.getInput();
        int mouseX = input.getMouseX();
        int mouseY = input.getMouseY();
        
        if(name.contains(mouseX, mouseY));
        if(back.contains(mouseX, mouseY) && input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
            game.enterState(StateManager.MENU);
        if(color.contains(mouseX, mouseY) && input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            mouseX = (mouseX - 150)%color.getSprite().getWidth();
            mouseY = (mouseY - 300)%color.getSprite().getHeight();
            
            vehicle.setColor(color.getSprite().getColor(mouseX, mouseY));
        }
    }

    @Override public void render(GameContainer container, StateBasedGame game, Graphics graphics) {
        option.render(graphics);
        name.render(graphics);
        back.render(graphics);
        color.render(graphics);
        
        graphics.drawImage(vehicle.getSprite(), 300, 316, vehicle.getColor());
    }
}