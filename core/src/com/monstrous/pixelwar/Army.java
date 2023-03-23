package com.monstrous.pixelwar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

public class Army {
    public String name;
    public Color color;
    public Material material;
    public boolean isNeutral;

    public Army(String name, Color color) {
        this.name = name;
        this.color = color;

        material = new Material(ColorAttribute.createDiffuse(color));
    }
}
