package Ai;

import Game.Entity.Playable;
import Game.Map.Map;
import Game.Map.Tile;
import Game.State.GameState;
import java.util.ArrayList;

/**
 * @author Leon Verhelst
 */
public class AStar {
    private ArrayList<Tile> openList;
    private ArrayList<Tile> closedList;
    private Tile tar;
    
    //holds the search space
    private static Map map;
    
    /**
     * Default constructor, finds the search space if not already present
     */
    public AStar(){        
        if(map == null)
            map = GameState.getMap();
    }
    
    /**
     * Generates a path between the source and the target
     * @param source the playable entity needing the path
     * @param target the playable entity which is being path'ed too
     * @return a ArrayList of tiles which is used as a path
     */
    public ArrayList<Tile> pathFind(Playable source, Playable target){
        openList = new ArrayList();
        closedList = new ArrayList();
        int x = (int)((source.getX() + source.getForceX() - source.getSize())/32);
        int y = (int)((source.getY() + source.getForceY() - source.getSize())/32);
        if(!(x >= 0 && y >= 0))
            return null;
        
        openList.add(map.getTile(x, y));
        if(target == null)
            return null;
        
        x = (int)((target.getX() + target.getForceX() - target.getSize())/32);
        y = (int)((target.getY() + target.getForceY() - target.getSize())/32);
        if(!(x >= 0 && y >= 0))
            return null;
        
        Tile targetTile = map.getTile(x, y);
        tar = targetTile;
        Tile currentSquare = openList.get(0);
        map.resetTiles();
        
        ArrayList<Tile> adjacents = new ArrayList();
        do{
            currentSquare = getLowestScore(currentSquare);
            currentSquare.isClosed = true;
            closedList.add(currentSquare);
            openList.remove(currentSquare);
            
            
            if(closedList.contains(targetTile)) {
                ArrayList<Tile> pth = new ArrayList();
                Tile cur = targetTile;
                
                double lastslope = Math.PI;
                double slope;
                while(cur != null){
                    //calculate slope 
                    if(cur.parent != null) {
                        slope = (cur.getY() - cur.parent.getY())/(cur.getX() - cur.parent.getX());
                        //System.out.println(slope);
                        if(lastslope != slope){
                            
                            lastslope = slope;
                            pth.add(cur);
                        } else {
                            //slope is the same, do not add
                            pth.add(cur);
                        }
                    }    
                    else {
                        break;
                    }
                    cur = cur.parent;
                }
                //remove target location nodes
                if(pth.size() >= 1)
                  pth.remove(pth.size() -1);
               
                if(pth.size() > 0)
                  pth.remove(0);
                
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
                }
            }            
            
        }while(!openList.isEmpty());  
        
