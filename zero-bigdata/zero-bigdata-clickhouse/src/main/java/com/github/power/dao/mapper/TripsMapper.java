package com.github.power.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.power.entity.Trips;
import com.github.power.model.res.ResTripsStatisticDTO;

public interface TripsMapper extends BaseMapper<Trips> {

    ResTripsStatisticDTO statistic();

}
