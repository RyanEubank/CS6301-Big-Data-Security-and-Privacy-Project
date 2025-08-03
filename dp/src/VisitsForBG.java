package dp.src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.src.PatientRecord;

/**
 * A collection of patient visits, grouped by blood group. This class provides
 * a way to store and retrieve {@link PatientRecord} objects based on their
 * blood type.
 */
class VisitsForBG {
  private final Map<String, List<PatientRecord>> visits;

  VisitsForBG() {
    visits = new HashMap<>();
  }

  /**
   * Adds the given {@link PatientRecord} to a list associated with its blood group.
   *
   * @param record The patient record to add.
   */
  void addVisit(PatientRecord record) {
    String BG = record.bloodType;
    visits.computeIfAbsent(BG, k -> new ArrayList<>()).add(record);
  }

  /**
   * Returns all visits for a specific blood group (e.g., "O+").
   *
   * @param BG The string representing the blood group.
   * @return A list of patient records for the specified blood group. Returns an empty list if the group does not exist.
   */
  List<PatientRecord> getVisitsForBG(String BG) {
    return visits.getOrDefault(BG, new ArrayList<>());
  }

  /**
   * Returns the set of all blood groups that have at least one visit record.
   *
   * @return A set of strings, where each string is a blood group.
   */
  Set<String> getBGWithData() {
    return visits.keySet();
  }

  /**
   * Returns a single list containing all visits from all blood groups.
   *
   * @return A new list containing all stored patient records.
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
