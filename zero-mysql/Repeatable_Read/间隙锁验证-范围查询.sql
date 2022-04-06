-- 执行顺序：begin -> select for update -> insert -> commit

-- 执行结果：事务B执行过程中会被阻塞，当事务A将事务提交只有，事务B也相较的完成了
-- 产生原因：在操作事务A时，会对范围进行加上间隙锁，保证了这个范围在当前内所有提交的数据都会被阻塞，所以事务B只能等待事务A完成后才能进行新的事务操作


-- 事务A
begin;

-- 查询id为8-10的数据进行上锁
select * from test_innodb_engin where id between 8 and 10 for update;

-- 查询是否存在id为9的数据
select * from test_innodb_engin where id = 9;

-- 事务中插入一条数据，该数据已经被其他事务所插入，则会产生幻读
INSERT INTO `test`.`test_innodb_engin`(`id`, `a`, `b`, `c`) VALUES (12, 12, 12, '12');

-- 查询是否存在id为9的数据
select * from test_innodb_engin where id = 9;

commit;






-- 事务B
begin;

INSERT INTO `test`.`test_innodb_engin`(`id`, `a`, `b`, `c`) VALUES (9, 9, 9, '9');

commit;