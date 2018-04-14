CREATE TABLE app.subject (
  subject_id          SERIAL,
  meet_id             INTEGER NOT NULL,
  subject_order       INTEGER NOT NULL DEFAULT 0,
  subject_title       VARCHAR(100) NOT NULL,
  subject_duration    SMALLINT NOT NULL DEFAULT 0,
  subject_priority    SMALLINT NOT NULL DEFAULT 1,     -- 0: Irrelevant, 1: Normal, 2: Essential
  subject_time_start  TIMESTAMP,
  subject_time_end    TIMESTAMP
);
CREATE UNIQUE INDEX u_subject ON app.subject(subject_id, subject_order);
ALTER TABLE app.subject ADD CONSTRAINT pk_subject PRIMARY KEY (subject_id);
ALTER TABLE app.subject ADD CONSTRAINT f_subject_meeting FOREIGN KEY (meet_id) REFERENCES app.meeting(meet_id);
ALTER TABLE app.subject ADD CONSTRAINT c_subject_info_end CHECK (
  subject_time_end IS NULL OR subject_time_start IS NOT NULL
);