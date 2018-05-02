CREATE TABLE app.meeting_date (
  meet_date_id  SERIAL,
  meet_id       INTEGER NOT NULL,
  meet_date     TIMESTAMP NOT NULL
);
ALTER TABLE app.meeting_date ADD CONSTRAINT pk_meeting_date PRIMARY KEY (meet_date_id);
ALTER TABLE app.meeting_date ADD CONSTRAINT f_meeting_date FOREIGN KEY (meet_id) REFERENCES app.meeting(meet_id) ON DELETE CASCADE ;
CREATE UNIQUE INDEX u_meeting_date ON app.meeting_date(meet_id, meet_date);
CREATE SEQUENCE meeting_date_seq START 101;
