package com.yongchun.library.model;

import java.util.List;

public interface OnItemClickListener<T> {
    /**
     * Callback method to be invoked when an item has been selected.
     *
     * @param items An items that are selected
     * @param name Name of folder of an items
     */
    void onItemsClick(String name, List<T> items);
}
