package com.monstrous.pixelwar;

import com.badlogic.gdx.utils.Array;

public class AI {

    private static float UPDATE_INTERVAL = 5f;

    private GameObject flag;
    private Array<GameObject> gameObjects;
    private GameObject enemyFlag;
    private Array<GameObject> ownUnits;
    private Array<GameObject> ownTowers;
    private Array<GameObject> enemyAA;
    private Array<GameObject> enemyTanks;
    private Array<GameObject> enemyTowers;
    private float updateTimer;

    public AI(GameObject flag, Array<GameObject> gameObjects) {
        this.flag = flag;
        this.gameObjects = gameObjects; // reference to master list, don't copy it
        // create some arrays for specific units
        ownUnits = new Array<>();   // tanks and airships
        ownTowers = new Array<>();
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

        // make an inventory of all own units and enemy units/structures
        ownUnits.clear();
        for(GameObject go: gameObjects) {
            // scout enemy units/structures
            if(go.army != flag.army && go.type.isFlag) {
                enemyFlag = go;
            }
            if(go.army != flag.army && go.type.isAA) {
                enemyAA.add(go);
            }
            if(go.army != flag.army && go.type.isTank) {
                enemyTanks.add(go);
            }
            if(go.army != flag.army && go.type.isTower) {
                enemyTowers.add(go);
            }
            // own units
            if(go.army == flag.army && go.type.isTower && !go.isDying) {
                ownTowers.add(go);
            }
            if(go.army == flag.army && go.type.isTank && !go.isDying) {
                ownUnits.add(go);
            }
            if(go.army == flag.army && go.type.isAirship && !go.isDying) {
                ownUnits.add(go);
            }
        }

        // now allocate destinations for our units

        int index = 0;
        boolean flagGuardAssigned = false;
        for(GameObject unit : ownUnits) {

            // if unit is an airship, and it has no bomb attached, go to a reloading tower
            if(unit.type.isAirship && unit.modelInstance2 == null) {
                int targetIndex = (int) (Math.random() * ownTowers.size);   // choose one of the towers at random
                unit.setDestination(ownTowers.get(targetIndex).position,3f);
            }
            else if(!flagGuardAssigned && unit.type.isTank) {   // put first tank to defend the flag
                unit.setDestination(flag.position, 5f);     // defend the flag
                flagGuardAssigned = true;
            }
            else {
                // find random enemy target to attack
                float r = (float)Math.random();
                if(r < 0.2f)
                    unit.setDestination(enemyFlag.position, 4f);
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
                    if(targets.size > 0) {
                        int targetIndex = (int) (Math.random() * targets.size);
                        unit.setDestination(targets.get(targetIndex).position, 4f);
                    }
                }
            }
        }
    }
}
