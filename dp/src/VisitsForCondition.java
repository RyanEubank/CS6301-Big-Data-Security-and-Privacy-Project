package dp.src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.src.PatientRecord;

/**
 * A collection of patient visits, grouped by medical condition. This class provides
 * a way to store and retrieve {@link PatientRecord} objects based on their
 * diagnosed condition.
 */
class VisitsForCT {
  private final Map<String, List<PatientRecord>> visits;

  VisitsForCT() {
    visits = new HashMap<>();
  }

  /**
   * Adds the given {@link PatientRecord} to a list associated with its medical condition.
   *
   * @param record The patient record to add.
   */
  void addVisit(PatientRecord record) {
    String CT = record.condition;
    visits.computeIfAbsent(CT, k -> new ArrayList<>()).add(record);
  }

  /**
   * Returns all visits for a specific medical condition (e.g., "Asthma").
   *
   * @param CT The string representing the medical condition.
   * @return A list of patient records for the specified condition. Returns an empty list if the condition does not exist.
   */
  List<PatientRecord> getVisitsForCT(String CT) {
    return visits.getOrDefault(CT, new ArrayList<>());
  }

  /**
   * Returns the set of all medical conditions that have at least one visit record.
   *
   * @return A set of strings, where each string is a medical condition.
   */
  Set<String> getCTWithData() {
    return visits.keySet();
  }

  /**
   * Returns a single list containing all visits from all medical conditions.
   *
   * @return A new list containing all stored patient records.
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
