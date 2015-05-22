package com.theodore.endlessgame.objects.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.theodore.endlessgame.screens.SplashScreen;

public class Weather {

    private Array<Sprite> particles = new Array<Sprite>();

    private Texture particleTexture;

    private Vector2 playerVelocity;

    public float spawnRate = 0.6f;

    public Weather(boolean isLoading) {
        if (isLoading) {
            particleTexture = new Texture(Gdx.files.internal("particles/snow.png"));
            particleTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } else {
            particleTexture = SplashScreen.assetManager.get("particles/snow.png");
        }

        playerVelocity = new Vector2();

        for (int i = 0; i < 20; i++) {
            addParticle(MathUtils.random(300, 720));
        }
    }

    private float i = 0;

    public void draw(SpriteBatch spriteBatch, Vector2 playerVelocity) {
        this.playerVelocity = playerVelocity;

        for (int i = 0; i < particles.size; i++) {
            particles.get(i).setColor(new Color(spriteBatch.getColor().r, spriteBatch.getColor().g, spriteBatch.getColor().b, particles.get(i).getColor().a));
//            particles.get(i).setColor(new Color(color.r, color.g, color.b, particles.get(i).getColor().a));
            particles.get(i).setPosition(particles.get(i).getX() - (((40 * Gdx.graphics.getDeltaTime()) + (playerVelocity.x / 15)) * particles.get(i).getScaleX()), particles.get(i).getY() - ((100 * Gdx.graphics.getDeltaTime()) * particles.get(i).getScaleX()));
            particles.get(i).rotate((200 * Gdx.graphics.getDeltaTime()) * particles.get(i).getScaleX());
            particles.get(i).draw(spriteBatch);

            if (particles.get(i).getY() < -100) {
                particles.removeIndex(i);
            }
        }

        if (i > 1) {
            i = 0;
            addParticle(750);
        } else {
            i += spawnRate * Gdx.graphics.getDeltaTime();
        }

    }

    public void addParticle(float y) {
        Sprite sprite = new Sprite(particleTexture);
        sprite.setPosition(MathUtils.random(200, 1500), y);
        sprite.setScale(MathUtils.random(0.01f, 0.3f));
        particles.add(sprite);
    }

    public void dispose() {
        particleTexture.dispose();
    }

}
