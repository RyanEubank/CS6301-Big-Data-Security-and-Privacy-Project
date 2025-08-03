package dp.src;
import com.google.privacy.differentialprivacy.*;

import util.src.PatientRecord;

import java.nio.file.Path;
import java.time.Year;
import java.util.*;

public class PatientCountPerBloodType {
    private static final String NON_PRIVATE_OUTPUT = "dp/out/non_private_counts_per_bloodType.csv";
    private static final String PRIVATE_OUTPUT = "dp/out/private_counts_per_bloodType.csv";
    private static final double LN_X = Math.log(1.1);
    private static final int MAX_CONTRIBUTED_BLOOD_TYPE = 1; // 1 patient should not have more than 1 blood type 

    private PatientCountPerBloodType() { }

    public static void run(Path path){
        VisitsForBG visitsForBG = IOUtils.readBGVisits(path);

        Map<String, Integer> nonPrivatePtntBGCount = getNonPrivateBGCount(visitsForBG); // Calculate non-private patient counts per blood type
        Map<String, Integer> privatePtntBGCount = getPrivateBGCount(visitsForBG); // Calculate private patient counts per blood type

        IOUtils.writeCountsPerBloodType(nonPrivatePtntBGCount, NON_PRIVATE_OUTPUT); // Write non-private counts to file
        IOUtils.writeCountsPerBloodType(privatePtntBGCount, PRIVATE_OUTPUT); // Write private counts to file
    }

    static Map<String, Integer> getNonPrivateBGCount(VisitsForBG visits){
        Map<String, Integer> ptntPerBloodType = new HashMap<>();
        for (String bloodType : visits.getBGWithData()){
            Set<Integer> uniquePtnt = new HashSet<>();
            for (PatientRecord record : visits.getVisitsForBG(bloodType)){
                uniquePtnt.add(record.id); // Collect unique patient names for the blood type
            }
            ptntPerBloodType.put(bloodType, uniquePtnt.size()); // Store the count of unique patients for the blood type
        }
        return ptntPerBloodType;
    }

    static Map<String, Integer> getPrivateBGCount(VisitsForBG visits){
        Map<String, Integer> privateCnt = new HashMap<>();

        VisitsForBG boundedVisits = ContributionBoundingUtils.boundContributedBG(visits, MAX_CONTRIBUTED_BLOOD_TYPE);

        for (String bloodType : boundedVisits.getBGWithData()){
            Set<Integer> uniquePtnt = new HashSet<>();
            for (PatientRecord record : boundedVisits.getVisitsForBG(bloodType)){
                uniquePtnt.add(record.id); // Collect unique patient names for the blood type
            }

            Count dpCount = Count.builder()
                .epsilon(LN_X)
                .maxPartitionsContributed(MAX_CONTRIBUTED_BLOOD_TYPE)
                .build();
            dpCount.incrementBy(uniquePtnt.size()); // Report the number of unique patients in the blood type to DP count
            privateCnt.put(bloodType, (int) dpCount.computeResult()); // DP count and store in the result
        }

        return privateCnt;
    }
}
