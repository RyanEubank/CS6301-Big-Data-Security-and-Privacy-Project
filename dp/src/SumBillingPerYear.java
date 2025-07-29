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
public class SumBillingPerYear {
  private static final String NON_PRIVATE_OUTPUT = "dp/out/non_private_sums_per_year.csv";
  private static final String PRIVATE_OUTPUT = "dp/out/private_sums_per_year.csv";

  private static final double LN_3 = Math.log(3);

  /**
   * Number of visits contributed by a single patient will be limited to 2. All exceeding
   * visits will be discarded. In the dataset though there very minimal duplicate names, so 2 shouuld be good.
   */
  private static final int MAX_CONTRIBUTED_YEARS = 2;
  /** Minimum amount of money we expect a patient billed in a single visit. */
  private static final int MIN_EUROS_SPENT = -2009;
  /** Maximum amount of money we expect a patient billed in a single visit. */
  private static final int MAX_EUROS_SPENT = 50000;

  private SumBillingPerYear() { }

  /**
   * Reads statistics for all years, calculates raw and anonymized sums of money spent by visits
   * per year, and writes the results.
   * {@see the Javadoc of {@link SumRevenuePerDay} for more details}.
   */
  public static void run(Path path) {
    VisitsForYear visitsForYear = IOUtils.readYearlyVisits(path);

    Map<Year, Integer> nonPrivateSums = getNonPrivateSums(visitsForYear);
    Map<Year, Integer> privateSums = getPrivateSums(visitsForYear);

    IOUtils.writeCountsPerYear(nonPrivateSums, NON_PRIVATE_OUTPUT);
    IOUtils.writeCountsPerYear(privateSums, PRIVATE_OUTPUT);
  }

  /** Returns total raw billing amount for each year. */
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

  /** Returns total anonymized billing amount for each year. */
  private static Map<Year, Integer> getPrivateSums(VisitsForYear visits) {
    Map<Year, Integer> privateSumsPerYear = new HashMap<>();

    // Pre-process the data set: limit the number of visits to MAX_CONTRIBUTED_YEARS
    // per name.
    VisitsForYear boundedVisits =
        ContributionBoundingUtils.boundContributedYears(visits, MAX_CONTRIBUTED_YEARS);

    for (Year y : boundedVisits.getYearsWithData()) {
      BoundedSum dpSum =
          BoundedSum.builder()
              .epsilon(LN_3)
              // The data was pre-processed so that each patient may visit the hospital up to
              // MAX_CONTRIBUTED_YEARS
              // Note: while the library accepts this limit as a configurable parameter,
              // it doesn't pre-process the data to ensure this limit is respected.
              // It is responsibility of the caller to ensure the data passed to the library
              // is capped for getting the correct privacy guarantee.
              .maxPartitionsContributed(MAX_CONTRIBUTED_YEARS)
              // No need to pre-process the data: BoundedSum will clamp the input values.
              .lower(MIN_EUROS_SPENT)
              .upper(MAX_EUROS_SPENT)
              .build();

      for (PatientRecord r : boundedVisits.getVisitsForYear(y)) {
        dpSum.addEntry(r.bill);
      }

      privateSumsPerYear.put(y, (int) dpSum.computeResult());
    }

    return privateSumsPerYear;
  }
}