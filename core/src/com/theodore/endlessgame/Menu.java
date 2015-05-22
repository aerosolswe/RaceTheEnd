package com.theodore.endlessgame;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.theodore.endlessgame.objects.Player;
import com.theodore.endlessgame.objects.ui.GaragePopup;
import com.theodore.endlessgame.objects.ui.MainPopup;
import com.theodore.endlessgame.objects.ui.StatsPopup;
import com.theodore.endlessgame.objects.ui.Text;
import com.theodore.endlessgame.screens.MainScreen;
import com.theodore.endlessgame.screens.SplashScreen;

public class Menu {

    public static Array<Body> BODIES_TO_REMOVE = new Array<Body>();

    private final float TIMESTEP = 1 / 60f;
    private final int VELOCITYITERATION = 8;
    private final int POSITIONITERATION = 3;

    private Array<Text> texts = new Array<Text>();

    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;
    private Color color;

    private Text fpsText;
    private Texture fadeTexture;

    public MainPopup mainPopup;
    public GaragePopup garagePopup;
    public StatsPopup statsPopup;

    private World world;
    private Player player;

    private boolean init = false;
    private boolean clean = false;
    public boolean clear = false;
    public boolean readyToInit = false;

    private Skin skin;
    private Stage stage;
    public MainScreen mainScreen;

    public String activeCar;

    private boolean firstTime = true;

    public Menu(MainScreen mainScreen) {
        this.mainScreen = mainScreen;

        activeCar = SplashScreen.save.getString("activeCar", "pickup");

        camera = new OrthographicCamera(1280, 720);
        camera.position.set(1280 / 2, 720 / 2, 0);
        camera.update();

        spriteBatch = new SpriteBatch();
        color = new Color(1, 1, 1, 1);

        fpsText = new Text("Fps: " + 60, 50, 50);

        texts.add(fpsText);

        world = new World(new Vector2(0, -9.81f), true);
        createGround();
        player = new Player(activeCar, new RayHandler(world), world, 7.5f, 7.5f);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        stage = new Stage(new StretchViewport(1280, 720));

        mainPopup = new MainPopup(this, skin);
        garagePopup = new GaragePopup(this, skin);
        statsPopup = new StatsPopup(this, skin);

        stage.addActor(mainPopup);
        stage.addActor(garagePopup);
        stage.addActor(statsPopup);

        fadeTexture = SplashScreen.assetManager.get("ui/fade.png");

    }

    public void update(float delta) {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        mainPopup.update(delta);

        if(firstTime) {
            mainPopup.goIn();
            firstTime = false;
        }

        if (mainPopup.play) {
            mainScreen.gameWorld.started = true;
            mainScreen.gameWorld.getHud().showPedals(true);
            mainPopup.goOut();
            mainPopup.play = false;
            mainScreen.gameWorld.getLevel().setDead(false);
        }

        if (init) {
            if (readyToInit) {
                init();

                init = false;
                readyToInit = false;
            }
        }

        if (clean) {
            clearPlayer();
        }

        fpsText.setText("Fps: " + Gdx.graphics.getFramesPerSecond());
    }

    public void draw() {
        spriteBatch.setColor(color);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        if (!mainScreen.gameWorld.started)
            spriteBatch.draw(fadeTexture, 0, 0);

        mainPopup.draw(spriteBatch);

        for (Text text : texts)
            text.draw(spriteBatch, color);
        spriteBatch.end();

        if(!mainScreen.gameWorld.started){
            stage.draw();
        }
    }

    public void init() {
        mainPopup.setVisible(true);
        player = new Player(activeCar, new RayHandler(world), world, 7.5f, 7.5f);
    }

    public void clearPlayer() {
        if (player != null) {
            BODIES_TO_REMOVE.add(player.getActiveCar().getChassis());
            BODIES_TO_REMOVE.add(player.getActiveCar().getLeftWheel());
            BODIES_TO_REMOVE.add(player.getActiveCar().getRightWheel());
            player = null;


            clean = false;
            clear = true;
        }
    }

    public void cleanUp() {
        if (clear) {
            for (int i = 0; i < BODIES_TO_REMOVE.size; i++) {
                world.destroyBody(BODIES_TO_REMOVE.get(i));
                BODIES_TO_REMOVE.removeIndex(i);
            }

            if (BODIES_TO_REMOVE.size <= 0) {
                clear = false;
                readyToInit = true;
            }
        }
    }


    public void initialize() {
        init = true;
    }

    public void clean() {
        clean = true;
    }

    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    public void dispose() {
        mainPopup.dispose();
        fadeTexture.dispose();
        world.dispose();
        skin.dispose();
        stage.dispose();

        for (Text text : texts)
            text.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    private void createGround() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(7.5f, 5));

        Body groundBody = world.createBody(groundBodyDef);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(40, 1);
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();
    }
}
