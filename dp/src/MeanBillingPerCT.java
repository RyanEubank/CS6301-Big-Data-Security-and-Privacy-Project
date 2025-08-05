package dp.src;

import com.google.privacy.differentialprivacy.BoundedMean;
import util.src.PatientRecord;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Calculates and writes the non-private and differentially private mean
 * billing amount per medical condition.
 */
public class MeanBillingPerCT {
    private static final String NON_PRIVATE_OUTPUT = "dp/out/non_private_means_billing_per_CT.csv";
    private static final String PRIVATE_OUTPUT = "dp/out/private_means_billing_per_CT.csv";

    // Epsilon value for the differential privacy algorithm. A smaller epsilon
    // results in stronger privacy guarantees but less accurate results.
    private static final double LN_X = Math.log(1.03);

    // The maximum number of different medical conditions a patient can contribute to.
    private static final int MAX_PARTITION_CONTRIBUTIONS = 2;

    // The maximum number of records a patient can contribute to for a single medical condition.
    private static final int MAX_CONTRIBUTIONS_PER_PARTITION = 2;

    // The plausible bounds for a single billing amount. These values are used to clamp
    // the input data, a requirement for many differential privacy mechanisms.
    private static final int MIN_BILLING_AMOUNT = 0;
    private static final int MAX_BILLING_AMOUNT = 50000;

    private MeanBillingPerCT() { }

    /**
     * Reads patient data, calculates both non-private and private mean billing amounts
     * per medical condition, and writes the results to separate CSV files.
     *
     * @param path The path to the input CSV file containing patient records.
     */
    public static void run(Path path) {
        VisitsForCT visitsForCT = IOUtils.readCTVisits(path);

        Map<String, Double> nonPrivateMeans = getNonPrivateMeans(visitsForCT);
        Map<String, Double> privateMeans = getPrivateMeans(visitsForCT);

        // Write the calculated means to their respective output files.
        IOUtils.writeMeansBillingPerCT(nonPrivateMeans, NON_PRIVATE_OUTPUT);
        IOUtils.writeMeansBillingPerCT(privateMeans, PRIVATE_OUTPUT);
    }

    /**
     * Calculates the exact (non-private) mean billing amount for each medical condition.
     *
     * @param visits The collection of patient visits, grouped by condition.
     * @return A map where each key is a medical condition and the value is the non-private mean billing amount.
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
            // Avoid division by zero if a condition has no records.
            if (count > 0) {
                meansPerCT.put(CT, sum / count);
            }
        }
        return meansPerCT;
    }

    /**
     * Calculates the differentially private mean billing amount for each medical condition.
     *
     * @param visits The original collection of patient visits.
     * @return A map where each key is a medical condition and the value is the private mean billing amount.
     */
    private static Map<String, Double> getPrivateMeans(VisitsForCT visits) {
        Map<String, Double> privateMeansPerCT = new HashMap<>();

        // Pre-process the data by applying contribution bounding.
        VisitsForCT boundedVisits =
                ContributionBoundingUtils.boundContributedCT(visits, MAX_PARTITION_CONTRIBUTIONS);

        for (String CT : boundedVisits.getCTWithData()) {
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
            for (PatientRecord r : boundedVisits.getVisitsForCT(CT)) {
                dpMean.addEntry(r.bill);
            }

            // Compute the differentially private result and store it.
            privateMeansPerCT.put(CT, dpMean.computeResult());
        }

        return privateMeansPerCT;
    }
}
