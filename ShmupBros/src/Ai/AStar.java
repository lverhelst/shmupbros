package Ai;

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
    private Tile tar;
    
    private static Map map;
    
    public AStar(){
        path = new ArrayList();
        if(map == null)
            map = GameState.getMap();
    }
    
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
        
        ArrayList<Tile> adjacents = new ArrayList<Tile>();
        do{
            currentSquare = getLowestScore(currentSquare);
            currentSquare.isClosed = true;
            closedList.add(currentSquare);
            openList.remove(currentSquare);
            
            
            if(closedList.contains(targetTile)){
                ArrayList<Tile> pth = new ArrayList<Tile>();
                Tile cur = targetTile;
                
                double lastslope = Math.PI;
                double slope;
                while(cur != null){
                    //calculate slope 
                    if(cur.parent != null)
                    {
                        slope = (cur.getY() - cur.parent.getY())/(cur.getX() - cur.parent.getX());
                        //System.out.println(slope);
                        if(lastslope != slope){
                            
                            lastslope = slope;
                            pth.add(cur);
                        }else{
                            //slope is the same, do not add
                            pth.add(cur);
                        }
                    }    
                    else{
                        break;
                    }
                    cur = cur.parent;
                }
                //remove target location nodes
                if(pth.size() >= 1)
                  pth.remove(pth.size() -1);
                //if(pth.size() >= 1)
                //    pth.remove(pth.size() -1);
               
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
                }else{
                    // test if using the current G score make the ent F score lower, if yes update the parent because it means its a better path
                    //if(ent.score <= currentSquare.score){
                    //    currentSquare.parent = ent.parent;
                    //}
                }
            }
            
            
        }while(!openList.isEmpty());      
        //no path
        return null;
    }
    
    private Tile getLowestScore(Tile currentSquare){
        Tile lowest = null;
        double lowscore = Double.MAX_VALUE;
        for(Tile t : openList){
            //TODO better score heuristic
            //direct is better (above, below, left, right)
            //angles are not
            int diffx = (int) (currentSquare.getX() - t.getX())/32;
            int diffy = (int) (currentSquare.getY() - t.getY())/32;
            //Manhatten distance 
            double diffdist = Math.abs(currentSquare.getX() - tar.getX()) + Math.abs(tar.getY() - currentSquare.getY());
            double score = (diffx == 0 || diffy == 0) ? 10 : 14;
            //diffdist = 0;
            score += (diffdist/32 + ((t.parent == null) ? 0 : t.parent.score));
            
         //   System.out.println(score);
            
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
    
    private ArrayList<Tile> getAdjacents(Tile current){
        ArrayList<Tile> adj = new ArrayList<>();
        if(current == null)
            return null;
        //above
        int x = (int)Math.floor(current.getX()/32);
        int y = (int)Math.floor(current.getY()/32) + 1;

        
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
    
    public Map getMap(){
        return this.map;
    }
}