package com.kdpark.sickdan.model.dto;

public enum RelationshipStatus {
    NONE(0), REQUESTING(1), REQUESTED(2), FRIEND(3);

    private int intVal;

    RelationshipStatus(int type) {
        this.intVal = type;
    }

    public int getInt() {
        return intVal;
    }
}