CREATE TABLE app.user (
  user_id         INTEGER NOT NULL ,
  user_firstname  VARCHAR(80) NOT NULL,
  user_lastname   VARCHAR(160),
  user_company    VARCHAR(160),
  user_phone      VARCHAR(50),
  user_photo      BYTEA,
  user_schedule   CHAR(10)
);
ALTER TABLE app.user ADD CONSTRAINT pk_user PRIMARY KEY (user_id);
ALTER TABLE app.user ADD CONSTRAINT f_user_auth_user FOREIGN KEY (user_id) REFERENCES app.auth_user(user_id) ON DELETE CASCADE;
ALTER TABLE app.user ADD CONSTRAINT f_user_schedule FOREIGN KEY (user_schedule) REFERENCES app.schedule(sch_code);