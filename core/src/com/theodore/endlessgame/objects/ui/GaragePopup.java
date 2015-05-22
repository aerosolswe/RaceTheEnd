package com.theodore.endlessgame.objects.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.theodore.endlessgame.GameWorld;
import com.theodore.endlessgame.Menu;
import com.theodore.endlessgame.screens.MainScreen;
import com.theodore.endlessgame.utils.Util;
import com.theodore.endlessgame.screens.SplashScreen;

public class GaragePopup extends Window {
    Object[] listEntries = {"Pickup", "Offroad", "StationWagon", "Hatchback",
            "Mini", "Sport", "Coupe", "FastSport", "Race"};

    private Image carImage;

    private Vector2 newPos = new Vector2();

    private boolean goIn, goOut;

    public GaragePopup(final Menu menu, final Skin skin) {
        super("GARAGE", skin);

        Button backButton = Util.createButton((Texture) SplashScreen.assetManager.get("ui/check_button_up.png"), (Texture) SplashScreen.assetManager.get("ui/check_button_down.png"));

        String ac = SplashScreen.save.getString("activeCar");

        final List list = new List(skin);
        list.setItems(listEntries);
        list.setSelected(ac);
        final ScrollPane scrollPane = new ScrollPane(list, skin);

        String color;
        float r = MathUtils.random();

        if (r < 0.125f)
            color = "green";
        else if (r > 0.125f && r < 0.25f)
            color = "grey";
        else if (r > 0.25f && r < 0.375)
            color = "orange";
        else if (r > 0.375 && r < 0.5)
            color = "purple";
        else if (r > 0.5 && r < 0.625)
            color = "red";
        else if (r > 0.625 && r < 0.75)
            color = "yellow";
        else
            color = "blue";

        if (ac.isEmpty())
            ac = "pickup";

//        carTexture = SplashScreen.assetManager.get("cars/" + ac.toLowerCase() + "/" + color + ".png");
        TextureAtlas carAtlas = SplashScreen.assetManager.get("cars/" + ac.toLowerCase() + "/" + ac.toLowerCase() + ".pack");
        TextureAtlas.AtlasRegion region = carAtlas.findRegion(color);
        carImage = new Image();
        carImage.setScaling(Scaling.fit);
        carImage.setDrawable(new SpriteDrawable(new Sprite(region)));

        SplitPane splitPane = new SplitPane(carImage, scrollPane, true, skin);

        setPosition(2000, 720 / 2 - 590 / 2);
        setSize(500, 590);
        setMovable(false);
        setKeepWithinStage(false);
        add(splitPane).top().prefSize(500, 400).expandX();
//        add(scrollPane).left().top().prefSize(250, 400).expandX();
//        add(carImage).right().top().prefSize(250, 400).expandX();
        row();
        add(backButton).bottom().right().prefSize(80, 90).pad(5).colspan(3);

        final Sound clickSound = SplashScreen.assetManager.get("sound/click.ogg");
        final Sound switchSound = SplashScreen.assetManager.get("sound/switch.ogg");

        scrollPane.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play(SplashScreen.settings.getFloat("soundVolume"));
                String color;
                float r = MathUtils.random();

                if (r < 0.125f)
                    color = "green";
                else if (r > 0.125f && r < 0.25f)
                    color = "grey";
                else if (r > 0.25f && r < 0.375)
                    color = "orange";
                else if (r > 0.375 && r < 0.5)
                    color = "purple";
                else if (r > 0.5 && r < 0.625)
                    color = "red";
                else if (r > 0.625 && r < 0.75)
                    color = "yellow";
                else
                    color = "blue";

                String selectedCar = (String) list.getSelected();
                TextureAtlas carAtlas = SplashScreen.assetManager.get("cars/" + selectedCar.toLowerCase() + "/" + selectedCar.toLowerCase() + ".pack");
                TextureAtlas.AtlasRegion region = carAtlas.findRegion(color);
                carImage.setDrawable(new SpriteDrawable(new Sprite(region)));
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                clickSound.play(SplashScreen.settings.getFloat("soundVolume"));
                String car = ((String) list.getSelected());
                menu.activeCar = car;
                SplashScreen.save.putString("activeCar", car);
                SplashScreen.save.flush();

                goOut();
                menu.mainPopup.goIn();
                menu.mainScreen.gameWorld.restart();
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
