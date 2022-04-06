-- 在读不提交的隔离级别下，事务几乎没什么作用了，事务A操作的查询能够透明的看到事务B操作的结果。
-- 下列案例就解释了脏读，事务A三次读取，每一次的状态都不一样，此时事务A并不知道哪一个读取操作时正确的
-- 当第三次执行时，事务B进行了回滚操作，倒是第二次查询的结果和第三次查询的结果不一致，这条数据到底是什么形态完全不清，那么就称之为脏读


-- 事务A
begin;

-- 查询id为3的事务B执行前数据状态
-- 3 3 3 3
select * from test_innodb_engin where id = 3;

-- 查询id为3的事务B执行后数据状态
-- 3 3 3 333
select * from test_innodb_engin where id = 3;

-- 查询id为3的事务B回滚数据状态
-- 3 3 3 3
select * from test_innodb_engin where id = 3;

commit;

-- 事务B
begin;

update test_innodb_engin set c = '333' where id = 3;

rollback;