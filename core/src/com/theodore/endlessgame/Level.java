package com.theodore.endlessgame;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.theodore.endlessgame.objects.LavaWall;
import com.theodore.endlessgame.objects.ParticleEffect;
import com.theodore.endlessgame.objects.Player;
import com.theodore.endlessgame.objects.powerups.HealthUp;
import com.theodore.endlessgame.objects.powerups.PowerUp;
import com.theodore.endlessgame.screens.MainScreen;
import com.theodore.endlessgame.utils.Terrain;
import com.theodore.endlessgame.screens.SplashScreen;

public class Level {

    public static String SEASON = "grass";

    private Array<Terrain> terrains;
    private Array<PowerUp> powerUps;

    private PolygonSpriteBatch polygonSpriteBatch;

    private Terrain baseTerrain;
    private Player player;

    private Vector2 last;

    private LavaWall lavaWall;
    private GameWorld gameWorld;
    private World world;
    private RayHandler rayHandler;

    private ParticleEffect explosionEffect;
    private Texture[] explTextures = new Texture[3];

    private boolean init = true;
    public boolean clean = false;
    private boolean isDead = false;

    public static int airplanesDestroyed = 0;

    private float i = 0;

    public MyQueryCallback myQueryCallback;

    private String activeCar;

    public Level(World world, GameWorld gameWorld) {
        this.world = world;
        this.gameWorld = gameWorld;
        this.myQueryCallback = new MyQueryCallback();


        this.rayHandler = new RayHandler(world);
        this.rayHandler.setShadows(true);

        this.explosionEffect = new ParticleEffect(new Texture(Gdx.files.internal("explosions/exp0.png")), new Vector2(-100, -100), new Vector2(2f, 2f), new Vector2(0, 5), new Vector2(0, -9f), new Vector2(0, 0), 25, 3f);
        for (int i = 0; i < 3; i++) {
            this.explTextures[i] = SplashScreen.assetManager.get("explosions/exp" + i + ".png");
        }
    }

    boolean destroyedJoints = false;

    public void update(float delta) {
        if (player != null) {
            player.update(delta, gameWorld.camera);
            player.setStarted(gameWorld.started);
            player.setDestroyedJoints(destroyedJoints);
            player.setDead(isDead);
            gameWorld.getHud().setPlayer(player);

            rayHandler.setCombinedMatrix(gameWorld.camera.combined);
            rayHandler.update();

            if (gameWorld.started)
                lavaWall.update(delta);
            lavaWall.setPlayerPosition(player.getActiveCar().chassis.getPosition());

            if (lavaWall.getX() + 20 > player.getActiveCar().chassis.getPosition().x)
                player.damage(-(100 * delta));

            if (player.getHealth() < 1 && !isDead) {
                killPlayer();
            }

            for(int i = 0; i < powerUps.size; i++) {
                powerUps.get(i).update(delta, player);

                if(powerUps.get(i).isReadyToBeRemoved()) {
                    powerUps.removeIndex(i);
                }
            }
        }

        if (terrains != null) {
            if (terrains.size > 0) {
                if ((gameWorld.camera.position.x + 50) > terrains.get(terrains.size - 1).getX()) {
                    addTerrain(world, rayHandler, last);

//                    if(MathUtils.randomBoolean(1)) {
//                        powerUps.add(new HealthUp(last.x, last.y + 0.5f));
//                    }
                }

                if (terrains.get(terrains.size - 1).getLava() != null) {
                    for (int i = 0; i < terrains.size - 5; i++) {
                        GameWorld.BODIES_TO_REMOVE.add(terrains.get(i).getBody());

                        if (terrains.get(i).getWater() != null) {
                            GameWorld.BODIES_TO_REMOVE.add(terrains.get(i).getWater().getBody());
                        }

                        if (terrains.get(i).getLava() != null) {
                            GameWorld.BODIES_TO_REMOVE.add(terrains.get(i).getLava().getBody());
                        }

                        terrains.removeIndex(i);
                    }

                    baseTerrain = null;
                }
            }
        }

        if (init) {
            if (gameWorld.readyToInit) {
                init();

                init = false;
                gameWorld.readyToInit = false;
            }
        }

        if (clean) {
            clearBodies();
            rayHandler.removeAll();
        }

        updateCamera(delta);
    }

