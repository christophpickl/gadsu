
-- deployed with gadsu version 1.13

-- #119 rework client name fields
-- ========================================================================= --

ALTER TABLE client ALTER COLUMN nickName RENAME TO nickNameInt;
ALTER TABLE client ADD COLUMN nickNameExt VARCHAR(512) DEFAULT '' NOT NULL;
ALTER TABLE client ADD COLUMN knownBy VARCHAR(512) DEFAULT '' NOT NULL;
ALTER TABLE client ADD COLUMN yyTendency VARCHAR(16) DEFAULT '?' NOT NULL;
ALTER TABLE client ADD COLUMN elementTendency VARCHAR(16) DEFAULT '?' NOT NULL;
