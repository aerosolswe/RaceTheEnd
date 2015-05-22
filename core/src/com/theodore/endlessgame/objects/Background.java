package com.theodore.endlessgame.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.theodore.endlessgame.objects.background.Airplanes;
import com.theodore.endlessgame.objects.background.Clouds;
import com.theodore.endlessgame.objects.background.Scenery;
import com.theodore.endlessgame.objects.background.Weather;

public class Background {

    private Texture backgroundTexture;

    private SpriteBatch spriteBatch;

    private Clouds clouds;
    private Scenery scenery;
    private Weather weather;
    private Airplanes airplanes;

    private Vector2 playerVelocity;

    public Background(boolean isLoading) {
        spriteBatch = new SpriteBatch();

        backgroundTexture = new Texture(Gdx.files.internal("background/sky.png"));

        playerVelocity = new Vector2(0, 0);

        clouds = new Clouds(isLoading);
        scenery = new Scenery(isLoading);
        weather = new Weather(isLoading);
        airplanes = new Airplanes(isLoading);
    }

    public void draw(Color color) {
        spriteBatch.setColor(color);
        spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, 1280, 720));
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, 1280, 720);
        scenery.draw(spriteBatch, playerVelocity);
        airplanes.draw(spriteBatch);
        clouds.draw(spriteBatch);
        weather.draw(spriteBatch, playerVelocity);
        spriteBatch.end();
    }

    public void dispose() {
        spriteBatch.dispose();
        backgroundTexture.dispose();
        clouds.dispose();
        airplanes.dispose();
        scenery.dispose();
        weather.dispose();
    }

    public void setPlayerVelocity(Vector2 playerVelocity) {
        this.playerVelocity = playerVelocity;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public Scenery getScenery() {
        return scenery;
    }

    public Weather getWeather() {
        return weather;
    }

    public Airplanes getAirplanes() {
        return airplanes;
    }
}
