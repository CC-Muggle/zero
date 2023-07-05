package com.solax.power.model.res;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class ResTripsDetailDTO {

    private String tripId;

    private String pickupDatetime;

    private String dropoffDatetime;

    private String pickupLongitude;

    private String pickupLatitude;

    private String dropoffLongitude;

    private String dropoffLatitude;

    private String passengerCount;

    private String tripDistance;

    private String fareAmount;

    private String extra;

    private String tipAmount;

    private String tollsAmount;

    private String totalAmount;

    private String paymentType;

    private String pickupNtaname;

    private String dropoffNtaname;

}
