package com.theodore.endlessgame.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.theodore.endlessgame.screens.SplashScreen;

public class ParticleEffect {

    private Array<Particle> particles = new Array<Particle>();

    private Texture texture;

    private Vector2 position;
    private Vector2 size;

    private float spawnRate;
    private float lifeTime;
    private Vector2 randomFactor;
    private Vector2 gravity;
    private Vector2 velocity;

    public ParticleEffect(Texture texture, Vector2 position, Vector2 size, Vector2 velocity, Vector2 gravity, Vector2 randomFactor, float spawnRate, float lifeTime) {
        this.texture = texture;
        this.position = position;
        this.size = size;
        this.velocity = velocity;
        this.gravity = gravity;
        this.spawnRate = spawnRate;
        this.lifeTime = lifeTime;
        this.randomFactor = randomFactor;
    }

    public ParticleEffect(String texture, Vector2 position, Vector2 size, Vector2 velocity, Vector2 gravity, Vector2 randomFactor, float spawnRate, float lifeTime) {
        this(((Texture) SplashScreen.assetManager.get(texture)), position, size, velocity, gravity, randomFactor, spawnRate, lifeTime);
    }

    private Particle generateNewParticle() {
        Vector2 particleLocation = new Vector2(position);
        Vector2 particleVelocity = new Vector2();
        float randomX = MathUtils.random(0.5f, randomFactor.x);
        float randomY = MathUtils.random(0.5f, randomFactor.y);

        particleVelocity.x = (randomX * velocity.x);
        particleVelocity.y = (randomY * velocity.y);

        return new Particle(texture, particleLocation, size, particleVelocity, lifeTime);
    }

    private float i = 0;

    public void update(float delta) {

        if (i > 1) {
            i = 0;
            particles.add(generateNewParticle());
        } else {
            i += spawnRate * delta;
        }

        for (int i = 0; i < particles.size; i++) {
            Particle particle = particles.get(i);
            particle.update(delta, gravity);

            if (particle.isDead) {
                particles.removeIndex(i);
                i--;
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

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
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

    private class Particle {

        public Sprite sprite;
        public Vector2 velocity;
        public float lifeTime;

        public boolean isDead = false;

        public Vector2 position;
        public Vector2 size;

        public Texture texture;

        private float i;

        public Particle(Texture texture, Vector2 position, Vector2 size, Vector2 velocity, float lifeTime) {
            this.position = position;
            this.size = size;
            this.velocity = velocity;
            this.lifeTime = lifeTime;

            this.texture = texture;
            this.sprite = new Sprite(texture);
            this.sprite.setPosition(position.x, position.y);
            this.sprite.setSize(size.x, size.y);

            this.i = lifeTime;
        }


        public void update(float delta, Vector2 gravity) {
            sprite.setAlpha(i / lifeTime);

            if (this.i <= 0) {
                isDead = true;
                i = lifeTime;
            } else {
                i -= 1 * delta;
            }

            Vector2 newVel = velocity;
            newVel.x += gravity.x * delta;
            newVel.y += gravity.y * delta;

            Vector2 pos = new Vector2(sprite.getX(), sprite.getY());
            pos.x += newVel.x * delta;
            pos.y += newVel.y * delta;

            sprite.setPosition(pos.x, pos.y);
        }

        public void draw(SpriteBatch spriteBatch) {
            sprite.setColor(spriteBatch.getColor().r, spriteBatch.getColor().g, spriteBatch.getColor().b, sprite.getColor().a);
            sprite.draw(spriteBatch);
        }

        public void dispose() {
            texture.dispose();
        }
    }

}
