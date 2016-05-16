
-- APPOINTMENT
-- ================================================================================================================== --
CREATE TABLE appointment (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  id_client VARCHAR(36) NOT NULL,
  created TIMESTAMP NOT NULL,

  startDate TIMESTAMP NOT NULL,
  endDate TIMESTAMP NOT NULL,

  FOREIGN KEY (id_client) REFERENCES client(id)
);
