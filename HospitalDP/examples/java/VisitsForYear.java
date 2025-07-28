//
// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.google.privacy.differentialprivacy.example;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class VisitsForYear {
  private final Map<Year, List<Visit>> visits;

  VisitsForYear() {
    visits = new HashMap<>();
  }

  /**
   * Adds the given {@link Visit}.
   */
  void addVisit(Visit visit) {
    Year year = visit.year();
    visits.computeIfAbsent(year, k -> new ArrayList<>()).add(visit);
  }

  /**
   * Returns all visits for a specific year.
   */
  List<Visit> getVisitsForYear(Year year) {
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
  List<Visit> getAllVisits() {
    List<Visit> allVisits = new ArrayList<>();
    for (Year y : getYearsWithData()) {
      Collection<Visit> visitsForYear = getVisitsForYear(y);
      allVisits.addAll(visitsForYear);
    }
    return allVisits;
  }
}