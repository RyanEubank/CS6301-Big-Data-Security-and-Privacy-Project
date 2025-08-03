package util.src;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class PatientRecord {

    public int id;
    public String name;
    public int age;
    public String gender;
    public String bloodType;
    public String condition;
    public LocalDate admissionDate;
    public String doctor;
    public String hospital;
    public String provider;
    public float bill;
    public int roomNumber;
    public String admissionType;
    public LocalDate dischargeDate;
    public String medication;
    public String testResults;

    public PatientRecord() {}

    public PatientRecord(String[] record) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");

        id = Integer.parseInt(record[0]);
        name = record[1];
        age = Integer.parseInt(record[2]);
        gender = record[3];
        bloodType = record[4];
        condition = record[5];
        admissionDate = LocalDate.parse(record[6], formatter);
        doctor = record[7];
        hospital = record[8];
        provider = record[9];
        bill = Float.parseFloat(record[10]);
        roomNumber = Integer.parseInt(record[11]);
        admissionType = record[12];
        dischargeDate = LocalDate.parse(record[13], formatter);
        medication = record[14];
        testResults = record[15];
    }



    public Year yearAdmitted() {
        return Year.from(admissionDate);
    }

    public String toString() {
        return summary();
    }

    public String summary() {
        return "[Age: " + age + 
               ", Gender: " + gender + 
               ", Blood Type: " + bloodType + 
               ", Condition: " + condition + "]";
    }

    public String fullRecord() {
        return "* ================================== *" +
                "            PATIENT RECORD            " +
                "* ================================== *" +
                "Id: " + id + "\n" +
                "Name: " + name + "\n" +
                "Age: " + age + "\n" +
                "Gender: " + gender + "\n" +
                "Blood Type: " + bloodType + "\n" +
                "Condition: " + condition + "\n" +
                "Admission Date: " + admissionDate + "\n" +
                "Doctor: " + doctor + "\n" +
                "Hospital: " + hospital + "\n" +
                "Provider: " + provider + "\n" +
                "Bill: " + bill + "\n" +
                "Room Number: " + roomNumber + "\n" +
                "Admission Type: " + admissionType + "\n" +
                "Discharge Date: " + dischargeDate + "\n" +
                "Medication: " + medication + "\n" +
                "Test Results: " + testResults;
    }
}