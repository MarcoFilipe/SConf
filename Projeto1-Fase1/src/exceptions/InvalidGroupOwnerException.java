package exceptions;

/**
 * Lanca uma excecao que indica que o apenas o dono do grupo pode adicionar um
 * novo membro.
 * 
 * @author grupo 36.
 *
 */
public class InvalidGroupOwnerException extends Exception {

	private static final long serialVersionUID = -1467386100082809688L;

	/**
	 * Constroi uma InvalidGroupOwnerException com mensagem informativa.
	 * 
	 * @param message - mensagem informativa.
	 */
	public InvalidGroupOwnerException(String message) {
		super(message);
	}
}
