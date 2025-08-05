package dp.src;

import com.google.privacy.differentialprivacy.BoundedMean;
import util.src.PatientRecord;

import java.nio.file.Path;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

/**
 * Calculates and writes the non-private and differentially private mean
 * billing amount per year.
 */
public class MeanBillingPerYear {
    private static final String NON_PRIVATE_OUTPUT = "dp/out/non_private_means_billing_per_year.csv";
    private static final String PRIVATE_OUTPUT = "dp/out/private_means_billing_per_year.csv";

    // Epsilon value for the differential privacy algorithm. A smaller epsilon
    // results in stronger privacy guarantees but less accurate results.
    private static final double LN_X = Math.log(1.03);

    // The maximum number of different years a patient can contribute to.
    private static final int MAX_PARTITION_CONTRIBUTIONS = 2;

    // The maximum number of records a patient can contribute to for a single year.
    private static final int MAX_CONTRIBUTIONS_PER_PARTITION = 2;

    // The plausible bounds for a single billing amount. These values are used to clamp
    // the input data, a requirement for many differential privacy mechanisms.
    private static final int MIN_BILLING_AMOUNT = 0;
    private static final int MAX_BILLING_AMOUNT = 50000;

    private MeanBillingPerYear() { }

    /**
     * Reads patient data, calculates both non-private and private mean billing amounts
     * per year, and writes the results to separate CSV files.
     *
     * @param path The path to the input CSV file containing patient records.
     */
    public static void run(Path path) {
        VisitsForYear visitsForYear = IOUtils.readYearlyVisits(path);

        Map<Year, Double> nonPrivateMeans = getNonPrivateMeans(visitsForYear);
        Map<Year, Double> privateMeans = getPrivateMeans(visitsForYear);

        // Write the calculated means to their respective output files.
        IOUtils.writeMeansBillingPerYear(nonPrivateMeans, NON_PRIVATE_OUTPUT);
        IOUtils.writeMeansBillingPerYear(privateMeans, PRIVATE_OUTPUT);
    }

    /**
     * Calculates the exact (non-private) mean billing amount for each year.
     *
     * @param visits The collection of patient visits, grouped by year.
     * @return A map where each key is a year and the value is the non-private mean billing amount.
     */
    static Map<Year, Double> getNonPrivateMeans(VisitsForYear visits) {
        Map<Year, Double> meansPerYear = new HashMap<>();
        for (Year y : visits.getYearsWithData()) {
            double sum = 0;
            long count = 0;
            for (PatientRecord r : visits.getVisitsForYear(y)) {
                sum += r.bill;
                count++;
            }
            // Avoid division by zero if a year has no records.
            if (count > 0) {
                meansPerYear.put(y, sum / count);
            }
        }
        return meansPerYear;
    }

    /**
     * Calculates the differentially private mean billing amount for each year.
     *
     * @param visits The original collection of patient visits.
     * @return A map where each key is a year and the value is the private mean billing amount.
     */
    private static Map<Year, Double> getPrivateMeans(VisitsForYear visits) {
        Map<Year, Double> privateMeansPerYear = new HashMap<>();

        // Pre-process the data by applying contribution bounding to limit the influence of any single user.
        VisitsForYear boundedVisits =
                ContributionBoundingUtils.boundContributedYears(visits, MAX_PARTITION_CONTRIBUTIONS);

        for (Year y : boundedVisits.getYearsWithData()) {
            // Initialize the BoundedMean utility with our privacy parameters.
            BoundedMean dpMean =
                    BoundedMean.builder()
                            .epsilon(LN_X)
                            .maxPartitionsContributed(MAX_PARTITION_CONTRIBUTIONS)
                            .maxContributionsPerPartition(MAX_CONTRIBUTIONS_PER_PARTITION)
                            .lower(MIN_BILLING_AMOUNT)
                            .upper(MAX_BILLING_AMOUNT)
                            .build();

            // Add each billing amount to the BoundedMean instance.
            for (PatientRecord r : boundedVisits.getVisitsForYear(y)) {
                dpMean.addEntry(r.bill);
            }

            // Compute the differentially private result and store it.
            privateMeansPerYear.put(y, dpMean.computeResult());
        }

        return privateMeansPerYear;
    }
}
