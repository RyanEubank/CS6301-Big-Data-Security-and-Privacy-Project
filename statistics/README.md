# Statistics-Module
This project module implements a simple tool to read/parse a csv file with the following columns:

* Name - string
* Age - integer
* Gender - string
* Blood Type - string
* Medical Condition - string
* Date of Admission - mm/dd/yyyy
* Doctor - string
* Hospital - string
* Insurance Provider - string
* Billing Amount - float
* Room Number - integer
* Admission Type - string
* Discharge Date - mm/dd/yyyy
* Medication - string
* Test Results - string

The data is stored in a list of PatientRecord objects, and the program calculates aggregate 
statistics and prints them to the console. Use this tool to gather values that can then be
passed into the inference tool as an example to show how datasets can be reconstructed from
the unaltered statistics.

## Run the program
Use the following command to run the statistics tool
```
./run.bat --statistics <record-limit> <path-to-csv>
```
The first argument given is the limit of the number of records to read, and the second argument
is the path to the file.