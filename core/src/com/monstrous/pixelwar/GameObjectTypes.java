package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

// acts as 'static' class. Instantiated on World creation. All members are static.

public class GameObjectTypes {

    public static Array<GameObjectType> types;


    // instantiate in World constructor
    public GameObjectTypes() {
        types = new Array<>();
        GameObjectType tank = new GameObjectType("Tank", "TankBody", "TankTurret", false, -1f);
        tank.isMobile = true;
        types.add(tank);

        types.add(new GameObjectType("Anti-Aircraft", "AntiAircraftBase", "AntiAircraft", false, -1f));

        GameObjectType airShip = new GameObjectType("AirShip", "AirShip", null, false, -1f);
        airShip.followsTerrain = false;
        airShip.isMobile = true;
        types.add(airShip);

        types.add(new GameObjectType("Flag", "Flag", null, false, -1f));
        types.add(new GameObjectType("Tower", "Tower", null, false, -1f));
        types.add(new GameObjectType("Arrow", "Arrow", null, false, 2f));
        types.add(new GameObjectType("Bullet", "Bullet", null, false, 10f));
        types.add(new GameObjectType("Missile", "Missile", null, false, 10f));
        types.add(new GameObjectType("Tree1", "Tree1", null, true, -1f));
        types.add(new GameObjectType("Stone1", "Stone1", null,true,-1f ));
        types.add(new GameObjectType("Stone2", "Stone2", null, true, -1f));
        types.add(new GameObjectType("Stone3", "Stone3", null, true,-1f ));

        findType("Bullet").followsTerrain = false;
        findType("Missile").followsTerrain = false;
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