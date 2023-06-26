package com.solax.power.model.res;

import lombok.Data;

@Data
public class ResultDTO<T>{

    private int code;

    private String message;

    private T data;


    public ResultDTO<T> ofSuccess(T t){
        ResultDTO<T> result = new ResultDTO<>();
        result.code = 0;
        result.message = "成功";
        result.data = t;
        return result;
    }

    public ResultDTO<T> ofSuccess(){
        ResultDTO<T> result = new ResultDTO<>();
        result.code = 0;
        result.message = "成功";
        return result;
    }

}
