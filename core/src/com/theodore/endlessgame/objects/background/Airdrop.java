package com.theodore.endlessgame.objects.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.theodore.endlessgame.objects.ParticleEffect;
import com.theodore.endlessgame.screens.SplashScreen;

public class Airdrop {

    private Texture texture;

    private Sprite sprite;

    private OrthographicCamera camera;

    private boolean dead;
    private boolean readyToBeRemoved;

    private float elapsedTime = 0;

    public Airdrop(float x, float y, float scale, boolean isLoading) {
        dead = false;
        readyToBeRemoved = false;

        camera = new OrthographicCamera(1280, 720);
        camera.position.set(1280 / 2, 720 / 2, 0);
        camera.update();

        if(isLoading) {
            texture = new Texture(Gdx.files.internal("powerups/paradrop.png"));
        }else{
            texture = SplashScreen.assetManager.get("powerups/paradrop.png");
        }

        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        sprite.setScale(scale);

    }

    private float i = 0;

    public void draw(SpriteBatch spriteBatch) {

        if (getMouse().x > sprite.getX() && getMouse().x < sprite.getX() + sprite.getWidth()
                && getMouse().y > sprite.getY() && getMouse().y < sprite.getY() + sprite.getHeight()
                && Gdx.input.justTouched()) {
            dead = true;
        }

        if (dead) {
            elapsedTime += Gdx.graphics.getDeltaTime();

            if (i > 0.5f) {
                i = 0;
                readyToBeRemoved = true;
                dead = false;
            } else {
                i += 1 * Gdx.graphics.getDeltaTime();
            }
        } else {
            sprite.setColor(new Color(spriteBatch.getColor().r, spriteBatch.getColor().g, spriteBatch.getColor().b, sprite.getColor().a));
            sprite.setPosition(sprite.getX(), sprite.getY() - ((200 * Gdx.graphics.getDeltaTime()) * sprite.getScaleX()));
            sprite.draw(spriteBatch);
        }

    }

    private Vector2 getMouse() {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);

        return new Vector2(mouse.x, mouse.y);
    }

    public void dispose() {
        texture.dispose();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isReadyToBeRemoved() {
        return readyToBeRemoved;
    }

}
