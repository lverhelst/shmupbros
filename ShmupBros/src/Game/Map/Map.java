package Game.Map;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.newdawn.slick.Graphics;

/**
 * Map: Used to hold the information in the map and is used to render it
 * @author Daniel Finke, Emery Berg, Leon Verhelst
 */
public class Map {
    private final int TILE_SIZE = 32; //size of tiles to be drawn
    private int width;
    private int height;
    private Tile[][] map; //holds the tiles of the map
	
    /**
     * constructor which takes a string for the map name to load
     * @param filename File name of map to load
    */
    public Map(String filename, int width, int height) {
        File file;
        Scanner input;
        String row;
        
        this.width = width;
        this.height = height;
		
        file = new File(filename); //creates a link to the file
        
        //ensures the map is loaded correctly
        try {
            input = new Scanner(file); //sets the scanner to open the file
            int x = Integer.parseInt(input.nextLine()); //reads in x
            int y = Integer.parseInt(input.nextLine()); //reads in y
            int col = 0; //used for scanning in file
		
            //sets map to the sizes contained in the 1st 2 lines of the file
            map = new Tile[x][y];

            //reads in the map file
            while(input.hasNext()){
                row = input.nextLine();
                    
                for(int i = 0; i < map.length; ++i){
                    switch(row.charAt(i)){
                        case '0': //empty space
       				        map[i][col] = new Tile(i*TILE_SIZE,
                                    (col*TILE_SIZE), true);
                            break;
                        case '1': //visible wall
                            map[i][col] = new Concrete(i*TILE_SIZE,
                                    (col*TILE_SIZE), false);
                            break;
                        case '2': //invisible wall
                            map[i][col] = new Tile(i*TILE_SIZE,
                                    (col*TILE_SIZE), false);
                            break;
                    }
                }
                ++col;
            }
        } catch(FileNotFoundException fnfe) { //prints error if file load fails
            System.out.println(fnfe.getMessage());
        }
    }

    /**
     * @return Tile Size 
     */
    public int getTileSize() { return TILE_SIZE; }
    
    /**
     * @return Width of current map
     */
    public int getWidth() { return map.length; }
     
    /**
     * @return Height of current Map
     */
    public int getHeight() { return map[0].length; }
    
    public Tile getTile(int x, int y){
        return map[x][y];
    }
    
    /**
     * Check if location denoted by i and j is passable on the linked map addresser
     * @param i X-Coordinate to check
     * @param j Y Coordinate
     * @return If possible or not
    */
    public boolean getPassable(int i, int j){
        if(i < map.length && j < map[0].length && i >= 0 & j >= 0)
            return map[i][j].getPassable();
        return false;
    }
	
    /**
     * Renders the map
     */
    public void render(Graphics graphics, int x, int y) {
        int aWidth = x + width;
        int aHeight = y + height;
        
        if(x < 0)
            x = 0;
        
        if(y < 0)
            y = 0;
        
        if(aWidth > getWidth())
            aWidth = getWidth();
        
        if(aHeight > getWidth())
            aHeight = getHeight();
        
        for (int i = x; i < aWidth; ++i) {
            for (int j = y; j < aHeight; ++j) {
                map[i][j].render(graphics);
            }
        }
    }
    
    public void resetTiles(){
        for (Tile[] map1 : map) {
            for (Tile t : map1) {
                t.parent = null;
                t.pathnode = false;
                t.isClosed = false;
                t.score = 0;
            }
        }
    }
}