    public void draw(SpriteBatch spriteBatch, Color color) {
        spriteBatch.setProjectionMatrix(gameWorld.camera.combined);
        spriteBatch.setColor(color);
        spriteBatch.begin();

        if (player != null) {
            if(gameWorld.started) {
                player.draw(spriteBatch);
            }
            if (isDead) {
                explosionEffect.draw(spriteBatch);
            }
            lavaWall.draw(spriteBatch);

            for(int i = 0; i < powerUps.size; i++)
                powerUps.get(i).draw(spriteBatch);
        }


        spriteBatch.end();

        for (Terrain terrain : terrains) {
            terrain.drawWater(gameWorld.camera.combined, color);
            if (player != null) {
                terrain.drawLava(gameWorld.camera.combined, spriteBatch, color, player.getActiveCar().getChassis().getPosition().x);
            }
        }

        polygonSpriteBatch.setColor(color);
        polygonSpriteBatch.setProjectionMatrix(gameWorld.camera.combined);
        polygonSpriteBatch.begin();
        for (Terrain terrain : terrains)
            terrain.draw(polygonSpriteBatch);
        polygonSpriteBatch.end();

        if(gameWorld.started) {
            rayHandler.render();
        }

        if (isDead && gameWorld.started) {
            if (i < 1.8f) {
                explosionEffect.update(Gdx.graphics.getDeltaTime());
                explosionEffect.setPosition(new Vector2(gameWorld.camera.position.x, gameWorld.camera.position.y));
                explosionEffect.setTexture(explTextures[MathUtils.random(0, 2)]);
                explosionEffect.setVelocity(new Vector2(MathUtils.random(-5, 5), 20));
            }

            if (i > 2) {
                gameWorld.restart();
                gameWorld.getHud().showGameOverPopup(true);
                i = 0;
//                MainScreen.restart = true;
            } else {
                i += 1 * Gdx.graphics.getDeltaTime();
            }
        }
    }

    public void initialize(String activeCar) {
        this.activeCar = activeCar;
        init = true;
    }

    public void clean() {
        clean = true;
    }

    public void updateCamera(float delta) {
        if (player != null) {
            if (player.getActiveCar() != null) {
//                if(!isDead)
                gameWorld.camera.position.lerp(new Vector3(player.getActiveCar().getChassis().getPosition().x, player.getActiveCar().getChassis().getPosition().y, 0), delta * 10);

                Vector3 zV = new Vector3(gameWorld.camera.zoom, 0, 0);

                if (player.getActiveCar().getChassis().getLinearVelocity().x > 15 || player.getActiveCar().getChassis().getLinearVelocity().x < -15) {
//            zV.lerp(new Vector3(3f, 0, 0), delta * 2);
                } else if (player.getActiveCar().getChassis().getLinearVelocity().x > 3 || player.getActiveCar().getChassis().getLinearVelocity().x < -3) {
                    zV.lerp(new Vector3(1.5f, 0, 0), delta * 2);
                } else {
                    zV.lerp(new Vector3(1, 0, 0), delta * 2);
                }
                gameWorld.camera.zoom = zV.x;

                gameWorld.camera.update();
            }

        }
    }

    public void addTerrain(World world, RayHandler rayHandler, Vector2 start) {
        Terrain t = new Terrain(start.x, 0);
        last = t.generateRandomTerrain(world, rayHandler, start.y, terrains.size);
        terrains.add(t);
    }

    public void applyBlastImpulse(Body body, Vector2 blastCenter, Vector2 applyPoint, float blastPower) {
        Vector2 blastDir = applyPoint.sub(blastCenter);
//        float distance = blastDir.Normalize();
        float distance = blastDir.dst(blastDir.nor());
//        float distance = blastDir.len();

//        if(distance != 0) {
        if (distance == 0)
            distance = 1;
        float invDistance = 1 / distance;
        float impulseMag = blastPower * invDistance * invDistance;


        body.applyLinearImpulse(blastDir.scl(impulseMag), applyPoint, true);
//        }
    }


