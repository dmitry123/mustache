package Game;

import Common.Vertex;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.*;

public class PlayerSession implements Runnable {

    private Player player;

    public PlayerSession(Player player) {
        this.player = player;
    }

    private DataInputStream in;
    private DataOutputStream out;

    public void run() {

        InputStream _in = null;
        OutputStream _out = null;

        try {
            _in = player.getSocket().getInputStream();
            _out = player.getSocket().getOutputStream();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            in = new DataInputStream(_in);
            out = new DataOutputStream(_out);
        }

        String line = null;

        while (in != null) {

            try {
                if ((line = in.readUTF()) == null) {
                    break;
                }
            }
            catch (EOFException e) {
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if (line == null) {
                break;
            }

            line = line.substring(0, line.indexOf('\n'));

            if (line.length() < 3) {
                continue;
            }

            int cmd = Integer.parseInt(line.substring(0, 1));
            int state = Integer.parseInt(line.substring(2, 3));

            if (cmd == 0) {
                player.isLeft = (state == 1);
            } else if (cmd == 1) {
                player.isRight = (state == 1);
            }

            if (cmd == 2) {
                player.doJump();
            } else if (cmd == 3) {
                player.doFight();
            }

            if (player.isBackground) {

                String accString = line.substring(4, line.length());
                String[] strArray = accString.split(",");

                if (strArray.length == 3) {

                    Vertex backgroundOffset = player.getWorld().getBackgroundOffset();

                    backgroundOffset.x = Float.parseFloat(strArray[0]);
                    backgroundOffset.y = Float.parseFloat(strArray[1]);
                    backgroundOffset.z = Float.parseFloat(strArray[2]);
                }
            }
        }

        player.closeSocketSession();

        int countOfPlayers = 0;

        for (GameObject go : player.getWorld ().getList()) {
            if (go instanceof Player) {
                ++countOfPlayers;
            }
        }

        if (countOfPlayers > 1) {
            player.getWorld().detach(player);
        }
    }
}
