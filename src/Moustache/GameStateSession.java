package Moustache;

import Game.Player;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GameStateSession implements Runnable {

    GameStateSession(GameState gameState) {
        this.gameState = gameState;
    }

    GameState gameState;
    ServerSocket socket;

    public void run() {

        Player player;
        Socket client;

        try {
            socket = new ServerSocket(28181);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {

            player = null;

            while (socket != null) {

                try {
                    if ((client = socket.accept()) != null) {

                        DataOutputStream out = new DataOutputStream(client.getOutputStream());

                        if (gameState.getWorld() == null || gameState.getWorld() != null && (player = (Player) gameState.getWorld().find("player")) == null) {

                            // write error message
                            out.writeUTF("$");
                            out.flush();

                            // close socket
                            client.close();

                            // skip player creation
                            continue;
                        }

                        // write error message
                        out.writeUTF("~");
                        out.flush();

                        // result of all our actions
                        Player result = player;

                        // if we have player in the world or we have player
                        // and player with socket, then we can create
                        // new player and attach to the world, else we have
                        // to start session with basic world's player
                        if (player != null && player.getSocket() != null) {
                            result = new Player(gameState.getWorld(), player.getPosition().x, player.getPosition().y, client);
                        } else if (player != null) {
                            player.startSocketSession(client);
                        }

                        gameState.getPlayerList().add(result);
                    }
                } catch (Exception e) {
                    break;
                }
            }
        }
    }
}