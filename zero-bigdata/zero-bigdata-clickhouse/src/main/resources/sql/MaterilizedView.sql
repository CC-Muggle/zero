use solaxpower;

CREATE MATERIALIZED VIEW trips_materialized_mv
ENGINE = MergeTree ORDER BY (pickup_datetime, dropoff_datetime) POPULATE as
	select
		*
	from
		trips
	where
		payment_type = 1


SELECT COUNT() FROM trips_materialized_mv