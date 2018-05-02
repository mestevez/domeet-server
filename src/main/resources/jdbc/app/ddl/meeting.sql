CREATE TABLE app.meeting (
  meet_id           SERIAL,
  meet_title        VARCHAR(100) NOT NULL,
  meet_description  TEXT,
  meet_duration     SMALLINT NOT NULL DEFAULT 0,
  meet_type         SMALLINT NOT NULL DEFAULT 0,     -- 0: Undetermined, 1: Inform, 2: Consult, 3: Solve problems, 4: Make decisions,
  meet_state        SMALLINT NOT NULL DEFAULT 0,     -- 0: Ready, 1: Cancelled, 2: Started, 3: Ended, 4: Concluded, 5: Mail Sent
  meet_time_start   TIMESTAMP,
  meet_time_end     TIMESTAMP,
  meet_mom          BYTEA,                           -- Minutes of the meeting document,
  meet_leader       INTEGER NOT NULL
);
ALTER TABLE app.meeting ADD CONSTRAINT pk_meeting PRIMARY KEY (meet_id);
ALTER TABLE app.meeting ADD CONSTRAINT f_meeting_leader FOREIGN KEY (meet_leader) REFERENCES app.user(user_id);
ALTER TABLE app.meeting ADD CONSTRAINT c_meeting_type_range CHECK (
  meet_type IN (0, 1, 2, 3, 4)
);
ALTER TABLE app.meeting ADD CONSTRAINT c_meeting_state_range CHECK (
  meet_state IN (0, 1, 2, 3, 4, 5, 6)
);
-- Start time must be defined when the meeting starts
ALTER TABLE app.meeting ADD CONSTRAINT c_meeting_info_start CHECK (
  (meet_state < 3 AND meet_time_start IS NULL) OR
  (meet_state >= 3 AND meet_time_start IS NOT NULL)
);
-- End time must be defined when the meeting ends
ALTER TABLE app.meeting ADD CONSTRAINT c_meeting_info_end CHECK (
  (meet_state < 4 AND meet_time_end IS NULL) OR
  (meet_state >= 4 AND meet_time_end IS NOT NULL)
);
-- Minutes of the meeting can only be defined if the meeting has been concluded
ALTER TABLE app.meeting ADD CONSTRAINT c_meeting_info_mom CHECK (
  meet_mom IS NULL OR meet_state >= 5
);
CREATE SEQUENCE meeting_seq START 101;