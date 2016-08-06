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

-- name: district-counties-summary
-- Counts the number of inspections grouped by Districts and Counties
SELECT district, county_number as countyNumber,
                 county_name as countyName, count(*) as inspections
FROM restaurant_inspections
GROUP BY district, county_number, county_name
ORDER by district, county_name;