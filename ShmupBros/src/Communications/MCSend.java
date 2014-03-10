package Communications;

import Game.Entity.Physical;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

/**
 * @author Daniel Finke, Emery Berg, Leon Verhelst
 * Multicasts datagram packets to a specific group
 */
public class MCSend {
    private static DecimalFormat df = new DecimalFormat("00000.0");
    private static MulticastSocket socket;
    private InetAddress ip;
    private String group;
    private int port;
    private long curtime;
    
    /**
     * Constructor
     * @param initialPort the port to send through, usually assigned dynamically
     * @param initialGroup unused
     * @param initialPlayerID The current instance of the local player 
     */
    public MCSend(int initialPort, String initialGroup) {
        group = initialGroup;
        port = initialPort;
        
        try {
            ip = InetAddress.getByName(group);
            socket = new MulticastSocket();
        } catch (UnknownHostException uhe) {
            System.out.println("Host not found: " + uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Could not create local player send socket: " + ioe.getMessage());
        }
    }
    
    /**
     * Multicast the new position of the local player
     * @param x X-Coordinate of the local player
     * @param y Y-Coordinate of the local player
     * @param dir Direction of the local  player
     */
    public void sendPosition(Physical local) {
        String message = "pos|" + local.getID() + "|" + df.format(local.getX()) + "|" +
                df.format(local.getY()) + "|" + df.format(local.getRotation())+ "|";
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), ip, port);

        try {
            socket.send(packet); //limites the speed at which update occur
            curtime = System.currentTimeMillis();
        } catch (IOException ioe) {
            System.out.println("Could not send position packet: " + ioe.getMessage());
        }
    }
    
    /**
     * If the player exits the game, multicast a signal telling others to release them
     */
    public void sendReleasePlayer(long playerID) {
        String message = "rls|" + playerID + "|";
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), ip, port);

        try {
            socket.send(packet);
        } catch (IOException ioe) {
            System.out.println("Could not send release packet: " + ioe.getMessage());
        }
    }
    
    /**
     * If a player's health is reported to have reached 0, then multicast that the player has died.
     */
    public void sendPlayerKilled(long playerID) {
        String message = "kill|" + playerID + "|";
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), ip, port);

        try {
            socket.send(packet);
        } catch (IOException ioe) {
            System.out.println("Could not send release packet: " + ioe.getMessage());
        }
    }
    
    public void sendAttack(long playerID) {
        String message = "atk|" + playerID + "|";
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), ip, port);

        try {
            socket.send(packet);
        } catch (IOException ioe) {
            System.out.println("Could not send release packet: " + ioe.getMessage());
        }
    }
    
    /**
     * Multicast to create this player
     * @param x X-Coordinate of the local player
     * @param y Y-Coordinate of the local player
     * @param dir Direction of the local  player
     */
    public void sendPlayer(Physical local) {
        String message = "crt|" + local.getID() + "|" + df.format(local.getX()) + "|" +
                df.format(local.getY()) + "|" + df.format(local.getRotation())+ "|";
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), ip, port);

        try {
            socket.send(packet); //limites the speed at which update occur
            curtime = System.currentTimeMillis();
        } catch (IOException ioe) {
            System.out.println("Could not send player packet: " + ioe.getMessage());
        }
    }
    
    /**
     * Multicast to create this player
     * @param x X-Coordinate of the local player
     * @param y Y-Coordinate of the local player
     * @param dir Direction of the local  player
     */
    public void requestPlayer(String ID) {
        String message = "rqt|" + ID +  "|";
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), ip, port);

        try {
            socket.send(packet); //limites the speed at which update occur
            curtime = System.currentTimeMillis();
        } catch (IOException ioe) {
            System.out.println("Could not request player packet: " + ioe.getMessage());
        }
    }
}