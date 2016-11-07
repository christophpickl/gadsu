
-- deployed with version 1.7

-- #59 rewrite preferences to use database instead java prefs api
-- ========================================================================= --

CREATE TABLE prefs (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  data_key VARCHAR(128) NOT NULL,
  data_value VARCHAR(1024) NOT NULL
);
