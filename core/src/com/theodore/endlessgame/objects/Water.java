package com.theodore.endlessgame.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.theodore.endlessgame.screens.SplashScreen;

public class Water {

    public static String vertexShader =
            "attribute vec4 a_position;    \n"
                    + "attribute vec2 a_texCoord0;\n"
                    + "uniform mat4 u_worldView;\n"
                    + "varying vec4 v_color;"
                    + "varying vec2 v_texCoords;"
                    + "void main()                  \n"
                    + "{                            \n"
                    + "   v_color = vec4(1, 1, 1, 1); \n"
                    + "   v_texCoords = a_texCoord0; \n"
                    + "   gl_Position =  u_worldView * a_position;  \n"
                    + "}                            \n";

    public static String fragmentShader = "#ifdef GL_ES\n"
            + "precision mediump float;\n"
            + "#endif\n"
            + "varying vec4 v_color;\n"
            + "varying vec2 v_texCoords;\n"
            + "uniform sampler2D u_texture;\n"
            + "uniform sampler2D u_texture2;\n"
            + "uniform float timedelta;\n"
            + "void main()                                  \n"
            + "{                                            \n"
            + "  vec2 displacement = texture2D(u_texture2, v_texCoords/6.0).xy;\n" //
            + "  float t=v_texCoords.y +displacement.y*0.1-0.15+  (sin(v_texCoords.x * 60.0+timedelta) * 0.005); \n" //
            + "  gl_FragColor = v_color * texture2D(u_texture, vec2(v_texCoords.x,t));\n"
            + "}";

    public static String fragmentShader2 = "#ifdef GL_ES\n"
            + "precision mediump float;\n"
            + "#endif\n"
            + "varying vec4 v_color;\n"
            + "varying vec2 v_texCoords;\n"
            + "uniform sampler2D u_texture;\n"
            + "void main()                                  \n"
            + "{                                            \n"
            + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n"
            + "}";


    private ShaderProgram shader;
    private ShaderProgram waterShader;

    private Matrix4 matrix;
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

    public Water(World world, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        texture = SplashScreen.assetManager.get("fluid/water.png");
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        displacement = SplashScreen.assetManager.get("fluid/waterdisplacement.png");
        displacement.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        displacement.bind();
        matrix = new Matrix4();

        ShaderProgram.pedantic = false;

        shader = new ShaderProgram(vertexShader, fragmentShader);

        mesh = createQuad(/*-1, -1, 1, -1, 1, -0.3f, -1, -0.3f*/x, y, width, height);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(x + width / 2, y + (height / 2)));

        body = world.createBody(bodyDef);
        body.setUserData("water");

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, (height / 2) - 1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);

        shape.dispose();
    }

    public void update(float delta) {
        time += delta;
        angle = time * (2 * MathUtils.PI);
        if (angle > (2 * MathUtils.PI))
            angle -= (2 * MathUtils.PI);
    }

    public void draw(Matrix4 projectionMatrix, Color color) {
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
    }

    public void dispose() {
        mesh.dispose();
        displacement.dispose();
        waterShader.dispose();
        texture.dispose();
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
