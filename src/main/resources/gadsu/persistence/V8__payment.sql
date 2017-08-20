
-- deployed with gadsu version 1.12

-- #113 add client.donation
-- ========================================================================= --

ALTER TABLE client ADD COLUMN donation VARCHAR(64) DEFAULT 'UNKNOWN' NOT NULL;
