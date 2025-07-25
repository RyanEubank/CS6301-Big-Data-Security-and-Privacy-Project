package inference.src;

import java.util.*;
import java.util.stream.*;

import util.src.*;

/**
 * CS6301 Big Data Security and Privacy
 * Author - Ryan Eubank
 * NetID - rme190001
 */

public class Main {

	private static class Statistics {
		private int num_records;
		private float average_age;
		private int min_age;
		private int max_age;
		private float median_age;

		public Statistics() {
			this.num_records = 0;
			this.average_age = -1f;
			this.min_age = -1;
			this.max_age = -1;
			this.median_age = -1f;
		}

		public int calcAgeSum() {
			return Math.round(this.num_records * this.average_age);
		}
	}

	public static void main(String[] args) {
		checkUsage(args);

		try (Scanner scanner = new Scanner(System.in)) {
			Statistics stats = new Statistics();

			stats.num_records = getIntFromUser("Please enter the number of records: ", scanner);
			stats.average_age = getFloatFromUser("Please enter the average age: ", scanner);
			System.out.println();

			Debug.print(
				Status.DEBUG, 
				"Calculating possible lists of ages...",
				"Number of records: " + stats.num_records,
				"Average age: " + stats.average_age,
				"Assuming minimum age: " + 0,
				"Assuming maximum age: " + 100,
				"Calculated sum of ages: " + stats.calcAgeSum() + "\n"
			);

			List<List<PatientRecord>> possibileRecords = 
				Inference.reconstructByAge(stats.calcAgeSum(), stats.num_records, 0, 100);

			printPossibleRecords(
				"Number of possible records given the average age and count: ", 
				possibileRecords
			);

			stats.min_age = getIntFromUser("Please enter the minimum age: ", scanner);
			stats.max_age = getIntFromUser("Please enter the maximum age: ", scanner);
			System.out.println();
			Inference.filterByAgeRange(possibileRecords, stats.min_age, stats.max_age);

			printPossibleRecords(
				"Number of possible records given the actual age range: ", 
				possibileRecords
			);

			stats.median_age = getFloatFromUser("Please enter the median age: ", scanner);
			System.out.println();
			Inference.filterByMedianAge(possibileRecords, stats.median_age);

			printPossibleRecords(
				"Number of possible records after filtering my median age: ",
				possibileRecords
			);

		} catch (Exception e) {
			Debug.print(Status.ERROR, e.toString());
			System.exit(-1);
		}

		return;
	}

	private static int getIntFromUser(String msg, Scanner scanner) {
		Debug.requestInput(msg);
		return scanner.nextInt();
	}

	private static float getFloatFromUser(String msg, Scanner scanner) {
		Debug.requestInput(msg);
		return scanner.nextFloat();
	}

	private static void checkUsage(String[] args) {
		try {
			if (args.length > 1)
				throw new RuntimeException("Additional arguments given.");
		}
		catch(Exception e) {
			String usage = "Usage: ./run.bat --inference";
			Debug.print(Status.ERROR, e.toString(), usage);
			System.exit(-1);
		}
	}

	private static void printPossibleRecords(
		String msg, 
		List<List<PatientRecord>> possibilities
	) {
		List<String> preview = new ArrayList<>();
		preview.add(msg + Integer.toString(possibilities.size()));

		possibilities.stream().limit(3).forEach((possibleList) -> {
			possibleList.forEach((record) -> preview.add(record.toString()));
			preview.add("-- OR --");
		});

		preview.add("... And more ...\n");
		String[] formattedMsg = preview.toArray(new String[preview.size()]);
		Debug.print(Status.INFO, formattedMsg);
	}
}