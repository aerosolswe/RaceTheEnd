package com.theodore.endlessgame.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.theodore.endlessgame.GameWorld;
import com.theodore.endlessgame.screens.SplashScreen;

public class ParticleEffectPhys {

    private Array<Particle> particles = new Array<Particle>();

    private Vector2 position;
    private Vector2 size;

    private String particle;

    private float spawnRate;
    private float lifeTime;
    private Vector2 velocity;

    private World world;

    public ParticleEffectPhys(World world, String particle, Vector2 position, Vector2 size, float spawnRate, float lifeTime, Vector2 velocity) {
        this.world = world;
        this.position = position;
        this.size = size;
        this.particle = particle;
        this.spawnRate = spawnRate;
        this.lifeTime = lifeTime;
        this.velocity = velocity;
    }

    private float i = 0;

    public void update(float delta) {

        if (i > 1) {
            i = 0;
            particles.add(new Particle(particle, position, size, velocity, lifeTime));
        } else {
            i += spawnRate * delta;
        }

        for (int i = 0; i < particles.size; i++) {
            particles.get(i).update(delta);

            if (particles.get(i).isDead) {
                GameWorld.BODIES_TO_REMOVE.add(particles.get(i).body);
                particles.removeIndex(i);
            }
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        for (int i = 0; i < particles.size; i++) {
            particles.get(i).draw(spriteBatch);
        }
    }

    public void dispose() {
        for (int i = 0; i < particles.size; i++) {
            particles.get(i).dispose();
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getSize() {
        return size;
    }

    public void setSize(Vector2 size) {
        this.size = size;
    }

    public String getParticle() {
        return particle;
    }

    public void setParticle(String particle) {
        this.particle = particle;
    }

    public float getSpawnRate() {
        return spawnRate;
    }

    public void setSpawnRate(float spawnRate) {
        this.spawnRate = spawnRate;
    }

    public float getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(float lifeTime) {
        this.lifeTime = lifeTime;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public class Particle {

        public Sprite sprite;
        public Vector2 velocity;
        public float lifeTime;

        public boolean isDead = false;

        public Vector2 position;
        public Vector2 size;

        public Texture texture;

        public Body body;

        private float i;

        public Particle(String particle, Vector2 position, Vector2 size, Vector2 velocity, float lifeTime) {
            this.position = position;
            this.size = size;
            this.velocity = velocity;
            this.lifeTime = lifeTime;

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(position.x, position.y);

            CircleShape shape = new CircleShape();
            shape.setRadius(size.x / 2);

            FixtureDef fixtureDef = new FixtureDef();

            fixtureDef.density = 1;
            fixtureDef.friction = 0.5f;
            fixtureDef.restitution = .3f;

            fixtureDef.shape = shape;

            body = world.createBody(bodyDef);
            body.createFixture(fixtureDef);

            body.setLinearVelocity(velocity);

            texture = SplashScreen.assetManager.get(particle);
            this.sprite = new Sprite(texture);
            this.sprite.setPosition(position.x, position.y);
            this.sprite.setSize(size.x, size.y);

            this.i = lifeTime;
        }

        public void update(float delta) {
            sprite.setAlpha(i / lifeTime);

            if (i <= 0) {
                isDead = true;
                i = lifeTime;
            } else {
                i -= 1 * delta;
            }

            sprite.setPosition(body.getPosition().x - size.x / 2, body.getPosition().y - size.y / 2);
//            sprite.setRotation((float) Math.toDegrees(body.getAngle()));
        }

        public void draw(SpriteBatch spriteBatch) {
            sprite.draw(spriteBatch);
        }

        public void dispose() {
            texture.dispose();
        }
    }

}
