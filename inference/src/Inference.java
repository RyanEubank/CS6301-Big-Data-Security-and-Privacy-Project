package inference.src;

import java.util.*;

import util.src.PatientRecord;

public class Inference {
    
    public static List<List<PatientRecord>> reconstructByAgeStats(
		int target, 
		int count, 
		int min, 
		int max
	) {
        List<List<List<List<Integer>>>> subresults = new ArrayList<>(target);

		for (int i = 0; i <= target; ++i) {
			subresults.add(new ArrayList<>(count));
			for (int j = 0; j <= count; ++j)
				subresults.get(i).add(new ArrayList<>());
		}

		enumerateSum(target, count, min, max, subresults);

		List<List<Integer>> possibileAges = subresults.get(target).get(count);
		List<List<PatientRecord>> results = new ArrayList<>(possibileAges.size());

		possibileAges.forEach((list) -> {
			results.add(list.stream().map((age) -> {
				PatientRecord p = new PatientRecord();
				p.age = age;
				return p;
			})
			.toList());
		});

		return results;
    }

	public static void filterByAgeRange(List<List<PatientRecord>> results, int min, int max) {
		results.removeIf((list) -> list.stream()
			.mapToInt(record -> record.age)
			.anyMatch(age -> age < min || age > max)
		);
	}

	public static void filterByMedianAge(List<List<PatientRecord>> results, double median) {
		results.removeIf((list) -> list.stream()
			.mapToInt(l -> l.age)
			.sorted()
			.skip((list.size() - 1) / 2)
			.limit(2 - (list.size() % 2))
			.average()
			.getAsDouble() != median
		);
	}

	public static void filterByGenderStatistics(List<List<PatientRecord>> results, String gender, int count, float averageAge) {
		results.removeIf(list -> {
			if (count < 0 || count > list.size()) return true;
			if (count == 0) return false; // No constraint if count is 0
			
			float requiredSum = count * averageAge;
			
			return !canSelectAgesWithSum(list, count, requiredSum);
		});
		
		String otherGender = gender.equalsIgnoreCase("Male") ? "Female" : "Male";
		
		for (List<PatientRecord> list : results) {
			assignGendersWithAverageAge(list, gender, otherGender, count, averageAge);
		}
	}
	
	private static boolean canSelectAgesWithSum(List<PatientRecord> list, int count, float targetSum) {
		List<Integer> ages = list.stream().mapToInt(r -> r.age).boxed().sorted().toList();
		
		return canFindExactSum(ages, count, Math.round(targetSum), 0);
	}
	
	private static boolean canFindExactSum(List<Integer> ages, int count, int targetSum, int startIndex) {
		if (count == 0) {
			return targetSum == 0;
		}
		if (startIndex >= ages.size()) {
			return false;
		}
		
		if (canFindExactSum(ages, count - 1, targetSum - ages.get(startIndex), startIndex + 1)) {
			return true;
		}
		
		return canFindExactSum(ages, count, targetSum, startIndex + 1);
	}
	
	private static void assignGendersWithAverageAge(List<PatientRecord> list, String gender, String otherGender, int count, float averageAge) {
		List<PatientRecord> sortedList = new ArrayList<>(list);
		sortedList.sort((a, b) -> Integer.compare(a.age, b.age));
		
		float targetSum = count * averageAge;
		List<Integer> selectedIndices = findBestAgeSelection(sortedList, count, targetSum);
		
		for (int i = 0; i < list.size(); i++) {
			if (selectedIndices.contains(i)) {
				list.get(i).gender = gender;
			} else {
				list.get(i).gender = otherGender;
			}
		}
	}
	
	private static List<Integer> findBestAgeSelection(List<PatientRecord> sortedList, int count, float targetSum) {
		List<List<Integer>> combinations = generateCombinations(sortedList.size(), count);
		
		for (List<Integer> combination : combinations) {
			float sum = 0;
			for (int index : combination) {
				sum += sortedList.get(index).age;
			}
			
			if (Math.abs(sum - targetSum) < 0.01f) {
				return combination;
			}
		}
		
		return new ArrayList<>();
	}
	
	private static List<List<Integer>> generateCombinations(int n, int k) {
		List<List<Integer>> combinations = new ArrayList<>();
		generateCombinationsHelper(combinations, new ArrayList<>(), 0, n, k);
		return combinations;
	}
	
	private static void generateCombinationsHelper(List<List<Integer>> combinations, List<Integer> current, int start, int n, int k) {
		if (current.size() == k) {
			combinations.add(new ArrayList<>(current));
			return;
		}
		
		for (int i = start; i < n; i++) {
			current.add(i);
			generateCombinationsHelper(combinations, current, i + 1, n, k);
			current.remove(current.size() - 1);
		}
	}

	public static void filterByDiagnosisStatistics(List<List<PatientRecord>> results, String diagnosis, int count, float averageAge) {
		results.removeIf(list -> {
			if (count < 0 || count > list.size()) return true;
			if (count == 0) return false; // No constraint if count is 0
			
			float requiredSum = count * averageAge;
			
			return !canSelectAgesWithSum(list, count, requiredSum);
		});
		
		String[] otherDiagnoses = {"Diabetes", "Hypertension", "Asthma", "Obesity", "Heart Disease"};
		
		for (List<PatientRecord> list : results) {
			assignDiagnosisWithAverageAge(list, diagnosis, otherDiagnoses, count, averageAge);
		}
	}
	
