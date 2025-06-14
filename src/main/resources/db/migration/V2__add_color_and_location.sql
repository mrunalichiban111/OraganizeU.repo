-- Add color and location columns to events table
ALTER TABLE events
ADD COLUMN color VARCHAR(7) DEFAULT '#8c52ff',
ADD COLUMN location VARCHAR(255); 