package inference.src;

import java.util.*;
import java.util.stream.Collectors;

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

		public void setNumRecords(int count) {
			this.num_records = count;
		}

		public void setMinAge(int min) {
			this.min_age = min;
		}

		public void setMaxAge(int max) {
			this.max_age = max;
		}

		public void setAverageAge(float avg) {
			this.average_age = avg;
		}

		public void setMedianAge(float median) {
			this.median_age = median;
		}

		public int calcAgeSum() {
			return Math.round(this.num_records * this.average_age);
		}
	}

	public static void main(String[] args) {
		checkUsage(args);

		try (Scanner scanner = new Scanner(System.in)) {
			Statistics stats = getStatsFromUser(scanner);

			Debug.print(
				Status.DEBUG, 
				"Calculating possible lists of ages...",
				"Number of records: " + stats.num_records,
				"Average age: " + stats.average_age,
				"Minimum age: " + stats.min_age,
				"Maximum age: " + stats.max_age,
				"Median age: " + stats.median_age,
				"Calculated sum of ages: " + stats.calcAgeSum()
			);

			List<List<PatientRecord>> possibileRecords = Inference.reconstructByAge(
				stats.calcAgeSum(), 
				stats.num_records,
				stats.min_age, 
				stats.max_age
			);

			printPossibleRecords(
				"Number of possible records given the average age, age range, and count: ", 
				possibileRecords
			);	

			waitForUser(scanner);

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

	private static Statistics getStatsFromUser(Scanner scanner) {
		Statistics stats = new Statistics();

		System.out.print("Please enter the count or number of records: ");
		stats.setNumRecords(scanner.nextInt());

		System.out.print("Please enter the minimum age: ");
		stats.setMinAge(scanner.nextInt());

		System.out.print("Please enter the maximum age: ");
		stats.setMaxAge(scanner.nextInt());

		System.out.print("Please enter the average age: ");
		stats.setAverageAge(scanner.nextFloat());

		System.out.print("Please enter the median age: ");
		stats.setMedianAge(scanner.nextFloat());

		scanner.nextLine();
		System.out.println();
		return stats;
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
		Debug.print(
			Status.INFO, 
			msg + Integer.toString(possibilities.size()),
			"Preview:"
		);

		possibilities.stream().limit(5).forEach(
			(possibleList) -> {
				possibleList.forEach((record) -> System.out.print(record));
				System.out.println();
			}
		);
	}

	private static void waitForUser(Scanner scanner) {
		System.out.print("Press enter to continue...");
		scanner.nextLine();
	}
}