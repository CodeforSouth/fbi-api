-- Select inspections by all
SELECT i.inspection_visit_id, i.inspection_number,
i.visit_number, i.inspection_class, i.inspection_type,
i.inspection_disposition, i.inspection_date, i.total_violations,
i.high_priority_violations, i.intermediate_violations,
i.basic_violations, c.county_number,
r.county_number, r.license_type_code, r.license_number,
r.business_name, r.location_address, r.location_city, r.location_zipcode,
r.location_latitude, r.location_longitude,
c.county_name, c.district
FROM inspections AS i
INNER JOIN counties AS c
INNER JOIN restaurants AS r
ON r.county_number = c.county_number
WHERE i.inspection_date BETWEEN :startDate AND :endDate
AND r.business_name LIKE :businessName
AND r.location_zipcode IN (:zipCodes)
AND c.county_number = :countyNumber
AND c.district = :district
LIMIT 10;
