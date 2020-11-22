package com.ta.sm;

import java.util.concurrent.atomic.AtomicInteger;

public class ProductInfo {
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final int id;

    private String name;

    private String val;



    public ProductInfo(String name, String val) {
        this.id = COUNTER.getAndIncrement();;
        this.name = name;
        this.val = val;

    }


    public ProductInfo() {

        this.id = COUNTER.getAndIncrement();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }


}
