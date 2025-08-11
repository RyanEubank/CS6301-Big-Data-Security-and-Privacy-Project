package dp.src;

import com.google.privacy.differentialprivacy.*;

import util.src.PatientRecord;

import java.nio.file.Path;
import java.time.Year;
import java.util.*;

/**
 * Reads patient visit data, calculates the raw and anonymized total billing amount
 * per year, and prints them to {@link #NON_PRIVATE_OUTPUT} and {@link #PRIVATE_OUTPUT} respectively.
 * Assumes that a patient may have records in multiple years.
 */
public class SumBillingPerYear {
  private static final String NON_PRIVATE_OUTPUT = "dp/out/non_private_sums_billing_per_year.csv";
  private static final String PRIVATE_OUTPUT = "dp/out/private_sums_billing_per_year.csv";

  private static final double LN_X = Math.log(1.05);

  /**
   * The maximum number of different years a single patient can contribute to.
   * All contributions to additional years will be discarded.
   */
  private static final int MAX_PARTITION_CONTRIBUTIONS = 2;
  /** Minimum billing amount expected for a single visit. */
  private static final int MIN_EUROS_SPENT = 0;
  /** Maximum billing amount expected for a single visit. */
  private static final int MAX_EUROS_SPENT = 50000;

  private SumBillingPerYear() { }

  /**
   * Reads patient data for all years, calculates raw and anonymized sums of billing amounts
   * per year, and writes the results to CSV files.
   * @param path The path to the input CSV file containing patient records.
   */
  public static void run(Path path) {
    VisitsForYear visitsForYear = IOUtils.readYearlyVisits(path);

    Map<Year, Integer> nonPrivateSums = getNonPrivateSums(visitsForYear);
    Map<Year, Integer> privateSums = getPrivateSums(visitsForYear);

    IOUtils.writeSumsBillingPerYear(nonPrivateSums, NON_PRIVATE_OUTPUT);
    IOUtils.writeSumsBillingPerYear(privateSums, PRIVATE_OUTPUT);
  }

  /** Returns the total raw billing amount for each year. */
  static Map<Year, Integer> getNonPrivateSums(VisitsForYear visits) {
    Map<Year, Integer> sumsPerYear = new HashMap<>();
    for (Year y : visits.getYearsWithData()) {
      int sum = 0;
      for (PatientRecord r : visits.getVisitsForYear(y)) {
        sum += r.bill;
      }
      sumsPerYear.put(y, sum);
    }
    return sumsPerYear;
  }

  /** Returns the total anonymized billing amount for each year. */
  private static Map<Year, Integer> getPrivateSums(VisitsForYear visits) {
    Map<Year, Integer> privateSumsPerYear = new HashMap<>();

    // Pre-process the data set: limit the number of years a patient can contribute to.
    VisitsForYear boundedVisits =
            ContributionBoundingUtils.boundContributedYears(visits, MAX_PARTITION_CONTRIBUTIONS);

    for (Year y : boundedVisits.getYearsWithData()) {
      BoundedSum dpSum =
              BoundedSum.builder()
                      .epsilon(LN_X)
                      // The data was pre-processed so that each patient may contribute to at most
                      // MAX_PARTITION_CONTRIBUTIONS partitions (years).
                      // Note: while the library accepts this limit as a configurable parameter,
                      // it doesn't pre-process the data to ensure this limit is respected.
                      // It is the responsibility of the caller to ensure the data passed to the library
                      // is capped to get the correct privacy guarantee.
                      .maxPartitionsContributed(MAX_PARTITION_CONTRIBUTIONS)
                      // BoundedSum will clamp the input values to these bounds.
                      .lower(MIN_EUROS_SPENT)
                      .upper(MAX_EUROS_SPENT)
                      .build();


      // For each patient, pre-aggregate their spending for the year.
      Map<Integer, Double> patientToYearSpending = new HashMap<>();
      for (PatientRecord r : boundedVisits.getVisitsForYear(y)) {
        int id = r.id;
        if (patientToYearSpending.containsKey(id)) {
          double newAmount = patientToYearSpending.get(id) + r.bill;
          patientToYearSpending.put(id, newAmount);
        } else {
          patientToYearSpending.put(id, (double) r.bill);
        }
      }

      for (PatientRecord r : boundedVisits.getVisitsForYear(y)) {
        dpSum.addEntry(r.bill);
      }

      privateSumsPerYear.put(y, (int) dpSum.computeResult());
    }

    return privateSumsPerYear;
  }
}
