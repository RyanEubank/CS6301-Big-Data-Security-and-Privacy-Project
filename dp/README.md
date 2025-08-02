
# Differential Privacy application on Hospital Dataset.

This program is built upon Google's Java Differential Privacy Library using a Healthcare dataset sourced from Kaggle. The csv source file is titled patient_records.csv

After navigating to the directory of the project in shell, run the following:

For Windows:
```shell
$ ./run.bat --dp ./patient_records.csv
```

For macOS:
```shell
$ javac util/src/*.java -d util/build
$ javac -cp util/build:dp/lib/* dp/src/*.java -d dp/build
$ java -cp "dp/build:util/build:dp/lib/*" dp.src.Main "patient_records.csv"
```

## SUM Statistics:

DP Library provides methods for the calculation of Bounded Sum of any field in the given dataset. We chose to calculate the sum of billing amount based on the Year/Condition/Gender.

### SumBillingPerYear:

This triggers the logic of SumBillingPerYear. It reads the yearly statistics and calculates the total billing amount for patients that entered the hospital every year. The calculation is done twice.

First, SumBillingPerYear computes the raw counts and outputs them to non_private_counts_per_year.csv.
Next, SumBillingPerYear calculates private (anonymized) counts using the Differential Privacy library and prints them to private_counts_per_year.csv


## Contributions by Patients and Bounds/Clamping the billing amounts for each visit:


```java
 
 //The scale of noise added, epsilon is set as the natural logarithm value constant below.
 
 private static final double LN_X = Math.log(1.1);

  /**
   * Number of visits contributed by a single patient will be limited to 2. All exceeding
   * visits will be discarded. In the dataset though there very minimal duplicate names, so 2 shouuld be good.
   */
  //How many different Years can a patient name contribute to overall.
  private static final int MAX_CONTRIBUTED_YEARS = 2;
  /** Minimum amount of money we expect a patient billed in a single visit. */
  private static final int MIN_EUROS_SPENT = 0;
  /** Maximum amount of money we expect a patient billed in a single visit. */
  private static final int MAX_EUROS_SPENT = 50000;

  
  
  
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
              .epsilon(LN_X)
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

      for (PatientRecord r : boundedVisits.getVisitsForYear(y)) {
        dpSum.addEntry(r.bill);
      }
```

Please refer to the Restaurant example README file for understanding the importance of Clamping and contribution bounds which influences the noise level added.

## MEAN Statistics:

