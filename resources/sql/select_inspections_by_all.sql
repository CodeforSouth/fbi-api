-- Select inspections by restaurant name and location
SELECT i.inspection_visit_id, i.inspection_number, i.visit_number,
       i.inspection_class, i.inspection_type, i.inspection_disposition,
       i.inspection_date, i.total_violations, i.high_priority_violations,
       i.intermediate_violations, i.basic_violations,
       i.license_number, r.county_number, r.license_type_code,
       r.business_name, r.location_address, r.location_city, r.location_zipcode,
       c.county_name, c.district
  FROM inspections AS i
 INNER JOIN counties AS c
    ON c.county_number = i.county_number
 INNER JOIN restaurants AS r
    ON r.county_number = i.county_number
   AND r.license_number = i.license_number
 WHERE i.inspection_date BETWEEN :startDate AND :endDate
   AND (:businessName is null or r.business_name LIKE :businessName)
   AND (length(concat(:zipCodes)) is null or r.location_zipcode IN (:zipCodes))
   AND (:districtCode is null or c.district = :districtCode)
   AND (:countyNumber is null or i.county_number = :countyNumber)
 LIMIT :perPage OFFSET :page;
