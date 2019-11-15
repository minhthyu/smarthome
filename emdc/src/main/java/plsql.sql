-- Oracle 实现 --
--使用PL/SQL建表
create database s_emdc;
use s_emdc;
BEGIN
FOR i IN 1..31 LOOP
EXECUTE IMMEDIATE
'CREATE TABLE e_detail_'||TO_CHAR(i)||
'(
name varchar(20),
srcId varchar(5),
dstId varchar(5),
sersorAddress varchar(7),
count int,
cmd  varchar(5),
status int,
data int,
gather_date date
)';
END LOOP;
END;
/
--使用PL/SQL删除表
BEGIN
FOR i IN 1..31 LOOP
EXECUTE IMMEDIATE
'DROP TABLE e_detail_'||TO_CHAR(i);
END LOOP;
END;
/



-- mysql 实现 --
-- 批量建表 --
DELIMITER $$
DROP PROCEDURE IF EXISTS pro_TableCreate$$
CREATE PROCEDURE pro_TableCreate()
BEGIN
DECLARE i INT;
DECLARE table_name VARCHAR(20);
SET i = 1;
WHILE i<32 DO
-- IF i<10 THEN
-- SET table_name = CONCAT('e_detail_0',i);
-- ELSE
SET table_name = CONCAT('e_detail_',i);
-- END IF;
SET @csql = CONCAT(
'CREATE TABLE ',table_name,
'(
name varchar(20),
srcId varchar(5),
dstId varchar(5),
sersorAddress varchar(7),
count int,
cmd  varchar(5),
status int,
data float,
gather_date date
)'
);
PREPARE create_stmt FROM @csql;
EXECUTE create_stmt;
DEALLOCATE PREPARE create_stmt;
SET i = i+1;
END WHILE;
END $$
DELIMITER ;
-- 批量删表 --
DELIMITER $$
DROP PROCEDURE IF EXISTS 'pro_TableDrop'$$
CREATE PROCEDURE pro_TableDrop()
BEGIN
DECLARE i INT;
DECLARE table_name VARCHAR(20);
SET i = 1;
WHILE i<32 DO
-- IF i<10 THEN
-- SET table_name = CONCAT('e_detail_0',i);
-- ELSE
SET table_name = CONCAT('e_detail_',i);
-- END IF;
SET @csql = CONCAT(
'DROP TABLE ',table_name);
PREPARE create_stmt FROM @csql;
EXECUTE create_stmt;
DEALLOCATE PREPARE create_stmt;
SET i = i+1;
END WHILE;
END $$
DELIMITER ;