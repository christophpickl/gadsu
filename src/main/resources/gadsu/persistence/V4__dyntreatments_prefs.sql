
-- deployed with version 1.7

-- #17 dynamic treatments
-- ========================================================================= --

CREATE TABLE hara_diagnosis (
  id_treatment VARCHAR(36) NOT NULL PRIMARY KEY,
  note VARCHAR(5120) NOT NULL,

  FOREIGN KEY (id_treatment) REFERENCES treatment(id)
);

CREATE TABLE hara_diagnosis_kyo (
  id_treatment VARCHAR(36) NOT NULL PRIMARY KEY,
  meridian CHAR(2) NOT NULL,

  FOREIGN KEY (id_treatment) REFERENCES treatment(id)
);

CREATE TABLE hara_diagnosis_jitsu (
  id_treatment VARCHAR(36) NOT NULL PRIMARY KEY,
  meridian CHAR(2) NOT NULL,

  FOREIGN KEY (id_treatment) REFERENCES treatment(id)
);


-- #59 rewrite preferences to use database instead of java prefs api
-- ========================================================================= --

CREATE TABLE prefs (
  data_key VARCHAR(128) NOT NULL PRIMARY KEY,
  data_value VARCHAR(1024) NOT NULL
);
