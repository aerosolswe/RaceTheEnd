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

public class Airplane {

    private Texture airplaneTexture;
    private Texture particleTexture;
    private TextureAtlas explosionAtlas;
    private Animation explosionAnimation;

    private Sprite airplaneSprite;

    private ParticleEffect particleEffect;

    private OrthographicCamera camera;

    private boolean dead;
    private boolean readyToBeRemoved;
    private boolean spawnAirdrop;
    private boolean tryToSpawnAirdrop = true;

    private float elapsedTime = 0;

    public Airplane(Texture airplaneTexture, float x, boolean isLoading) {
        this.airplaneTexture = airplaneTexture;

        dead = false;
        readyToBeRemoved = false;

        camera = new OrthographicCamera(1280, 720);
        camera.position.set(1280 / 2, 720 / 2, 0);
        camera.update();

        airplaneSprite = new Sprite(airplaneTexture);
        airplaneSprite.setPosition(x, MathUtils.random(450, 650));
        airplaneSprite.setScale(MathUtils.random(0.1f, 0.2f));


        if (isLoading) {
            particleTexture = new Texture(Gdx.files.internal("particles/smoke.png"));
            explosionAtlas = new TextureAtlas(Gdx.files.internal("explosions/explosion0.pack"));
        } else {
            particleTexture = SplashScreen.assetManager.get("particles/smoke.png");
            explosionAtlas = SplashScreen.assetManager.get("explosions/explosion0.pack");
        }

        particleEffect = new ParticleEffect(particleTexture, new Vector2(airplaneSprite.getX(), airplaneSprite.getY()), new Vector2(10, 3), new Vector2(5, 0), new Vector2(0, 0), new Vector2(1, 1), 5, 5);

        for (int i = 0; i < explosionAtlas.getRegions().size; i++) {
            explosionAtlas.getRegions().get(i).getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        explosionAnimation = new Animation(0.02f, explosionAtlas.getRegions());
    }

    private float i = 0;

    public void draw(SpriteBatch spriteBatch) {

        if (getMouse().x > airplaneSprite.getX() && getMouse().x < airplaneSprite.getX() + airplaneSprite.getWidth()
                && getMouse().y > airplaneSprite.getY() && getMouse().y < airplaneSprite.getY() + airplaneSprite.getHeight()
                && Gdx.input.justTouched()) {
            dead = true;
        }

        if (dead) {
            elapsedTime += Gdx.graphics.getDeltaTime();
            spriteBatch.draw(explosionAnimation.getKeyFrame(elapsedTime, false), airplaneSprite.getX() + 100, airplaneSprite.getY() + 15, explosionAtlas.getRegions().get(0).getRegionWidth() / 2, explosionAtlas.getRegions().get(0).getRegionHeight() / 2);

            if (i > 1.5f) {
                i = 0;
                readyToBeRemoved = true;
                dead = false;
            } else {
                i += 1 * Gdx.graphics.getDeltaTime();
            }
        } else {
            if(airplaneSprite.getX() > 255) {
                if(tryToSpawnAirdrop) {
                    if (i > 1) {
                        i = 0;
                        spawnAirdrop = !MathUtils.randomBoolean(0.005f);
                        tryToSpawnAirdrop = false;
                    } else {
                        i += 1 * Gdx.graphics.getDeltaTime();
                    }
                }
            }

            particleEffect.update(Gdx.graphics.getDeltaTime());
            particleEffect.setPosition(new Vector2(airplaneSprite.getX() + 125, airplaneSprite.getY() + 27.5f));

            airplaneSprite.setColor(new Color(spriteBatch.getColor().r, spriteBatch.getColor().g, spriteBatch.getColor().b, airplaneSprite.getColor().a));
            airplaneSprite.setPosition(airplaneSprite.getX() + ((200 * Gdx.graphics.getDeltaTime()) * airplaneSprite.getScaleX()), airplaneSprite.getY());

            particleEffect.draw(spriteBatch);
            airplaneSprite.draw(spriteBatch);
        }

    }

    private Vector2 getMouse() {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);

        return new Vector2(mouse.x, mouse.y);
    }

    public void dispose() {
        airplaneTexture.dispose();
        particleEffect.dispose();
        explosionAtlas.dispose();
        particleTexture.dispose();
    }

    public Texture getAirplaneTexture() {
        return airplaneTexture;
    }

    public void setAirplaneTexture(Texture airplaneTexture) {
        this.airplaneTexture = airplaneTexture;
    }

    public Sprite getAirplaneSprite() {
        return airplaneSprite;
    }

    public void setAirplaneSprite(Sprite airplaneSprite) {
        this.airplaneSprite = airplaneSprite;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isReadyToBeRemoved() {
        return readyToBeRemoved;
    }

    public boolean spawnAirdrop() {
        return spawnAirdrop;
    }

    public void setSpawnAirdrop(boolean spawnAirdrop) {
        this.spawnAirdrop = spawnAirdrop;
    }
}
