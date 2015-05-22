package com.theodore.endlessgame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.theodore.endlessgame.screens.SplashScreen;

public class Util {

    public static Color fade(Color from, Color to, float time) {
        float t = 0;

        if (t < 1.0f) {
            t += Gdx.graphics.getDeltaTime() / time;
        }

        from.set(from.lerp(to, t));

        return from;
    }

    public static float fade(float from, float to, float time) {
        float t = 0;

        if (t < 1.0f) {
            t += Gdx.graphics.getDeltaTime() / time;
        }

        final float invAlpha = 1.0f - t;
        from = (from * invAlpha) + (to * t);

        return from;
    }

    public static Vector2 fade(Vector2 from, Vector2 to, float time) {
        float t = 0;

        if (t < 1.0f) {
            t += Gdx.graphics.getDeltaTime() / time;
        }

        from.set(from.lerp(to, t));

        return from;
    }

    public static float distance(Vector2 firstPoint, Vector2 secondPoint) {
        return (float) Math.sqrt((firstPoint.x - secondPoint.x) * (firstPoint.x - secondPoint.x) + (firstPoint.y - secondPoint.y) * (firstPoint.y - secondPoint.y));
    }

    public static CheckBox createCheckbox(String text, Texture up, Texture down) {
        return createCheckbox(text, up, down, ((BitmapFont) SplashScreen.assetManager.get("ui/default.fnt")));
    }

    public static CheckBox createCheckbox(String text, Texture up, Texture down, BitmapFont font) {
        TextureRegion bUp = new TextureRegion(up);
        TextureRegion bDown = new TextureRegion(down);

        CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle();
        style.checkboxOn = new TextureRegionDrawable(bUp);
        style.checkboxOff = new TextureRegionDrawable(bDown);
        style.font = font;

        return new CheckBox(text, style);
    }

    public static Button createButton(Texture up, Texture down) {
        TextureRegion bUp = new TextureRegion(up);
        TextureRegion bDown = new TextureRegion(down);

        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = new TextureRegionDrawable(bUp);
        style.down = new TextureRegionDrawable(bDown);

        return new Button(style);
    }

    public static TextButton createTextButton(String text) {
        return createTextButton(text, (Texture) SplashScreen.assetManager.get("ui/button_up.png"), (Texture) SplashScreen.assetManager.get("ui/button_down.png"));
    }

    public static TextButton createTextButton(String text, Texture up, Texture down) {
        return createTextButton(text, up, down, ((BitmapFont) SplashScreen.assetManager.get("ui/default.fnt")));
    }

    public static TextButton createTextButton(String text, Texture up, Texture down, BitmapFont font) {
        TextureRegion bUp = new TextureRegion(up);
        TextureRegion bDown = new TextureRegion(down);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = new TextureRegionDrawable(bUp);
        style.down = new TextureRegionDrawable(bDown);
        style.font = font;

        return new TextButton(text, style);
    }

    public static String timeConversion(int totalSeconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        return hours + "h " + minutes + "m " + seconds + "s";
    }
}
