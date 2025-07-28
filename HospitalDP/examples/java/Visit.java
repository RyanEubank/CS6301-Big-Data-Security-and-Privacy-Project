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

import com.google.auto.value.AutoValue;
import java.time.LocalDate;
import java.time.Year;

/** Stores data about single visit of a patient to the hospital. */
@AutoValue
abstract class Visit {

  static Visit create(
          String name, int age, String gender, String bloodType, String medicalCondition, LocalDate dateEntered,String insurance,int billingAmount,String admissionType,LocalDate dateDischarged,String medication,String testResults) {
    return new AutoValue_Visit(name, age, gender, bloodType, medicalCondition, dateEntered,insurance,billingAmount,admissionType,dateDischarged,medication,testResults);
  }

  abstract String name();
  abstract int age();
  abstract String gender();
  abstract String bloodType();
  abstract String medicalCondition();
  abstract LocalDate dateEntered();
  abstract String insurance();
  abstract int billingAmount();
  abstract String admissionType();
  abstract LocalDate dateDischarged();
  abstract String medication();
  abstract String testResults();

  Year year() {
    return Year.from(dateEntered());
  }
}
