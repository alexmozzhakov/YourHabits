package com.habitart.yourhabits.app;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "habits")
public class Habit {
    @DatabaseField(canBeNull = false)
    String title;
    @DatabaseField
    String description;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @DatabaseField
    int perDay;
    @DatabaseField
    int frequencyType;
    @DatabaseField
    int takes;
    @DatabaseField
    int beginTime;
    @DatabaseField
    int finishTime;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPerDay(int perDay) {
        this.perDay = perDay;
    }

    public void setFrequencyType(int frequencyType) {
        this.frequencyType = frequencyType;
    }

    public void setTakes(int takes) {
        this.takes = takes;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public void setFinishDate(int finishDate) {
        this.finishDate = finishDate;
    }

    public void setParticipants(Users[] participants) {
        this.participants = participants;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public String getTitle() {

        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPerDay() {
        return perDay;
    }

    public int getFrequencyType() {
        return frequencyType;
    }

    public int getTakes() {
        return takes;
    }

    public int getBeginTime() {
        return beginTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public int getFinishDate() {
        return finishDate;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public Users[] getParticipants() {
        return participants;
    }

    @DatabaseField
    int finishDate;
    @DatabaseField
    int totalTime;
    @DatabaseField(generatedId = true)
    int ID;
    Users[] participants;

    public Habit() {

    }

    public Habit(String title, String description, int perDay, int frequencyType, int takes, int beginTime, int finishTime, int finishDate, int totalTime, Users[] participants) {
        this.title = title;
        this.description = description;
        this.perDay = perDay;
        this.frequencyType = frequencyType;
        this.takes = takes;
        this.beginTime = beginTime;
        this.finishTime = finishTime;
        this.finishDate = finishDate;
        this.totalTime = totalTime;
        this.participants = participants;
    }
}
