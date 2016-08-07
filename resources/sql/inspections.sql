-- name: inspection-details
-- Select the Inspection Details for a Given Id
SELECT *
  FROM restaurant_inspections
 WHERE inspection_visit_id = :id;

-- name: inspections-by-county
-- Select inspections by county
SELECT inspection_visit_id, district, county_number, county_name, license_type_code, license_number,
       business_name, inspection_date, location_address, location_city, location_zipcode,
       inspection_number, visit_number, inspection_type, inspection_disposition,
       total_violations, high_priority_violations,
       intermediate_violations, basic_violations
  FROM restaurant_inspections
 WHERE inspection_date BETWEEN :startDate AND :endDate
   AND county_number = :countyNumber;

-- name: inspections-by-district
-- Select inspections by district
SELECT inspection_visit_id, district, county_number, county_name, license_type_code, license_number,
       business_name, inspection_date, location_address, location_city, location_zipcode,
       inspection_number, visit_number, inspection_type, inspection_disposition,
       total_violations, high_priority_violations,
       intermediate_violations, basic_violations
  FROM restaurant_inspections
 WHERE inspection_date BETWEEN :startDate AND :endDate
   AND district = :district;

-- name: inspections-by-business
-- Select inspections by business name
SELECT inspection_visit_id, district, county_number, county_name, license_type_code, license_number,
       business_name, inspection_date, location_address, location_city, location_zipcode,
       inspection_number, visit_number, inspection_type, inspection_disposition,
       total_violations, high_priority_violations,
       intermediate_violations, basic_violations
  FROM restaurant_inspections
 WHERE inspection_date BETWEEN :startDate AND :endDate
   AND business_name LIKE :businessName;

-- name: inspections-by-business-location
-- Select inspections by business name and location
SELECT inspection_visit_id, district, county_number, county_name, license_type_code, license_number,
       business_name, inspection_date, location_address, location_city, location_zipcode,
       inspection_number, visit_number, inspection_type, inspection_disposition,
       total_violations, high_priority_violations,
       intermediate_violations, basic_violations
  FROM restaurant_inspections
 WHERE inspection_date BETWEEN :startDate AND :endDate
   AND business_name LIKE :businessName
   AND location_zipcode IN (:zips);

-- name: inspections-by-zips
-- Select inspections by zips
SELECT inspection_visit_id, district, county_number, county_name, license_type_code, license_number,
       business_name, inspection_date, location_address, location_city, location_zipcode,
       inspection_number, visit_number, inspection_type, inspection_disposition,
       total_violations, high_priority_violations,
       intermediate_violations, basic_violations
  FROM restaurant_inspections
 WHERE inspection_date BETWEEN :startDate AND :endDate
   AND location_zipcode IN (:zips);

-- name: district-counties-summary
-- Counts the number of inspections grouped by Districts and Counties
SELECT district, county_number as countyNumber,
                 county_name as countyName, count(*) as inspections
FROM restaurant_inspections
GROUP BY district, county_number, county_name
ORDER by district, county_name;

-- name: select-existent-ids
-- List existent inspection visit ids
SELECT inspection_visit_id
  FROM restaurant_inspections
 WHERE inspection_visit_id IN (:ids);

-- name: save-inspections!
-- Insert a new inspection
INSERT INTO restaurant_inspections
 (district, county_number, county_name,
  license_type_code, license_number, business_name,
  location_address, location_city, location_zipcode,
  inspection_number, visit_number, inspection_class,
  inspection_type, inspection_disposition, inspection_date,
  critical_violations_before_2013, noncritical_violations_before_2013,
  total_violations, high_priority_violations, intermediate_violations,
  basic_violations, pda_status, violation_01, violation_02, violation_03,
  violation_04, violation_05, violation_06, violation_07, violation_08,
  violation_09, violation_10, violation_11, violation_12, violation_13,
  violation_14, violation_15, violation_16, violation_17, violation_18,
  violation_19, violation_20, violation_21, violation_22, violation_23,
  violation_24, violation_25, violation_26, violation_27, violation_28,
  violation_29, violation_30, violation_31, violation_32, violation_33,
  violation_34, violation_35, violation_36, violation_37, violation_38,
  violation_39, violation_40, violation_41, violation_42, violation_43,
  violation_44, violation_45, violation_46, violation_47, violation_48,
  violation_49, violation_50, violation_51, violation_52, violation_53,
  violation_54, violation_55, violation_56, violation_57, violation_58,
  license_id, inspection_visit_id)
VALUES (:district, :county_number, :county_name,
  :license_type_code, :license_number, :business_name,
  :location_address, :location_city, :location_zipcode,
  :inspection_number, :visit_number, :inspection_class,
  :inspection_type, :inspection_disposition, :inspection_date,
  :critical_violations_before_2013, :noncritical_violations_before_2013,
  :total_violations, :high_priority_violations, :intermediate_violations,
  :basic_violations, :pda_status, :violation_01, :violation_02, :violation_03,
  :violation_04, :violation_05, :violation_06, :violation_07, :violation_08,
  :violation_09, :violation_10, :violation_11, :violation_12, :violation_13,
  :violation_14, :violation_15, :violation_16, :violation_17, :violation_18,
  :violation_19, :violation_20, :violation_21, :violation_22, :violation_23,
  :violation_24, :violation_25, :violation_26, :violation_27, :violation_28,
  :violation_29, :violation_30, :violation_31, :violation_32, :violation_33,
  :violation_34, :violation_35, :violation_36, :violation_37, :violation_38,
  :violation_39, :violation_40, :violation_41, :violation_42, :violation_43,
  :violation_44, :violation_45, :violation_46, :violation_47, :violation_48,
  :violation_49, :violation_50, :violation_51, :violation_52, :violation_53,
  :violation_54, :violation_55, :violation_56, :violation_57, :violation_58,
  :license_id, :inspection_visit_id);
