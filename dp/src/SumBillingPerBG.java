package dp.src;

import com.google.privacy.differentialprivacy.*;

import util.src.PatientRecord;

import java.nio.file.Path;
import java.time.Year;
import java.util.*;

/**
 * Reads yearly visits from command line, calculates the
 * raw and anonymized amount of money spent by the PATIENTS per year
 * and prints them to {@link #NON_PRIVATE_OUTPUT} and {@link #PRIVATE_OUTPUT} correspondingly.
 * Assumes that a patient may enter the Hospital multiple times across years.
 */
public class SumBillingPerBG {
  private static final String NON_PRIVATE_OUTPUT = "dp/out/non_private_sums_per_BG.csv";
  private static final String PRIVATE_OUTPUT = "dp/out/private_sums_per_BG.csv";

  private static final double LN_3 = Math.log(3);

  /**
   * Number of visits contributed by a single patient per blood group will be limited to 3. All exceeding
   * visits will be discarded. In the dataset though there very minimal duplicate names, so 3 should be good.
   */
  private static final int MAX_CONTRIBUTED_BG = 3;
  /** Minimum amount of money we expect a patient billed in a single visit. */
  private static final int MIN_EUROS_SPENT = -2009;
  /** Maximum amount of money we expect a patient billed in a single visit. */
  private static final int MAX_EUROS_SPENT = 55000;

  private SumBillingPerBG() { }

  /**
   * Reads statistics for all blood types, calculates raw and anonymized sums of money spent by visits
   * per blood type, and writes the results.
   * {@see the Javadoc of {@link SumRevenuePerDay} for more details}.
   */
  public static void run(Path path) {
    VisitsForBG visitsForBG = IOUtils.readBGVisits(path);

    Map<String, Integer> nonPrivateSums = getNonPrivateSums(visitsForBG);
    Map<String, Integer> privateSums = getPrivateSums(visitsForBG);

    IOUtils.writeCountsPerBG(nonPrivateSums, NON_PRIVATE_OUTPUT);
    IOUtils.writeCountsPerBG(privateSums, PRIVATE_OUTPUT);
  }

  /** Returns total raw billing amount for each blood type. */
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

  /** Returns total anonymized billing amount for each blood type. */
  private static Map<String, Integer> getPrivateSums(VisitsForBG visits) {
    Map<String, Integer> privateSumsPerBG = new HashMap<>();

    // Pre-process the data set: limit the number of visits to MAX_CONTRIBUTED_YEARS
    // per name.
    VisitsForBG boundedVisits =
        ContributionBoundingUtils.boundContributedBG(visits, MAX_CONTRIBUTED_BG);

    for (String BG : boundedVisits.getBGWithData()) {
      BoundedSum dpSum =
          BoundedSum.builder()
              .epsilon(LN_3)
              // The data was pre-processed so that each patient may visit the hospital up to
              // MAX_CONTRIBUTED_YEARS
              // Note: while the library accepts this limit as a configurable parameter,
              // it doesn't pre-process the data to ensure this limit is respected.
              // It is responsibility of the caller to ensure the data passed to the library
              // is capped for getting the correct privacy guarantee.
              .maxPartitionsContributed(MAX_CONTRIBUTED_BG)
              // No need to pre-process the data: BoundedSum will clamp the input values.
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