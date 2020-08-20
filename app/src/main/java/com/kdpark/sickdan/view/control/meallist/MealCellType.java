package com.kdpark.sickdan.view.control.meallist;

public enum MealCellType {
    SECTION(0), ITEM(1);

    private int intVal;

    MealCellType(int type) {
        this.intVal = type;
    }

    public int getInt() {
        return intVal;
    }
}
