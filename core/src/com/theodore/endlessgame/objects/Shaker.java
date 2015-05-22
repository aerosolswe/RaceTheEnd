package com.theodore.endlessgame.objects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public class Shaker {

    public float time;
    Random random;
    float x, y;
    float currentTime;
    float power;
    float currentPower;

    public Shaker() {
        time = 0;
        currentTime = 0;
        power = 0;
        currentPower = 0;
    }

    public void shake(float power, float time) {
        random = new Random();
        this.power = power;
        this.time = time;
        this.currentTime = 0;
    }

    public void update(float delta, Camera camera) {
        if (time > 0) {
            if (currentTime <= time) {
                currentPower = power * ((time - currentTime) / time);
                x = (random.nextFloat() - 0.5f) * 2 * currentPower;
                y = (random.nextFloat() - 0.5f) * 2 * currentPower;

//                camera.position.lerp(new Vector3(-x, -y, 0), delta * 10);
                camera.translate(-x, -y, 0);
                currentTime += delta;
            }
        } else {
            x = camera.position.x;
            y = camera.position.y;
        }
    }
}
