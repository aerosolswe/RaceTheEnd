package com.theodore.endlessgame.utils;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.theodore.endlessgame.Level;
import com.theodore.endlessgame.objects.Lava;
import com.theodore.endlessgame.objects.Water;
import com.theodore.endlessgame.screens.SplashScreen;

public class Terrain {

    public static float LOW_LEVEL = -50;

    private PolygonRegion polygonRegion;
    private PolygonRegion polygonRegion0;
    private Texture inner;
    private Texture outer;

    private Water water;
    private Lava lava;

    private Body body;

    private float[] array;

    private float x;
    private float y;

    private String innerString = "grass";
    private String outerString = "dirt";

    public Terrain(float x, float y) {
        this.x = x;
        this.y = y;

        if (Level.SEASON.equals("snow")) {
            innerString = "snow";
            outerString = "dirt";
        } else if (Level.SEASON.equals("dirt")) {
            innerString = "light_dirt";
            outerString = "dirt";
        } else if (Level.SEASON.equals("rock")) {
            innerString = "light_rock";
            outerString = "rock";
        } else {
            innerString = "grass";
            outerString = "dirt";
        }
    }

    public Terrain generateFirstTerrain(World world) {
        array = new float[]{
                -100, LOW_LEVEL,
                -50, MathUtils.random(0, 1),
                -25, MathUtils.random(0, 1),
                -10, 0,
                15, MathUtils.random(0, 1),
                25, 0,
                25, LOW_LEVEL
        };

        inner = SplashScreen.assetManager.get("terrain/" + innerString + ".png");
        outer = SplashScreen.assetManager.get("terrain/" + outerString + ".png");

        generateTerrain(world, array, inner, outer);

        return this;
    }

    public Vector2 generateRandomTerrain(World world, RayHandler rayHandler, float startY, int terrainAmount) {
        if (terrainAmount > 10 + MathUtils.random(0, 10)) {
            return generateLava(world, rayHandler, startY);
        } else {
            boolean r = MathUtils.randomBoolean(0.2f);

            if (r) {
                return generateWater(world, startY);
            } else {
//                return generateLava(world, rayHandler, startY);
                return generateRandom(world, startY);
            }
        }

    }

    private Vector2 generateWater(World world, float startY) {

        float startX = MathUtils.random(3, 7);

        float nextY = startY - MathUtils.random(1, 2);
        float nextX = startX + 5;

        float nextX0 = nextX + 15;
        float nextX1 = nextX0 + 5;

        Vector2 last = new Vector2(40, startY);

        if (last.y < LOW_LEVEL / 2) {
            LOW_LEVEL -= 100;
        }

        array = new float[]{
                0, LOW_LEVEL,
                0, startY,
                startX, startY,
                nextX, nextY,
                nextX0, nextY,
                nextX1, last.y,
                last.x, last.y,
                last.x, LOW_LEVEL
        };

        water = new Water(world, (x + startX) - 1, startY - 4.5f, startX + nextX0, 5);

        inner = SplashScreen.assetManager.get("terrain/" + innerString + ".png");
        outer = SplashScreen.assetManager.get("terrain/" + outerString + ".png");

        generateTerrain(world, array, inner, outer);

        return new Vector2(x + last.x, last.y);
    }

