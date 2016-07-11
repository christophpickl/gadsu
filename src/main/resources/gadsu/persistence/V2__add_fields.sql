
-- add new fields for client
-- ========================================================================= --
ALTER TABLE client ADD COLUMN hobbies VARCHAR(512) DEFAULT '' NOT NULL;
ALTER TABLE client ADD COLUMN origin  VARCHAR(512) DEFAULT '' NOT NULL;
ALTER TABLE client ADD COLUMN tcmNote VARCHAR(5120) DEFAULT '' NOT NULL;

-- textImpression textMedical textComplaints textPersonal textObjective
ALTER TABLE client ADD COLUMN textImpression VARCHAR(5120) DEFAULT '' NOT NULL;
ALTER TABLE client ADD COLUMN textMedical VARCHAR(5120) DEFAULT '' NOT NULL;
ALTER TABLE client ADD COLUMN textComplaints VARCHAR(5120) DEFAULT '' NOT NULL;
ALTER TABLE client ADD COLUMN textPersonal VARCHAR(5120) DEFAULT '' NOT NULL;
ALTER TABLE client ADD COLUMN textObjective VARCHAR(5120) DEFAULT '' NOT NULL;

-- change varchar sizes
-- ========================================================================= --
ALTER TABLE client ALTER COLUMN firstName VARCHAR(512);
ALTER TABLE client ALTER COLUMN lastName VARCHAR(512);
ALTER TABLE client ALTER COLUMN countryOfOrigin VARCHAR(512);
ALTER TABLE client ALTER COLUMN job VARCHAR(512);
ALTER TABLE client ALTER COLUMN children VARCHAR(512);
ALTER TABLE client ALTER COLUMN note VARCHAR(5120);

ALTER TABLE client ALTER COLUMN mail VARCHAR(512);
ALTER TABLE client ALTER COLUMN phone VARCHAR(512);
ALTER TABLE client ALTER COLUMN street VARCHAR(512);
ALTER TABLE client ALTER COLUMN zipCode VARCHAR(512);
ALTER TABLE client ALTER COLUMN city VARCHAR(512);

ALTER TABLE treatment ALTER COLUMN aboutDiscomfort VARCHAR(5120);
ALTER TABLE treatment ALTER COLUMN aboutDiagnosis VARCHAR(5120);
ALTER TABLE treatment ALTER COLUMN aboutContent VARCHAR(5120);
ALTER TABLE treatment ALTER COLUMN aboutFeedback VARCHAR(5120);
ALTER TABLE treatment ALTER COLUMN aboutHomework VARCHAR(5120);
ALTER TABLE treatment ALTER COLUMN aboutUpcoming VARCHAR(5120);
ALTER TABLE treatment ALTER COLUMN note VARCHAR(5120);

ALTER TABLE appointment ALTER COLUMN note VARCHAR(5120);
