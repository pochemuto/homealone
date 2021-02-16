package com.pochemuto.homealone.ikea;

import java.util.List;

import com.google.common.collect.MapDifference.ValueDifference;

public interface IkeaListener {
    void onItemsChanged(List<Item> added, List<Item> removed, List<ValueDifference<Item>> changed);
}
