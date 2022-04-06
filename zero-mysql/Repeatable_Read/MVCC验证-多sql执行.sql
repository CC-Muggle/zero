-- 当前隔离级别 可重复读（REPEATABLE READ）
-- 当前执行顺序 5->7->16->18->9->11
-- 在可重复读的隔离级别下的MVCC只会获取第一次select中获得readView之后不再会获取。
-- 所以在事务执行的过程中，我们看不到事务以外操作数据的结果，而只能看到事物内执行的结果

-- 事务A，执行修改id为1的数据
begin;

update test_innodb_engin set a = 111 where id = 1;

update test_innodb_engin set b = 222 where id = 2;

update test_innodb_engin set c = '333' where id = 3;

commit;

-- 事务B，尝试修改id为1的数据
begin;

update test_innodb_engin set a = 1111 where id = 1;

commit;

-- 事务C，查看每个阶段的readview情况
begin;

select * from test_innodb_engin where id = 1;

commit;

-- 执行过程中，当执行第七行时id为1的这条数据会被加上排它锁（写锁），事务A执行之前会写入undo.log
-- 此时，事务B尝试更新时会被阻塞，在这阶段中，等待txr_id小于自己的事务被提交