package com.theodore.endlessgame;

import box2dLight.ConeLight;
import box2dLight.DirectionalLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.graphics.ParticleEmitterBox2D;
import com.badlogic.gdx.utils.Array;
import com.theodore.endlessgame.objects.*;
import com.theodore.endlessgame.screens.MainScreen;
import com.theodore.endlessgame.screens.SplashScreen;

public class GameWorld implements ContactListener {

    public static Array<Body> BODIES_TO_REMOVE = new Array<Body>();
    public static Array<Joint> JOINTS_TO_REMOVE = new Array<Joint>();
    public static boolean CLEAR = false;

    public static final float WIDTH = 20;
    public static final float HEIGHT = 11;

    private final float TIMESTEP = 1 / 60f;
    private final int VELOCITYITERATION = 8;
    private final int POSITIONITERATION = 3;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch spriteBatch;
    public OrthographicCamera camera;
    private Level level;
    private Background background;
    private Hud hud;

    /**
     * Light stuff
     */

    public boolean readyToInit = true;
    public boolean started = false;

    private int panButton = 0;

    public float secondsPlayed;

    public MainScreen mainScreen;

    public GameWorld(MainScreen mainScreen) {
        this.mainScreen = mainScreen;

        world = new World(new Vector2(0, -9.81f), true);
        world.setContactListener(this);
        debugRenderer = new Box2DDebugRenderer();
        spriteBatch = new SpriteBatch();

        hud = new Hud(this);

        camera = new OrthographicCamera(WIDTH, HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();

        level = new Level(world, this);
        level.initialize(mainScreen.getMenu().activeCar);
        background = new Background(false);
    }

    public void update(float delta) {
        hud.update(delta);
        if (started) {
            doPhysicsStep(delta);
            if (level.getPlayer() != null)
                background.setPlayerVelocity(level.getPlayer().getActiveCar().chassis.getLinearVelocity());

            secondsPlayed += 1 * delta;
        } else {
            background.setPlayerVelocity(new Vector2(1, 1));
        }

        level.update(delta);
    }

    public void draw(Color color) {
        background.draw(color);

        level.draw(spriteBatch, color);

//        debugRenderer.render(world, camera.combined);

        for (int i = 0; i < BODIES_TO_REMOVE.size; i++) {
            world.destroyBody(BODIES_TO_REMOVE.get(i));
            BODIES_TO_REMOVE.removeIndex(i);
        }

        for (int i = 0; i < JOINTS_TO_REMOVE.size; i++) {
            world.destroyJoint(JOINTS_TO_REMOVE.get(i));
            JOINTS_TO_REMOVE.removeIndex(i);
        }
    }

    public void drawHud(Color color) {
        hud.draw(spriteBatch, color);
    }

    public void clean() {
        if (CLEAR) {
            for (int i = 0; i < BODIES_TO_REMOVE.size; i++) {
                world.destroyBody(BODIES_TO_REMOVE.get(i));
                BODIES_TO_REMOVE.removeIndex(i);
            }

            for (int i = 0; i < JOINTS_TO_REMOVE.size; i++) {
                world.destroyJoint(JOINTS_TO_REMOVE.get(i));
                JOINTS_TO_REMOVE.removeIndex(i);
            }

            if (BODIES_TO_REMOVE.size <= 0 && JOINTS_TO_REMOVE.size <= 0) {
                CLEAR = false;
                readyToInit = true;
            }
        }
    }

    public void start() {
        started = true;

        level.clean();
        level.initialize(mainScreen.getMenu().activeCar);
    }

    public void restart() {
//        MainScreen.restart = true;
        hud.showPedals(false);
        started = false;
//        mainScreen.getMenu().mainPopup.setVisible(true);

        level.clean();
        level.initialize(mainScreen.getMenu().activeCar);
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (level.getPlayer() != null) {
            if (started) {
                level.getPlayer().touchDown(screenX, screenY, pointer, button);
            }
        }

        if (button == 2) {
            panButton = button;
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (level.getPlayer() != null) {
            if (started) {
                level.getPlayer().touchUp(screenX, screenY, pointer, button);
            }
        }

        if (button == 2) {
            panButton = 0;
        }
        return false;
    }

    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (panButton == 2) {
            camera.position.set(camera.position.x - deltaX / 10, camera.position.y + deltaY / 10, 0);
            camera.update();
        }
        return false;
    }


    public boolean scrolled(int amount) {
        camera.zoom += (float) amount / 15;
        camera.update();
        System.out.println(camera.zoom);
        return false;
    }

    @Override
    public void beginContact(Contact contact) {
        level.beginContact(contact);
    }

    @Override
    public void endContact(Contact contact) {
        level.endContact(contact);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        level.preSolve(contact, oldManifold);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        level.postSolve(contact, impulse);
    }

    public void dispose() {
        spriteBatch.dispose();
        world.dispose();
        debugRenderer.dispose();
        level.dispose();
        background.dispose();
        hud.dispose();
    }

    private float accumulator = 0;

    private void doPhysicsStep(float deltaTime) {
        world.step(deltaTime, VELOCITYITERATION, POSITIONITERATION);
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
//        float frameTime = Math.min(deltaTime, 0.25f);
        /*accumulator += deltaTime; // frameTime
        while (accumulator >= TIMESTEP) {
            world.step(TIMESTEP, VELOCITYITERATION, POSITIONITERATION);
            accumulator -= TIMESTEP;
        }*/
    }

    public Level getLevel() {
        return level;
    }

    public Background getBackground() {
        return background;
    }

    public Hud getHud() {
        return hud;
    }
}
