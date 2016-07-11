package com.doapps.habits.models;

import java.util.List;

interface ListProvider {
    List<?> getList();
    boolean isEmpty();
}
