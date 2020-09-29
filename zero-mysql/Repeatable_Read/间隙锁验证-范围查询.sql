-- 执行顺序：begin -> select for update -> insert -> commit

-- 执行结果：事务B执行过程中会被阻塞，当事务A将事务提交只有，事务B也相较的完成了
-- 产生原因：在操作事务A时，会对范围进行加上间隙锁，保证了这个范围在当前内所有提交的数据都会被阻塞，所以事务B只能等待事务A完成后才能进行新的事务操作


-- 事务A
begin

select a,b,d from e4 where d between '2020-08-15' and '2020-09-07' for update;

commit

-- 事务B
insert into e4 select 8,8,'2020-09-03'