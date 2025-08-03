
# Differential Privacy Analytics application on a Healthcare Dataset.

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
## Basic Concepts:

### Differential Privacy:

Differential privacy (DP) is a mathematically rigorous framework for releasing statistical information about datasets while protecting the privacy of individual data subjects. 
This is done by injecting carefully calibrated noise into statistical computations such that the utility of the statistic is preserved while provably limiting what can be inferred about any individual in the dataset.


### Sensitivity:
Sensitivity measures the maximum possible change to the output (the sum) if you add or remove the data of a single individual. The amount of noise needed is directly proportional to this sensitivity.
It is measured in different types: L0, L1, L2 and LInf and their application is abstracted out through the DP library.

### Laplace Mechanism and Noise addition:
The amount of Noise added to a data sample is mathematically determined through a Laplace Distribution for the evaluated data and controlled by the noise parameter epsilon (ϵ) for the distribution.
With Lower ϵ(wider distribution hence more noise) meaning higher privacy and less utility, while higher ϵ implies lower privacy and high utility. 
The trade-off needs to be determined based on use-case. 

In this study, we evaluated with noise parameters ϵ values as log(1.05), log(1.1), log(1.5).

### Contribution Partitions, Clamping and Bounds:

A given patient can visit a hospital multiple times in a given year or across years or have multiple conditions based admits and hence might contribute to multiple entries in the dataset. 
Since the level of noise added is also dependent on the number of contributions by each patient to a partition or across partitions, 
where multiple entries will add more noise to the output, we limit the number of possible entries by a patient id to 2(which can be determined based on the dataset in question) for this use-case.

For Sum and Mean Statistics, the billing amounts are clamped with lower and upper bounds to avoid domination by any one or few exceptional extremes in the dataset and ensure proportional noise addition.

For more details on application of bounds, clamping and partitions, please refer to the Restaurant visits example Readme and Google Differential Privacy Library: 
https://github.com/google/differential-privacy/blob/main/examples/java/README.md


### Privacy Budget(ϵ):

This is the total noise budget (ϵ) that can be distributed across different partitions in the analysis. 
For instance, for calculating Mean statistic, the noise ϵ is distributed as ϵ1 for the numerator i.e Sum Total and ϵ2 for the denominatior i.e the count.
Where ϵ1+ϵ2 = ϵ, this distribution is handled by the DP library in the background.

### Assumptions:
We assume each unique patient admitted to a hospital is identified by assigning a unique id based on their biodata such as age, gender, name, blood group, with no two patients having the same bio-data.
This ensures unique identification of the records and helps in determining the bounds and contributions range by each patient.

## SUM Statistics:

DP Library provides methods for the calculation of Bounded Sum for the desired field in the given dataset. 
We chose to calculate the private sum of billing amount based on the Year/Condition/Gender.

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

