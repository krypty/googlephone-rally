
package ch.hes_so.master.phonerally.bluetooth.command;

import android.graphics.Point;
import android.util.Log;

import java.util.List;

public class CommandFactory {

	/*------------------------------------------------------------------*\
    |*							Methodes Public							*|
	\*------------------------------------------------------------------*/

    public static Command createCandyPositionCommand(int x, int y) {
        StringBuilder builder = new StringBuilder();
        builder.append(x);
        builder.append(Command.PARAMETER_DELIMITER);
        builder.append(y);
        return new Command(Command_E.CANDY_POS, builder.toString());
    }

    private static final String TAG = CommandFactory.class.getSimpleName();

    public static Command createSnakePositionCommand(List<Point> snakePosition) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < snakePosition.size(); i++) {
            builder.append(snakePosition.get(i).x);
            builder.append(Command.PARAMETER_DELIMITER);
            builder.append(snakePosition.get(i).y);
            if (i != snakePosition.size() - 1) {
                builder.append(Command.PARAMETER_DELIMITER);
            }
        }
        Log.d(TAG, builder.toString());
        return new Command(Command_E.SNAKE_POS, builder.toString());
    }

    public static Command createConnectedCommand() {
        return new Command(Command_E.CONNECTED);
    }

    public static Command createDebugCommand(String message) {
        return new Command(Command_E.DEBUG, message);
    }

    public static Command createStartGameCommand(int offsetX, int totalWidth) {
        StringBuilder builder = new StringBuilder();
        builder.append(offsetX);
        builder.append(Command.PARAMETER_DELIMITER);
        builder.append(totalWidth);
        return new Command(Command_E.START_GAME, String.valueOf(builder.toString()));
    }

    public static Command createGameOverCommand(String message) {
        return new Command(Command_E.GAMEOVER, message);
    }

    public static Command createEndOfTurnCommand() {
        return new Command(Command_E.END_YOUR_TURN);
    }

    public static Command createYourTurnCommand() {
        return new Command(Command_E.YOUR_TURN);
    }

    public static Command createMoveUpCommand() {
        return new Command(Command_E.MOVE_UP);
    }

    public static Command createMoveLeftCommand() {
        return new Command(Command_E.MOVE_LEFT);
    }

    public static Command createMoveDownCommand() {
        return new Command(Command_E.MOVE_DOWN);
    }

    public static Command createMoveRightCommand() {
        return new Command(Command_E.MOVE_RIGHT);
    }

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

}
