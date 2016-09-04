package com.doapps.habits.models;

import java.util.List;

interface IListProvider {

    /**
     * @return List of any kind
     */
    List<?> getList();

    /**
     * Checks whether list is empty or not
     * @return true, if list is empty
     */
    boolean isEmpty();
}
