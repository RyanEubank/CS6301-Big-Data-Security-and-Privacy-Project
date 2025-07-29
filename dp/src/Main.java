package dp.src;

import util.src.*;

import java.nio.file.*;

import com.google.privacy.differentialprivacy.*
;
public class Main {
  public static void main(String[] args) {
    Path path = checkUsage(args);
    SumBillingPerYear.run(path);
    return;
  }

  public static Path checkUsage(String[] args) {
    try {
      return parseArgs(args);
    } catch (Exception e) {
      String usage = "Usage: ./run.bat --dp <file>";
      Debug.print(Status.ERROR, e.toString(), usage);
      System.exit(-1);
    }
    return null;
  }

  private static Path parseArgs(String[] args) {
		if (args.length != 1)
			throw new RuntimeException("Invalid argument count.");
		else 
			return Paths.get(args[0]).toAbsolutePath();
	}
}
