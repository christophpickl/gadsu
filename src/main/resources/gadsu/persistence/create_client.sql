
CREATE TABLE IF NOT EXISTS client (
  id CHAR(36) NOT NULL PRIMARY KEY,
  firstName VARCHAR(100) NOT NULL,
  lastName VARCHAR(100) NOT NULL,
  created TIMESTAMP NOT NULL
);
