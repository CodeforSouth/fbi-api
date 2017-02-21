-- Select all restaurants
-- Filter by query params
-- TODO Allow escaping of keys (:county-number)
-- TODO Add district to query (maybe with a join)
-- TODO Add geocapabilities (for Lat & Lon)
SELECT * FROM restaurants as r
WHERE ((length(concat(:zipCodes)) is null or r.location_zipcode IN (:zipCodes))
  AND (:countyNumber is null or r.county_number = :countyNumber))
LIMIT :perPage OFFSET :page;
