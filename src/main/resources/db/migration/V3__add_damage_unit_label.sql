ALTER TABLE rental_damage_report
ADD COLUMN IF NOT EXISTS damaged_unit_label VARCHAR(80) NULL;
