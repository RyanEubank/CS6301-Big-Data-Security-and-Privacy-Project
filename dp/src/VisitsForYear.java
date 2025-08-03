package dp.src;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.src.PatientRecord;

class VisitsForYear {
  private final Map<Year, List<PatientRecord>> visits;

  VisitsForYear() {
    visits = new HashMap<>();
  }

  /**
   * Adds the given {@link Visit}.
   */
  void addVisit(PatientRecord record) {
    Year year = record.yearAdmitted();
    visits.computeIfAbsent(year, k -> new ArrayList<>()).add(record);
  }

  /**
   * Returns all visits for a specific year.
   */
  List<PatientRecord> getVisitsForYear(Year year) {
    return visits.getOrDefault(year, new ArrayList<>());
  }

  /**
   * Returns the set of years for which there is visit data.
   */
  Set<Year> getYearsWithData() {
    return visits.keySet();
  }

  /**
   * Returns a single list containing all visits from all years.
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