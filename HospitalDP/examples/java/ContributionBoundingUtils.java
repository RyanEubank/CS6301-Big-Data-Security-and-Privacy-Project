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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    List<Visit> allVisits = new ArrayList<>();
    VisitsForYear boundedVisits = new VisitsForYear();

    // Add all visits to a list in order to shuffle them.
    for (Year y : visits.getYearsWithData()) {
      Collection<Visit> visitsForYear = visits.getVisitsForYear(y);
      allVisits.addAll(visitsForYear);
    }
    Collections.shuffle(allVisits);

    // For each name, copy their visits for at most maxContributedDays days to the result.
    for (Visit visit : allVisits) {
      String name = visit.name();
      Year visitYear = visit.year();
      if (boundedVisitorYears.containsKey(name)) {
        Set<Year> visitorYears = boundedVisitorYears.get(name);
        if (visitorYears.contains(visitYear)) {
          boundedVisits.addVisit(visit);
        } else if (visitorYears.size() < maxContributedYears) {
          visitorYears.add(visitYear);
          boundedVisits.addVisit(visit);
        }
      } else {
        Set<Year> visitorYears = new HashSet<>();
        boundedVisitorYears.put(name, visitorYears);
        visitorYears.add(visitYear);
        boundedVisits.addVisit(visit);
      }
    }

    return boundedVisits;
  }
}
