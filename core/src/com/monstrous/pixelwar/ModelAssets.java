package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectMap;



//

public class ModelAssets implements Disposable {

    public static ObjectMap<String,Model> map = null;

    public ModelAssets() {
        ModelBuilder modelBuilder = new ModelBuilder();
        if(map == null)
            map = new ObjectMap<>();
        map.clear();

        // create models


        AssetManager assets = new AssetManager();
        assets.load("models/assets.g3db", Model.class);

        boolean loading = true;
        while(!assets.update())
            ;


        map.put("Assets", assets.get("models/assets.g3db", Model.class));
    }

    public static Model getModel(String name) {
        return map.get(name);
    }


    @Override
    public void dispose() {
        for(ObjectMap.Entry<String,Model> entry : map) {
            entry.value.dispose();
        }
        map.clear();
    }
}
