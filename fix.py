import csv
import random
import sys

data = []

hospitals = [
    "Riverside Medical Center", 
    "Starlight General Hospital", 
    "Northvale Health Institute", 
    "Harmony Ridge Hospital", 
    "Oakwood Memorial", 
    "Silverlake Medical Pavilion", 
    "Willowcrest Hospital", 
    "Unity Point Health", 
    "Redbridge Medical Center", 
    "Crystal Bay Hospital", 
    "Goldenview General", 
    "Horizon Heights Hospital", 
    "Bluebell Medical Center", 
    "Pinebrook Regional", 
    "Summit Grove Hospital", 
    "Tranquil Shores Medical", 
    "Evergreen Care Center", 
    "Westhaven Hospital", 
    "Meadowstone Medical", 
    "Avalon General Hospital"
]

header = [
    "Name",
    "Age",
    "Gender",
    "Blood Type",
    "Medical Condition",
    "Date of Admission",
    "Doctor",
    "Hospital",
    "Insurance Provider",
    "Billing Amount",
    "Room Number",
    "Admission Type",
    "Discharge Date",
    "Medication",
    "Test Results"
]

with open(sys.argv[1], 'r', newline='') as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        row['Name'] = row['Name'].title()
        row['Hospital'] = random.choice(hospitals)
        data.append(row)

with open(sys.argv[2], mode='w', newline='') as file:
    writer = csv.DictWriter(file, fieldnames=header)
    writer.writeheader()
    writer.writerows(data)
