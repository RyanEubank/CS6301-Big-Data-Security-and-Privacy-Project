package dp.src;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.nio.file.Path;
import java.time.Year;
import java.util.Map;
import util.src.PatientRecord;

/**
 * Utility class for reading patient data from and writing aggregated statistics to CSV files.
 * This class handles the parsing of patient records and the formatting of output data.
 */
class IOUtils {

  // This class is not meant to be instantiated.
  private IOUtils() {}

  /**
   * Reads patient records from a resource file within the project.
   * Assumes the first line is a header, which will be skipped.
   *
   * @param file The name of the resource file to read.
   * @return An {@link ImmutableSet} of {@link PatientRecord} objects.
   * @throws IllegalStateException if the file cannot be read.
   */
  static ImmutableSet<PatientRecord> readDailyBilling(String file) {
    try {
      // Read all lines from the specified resource file.
      List<String> visitsAsText =
              Resources.readLines(Resources.getResource(file), UTF_8);

      // Stream the lines, skip the header, convert each line to a PatientRecord,
      // and collect the results into an immutable set.
      return visitsAsText.stream()
              .skip(1)
              .map(IOUtils::convertLineToVisit)
              .collect(toImmutableSet());
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read resource file: " + file, e);
    }
  }

  /**
   * Converts a single CSV line into a {@link PatientRecord}.
   * The expected format is a comma-separated string with patient attributes.
   *
   * @param line A single line from a CSV file.
   * @return A new {@link PatientRecord} instance.
   */
  private static PatientRecord convertLineToVisit(String line) {
    // A more robust implementation might use a proper CSV parsing library.
    return new PatientRecord(line.split(","));
  }

  /**
   * Reads patient records from a CSV file and organizes them by year of admission.
   *
   * @param path The {@link Path} to the input CSV file.
   * @return A {@link VisitsForYear} object containing the parsed data.
   * @throws IllegalStateException if the file cannot be read.
   */
  static VisitsForYear readYearlyVisits(Path path) {
    VisitsForYear result = new VisitsForYear();
    try {
      List<String> visitsAsText = Files.readAllLines(path);
      visitsAsText.stream()
              .skip(1) // Skip header row.
              .map(IOUtils::convertLineToVisit)
              .forEach(result::addVisit);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read file: " + path, e);
    }
    return result;
  }

  /**
   * Reads patient records from a CSV file and organizes them by blood group.
   *
   * @param path The {@link Path} to the input CSV file.
   * @return A {@link VisitsForBG} object containing the parsed data.
   * @throws IllegalStateException if the file cannot be read.
   */
  static VisitsForBG readBGVisits(Path path) {
    VisitsForBG result = new VisitsForBG();
    try {
      List<String> visitsAsText = Files.readAllLines(path);
      visitsAsText.stream()
              .skip(1) // Skip header row.
              .map(IOUtils::convertLineToVisit)
              .forEach(result::addVisit);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read file: " + path, e);
    }
    return result;
  }

  /**
   * Reads patient records from a CSV file and organizes them by medical condition.
   *
   * @param path The {@link Path} to the input CSV file.
   * @return A {@link VisitsForCT} object containing the parsed data.
   * @throws IllegalStateException if the file cannot be read.
   */
  static VisitsForCT readCTVisits(Path path) {
    VisitsForCT result = new VisitsForCT();
    try {
      List<String> visitsAsText = Files.readAllLines(path);
      visitsAsText.stream()
              .skip(1) // Skip header row.
              .map(IOUtils::convertLineToVisit)
              .forEach(result::addVisit);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read file: " + path, e);
    }
    return result;
  }

  /**
   * Reads patient records from a CSV file and organizes them by age group.
   *
   * @param path The {@link Path} to the input CSV file.
   * @return A {@link VisitsForAG} object containing the parsed data.
   * @throws IllegalStateException if the file cannot be read.
   */
  static VisitsForAG readAGVisits(Path path) {
    VisitsForAG result = new VisitsForAG();
    try {
      List<String> visitsAsText = Files.readAllLines(path);
      visitsAsText.stream()
              .skip(1) // Skip header row.
              .map(IOUtils::convertLineToVisit)
              .forEach(result::addVisit);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read file: " + path, e);
    }
    return result;
  }

