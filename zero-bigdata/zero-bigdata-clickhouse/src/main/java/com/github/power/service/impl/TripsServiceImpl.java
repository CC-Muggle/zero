package com.github.power.service.impl;

import com.github.power.dao.TripsDao;
import com.github.power.dao.mapper.TripsMapper;
import com.github.power.model.res.ResTripsStatisticDTO;
import com.github.power.service.TripsService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class TripsServiceImpl implements TripsService {

    @Resource
    private TripsDao tripsDao;

    @Override
    public ResTripsStatisticDTO statistic() {
        return null;
    }
}
