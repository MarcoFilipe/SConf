package server;

/**
 * Classe que contem a funcao main do servidor.
 * 
 * @author grupo 36.
 *
 */
public class TrokosServer {

	private static int port = 45678;
	private static String cipherPass = null;
	private static String keyStore = null;
	private static String keyStorePass = null;

	public static void main(String[] args) {
		if (!validateArgs(args)) {
			System.exit(-1);
		}

		setupNetworkServer();
	}

	/**
	 * Metodo que cria uma variavel da classe NetworkServer e chama a funcao de
	 * inicializacao com a porta definida.
	 */
	private static void setupNetworkServer() {
		NetworkServer network = new NetworkServer();

		network.init(port, cipherPass, keyStore, keyStorePass);
	}

	/**
	 * Metodo que valida os argumentos recebidos pela funcao main.
	 * 
	 * @param args - argumentos a serem validados.
	 * @return - true caso os argumentos sejam validos, false caso ao contrario.
	 */
	private static boolean validateArgs(String[] args) {
		if (args.length == 3) {
			assignValues(args[0], args[1], args[2]);
			return true;
		}

		if (args.length == 4) {
			try {
				port = Integer.parseInt(args[0]);
				assignValues(args[1], args[2], args[3]);
				return true;
			} catch (NumberFormatException e) {
				System.err.println(e.toString());
			}
		}

		System.err.println(
				"A execução do servidor deve ser feita da seguinte forma: TrokosServer <port> <password-cifra> <keystore> <password-keystore>");
		return false;
	}

	/**
	 * Atribui valores as variaveis globais responsaveis pela configuracao da
	 * conexao do servidor.
	 * 
	 * @param cp  Palavra-passe da cifra
	 * @param ks  A keystore
	 * @param ksp A palavra-passe da keystore
	 */
	private static void assignValues(String cp, String ks, String ksp) {
		cipherPass = cp;
		keyStore = ks;
		keyStorePass = ksp;
	}

}
