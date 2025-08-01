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
    private static final String NON_PRIVATE_OUTPUT = "dp/out/non_private_means_per_year.csv";
    private static final String PRIVATE_OUTPUT = "dp/out/private_means_per_year.csv";

    private static final double LN_3 = Math.log(3);

    // This remains the same: we limit how many years a patient can contribute to overall.
    private static final int MAX_CONTRIBUTED_YEARS = 2;

    // The bounds for a single billing amount remain the same.
    private static final int MIN_BILLING_AMOUNT = 0;
    private static final int MAX_BILLING_AMOUNT = 50000;

    private MeanBillingPerYear() { }

    /**
     * Executes the mean calculation process.
     */
    public static void run(Path path) {
        VisitsForYear visitsForYear = IOUtils.readYearlyVisits(path);

        Map<Year, Double> nonPrivateMeans = getNonPrivateMeans(visitsForYear);
        Map<Year, Double> privateMeans = getPrivateMeans(visitsForYear);

        // Use a new IOUtils method to write the results.
        IOUtils.writeMeansPerYear(nonPrivateMeans, NON_PRIVATE_OUTPUT);
        IOUtils.writeMeansPerYear(privateMeans, PRIVATE_OUTPUT);
    }

    /**
     * Calculates the exact (non-private) mean billing amount for each year.
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
            // Avoid division by zero, though it's unlikely.
            if (count > 0) {
                meansPerYear.put(y, sum / count);
            }
        }
        return meansPerYear;
    }

    /**
     * Calculates the differentially private mean billing amount for each year.
     */
    private static Map<Year, Double> getPrivateMeans(VisitsForYear visits) {
        Map<Year, Double> privateMeansPerYear = new HashMap<>();

        // Pre-process the data set to limit contributions, same as before.
        VisitsForYear boundedVisits =
            ContributionBoundingUtils.boundContributedYears(visits, MAX_CONTRIBUTED_YEARS);

        for (Year y : boundedVisits.getYearsWithData()) {
            // Use BoundedMean instead of BoundedSum.
            BoundedMean dpMean =
                BoundedMean.builder()
                    .epsilon(LN_3)
                    // For a given year's calculation, a patient can only be in one
                    // partition (that year), so this is 1.
                    .maxPartitionsContributed(1)
                    // Each patient record is a single contribution to the mean for that year.
                    .maxContributionsPerPartition(1)
                    .lower(MIN_BILLING_AMOUNT)
                    .upper(MAX_BILLING_AMOUNT)
                    .build();

            // Add each billing amount to the BoundedMean instance.
            for (PatientRecord r : boundedVisits.getVisitsForYear(y)) {
                dpMean.addEntry(r.bill);
            }

            // The result is a double.
            privateMeansPerYear.put(y, dpMean.computeResult());
        }

        return privateMeansPerYear;
    }
}
