CREATE TABLE app.subject_doc (
  subject_id        INTEGER NOT NULL,
  doc_name          VARCHAR(255) NOT NULL,
  doc_data          BYTEA NOT NULL,
  doc_mimetype      VARCHAR(100),
  doc_size          INTEGER
);
ALTER TABLE app.subject_doc ADD CONSTRAINT f_subject_doc FOREIGN KEY (subject_id) REFERENCES app.subject(subject_id);