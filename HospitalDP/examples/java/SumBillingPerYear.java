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

import com.google.privacy.differentialprivacy.BoundedSum;
import java.time.Year;
import java.util.Map;
import java.util.HashMap;

/**
 * Reads yearly visits from {@link InputFilePath#PATIENT_STATISTICS}, calculates the
 * raw and anonymized amount of money spent by the PATIENTS per year
 * and prints them to {@link #NON_PRIVATE_OUTPUT} and {@link #PRIVATE_OUTPUT} correspondingly.
 * Assumes that a patient may enter the Hospital multiple times across years.
 */
public class SumBillingPerYear {
  private static final String NON_PRIVATE_OUTPUT = "non_private_sums_per_year.csv";
  private static final String PRIVATE_OUTPUT = "private_sums_per_year.csv";

  private static final double LN_3 = Math.log(3);

  /**
   * Number of visits contributed by a single patient will be limited to 2. All exceeding
   * visits will be discarded. In the dataset though there very minimal duplicate names, so 2 shouuld be good.
   */
  private static final int MAX_CONTRIBUTED_YEARS = 2;
  /** Minimum amount of money we expect a patient billed in a single visit. */
  private static final int MIN_EUROS_SPENT = -2009;
  /** Maximum amount of money we expect a patient billed in a single visit. */
  private static final int MAX_EUROS_SPENT = 50000;

  private SumBillingPerYear() { }

  /**
   * Reads statistics for all years, calculates raw and anonymized sums of money spent by visits
   * per year, and writes the results.
   * {@see the Javadoc of {@link SumRevenuePerDay} for more details}.
   */
  public static void run() {
    VisitsForYear visitsForYear = IOUtils.readYearlyVisits(InputFilePath.PATIENT_STATISTICS);

    Map<Year, Integer> nonPrivateSums = getNonPrivateSums(visitsForYear);
    Map<Year, Integer> privateSums = getPrivateSums(visitsForYear);

    IOUtils.writeCountsPerYear(nonPrivateSums, NON_PRIVATE_OUTPUT);
    IOUtils.writeCountsPerYear(privateSums, PRIVATE_OUTPUT);
  }

  /** Returns total raw billing amount for each year. */
  static Map<Year, Integer> getNonPrivateSums(VisitsForYear visits) {
    Map<Year, Integer> sumsPerYear = new HashMap<>();
    for (Year y : visits.getYearsWithData()) {
      int sum = 0;
      for (Visit v : visits.getVisitsForYear(y)) {
        sum += v.billingAmount();
      }
      sumsPerYear.put(y, sum);
    }
    return sumsPerYear;
  }

  /** Returns total anonymized billing amount for each year. */
  private static Map<Year, Integer> getPrivateSums(VisitsForYear visits) {
    Map<Year, Integer> privateSumsPerYear = new HashMap<>();

    // Pre-process the data set: limit the number of visits to MAX_CONTRIBUTED_YEARS
    // per name.
    VisitsForYear boundedVisits =
        ContributionBoundingUtils.boundContributedYears(visits, MAX_CONTRIBUTED_YEARS);

    for (Year y : boundedVisits.getYearsWithData()) {
      BoundedSum dpSum =
          BoundedSum.builder()
              .epsilon(LN_3)
              // The data was pre-processed so that each patient may visit the hospital up to
              // MAX_CONTRIBUTED_YEARS
              // Note: while the library accepts this limit as a configurable parameter,
              // it doesn't pre-process the data to ensure this limit is respected.
              // It is responsibility of the caller to ensure the data passed to the library
              // is capped for getting the correct privacy guarantee.
              .maxPartitionsContributed(MAX_CONTRIBUTED_YEARS)
              // No need to pre-process the data: BoundedSum will clamp the input values.
              .lower(MIN_EUROS_SPENT)
              .upper(MAX_EUROS_SPENT)
              .build();

      for (Visit v : boundedVisits.getVisitsForYear(y)) {
        dpSum.addEntry(v.billingAmount());
      }

      privateSumsPerYear.put(y, (int) dpSum.computeResult());
    }

    return privateSumsPerYear;
  }
}
