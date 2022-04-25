package exceptions;

/**
 * Lanca uma excecao que indica que o grupo nao existe.
 * 
 * @author grupo 36.
 *
 */
public class GroupNotFoundException extends Exception {

	private static final long serialVersionUID = 1616745967263767467L;

	/**
	 * Constroi uma GroupNotFoundException com mensagem informativa.
	 * 
	 * @param message - mensagem informativa.
	 */
	public GroupNotFoundException(String message) {
		super(message);
	}
}
