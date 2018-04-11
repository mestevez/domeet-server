CREATE TABLE app.meeting_date (
  meet_id     INTEGER NOT NULL,
  meet_date   TIMESTAMP NOT NULL
);
ALTER TABLE app.meeting_date ADD CONSTRAINT pk_meeting_date PRIMARY KEY (meet_id, meet_date);
ALTER TABLE app.meeting_date ADD CONSTRAINT f_meeting_date FOREIGN KEY (meet_id) REFERENCES app.meeting(meet_id);
