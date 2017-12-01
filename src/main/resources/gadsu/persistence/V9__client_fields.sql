
-- deployed with gadsu version 1.13

-- #119 rework client name fields
-- ========================================================================= --

ALTER TABLE client ALTER COLUMN nickname RENAME TO nicknameInt;
ALTER TABLE client ADD COLUMN nicknameExt VARCHAR(512);
ALTER TABLE client ADD COLUMN knownBy VARCHAR(512);
ALTER TABLE client ADD COLUMN yyTendency VARCHAR(16);
