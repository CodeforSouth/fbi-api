-- Select Violations for a Given Inspection Id
SELECT iv.inspection_id, iv.violation_id, iv.violation_count,
       v.description, v.is_risk_factor, v.is_primary_concern
  FROM inspections_violations AS iv
  INNER JOIN violations AS v
    ON v.id = iv.violation_id
WHERE iv.inspection_id = :id;