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
