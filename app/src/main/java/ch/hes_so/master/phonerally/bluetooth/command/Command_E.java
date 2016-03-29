
package ch.hes_so.master.phonerally.bluetooth.command;

public enum Command_E {
    SNAKE_POS, CANDY_POS, GAMEOVER, YOUR_TURN, END_YOUR_TURN, DEBUG, START_GAME, CONNECTED, MOVE_UP, MOVE_LEFT, MOVE_DOWN, MOVE_RIGHT;

    public static boolean contains(String s) {
        for (Command_E cmd : values()) {
            if (cmd.name().equals(s)) {
                return true;
            }
        }
        return false;
    }
}
