package dp.src;

import com.google.privacy.differentialprivacy.*;

import util.src.PatientRecord;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads patient visit data, calculates the raw and anonymized total billing amount
 * per blood group, and prints them to {@link #NON_PRIVATE_OUTPUT} and {@link #PRIVATE_OUTPUT} respectively.
 * Assumes that a patient may have records associated with multiple blood groups (unlikely, but handled).
 */
public class SumBillingPerBG {
  private static final String NON_PRIVATE_OUTPUT = "dp/out/non_private_sums_billing_per_BG.csv";
  private static final String PRIVATE_OUTPUT = "dp/out/private_sums_billing_per_BG.csv";

  private static final double LN_X = Math.log(1.05);

  /**
   * The maximum number of different blood groups a single patient can contribute to.
   * All contributions to additional blood groups will be discarded.
   */
  private static final int MAX_PARTITION_CONTRIBUTIONS = 2;
  /** Minimum billing amount expected for a single visit. */
  private static final int MIN_EUROS_SPENT = 0;
  /** Maximum billing amount expected for a single visit. */
  private static final int MAX_EUROS_SPENT = 50000;

  private SumBillingPerBG() { }

  /**
   * Reads patient data for all blood groups, calculates raw and anonymized sums of billing amounts
   * per blood group, and writes the results to CSV files.
   * @param path The path to the input CSV file containing patient records.
   */
  public static void run(Path path) {
    VisitsForBG visitsForBG = IOUtils.readBGVisits(path);

    Map<String, Integer> nonPrivateSums = getNonPrivateSums(visitsForBG);
    Map<String, Integer> privateSums = getPrivateSums(visitsForBG);

    IOUtils.writeSumsBillingPerBG(nonPrivateSums, NON_PRIVATE_OUTPUT);
    IOUtils.writeSumsBillingPerBG(privateSums, PRIVATE_OUTPUT);
  }

  /** Returns the total raw billing amount for each blood group. */
  static Map<String, Integer> getNonPrivateSums(VisitsForBG visits) {
    Map<String, Integer> sumsPerBG = new HashMap<>();
    for (String BG : visits.getBGWithData()) {
      int sum = 0;
      for (PatientRecord r : visits.getVisitsForBG(BG)) {
        sum += r.bill;
      }
      sumsPerBG.put(BG, sum);
    }
    return sumsPerBG;
  }

  /** Returns the total anonymized billing amount for each blood group. */
  private static Map<String, Integer> getPrivateSums(VisitsForBG visits) {
    Map<String, Integer> privateSumsPerBG = new HashMap<>();

    // Pre-process the data set: limit the number of blood groups a patient can contribute to.
    VisitsForBG boundedVisits =
            ContributionBoundingUtils.boundContributedBG(visits, MAX_PARTITION_CONTRIBUTIONS);

    for (String BG : boundedVisits.getBGWithData()) {
      BoundedSum dpSum =
              BoundedSum.builder()
                      .epsilon(LN_X)
                      // The data was pre-processed so that each patient may contribute to at most
                      // MAX_PARTITION_CONTRIBUTIONS partitions (blood groups).
                      // Note: while the library accepts this limit as a configurable parameter,
                      // it doesn't pre-process the data to ensure this limit is respected.
                      // It is the responsibility of the caller to ensure the data passed to the library
                      // is capped to get the correct privacy guarantee.
                      .maxPartitionsContributed(MAX_PARTITION_CONTRIBUTIONS)
                      // BoundedSum will clamp the input values to these bounds.
                      .lower(MIN_EUROS_SPENT)
                      .upper(MAX_EUROS_SPENT)
                      .build();

      for (PatientRecord r : boundedVisits.getVisitsForBG(BG)) {
        dpSum.addEntry(r.bill);
      }

      privateSumsPerBG.put(BG, (int) dpSum.computeResult());
    }

    return privateSumsPerBG;
  }
}
