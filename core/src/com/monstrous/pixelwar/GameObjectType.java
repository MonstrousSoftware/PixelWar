package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class GameObjectType {
    public String name;
    public int typeId;
    public String modelName;
    public String modelName2;
    public Boolean isScenery;  // show this type in mini map? false for scenery
    public boolean isMobile;
    public boolean isProjectile;
    public boolean isAirship;
    public boolean isTank;
    public boolean isTower;
    public boolean isFlag;
    public boolean isAA;
    public boolean isBullet;
    public boolean isBomb;
    public boolean hasHealthBar;
    public boolean followsTerrain;
    public float timeToLive;
    public float healthPoints;
    public Vector3 dimensions;
    public BoundingBox bbox;
    public float radius;
    public float maxSpeed;
    public float rotationSpeed;
    public float turnFactor;
    public Texture iconTexture = null;
    public Texture enemyIconTexture = null;

    public GameObjectType(String name, int id, String modelName, String modelName2, String textureName, boolean isScenery, float healthPoints, boolean hasHealthBar, float timeToLive ) {
        this.name = name;
        this.typeId = id;
        this.modelName = modelName;
        this.modelName2 = modelName2;
        this.isScenery = isScenery;
        this.timeToLive = timeToLive;
        this.healthPoints = healthPoints;
        this.hasHealthBar = hasHealthBar;
        this.followsTerrain = true;
        this.isMobile = false;
        this.isProjectile = false;
        this.isAirship = false;
        this.isTank = false;
        this.isFlag = false;
        this.isAA = false;
        this.isBullet = false;
        this.isBomb = false;
        this.isTower = false;
        this.maxSpeed = 5f;
        this.rotationSpeed = 60f;


        this.turnFactor = 12;

        Model model = ModelAssets.getModel("Assets");
        ModelInstance modelInstance = new ModelInstance(model, modelName);

        if (textureName != null) {
             iconTexture = new Texture(Gdx.files.internal("icons/" + textureName));
             enemyIconTexture = new Texture(Gdx.files.internal("icons/red-" + textureName));
        }

        // calculate dimensions, e.g. for object picking
        bbox = new BoundingBox();
        modelInstance.calculateBoundingBox(bbox);
        dimensions = new Vector3();
        bbox.getDimensions(dimensions);
        float hw = dimensions.x/2f;
        float hd = dimensions.z/2f;
        radius = (float) Math.sqrt(hw*hw + hd*hd);      // radius in horizontal plane
    }
}
