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
		public int num_records;
		public float average_age;
		public int min_age;
		public int max_age;

		public Statistics(String[] args) {
			this.num_records = Integer.parseInt(args[0]);
			this.average_age = Float.parseFloat(args[1]);
			this.min_age = Integer.parseInt(args[2]);
			this.max_age = Integer.parseInt(args[3]);
		}

		public int ageSum() {
			return Math.round(num_records * average_age);
		}
	}

	public static void main(String[] args) {
		Statistics stats  = checkUsage(args);

		Debug.print(
			Status.INFO, 
			"Calculating possible ages...",
			"Number of records: " + stats.num_records,
			"Average age: " + stats.average_age,
			"Minimum age: " + stats.min_age,
			"Maximum age: " + stats.max_age,
			"Sum of patient ages: " + stats.ageSum()
		);

		List<List<Integer>> possibleSums = Inference.reconstructSum(
			stats.ageSum(), 
			stats.num_records,
			stats.min_age, 
			stats.max_age
		);
		
		String results = possibleSums.stream().limit(5).collect(Collectors.toList()).toString();

		Debug.print(
			Status.INFO, 
			"Number of results: " + Integer.toString(possibleSums.size()),
			results + " ... And others."
		);

		return;
	}

	public static Statistics checkUsage(String[] args) {
		try {
			return parseArgs(args);
		}
		catch(Exception e) {
			String usage = "Usage: ./run.bat --inference <count> <average> <min> <max>";
			Debug.print(Status.ERROR, e.toString(), usage);
			System.exit(-1);
		}
        return null;
	}
    
	private static Statistics parseArgs(String[] args) {
		if (args.length < 2)
			throw new RuntimeException("Invalid argument count.");
		else {
			Statistics arguments = new Statistics(args);
			return arguments;
		}
	}
}