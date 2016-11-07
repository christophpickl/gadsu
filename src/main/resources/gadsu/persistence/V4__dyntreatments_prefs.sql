
-- deployed with version 1.7

-- #17 dynamic treatments
-- ========================================================================= --

CREATE TABLE hara_diagnosis (
  id_treatment VARCHAR(36) NOT NULL PRIMARY KEY,
  note VARCHAR(5120) NOT NULL,

  FOREIGN KEY (id_treatment) REFERENCES treatment(id)
);
