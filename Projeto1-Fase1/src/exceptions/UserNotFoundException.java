package exceptions;

/**
 * Lanca uma excecao que indica que o utilizador nao existe.
 * 
 * @author grupo 36.
 *
 */
public class UserNotFoundException extends Exception {

	private static final long serialVersionUID = 1398946747507327275L;

	/**
	 * Constroi uma UserDoesNotExistException sem mensagem informativa.
	 */
	public UserNotFoundException() {/* do nothing */}

	/**
	 * Constroi uma UserDoesNotExistException com mensagem informativa.
	 * 
	 * @param message - mensagem informativa.
	 */
	public UserNotFoundException(String message) {
		super(message);
	}

}
