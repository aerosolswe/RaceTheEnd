package com.theodore.endlessgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.theodore.endlessgame.MyGame;
import com.theodore.endlessgame.objects.Background;
import com.theodore.endlessgame.objects.Loader;
import com.theodore.endlessgame.objects.ui.Text;

public class SplashScreen implements Screen {

    public static AssetManager assetManager;
    public static Preferences settings;
    public static Preferences save;

    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;

    private Background background;

    private Loader loader;

    private Color color;

    public static MyGame myGame;

    public SplashScreen(MyGame myGame) {
        SplashScreen.myGame = myGame;
    }

    @Override
    public void show() {
        assetManager = new AssetManager();
        settings = Gdx.app.getPreferences("settings");
        save = Gdx.app.getPreferences("save");

        settings.getFloat("soundVolume", 0);
        settings.getFloat("musicVolume", 0);

        spriteBatch = new SpriteBatch();

        camera = new OrthographicCamera(1280, 720);
        camera.position.set(1280 / 2, 720 / 2, 0);
        camera.update();

        background = new Background(true);
        background.getAirplanes().addPlane(MathUtils.random(100, 1000));

        loader = new Loader(assetManager);
        loader.loadCarTextures();
        loader.loadUI();
        loader.loadParticles();
        loader.loadOther();
        loader.loadSound();

        color = new Color(1, 1, 1, 1);
    }

    @Override
    public void render(float delta) {
        if (loader.isDoneLoading()) {
            if (color.r > 0.05f) {
                color = fade(color, new Color(0, 0, 0, 1), .3f);
            } else if (color.r <= 0.05f) {
                myGame.setScreen(myGame.mainScreen);
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        background.draw(color);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.setColor(color);
        spriteBatch.begin();
        loader.draw(spriteBatch);
        spriteBatch.end();
    }

    public Color fade(Color from, Color to, float time) {

        float t = 0;

        if (t < 1.0f) {
            t += Gdx.graphics.getDeltaTime() / time;
        }

        Color c = from;

        c.set(c.lerp(to, t));

        return c;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        background.dispose();
        assetManager.dispose();
        loader.dispose();
    }
}
