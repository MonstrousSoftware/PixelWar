package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.monstrous.pixelwar.behaviours.*;

// acts as 'static' class. Instantiated on World creation. All members are static.

public class GameObjectTypes {

    public static Array<GameObjectType> types;


    // instantiate in World constructor
    public GameObjectTypes() {
        types = new Array<>();
        GameObjectType tank = new GameObjectType("Tank", "TankBody", "TankTurret", "tank.png", false, 100, -1f);
        tank.isMobile = true;
        tank.maxSpeed = 5f;
        types.add(tank);

        types.add(new GameObjectType("Anti-Aircraft", "AntiAircraftBase", "AntiAircraft", "anti-aircraft.png",false, 100, -1f));

        GameObjectType airShip = new GameObjectType("AirShip", "AirShip", "Bomb", "airship.png",false, 80,-1f);
        airShip.followsTerrain = false;
        airShip.isMobile = true;
        airShip.isAirship = true;
        airShip.maxSpeed = 3f;
        types.add(airShip);

        types.add(new GameObjectType("Flag", "Flag", null, "flag.png",false, 80, -1f));
        types.add(new GameObjectType("Tower", "Tower", null, "tower.png",false, 200, -1f));
        types.add(new GameObjectType("Arrow", "Arrow", null, null, false, 100, 2f));
        types.add(new GameObjectType("Bullet", "Bullet", null, null, false, 100, 10f));
        types.add(new GameObjectType("Bomb", "Bomb", null, null, false, 100, 5f));
        types.add(new GameObjectType("Missile", "Missile", null, null, false, 100,10f));
        types.add(new GameObjectType("Tree1", "Tree1", null, null, true, 100,-1f));
        types.add(new GameObjectType("Stone1", "Stone1", null,null, true,100,-1f ));
        types.add(new GameObjectType("Stone2", "Stone2", null, null, true, 100,-1f));
        types.add(new GameObjectType("Stone3", "Stone3", null, null, true,100,-1f ));

        findType("Bullet").followsTerrain = false;
        findType("Bullet").isProjectile = true;
        findType("Missile").followsTerrain = false;
        findType("Missile").isProjectile = true;
        findType("Bomb").followsTerrain = false;
        findType("Bomb").isProjectile = true;
        findType("Tower").isTower = true;
;    }

    public static GameObjectType findType(String typeName) {
        for(GameObjectType type : types ){
            if(type.name.contentEquals(typeName))
                return type;
        }
        Gdx.app.error("findType: not found", typeName);
        return null;
    }

    // may return null
    public static Behaviour getTypeBehaviour(GameObject go ) {
        // yuck...
        Behaviour behaviour = null;
        if(go.type.name.contentEquals("Anti-Aircraft"))
             behaviour = new AntiAircraft(go);
        if(go.type.name.contentEquals("Tank"))
            behaviour = new Tank(go);
        if(go.type.name.contentEquals("Bullet"))
            behaviour = new Bullet(go);
        if(go.type.name.contentEquals("Bomb"))
            behaviour = new Bomb(go);
        if(go.type.name.contentEquals("AirShip"))
            behaviour = new AirShip(go);
        if(go.type.name.contentEquals("Tower"))
            behaviour = new Tower(go);
        if(go.type.name.contentEquals("Flag"))
            behaviour = new Flag(go);
        return behaviour;
    }
}
