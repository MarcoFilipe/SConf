package server;

public class TrokosServer {

	private static int port = 45678;

	public static void main(String[] args) {
		if (!validateArgs(args)) {
			System.exit(-1);
		}

		setupNetworkServer();
	}

	private static void setupNetworkServer() {
		NetworkServer network = new NetworkServer();

		network.init(port);
	}

	/**
	 * TODO
	 * 
	 * @param args
	 * @return
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
