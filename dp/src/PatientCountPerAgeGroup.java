package dp.src;

import com.google.privacy.differentialprivacy.*;

import util.src.PatientRecord;

import java.nio.file.Path;
import java.time.Year;
import java.util.*;

public class PatientCountPerAgeGroup {
    private static final String NON_PRIVATE_OUTPUT = "dp/out/non_private_counts_per_age_group.csv";
    private static final String PRIVATE_OUTPUT = "dp/out/private_counts_per_age_group.csv";

    private static final double LN_X = Math.log(1.05); 
    private static final int MAX_CONTRIBUTED_AGE_GROUP = 2;

    private PatientCountPerAgeGroup() { }

    public static void run(Path path) {
        VisitsForAG visitsForAG = IOUtils.readAGVisits(path);

        Map<String, Integer> nonPrivatePtntAGCnt = getNonPrivateAGCount(visitsForAG);  // Calculate non-private patient counts per age group
        Map<String, Integer> privatePtntAGCnt = getPrivateAGCount(visitsForAG); // Calculate private patient counts per age group

        IOUtils.writeCountsPerAgeGroup(nonPrivatePtntAGCnt, NON_PRIVATE_OUTPUT); // Write non-private counts to file
        IOUtils.writeCountsPerAgeGroup(privatePtntAGCnt, PRIVATE_OUTPUT); // Write private counts to file
    
    }

    static Map<String, Integer> getNonPrivateAGCount(VisitsForAG visits){
        Map<String, Integer> ptntPerAgeGroup = new HashMap<>();
        for (String ageGrp : visits.getAGWithData()){
            Set<Integer> uniquePtnt = new HashSet<>();
            for (PatientRecord record : visits.getVisitsForAG(ageGrp)) {
                uniquePtnt.add(record.id); // Collect unique patient names for the age group
            }
            ptntPerAgeGroup.put(ageGrp, uniquePtnt.size()); // Store the count of unique patients for the age group
        }
        return ptntPerAgeGroup;
    }

    static Map<String, Integer> getPrivateAGCount(VisitsForAG visits){
        Map<String, Integer> privateCnt = new HashMap<>();

        VisitsForAG boundedVisitsForAG = ContributionBoundingUtils.boundContributedAG(visits, MAX_CONTRIBUTED_AGE_GROUP);

        for (String ageGrp : boundedVisitsForAG.getAGWithData()){
            Set<Integer> uniquePtnt = new HashSet<>();
            for (PatientRecord record : boundedVisitsForAG.getVisitsForAG(ageGrp)){
                uniquePtnt.add(record.id); // Collect unique patient names for the age group
            }

            Count dpCount = Count.builder()
                .epsilon(LN_X)
                .maxPartitionsContributed(MAX_CONTRIBUTED_AGE_GROUP)
                .build();
            dpCount.incrementBy(uniquePtnt.size());
            privateCnt.put(ageGrp, (int) dpCount.computeResult());
        }
        return privateCnt;
    }
}
