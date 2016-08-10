-- district_counties
CREATE TABLE district_counties (
  county_number INT PRIMARY KEY,
  district VARCHAR(3) NOT NULL,
  county_name VARCHAR(50)
);

-- restaurants
CREATE TABLE restaurants(
  id INT PRIMARY KEY AUTO_INCREMENT,
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
  noncritical_violations_before_2013 INT
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
  level INT,
  description VARCHAR(255)
);

-- inspections_violations
CREATE TABLE inspections_violations(
  id INT PRIMARY KEY AUTO_INCREMENT,
  inspection_id INT NOT NULL,
  violation_id INT NOT NULL
);
