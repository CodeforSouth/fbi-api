-- Counts the number of inspections grouped by Districts and Counties
SELECT c.district, i.county_number,
      c.county_name, count(i.inspection_visit_id) as inspections
 FROM inspections as i
INNER JOIN counties as c
   ON c.county_number = i.county_number
GROUP BY c.district, i.county_number, c.county_name
ORDER by c.district, c.county_name;
