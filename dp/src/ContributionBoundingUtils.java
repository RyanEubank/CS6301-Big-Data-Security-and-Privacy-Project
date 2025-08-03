package dp.src;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.src.PatientRecord;

/**
 * A utility class for applying contribution bounding to a patient dataset.
 * This is a crucial pre-processing step for differential privacy, ensuring that no
 * single patient can disproportionately influence the outcome of an analysis by
 * contributing to an excessive number of partitions (e.g., years, conditions).
 */
public class ContributionBoundingUtils {

  private ContributionBoundingUtils() {
  }

  /**
   * Limits the number of distinct years a single patient can contribute records to.
   *
   * @param visits The original collection of visits, grouped by year.
   * @param maxContributedYears The maximum number of different years a patient can contribute to.
   * @return A new VisitsForYear object containing the bounded collection of visits.
   */
  static VisitsForYear boundContributedYears(VisitsForYear visits, int maxContributedYears) {
    // Tracks the unique years each patient ID has contributed to.
    Map<Integer, Set<Year>> boundedVisitorYears = new HashMap<>();
    VisitsForYear boundedVisits = new VisitsForYear();
    List<PatientRecord> allVisits = new ArrayList<>();

    // Flatten all visits into a single list.
    for (Year y : visits.getYearsWithData()) {
      Collection<PatientRecord> visitsForYear = visits.getVisitsForYear(y);
      allVisits.addAll(visitsForYear);
    }
    // Shuffle the records to prevent any bias from the original data ordering.
    // The selection of which years are kept for a patient will be random.
    Collections.shuffle(allVisits);

    // Iterate through each visit and decide whether to include it based on the contribution cap.
    for (PatientRecord record : allVisits) {
      int id = record.id;
      Year visitYear = record.yearAdmitted();

      // Get the set of years this patient has already contributed to.
      Set<Year> visitorYears = boundedVisitorYears.computeIfAbsent(id, k -> new HashSet<>());

      // Add the record if the patient has already contributed to this year,
      // or if they have not yet reached their contribution limit for new years.
      if (visitorYears.contains(visitYear) || visitorYears.size() < maxContributedYears) {
        visitorYears.add(visitYear); // Add the year (no effect if already present).
        boundedVisits.addVisit(record);
      }
    }
    return boundedVisits;
  }

  /**
   * Limits the number of distinct blood groups a single patient can contribute records to.
   *
   * @param visits The original collection of visits, grouped by blood group.
   * @param maxContributedBG The maximum number of different blood groups a patient can contribute to.
   * @return A new VisitsForBG object containing the bounded collection of visits.
   */
  static VisitsForBG boundContributedBG(VisitsForBG visits, int maxContributedBG) {
    // Tracks the unique blood groups each patient ID has contributed to.
    Map<Integer, Set<String>> boundedVisitorBG = new HashMap<>();
    VisitsForBG boundedVisits = new VisitsForBG();
    List<PatientRecord> allVisits = new ArrayList<>();

    // Flatten all visits into a single list.
    for (String BG : visits.getBGWithData()) {
      Collection<PatientRecord> visitsForBG = visits.getVisitsForBG(BG);
      allVisits.addAll(visitsForBG);
    }
    // Shuffle to prevent ordering bias.
    Collections.shuffle(allVisits);

    // Iterate through each visit and enforce the contribution cap.
    for (PatientRecord record : allVisits) {
      int id = record.id;
      String visitBG = record.bloodType;

      // Get the set of blood groups this patient has already contributed to.
      Set<String> visitorBG = boundedVisitorBG.computeIfAbsent(id, k -> new HashSet<>());

      // Add the record if the patient has contributed to this blood group before,
      // or if they are still under their contribution limit for new blood groups.
      if (visitorBG.contains(visitBG) || visitorBG.size() < maxContributedBG) {
        visitorBG.add(visitBG);
        boundedVisits.addVisit(record);
      }
    }
    return boundedVisits;
  }

  /**
   * Limits the number of distinct medical conditions a single patient can contribute records to.
   *
   * @param visits The original collection of visits, grouped by condition.
   * @param maxContributedCT The maximum number of different conditions a patient can contribute to.
   * @return A new VisitsForCT object containing the bounded collection of visits.
   */
  static VisitsForCT boundContributedCT(VisitsForCT visits, int maxContributedCT) {
    // Tracks the unique medical conditions each patient ID has contributed to.
    Map<Integer, Set<String>> boundedVisitorCT = new HashMap<>();
    VisitsForCT boundedVisits = new VisitsForCT();
    List<PatientRecord> allVisits = new ArrayList<>();

    // Flatten all visits into a single list.
    for (String CT : visits.getCTWithData()) {
      Collection<PatientRecord> visitsForCT = visits.getVisitsForCT(CT);
      allVisits.addAll(visitsForCT);
    }
    // Shuffle to prevent ordering bias.
    Collections.shuffle(allVisits);

    // Iterate through each visit and enforce the contribution cap.
    for (PatientRecord record : allVisits) {
      int id = record.id;
      String visitCT = record.condition;

      // Get the set of conditions this patient has already contributed to.
      Set<String> visitorCT = boundedVisitorCT.computeIfAbsent(id, k -> new HashSet<>());

      // Add the record if the patient has contributed to this condition before,
      // or if they are still under their contribution limit for new conditions.
      if (visitorCT.contains(visitCT) || visitorCT.size() < maxContributedCT) {
        visitorCT.add(visitCT);
        boundedVisits.addVisit(record);
      }
    }
    return boundedVisits;
  }

  /**
   * Limits the number of distinct age groups a single patient can contribute records to.
   *
   * @param visits The original collection of visits, grouped by age group.
   * @param maxContributedAG The maximum number of different age groups a patient can contribute to.
   * @return A new VisitsForAG object containing the bounded collection of visits.
   */
  static VisitsForAG boundContributedAG(VisitsForAG visits, int maxContributedAG) {
    // Tracks the unique age groups each patient ID has contributed to.
    Map<Integer, Set<String>> boundedVisitorAG = new HashMap<>();
    VisitsForAG boundedVisits = new VisitsForAG();
    List<PatientRecord> allVisits = new ArrayList<>();

    // Flatten all visits into a single list.
    for (String AG : visits.getAGWithData()) {
      Collection<PatientRecord> visitsForAG = visits.getVisitsForAG(AG);
      allVisits.addAll(visitsForAG);
    }
    // Shuffle to prevent ordering bias.
    Collections.shuffle(allVisits);

    // Iterate through each visit and enforce the contribution cap.
    for (PatientRecord record : allVisits) {
      int id = record.id;
      String ageGroup = record.ageGroup;

      // Skip records where the age group could not be determined.
      if (ageGroup == null) {
        continue;
      }

      // Get the set of age groups this patient has already contributed to.
      Set<String> visitorAG = boundedVisitorAG.computeIfAbsent(id, k -> new HashSet<>());

      // Add the record if the patient has contributed to this age group before,
      // or if they are still under their contribution limit for new age groups.
      if (visitorAG.contains(ageGroup) || visitorAG.size() < maxContributedAG) {
        visitorAG.add(ageGroup);
        boundedVisits.addVisit(record);
      }
    }
    return boundedVisits;
  }
}