	private static void assignDiagnosisWithAverageAge(List<PatientRecord> list, String diagnosis, String[] otherDiagnoses, int count, float averageAge) {
		List<PatientRecord> sortedList = new ArrayList<>(list);
		sortedList.sort((a, b) -> Integer.compare(a.age, b.age));
		
		float targetSum = count * averageAge;
		List<Integer> selectedIndices = findBestAgeSelection(sortedList, count, targetSum);
		
		Random random = new Random(42);
		
		Set<PatientRecord> selectedRecords = new HashSet<>();
		for (int index : selectedIndices) {
			selectedRecords.add(sortedList.get(index));
		}
		
		for (PatientRecord record : list) {
			if (selectedRecords.contains(record)) {
				record.condition = diagnosis;
			} else {
				record.condition = otherDiagnoses[random.nextInt(otherDiagnoses.length)];
			}
		}
	}

	public static void filterByDiagnosisCount(List<List<PatientRecord>> results, String diagnosis, int count) {
		results.removeIf(list -> count < 0 || count > list.size());
		
		String[] otherDiagnoses = {"Diabetes", "Hypertension", "Asthma", "Obesity", "Heart Disease"};
		Random random = new Random(42);
		
		for (List<PatientRecord> list : results) {
			for (int i = 0; i < list.size(); i++) {
				if (i < count) {
					list.get(i).condition = diagnosis;
				} else {
					list.get(i).condition = otherDiagnoses[random.nextInt(otherDiagnoses.length)];
				}
			}
		}
	}

	public static void filterByBloodTypeStatistics(List<List<PatientRecord>> results, String bloodType, int count, float averageAge) {
		results.removeIf(list -> {
			if (count < 0 || count > list.size()) return true;
			if (count == 0) return false; // No constraint if count is 0
			
			float requiredSum = count * averageAge;
			
			return !canSelectAgesWithSum(list, count, requiredSum);
		});
		
		String[] otherBloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
		
		List<String> availableTypes = new ArrayList<>();
		for (String type : otherBloodTypes) {
			if (!type.equals(bloodType)) {
				availableTypes.add(type);
			}
		}
		
		for (List<PatientRecord> list : results) {
			assignBloodTypeWithAverageAge(list, bloodType, availableTypes, count, averageAge);
		}
	}
	
	private static void assignBloodTypeWithAverageAge(List<PatientRecord> list, String bloodType, List<String> otherBloodTypes, int count, float averageAge) {
		List<PatientRecord> sortedList = new ArrayList<>(list);
		sortedList.sort((a, b) -> Integer.compare(a.age, b.age));
		
		float targetSum = count * averageAge;
		List<Integer> selectedIndices = findBestAgeSelection(sortedList, count, targetSum);
		
		Random random = new Random(42);
		
		Set<PatientRecord> selectedRecords = new HashSet<>();
		for (int index : selectedIndices) {
			selectedRecords.add(sortedList.get(index));
		}
		
		for (PatientRecord record : list) {
			if (selectedRecords.contains(record)) {
				record.bloodType = bloodType;
			} else {
				record.bloodType = otherBloodTypes.get(random.nextInt(otherBloodTypes.size()));
			}
		}
	}

	public static void filterByBloodTypeCount(List<List<PatientRecord>> results, String bloodType, int count) {
		results.removeIf(list -> count < 0 || count > list.size());
		
		String[] otherBloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
		Random random = new Random(42);
		
		List<String> availableTypes = new ArrayList<>();
		for (String type : otherBloodTypes) {
			if (!type.equals(bloodType)) {
				availableTypes.add(type);
			}
		}
		
		for (List<PatientRecord> list : results) {
			for (int i = 0; i < list.size(); i++) {
				if (i < count) {
					list.get(i).bloodType = bloodType;
				} else {
					list.get(i).bloodType = availableTypes.get(random.nextInt(availableTypes.size()));
				}
			}
		}
	}



    private static void enumerateSum(
		int target, 
		int count, 
		int min, 
		int max,
		List<List<List<List<Integer>>>> table
	) {
		if (table.get(target).get(count).isEmpty()) {
			if (count == 0)
				table.get(target).get(count).add(Collections.emptyList());
			else if (count == 1) {
				if  (target >= min && target <= max) 
					table.get(target).get(count).add(List.of(target));
				else
					table.get(target).get(count).add(Collections.emptyList()); // no solution exists
			}
			else {
				int ceiling = Math.min(max, target); // skip elements > target
				for (int i = min; i <= ceiling; ++i) {
					final int current = i;
					enumerateSum(target - i, count - 1, min, max, table);
					table.get(target - i).get(count - 1).forEach((numbers) -> {
						if (!numbers.isEmpty() && current <= numbers.get(0)) {
							List<Integer> result = new ArrayList<>(numbers.size() + 1);
							result.add(current);
							result.addAll(numbers);
							table.get(target).get(count).add(result);
						}
					});
				}
			}
		}
	}
}
