-- Select inspections by county
SELECT i.inspection_visit_id, i.inspection_number,
       i.visit_number, i.inspection_class, i.inspection_type,
       i.inspection_disposition, i.inspection_date, i.total_violations,
       i.high_priority_violations, i.intermediate_violations,
       i.basic_violations, i.county_number, i.license_number,
       r.county_number, r.license_type_code, r.license_number,
       r.business_name, r.location_address, r.location_city, r.location_zipcode,
       r.location_latitude, r.location_longitude,
       c.county_name, c.district
  FROM inspections AS i
 INNER JOIN counties AS c
    ON c.county_number = i.county_number
 INNER JOIN restaurants AS r
    ON r.county_number = i.county_number
   AND r.license_number = i.license_number
 WHERE i.inspection_date BETWEEN :startDate AND :endDate
   AND i.county_number = :countyNumber;