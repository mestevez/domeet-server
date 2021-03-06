CREATE TABLE app.schedule_time (
  sch_id          INTEGER,
  sch_code        CHAR(10) NOT NULL,
  sch_dayofweek   SMALLINT NOT NULL,
  sch_allday      BOOLEAN NOT NULL DEFAULT FALSE,
  sch_hourbegin   CHAR(5),
  sch_hourend     CHAR(5)
);
ALTER TABLE app.schedule_time ADD CONSTRAINT pk_schedule_time PRIMARY KEY (sch_id);
CREATE UNIQUE INDEX u_schedule_time ON app.schedule_time(sch_code, sch_dayofweek, sch_hourbegin);
ALTER TABLE app.schedule_time ADD CONSTRAINT f_schedule_time_code FOREIGN KEY (sch_code) REFERENCES app.schedule(sch_code);
ALTER TABLE app.schedule_time ADD CONSTRAINT c_schedule_time_dayofweek_range CHECK (
  sch_dayofweek >= 1 AND sch_dayofweek <= 7
);
ALTER TABLE app.schedule_time ADD CONSTRAINT c_schedule_time_allday CHECK (
  (sch_allday = FALSE AND sch_hourbegin IS NOT NULL AND sch_hourend IS NOT NULL) OR
  (sch_allday = TRUE AND sch_hourbegin IS NULL AND sch_hourend IS NULL)
);
CREATE SEQUENCE app.schedule_time_seq START 100 INCREMENT BY 50 OWNED BY app.schedule_time.sch_id;