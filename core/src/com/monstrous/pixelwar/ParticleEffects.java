package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;



public class ParticleEffects implements Disposable {


    private ParticleSystem particleSystem;
    private ParticleEffect smokeEffect;
    private ParticleEffect ringEffect;
    private Array<ParticleEffect> activeEffects;
    private Array<ParticleEffect> deleteList;

    public ParticleEffects(Camera cam) {
        // create a particle system
        particleSystem = new ParticleSystem();

        // create a point sprite batch and add it to the particle system
        PointSpriteParticleBatch  pointSpriteBatch = new PointSpriteParticleBatch();
        pointSpriteBatch.setCamera(cam);
        //pointSpriteBatch.getBlendingAttribute().sourceFunction =
        particleSystem.add(pointSpriteBatch);

        // load particle effect from file
        AssetManager assets = new AssetManager();
        ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
        assets.load("particle/fire-and-smoke.pfx", ParticleEffect.class, loadParam);
        assets.load("particle/explosion-ring.pfx", ParticleEffect.class, loadParam);
        assets.finishLoading();
        smokeEffect = assets.get("particle/fire-and-smoke.pfx");
        ringEffect = assets.get("particle/explosion-ring.pfx");

        activeEffects = new Array<>();
        deleteList = new Array<>();
    }

    public void addFire(Vector3 position) {
        // add loaded effect to particle system

        // we cannot use the originalEffect, we must make a copy each time we create new particle effect
        ParticleEffect effect = smokeEffect.copy();
        effect.translate(position);
        effect.init();
        effect.start();  // optional: particle will begin playing immediately
        particleSystem.add(effect);
        activeEffects.add(effect);
    }

    public void addExplosion(Vector3 position) {
        // add loaded effect to particle system

        // we cannot use the originalEffect, we must make a copy each time we create new particle effect
        ParticleEffect effect = ringEffect.copy();
        effect.translate(position);
        effect.init();
        effect.start();  // optional: particle will begin playing immediately
        particleSystem.add(effect);
        activeEffects.add(effect);
    }

    public void update( float deltaTime ) {
        particleSystem.update(deltaTime);

        // remove effects that have finished
        deleteList.clear();
        for(ParticleEffect effect : activeEffects) {
            if(effect.isComplete()) {
                Gdx.app.debug("particle effect completed", "");
                particleSystem.remove(effect);
                effect.dispose();
                deleteList.add(effect);
            }
        }
        activeEffects.removeAll(deleteList, true);
    }


    public void render(ModelBatch modelBatch) {
        particleSystem.begin();
        particleSystem.draw();
        particleSystem.end();
        modelBatch.render(particleSystem);
    }


    @Override
    public void dispose() {
        particleSystem.removeAll();
        for(ParticleEffect effect : activeEffects)
            effect.dispose();
    }
}
