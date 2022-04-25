package exceptions;

/**
 * Lanca uma excecao que indica que o utilizador ja esta no grupo.
 * 
 * @author grupo 36.
 *
 */
public class UserAlreadyExistsInGroupException extends Exception {

	private static final long serialVersionUID = -8569025410904010385L;

	/**
	 * Constroi uma UserAlreadyExistsInGroupException com mensagem informativa.
	 * 
	 * @param message - mensagem informativa.
	 */
	public UserAlreadyExistsInGroupException(String message) {
		super(message);
	}
}
