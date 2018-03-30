CREATE TABLE app.auth_role (
  role_id           SERIAL,
  role_code         VARCHAR(80)  NOT NULL,
  role_description  VARCHAR(160) NOT NULL
);
ALTER TABLE app.auth_role ADD CONSTRAINT p_auth_role_pk PRIMARY KEY (role_id);
CREATE UNIQUE INDEX u_auth_role_code ON app.auth_role(role_code);