    public void beginContact(Contact contact) {
        if (player != null && !isDead) {
            if (contact.getFixtureA().getBody().getUserData() == "chassis" ||
                    contact.getFixtureB().getBody().getUserData() == "chassis") {
                if (contact.getFixtureA().getBody().getUserData() == "lava" ||
                        contact.getFixtureB().getBody().getUserData() == "lava") {
                    player.melting = true;
                }
            }

            if (contact.getFixtureA().getBody().getUserData() == "leftWheel" ||
                    contact.getFixtureB().getBody().getUserData() == "leftWheel") {
                player.getActiveCar().leftAirborn = false;

                if (contact.getFixtureA().getBody().getUserData() == "water" ||
                        contact.getFixtureB().getBody().getUserData() == "water") {
                    player.leftWheelInWater = true;
                    player.getActiveCar().leftAirborn = true;
                }
            }

            if (contact.getFixtureA().getBody().getUserData() == "rightWheel" ||
                    contact.getFixtureB().getBody().getUserData() == "rightWheel") {
                player.getActiveCar().rightAirborn = false;

                if (contact.getFixtureA().getBody().getUserData() == "water" ||
                        contact.getFixtureB().getBody().getUserData() == "water") {
                    player.rightWheelInWater = true;
                    player.getActiveCar().rightAirborn = true;
                }
            }
        }
    }

    public void endContact(Contact contact) {
        if (player != null && !isDead) {
            if (contact.getFixtureA().getBody().getUserData() == "chassis" ||
                    contact.getFixtureB().getBody().getUserData() == "chassis") {
                if (contact.getFixtureA().getBody().getUserData() == "lava" ||
                        contact.getFixtureB().getBody().getUserData() == "lava") {
                    player.melting = false;
                }
            }

            if (contact.getFixtureA().getBody().getUserData() == "leftWheel" ||
                    contact.getFixtureB().getBody().getUserData() == "leftWheel") {

                player.getActiveCar().leftAirborn = true;

                if (contact.getFixtureA().getBody().getUserData() == "water" ||
                        contact.getFixtureB().getBody().getUserData() == "water") {
                    player.leftWheelInWater = false;
                }
            }
            if (contact.getFixtureA().getBody().getUserData() == "rightWheel" ||
                    contact.getFixtureB().getBody().getUserData() == "rightWheel") {
                player.getActiveCar().rightAirborn = true;

                if (contact.getFixtureA().getBody().getUserData() == "water" ||
                        contact.getFixtureB().getBody().getUserData() == "water") {
                    player.rightWheelInWater = false;
                }
            }
        }
    }

    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    public void postSolve(Contact contact, ContactImpulse impulse) {
        if (player != null && !isDead) {

            if (contact.getFixtureA().getBody().getUserData() == "chassis" ||
                    contact.getFixtureB().getBody().getUserData() == "chassis") {

                for (int i = 0; i < impulse.getCount(); i++) {
                    float damageValue = impulse.getNormalImpulses()[i];

                    if (damageValue > 2) {
                        assert player != null;
                        player.damage(-(damageValue / 10));
                    }
                }

            }
        }
    }

