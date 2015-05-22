package com.theodore.endlessgame.objects.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.theodore.endlessgame.Menu;
import com.theodore.endlessgame.utils.Util;
import com.theodore.endlessgame.screens.SplashScreen;

public class MainPopup extends Window {

    private Text bestDistanceText;
    private Text bestDistance;
    private Text lastDistanceText;
    private Text lastDistance;

    public boolean play = false;

    private Vector2 newPos = new Vector2();

    private boolean goIn, goOut;

    public MainPopup(final Menu menu, Skin skin) {
        super("MENU", skin);

        Button playButton = Util.createButton((Texture) SplashScreen.assetManager.get("ui/play_up.png"), (Texture) SplashScreen.assetManager.get("ui/play_down.png"));
        TextButton garageButton = Util.createTextButton("GARAGE");
        garageButton.padBottom(15);
        TextButton statsButton = Util.createTextButton("STATS");
        statsButton.padBottom(15);

        final CheckBox audioButton = Util.createCheckbox("", (Texture) SplashScreen.assetManager.get("ui/sound_switch_on.png"), (Texture) SplashScreen.assetManager.get("ui/sound_switch_off.png"));
        if (SplashScreen.settings.getFloat("soundVolume") == 0)
            audioButton.setChecked(false);
        else
            audioButton.setChecked(true);


        final CheckBox musicButton = Util.createCheckbox("", (Texture) SplashScreen.assetManager.get("ui/music_switch_on.png"), (Texture) SplashScreen.assetManager.get("ui/music_switch_off.png"));
        if (SplashScreen.settings.getFloat("musicVolume") == 0)
            musicButton.setChecked(false);
        else
            musicButton.setChecked(true);


        Button exitButton = Util.createButton((Texture) SplashScreen.assetManager.get("ui/x_button_up.png"), (Texture) SplashScreen.assetManager.get("ui/x_button_down.png"));

//        window.debug();
        setPosition(2000, 720 / 2 - 590 / 2);
        setSize(290, 590);
        setKeepWithinStage(false);
        setMovable(false);
        row().spaceBottom(5);
        add().spaceBottom(5);
        top().row().prefSize(265, 150).spaceBottom(10);
        add(playButton).colspan(3);
        row().prefSize(265, 90).spaceBottom(10);
        add(garageButton).colspan(3);
        row().prefSize(265, 90).spaceBottom(10);
        add(statsButton).colspan(3);
        row().expandY();
        add(audioButton).prefSize(50, 110).align(Align.bottom).space(10);
        add(musicButton).prefSize(50, 110).align(Align.bottomLeft).space(10);
        add(exitButton).prefSize(80, 90).align(Align.bottom).space(10);
        row().spaceBottom(5);
        add().spaceBottom(5);

        final Sound clickSound = SplashScreen.assetManager.get("sound/click.ogg");
        final Sound switchSound = SplashScreen.assetManager.get("sound/switch.ogg");

        playButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                play = true;

                clickSound.play(SplashScreen.settings.getFloat("soundVolume"));
            }
        });

        garageButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                goOut();
                menu.garagePopup.goIn();

                clickSound.play(SplashScreen.settings.getFloat("soundVolume"));
            }
        });

        statsButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                goOut();
                menu.statsPopup.goIn();

                clickSound.play(SplashScreen.settings.getFloat("soundVolume"));
            }
        });

        audioButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (audioButton.isChecked())
                    SplashScreen.settings.putFloat("soundVolume", 1);
                else
                    SplashScreen.settings.putFloat("soundVolume", 0);

                SplashScreen.settings.flush();

                switchSound.play(SplashScreen.settings.getFloat("soundVolume"));
            }
        });

        musicButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (musicButton.isChecked())
                    SplashScreen.settings.putFloat("musicVolume", 1);
                else
                    SplashScreen.settings.putFloat("musicVolume", 0);

                SplashScreen.settings.flush();

                switchSound.play(SplashScreen.settings.getFloat("soundVolume"));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        bestDistanceText = new Text("Best distance", 0, 0);
        bestDistance = new Text("big.fnt", "0m", 0, 0);

        lastDistanceText = new Text("Last distance", 0, 0);
        lastDistance = new Text("big.fnt", "0m", 0, 0);

    }

    public void goIn() {
        newPos.set(1280 / 2 - 290 / 2, getY());
        goOut = false;
        goIn = true;
    }

    public void goOut() {
        newPos.set(-820, getY());
        goIn = false;
        goOut = true;
    }

    public void update(float delta) {
        if (goIn) {
            Vector2 result = Util.fade(new Vector2(getX(), getY()), newPos, 0.3f);
            setPosition(result.x, result.y);

            if (result.equals(newPos)) {
                goIn = false;
            }
        }

        if (goOut) {
            Vector2 result = Util.fade(new Vector2(getX(), getY()), newPos, 0.3f);
            setPosition(result.x, result.y);

            if (result.equals(newPos)) {
                goIn = false;
            }
        }

        bestDistance.setText(SplashScreen.save.getInteger("bestDistance", 0) + "m");
        bestDistance.center(getX() + 500, 720 / 2 + 150);
        bestDistanceText.center(getX() + 500, bestDistance.getY() + 50);

        lastDistance.setText(SplashScreen.save.getInteger("lastDistance", 0) + "m");
        lastDistance.center(getX() + 500, 720 / 2);
        lastDistanceText.center(getX() + 500, lastDistance.getY() + 50);
    }

    public void draw(SpriteBatch spriteBatch) {
        if (isVisible()) {
            bestDistanceText.draw(spriteBatch, spriteBatch.getColor());
            bestDistance.draw(spriteBatch, spriteBatch.getColor());
            lastDistanceText.draw(spriteBatch, spriteBatch.getColor());
            lastDistance.draw(spriteBatch, spriteBatch.getColor());
        }
    }

    public void dispose() {
        bestDistance.dispose();
        bestDistanceText.dispose();
        lastDistance.dispose();
        lastDistanceText.dispose();
    }

}
