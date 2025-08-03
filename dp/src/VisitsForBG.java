package dp.src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.src.PatientRecord;

class VisitsForBG {
  private final Map<String, List<PatientRecord>> visits;

  VisitsForBG() {
    visits = new HashMap<>();
  }

  /**
   * Adds the given {@link Visit}.
   */
  void addVisit(PatientRecord record) {
    String BG = record.bloodType;
    visits.computeIfAbsent(BG, k -> new ArrayList<>()).add(record);
  }

  /**
   * Returns all visits for a specific Blood Group.
   */
  List<PatientRecord> getVisitsForBG(String BG) {
    return visits.getOrDefault(BG, new ArrayList<>());
  }

  /**
   * Returns the set of blood groups for which there is visit data.
   */
  Set<String> getBGWithData() {
    return visits.keySet();
  }

  /**
   * Returns a single list containing all visits from all blood groups.
   */
  List<PatientRecord> getAllVisits() {
    List<PatientRecord> allVisits = new ArrayList<>();
    for (String BG : getBGWithData()) {
      Collection<PatientRecord> visitsForBG = getVisitsForBG(BG);
      allVisits.addAll(visitsForBG);
    }
    return allVisits;
  }
}