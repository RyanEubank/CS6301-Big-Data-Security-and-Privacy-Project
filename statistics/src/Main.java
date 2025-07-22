package statistics.src;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import util.src.*;

/**
 * CS6301 Big Data Security and Privacy
 * Author - Ryan Eubank
 * NetID - rme190001
 */
public class Main {

    public static void main(String[] args) {
		Stream<String> lines = checkUsage(args);

		Debug.print(
			Status.DEBUG, 
			"Reading records from csv file...",
			"Count: " + args[0],
			"File: " + args[1]
		);

		Optional<List<PatientRecord>> opt = parseCSV(lines);

		Debug.print(Status.DEBUG, "Finished parsing csv, calculating aggregate statistics...");
		
		try {
			calcStatistics(opt.get());
		}
		catch(Exception e) {
			Debug.print(Status.ERROR, e.toString());
		}

		return;
	}
    
	private static void calcStatistics(List<PatientRecord> list) {
		double average = list
			.stream()
			.mapToInt(record -> record.age)
			.average()
			.orElseThrow();

		int sum = list
			.stream()
			.mapToInt(record -> record.age)
			.sum();

		double median = list
			.stream()
			.mapToInt(record -> record.age)
			.sorted()
			.skip((list.size() - 1) / 2)
			.limit(2 - (list.size() % 2))
			.average()
			.getAsDouble();

		Debug.print(
			Status.INFO, 
			"Age statistics:",
			"Number of records = " + list.size(),
			"Average age = " + average,
			"Age sum = " + sum,
			"Median age " + median
		);
	}
    	
    private static Optional<List<PatientRecord>> parseCSV(Stream<String> lines) {
		try {
			List<PatientRecord> records = new ArrayList<>();
			lines.skip(1) // skip header row in csv file
				.map(line -> line.split(","))
				.forEach(values -> records.add(new PatientRecord(values)));
			return Optional.of(records);
		}
		catch (Exception e) {
			Debug.print(Status.ERROR, e.toString());
		}
		return Optional.empty();
	}

    public static Stream<String> checkUsage(String[] args) {
		try {
			return parseArgs(args);
		}
		catch(Exception e) {
			String usage = "Usage: ./run.bat --statistics <limit> <file>";
			Debug.print(Status.ERROR, e.toString(), usage);
			System.exit(-1);
		}
        return null;
	}
    
	private static Stream<String> parseArgs(String[] args) throws IOException {
		if (args.length != 2)
			throw new RuntimeException("Invalid argument count.");
		else 
			return Files.lines(Paths.get(args[1])).limit(Integer.parseInt(args[0]) + 1);
	}
}
