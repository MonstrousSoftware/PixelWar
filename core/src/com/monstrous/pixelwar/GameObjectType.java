package com.monstrous.pixelwar;

public class GameObjectType {
    String name;
    String modelName;
    String modelName2;
    Boolean showInMap;  // show this type in mini map? false for scenery


    public GameObjectType(String name, String modelName, String modelName2, boolean showInMap) {
        this.name = name;
        this.modelName = modelName;
        this.modelName2 = modelName2;
        this.showInMap = showInMap;
    }
}
