-- 在读并提交的MVCC中，我们可以很直观的看到其他事务修改的信息
-- 因为读并提交下每一次select都会去更新自己的readView，所以每一次每一句update后或者insert后的数据我们是都看得到的
-- 不同于可重复读，事务中的select能够看到其他事务操作的结果，而可重复读是看不到其他事务中的结果的（与其他事物操作相同数据的情况下）


-- 事务A
begin;

-- 第一次查询得到的结果为 2 2 2 2
select * from test_innodb_engin where id = 2;

-- 第二次查询的结果为2 2 2 2222
select * from test_innodb_engin where id = 2;

-- 更新id为2的语句
UPDATE `test`.`test_innodb_engin` SET `c` = '222' WHERE `id` = 2;

-- 第二次查询的结果为2 2 2 222
select * from test_innodb_engin where id = 2;

commit;




-- 事务B
begin;

-- 更新id为2的语句
UPDATE `test`.`test_innodb_engin` SET `c` = '2222' WHERE `id` = 2;

commit;