package dp.src;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.src.PatientRecord;

/**
 * A collection of patient visits, grouped by the year of admission. This class
 * provides a way to store and retrieve {@link PatientRecord} objects based on
 * the year they occurred.
 */
class VisitsForYear {
  private final Map<Year, List<PatientRecord>> visits;

  VisitsForYear() {
    visits = new HashMap<>();
  }

  /**
   * Adds the given {@link PatientRecord} to a list associated with its admission year.
   *
   * @param record The patient record to add.
   */
  void addVisit(PatientRecord record) {
    Year year = record.yearAdmitted();
    visits.computeIfAbsent(year, k -> new ArrayList<>()).add(record);
  }

  /**
   * Returns all visits for a specific year.
   *
   * @param year The year for which to retrieve visits.
   * @return A list of patient records for the specified year. Returns an empty list if the year does not exist.
   */
  List<PatientRecord> getVisitsForYear(Year year) {
    return visits.getOrDefault(year, new ArrayList<>());
  }

  /**
   * Returns the set of all years that have at least one visit record.
   *
   * @return A set of {@link Year} objects.
   */
  Set<Year> getYearsWithData() {
    return visits.keySet();
  }

  /**
   * Returns a single list containing all visits from all years.
   *
   * @return A new list containing all stored patient records.
   */
  List<PatientRecord> getAllVisits() {
    List<PatientRecord> allVisits = new ArrayList<>();
    for (Year y : getYearsWithData()) {
      Collection<PatientRecord> visitsForYear = getVisitsForYear(y);
      allVisits.addAll(visitsForYear);
    }
    return allVisits;
  }
}
