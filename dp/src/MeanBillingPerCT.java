package dp.src;

import com.google.privacy.differentialprivacy.BoundedMean;
import util.src.PatientRecord;

import java.nio.file.Path;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

/**
 * Calculates and writes the non-private and differentially private mean
 * billing amount per condition type.
 */
public class MeanBillingPerCT {
    private static final String NON_PRIVATE_OUTPUT = "dp/out/non_private_means_per_condition.csv";
    private static final String PRIVATE_OUTPUT = "dp/out/private_condition_means_per_condition.csv";

    private static final double LN_X = Math.log(1.1);

    // we limit how many different conditions a patient name can contribute to overall.
    private static final int MAX_CONTRIBUTIONS = 2;

    // The bounds for a single billing.
    private static final int MIN_BILLING_AMOUNT = 0;
    private static final int MAX_BILLING_AMOUNT = 50000;

    private MeanBillingPerCT() { }

    /**
     * Executes the mean calculation process.
     */
    public static void run(Path path) {
        VisitsForCT visitsForCT = IOUtils.readCTVisits(path);

        Map<String, Double> nonPrivateMeans = getNonPrivateMeans(visitsForCT);
        Map<String, Double> privateMeans = getPrivateMeans(visitsForCT);

        // Use a new IOUtils method to write the results.
        IOUtils.writeMeansPerCT(nonPrivateMeans, NON_PRIVATE_OUTPUT);
        IOUtils.writeMeansPerCT(privateMeans, PRIVATE_OUTPUT);
    }

    /**
     * Calculates the exact (non-private) mean billing amount for each condition type.
     */
    static Map<String, Double> getNonPrivateMeans(VisitsForCT visits) {
        Map<String, Double> meansPerCT = new HashMap<>();
        for (String CT : visits.getCTWithData()) {
            double sum = 0;
            long count = 0;
            for (PatientRecord r : visits.getVisitsForCT(CT)) {
                sum += r.bill;
                count++;
            }
            // Avoid division by zero, though it's unlikely.
            if (count > 0) {
                meansPerCT.put(CT, sum / count);
            }
        }
        return meansPerCT;
    }

    /**
     * Calculates the differentially private mean billing amount for each condition type.
     */
    private static Map<String, Double> getPrivateMeans(VisitsForCT visits) {
        Map<String, Double> privateMeansPerCT = new HashMap<>();

        // Pre-process the data set to limit contributions.
        VisitsForCT boundedVisits =
            ContributionBoundingUtils.boundContributedCT(visits, MAX_CONTRIBUTIONS);

        for (String CT : boundedVisits.getCTWithData()) {
            // Use BoundedMean.
            BoundedMean dpMean =
                BoundedMean.builder()
                    .epsilon(LN_X)
                    // For a given condition type, a patient can only be in one
                    // partition (that condition type), so this is 1.
                    .maxPartitionsContributed(2)
                    // Each patient record is a single contribution to the mean for that condition.
                    .maxContributionsPerPartition(2)
                    .lower(MIN_BILLING_AMOUNT)
                    .upper(MAX_BILLING_AMOUNT)
                    .build();

            // Add each billing amount to the BoundedMean instance.
            for (PatientRecord r : boundedVisits.getVisitsForCT(CT)) {
                dpMean.addEntry(r.bill);
            }

            // The result is a double.
            privateMeansPerCT.put(CT, dpMean.computeResult());
        }

        return privateMeansPerCT;
    }
}
