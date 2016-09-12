-- Insert a new restaurant
INSERT IGNORE INTO restaurants
  (county_number,
  license_type_code,
  license_number,
  business_name,
  location_address,
  location_city,
  location_zipcode,
  critical_violations_before_2013,
  noncritical_violations_before_2013)
VALUES (:county_number,
  :license_type_code,
  :license_number,
  :business_name,
  :location_address,
  :location_city,
  :location_zipcode,
  :critical_violations_before_2013,
  :noncritical_violations_before_2013);
