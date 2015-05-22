package com.theodore.endlessgame.objects.cars;

import box2dLight.ConeLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.theodore.endlessgame.objects.Car;
import com.theodore.endlessgame.screens.SplashScreen;

public class Stationwagon extends Car {

    public static float width = 4;
    public static float height = 1.5f;
    public static float motorSpeed = 30;
    public static float wheelFriction = 70;
    public static float health = 200;

    public Stationwagon(RayHandler rayHandler, World world, String color, String wheel, float x, float y) {
        super(width, height, motorSpeed, wheelFriction, health);

        FixtureDef carDef = new FixtureDef();
        FixtureDef wheelDef = new FixtureDef();

        carDef.density = 5;
        carDef.friction = 0.5f;
        carDef.restitution = .3f;

        wheelDef.density = carDef.density * 3f;
        wheelDef.friction = wheelFriction;
        wheelDef.restitution = .5f;

        setEngineLocation(3);

        Vector2[] shape = new Vector2[]{
                new Vector2(-width / 2, -height / 2),
                new Vector2(width / 2, -height / 2),
                new Vector2(width / 2 * 0.8f, height / 2 * 0.9f),
                new Vector2(-width / 2 * 0.8f, height / 2 * 0.9f)
        };

        float wheelSize = height / 3;

        float wheelY = (-height / 2);

        Vector2 wheelPos1 = new Vector2(-width / 2 * 0.55f, wheelY);
        Vector2 wheelPos2 = new Vector2(width / 2 * 0.7f, wheelY);

        TextureAtlas chassi = SplashScreen.assetManager.get("cars/stationwagon/stationwagon.pack");
        TextureAtlas.AtlasRegion region = chassi.findRegion(color);

        Sprite chassiSprite = new Sprite(region);

        createCarBody(world, carDef, wheelDef, x, y, shape, wheelSize, wheelPos1, wheelPos2, new Vector2(2, -0.1f));
        createSprites(chassiSprite, (Texture) SplashScreen.assetManager.get("cars/wheels/" + wheel + ".png"), new Vector2(wheelSize, wheelSize));

        coneLight = new ConeLight(rayHandler, 1000, new Color(0.98f, 0.92f, 0.53f, 0.5f), 15, 0, 0, 0, 30);
        coneLight.setSoft(false);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (coneLight != null) {
            float direction = this.getLight().getAngle() * MathUtils.radDeg;
            float xl = this.getLight().getPosition().x;
            float yl = this.getLight().getPosition().y;

            coneLight.setDirection(direction);
            coneLight.setPosition(xl, yl);
        }
    }
}
