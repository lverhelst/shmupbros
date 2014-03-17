package Game;

import Game.Entity.Entity;
import Game.Entity.Playable;
import Game.Map.Map;
import Game.Map.Tile;
import Game.State.GameState;
import java.util.ArrayList;


/**
 *
 * @author Leon Verhelst
 */
public class AStar {
    private ArrayList<Tile> path;
    private ArrayList<Tile> openList;
    private ArrayList<Tile> closedList;
    
    
    private Map map;
    
    public AStar(){
        path = new ArrayList<Tile>();
        map = GameState.getMap();
    }
    
    public ArrayList<Tile> pathFind(Playable source, Playable target){
        openList = new ArrayList<Tile>();
        closedList = new ArrayList<Tile>();
        int x = (int)((source.getX() + source.getForceX() - source.getSize())/32);
        int y = (int)((source.getY() + source.getForceY() - source.getSize())/32);
        if(!(x >= 0 && y >= 0))
            return null;
        openList.add(map.getTile(x, y));
        
        
        x = (int)((target.getX() + target.getForceX() - target.getSize())/32);
        y = (int)((target.getY() + target.getForceY() - target.getSize())/32);
        if(!(x >= 0 && y >= 0))
            return null;
        Tile targetTile = map.getTile(x, y);
        Tile currentSquare = openList.get(0);
        map.resetTiles();
        
        
        ArrayList<Tile> adjacents = new ArrayList<Tile>();
        do{
            currentSquare = getLowestScore(currentSquare);
            closedList.add(currentSquare);
            openList.remove(currentSquare);
            
            
            if(closedList.contains(targetTile)){
                ArrayList<Tile> pth = new ArrayList<Tile>();
                Tile cur = targetTile;
                while(cur != null){
                    pth.add(cur);
                    cur = cur.parent;
                }
                //Path found
                return pth;
            }
            adjacents.clear();
            adjacents = getAdjacents(currentSquare);
            if(adjacents == null)
                return null;
            for(Tile ent : adjacents){
                if(closedList.contains(ent))
                    continue;
                if(!openList.contains(ent)){
                    ent.parent = currentSquare;
                    openList.add(ent);
                    
                    
                }else{
                    // test if using the current G score make the ent F score lower, if yes update the parent because it means its a better path
                    
                }
            }
            
            
        }while(!openList.isEmpty());      
        //no path
        return null;
    }
    
    private Tile getLowestScore(Tile currentSquare){
        Tile lowest = null;
        double lowscore = 999.0;
        for(Tile t : openList){
            double score = Math.abs(currentSquare.getX() - t.getX() + currentSquare.getY() - t.getY());
            if(score < lowscore){
                lowscore = score;
                lowest = t;
            }   
        }
        return lowest;
    }
    
    private ArrayList<Tile> getAdjacents(Tile current){
        ArrayList<Tile> adj = new ArrayList<>();
        if(current == null)
            return null;
        
        int x = (int)current.getX()/32;
        int y = (int)current.getY()/32 + 1;
        if(map.getPassable(x, y)){
            adj.add(map.getTile(x, y));
            
        }
        y -= 2;
        if(map.getPassable(x, y)){
            adj.add(map.getTile(x, y));
            
        }
        y += 1;
        x += 1;
        if(map.getPassable(x, y)){
            adj.add(map.getTile(x, y));
            
        }
        x -= 2;
        if(map.getPassable(x, y)){
            adj.add(map.getTile(x, y));
        }
        return adj;
    }

    /**
     * @param map the map to set
     */
    public void setMap(Map map) {
        this.map = map;
    }
    
    
}
