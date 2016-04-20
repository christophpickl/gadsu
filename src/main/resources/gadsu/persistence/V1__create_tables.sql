CREATE TABLE client (
  id VARCHAR(36) NOT NULL PRIMARY KEY,

  firstName VARCHAR(100) NOT NULL,
  lastName VARCHAR(100) NOT NULL,
  created TIMESTAMP NOT NULL,
  birthday DATE, -- NULLABLE
  gender_enum CHAR(1) NOT NULL, -- '?', 'M', or 'F'
  countryOfOrigin VARCHAR(100) NOT NULL, -- smart enabled

  mail VARCHAR(100) NOT NULL,
  phone VARCHAR(100) NOT NULL,
  street VARCHAR(100) NOT NULL,
  zipCode VARCHAR(10) NOT NULL,
  city VARCHAR(10) NOT NULL, -- smart enabled

  relationship_enum VARCHAR(10) NOT NULL,
  job VARCHAR(100) NOT NULL,
  children VARCHAR(100) NOT NULL,

  note VARCHAR(1000) NOT NULL,

  picture BLOB
);

CREATE TABLE treatment (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  id_client VARCHAR(36) NOT NULL,

  created TIMESTAMP NOT NULL,
  number INT NOT NULL,
  date TIMESTAMP NOT NULL, -- second and millisecond will be cut off by application
  note VARCHAR(1000) NOT NULL,

  FOREIGN KEY (id_client) REFERENCES client(id)
);
