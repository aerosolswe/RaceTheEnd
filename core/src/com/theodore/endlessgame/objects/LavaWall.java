package com.theodore.endlessgame.objects;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.theodore.endlessgame.screens.SplashScreen;
import com.theodore.endlessgame.utils.Util;

public class LavaWall {

    private Texture[] explosionTextures = new Texture[3];
    private Sprite[] explosionSprites = new Sprite[3];
    private Texture fsExplosion;

    private Vector2 playerPosition = new Vector2();

    private PointLight pointLight;

    private float x;
    private float y;

    public LavaWall(RayHandler rayHandler) {
        x = -100;
        y = -500;

        fsExplosion = SplashScreen.assetManager.get("explosions/fsexp.png");

        pointLight = new PointLight(rayHandler, 50, Color.ORANGE, 15, x, y);

        for (int i = 0; i < 3; i++) {
            explosionTextures[i] = SplashScreen.assetManager.get("explosions/exp" + (3 + i) + ".png");

            explosionSprites[i] = new Sprite(explosionTextures[i]);
            explosionSprites[i].setSize(20, 20);
            explosionSprites[i].setOrigin(explosionSprites[i].getWidth() / 2, explosionSprites[i].getHeight() / 2);

            explosionSprites[i].setPosition(x, y);
        }

    }

    public void update(float delta) {
        y = playerPosition.y - 10;

        for (int i = 0; i < explosionSprites.length; i++) {
            if (i == 0)
                explosionSprites[i].setPosition(x, y - 3);
            else if (i == 1)
                explosionSprites[i].setPosition(x, y + 3);
            else if (i == 2)
                explosionSprites[i].setPosition(x, y + 8);
            else if (i == 3)
                explosionSprites[i].setPosition(x, y + 16);

            explosionSprites[i].rotate(10 * delta);
        }

        pointLight.setPosition(explosionSprites[0].getX() + 10, explosionSprites[2].getY() + 8);

        if (Util.distance(playerPosition, pointLight.getPosition()) < 30) {
            pointLight.setActive(true);
        } else {
            pointLight.setActive(false);
        }

        float speed = 0;

        if (x < playerPosition.x - 10)
            speed = 5 * (Util.distance(playerPosition, pointLight.getPosition()) / 10);
        else
            speed = -5 * (Util.distance(playerPosition, pointLight.getPosition()) / 10);


        x += speed * delta;

    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(fsExplosion, x - 30, y, 40, 22);
        for (int i = 0; i < explosionSprites.length; i++) {
            explosionSprites[i].setColor(spriteBatch.getColor());
            explosionSprites[i].draw(spriteBatch);
        }

    }

    public void dispose() {
        fsExplosion.dispose();
        pointLight.dispose();
        for (int i = 0; i < explosionTextures.length; i++) {
            explosionTextures[i].dispose();
        }
    }

    public Vector2 getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(Vector2 playerPosition) {
        this.playerPosition = playerPosition;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
