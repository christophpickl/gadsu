
-- deployed with gadsu version 1.11

-- #87 Send confirmation mail when importing appointment #87
-- ========================================================================= --

ALTER TABLE client ALTER COLUMN wantReceiveDoodleMails RENAME TO wantReceiveMails;


-- #106 ABC clients
-- ========================================================================= --

ALTER TABLE client ADD COLUMN category CHAR(1) DEFAULT 'B' NOT NULL;
