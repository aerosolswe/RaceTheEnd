package com.theodore.endlessgame.objects.ui;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.theodore.endlessgame.GameWorld;
import com.theodore.endlessgame.Menu;
import com.theodore.endlessgame.objects.Hud;
import com.theodore.endlessgame.screens.MainScreen;
import com.theodore.endlessgame.screens.SplashScreen;
import com.theodore.endlessgame.utils.Util;

import java.text.DecimalFormat;

public class GameOverPopup extends Window {

    private Vector2 newPos = new Vector2();

    private boolean goIn, goOut;

    public GameOverPopup(final Hud hud, Skin skin) {
        super("GAME OVER", skin);

        Button menuButton = Util.createButton((Texture) SplashScreen.assetManager.get("ui/check_button_up.png"), (Texture) SplashScreen.assetManager.get("ui/check_button_down.png"));
        Button retryButton = Util.createButton((Texture) SplashScreen.assetManager.get("ui/back_button_up.png"), (Texture) SplashScreen.assetManager.get("ui/back_button_down.png"));

//        debug();
        setPosition(2000, 720 / 2 - 590 / 2);
        setSize(500, 590);
        setMovable(false);
        setKeepWithinStage(false);
        row().expandX();
        add(retryButton).padBottom(15).padLeft(15).expand().bottom().left().prefSize(80, 90).colspan(3);
        add(menuButton).padBottom(15).padRight(15).expand().bottom().right().prefSize(80, 90).colspan(3);

        final Sound clickSound = SplashScreen.assetManager.get("sound/click.ogg");
        final Sound switchSound = SplashScreen.assetManager.get("sound/switch.ogg");

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                goOut();
                hud.getGameWorld().mainScreen.getMenu().mainPopup.goIn();
            }
        });

        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                goOut();
                hud.showPedals(true);
                hud.getGameWorld().mainScreen.gameWorld.started = true;
                hud.getGameWorld().getLevel().setDead(false);
            }
        });
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
    }
}
