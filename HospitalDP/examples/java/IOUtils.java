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

import static java.nio.charset.StandardCharsets.UTF_8;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Year;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Reads patients' data and prints statistics. */
class IOUtils {

  private static final String CSV_ITEM_SEPARATOR = ",";
  private static final DateTimeFormatter TIME_FORMATTER =
      new DateTimeFormatterBuilder()
          // case insensitive
          .parseCaseInsensitive()
          // pattern
          .appendPattern("M/d/yy")
            .toFormatter(Locale.ENGLISH);
  private static final String CSV_YEAR_COUNT_WRITE_TEMPLATE = "%d,%d\n";

  private IOUtils() {}

  /**
   * Reads daily patients' data.
   * {@see #convertCsvLineWithoutDayToList} for details on the format.
   */
  static ImmutableSet<Visit> readDailyBilling(String file) {
    try {
      List<String> visitsAsText =
          Resources.readLines(Resources.getResource(file), UTF_8);

      return visitsAsText.stream()
          .skip(1)
          .map(IOUtils::convertLineToVisit)
          .collect(toImmutableSet());
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Converts a line of format "name, age, gender, bloodType, medicalCondition, dateEntered,insurance,
   * billingAmount,admissionType,dateDischarged,medication,testResults" to
   * {@link Visit}.
   */
  private static Visit convertLineToVisit(String visitAsText) {
    Iterator<String> splitVisit = Splitter.on(CSV_ITEM_SEPARATOR).split(visitAsText).iterator();
    // element 0
    String name = splitVisit.next();
    // element 1
    String ageStr = splitVisit.next();
    int age = (int) Math.round(Double.parseDouble(ageStr));
    // element 2
    String gender = splitVisit.next();
    // element 3
    String bloodType = splitVisit.next();
    // element 4
    String medicalCondition = splitVisit.next();
    // element 5
    LocalDate dateEntered = LocalDate.parse(splitVisit.next(), TIME_FORMATTER);
    // element 6
    String insurance = splitVisit.next();
    // element 7
    String billingStr = splitVisit.next();
    int billingAmount = (int) Math.round(Double.parseDouble(billingStr));
    // element 8
    String admissionType = splitVisit.next();
    // element 9
    LocalDate dateDischarged = LocalDate.parse(splitVisit.next(), TIME_FORMATTER);
    // element 10
    String medication = splitVisit.next();
    // element 11
    String testResults = splitVisit.next();

    return Visit.create(name, age, gender, bloodType, medicalCondition, dateEntered,insurance,billingAmount,admissionType,dateDischarged,medication,testResults);
  }

  /**
   * Reads yearly patients' data. Assumes that the input file is a .csv file of format "name,
   * age, gender, bloodType, medicalCondition, dateEntered,insurance,billingAmount,admissionType,dateDischarged,medication,testResults".
   */
  static VisitsForYear readYearlyVisits(String file) {
    VisitsForYear result = new VisitsForYear();

    try {
      List<String> visitsAsText =
          Resources.readLines(Resources.getResource(file), UTF_8);
      visitsAsText.stream()
          .skip(1)
          .forEach(v -> {
            Visit visit = convertLineToVisit(v);
            result.addVisit(visit);
          });

    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    return result;
  }


  static void writeCountsPerYear(Map<Year, Integer> counts, String file) {
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      counts.forEach(
          (year, count) -> pw.write(String.format(CSV_YEAR_COUNT_WRITE_TEMPLATE, year.getValue(), count)));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
