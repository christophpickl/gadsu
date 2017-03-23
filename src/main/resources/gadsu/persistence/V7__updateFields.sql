
-- deployed with gadsu version 1.11

-- #87 Send confirmation mail when importing appointment #87
-- ========================================================================= --

ALTER TABLE client RENAME COLUMN wantReceiveDoodleMails to wantReceiveMails;
