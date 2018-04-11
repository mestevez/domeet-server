CREATE TABLE app.schedule (
  sch_code          CHAR(10) NOT NULL,
  sch_description   VARCHAR(80) NOT NULL
);
ALTER TABLE app.schedule ADD CONSTRAINT pk_schedule PRIMARY KEY (sch_code);