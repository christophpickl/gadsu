
-- deployed with version 1.7

-- #17 dynamic treatments
-- ========================================================================= --

CREATE TABLE hara_diagnosis (
  id_treatment VARCHAR(36) NOT NULL PRIMARY KEY,
  -- best connection of meridians, might be null
  connection1Meridian CHAR(2),
  connection1Position CHAR(2),
  connection2Meridian CHAR(2),
  connection2Position CHAR(2),
  note VARCHAR(5120) NOT NULL,

  FOREIGN KEY (id_treatment) REFERENCES treatment(id)
);

CREATE TABLE hara_diagnosis_kyo (
  id_treatment VARCHAR(36) NOT NULL,
  meridian CHAR(2) NOT NULL,
  position CHAR(2) NOT NULL,

  FOREIGN KEY (id_treatment) REFERENCES treatment(id)
);

CREATE TABLE hara_diagnosis_jitsu (
  id_treatment VARCHAR(36) NOT NULL,
  meridian CHAR(2) NOT NULL,
  position CHAR(2) NOT NULL,

  FOREIGN KEY (id_treatment) REFERENCES treatment(id)
);

CREATE TABLE tongue_diagnosis (
  id_treatment VARCHAR(36) NOT NULL PRIMARY KEY,
  note VARCHAR(5120) NOT NULL,

  FOREIGN KEY (id_treatment) REFERENCES treatment(id)
);

CREATE TABLE tongue_diagnosis_properties (
  id_treatment VARCHAR(36) NOT NULL,
  sql_code VARCHAR(128) NOT NULL,

  PRIMARY KEY (id_treatment, sql_code),
  FOREIGN KEY (id_treatment) REFERENCES treatment(id)
);

CREATE TABLE blood_pressure (
  id_treatment VARCHAR(36) NOT NULL PRIMARY KEY,
  before_systolic INT NOT NULL,
  before_diastolic INT NOT NULL,
  before_frequency INT NOT NULL,
  after_systolic INT NOT NULL,
  after_diastolic INT NOT NULL,
  after_frequency INT NOT NULL,

  FOREIGN KEY (id_treatment) REFERENCES treatment(id)
);

-- #59 rewrite preferences to use database instead of java prefs api
-- ========================================================================= --

CREATE TABLE prefs (
  data_key VARCHAR(512) NOT NULL PRIMARY KEY,
  data_value VARCHAR(5120) NOT NULL
);

-- #58 send bulk mails (for doodle polls)
-- ========================================================================= --

ALTER TABLE client ADD COLUMN wantReceiveDoodleMails BOOLEAN DEFAULT TRUE NOT NULL;
