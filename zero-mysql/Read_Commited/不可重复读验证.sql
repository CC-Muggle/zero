-- 在读并提交的情况下，会产生不可重复读
-- 先执行事务A查询id为2的数据，再将事务B完全执行，当执行到第二次查询id为2的数据时，看到的数据为2222

-- 事务A
begin;

-- 第一次查询得到的结果为 2 2 2 2
select * from test_innodb_engin where id = 2;

-- 第二次查询的结果为2 2 2 2222
select * from test_innodb_engin where id = 2;

commit;



-- 事务B
begin;

-- 更新id为2的语句
UPDATE `test`.`test_innodb_engin` SET `c` = '2222' WHERE `id` = 2;

commit;