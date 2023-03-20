package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class World implements Disposable {

    public Array<ModelInstance> instances;
    private Array<Model> models;
    private Terrain terrain;
    private ModelAssets modelAssets;

    public World() {
        Gdx.app.debug("World", "constructor");

        modelAssets = new ModelAssets();

        terrain = new Terrain();

        models = new Array<>();         // todo take care in assets to dispose
        instances = new Array<>();

        Model model = terrain.getModel();
        ModelInstance modelInstance = new ModelInstance(model);
        instances.add(modelInstance);

//        model = terrain.getGridModel();
//        modelInstance = new ModelInstance(model);
//        modelInstance.transform.translate(0,.1f, 0.1f);
//        instances.add(modelInstance);

        //makeArrows();
        //makeGrid();
        populate();
    }

    private void populate() {
        placeAA(0,0);
        placeAA(0,15);



        for(int n = 0; n < 2600; n++) {
            float xx = (float) (Math.random()-0.5f)*Settings.worldSize;
            float zz = (float) (Math.random()-0.5f)*Settings.worldSize;
            float r = (float) (Math.random()*360f);
            placeTree(xx, zz, r);
        }



        placeBridge(50,15);
    }

    private void placeAA(float x, float z){
        Model model;
        ModelInstance modelInstance;

        model = ModelAssets.getModel("Assets");
        modelInstance =  new ModelInstance(model, "AntiAircraftBase");
        float y = terrain.getHeight(x, z);
        modelInstance.transform.translate(x,y,z);
        instances.add(modelInstance);

        modelInstance =  new ModelInstance(model, "AntiAircraft");
        modelInstance.transform.translate(x,y,z);
        modelInstance.transform.rotate(Vector3.Y, 60f);
        instances.add(modelInstance);
    }

    private void placeBridge(float x, float z){
        Model model;
        ModelInstance modelInstance;

        model = ModelAssets.getModel("Assets");
        modelInstance =  new ModelInstance(model, "Bridge");
        float y = terrain.getHeight(x, z);
        modelInstance.transform.translate(x,y,z);
        instances.add(modelInstance);
    }

    private void placeTree(float x, float z, float angle){
        Model model;
        ModelInstance modelInstance;

        model = ModelAssets.getModel("Assets");
        modelInstance =  new ModelInstance(model, "Tree1");
        float y = terrain.getHeight(x, z);
        //modelInstance.transform.rotate(Vector3.Y, angle);
        modelInstance.transform.translate(x,y,z);

        instances.add(modelInstance);
    }


    private void makeArrows() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createXYZCoordinates(10f, new Material(), VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked);
        ModelInstance modelInstance =  new ModelInstance(model);
        models.add(model);
        instances.add(modelInstance);
    }

    private void makeGrid() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createLineGrid(20, 20, 10, 10, new Material(ColorAttribute.createDiffuse(Color.BLUE)),   VertexAttributes.Usage.Position);
        ModelInstance modelInstance =  new ModelInstance(model);
        models.add(model);
        instances.add(modelInstance);
    }

    @Override
    public void dispose() {
        Gdx.app.debug("World", "dispose");
        for(Model model : models)
            model.dispose();
        modelAssets.dispose();
        terrain.dispose();
    }
}
