package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class World implements Disposable {

    public Array<ModelInstance> instances;
    private Array<Model> models;
    private ModelAssets modelAssets;

    public World() {
        Gdx.app.debug("World", "constructor");

        modelAssets = new ModelAssets();

        models = new Array<>();
        instances = new Array<>();

        makeArrows();
        makeGrid();
        populate();
    }

    private void populate() {
        Model model;
        ModelInstance modelInstance;

        model = ModelAssets.getModel("AA");
        modelInstance =  new ModelInstance(model);
        models.add(model);
        instances.add(modelInstance);

        modelInstance =  new ModelInstance(model);
        modelInstance.transform.translate(0,0,15);
        models.add(model);
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
    }
}
