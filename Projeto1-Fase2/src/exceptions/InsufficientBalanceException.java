package exceptions;

/**
 * Lanca uma excecao que indica que o utilizador nao tem saldo suficiente na
 * conta.
 * 
 * @author grupo 36.
 *
 */
public class InsufficientBalanceException extends Exception {

	private static final long serialVersionUID = -7739049293300506757L;

	/**
	 * Constroi uma InsufficientBalanceException com mensagem informativa.
	 * 
	 * @param message - mensagem informativa.
	 */
	public InsufficientBalanceException(String message) {
		super(message);
	}

}
