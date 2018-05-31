
-- deployed with gadsu version 1.14

-- #134 add DSGVO checkbox
-- ========================================================================= --

ALTER TABLE client ADD COLUMN dsgvoAccepted BOOLEAN DEFAULT FALSE NOT NULL;
UPDATE client SET wantReceiveMails = FALSE;
