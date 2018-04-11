CREATE TABLE app.attend (
  user_id         INTEGER NOT NULL,
  meet_id         INTEGER NOT NULL,
  attd_status     SMALLINT NOT NULL DEFAULT 0,    -- 0: Pending, 1: Notified, 2: Rejected, 3: Confirmed, 4: Attend
  attd_time_join  TIMESTAMP,
  attd_time_left  TIMESTAMP,
  attd_rate       SMALLINT,
  attd_comment    TEXT
);
ALTER TABLE app.attend ADD CONSTRAINT pk_attend PRIMARY KEY (user_id, meet_id);
ALTER TABLE app.attend ADD CONSTRAINT f_attend_user FOREIGN KEY (user_id) REFERENCES app.user(user_id);
ALTER TABLE app.attend ADD CONSTRAINT f_attend_meeting FOREIGN KEY (meet_id) REFERENCES app.meeting(meet_id);
ALTER TABLE app.attend ADD CONSTRAINT c_attend_status_range CHECK (
  attd_status IN (0, 1, 2, 3, 4)
);
ALTER TABLE app.attend ADD CONSTRAINT c_attend_rate_range CHECK (
  attd_rate IN (0, 1, 2, 3, 4, 5)
);