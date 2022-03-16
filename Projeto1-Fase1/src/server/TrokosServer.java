package server;

/**
 * Classe que contem a funcao main do servidor.
 * 
 * @author grupo 36.
 *
 */
public class TrokosServer {

	private static int port = 45678;

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

		network.init(port);
	}

	/**
	 * Metodo que valida os argumentos recebidos pela funcao main.
	 * 
	 * @param args - argumentos a serem validados.
	 * @return - true caso os argumentos sejam validos, false caso ao contrario.
	 */
	private static boolean validateArgs(String[] args) {
		if (args.length == 0) {
			return true;
		}

		if (args.length == 1) {
			try {
				TrokosServer.port = Integer.parseInt(args[0]);
				return true;
			} catch (NumberFormatException e) {
				System.err.println(e.toString());
			}
		}

		System.err.println("A execução do servidor deve ser feita da seguinte forma: TrokosServer <port>");
		return false;
	}
}
