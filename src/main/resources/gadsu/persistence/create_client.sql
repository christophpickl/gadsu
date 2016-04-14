
CREATE TABLE IF NOT EXISTS client (
    id CHAR(36) NOT NULL PRIMARY KEY,
    firstName VARCHAR(100) NOT NULL,
    lastName VARCHAR(100) NOT NULL,
    created TIMESTAMP NOT NULL
);

DROP PROCEDURE IF EXISTS insert_client;
CREATE PROCEDURE insert_client(id CHAR(36), firstName VARCHAR(100), lastName VARCHAR(100), created TIMESTAMP)
    MODIFIES SQL DATA
    INSERT INTO client VALUES (id, firstName, lastName, created);

--CREATE PROCEDURE sp_get_address_by_id(IN address_id INT, OUT address VARCHAR(100), OUT city VARCHAR(25), OUT country VARCHAR(25), OUT postaleCode VARCHAR(10))
--  READS SQL DATA
--  BEGIN ATOMIC
--    SELECT a.address, a.city, a.country, a.postaleCode
--    INTO address, city, country, postaleCode
--    FROM addresses a
--    WHERE a.address_id = address_id;
--  END