  /**
   * Writes a map of medical conditions and their mean billing amounts to a CSV file.
   * The output format is "Condition,MeanBilling".
   *
   * @param means A map where the key is the condition and the value is the mean billing amount.
   * @param file  The path to the output CSV file.
   */
  static void writeMeansBillingPerCT(Map<String, Double> means, String file) {
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Condition,MeanBilling\n"); // Write header.
      // Format the double to two decimal places for cleaner output.
      String format = "%s,%.2f\n";
      means.forEach((condition, mean) -> pw.write(String.format(format, condition, mean)));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write to file: " + file, e);
    }
  }

  /**
   * Writes a map of years and their mean billing amounts to a CSV file.
   * The output format is "Year,MeanBilling".
   *
   * @param means A map where the key is the year and the value is the mean billing amount.
   * @param file  The path to the output CSV file.
   */
  static void writeMeansBillingPerYear(Map<Year, Double> means, String file) {
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Year,MeanBilling\n"); // Write header.
      // Format the double to two decimal places.
      String format = "%d,%.2f\n";
      means.forEach((year, mean) -> pw.write(String.format(format, year.getValue(), mean)));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write to file: " + file, e);
    }
  }

  /**
   * Writes a map of age groups and their mean billing amounts to a CSV file.
   * The output format is "Age Group,MeanBilling".
   *
   * @param means A map where the key is the age group and the value is the mean billing amount.
   * @param file  The path to the output CSV file.
   */
  static void writeMeansBillingPerAG(Map<String, Double> means, String file) {
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Age Group,MeanBilling\n"); // Write header.
      String format = "%s,%.2f\n";
      means.forEach((ageGroup, mean) -> pw.write(String.format(format, ageGroup, mean)));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write to file: " + file, e);
    }
  }

  /**
   * Writes a map of medical conditions and their mean patient ages to a CSV file.
   * The output format is "Condition,MeanAge".
   *
   * @param means A map where the key is the condition and the value is the mean age.
   * @param file  The path to the output CSV file.
   */
  static void writeMeansAgePerCT(Map<String, Double> means, String file) {
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Condition,MeanAge\n"); // Write header.
      String format = "%s,%.2f\n";
      // The key represents the medical condition.
      means.forEach((condition, mean) -> pw.write(String.format(format, condition, mean)));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write to file: " + file, e);
    }
  }

  /**
   * Writes a map of years and their total billing amounts to a CSV file.
   * The output format is "Year,SumBilling".
   *
   * @param sums A map where the key is the year and the value is the total billing amount.
   * @param file The path to the output CSV file.
   */
  static void writeSumsBillingPerYear(Map<Year, Integer> sums, String file) {
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Year,SumBilling\n"); // Write header.
      String format = "%d,%d\n";
      sums.forEach((year, sum) -> pw.write(String.format(format, year.getValue(), sum)));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write to file: " + file, e);
    }
  }

  /**
   * Writes a map of blood groups and their total billing amounts to a CSV file.
   * The output format is "Blood Group,SumBilling".
   *
   * @param sums A map where the key is the blood group and the value is the total billing amount.
   * @param file The path to the output CSV file.
   */
  static void writeSumsBillingPerBG(Map<String, Integer> sums, String file) {
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Blood Group,SumBilling\n"); // Write header.
      String format = "%s,%d\n";
      sums.forEach((bloodGroup, sum) -> pw.write(String.format(format, bloodGroup, sum)));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write to file: " + file, e);
    }
  }

  /**
   * Writes a map of age groups and their total billing amounts to a CSV file.
   * The output format is "Age Group,SumBilling".
   *
   * @param sums A map where the key is the age group and the value is the total billing amount.
   * @param file The path to the output CSV file.
   */
  static void writeSumsBillingPerAG(Map<String, Double> sums, String file) {
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Age Group,SumBilling\n"); // Write header.
      String format = "%s,%.2f\n";
      sums.forEach((ageGroup, sum) -> pw.write(String.format(format, ageGroup, sum)));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write to file: " + file, e);
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

  static void writeCountPerYear(Map<Year, Integer> counts, String file){
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Year,Count\n"); // Write header
      String format = "%d,%d\n";
      counts.forEach(
              (year, count) -> pw.write(String.format(format, year.getValue(), count)));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  static void writeCountsPerConditionType(Map<String, Integer> counts, String file) {
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Condition,Count\n"); // Write header
      String format = "%s,%d\n";
      counts.forEach(
              (conditionType, count) -> pw.write(String.format(format, conditionType, count)));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  static void writeCountsPerBloodType(Map<String, Integer> counts, String file){
    try (PrintWriter pw = new PrintWriter(new File(file), UTF_8.name())) {
      pw.write("Blood Group,Counts\n"); // Write header
      String format = "%s,%d\n";
      counts.forEach(
              (BG, count) -> pw.write(String.format(format, BG, count)));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}