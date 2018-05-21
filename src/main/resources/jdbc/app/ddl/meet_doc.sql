CREATE TABLE app.meet_doc (
  doc_id           INTEGER,
  meet_id          INTEGER NOT NULL,
  doc_name         VARCHAR(255) NOT NULL,
  doc_data         BYTEA NOT NULL,
  doc_mimetype     VARCHAR(100),
  doc_size         INTEGER
);
ALTER TABLE app.meet_doc ADD CONSTRAINT pk_meet_doc PRIMARY KEY (doc_id);
ALTER TABLE app.meet_doc ADD CONSTRAINT f_meet_doc FOREIGN KEY (meet_id) REFERENCES app.meeting(meet_id) ON DELETE CASCADE ;
CREATE SEQUENCE app.doc_seq START 100 INCREMENT BY 50 OWNED BY app.meet_doc.doc_id;