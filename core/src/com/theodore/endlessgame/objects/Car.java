package com.theodore.endlessgame.objects;

import box2dLight.ConeLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;

public class Car {

    public Body chassis, leftWheel, rightWheel, light;
    public WheelJoint leftMotor, rightMotor;

    private Texture chassisTexture;
    private Texture wheelTexture;

    private Sprite chassisSprite;
    private Sprite leftWheelSprite;
    private Sprite rightWheelSprite;

    public ConeLight coneLight;

    private float width;
    private float height;

    private float motorSpeed;
    private float friction;
    private float health;

    private int engineLocation = 0;

    public boolean movingForward = false;
    public boolean movingBackward = false;
    public boolean leftAirborn = true;
    public boolean rightAirborn = true;

    private boolean flying = false;
    private boolean started = false;
    private boolean destroyedJoints = false;

    public Car(float width, float height, float motorSpeed, float friction, float health) {
        this.width = width;
        this.height = height;
        this.motorSpeed = motorSpeed;
        this.friction = friction;
        this.health = health;
    }

    public void update(float delta) {
        if (chassisSprite != null) {
            chassisSprite.setPosition(chassis.getPosition().x - chassisSprite.getWidth() / 2, chassis.getPosition().y - chassisSprite.getHeight() / 2);
            chassisSprite.setRotation((float) Math.toDegrees(chassis.getAngle()));
            leftWheelSprite.setPosition(leftWheel.getPosition().x - leftWheelSprite.getWidth() / 2, leftWheel.getPosition().y - leftWheelSprite.getHeight() / 2);
            rightWheelSprite.setPosition(rightWheel.getPosition().x - rightWheelSprite.getWidth() / 2, rightWheel.getPosition().y - rightWheelSprite.getHeight() / 2);
        }

        if (light != null) {
            light.setTransform(light.getPosition(), chassis.getAngle());
        }

        /*if (chassis != null && leftWheel != null && leftMotor != null && rightWheel != null && rightMotor != null && started) {
            if (flying) {
                if (Gdx.input.isTouched()) {
                    if (Gdx.input.getX() > (Gdx.graphics.getWidth() / 2)) {
                        chassis.applyAngularImpulse(2, true);
                    } else {
                        chassis.applyAngularImpulse(-2, true);
                    }
                } else {
                    if (Gdx.input.getX() > (Gdx.graphics.getWidth() / 2))
                        movingBackward = false;
                    else
                        movingForward = false;

                }

            } else {
                if (Gdx.input.isTouched()) {
                    if (Gdx.input.getX() > (Gdx.graphics.getWidth() / 2)) {
                        driveForward();
                    } else {
                        driveBackward();
                    }
                } else {
                    if (Gdx.input.getX() > (Gdx.graphics.getWidth() / 2))
                        movingForward = false;
                    else
                        movingBackward = false;
                }
            }


        }*/

        if (!movingForward && !movingBackward) {
            turnEngine(false);
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        if (chassisSprite != null) {
            chassisSprite.setColor(spriteBatch.getColor());
            chassisSprite.draw(spriteBatch);
            leftWheelSprite.setColor(spriteBatch.getColor());
            leftWheelSprite.draw(spriteBatch);
            rightWheelSprite.setColor(spriteBatch.getColor());
            rightWheelSprite.draw(spriteBatch);
        }
    }

    public void driveForward() {
        if (!destroyedJoints) {
            assert leftMotor != null;
            assert rightMotor != null;
            movingForward = true;
            movingBackward = false;
            turnEngine(true);

            if (engineLocation == 1) {
                leftMotor.setMotorSpeed(-motorSpeed);
            } else if (engineLocation == 2) {
                rightMotor.setMotorSpeed(-motorSpeed);
            } else if (engineLocation == 3) {
                leftMotor.setMotorSpeed(-motorSpeed);
                rightMotor.setMotorSpeed(-motorSpeed);
            }
        }
    }

    public void driveBackward() {
        if (!destroyedJoints) {
            assert leftMotor != null;
            assert rightMotor != null;
            movingBackward = true;
            movingForward = false;
            turnEngine(true);
            if (engineLocation == 1) {
                leftMotor.setMotorSpeed(motorSpeed);
            } else if (engineLocation == 2) {
                rightMotor.setMotorSpeed(motorSpeed);
            } else if (engineLocation == 3) {
                leftMotor.setMotorSpeed(motorSpeed);
                rightMotor.setMotorSpeed(motorSpeed);
            }
        }
    }

    public void turnEngine(boolean state) {
        if (!destroyedJoints) {
            assert leftMotor != null;
            assert rightMotor != null;
            leftMotor.enableMotor(state);
            rightMotor.enableMotor(state);
        }
    }

    public Car createLight(RayHandler rayHandler) {

        return this;
    }

    public Car createSprites(Sprite chassisSprite, Texture wheelTexture, Vector2 wheelSize) {
        return createSprites(chassisSprite, wheelTexture, new Vector2(width, height), wheelSize);
    }

    public Car createSprites(Sprite chassisSprite, Texture wheelTexture, Vector2 chassisSize, Vector2 wheelSize) {
        this.chassisSprite = chassisSprite;
        this.chassisSprite.setSize(chassisSize.x, chassisSize.y);
        this.chassisSprite.setOrigin(chassisSize.x / 2, chassisSize.y / 2);

        leftWheelSprite = new Sprite(wheelTexture);
        leftWheelSprite.setSize(wheelSize.x * 2, wheelSize.y * 2);
        leftWheelSprite.setOrigin(wheelSize.x / 2, wheelSize.y / 2);
        rightWheelSprite = new Sprite(wheelTexture);
        rightWheelSprite.setSize(wheelSize.x * 2, wheelSize.y * 2);
        rightWheelSprite.setOrigin(wheelSize.x / 2, wheelSize.y / 2);

        return this;
    }

    public Car createSprites(Sprite chassisSprite, Texture wheelTexture) {
        return createSprites(chassisSprite, wheelTexture, new Vector2(width, height), new Vector2((height / 3.0f) * 2, (height / 3.0f) * 2));
    }

    public Car createCarBody(World world, FixtureDef chassisFixtureDef, FixtureDef lightFixtureDef, float x, float y) {
        return createCarBody(world,
                chassisFixtureDef,
                lightFixtureDef,
                x,
                y,
                new Vector2[]{
                        new Vector2(-width / 2, -height / 2),
                        new Vector2(width / 2, -height / 2),
                        new Vector2(width / 2 * .4f, height / 2),
                        new Vector2(-width / 2 * .8f, height / 2 * .8f)
                },
                height / 3.0f,
                new Vector2(-width / 2 * .79f + (height / 3.5f), -height / 2),
                new Vector2((-width / 2 * .79f + (height / 3.5f)) * -1, -height / 2),
                new Vector2(1, 1)
        );
    }

    public Car createCarBody(World world, FixtureDef chassisFixtureDef, FixtureDef wheelFixtureDef, float x, float y, Vector2[] shape, float wheelRadius, Vector2 wheelPos1, Vector2 wheelPos2, Vector2 lightPos) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        /** Chassis */
        PolygonShape chassisShape = new PolygonShape();
        chassisShape.set(shape);

        chassisFixtureDef.shape = chassisShape;

        chassis = world.createBody(bodyDef);
        chassis.createFixture(chassisFixtureDef);
        chassis.setUserData("chassis");

        /** Light */
        PolygonShape lightShape = new PolygonShape();
        lightShape.setAsBox(0.1f, 0.1f);

        FixtureDef lightFixtureDef = new FixtureDef();

        lightFixtureDef.shape = lightShape;
        lightFixtureDef.isSensor = true;

        light = world.createBody(bodyDef);
        light.createFixture(lightFixtureDef);

        RevoluteJointDef lightJointDef = new RevoluteJointDef();
        lightJointDef.bodyA = chassis;
        lightJointDef.bodyB = light;
        lightJointDef.localAnchorA.set(lightPos);

        world.createJoint(lightJointDef);

        /** wheel */
        CircleShape wheelShape = new CircleShape();
        wheelShape.setRadius(wheelRadius);

        wheelFixtureDef.shape = wheelShape;

        /** left */
        leftWheel = world.createBody(bodyDef);
        leftWheel.createFixture(wheelFixtureDef);
        leftWheel.setUserData("leftWheel");

        /** right */
        rightWheel = world.createBody(bodyDef);
        rightWheel.createFixture(wheelFixtureDef);
        rightWheel.setUserData("rightWheel");

        /** left axis */
        WheelJointDef axisDef = new WheelJointDef();
        axisDef.bodyA = chassis;
        axisDef.bodyB = leftWheel;
        axisDef.localAnchorA.set(wheelPos1);
        axisDef.frequencyHz = chassisFixtureDef.density * 0.7f;
        axisDef.maxMotorTorque = chassisFixtureDef.density * 25;
        axisDef.localAxisA.set(Vector2.Y);

        leftMotor = (WheelJoint) world.createJoint(axisDef);

        /** right axis */
        axisDef.bodyA = chassis;
        axisDef.bodyB = rightWheel;
        axisDef.localAnchorA.set(wheelPos2);

        rightMotor = (WheelJoint) world.createJoint(axisDef);

        return this;
    }

    public void dispose() {
        if (chassisTexture != null) {
            chassisTexture.dispose();
            wheelTexture.dispose();
        }
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button, boolean flying) {

        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button, boolean flying) {

        return false;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getMotorSpeed() {
        return motorSpeed;
    }

    public void setMotorSpeed(float motorSpeed) {
        this.motorSpeed = motorSpeed;
    }

    public Body getChassis() {
        return chassis;
    }

    public Body getLeftWheel() {
        return leftWheel;
    }

    public Body getRightWheel() {
        return rightWheel;
    }

    public WheelJoint getLeftMotor() {
        return leftMotor;
    }

    public WheelJoint getRightMotor() {
        return rightMotor;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getEngineLocation() {
        return engineLocation;
    }

    public void setEngineLocation(int engineLocation) {
        this.engineLocation = engineLocation;
    }

    public Body getLight() {
        return light;
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean destroyedJoints() {
        return destroyedJoints;
    }

    public void setDestroyedJoints(boolean destroyedJoits) {
        this.destroyedJoints = destroyedJoits;
    }
}
