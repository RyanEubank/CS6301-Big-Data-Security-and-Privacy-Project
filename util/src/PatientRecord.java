package util.src;

import java.time.LocalDate;

public class PatientRecord {
    
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
        name = record[0];
        age = Integer.parseInt(record[1]);
        gender = record[2];
        bloodType = record[3];
        condition = record[4];
        admissionDate = LocalDate.parse(record[5]);
        doctor = record[6];
        hospital = record[7];
        provider = record[8];
        bill = Float.parseFloat(record[9]);
        roomNumber = Integer.parseInt(record[10]);
        admissionType = record[11];
        dischargeDate = LocalDate.parse(record[12]);
        medication = record[13];
        testResults = record[14];
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