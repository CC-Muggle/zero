package com.github.power.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.power.dao.mapper.TripsMapper;
import com.github.power.entity.Trips;
import org.springframework.stereotype.Component;

@Component
public class TripsDao extends ServiceImpl<TripsMapper, Trips> {
}
