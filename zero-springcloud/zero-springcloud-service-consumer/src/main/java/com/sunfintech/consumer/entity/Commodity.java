package com.sunfintech.consumer.entity;

import java.math.BigDecimal;
import java.util.Arrays;

public class Commodity {

    private String name;
    
    private BigDecimal price;
    
    private byte[] barCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public byte[] getBarCode() {
        return barCode;
    }

    public void setBarCode(byte[] barCode) {
        this.barCode = barCode;
    }

    @Override
    public String toString() {
        return "Order [name=" + name + ", price=" + price + ", barCode=" + Arrays.toString(barCode) + "]";
    }
    
}
