# MySQL5 Addon for GoCD

### TODO
- [x] Working version of `Database` and `QueryExtensions` implementation for MySQL5
- [x] Patch all the iBatis SQLMap xml files to override specific queries for MySQL5
- [ ] Migration Tool for moving data from an existing H2 -> new MySQL (just like in Postgres Addon)
- [ ] Package MySQL Addon as a separate JAR on a new repo

init.sql that needs to be run to setup a MySQL server
```sql
create database cruise default CHARACTER set utf8 default COLLATE utf8_general_ci;
USE cruise;
CREATE TABLE changelog (
  change_number BIGINT NOT NULL,
  complete_dt TIMESTAMP NULL,
  applied_by VARCHAR(100) NOT NULL,
  description VARCHAR(500) NOT NULL
);
-- Patch for H2 Specific Procedures that're being used
DELIMITER $$
CREATE PROCEDURE IDENTITY()
BEGIN
  SELECT LAST_INSERT_ID() as id;
END $$
DELIMITER ;

```

DevelopmentServer with MySQL5 Properties that are needed
```sql
-Xms512m
-Xmx1024m
-XX:PermSize=400m
-Dgo.database.provider=in.ashwanthkumar.gocd.database.mysql5.MySQL5Database
-Dcruise.database.dir=../addon-api/mysql-addon/db/mysql
-Dcruise.database.port=3306
-Ddb.user=root
-Ddb.password=root
-Ddb.host=localhost
``` 