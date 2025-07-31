package statistics.src;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

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
			calcAgeStatistics(opt.get());
			calcGenderStatistics(opt.get());
			calcDiagnosisStatistics(opt.get());
			calcBloodtypeStatistics(opt.get());
		}
		catch(Exception e) {
			Debug.print(Status.ERROR, e.toString());
		}

		return;
	}
    
	private static void calcAgeStatistics(List<PatientRecord> list) {
		int min = list
			.stream()
			.mapToInt(record -> record.age)
			.min()
			.getAsInt();

		int max = list
			.stream()
			.mapToInt(record -> record.age)
			.max()
			.getAsInt();

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
			"Age range = (" + min + " - " + max + ")",
			"Average age = " + average,
			"Age sum = " + sum,
			"Median age " + median
		);
	}
    
	private static void calcGenderStatistics(List<PatientRecord> list) {
        long count = list
            .stream()
            .filter((record) -> record.gender.equals("Male"))
            .count();

		double averageMale = list
			.stream()
            .filter((record) -> record.gender.equals("Male"))
			.mapToInt(record -> record.age)
			.average()
			.orElseThrow();

		double averageFemale = list
			.stream()
            .filter((record) -> !record.gender.equals("Male"))
			.mapToInt(record -> record.age)
			.average()
			.orElseThrow();

		Debug.print(
			Status.INFO, 
			"Gender statistics:",
			"Number of males = " + count,
			"Number of females = " + (list.size() - count),
            "Average age of males = " + averageMale,
            "Average age of females = " + averageFemale
		);
	}

	private static void calcDiagnosisStatistics(List<PatientRecord> list) {  
        Map<String, Long> cumulativeDistribution = list
			.stream()
			.map((record) -> record.condition)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<String, Double> averageAgeByDiagnosis = list
            .stream()
            .collect(Collectors.groupingBy(
                (record) -> record.condition, 
                Collectors.averagingDouble((record) -> record.age)
            ));

		Debug.print(
			Status.INFO, 
			"Diagnosis statistics:",
            "Cummulative Distribution = " + cumulativeDistribution,
            "Average age by diagnosis = " + averageAgeByDiagnosis
		);		
	}

	private static void calcBloodtypeStatistics(List<PatientRecord> list) {
		Map<String, Long> cumulativeDistribution = list
			.stream()
			.map((record) -> record.bloodType)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		Map<String, Long> arthritisDistribution = list
			.stream()
			.filter((record) -> record.condition.equals("Arthritis"))
			.map((record) -> record.bloodType)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		Map<String, Long> asthmaDistribution = list
			.stream()
			.filter((record) -> record.condition.equals("Asthma"))
			.map((record) -> record.bloodType)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
		Map<String, Long> cancerDistribution = list
			.stream()
			.filter((record) -> record.condition.equals("Cancer"))
			.map((record) -> record.bloodType)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		Map<String, Long> diabetesDistribution = list
			.stream()
			.filter((record) -> record.condition.equals("Diabetes"))
			.map((record) -> record.bloodType)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<String, Long> hypertensionDistribution = list
			.stream()
			.filter((record) -> record.condition.equals("Hypertension"))
			.map((record) -> record.bloodType)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		Map<String, Long> obesityDistribution = list
			.stream()
			.filter((record) -> record.condition.equals("Obesity"))
			.map((record) -> record.bloodType)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<String, Double> averageAgeByBloodtype = list
            .stream()
            .collect(Collectors.groupingBy(
                (record) -> record.bloodType, 
                Collectors.averagingDouble((record) -> record.age)
            ));

		Debug.print(
			Status.INFO, 
			"Blood type statistics:",
			"Cummulative Distribution = " + cumulativeDistribution,
			"-- Distribution by diagnosis --",
			"Arthritis = " + arthritisDistribution,
			"Asthma = " + asthmaDistribution,
			"Cancer = " + cancerDistribution,
			"Diabetes = " + diabetesDistribution,
			"Hypertension = " + hypertensionDistribution,
			"Obesity = " + obesityDistribution,
            "Average age by blood type  = " + averageAgeByBloodtype
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
