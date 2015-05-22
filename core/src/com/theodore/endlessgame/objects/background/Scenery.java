package com.theodore.endlessgame.objects.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.theodore.endlessgame.Level;
import com.theodore.endlessgame.screens.SplashScreen;

public class Scenery {

    private Array<Sprite> sceneries = new Array<Sprite>();

    private Array<Texture> textures = new Array<Texture>();

    private Texture lastTexture;

    public Scenery(boolean isLoading) {
        for (int i = 0; i < 3; i++) {
            if (isLoading) {
                textures.add(new Texture(Gdx.files.internal("background/mountain" + i + ".png")));
                textures.get(i).setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            } else {
                textures.add((Texture) SplashScreen.assetManager.get("background/mountain" + i + ".png"));
            }
        }

        lastTexture = textures.first();

        for (int i = 0; i < 7; i++) {
            if (i != 0) {
                generateScenery(i * 200, 0);
            } else if (i == 0) {
                generateScenery(-200, 0);
            } else if (i == 1) {
                generateScenery(0, 0);
            }
        }
    }

    public void draw(SpriteBatch spriteBatch, Vector2 playerVelocity) {
        for (int i = 0; i < sceneries.size; i++) {
            sceneries.get(i).setColor(new Color(spriteBatch.getColor().r, spriteBatch.getColor().g, spriteBatch.getColor().b, sceneries.get(i).getColor().a));
//            sceneries.get(i).setColor(new Color(color.r, color.g, color.b, sceneries.get(i).getColor().a));
            sceneries.get(i).setPosition(sceneries.get(i).getX() - (playerVelocity.x / 50), sceneries.get(i).getY());
            sceneries.get(i).draw(spriteBatch);

            if (sceneries.get(sceneries.size - 1).getX() < 1200) {
                generateScenery(1400, 0);
            }

            if (sceneries.get(i).getX() < -1000) {
                sceneries.removeIndex(i);
            }
        }

    }

    public void generateScenery(float x, int textureIndex) {
        float i = MathUtils.random(0, 1);
        Texture chosenTexture;

        if (i < 0.33f) {
            chosenTexture = textures.get(textureIndex);

            if (chosenTexture.equals(lastTexture)) {
                chosenTexture = textures.get(textureIndex + 1);
            }
        } else if (i > 0.33f && i < 0.66f) {
            chosenTexture = textures.get(textureIndex + 1);

            if (chosenTexture.equals(lastTexture)) {
                chosenTexture = textures.get(textureIndex + 2);
            }
        } else {
            chosenTexture = textures.get(textureIndex + 2);

            if (chosenTexture.equals(lastTexture)) {
                chosenTexture = textures.get(textureIndex);
            }
        }

        Sprite c = new Sprite(chosenTexture);
        c.setPosition(x, MathUtils.random(-50, 0));
        sceneries.add(c);
        lastTexture = chosenTexture;
    }

    public void dispose() {
        for (int i = 0; i < textures.size; i++) {
            textures.get(i).dispose();
        }
        lastTexture.dispose();
    }

}
