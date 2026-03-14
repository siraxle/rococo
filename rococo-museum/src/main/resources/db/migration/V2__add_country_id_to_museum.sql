ALTER TABLE museum ADD COLUMN country_id BINARY(16);
CREATE INDEX idx_museum_country_id ON museum(country_id);