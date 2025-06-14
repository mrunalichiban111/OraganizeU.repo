-- Drop existing table if it exists
DROP TABLE IF EXISTS events;

-- Create events table with color column
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    color VARCHAR(20) DEFAULT '#8c52ff',
    location VARCHAR(255)
); 