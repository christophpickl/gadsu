
-- #28 multiprotocol generation
-- ========================================================================= --

CREATE TABLE multiprotocol (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  created TIMESTAMP NOT NULL,
  description VARCHAR(5120) NOT NULL
);

CREATE TABLE multiprotocol2treatment (
  id_multiprotocol VARCHAR(36) NOT NULL,
  id_treatment VARCHAR(36) NOT NULL,

  PRIMARY KEY(id_multiprotocol, id_treatment),
  FOREIGN KEY (id_multiprotocol) REFERENCES multiprotocol(id),
  FOREIGN KEY (id_treatment) REFERENCES treatment(id)
);
