package ch.hes_so.master.phonerally.command;

public class Command {
    private Command_E name;
    private String parameter;

    public final static String PARAMETER_DELIMITER = ";";
    public final static String COMMAND_DELIMITER = "#";
    private static final String EMPTY_PARAMETER = "empty";

    /**
     * Please use CommandFactory to instantiate a Command
     *
     * @param name
     * @param parameter
     */
    protected Command(Command_E name, String parameter) {
        this.setName(name);
        this.setParameter(parameter);
    }

    /**
     * Please use CommandFactory to instantiate a Command
     *
     * @param name
     */
    protected Command(Command_E name) {
        this(name, EMPTY_PARAMETER);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Command [name=");
        builder.append(this.name);
        builder.append(", parameter=");
        builder.append(this.parameter);
        builder.append("]");
        return builder.toString();
    }

    public Command_E getName() {
        return name;
    }

    public String getParameter() {
        return parameter;
    }

    public void setName(Command_E name) {
        this.name = name;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

}
