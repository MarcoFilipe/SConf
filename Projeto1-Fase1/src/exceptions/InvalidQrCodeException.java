package exceptions;

/**
 * Lanca uma excecao que indica que o qr code eh invalido.
 * 
 * @author grupo 36.
 *
 */
public class InvalidQrCodeException extends Exception {

	private static final long serialVersionUID = -6427999528120783193L;

	/**
	 * Constroi uma InvalidQrCodeException com mensagem informativa.
	 * 
	 * @param message - mensagem informativa.
	 */
	public InvalidQrCodeException(String message) {
		super(message);
	}
	
}
