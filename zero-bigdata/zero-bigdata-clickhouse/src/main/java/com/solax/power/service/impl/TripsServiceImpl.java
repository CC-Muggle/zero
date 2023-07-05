package com.solax.power.service.impl;

import com.solax.power.dao.TripsDao;
import com.solax.power.dao.mapper.TripsMapper;
import com.solax.power.model.res.ResTripsStatisticDTO;
import com.solax.power.service.TripsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TripsServiceImpl implements TripsService {

    @Resource
    private TripsDao tripsDao;

    @Override
    public ResTripsStatisticDTO statistic() {
        return null;
    }
}
