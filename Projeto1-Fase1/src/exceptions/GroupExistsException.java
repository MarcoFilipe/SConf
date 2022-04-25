package exceptions;

/**
 * Lanca uma excecao que indica ja existe um grupo com o mesmo id.
 * 
 * @author grupo 36.
 *
 */
public class GroupExistsException extends Exception {

	private static final long serialVersionUID = 3727162626589562835L;

	/**
	 * Constroi uma GroupExistsException com mensagem informativa.
	 * 
	 * @param message - mensagem informativa.
	 */
	public GroupExistsException(String message) {
		super(message);
	}
}
