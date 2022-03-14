package exceptions;

/**
 * Lanca uma excecao ao realizar uma operacao invalida, que nao eh tratada por
 * outras excecoes.
 * 
 * @author grupo 36.
 *
 */
public class InvalidOperation extends Exception {

	private static final long serialVersionUID = 5965861953485273267L;

	/**
	 * Constroi uma NegativeAmountException sem mensagem informativa.
	 */
	public InvalidOperation() {
		/* do nothing */}
}
