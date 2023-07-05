package com.solax.power.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.solax.power.dao.mapper.TripsMapper;
import com.solax.power.entity.Trips;
import org.springframework.stereotype.Component;

@Component
public class TripsDao extends ServiceImpl<TripsMapper, Trips> {
}
