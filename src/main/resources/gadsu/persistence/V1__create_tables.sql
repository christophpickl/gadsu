
-- CLIENT
-- ================================================================================================================== --
CREATE TABLE client (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  created TIMESTAMP NOT NULL,

  firstName VARCHAR(128) NOT NULL,
  lastName VARCHAR(128) NOT NULL,
  birthday DATE, -- NULLABLE
  gender_enum CHAR(1) NOT NULL, -- eg: '?', 'M', or 'F'
  countryOfOrigin VARCHAR(128) NOT NULL,
  relationship_enum VARCHAR(16) NOT NULL, -- eg: 'SINGLE'
  job VARCHAR(128) NOT NULL,
  children VARCHAR(128) NOT NULL,
  note VARCHAR(1024) NOT NULL,

  -- CONTACT
  mail VARCHAR(128) NOT NULL,
  phone VARCHAR(128) NOT NULL,
  street VARCHAR(128) NOT NULL,
  zipCode VARCHAR(16) NOT NULL,
  city VARCHAR(128) NOT NULL, -- smart enabled

  picture BLOB
);


-- XPROPS
-- ================================================================================================================== --
CREATE TABLE xprops (
  id_client VARCHAR(36) NOT NULL,
  key VARCHAR(128) NOT NULL,
  val VARCHAR(1024) NOT NULL,

  PRIMARY KEY(id_client, key),
  FOREIGN KEY (id_client) REFERENCES client(id)
);


-- TREATMENT
-- ================================================================================================================== --
CREATE TABLE treatment (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  id_client VARCHAR(36) NOT NULL,

  created TIMESTAMP NOT NULL,
  number INT NOT NULL,
  date TIMESTAMP NOT NULL, -- second and millisecond will be cut off by application
  note VARCHAR(1024) NOT NULL,

  FOREIGN KEY (id_client) REFERENCES client(id)
);
