
-- deployed with gadsu version 1.8

-- #57 meridian selector
-- ========================================================================= --

CREATE TABLE treatment_meridian (
  id_treatment VARCHAR(36) NOT NULL,
  meridian CHAR(2) NOT NULL,

  PRIMARY KEY(id_treatment, meridian),
  FOREIGN KEY (id_treatment) REFERENCES treatment(id)
);
