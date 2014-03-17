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

        openList.add(map.getTile(x, y));
        
        
        x = (int)((target.getX() + target.getForceX() - target.getSize())/32);
        y = (int)((target.getY() + target.getForceY() - target.getSize())/32);
        Tile targetTile = map.getTile(x, y);
        Tile currentSquare = openList.get(0);
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
                    System.out.println(cur);
                }
                //Path found
                return pth;
            }
            adjacents.clear();
            adjacents = getAdjacents(currentSquare);
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
        System.out.println(lowscore);
        return lowest;
    }
    
    private ArrayList<Tile> getAdjacents(Tile current){
        ArrayList<Tile> adj = new ArrayList<>();
        Tile t = map.getTile((int)current.getX()/32, (int)current.getY()/32 + 1);
        if(t.getPassable()){
            adj.add(t);
            
        }
        t = map.getTile((int)current.getX()/32, (int)current.getY()/32 - 1);
        if(t.getPassable()){
            adj.add(t);

        }
        t = map.getTile((int)current.getX()/32 - 1, (int)current.getY()/32);
        if(t.getPassable()){
            adj.add(t);

        }
        t = map.getTile((int)current.getX()/32 + 1, (int)current.getY()/32);
        if(t.getPassable()){
            adj.add(t);

        }
        return adj;
    }
    
    
}
