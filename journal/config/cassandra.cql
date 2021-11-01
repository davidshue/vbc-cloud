CREATE KEYSPACE IF NOT EXISTS vbc
  WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1 };

CREATE TABLE IF NOT EXISTS
  vbc.user_event (
  user_id text,
  event_ts timeuuid,
  type tinyint,
  meta frozen<map<text, text>>,
  content text,
  PRIMARY KEY ((user_id), event_ts, type)
) WITH CLUSTERING ORDER BY (event_ts DESC)
  AND compaction = {'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy',
  'tombstone_compaction_interval': '86400'};
