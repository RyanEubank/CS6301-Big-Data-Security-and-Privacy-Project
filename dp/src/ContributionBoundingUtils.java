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
 * Static utils that bound contributions on the input data.
 */
public class ContributionBoundingUtils {

  private ContributionBoundingUtils() { }

  /**
   * @return {@link VisitsForYear} containing the hospital visits where the number of Years
   * contributed by a single patient is limited to {@code maxContributedYears}.
   *
   * TODO: Generalize the logic to be used for different partition keys.
   */
  static VisitsForYear boundContributedYears(VisitsForYear visits, int maxContributedYears) {
    Map<Integer, Set<Year>> boundedVisitorYears = new HashMap<>();
    List<PatientRecord> allVisits = new ArrayList<>();
    VisitsForYear boundedVisits = new VisitsForYear();

    // Add all visits to a list in order to shuffle them.
    for (Year y : visits.getYearsWithData()) {
      Collection<PatientRecord> visitsForYear = visits.getVisitsForYear(y);
      allVisits.addAll(visitsForYear);
    }
    Collections.shuffle(allVisits);

    // For each id, copy their visits for at most maxContributedYears to the result,
    // limiting the number of Years a patient id can contribute.
    for (PatientRecord record : allVisits) {
      int id = record.id;
      Year visitYear = record.yearAdmitted();
      if (boundedVisitorYears.containsKey(id)) {
        Set<Year> visitorYears = boundedVisitorYears.get(id);
        if (visitorYears.contains(visitYear)) {
          boundedVisits.addVisit(record);
        } else if (visitorYears.size() < maxContributedYears) {
          visitorYears.add(visitYear);
          boundedVisits.addVisit(record);
        }
      } else {
        Set<Year> visitorYears = new HashSet<>();
        boundedVisitorYears.put(id, visitorYears);
        visitorYears.add(visitYear);
        boundedVisits.addVisit(record);
      }
    }

    return boundedVisits;
  }

  static VisitsForBG boundContributedBG(VisitsForBG visits, int maxContributedBG) {
    Map<Integer, Set<String>> boundedVisitorBG = new HashMap<>();
    List<PatientRecord> allVisits = new ArrayList<>();
    VisitsForBG boundedVisits = new VisitsForBG();

    // Add all visits to a list in order to shuffle them.
    for (String BG : visits.getBGWithData()) {
      Collection<PatientRecord> visitsForBG = visits.getVisitsForBG(BG);
      allVisits.addAll(visitsForBG);
    }
    Collections.shuffle(allVisits);

    // For each id, copy their visits for at most maxContributedBG to the result,
    // limiting the number of Blood Groups a patient id can contribute.
    for (PatientRecord record : allVisits) {
      int id = record.id;
      String visitBG = record.bloodType;
      if (boundedVisitorBG.containsKey(id)) {
        Set<String> visitorBG = boundedVisitorBG.get(id);
        if (visitorBG.contains(visitBG)) {
          boundedVisits.addVisit(record);
        } else if (visitorBG.size() < maxContributedBG) {
          visitorBG.add(visitBG);
          boundedVisits.addVisit(record);
        }
      } else {
        Set<String> visitorBG = new HashSet<>();
        boundedVisitorBG.put(id, visitorBG);
        visitorBG.add(visitBG);
        boundedVisits.addVisit(record);
      }
    }

    return boundedVisits;
  }

  static VisitsForCT boundContributedCT(VisitsForCT visits, int maxContributedCT) {
    Map<Integer, Set<String>> boundedVisitorCT = new HashMap<>();
    List<PatientRecord> allVisits = new ArrayList<>();
    VisitsForCT boundedVisits = new VisitsForCT();

    // Add all visits to a list in order to shuffle them.
    for (String CT : visits.getCTWithData()) {
      Collection<PatientRecord> visitsForCT = visits.getVisitsForCT(CT);
      allVisits.addAll(visitsForCT);
    }
    Collections.shuffle(allVisits);

    // For each id, copy their visits for at most maxContributedCT to the result,
    // limiting the number of conditions a patient id can contribute.
    for (PatientRecord record : allVisits) {
      int id = record.id;
      String visitCT = record.condition;
      if (boundedVisitorCT.containsKey(id)) {
        Set<String> visitorCT = boundedVisitorCT.get(id);
        if (visitorCT.contains(visitCT)) {
          boundedVisits.addVisit(record);
        } else if (visitorCT.size() < maxContributedCT) {
          visitorCT.add(visitCT);
          boundedVisits.addVisit(record);
        }
      } else {
        Set<String> visitorCT = new HashSet<>();
        boundedVisitorCT.put(id, visitorCT);
        visitorCT.add(visitCT);
        boundedVisits.addVisit(record);
      }
    }

    return boundedVisits;
  }
}
