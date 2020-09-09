package com.kdpark.sickdan.view.control.meallist;

public enum MealCellType {
    SECTION(0), ITEM(1), NEW(2), EDIT(3);

    private int intVal;

    MealCellType(int type) {
        this.intVal = type;
    }

    public int getInt() {
        return intVal;
    }
}