        //no path found
        return null;
    }
    
    /**
     * 
     * @param currentSquare
     * @return 
     */
    private Tile getLowestScore(Tile currentSquare){
        Tile lowest = null;
        double lowscore = Double.MAX_VALUE;
        for(Tile t : openList){
            //TODO better score heuristic. Direct is better (above, below, left, right)
            //angles are not
            int diffx = (int) (currentSquare.getX() - t.getX())/32;
            int diffy = (int) (currentSquare.getY() - t.getY())/32;
           
            //Manhatten distance 
            double diffdist = Math.abs(currentSquare.getX() - tar.getX()) + Math.abs(tar.getY() - currentSquare.getY());
            double score = (diffx == 0 || diffy == 0) ? 10 : 14;
            
            score += (diffdist/32 + ((t.parent == null) ? 0 : t.parent.score));
                        
            //Only calculate once
            if(t.score == 0)
                t.score = score;
            if(score < lowscore){
                lowscore = score;
                lowest = t;
            }   
        }
        return lowest;
    }
    
    /**
     * Used to get the surrounding tiles, based on a single tiles location
     * @param current the tile to find the surrounding tiles of
     * @return the ArrayList of tiles which surround the passed in tile
     */
    private ArrayList<Tile> getAdjacents(Tile current){
        ArrayList<Tile> adj = new ArrayList<>();
        
        if(current == null)
            return null;        
        
        int x = (int)Math.floor(current.getX()/32);
        int y = (int)Math.floor(current.getY()/32) + 1;

        //above
        if(map.getPassable(x, y)){
            if(map.getTile(x,y) == tar || ((map.getPassable(x +1, y) && map.getPassable(x -1 , y) && map.getPassable(x, y -1) && map.getPassable(x, y + 1)) &&
                        (map.getPassable(x +1, y + 1 ) && map.getPassable(x -1 , y - 1) && map.getPassable(x + 1, y -1) && map.getPassable(x -1 , y + 1))))
                adj.add(map.getTile(x, y));
            
        }
        //below
        y -= 2;
        if(map.getPassable(x, y)){
            if(map.getTile(x,y) == tar || ((map.getPassable(x +1, y) && map.getPassable(x -1 , y) && map.getPassable(x, y -1) && map.getPassable(x, y + 1)) &&
                        (map.getPassable(x +1, y + 1 ) && map.getPassable(x -1 , y - 1) && map.getPassable(x + 1, y -1) && map.getPassable(x -1 , y + 1))))
                adj.add(map.getTile(x, y));
        }
        //right?
        y += 1;
        x += 1;
        if(map.getPassable(x, y)){
            if(map.getTile(x,y) == tar || ((map.getPassable(x +1, y) && map.getPassable(x -1 , y) && map.getPassable(x, y -1) && map.getPassable(x, y + 1)) &&
                        (map.getPassable(x +1, y + 1 ) && map.getPassable(x -1 , y - 1) && map.getPassable(x + 1, y -1) && map.getPassable(x -1 , y + 1))))
                adj.add(map.getTile(x, y));
            
        }
        //lefT?
        x -= 2;
        if(map.getPassable(x, y)){
            if(map.getTile(x,y) == tar || (map.getPassable(x +1, y) && map.getPassable(x -1 , y) && map.getPassable(x, y -1) && map.getPassable(x, y + 1)) &&
                        (map.getPassable(x +1, y + 1 ) && map.getPassable(x -1 , y - 1) && map.getPassable(x + 1, y -1) && map.getPassable(x -1 , y + 1)))
                adj.add(map.getTile(x, y));
        }
       
        //upper left?
        y += 1;
        if(map.getPassable(x, y)){
            if(map.getTile(x,y) == tar || ((map.getPassable(x +1, y) && map.getPassable(x -1 , y) && map.getPassable(x, y -1) && map.getPassable(x, y + 1)) &&
                        (map.getPassable(x +1, y + 1 ) && map.getPassable(x -1 , y - 1) && map.getPassable(x + 1, y -1) && map.getPassable(x -1 , y + 1))))
                adj.add(map.getTile(x, y));
            
        }
        //upper right
        x+= 2;
        if(map.getPassable(x, y)){
            if(map.getTile(x,y) == tar || ((map.getPassable(x +1, y) && map.getPassable(x -1 , y) && map.getPassable(x, y -1) && map.getPassable(x, y + 1)) &&
                        (map.getPassable(x +1, y + 1 ) && map.getPassable(x -1 , y - 1) && map.getPassable(x + 1, y -1) && map.getPassable(x -1 , y + 1))))
                  adj.add(map.getTile(x, y));
            
        }
        //lower right
        y -= 2;
        if(map.getPassable(x, y)){
            if(map.getTile(x,y) == tar || ((map.getPassable(x +1, y) && map.getPassable(x -1 , y) && map.getPassable(x, y -1) && map.getPassable(x, y + 1)) &&
                        (map.getPassable(x +1, y + 1 ) && map.getPassable(x -1 , y - 1) && map.getPassable(x + 1, y -1) && map.getPassable(x -1 , y + 1))))
                adj.add(map.getTile(x, y));
            
        }
        //lower left
        x-=2;
        if(map.getPassable(x, y)){
            if(map.getTile(x,y) == tar || ((map.getPassable(x +1, y) && map.getPassable(x -1 , y) && map.getPassable(x, y -1) && map.getPassable(x, y + 1)) &&
                        (map.getPassable(x +1, y + 1 ) && map.getPassable(x -1 , y - 1) && map.getPassable(x + 1, y -1) && map.getPassable(x -1 , y + 1))))
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
    
    /**
     * @return the current search space 
     */
    public Map getMap(){
        return map;
    }
}