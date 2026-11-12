-- Version 4: the people who may use the API. We store a password HASH (a one-way scramble made by BCrypt), never the password itself.
-- 60 characters is what BCrypt produces; 100 leaves room for a stronger scheme later.
CREATE TABLE app_user (
    username      VARCHAR(50)  NOT NULL PRIMARY KEY,
    password_hash VARCHAR(100) NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE
);
