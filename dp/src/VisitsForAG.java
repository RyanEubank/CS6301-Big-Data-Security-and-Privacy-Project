package dp.src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.src.PatientRecord;

/**
 * A collection of patient visits, grouped by age group. This class provides
 * a way to store and retrieve {@link PatientRecord} objects based on the
 * pre-calculated age group string they belong to.
 */
class VisitsForAG {
  private final Map<String, List<PatientRecord>> visits;

  VisitsForAG() {
    visits = new HashMap<>();
  }

  /**
   * Adds the given {@link PatientRecord} to a list associated with its age group.
   *
   * @param record The patient record to add.
   */
  void addVisit(PatientRecord record) {
    // The following try-catch block for NumberFormatException appears to be a remnant
    // from a previous implementation. The current code uses the pre-computed ageGroup
    // string and does not perform any number parsing that would throw this exception.
    try {
      String AG = record.ageGroup;
      visits.computeIfAbsent(AG, k -> new ArrayList<>()).add(record);
    } catch (NumberFormatException e) {
      // This catch block is unlikely to be reached with the current implementation.
      System.err.println("Skipping record due to invalid age: " + record.age);
    }
  }


  /**
   * Returns all visits for a specific age group (e.g., "21-30").
   *
   * @param AG The string representing the age group.
   * @return A list of patient records for the specified age group. Returns an empty list if the group does not exist.
   */
  List<PatientRecord> getVisitsForAG(String AG) {
    return visits.getOrDefault(AG, new ArrayList<>());
  }

  /**
   * Returns the set of all age groups that have at least one visit record.
   *
   * @return A set of strings, where each string is an age group.
   */
  Set<String> getAGWithData() {
    return visits.keySet();
  }

  /**
   * Returns a single list containing all visits from all age groups.
   *
   * @return A new list containing all stored patient records.
   */
  List<PatientRecord> getAllVisits() {
    List<PatientRecord> allVisits = new ArrayList<>();
    for (String AG : getAGWithData()) {
      Collection<PatientRecord> visitsForGroup = getVisitsForAG(AG);
      allVisits.addAll(visitsForGroup);
    }
    return allVisits;
  }
}
