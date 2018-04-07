CREATE  KEYSPACE IF NOT EXISTS vbc
  WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1 };

CREATE TABLE IF NOT EXISTS
     vbc.event (
       user_id text,
       day timestamp,
       event_ts timestamp,
       content blob,
       PRIMARY KEY ((user_id, day), event_ts)
     ) WITH CLUSTERING ORDER BY (event_ts DESC);
