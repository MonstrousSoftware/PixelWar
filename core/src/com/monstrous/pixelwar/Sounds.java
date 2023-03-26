package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Sounds implements Disposable  {

    public static int COMMENCE = 0;
    public static int VICTORIOUS = 1;
    public static int DEFEATED = 2;
    public static int AFFIRMATIVE = 3;
    public static int ROGER_THAT = 4;
    public static int FLAG = 5;
    public static int UNDER_ATTACK = 6;
    public static int TANK_FIRE = 7;
    public static int AA_FIRE = 8;
    public static int EXPLOSION = 9;
    public static int FALLING_BOMB = 10;
    public static int BULLET_HIT = 11;

    private static Main game;
    private static Array<Sound> sounds;


    public Sounds(Main game) {
        this.game = game;
        sounds = new Array<>();
        sounds.add( Gdx.audio.newSound(Gdx.files.internal("sounds/voices/commence.wav")) );
        sounds.add( Gdx.audio.newSound(Gdx.files.internal("sounds/voices/victorious.wav")) );
        sounds.add( Gdx.audio.newSound(Gdx.files.internal("sounds/voices/defeated.wav")) );
        sounds.add( Gdx.audio.newSound(Gdx.files.internal("sounds/voices/affirmative.wav")) );
        sounds.add( Gdx.audio.newSound(Gdx.files.internal("sounds/voices/roger that.wav")) );
        sounds.add( Gdx.audio.newSound(Gdx.files.internal("sounds/voices/flag.wav")) );
        sounds.add( Gdx.audio.newSound(Gdx.files.internal("sounds/voices/under attack.wav")) );
        sounds.add( Gdx.audio.newSound(Gdx.files.internal("sounds/gunshot-37055.mp3")) );
        sounds.add( Gdx.audio.newSound(Gdx.files.internal("sounds/cannonball-89596.mp3")) );
        sounds.add( Gdx.audio.newSound(Gdx.files.internal("sounds/explosion_01-6225.mp3")) );
        sounds.add( Gdx.audio.newSound(Gdx.files.internal("sounds/falling-bomb-41038.mp3")) );
        sounds.add( Gdx.audio.newSound(Gdx.files.internal("sounds/bullet-hit.mp3")) );
    }

    public static void playSound(int code) {
        sounds.get(code).play(game.getSoundVolume());
    }

    @Override
    public void dispose() {
        for(Sound s : sounds)
            s.dispose();
        sounds.clear();
    }
}
