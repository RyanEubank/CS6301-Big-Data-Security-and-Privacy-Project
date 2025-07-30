package inference.src;

import java.util.*;

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

        private Map<String, Integer> blood_type_distribution;
        private Map<String, Map<String, Integer>> blood_types_by_diagnosis;

		public Statistics() {
			this.num_records = 0;
			this.average_age = -1f;
			this.min_age = -1;
			this.max_age = -1;
			this.median_age = -1f;
            this.blood_type_distribution = new HashMap<>();
            this.blood_types_by_diagnosis = new HashMap<>();
		}

		public int calcAgeSum() {
			return Math.round(this.num_records * this.average_age);
		}
	}

	public static void main(String[] args) {
		checkUsage(args);

		try (Scanner scanner = new Scanner(System.in)) {
			Statistics stats = new Statistics();

			List<List<PatientRecord>> possibileRecords = reconstructByAverageAge(scanner, stats);
			filterByAgeRange(scanner, stats, possibileRecords);
			filterByMedianAge(scanner, stats, possibileRecords);
			filterByGenderStatistics(scanner, stats, possibileRecords);
			filterByDiagnosisStatistics(scanner, stats, possibileRecords);
            reconstructByBloodTypeDistributions(scanner, stats, possibileRecords);
			filterByBloodTypeStatistics(scanner, stats, possibileRecords);
		} catch (Exception e) {
			Debug.print(Status.ERROR, e.toString());
			System.exit(-1);
		}

		return;
	}

    private static List<List<PatientRecord>> reconstructByAverageAge(
		Scanner scanner, 
		Statistics stats
	) {
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
            Inference.reconstructByAgeStats(stats.calcAgeSum(), stats.num_records, 0, 100);

		printPossibleRecords(
			"Number of possible records given the average age and count: ", 
			possibileRecords
		);

		return possibileRecords;
	}

	private static void filterByAgeRange(
		Scanner scanner, 
		Statistics stats, 
		List<List<PatientRecord>> possibileRecords
	) {
		stats.min_age = getIntFromUser("Please enter the minimum age: ", scanner);
		stats.max_age = getIntFromUser("Please enter the maximum age: ", scanner);
		System.out.println();
		Inference.filterByAgeRange(possibileRecords, stats.min_age, stats.max_age);

		printPossibleRecords(
			"Number of possible records given the actual age range: ", 
			possibileRecords
		);
	}

	private static void filterByMedianAge(
		Scanner scanner, 
		Statistics stats, 
		List<List<PatientRecord>> possibileRecords
	) {
		stats.median_age = getFloatFromUser("Please enter the median age: ", scanner);
		System.out.println();
		Inference.filterByMedianAge(possibileRecords, stats.median_age);

		printPossibleRecords(
			"Number of possible records after filtering by median age: ",
			possibileRecords
		);
	}

	private static void filterByGenderStatistics(
		Scanner scanner, 
		Statistics stats,
		List<List<PatientRecord>> possibileRecords
	) {
		//throw new UnsupportedOperationException("Not yet Implemented.");
	}

	private static void filterByDiagnosisStatistics(
		Scanner scanner, 
		Statistics stats,
		List<List<PatientRecord>> possibileRecords
	) {
		//throw new UnsupportedOperationException("Not yet Implemented.");
	}

	private static void reconstructByBloodTypeDistributions(
        Scanner scanner, 
        Statistics stats,
        List<List<PatientRecord>> possibileRecords
    ) {
        Debug.requestInput("Please enter the number of patients with the given blood types:\n");

        stats.blood_type_distribution.put("A+", getIntFromUser("A+: ", scanner));
        stats.blood_type_distribution.put("A-", getIntFromUser("A-: ", scanner));
        stats.blood_type_distribution.put("B+", getIntFromUser("B+: ", scanner));
        stats.blood_type_distribution.put("B-", getIntFromUser("B-: ", scanner));
        stats.blood_type_distribution.put("AB+", getIntFromUser("AB+: ", scanner));
        stats.blood_type_distribution.put("AB-", getIntFromUser("AB-: ", scanner));
        stats.blood_type_distribution.put("O+", getIntFromUser("O+: ", scanner));
        stats.blood_type_distribution.put("O-", getIntFromUser("O-: ", scanner));

        Inference.reconstructByBloodTypeCounts(stats.blood_type_distribution, possibileRecords);

        printPossibleRecords(
			"Number of possible records given the blood type information: ", 
			possibileRecords
		);
    }

	private static void filterByBloodTypeStatistics(
		Scanner scanner, 
		Statistics stats,
		List<List<PatientRecord>> possibileRecords
	) {
        stats.blood_types_by_diagnosis
            .put("Arthritis", getCountsByDiagnosis("Arthritis", scanner));
        stats.blood_types_by_diagnosis
            .put("Asthma", getCountsByDiagnosis("Asthma", scanner));
        stats.blood_types_by_diagnosis
            .put("Cancer", getCountsByDiagnosis("Cancer", scanner));
        stats.blood_types_by_diagnosis
            .put("Diabetes", getCountsByDiagnosis("Diabetes", scanner));
        stats.blood_types_by_diagnosis
            .put("Hypertension", getCountsByDiagnosis("Hypertension", scanner));
        stats.blood_types_by_diagnosis
            .put("Obesity", getCountsByDiagnosis("Obesity", scanner));

        Inference.filterByBloodTypeDistributions(
            possibileRecords, stats.blood_types_by_diagnosis);

        printPossibleRecords(
			"Number of possible records after filtering by blood type/diagnosis distribution: ",
			possibileRecords
		);
	}

    private static Map<String, Integer> getCountsByDiagnosis(
        String diagnosis, 
        Scanner scanner
    ) {
        Map<String, Integer> counts = new HashMap<>();

        Debug.requestInput(
            "Please enter the counts by blood type for users with" + 
            diagnosis + ".\n");

        counts.put("A+", getIntFromUser("A+ : ", scanner));
        counts.put("A-", getIntFromUser("A- : ", scanner));
        counts.put("B+", getIntFromUser("B+ : ", scanner));
        counts.put("B-", getIntFromUser("B- : ", scanner));
        counts.put("AB+", getIntFromUser("AB+ : ", scanner));
        counts.put("AB-", getIntFromUser("AB- : ", scanner));
        counts.put("O+", getIntFromUser("O+ : ", scanner));
        counts.put("O-", getIntFromUser("O- : ", scanner));

        return counts;
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