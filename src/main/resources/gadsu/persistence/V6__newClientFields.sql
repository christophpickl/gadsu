
-- deployed with gadsu version 1.9

-- #78 new client fields: mainObjective, symptoms, (5) elements, syndrom
-- ========================================================================= --

-- hauptanliegen == mainObjective
ALTER TABLE client ADD COLUMN mainObjective VARCHAR(5120) DEFAULT '' NOT NULL;

-- beschwerden/symptome == symptoms
ALTER TABLE client ADD COLUMN symptoms VARCHAR(5120) DEFAULT '' NOT NULL;

-- 5E einschaetzung == elements
ALTER TABLE client ADD COLUMN elements VARCHAR(5120) DEFAULT '' NOT NULL;

-- TCM patho syndrom == syndrom
ALTER TABLE client ADD COLUMN syndrom VARCHAR(5120) DEFAULT '' NOT NULL;


-- #71 TCM xprops revised
-- ========================================================================= --

ALTER TABLE xprops ADD COLUMN note VARCHAR(5120);
