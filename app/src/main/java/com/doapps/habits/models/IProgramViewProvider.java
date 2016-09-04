package com.doapps.habits.models;

import com.google.firebase.database.DataSnapshot;

public interface IProgramViewProvider {
    /**
     * Returns title of Program
     * @return Title of {@link com.doapps.habits.models.Program}
     *
     */
    String getTitle();
    /**
     * @return Description of {@link com.doapps.habits.models.Program}
     */
    String getDescription();
    /**
     * @return Success rate of {@link com.doapps.habits.models.Program}
     */
    String getPercent();
    /**
     * @return Link to image of {@link com.doapps.habits.models.Program}
     */
    String getImageLink();
    /**
     * @return Data snapshot of {@link com.doapps.habits.models.Program}
     */
    DataSnapshot getSnapshot();
}
