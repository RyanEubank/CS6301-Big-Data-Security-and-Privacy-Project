package inference.src;

import java.util.*;
import util.src.*;

/**
 * CS6301 Big Data Security and Privacy
 * Author - Ryan Eubank
 * NetID - rme190001
 */

public class Main {

	public static void main(String[] args) {
		List<List<Integer>> possibleSums = Inference.reconstructSum(125, 3, 0, 100);

		Debug.print(Status.INFO, "Number of results: " + Integer.toString(possibleSums.size()));
		if (possibleSums.size() <= 200)
			Debug.print(Status.INFO, possibleSums.toString());

		return;
	}
}