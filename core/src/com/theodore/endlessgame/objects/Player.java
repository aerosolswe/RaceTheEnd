package com.theodore.endlessgame.objects;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.theodore.endlessgame.GameWorld;
import com.theodore.endlessgame.Level;
import com.theodore.endlessgame.screens.SplashScreen;
import com.theodore.endlessgame.objects.cars.*;

public class Player {

    private Car activeCar;
    public boolean leftWheelInWater = false;
    public boolean rightWheelInWater = false;

    public boolean melting = false;

    private ParticleEffect exhaustEffect;
    private ParticleEffectPhys leftWheelEffect;

    private float health;
    private float maxHealth;

    private int meter;

    private World world;

    public Shaker shaker;

    private boolean flying = false;
    private boolean started = false;
    private boolean destroyedJoints = false;
    private boolean isDead = false;

    public Player(String carName, RayHandler rayHandler, World world, float x, float y) {
        String color;
        String wheelType = "basic";
        this.world = world;

        String car = carName.toLowerCase();

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


        activeCar = new Car(1, 1, 1, 1, 1);

        if (car.equals("pickup")) {
            activeCar = new Pickup(rayHandler, world, color, wheelType, x, y);
            health = Pickup.health;
            maxHealth = Pickup.health;
        } else if (car.equals("offroad")) {
            activeCar = new Offroad(rayHandler, world, color, wheelType, x, y);
            health = Offroad.health;
            maxHealth = Offroad.health;
        } else if (car.equals("stationwagon")) {
            activeCar = new Stationwagon(rayHandler, world, color, wheelType, x, y);
            health = Stationwagon.health;
            maxHealth = Stationwagon.health;
        } else if (car.equals("mini")) {
            activeCar = new Mini(rayHandler, world, color, wheelType, x, y);
            health = Mini.health;
            maxHealth = Mini.health;
        } else if (car.equals("coupe")) {
            activeCar = new Coupe(rayHandler, world, color, wheelType, x, y);
            health = Coupe.health;
            maxHealth = Coupe.health;
        } else if (car.equals("fastsport")) {
            activeCar = new FastSport(rayHandler, world, color, wheelType, x, y);
            health = FastSport.health;
            maxHealth = FastSport.health;
        } else if (car.equals("hatchback")) {
            activeCar = new Hatchback(rayHandler, world, color, wheelType, x, y);
            health = Hatchback.health;
            maxHealth = Hatchback.health;
        } else if (car.equals("race")) {
            activeCar = new Race(rayHandler, world, color, wheelType, x, y);
            health = Race.health;
            maxHealth = Race.health;
        } else if (car.equals("sport")) {
            activeCar = new Sport(rayHandler, world, color, wheelType, x, y);
            health = Sport.health;
            maxHealth = Sport.health;
        }

        melting = false;

        meter = 0;

        exhaustEffect = new ParticleEffect("particles/exhaustsmoke.png", new Vector2(0, 0), new Vector2(0.5f, 0.5f), new Vector2(-3f, 0.5f), new Vector2(), new Vector2(1, 1), 10, 0.5f);

        String particle = Level.SEASON;
        if (particle.equals("grass")) {
            particle = "dirt";
        }
        leftWheelEffect = new ParticleEffectPhys(world, "particles/" + particle + ".png", new Vector2(0, 0), new Vector2(0.25f, 0.25f), 2, 2f, new Vector2(-5f, 5f));

        shaker = new Shaker();
    }

    public void update(float delta, Camera camera) {
        if (activeCar != null) {
            activeCar.update(delta);
            activeCar.setDestroyedJoints(destroyedJoints);

            flying = activeCar.leftAirborn && activeCar.rightAirborn;
            activeCar.setFlying(flying);
            activeCar.setStarted(started);

            Vector2 lWheelPos = activeCar.getLeftWheel().getPosition();
            lWheelPos = lWheelPos.add(-0.5f, -0.3f);

            if (!activeCar.leftAirborn && activeCar.getLeftWheel().getLinearVelocity().len() > 2) {
                leftWheelEffect.setPosition(lWheelPos);
            } else {
                lWheelPos = new Vector2(-100, -100);
                leftWheelEffect.setPosition(lWheelPos);
            }

            leftWheelEffect.update(delta);

            exhaustEffect.setPosition(activeCar.getLeftWheel().getPosition());
            exhaustEffect.update(delta);


            if (melting) {
                damage(-100 * delta);
            }

            if (health > 0)
                meter = (int) activeCar.chassis.getPosition().x;

            activeCar.coneLight.setActive(!isDead);

            if (health <= 0) {
                shaker.shake(0.5f, 0.2f);
            }

            if (leftWheelInWater) {
                activeCar.getLeftWheel().applyForceToCenter(0, 200, true);
            }

            if (rightWheelInWater) {
                activeCar.getRightWheel().applyForceToCenter(0, 200, true);
            }

            shaker.update(delta, camera);
        }

    }

    public void damage(float amount) {
        health += amount;

        if (health < 1) {
            health = 0;
        }

        if(health >= maxHealth)
            health = maxHealth;

        if (amount < 0) {
            shaker.shake(amount / 35f, 0.3f);
        }

    }

    public void draw(SpriteBatch spriteBatch) {
        if (activeCar != null) {
            exhaustEffect.draw(spriteBatch);
            leftWheelEffect.draw(spriteBatch);
            activeCar.draw(spriteBatch);
        }
    }

    public void dispose() {
        if (activeCar != null) activeCar.dispose();
        exhaustEffect.dispose();
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (activeCar != null)
            activeCar.touchDown(screenX, screenY, pointer, button, activeCar.leftAirborn && activeCar.rightAirborn);
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (activeCar != null)
            activeCar.touchUp(screenX, screenY, pointer, button, activeCar.leftAirborn && activeCar.rightAirborn);
        return false;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }

    public Car getActiveCar() {
        return activeCar;
    }

    public void setActiveCar(Car activeCar) {
        this.activeCar = activeCar;
    }

    public float getHealth() {
        return health;
    }

    public int getMeter() {
        return meter;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public boolean isDestroyedJoints() {
        return destroyedJoints;
    }

    public void setDestroyedJoints(boolean destroyedJoints) {
        this.destroyedJoints = destroyedJoints;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }
}
