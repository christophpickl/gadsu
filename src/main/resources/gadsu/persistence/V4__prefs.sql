
-- deployed with version 1.7

-- #59 rewrite preferences to use database instead of java prefs api
-- ========================================================================= --

CREATE TABLE prefs (
  data_key VARCHAR(128) NOT NULL PRIMARY KEY,
  data_value VARCHAR(1024) NOT NULL
);
