package dp.src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.src.PatientRecord;

class VisitsForCT {
  private final Map<String, List<PatientRecord>> visits;

  VisitsForCT() {
    visits = new HashMap<>();
  }

  /**
   * Adds the given {@link Visit}.
   */
  void addVisit(PatientRecord record) {
    String CT = record.condition;
    visits.computeIfAbsent(CT, k -> new ArrayList<>()).add(record);
  }

  /**
   * Returns all visits for a specific condition.
   */
  List<PatientRecord> getVisitsForCT(String CT) {
    return visits.getOrDefault(CT, new ArrayList<>());
  }

  /**
   * Returns the set of conditions for which there is visit data.
   */
  Set<String> getCTWithData() {
    return visits.keySet();
  }

  /**
   * Returns a single list containing all visits from all conditions.
   */
  List<PatientRecord> getAllVisits() {
    List<PatientRecord> allVisits = new ArrayList<>();
    for (String CT : getCTWithData()) {
      Collection<PatientRecord> visitsForCT = getVisitsForCT(CT);
      allVisits.addAll(visitsForCT);
    }
    return allVisits;
  }
}