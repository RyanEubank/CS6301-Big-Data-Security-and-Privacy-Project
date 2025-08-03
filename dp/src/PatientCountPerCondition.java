package dp.src;
import com.google.privacy.differentialprivacy.*;

import util.src.PatientRecord;

import java.nio.file.Path;
import java.time.Year;
import java.util.*;

public class PatientCountPerCondition {
    private static final String NON_PRIVATE_OUTPUT = "dp/out/non_private_counts_per_conditionType.csv";
    private static final String PRIVATE_OUTPUT = "dp/out/private_counts_per_conditionType.csv";
    private static final double LN_3 = Math.log(1.1);
    private static final int MAX_CONTRIBUTED_COND_TYPE = 2;

    private PatientCountPerCondition() { }

    public static void run(Path path){
        VisitsForCT visitsForCT = IOUtils.readCTVisits(path);

        Map<String, Integer> nonPrivatePtntCTCount = getNonPrivateCTCount(visitsForCT); // Calculate non-private patient counts per condition type
        Map<String, Integer> privatePtntCTCount = getPrivatePtntCTCount(visitsForCT); // Calculate private patient counts per condition type

        IOUtils.writeCountsPerConditionType(nonPrivatePtntCTCount, NON_PRIVATE_OUTPUT); // Write non-private counts to file
        IOUtils.writeCountsPerConditionType(privatePtntCTCount, PRIVATE_OUTPUT); // Write private counts to file
    }

    static Map<String, Integer> getNonPrivateCTCount(VisitsForCT visit){
        Map<String,Integer> ptntPerCond = new HashMap<>();
        for (String cond : visit.getCTWithData()){
            Set<String> uniquePtnt = new HashSet<>();
            for (PatientRecord record : visit.getVisitsForCT(cond)) {
                uniquePtnt.add(record.name); // Collect unique patient names for the condition type
            }
            ptntPerCond.put(cond, uniquePtnt.size()); // Store the count of unique patients for the condition type
        }
        return ptntPerCond;
    }

    static Map<String, Integer> getPrivatePtntCTCount(VisitsForCT visits){
        Map<String, Integer> privateCnt = new HashMap<>();

        VisitsForCT boundedVisits = ContributionBoundingUtils.boundContributedCT(visits, MAX_CONTRIBUTED_COND_TYPE);
        
        for (String cond: boundedVisits.getCTWithData()){
            Set<String> uniquePtnt = new HashSet<>();
            for (PatientRecord record : boundedVisits.getVisitsForCT(cond)){
                uniquePtnt.add(record.name); // Collect unique patient names for the condition type
            }

            Count dpCount = Count.builder()
                .epsilon(LN_3)
                .maxPartitionsContributed(MAX_CONTRIBUTED_COND_TYPE)
                .build();
            dpCount.incrementBy(uniquePtnt.size()); // Report the number of unique patients in the condition type to DP count
            privateCnt.put(cond, (int) dpCount.computeResult()); // DP count and store in the result
        }
        return privateCnt;
    }
}
