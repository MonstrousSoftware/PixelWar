package com.monstrous.pixelwar;

import com.badlogic.gdx.utils.Array;

public class AI {

    private static float UPDATE_INTERVAL = 5f;

    private GameObject flag;
    private Array<GameObject> gameObjects;
    private GameObject enemyFlag;
    private Array<GameObject> units;
    private Array<GameObject> enemyAA;
    private Array<GameObject> enemyTanks;
    private Array<GameObject> enemyTowers;
    private float updateTimer;

    public AI(GameObject flag, Array<GameObject> gameObjects) {
        this.flag = flag;
        this.gameObjects = gameObjects; // reference to master list, don't copy it
        units = new Array<>();
        enemyAA = new Array<>();
        enemyTanks = new Array<>();
        enemyTowers = new Array<>();
        updateTimer = UPDATE_INTERVAL * 4;      // wait a bit at start to give player some time
    }

    public void update(float deltaTime) {
        // do AI processing at intervals, not every frame
        updateTimer-= deltaTime;
        if(updateTimer> 0)
            return;
        updateTimer = UPDATE_INTERVAL;

        units.clear();
        for(GameObject go: gameObjects) {
            // scout enemy units/structures
            if(go.army != flag.army && go.type.name.contentEquals("Flag")) {
                enemyFlag = go;
            }
            if(go.army != flag.army && go.type.name.contentEquals("Anti-Aircraft")) {
                enemyAA.add(go);
            }
            if(go.army != flag.army && go.type.name.contentEquals("Tank")) {
                enemyTanks.add(go);
            }
            if(go.army != flag.army && go.type.name.contentEquals("Tower")) {
                enemyTowers.add(go);
            }
            // own units
            if(go.army == flag.army && go.type.name.contentEquals("Tank") && !go.isDying) {
                units.add(go);
            }
            if(go.army == flag.army && go.type.name.contentEquals("AirShip") && !go.isDying) {
                units.add(go);
            }
        }

        // now allocate destinations for our units

        int index = 0;
        for(GameObject unit : units) {
            if(index++ < 1)
                unit.setDestination(flag.position);     // defend the flag
            else {
                // find random enemy target to attack
                float r = (float)Math.random();
                if(r < 0.2f)
                    unit.setDestination(enemyFlag.position);
                else {
                    // depending on random value find type of target
                    Array<GameObject> targets;
                    if (r < 0.4f)
                        targets = enemyAA;
                    else if (r < 0.6f)
                        targets = enemyTanks;
                    else
                        targets = enemyTowers;
                    // now select random item of that type
                    int targetIndex = (int)(Math.random() * targets.size);
                    unit.setDestination(targets.get(targetIndex).position);
                }
            }
        }
    }
}
