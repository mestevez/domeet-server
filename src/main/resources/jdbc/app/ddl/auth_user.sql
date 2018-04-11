CREATE TABLE app.auth_user (
  user_id         SERIAL,
  user_mail       VARCHAR(254) NOT NULL,    -- Email size: RFC3696, Errata ID 1690.
  user_passhash   VARCHAR(32) NOT NULL      -- Length of MD5 encryption
);
ALTER TABLE app.auth_user ADD CONSTRAINT pk_auth_user PRIMARY KEY (user_id);
CREATE UNIQUE INDEX u_auth_user_mail ON app.auth_user(user_mail);