    private Vector2 generateLava(World world, RayHandler rayHandler, float startY) {

        float startX = MathUtils.random(3, 7);

        float nextX = startX + MathUtils.random(8, 12);
        float nextY = startY + MathUtils.random(2, 3);

        float nextX0 = nextX + MathUtils.random(1, 2);
        float nextY0 = nextY - 5;

        float nextX1 = nextX0 + MathUtils.random(10, 12);
        float nextY1 = nextY0;

        float nextX2 = nextX1 + MathUtils.random(1, 2);
        float nextY2 = startY + MathUtils.random(0.5f, 1.5f);

        Vector2 last = new Vector2(40, startY);

        if (last.y < LOW_LEVEL / 2) {
            LOW_LEVEL -= 100;
        }

        array = new float[]{
                0, LOW_LEVEL,
                0, startY,
                startX, startY,
                nextX, nextY,
                nextX0, nextY0,
                nextX1, nextY1,
                nextX2, nextY2,
                last.x, last.y,
                last.x, LOW_LEVEL
        };


        lava = new Lava(world, rayHandler, (x + nextX) - 1, startY - 4.5f, (nextX0 + nextX1) / 2 - 1, 5);

        inner = SplashScreen.assetManager.get("terrain/" + innerString + ".png");
        outer = SplashScreen.assetManager.get("terrain/" + outerString + ".png");

        generateTerrain(world, array, inner, outer);

        return new Vector2(x + last.x, last.y);
    }

    private Vector2 generateRandom(World world, float startY) {
        float startX = MathUtils.random(1, 5);
        float nextY = startY + MathUtils.random(-10, 8);
        float nextX = MathUtils.random(15, 20);
        float nextX0 = nextX + MathUtils.random(1, 5);

        Vector2 last = new Vector2(40, nextY + MathUtils.random(-10, 8));

        if (last.y < LOW_LEVEL / 2) {
            LOW_LEVEL -= 100;
        }

        array = new float[]{
                0, LOW_LEVEL,
                0, startY,
                startX, startY,
                nextX, nextY,
                nextX0, nextY,
                last.x, last.y,
                last.x, LOW_LEVEL
        };


        inner = SplashScreen.assetManager.get("terrain/" + innerString + ".png");
        outer = SplashScreen.assetManager.get("terrain/" + outerString + ".png");

        generateTerrain(world, array, inner, outer);

        return new Vector2(x + last.x, last.y);
    }

    private void generateTerrain(World world, float[] array, Texture innerTexture, Texture outerTexture) {
        BodyDef floorDef = new BodyDef();
        FixtureDef floorFixtureDef = new FixtureDef();
        floorDef.type = BodyDef.BodyType.StaticBody;
        floorDef.position.set(x, y);


        ChainShape groundShape = new ChainShape();
        groundShape.createLoop(array);

        floorFixtureDef.shape = groundShape;
        floorFixtureDef.friction = .5f;
        floorFixtureDef.restitution = 0;

        body = world.createBody(floorDef);
        body.createFixture(floorFixtureDef);

        inner = innerTexture;
        outer = outerTexture;

        polygonRegion = createTextureFromShape(array, inner);
        polygonRegion0 = createTextureFromShape(array, outer);
    }

    public void draw(PolygonSpriteBatch polygonSpriteBatch) {
        polygonSpriteBatch.draw(polygonRegion, x, y);
        polygonSpriteBatch.draw(polygonRegion0, x, y - 1);
    }

    public void drawWater(Matrix4 projectionMatrix, Color color) {
        if (water != null) {
            water.update(Gdx.graphics.getDeltaTime());
            water.draw(projectionMatrix, color);
        }
    }

    public void drawLava(Matrix4 projectionMatrix, SpriteBatch spriteBatch, Color color, float x) {
        if (lava != null) {
            lava.update(Gdx.graphics.getDeltaTime(), x);
            lava.draw(projectionMatrix, spriteBatch, color);
        }
    }

    public PolygonRegion createTextureFromShape(float[] array, Texture texture) {
        short triangles[] = new EarClippingTriangulator()
                .computeTriangles(array)
                .toArray();
        return new PolygonRegion(new TextureRegion(texture), array, triangles);
    }

    public void dispose() {
        inner.dispose();
        outer.dispose();
        if (water != null) {
            water.dispose();
        }

        if (lava != null) {
            lava.dispose();
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Body getBody() {
        return body;
    }

    public Lava getLava() {
        return lava;
    }

    public Water getWater() {
        return water;
    }
}
