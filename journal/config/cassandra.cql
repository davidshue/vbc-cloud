CREATE  KEYSPACE IF NOT EXISTS vbc
  WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1 };

CREATE TABLE IF NOT EXISTS
     vbc.user_event (
       user_id text,
       event_ts timeuuid,
       type tinyint,
       content text,
       PRIMARY KEY ((user_id), event_ts, type)
     ) WITH CLUSTERING ORDER BY (event_ts DESC);
