package com.kdpark.sickdan.model.dto.enums;

public enum MealCategory {
    NONE(0, ""), BREAKFAST(1, "아침"), LUNCH(2, "점심"), DINNER(3, "저녁"), SNACK(4, "간식");

    private int intVal;
    private String strVal;

    MealCategory(int type, String str) {
        this.intVal = type;
        this.strVal = str;
    }

    public int getInt() {
        return intVal;
    }
    public String getStr() {
        return strVal;
    }
}
