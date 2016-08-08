package com.yongchun.library.model;

import java.util.List;

public interface OnItemClickListener<T> {
    /**
     * Callback method to be invoked when an item has been selected.
     * @param item An item that is selected
     */
    void onItemClick(String name, List<T> items);
}
