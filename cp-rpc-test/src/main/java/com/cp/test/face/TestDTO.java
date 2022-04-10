package com.cp.test.face;

import java.io.Serializable;

public class TestDTO implements Serializable {
    private static final long serialVersionUID = 580593988737789295L;
    private String aa;
    private int aabb;


    public String getAa() {
        return aa;
    }

    public void setAa(String aa) {
        this.aa = aa;
    }

    public int getAabb() {
        return aabb;
    }

    public void setAabb(int aabb) {
        this.aabb = aabb;
    }

    @Override
    public String toString() {
        return "TestDTO{" +
                "aa='" + aa + '\'' +
                ", aabb=" + aabb +
                '}';
    }
}
