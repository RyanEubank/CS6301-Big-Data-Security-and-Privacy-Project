
# Differential Privacy application on Hospital Dataset.

This program is built upon Google's Java Differential Privacy Librar using a Healthcare dataset. 

Install bazel before running the code.
## Sum Billing Per Year for all the patients combined:

Navigate to `examples/java` folder, build the codelab code and run it with the
`SUM_BILLING_PER_YEAR` argument.

```shell
$ cd examples/java
$ bazel build ...
$ bazel-bin/Main SUM_BILLING_PER_YEAR
```

This triggers the logic of CountVisitsPerHour. It reads the yearly statistics and calculates the number of patients that entered the hospital every year. The calculation is done twice.

First, SumBillingPerYear computes the raw counts and outputs them to non_private_counts_per_year.csv.
Next, SumBillingPerYear calculates private (anonymized) counts using the Differential Privacy library and prints them to private_counts_per_year.csv


## Contributions by Patients and Bounds/Clamping the billing amounts for each visit:

```java
/**
* Number of visits contributed by a single patient will be limited to 2. All exceeding
* visits will be discarded. In the dataset though there very minimal duplicate names, so 2 shouuld be good.
  */
  private static final int MAX_CONTRIBUTED_YEARS = 2;
  /** Minimum amount of money we expect a patient billed in a single visit. */
  private static final int MIN_EUROS_SPENT = -2009;
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
```

Please refer to the Restaurant example README file for understanding the importance of Clamping and contribution bounds which influences the noise level added.

