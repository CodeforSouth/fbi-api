-- Insert a new county
INSERT IGNORE INTO counties
  (county_number,
   county_name,
   district)
VALUES (:county_number,
        :county_name,
        :district);
