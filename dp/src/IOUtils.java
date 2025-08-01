package dp.src;

import static java.nio.charset.StandardCharsets.UTF_8;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;

import util.src.PatientRecord;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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

  private IOUtils() {}

  /**
   * Reads daily patients' data.
   * {@see #convertCsvLineWithoutDayToList} for details on the format.
   */
  static ImmutableSet<PatientRecord> readDailyBilling(String file) {
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
  private static PatientRecord convertLineToVisit(String line) {
    return new PatientRecord(line.split(","));
  }

  /**
   * Reads yearly patients' data. Assumes that the input file is a .csv file of format "name,
   * age, gender, bloodType, medicalCondition, dateEntered,insurance,billingAmount,admissionType,dateDischarged,medication,testResults".
   */
  static VisitsForYear readYearlyVisits(Path path) {
    VisitsForYear result = new VisitsForYear();

    try {
      List<String> visitsAsText = Files.readAllLines(path);
      visitsAsText.stream()
          .skip(1)
          .forEach(v -> {
            PatientRecord record = convertLineToVisit(v);
            result.addVisit(record);
          });

    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    
    return result;
  }

  
  static VisitsForBG readBGVisits(Path path) {
    VisitsForBG result = new VisitsForBG();

    try {
      List<String> visitsAsText = Files.readAllLines(path);
      visitsAsText.stream()
              .skip(1)
              .forEach(v -> {
                PatientRecord record = convertLineToVisit(v);
                result.addVisit(record);
              });

    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    return result;
  }

  
  static VisitsForCT readCTVisits(Path path) {
    VisitsForCT result = new VisitsForCT();

    try {
      List<String> visitsAsText = Files.readAllLines(path);
      visitsAsText.stream()
              .skip(1)
              .forEach(v -> {
                PatientRecord record = convertLineToVisit(v);
                result.addVisit(record);
              });

    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    return result;
  }


  static void writeMeansPerCT(Map<String, Double> means, String file) {
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Condition,MeanBilling\n"); // Write header
      // Format the double to two decimal places for cleaner output.
      String format = "%s,%.2f\n";
      means.forEach(
              (CT, mean) -> pw.write(String.format(format, CT, mean)));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }


  static void writeMeansPerYear(Map<Year, Double> means, String file) {
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Year,MeanBilling\n"); // Write header
      // Format the double to two decimal places for cleaner output.
      String format = "%d,%.2f\n";
      means.forEach(
              (year, mean) -> pw.write(String.format(format, year.getValue(), mean)));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  static void writeCountsPerYear(Map<Year, Integer> counts, String file) {
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Year,SumBilling\n"); // Write header
      String format = "%d,%d\n";
      counts.forEach(
          (year, count) -> pw.write(String.format(format, year.getValue(), count)));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  
  static void writeCountsPerBG(Map<String, Integer> counts, String file) {
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Blood Group,SumBilling\n"); // Write header
      String format = "%s,%d\n";
      counts.forEach(
              (BG, count) -> pw.write(String.format(format, BG, count)));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
