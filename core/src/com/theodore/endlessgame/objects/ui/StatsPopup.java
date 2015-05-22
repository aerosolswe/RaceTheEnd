package com.theodore.endlessgame.objects.ui;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.theodore.endlessgame.Menu;
import com.theodore.endlessgame.utils.Util;
import com.theodore.endlessgame.screens.SplashScreen;

import java.text.DecimalFormat;

public class StatsPopup extends Window {

    private Label bestDistanceLabel;
    private Label lastDistanceLabel;
    private Label totalDistanceLabel;
    private Label timePlayedLabel;
    private Label deathsLabel;
    private Label planesDeahtsLabel;

    private DecimalFormat df;

    private Vector2 newPos = new Vector2();

    private boolean goIn, goOut;

    public StatsPopup(final Menu menu, Skin skin) {
        super("STATS", skin);

        Button backButton = Util.createButton((Texture) SplashScreen.assetManager.get("ui/check_button_up.png"), (Texture) SplashScreen.assetManager.get("ui/check_button_down.png"));

        bestDistanceLabel = new Label(SplashScreen.save.getInteger("bestDistance", 0) + "m", skin);
        lastDistanceLabel = new Label(SplashScreen.save.getInteger("lastDistance", 0) + "m", skin);
        totalDistanceLabel = new Label(SplashScreen.save.getInteger("totalDistance", 0) + "m", skin);
        timePlayedLabel = new Label(Util.timeConversion(SplashScreen.save.getInteger("timePlayed", 0)), skin);
        deathsLabel = new Label(SplashScreen.save.getInteger("deaths", 0) + "", skin);
        planesDeahtsLabel = new Label(SplashScreen.save.getInteger("airplanesDestroyed", 0) + "", skin);

//        debug();
        setPosition(2000, 720 / 2 - 590 / 2);
        setSize(500, 590);
        setMovable(false);
        setKeepWithinStage(false);
        row().expandX();
        add("Best distance:").top().left().padLeft(10);
        add(bestDistanceLabel).top().right().padRight(10);
        row();
        add("Last distance:").top().left().padLeft(10);
        add(lastDistanceLabel).top().right().padRight(10);
        row();
        add("Total distance:").top().left().padLeft(10);
        add(totalDistanceLabel).top().right().padRight(10);
        row();
        add("Time played:").top().left().padLeft(10);
        add(timePlayedLabel).top().right().padRight(10);
        row();
        add("Deaths:").top().left().padLeft(10);
        add(deathsLabel).top().right().padRight(10);
        row();
        add("Planes destroyed:").top().left().padLeft(10);
        add(planesDeahtsLabel).top().right().padRight(10);
        row();
        add(backButton).padRight(15).padBottom(15).expand().bottom().right().prefSize(80, 90).colspan(3);

        final Sound clickSound = SplashScreen.assetManager.get("sound/click.ogg");
        final Sound switchSound = SplashScreen.assetManager.get("sound/switch.ogg");

        backButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                clickSound.play(SplashScreen.settings.getFloat("soundVolume"));

                goOut();
                menu.mainPopup.goIn();
            }
        });

        df = new DecimalFormat("###.##");
    }


    public void goIn() {
        newPos.set(1280 / 2 - 500 / 2, getY());
        goOut = false;
        goIn = true;
    }

    public void goOut() {
        newPos.set(2000, getY());
        goIn = false;
        goOut = true;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

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

        int totalDistance = SplashScreen.save.getInteger("totalDistance", 0);
        String td = totalDistance + "m";
        if (totalDistance > 1000) {
            td = df.format(((double) totalDistance / 1000)) + "km";
        }
        totalDistanceLabel.setText(td);

        int bestDistance = SplashScreen.save.getInteger("bestDistance", 0);
        String bd = bestDistance + "m";
        if (bestDistance > 1000) {
            bd = df.format(((double) bestDistance / 1000)) + "km";
        }
        bestDistanceLabel.setText(bd);

        int lastDistance = SplashScreen.save.getInteger("lastDistance", 0);
        String ld = lastDistance + "m";
        if (lastDistance > 1000) {
            ld = df.format(((double) lastDistance / 1000)) + "km";
        }
        lastDistanceLabel.setText(ld);

        timePlayedLabel.setText(Util.timeConversion(SplashScreen.save.getInteger("timePlayed", 0)));
        deathsLabel.setText(SplashScreen.save.getInteger("deaths", 0) + "");
        planesDeahtsLabel.setText(SplashScreen.save.getInteger("airplanesDestroyed", 0) + "");
    }
}
