package com.theodore.endlessgame.objects.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.theodore.endlessgame.Level;
import com.theodore.endlessgame.screens.SplashScreen;

public class Airplanes {

    private Array<Airplane> airplanes = new Array<Airplane>();
    private Array<Airdrop> airdrops = new Array<Airdrop>();

    private Texture airplaneTexture;

    private boolean shouldSpawnAirplane;
    private boolean isLoading;

    public Airplanes(boolean isLoading) {
        this.isLoading = isLoading;
        if (isLoading) {
            airplaneTexture = new Texture(Gdx.files.internal("planes/airplane.png"));
            airplaneTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } else {
            airplaneTexture = SplashScreen.assetManager.get("planes/airplane.png");
        }
        shouldSpawnAirplane = false;
    }

    private float i = 0;

    public void draw(SpriteBatch spriteBatch) {
        for (int i = 0; i < airdrops.size; i++) {
            airdrops.get(i).draw(spriteBatch);

            if (airdrops.get(i).getSprite().getY() < 0 || airdrops.get(i).isReadyToBeRemoved()) {
                airdrops.removeIndex(i);
            }
        }

        for (int i = 0; i < airplanes.size; i++) {
            airplanes.get(i).draw(spriteBatch);

            if(airplanes.get(i).spawnAirdrop()) {
                addAirdrop(airplanes.get(i).getAirplaneSprite().getX() + 90, airplanes.get(i).getAirplaneSprite().getY() - 80, airplanes.get(i).getAirplaneSprite().getScaleX());
                airplanes.get(i).setSpawnAirdrop(false);
            }

            if (airplanes.get(i).getAirplaneSprite().getX() > 1400 || airplanes.get(i).isReadyToBeRemoved()) {
                if (airplanes.get(i).isReadyToBeRemoved()) {
                    Level.airplanesDestroyed++;
                }

                airplanes.removeIndex(i);
            }
        }

        if (i > 10) {
            i = 0;
            shouldSpawnAirplane = airplanes.size == 0 && MathUtils.randomBoolean(0.25f);
        } else {
            i += 1 * Gdx.graphics.getDeltaTime();
        }

        if (shouldSpawnAirplane) {
            addPlane(-200);
            shouldSpawnAirplane = false;
        }
    }

    public void addPlane(float x) {
        Airplane airplane = new Airplane(airplaneTexture, x, isLoading);
        airplanes.add(airplane);
    }

    public void addAirdrop(float x, float y, float scale) {
        Airdrop airdrop = new Airdrop(x, y, scale, isLoading);
        airdrops.add(airdrop);
    }

    public void dispose() {
        airplaneTexture.dispose();
        for (int i = 0; i < airplanes.size; i++) {
            airplanes.get(i).dispose();
        }

        for (int i = 0; i < airdrops.size; i++) {
            airdrops.get(i).dispose();
        }
    }
}