    private void killPlayer() {
        isDead = true;
        player.getActiveCar().chassis.applyLinearImpulse(new Vector2(0, 200), player.getActiveCar().chassis.getPosition(), true);
        destroyedJoints = true;
        player.setDestroyedJoints(destroyedJoints);
        GameWorld.JOINTS_TO_REMOVE.add(player.getActiveCar().leftMotor);
        GameWorld.JOINTS_TO_REMOVE.add(player.getActiveCar().rightMotor);

        int deaths = SplashScreen.save.getInteger("deaths", 0) + 1;
        SplashScreen.save.putInteger("deaths", deaths);
        int meter = player.getMeter();
        if (meter <= 0)
            meter = 0;
        SplashScreen.save.putInteger("lastDistance", meter);
        SplashScreen.save.putInteger("totalDistance", SplashScreen.save.getInteger("totalDistance") + meter);
        SplashScreen.save.putInteger("airplanesDestroyed", SplashScreen.save.getInteger("airplanesDestroyed", 0) + airplanesDestroyed);
        airplanesDestroyed = 0;

        int highscore = SplashScreen.save.getInteger("bestDistance");

        if (player.getMeter() > highscore) {
            highscore = player.getMeter();
            SplashScreen.save.putInteger("bestDistance", highscore);
        }

        SplashScreen.save.putInteger("timePlayed", SplashScreen.save.getInteger("timePlayed") + (int) gameWorld.secondsPlayed);
        gameWorld.secondsPlayed = 0;

        SplashScreen.save.flush();
    }

    private void init() {
        int r = MathUtils.random(0, 4);

        if (r == 0) {
            SEASON = "grass";
            gameWorld.getBackground().getWeather().spawnRate = MathUtils.random(0f, 5f);
        } else if (r == 1) {
            SEASON = "snow";
            gameWorld.getBackground().getWeather().spawnRate = MathUtils.random(0f, 10f);
        } else if (r == 2) {
            SEASON = "dirt";
            gameWorld.getBackground().getWeather().spawnRate = MathUtils.random(0f, 2f);
        } else if (r == 4) {
            SEASON = "rock";
            gameWorld.getBackground().getWeather().spawnRate = MathUtils.random(0f, 3f);
        }


        boolean day = MathUtils.randomBoolean(0.7f);

        if (day) {
            rayHandler.setAmbientLight(1);
        } else {
            rayHandler.setAmbientLight(MathUtils.random(0.3f, 0.6f));
        }


        polygonSpriteBatch = new PolygonSpriteBatch();
        terrains = new Array<Terrain>();
        powerUps = new Array<PowerUp>();

        player = new Player(activeCar, rayHandler, world, 0, 3);
        lavaWall = new LavaWall(rayHandler);

        last = new Vector2(25, 0);

        baseTerrain = new Terrain(0, 0);
        baseTerrain.generateFirstTerrain(world);
        terrains.add(baseTerrain);

        addTerrain(world, rayHandler, last);

        destroyedJoints = false;
    }

    private void clearBodies() {
        if (terrains != null) {
            for (int i = 0; i < terrains.size; i++) {
                GameWorld.BODIES_TO_REMOVE.add(terrains.get(i).getBody());

                if (terrains.get(i).getWater() != null) {
                    GameWorld.BODIES_TO_REMOVE.add(terrains.get(i).getWater().getBody());
                }

                if (terrains.get(i).getLava() != null) {
                    GameWorld.BODIES_TO_REMOVE.add(terrains.get(i).getLava().getBody());
                }

                terrains.removeIndex(i);
            }

            baseTerrain = null;

            if (player != null) {
                GameWorld.BODIES_TO_REMOVE.add(player.getActiveCar().getChassis());
                GameWorld.BODIES_TO_REMOVE.add(player.getActiveCar().getLeftWheel());
                GameWorld.BODIES_TO_REMOVE.add(player.getActiveCar().getRightWheel());
                player = null;
            }

            if (terrains.size <= 0) {
                clean = false;
                GameWorld.CLEAR = true;
            }

        }

    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }

    public void dispose() {
        for (Terrain terrain : terrains)
            terrain.dispose();
        player.dispose();
        explosionEffect.dispose();
        lavaWall.dispose();
        rayHandler.dispose();
    }

    public Player getPlayer() {
        return player;
    }

    public class MyQueryCallback implements QueryCallback {

        public Array<Body> foundBodies = new Array<Body>();

        @Override
        public boolean reportFixture(Fixture fixture) {
            foundBodies.add(fixture.getBody());
            return true;
        }

        public void update(World world, Vector2 lower, Vector2 upper) {
            world.QueryAABB(this, lower.x, lower.y, upper.x, upper.y);
        }
    }

}
