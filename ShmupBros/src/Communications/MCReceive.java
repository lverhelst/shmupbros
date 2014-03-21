package Communications;

import Game.Entity.Physical;
import Game.Entity.Playable;
import Game.Entity.Projectile;
import Game.State.GameState;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *@Author Daniel Finke, Emery Berg, Leon Verhelst
 * Communications: Listening and Message Parsing
 */
public class MCReceive extends Thread {
    private ArrayList<Physical> entities;
    private Physical somePlayer;
    private long localID, selectedID;
    private MulticastSocket socket;
    private InetAddress ip;
    private String group;    
    private int port;

    /**
     * Constructor
     * @param initialPort Port to listen on
     * @param initialGroup  IP address to extract a inetAddressGroup from
     * @param thisPlayer The current instance of the local PlayerOld
     */
    public MCReceive(int initialPort, String initialGroup, long thisID) {
        port = initialPort;
        group = initialGroup;
        localID = thisID;
    }
    
    /**
     * Basic run operations for threaded execution
     */
    @Override public void run() {
        String token, remoteX, remoteY, rotation, force;
        StringTokenizer tokenizer;
        
        // Create the socket and bind it to port 'port'.
        try {
            ip = InetAddress.getByName(group);
            socket = new MulticastSocket(port);
            socket.joinGroup(ip);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }

        while (true) {
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            try {
                socket.receive(packet);
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            
            tokenizer = new StringTokenizer(new String(packet.getData()), "|");
            token = tokenizer.nextToken();
            selectedID = Long.parseLong(tokenizer.nextToken());
            
            if(selectedID != localID) {                
                if(token.equals("pos")) {
                        remoteX = tokenizer.nextToken();
                        remoteY = tokenizer.nextToken();
                        rotation = tokenizer.nextToken();
                        updateSelectedPlayer(remoteX, remoteY, rotation);
                }
                if(token.equals("atk")) {
                        SelectedPlayerAttack();
                }
                
                if(token.equals("rls")) {
                        releaseSelectedPlayer();
                }
            }
        }
    }
    
    /**
     * Searches the active list for the selected entity
     * @return returns the Entity if found and null if not
     */
    public Physical findSelected() {
        entities =  GameState.getEntities();

        for (int i = 0; i < entities.size(); i++) 
            if (entities.get(i).getID() == selectedID) 
                return entities.get(i);
        
        return null;
    }
    
    /**
     * Update the Positions of connected peers
     * @param remoteX   The X-Coordinate of the player
     * @param remoteY   The Y-Coordinate of the player
     * @param rotation     The rotation of the player
     */
    private void updateSelectedPlayer(String remoteX, String remoteY, String rotation) {
        float x, y, rot;
        
        if(somePlayer != null && !((Playable)somePlayer).isAlive())
            //((Playable)somePlayer).respawn();
            GameState.spawn((Playable)somePlayer);
        
        //Parse the parameters
        try {
            x = Float.parseFloat(remoteX);
            y = Float.parseFloat(remoteY);
            rot = Float.parseFloat(rotation);
        } catch (NumberFormatException nfe) {
            StringTokenizer tokenizer = new StringTokenizer(remoteX, ".");
            x = Float.parseFloat(tokenizer.nextToken());
            tokenizer = new StringTokenizer(remoteY, ".");
            y = Float.parseFloat(tokenizer.nextToken());
            tokenizer = new StringTokenizer(rotation, ".");
            rot = Float.parseFloat(tokenizer.nextToken());            
        }
        
        somePlayer = findSelected();
        
        if(somePlayer != null) { //update the player
            somePlayer.setX(x);
            somePlayer.setY(y);
            somePlayer.setRotation(rot);
        } else {
            somePlayer = new Playable(32);
            somePlayer.setID(selectedID);
            somePlayer.setX(x);
            somePlayer.setY(y);
            somePlayer.setRotation(rot);
            GameState.addEntity(somePlayer);
            ((Playable)somePlayer).respawn();
        }
    }
    
    /**
     * Removing a peer when their game has terminated
     */
    public void releaseSelectedPlayer() {
        somePlayer = findSelected();
        
        if(somePlayer != null)
            GameState.removeEntity(somePlayer);
    }
    
    /**
     * Used to make remote player attack
     */
    public void SelectedPlayerAttack() {
        somePlayer = findSelected();
        
        if(somePlayer != null)
            ((Playable)somePlayer).attack();
    }
}