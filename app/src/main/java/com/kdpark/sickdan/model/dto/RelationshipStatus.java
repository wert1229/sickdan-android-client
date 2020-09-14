package com.kdpark.sickdan.model.dto;

public enum RelationshipStatus {
    NONE(0, "없음"),
    REQUESTING(1, "요청중"),
    REQUESTED(2, "요청받음"),
    FRIEND(3, "친구"),
    SELF(4, "");

    private int intVal;
    private String desc;

    RelationshipStatus(int type, String desc) {
        this.intVal = type;
        this.desc = desc;
    }

    public int getInt() {
        return intVal;
    }

    public String getDesc() {
        return desc;
    }
}
