package com.theodore.endlessgame.objects.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.theodore.endlessgame.objects.Player;
import com.theodore.endlessgame.objects.ui.Text;
import com.theodore.endlessgame.screens.SplashScreen;
import com.theodore.endlessgame.utils.Util;

public abstract class PowerUp {

    protected TextureAtlas textureAtlas;
    protected Animation animation;

    protected float elapsedTime;

    protected float x;
    protected float y;
    protected float width;
    protected float height;

    protected boolean readyToBeRemoved = false;
    protected boolean activated = false;

    protected Color color;

    public PowerUp(String powerUp, float x, float y) {
        textureAtlas = SplashScreen.assetManager.get("powerups/" + powerUp + ".pack");
        for (int i = 0; i < textureAtlas.getRegions().size; i++) {
            textureAtlas.getRegions().get(i).getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        color = new Color(1, 1, 1, 1);

        animation = new Animation(textureAtlas.getRegions().size, textureAtlas.getRegions());

        this.x = x;
        this.y = y;

        this.width = 1.5f;
        this.height = 1.5f;
    }

    public void update(float delta, Player player) {

        if(player.getActiveCar().getChassis().getPosition().x - 50 > x) {
            readyToBeRemoved = true;
        }

        if(Util.distance(player.getActiveCar().getChassis().getPosition(), new Vector2(x, y)) < 2.5f) {
            activate(player);
            activated = true;
        }

        if(activated) {
            Util.fade(color, new Color(1, 1, 1, 0), 0.1f);
            Vector2 newXY = new Vector2(x, y);
            Util.fade(newXY, new Vector2(x, y + 2), 0.1f);
            x = newXY.x;
            y = newXY.y;
        }

        if(color.a <= 0.01f){
            readyToBeRemoved = true;
        }

        elapsedTime += delta;
    }

    public void draw(SpriteBatch spriteBatch) {
        Color tmpColor = spriteBatch.getColor();
        spriteBatch.setColor(color);

        spriteBatch.draw(animation.getKeyFrame(elapsedTime, false), x, y, width, height);

        spriteBatch.setColor(tmpColor);
    }

    public abstract void activate(Player player);

    public void dispose() {
        textureAtlas.dispose();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isReadyToBeRemoved() {
        return readyToBeRemoved;
    }
}
