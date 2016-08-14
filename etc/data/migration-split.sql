-- counties
CREATE TABLE counties (
  county_number INT NOT NULL,
  county_name VARCHAR(50),
  district VARCHAR(3) NOT NULL,
  PRIMARY KEY (county_number, district)
);

-- restaurants
CREATE TABLE restaurants(
  county_number INT NOT NULL,
  license_type_code VARCHAR(5) NOT NULL,
  license_number INT NOT NULL,
  business_name VARCHAR(200) NOT NULL,
  location_address VARCHAR(200),
  location_city VARCHAR(200),
  location_zipcode VARCHAR(10),
  location_latitude DECIMAL(10,7),
  location_longitude DECIMAL(10,7),
  critical_violations_before_2013 INT,
  noncritical_violations_before_2013 INT,
  PRIMARY KEY (county_number, license_number)
);

-- inspections
CREATE TABLE inspections(
  inspection_visit_id VARCHAR(7) PRIMARY KEY,
  license_id varchar(10),
  inspection_number INT,
  visit_number INT,
  inspection_class VARCHAR(20),
  inspection_type VARCHAR(200),
  inspection_disposition VARCHAR(200),
  inspection_date DATE,
  total_violations INT,
  high_priority_violations INT,
  intermediate_violations INT,
  basic_violations INT,
  pda_status BOOLEAN
);

-- violations
CREATE TABLE violations(
  id INT PRIMARY KEY,
  description VARCHAR(255)
);

-- inspections_violations
CREATE TABLE inspections_violations(
  inspection_id INT NOT NULL,
  violation_id INT NOT NULL,
  violation_count INT NOT NULL,
  PRIMARY KEY(inspection_id, violation_id)
);
