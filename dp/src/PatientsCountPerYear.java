package dp.src;

import com.google.privacy.differentialprivacy.*;

import util.src.PatientRecord;

import java.nio.file.Path;
import java.time.Year;
import java.util.*;

/*VisitorCountPerYear calculates the number of unique patients  per year
 * calculates the raw and the anonymized counts of patients per year
 * and writes the results to a {@link #NON_PRIVATE_OUTPUT} and {@link #PRIVATE_OUTPUT} respectively
 * accounts for each unique patient.
*/
public class PatientsCountPerYear {
    private static final String NON_PRIVATE_OUTPUT = "dp/out/non_private_counts_per_year.csv";
    private static final String PRIVATE_OUTPUT = "dp/out/private_counts_per_year.csv";

    private static final double LN_3 = Math.log(1.5); // Epsilon value for differential privacy
    private static final int MAX_CONTRIBUTED_YEARS = 2; // max number of years a patient can contribute to
    

    public static void run(Path path){ 
        VisitsForYear visitsForYear = IOUtils.readYearlyVisits(path);
        
        Map<Year, Integer> nonPrivatePtntCnt = getNonPrivatePatientCount(visitsForYear); // calc non-private patient/year counts
        Map<Year, Integer> privatePtntCnt = getPrivatePatientCount(visitsForYear); // calc private patient/year counts

        IOUtils.writeCountPerYear(nonPrivatePtntCnt, NON_PRIVATE_OUTPUT); // Write non-private counts to file
        IOUtils.writeCountPerYear(privatePtntCnt, PRIVATE_OUTPUT); // Write private counts to file

    }
    
    // returns a map of Year to the raw number of unique patients admitted in that year 
    static Map<Year, Integer> getNonPrivatePatientCount(VisitsForYear visits) {
        Map<Year, Integer> ptntCntPerYear = new HashMap<>();
        for (Year year : visits.getYearsWithData()) {
            Set<Integer> uniquePatients = new HashSet<>();
            for (PatientRecord record : visits.getVisitsForYear(year)) {
                uniquePatients.add(record.id);
            }
            ptntCntPerYear.put(year, uniquePatients.size());
        }
        return ptntCntPerYear;

    }
    // returns a map of Year to the anonymized number of unique patients admitted in that year
    // data is anonymized using differential privacy
    static Map<Year, Integer> getPrivatePatientCount(VisitsForYear visits){
        Map<Year, Integer> privateCnt = new HashMap<>();
        
        // Bound the contribution: limit each patient to MAX_CONTRIBUTED_YEARS 
        VisitsForYear boundedVisits = ContributionBoundingUtils.boundContributedYears(visits, MAX_CONTRIBUTED_YEARS);
        
        for (Year year : boundedVisits.getYearsWithData()){
            Set<Integer> uniquePtnt = new HashSet<>();
            for (PatientRecord record : boundedVisits.getVisitsForYear(year)) {
                uniquePtnt.add(record.id);
            }

            Count dpCount = Count.builder()
                .epsilon(LN_3)
                .maxPartitionsContributed(MAX_CONTRIBUTED_YEARS)
                .build();
            
            dpCount.incrementBy(uniquePtnt.size());
            privateCnt.put(year, (int) dpCount.computeResult());
        }
        return privateCnt;
    }

}