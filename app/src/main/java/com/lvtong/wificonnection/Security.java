package com.lvtong.wificonnection;

import java.io.Serializable;

/**
 * @author tong.lv
 * @date 2020/1/10
 */
public class Security implements Serializable {

    private static final long serialVersionUID = -8522262699379925709L;
    private int key;
    private String name;

    public Security(int key, String name) {
        this.key = key;
        this.name = name;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
