package com.solax.power.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "trips")
public class Trips {

    @TableField("trip_id")
    private String tripId;

    @TableField("pickup_datetime")
    private String pickupDatetime;

    @TableField("dropoff_datetime")
    private String dropoffDatetime;

    @TableField("pickup_longitude")
    private String pickupLongitude;

    @TableField("pickup_latitude")
    private String pickupLatitude;

    @TableField("dropoff_longitude")
    private String dropoffLongitude;

    @TableField("dropoff_latitude")
    private String dropoffLatitude;

    @TableField("passenger_count")
    private String passengerCount;

    @TableField("trip_distance")
    private String tripDistance;

    @TableField("fare_amount")
    private String fareAmount;

    @TableField("extra")
    private String extra;

    @TableField("tip_amount")
    private String tipAmount;

    @TableField("tolls_amount")
    private String tollsAmount;

    @TableField("total_amount")
    private String totalAmount;

    @TableField("payment_type")
    private String paymentType;

    @TableField("pickup_ntaname")
    private String pickupNtaname;

    @TableField("dropoff_ntaname")
    private String dropoffNtaname;

}
