package com.theodore.endlessgame.objects;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.theodore.endlessgame.screens.SplashScreen;

public class Lava {

    private ShaderProgram shader;
    private float time;

    private Mesh mesh;

    private Texture texture;
    private Texture displacement;

    private Body body;

    private float x;
    private float y;
    private float width;
    private float height;
    private float angle;

    private ParticleEffect lavaEffect;
    private RayHandler rayHandler;
    private PointLight lavaLight;

    public Lava(World world, RayHandler rayHandler, float x, float y, float width, float height) {
        this.rayHandler = rayHandler;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        texture = SplashScreen.assetManager.get("fluid/lava.png");
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        displacement = SplashScreen.assetManager.get("fluid/waterdisplacement.png");
        displacement.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        displacement.bind();

        ShaderProgram.pedantic = false;

        shader = new ShaderProgram(Water.vertexShader, Water.fragmentShader);

        mesh = createQuad(/*-1, -1, 1, -1, 1, -0.3f, -1, -0.3f*/x, y, width, height);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(x + width / 2, y + (height / 2)));

        body = world.createBody(bodyDef);
        body.setUserData("lava");

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, (height / 2) - 1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);

        shape.dispose();

        lavaEffect = new ParticleEffect("particles/lava.png", new Vector2(x + width / 2, y + height / 2), new Vector2(0.5f, 0.5f), new Vector2(0, 25f), new Vector2(0, -30f), new Vector2(3, 1), 1, 5f);
        lavaLight = new PointLight(rayHandler, 100, Color.ORANGE, 25, x + width / 2 - 2.5f, y + height / 2 + 0.5f);
        lavaLight.setSoft(false);
    }

    public void update(float delta, float playerX) {
        time += delta;
        angle = time * (2 * MathUtils.PI);
        if (angle > (2 * MathUtils.PI))
            angle -= (2 * MathUtils.PI);

        lavaEffect.update(delta);

        if (playerX < lavaLight.getX() - 100 || playerX > lavaLight.getX() + 100) {
            if (lavaLight.isActive()) {
                lavaLight.setActive(false);
            }
        } else {
            if (!lavaLight.isActive()) {
                lavaLight.setActive(true);
            }
        }

        lavaEffect.setPosition(new Vector2(MathUtils.random(x + (width / 2) - 4, x + (width / 2) + 2), y + height / 2));
    }

    public void draw(Matrix4 projectionMatrix, SpriteBatch spriteBatch, Color color) {
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        texture.bind(1);
        displacement.bind(2);

        shader.begin();
        shader.setUniformMatrix("u_worldView", projectionMatrix);
        shader.setUniformi("u_texture", 1);
        shader.setUniformi("u_texture2", 2);
        shader.setUniformf("timedelta", -angle);
        mesh.render(shader, GL20.GL_TRIANGLE_FAN);
        shader.end();

        texture.bind(0);
        displacement.bind(0);

        spriteBatch.setProjectionMatrix(projectionMatrix);
        spriteBatch.setColor(color);
        spriteBatch.begin();
        lavaEffect.draw(spriteBatch);
        spriteBatch.end();
    }

    public void dispose() {
        mesh.dispose();
        displacement.dispose();
        texture.dispose();
        lavaEffect.dispose();
        lavaLight.dispose();
    }

    public Mesh createQuad(float x, float y, float width, float height) {
        float[] verts = new float[20];
        int i = 0;


        verts[i++] = x + width; // x1
        verts[i++] = y; // y1
        verts[i++] = 0;
        verts[i++] = 1f; // u1
        verts[i++] = 1f; // v1

        verts[i++] = x; // x2
        verts[i++] = y; // y2
        verts[i++] = 0;
        verts[i++] = 0f; // u2
        verts[i++] = 1f; // v2

        verts[i++] = x; // x3
        verts[i++] = y + height; // y2
        verts[i++] = 0;
        verts[i++] = 0f; // u3
        verts[i++] = 0f; // v3

        verts[i++] = x + width; // x4
        verts[i++] = y + height; // y4
        verts[i++] = 0;
        verts[i++] = 1f; // u4
        verts[i++] = 0f; // v4

        Mesh mesh = new Mesh(true, 4, 0, // static mesh with 4 vertices and no
                // indices
                new VertexAttribute(
                        VertexAttributes.Usage.Position, 3,
                        ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(
                        VertexAttributes.Usage.TextureCoordinates, 2,
                        ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

        mesh.setVertices(verts);
        return mesh;

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

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public Body getBody() {
        return body;
    }
}
