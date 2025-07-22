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
		Statistics stats = getStatsFromUser();

		Debug.print(
			Status.INFO, 
			"Calculating possible ages...",
			"Number of records: " + stats.num_records,
			"Average age: " + stats.average_age,
			"Minimum age: " + stats.min_age,
			"Maximum age: " + stats.max_age,
			"Sum of patient ages: " + stats.calcAgeSum()
		);

		List<List<Integer>> possibleSums = Inference.reconstructSum(
			stats.calcAgeSum(), 
			stats.num_records,
			stats.min_age, 
			stats.max_age
		);
		
		String results = possibleSums
			.stream()
			.limit(5)
			.collect(Collectors.toList())
			.toString();

		Debug.print(
			Status.INFO, 
			"Number of possible sums: " + Integer.toString(possibleSums.size()),
			"Preview: " + results + "..."
		);

		return;
	}

	private static Statistics getStatsFromUser() {
		Statistics stats = new Statistics();

		try (Scanner scanner = new Scanner(System.in)) {
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
		} catch (Exception e) {
			Debug.print(Status.ERROR, e.toString());
			System.exit(-1);
		}

		return stats;
	}

	public static void checkUsage(String[] args) {
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
}