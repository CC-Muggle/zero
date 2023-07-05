package com.solax.power.controller;

import com.solax.power.model.res.ResTripsDetailDTO;
import com.solax.power.model.res.ResTripsStatisticDTO;
import com.solax.power.model.res.ResultDTO;
import com.solax.power.service.TripsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
