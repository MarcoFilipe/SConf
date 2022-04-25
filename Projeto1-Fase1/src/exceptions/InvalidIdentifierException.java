package exceptions;

public class InvalidIdentifierException extends Exception {

	private static final long serialVersionUID = -7076090618636711735L;

	/**
	 * Constroi uma InvalidIdentifierException com mensagem informativa.
	 * 
	 * @param message - mensagem informativa.
	 */
	public InvalidIdentifierException(String message) {
		super(message);
	}

}
