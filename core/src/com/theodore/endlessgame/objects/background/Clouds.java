package com.theodore.endlessgame.objects.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.theodore.endlessgame.screens.SplashScreen;

public class Clouds {

    private Array<Sprite> clouds = new Array<Sprite>();

    private Texture lastCloudTexture;
    private Array<Texture> cloudTextures = new Array<Texture>();

    public Clouds(boolean isLoading) {
        for (int i = 0; i < 4; i++) {
            if (isLoading) {
                cloudTextures.add(new Texture(Gdx.files.internal("background/cloud" + i + ".png")));
                cloudTextures.get(i).setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            } else {
                cloudTextures.add((Texture) SplashScreen.assetManager.get("background/cloud" + i + ".png"));
            }
        }

        lastCloudTexture = cloudTextures.first();

        for (int i = 0; i < 3; i++) {
            generateCloud(MathUtils.random(0, 1000));
        }
    }

    private float counter = 0;

    public void draw(SpriteBatch spriteBatch) {
        for (int i = 0; i < clouds.size; i++) {
            clouds.get(i).setColor(new Color(spriteBatch.getColor().r, spriteBatch.getColor().g, spriteBatch.getColor().b, clouds.get(i).getColor().a));
//            clouds.get(i).setColor(new Color(color.r, color.g, color.b, clouds.get(i).getColor().a));
            clouds.get(i).setPosition(clouds.get(i).getX() - (35 * clouds.get(i).getScaleX()) * Gdx.graphics.getDeltaTime(), clouds.get(i).getY());
            clouds.get(i).draw(spriteBatch);

            if (clouds.get(i).getX() < -300) {
                clouds.removeIndex(i);
            }
        }

        counter -= 1 * Gdx.graphics.getDeltaTime();

        if (counter <= 0) {
            generateCloud(1300);

            counter = MathUtils.random(10, 20);
        }

    }

    public void generateCloud(float x) {
        float i = MathUtils.random(0, 1);
        Texture chosenTexture;

        if (i < 0.25f) {
            chosenTexture = cloudTextures.first();

            if (chosenTexture.equals(lastCloudTexture)) {
                chosenTexture = cloudTextures.get(1);
            }
        } else if (i > 0.25f && i < 0.50f) {
            chosenTexture = cloudTextures.get(1);

            if (chosenTexture.equals(lastCloudTexture)) {
                chosenTexture = cloudTextures.get(2);
            }
        } else if (i > 0.50f && i < 0.75f) {
            chosenTexture = cloudTextures.get(2);

            if (chosenTexture.equals(lastCloudTexture)) {
                chosenTexture = cloudTextures.get(3);
            }
        } else {
            chosenTexture = cloudTextures.get(3);

            if (chosenTexture.equals(lastCloudTexture)) {
                chosenTexture = cloudTextures.first();
            }
        }

        Sprite c = new Sprite(chosenTexture);
        float alpha = MathUtils.random(0.3f, 0.7f);
        c.setScale(alpha);
        c.setPosition(x, MathUtils.random(200, 600));
        c.setAlpha(alpha);
        clouds.add(c);
        lastCloudTexture = chosenTexture;
    }

    public void dispose() {
        for (int i = 0; i < cloudTextures.size; i++) {
            cloudTextures.get(i).dispose();
        }

        lastCloudTexture.dispose();
    }

}
