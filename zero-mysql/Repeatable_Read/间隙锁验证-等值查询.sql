--  执行顺序：begin -> select for update -> insert -> commit

-- 执行结果：执行事务A，在执行事务B时不会被阻塞，但是在执行事务C时会被阻塞
-- 产生原因：执行事务A为等值查询，会对一个范围进行上锁，而这个锁的范围是通过主键进行确认的，在主键和主键的空隙之间加上锁，一旦超出了范围，就可以进行插入

-- 事务A
begin;

select * from test_innodb_engin where id = 9 for update;


INSERT INTO `test`.`test_innodb_engin`(`id`, `a`, `b`, `c`) VALUES (11, 11, 11, '11');

commit;


-- 事务C
INSERT INTO `test`.`test_innodb_engin`(`id`, `a`, `b`, `c`) VALUES (10, 10, 10, '10');