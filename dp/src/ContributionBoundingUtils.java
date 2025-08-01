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
    Map<String, Set<Year>> boundedVisitorYears = new HashMap<>();
    List<PatientRecord> allVisits = new ArrayList<>();
    VisitsForYear boundedVisits = new VisitsForYear();

    // Add all visits to a list in order to shuffle them.
    for (Year y : visits.getYearsWithData()) {
      Collection<PatientRecord> visitsForYear = visits.getVisitsForYear(y);
      allVisits.addAll(visitsForYear);
    }
    Collections.shuffle(allVisits);

    // For each name, copy their visits for at most maxContributedDays days to the result.
    for (PatientRecord record : allVisits) {
      String name = record.name;
      Year visitYear = record.yearAdmitted();
      if (boundedVisitorYears.containsKey(name)) {
        Set<Year> visitorYears = boundedVisitorYears.get(name);
        if (visitorYears.contains(visitYear)) {
          boundedVisits.addVisit(record);
        } else if (visitorYears.size() < maxContributedYears) {
          visitorYears.add(visitYear);
          boundedVisits.addVisit(record);
        }
      } else {
        Set<Year> visitorYears = new HashSet<>();
        boundedVisitorYears.put(name, visitorYears);
        visitorYears.add(visitYear);
        boundedVisits.addVisit(record);
      }
    }

    return boundedVisits;
  }

  static VisitsForBG boundContributedBG(VisitsForBG visits, int maxContributedBG) {
    Map<String, Set<String>> boundedVisitorBG = new HashMap<>();
    List<PatientRecord> allVisits = new ArrayList<>();
    VisitsForBG boundedVisits = new VisitsForBG();

    // Add all visits to a list in order to shuffle them.
    for (String BG : visits.getBGWithData()) {
      Collection<PatientRecord> visitsForBG = visits.getVisitsForBG(BG);
      allVisits.addAll(visitsForBG);
    }
    Collections.shuffle(allVisits);

    // For each name, copy their visits for at most maxContributedDays days to the result.
    for (PatientRecord record : allVisits) {
      String name = record.name;
      String visitBG = record.bloodType;
      if (boundedVisitorBG.containsKey(name)) {
        Set<String> visitorBG = boundedVisitorBG.get(name);
        if (visitorBG.contains(visitBG)) {
          boundedVisits.addVisit(record);
        } else if (visitorBG.size() < maxContributedBG) {
          visitorBG.add(visitBG);
          boundedVisits.addVisit(record);
        }
      } else {
        Set<String> visitorBG = new HashSet<>();
        boundedVisitorBG.put(name, visitorBG);
        visitorBG.add(visitBG);
        boundedVisits.addVisit(record);
      }
    }

    return boundedVisits;
  }

  static VisitsForCT boundContributedCT(VisitsForCT visits, int maxContributedCT) {
    Map<String, Set<String>> boundedVisitorCT = new HashMap<>();
    List<PatientRecord> allVisits = new ArrayList<>();
    VisitsForCT boundedVisits = new VisitsForCT();

    // Add all visits to a list in order to shuffle them.
    for (String CT : visits.getCTWithData()) {
      Collection<PatientRecord> visitsForCT = visits.getVisitsForCT(CT);
      allVisits.addAll(visitsForCT);
    }
    Collections.shuffle(allVisits);

    // For each name, copy their visits for at most maxContributedDays days to the result.
    for (PatientRecord record : allVisits) {
      String name = record.name;
      String visitCT = record.condition;
      if (boundedVisitorCT.containsKey(name)) {
        Set<String> visitorCT = boundedVisitorCT.get(name);
        if (visitorCT.contains(visitCT)) {
          boundedVisits.addVisit(record);
        } else if (visitorCT.size() < maxContributedCT) {
          visitorCT.add(visitCT);
          boundedVisits.addVisit(record);
        }
      } else {
        Set<String> visitorCT = new HashSet<>();
        boundedVisitorCT.put(name, visitorCT);
        visitorCT.add(visitCT);
        boundedVisits.addVisit(record);
      }
    }

    return boundedVisits;
  }
}
