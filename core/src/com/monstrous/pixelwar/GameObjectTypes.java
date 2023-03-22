package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

// acts as 'static' class. Instantiated on World creation. All members are static.

public class GameObjectTypes {

    public static Array<GameObjectType> types;

    // instantiate in World constructor
    public GameObjectTypes() {
        types = new Array<>();
        types.add(new GameObjectType("Tank", "TankBody", "TankTurret", true));
        types.add(new GameObjectType("Anti-Aircraft", "AntiAircraftBase", "AntiAircraft", true));
        types.add(new GameObjectType("AirShip", "AirShip", null, true));
        types.add(new GameObjectType("Flag", "Flag", null, true));
        types.add(new GameObjectType("Tree1", "Tree1", null, false));
        types.add(new GameObjectType("Stone1", "Stone1", null,false));
        types.add(new GameObjectType("Stone2", "Stone2", null, false));
        types.add(new GameObjectType("Stone3", "Stone3", null, false));
;    }

    public static GameObjectType findType(String typeName) {
        for(GameObjectType type : types ){
            if(type.name.contentEquals(typeName))
                return type;
        }
        Gdx.app.error("findType: not found", typeName);
        return null;
    }
}
