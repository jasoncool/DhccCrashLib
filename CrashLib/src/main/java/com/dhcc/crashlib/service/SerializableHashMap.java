package com.dhcc.crashlib.service;

import java.io.Serializable;
import java.util.HashMap;

public class SerializableHashMap implements Serializable {

    private HashMap<String,String> map;

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

}
