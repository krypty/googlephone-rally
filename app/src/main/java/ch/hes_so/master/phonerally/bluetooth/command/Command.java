
package ch.hes_so.master.phonerally.bluetooth.command;

public class Command {

	/*------------------------------------------------------------------*\
    |*							Attributs Private						*|
	\*------------------------------------------------------------------*/

    private Command_E name;
    private String parameter;

    public final static String PARAMETER_DELIMITER = ";";
    public final static String COMMAND_DELIMITER = "#";
    private static final String EMPTY_PARAMETER = "empty";

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

    /**
     * Commande qui circule à travers le réseau.
     * <br><b>NE PAS UTILISER DIRECTEMENT !! DEMANDER A CommandFactory de le faire pour nous</b>
     *
     * @param name      nom de la commande à effectuer
     * @param parameter paramètre de la commande. Si plusieurs paramètres ils sont concaténés avec un délimiteur
     *                  Exemple de sortie: SNAKE_POS#5;-4
     */
    public Command(Command_E name, String parameter) {
        this.setName(name);
        this.setParameter(parameter);
    }

    public Command(Command_E name) {
        this(name, EMPTY_PARAMETER);
    }

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

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

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

    public Command_E getName() {
        return name;
    }

    public String getParameter() {
        return parameter;
    }

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

    public void setName(Command_E name) {
        this.name = name;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

}
