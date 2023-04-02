package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectMap;

public class Armies {

    static private ObjectMap<String, Army> map = null;
    static private Army playerArmy;
    static private Army enemyArmy;
    static private Army neutral;

    public Armies() {
        map = new ObjectMap<>();

        playerArmy = new Army("Blue", Color.BLUE);
        enemyArmy = new Army("Red", Color.RED);
        neutral = new Army("Neutral", Color.WHITE); // for game objects that don't belong to either side, e.g. a tree
        neutral.isNeutral = true;
        enemyArmy.isEnemy = true;

        map.put(playerArmy.name,  playerArmy );
        map.put(enemyArmy.name, enemyArmy);
        map.put(neutral.name, neutral);

    }

    public static Army findArmy(String name) {
        Army army = map.get(name);
        if(army == null)
            Gdx.app.error("Unknown army name", name);

        return army;
    }

    public static Army getPlayerArmy() {
        return playerArmy;
    }


}
