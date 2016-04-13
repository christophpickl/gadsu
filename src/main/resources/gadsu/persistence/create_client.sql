
CREATE TABLE IF NOT EXISTS client (
    id CHAR(36) NOT NULL PRIMARY KEY,
    firstName VARCHAR(100) NOT NULL,
    lastName VARCHAR(100) NOT NULL,
    created TIMESTAMP NOT NULL
);

CREATE PROCEDURE insert_client(id CHAR(36), firstName VARCHAR(100), lastName VARCHAR(100), created TIMESTAMP)
    MODIFIES SQL DATA
    INSERT INTO client VALUES (id, firstName, lastName, created);
