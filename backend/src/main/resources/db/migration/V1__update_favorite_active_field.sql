-- Add a default value constraint to the active column
ALTER TABLE favorites ALTER COLUMN active SET DEFAULT false;

-- Update any existing rows where active might be null
UPDATE favorites SET active = false WHERE active IS NULL; 