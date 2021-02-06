package com.pochemuto.homealone.ikea;

import java.util.List;

public interface IkeaListener {
    void onItemsChanged(List<Item> added, List<Item> removed);
}
