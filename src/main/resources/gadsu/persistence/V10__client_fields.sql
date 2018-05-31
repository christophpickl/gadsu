
-- deployed with gadsu version 1.14

-- #133 yin yang and 5E input fields
-- ========================================================================= --

ALTER TABLE client ADD COLUMN textYinYang VARCHAR(5120) DEFAULT '' NOT NULL;
ALTER TABLE client ADD COLUMN textWood VARCHAR(5120) DEFAULT '' NOT NULL;
ALTER TABLE client ADD COLUMN textFire VARCHAR(5120) DEFAULT '' NOT NULL;
ALTER TABLE client ADD COLUMN textEarth VARCHAR(5120) DEFAULT '' NOT NULL;
ALTER TABLE client ADD COLUMN textMetal VARCHAR(5120) DEFAULT '' NOT NULL;
ALTER TABLE client ADD COLUMN textWater VARCHAR(5120) DEFAULT '' NOT NULL;

-- #134 add DSGVO checkbox
-- ========================================================================= --

ALTER TABLE client ADD COLUMN dsgvoAccepted BOOLEAN DEFAULT FALSE NOT NULL;
UPDATE client SET wantReceiveMails = FALSE;
