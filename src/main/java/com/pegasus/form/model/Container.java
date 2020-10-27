package com.pegasus.form.model;

import java.util.ArrayList;
import java.util.List;

public class Container {
    private List<PackingList> packingList = new ArrayList<>();

    public void setPackingList(List<PackingList> plist) {
        this.packingList = plist;
    }

    public List<PackingList> getPackingList() {
        return packingList;
    }   
}
