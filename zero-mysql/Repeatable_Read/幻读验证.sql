-- 幻读产生的条件来自于多个不同的事务之间相互作用而引发的问题
-- 事务A执行筛选id为9的数据，查询不到任何结果进行新增操作，此时事务B插入id为9的数据并进行提交
-- 当事务A在进行插入的情况下，却会发现主键重复，原因是因为在可重复读下readView只会保存第一次进行查询的结果集，所以并不能查询到id为9的数据
-- 所以第二次的select任然查询不到id为9的数据，那么我们就说在insert的时候出现了幻觉，我们则称之为幻读

-- 事务A
begin;

-- 查询是否存在id为9的数据
select * from test_innodb_engin where id = 9;

-- 事务中插入一条数据，该数据已经被其他事务所插入，则会产生幻读
INSERT INTO `test`.`test_innodb_engin`(`id`, `a`, `b`, `c`) VALUES (9, 9, 9, '9');

-- 查询是否存在id为9的数据
select * from test_innodb_engin where id = 9;

rollback;



-- 事务B
begin;

INSERT INTO `test`.`test_innodb_engin`(`id`, `a`, `b`, `c`) VALUES (9, 9, 9, '9');

commit;