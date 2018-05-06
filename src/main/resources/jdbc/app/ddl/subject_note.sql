CREATE TABLE app.subject_note (
  note_id           SERIAL,
  subject_id        INTEGER NOT NULL,
  note_order        INTEGER NOT NULL DEFAULT 0,
  note_description  VARCHAR(255) NOT NULL,
  note_type         SMALLINT NOT NULL DEFAULT 0     -- 0: Comment, 1: Decision, 2: Agreement, 3: Unsettled
);
ALTER TABLE app.subject_note ADD CONSTRAINT pk_subject_note PRIMARY KEY (note_id);
CREATE UNIQUE INDEX u_subject_note ON app.subject_note(subject_id, note_order, note_type);
ALTER TABLE app.subject_note ADD CONSTRAINT f_subject_note FOREIGN KEY (subject_id) REFERENCES app.subject(subject_id) ON DELETE CASCADE ;
ALTER TABLE app.subject_note ADD CONSTRAINT c_subject_note_type_rang CHECK (
  note_type IN (0, 1, 2, 3)
);
CREATE SEQUENCE note_seq START 101;