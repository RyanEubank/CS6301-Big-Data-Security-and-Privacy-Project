package util.src;

public class Debug {
    	
    private static String ANSI_RED = "\033[31m";
	private static String ANSI_BLUE = "\033[34m";
	private static String ANSI_CYAN = "\033[36m";
	private static String ANSI_YELLOW = "\033[33m";
	private static String ANSI_GREEN = "\033[32m";
	private static String ANSI_RESET = "\033[0m";

    public static void print(Status status, String... messages) {
		for (String msg : messages) {
			switch (status) {
				case DEBUG:
					System.out.println("[*] -- " + ANSI_CYAN + "[DEBUG]" + ANSI_RESET + " " + msg);
					break;
				case ERROR:
					System.out.println("[-] -- " + ANSI_RED + "[ERROR]" + ANSI_RESET + " " + msg);
					break;
				case WARNING:
					System.out.println("[-] -- " + ANSI_YELLOW + "[WARNING]" + ANSI_RESET + " " + msg);
					break;
				case INFO:
					System.out.println("[+] -- " + ANSI_GREEN + "[INFO]" + ANSI_RESET + " " + msg);
					break;
				default:
					System.out.println("    \\_ " + msg);
			}
			status = Status.NONE; // force remaining strings to print as branches in default case
		}
	}

	public static void requestInput(String msg) {
		System.out.print(ANSI_BLUE + msg + ANSI_RESET);
	}
}
