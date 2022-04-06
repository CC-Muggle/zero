SELECT @@tx_isolation;

-- 设置read uncommitted级别：
set session transaction isolation level read uncommitted;

-- 设置read committed级别：
set session transaction isolation level read committed;

-- 设置repeatable read级别：
set session transaction isolation level repeatable read;

-- 设置serializable级别：
set session transaction isolation level serializable;