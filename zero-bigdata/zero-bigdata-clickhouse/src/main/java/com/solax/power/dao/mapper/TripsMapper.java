package com.solax.power.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.solax.power.entity.Trips;
import com.solax.power.model.res.ResTripsStatisticDTO;

public interface TripsMapper extends BaseMapper<Trips> {

    ResTripsStatisticDTO statistic();

}
