package com.theodore.endlessgame.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.theodore.endlessgame.GameWorld;
import com.theodore.endlessgame.objects.ui.GameOverPopup;
import com.theodore.endlessgame.objects.ui.Text;
import com.theodore.endlessgame.screens.SplashScreen;
import com.theodore.endlessgame.utils.Util;

public class Hud {

    private OrthographicCamera camera;

    private Button throttlePedal;
    private Button brakePedal;
    private Stage stage;
    private Skin skin;

    private Text healthText;
    private Text meterText;

    private float playerHealth = 200;
    private float playerMaxHealth = 200;
    private int meter = 0;
    private int lastMeter = 0;

    private Player player;
    private GameOverPopup gameOverPopup;

    private boolean rotateForwards = false;
    private boolean rotateBackwards = false;

    private GameWorld gameWorld;

    public Hud(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        camera = new OrthographicCamera(1280, 720);
        camera.position.set(1280 / 2, 720 / 2, 0);
        camera.update();

        stage = new Stage(new StretchViewport(1280, 720));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        throttlePedal = Util.createButton((Texture) SplashScreen.assetManager.get("ui/throttle_up.png"), (Texture) SplashScreen.assetManager.get("ui/throttle_down.png"));
        throttlePedal.setPosition(1280 - 240, 720 / 2 - 260 / 2);
        throttlePedal.setSize(150, 202);
        throttlePedal.setVisible(false);
        brakePedal = Util.createButton((Texture) SplashScreen.assetManager.get("ui/brake_up.png"), (Texture) SplashScreen.assetManager.get("ui/brake_down.png"));
        brakePedal.setPosition(60, 720 / 2 - 260 / 2);
        brakePedal.setSize(150, 202);
        brakePedal.setVisible(false);


        gameOverPopup = new GameOverPopup(this, skin);

        stage.addActor(brakePedal);
        stage.addActor(throttlePedal);
        stage.addActor(gameOverPopup);

        throttlePedal.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (player != null)
                    player.getActiveCar().driveForward();

                rotateBackwards = true;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (player != null)
                    player.getActiveCar().movingForward = false;

                rotateBackwards = false;
                super.touchUp(event, x, y, pointer, button);
            }
        });

        brakePedal.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (player != null)
                    player.getActiveCar().driveBackward();

                rotateForwards = true;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (player != null)
                    player.getActiveCar().movingBackward = false;

                rotateForwards = false;
                super.touchUp(event, x, y, pointer, button);
            }
        });

        healthText = new Text("%" + 200, 1280 / 2, 75);
        healthText.setVisible(false);
        meterText = new Text("big.fnt", "" + meter + "m", 1280 / 2, 720 - 25);
        meterText.getFont().setColor(Color.YELLOW);
        meterText.setVisible(false);
    }

//    float scale = 1;

    Vector2 scale = new Vector2(1, 1);

    public void update(float delta) {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);

        if(gameWorld.started) {
            if (player != null) {
                playerHealth = player.getHealth();
                playerMaxHealth = player.getMaxHealth();
                if (player.getMeter() >= 0) {
                    lastMeter = meter;
                    meter = player.getMeter();
                } else {
                    meter = 0;
                }


                if(meter > lastMeter) {
                    scale.lerp(new Vector2(1.1f, 1.1f), delta * 100);
                }else{
                    scale.lerp(new Vector2(1.0f, 1.0f), delta * 10);
                }

                meterText.getFont().setScale(scale.x);

                if (player.isFlying() && rotateBackwards) {
                    player.getActiveCar().chassis.applyAngularImpulse(2, true);
                }

                if (player.isFlying() && rotateForwards) {
                    player.getActiveCar().chassis.applyAngularImpulse(-2, true);
                }

                throttlePedal.setVisible(!player.isDead());
                brakePedal.setVisible(!player.isDead());
            }
        }

        float h = (playerHealth / playerMaxHealth) * 100;

        if (h > 0 && h < 1) {
            h = 1;
        }

        healthText.setText("%" + (int) h);
        healthText.center(1280 / 2, 125);

//        if(meter >= 1000) {
//            meter = meter/1000;
//            meterText.setText("" + meter + "km");
//        } else {
        meterText.setText("" + meter + "m");
//        }
        meterText.center(1280 / 2, 720 - 25);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    }

    public void showGameOverPopup(boolean value) {
        if(value) {
            gameOverPopup.goIn();
        } else {
            gameOverPopup.goOut();
        }
    }

    public void showPedals(boolean value) {
        throttlePedal.setVisible(value);
        brakePedal.setVisible(value);
        healthText.setVisible(value);
        meterText.setVisible(value);
    }

    public void draw(SpriteBatch spriteBatch, Color color) {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.setColor(color);
        spriteBatch.begin();
        meterText.draw(spriteBatch, Color.YELLOW);
        if (playerHealth != 0)
            healthText.draw(spriteBatch, color);
        spriteBatch.end();

        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        healthText.dispose();
        skin.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }
}
