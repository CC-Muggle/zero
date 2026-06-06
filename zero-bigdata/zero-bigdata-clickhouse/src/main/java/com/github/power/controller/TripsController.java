package com.github.power.controller;

import com.github.power.model.res.ResTripsDetailDTO;
import com.github.power.model.res.ResTripsStatisticDTO;
import com.github.power.model.res.ResultDTO;
import com.github.power.service.TripsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
public class TripsController {

    @Resource
    private TripsService tripsService;

    @GetMapping("/detail")
    public ResultDTO<List<ResTripsDetailDTO>> detail(String tripsId){
        return null;
    }

    @GetMapping("/statistic")
    public ResultDTO<ResTripsStatisticDTO> statistic(){
        return null;
    }

}
