package com.kdpark.sickdan.model.dto;

import java.util.Comparator;

public enum RelationshipStatus {
    NONE(0, "없음"),
    REQUESTING(1, "요청중"),
    REQUESTED(2, "요청받음"),
    FRIEND(3, "친구"),
    SELF(4, "");

    public static final Comparator<RelationshipStatus> SHOW_ORDER = new Comparator<RelationshipStatus>() {
        @Override
        public int compare(RelationshipStatus o1, RelationshipStatus o2) {
            return getPriority(o1) - getPriority(o2);
        }

        private int getPriority(RelationshipStatus r) {
            switch (r) {
                case FRIEND:
                    return 0;
                case REQUESTED:
                    return 1;
                case REQUESTING:
                    return 2;
                default:
                    return 3;
            }
        }
    };

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
