package ch.hes_so.master.phonerally.command;

public class CommandFactory {
    private static final String TAG = CommandFactory.class.getSimpleName();

    // TODO: 29.03.16 keep or remove this command
    public static Command createConnectedCommand() {
        return new Command(Command_E.CONNECTED);
    }

    public static Command createDebugCommand(String message) {
        return new Command(Command_E.DEBUG, message);
    }
}
