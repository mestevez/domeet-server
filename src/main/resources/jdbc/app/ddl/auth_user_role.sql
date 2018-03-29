CREATE TABLE domeet.auth_user_role (
  user_id         INTEGER NOT NULL,
  role_id         INTEGER NOT NULL
);
ALTER TABLE domeet.auth_user_role ADD CONSTRAINT p_auth_user_role_pk PRIMARY KEY (user_id, role_id);
ALTER TABLE domeet.auth_user_role ADD CONSTRAINT f_auth_user_role_user FOREIGN KEY (user_id) REFERENCES domeet.auth_user(user_id);
ALTER TABLE domeet.auth_user_role ADD CONSTRAINT f_auth_user_role_role FOREIGN KEY (role_id) REFERENCES domeet.auth_role(role_id);