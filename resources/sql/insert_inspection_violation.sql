-- Insert a new violation related to a inspection
INSERT IGNORE INTO inspections_violations
  (inspection_id,
  violation_id,
  violation_count)
VALUES (:inspection_id,
  :violation_id,
  :violation_count);
