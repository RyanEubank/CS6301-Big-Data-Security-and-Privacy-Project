package inference.src;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Collections2;

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
			.anyMatch(age -> age < min || age > max));
	}

	public static void filterByMedianAge(List<List<PatientRecord>> results, double median) {
		results.removeIf((list) -> list.stream()
			.mapToInt(record -> record.age)
			.sorted()
			.skip((list.size() - 1) / 2)
			.limit(2 - (list.size() % 2))
			.average()
			.getAsDouble() != median);
	}

    public static void reconstructByBloodTypeCounts(
        Map<String, Integer> blood_type_distribution,
        List<List<PatientRecord>> results
    ) {
        List<String> flattenedDistribution = blood_type_distribution
            .entrySet()
            .stream()
            .flatMap((entry) -> Collections.nCopies(entry.getValue(), entry.getKey()).stream())
            .toList();

        if (flattenedDistribution.size() != results.get(0).size())
            throw new RuntimeException("Distribution inconsistent with number of records.");

        List<List<String>> permutations = 
            Collections2.permutations(flattenedDistribution).stream().distinct().toList();

        List<List<PatientRecord>> copy = List.copyOf(results);
        results.clear();

        permutations.stream().forEach((p) -> {
            copy.forEach((possibility) -> {
                for (int i = 0; i < possibility.size(); ++i)
                    possibility.get(i).bloodType = p.get(i);
                results.add(possibility);
            });
        });
    }

    public static void filterByBloodTypeDistributions(
        List<List<PatientRecord>> results,
        Map<String, Map<String, Integer>> distributions
    ) {
        results.removeIf((list) -> list.stream()
            .filter((record) -> record.condition.equals("Arthritis"))
            .map((record) -> record.bloodType)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .equals(distributions.get("Arthritis")) == false);

        results.removeIf((list) -> list.stream()
            .filter((record) -> record.condition.equals("Asthma"))
            .map((record) -> record.bloodType)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .equals(distributions.get("Asthma")) == false);

        results.removeIf((list) -> list.stream()
            .filter((record) -> record.condition.equals("Cancer"))
            .map((record) -> record.bloodType)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .equals(distributions.get("Cancer")) == false);

        results.removeIf((list) -> list.stream()
            .filter((record) -> record.condition.equals("Diabetes"))
            .map((record) -> record.bloodType)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .equals(distributions.get("Diabetes")) == false);

        results.removeIf((list) -> list.stream()
            .filter((record) -> record.condition.equals("Hypertension"))
            .map((record) -> record.bloodType)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .equals(distributions.get("Hypertension")) == false);

        results.removeIf((list) -> list.stream()
            .filter((record) -> record.condition.equals("Obesity"))
            .map((record) -> record.bloodType)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .equals(distributions.get("Obesity")) == false);
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

    @SuppressWarnings("unused")
    private static void enumerateSumTabulate(
		int target, 
		int count, 
		int min, 
		int max,
		List<List<List<List<Integer>>>> table
	) {
        for (int i = 0; i <= target; ++i)
            table.get(i).get(0).add(Collections.emptyList());       // zero ways to sum 0 integers to target value

		for (int i = 0; i <= target; ++i) {
            if (i >= min && i <= max)
                table.get(i).get(1).add(List.of(i));                // exactly 1 way to sum 1 integer to target value in range    
            else
                table.get(i).get(1).add(Collections.emptyList());   // zero ways to sum 1 integer to target value out of range 
        }

        for (int i = 2; i <= count; ++i) {
            for (int j = 0; j <= target; ++j) {
                List<List<Integer>> results = new ArrayList<>();

                for (int k = 0; k <= j; ++k) {
                    final int k_ = k;
                    List<List<Integer>> subresults = table.get(j - k).get(i - 1);
                    results.addAll(subresults.stream().filter((list) -> { 
                        if (list.isEmpty())
                            return false;
                        if (list.get(0) < k_)
                            return false;
                        return true;
                    }).map((list) -> { 
                        List<Integer> mutableList = new ArrayList<>();
                        mutableList.add(k_);
                        mutableList.addAll(list);
                        return mutableList;
                    }).toList());
                }

                table.get(j).get(i).addAll(results);
            }
            
            for (int p = 0; p <= target; ++p)
                table.get(p).get(i-1).clear();
        }
	}
}
