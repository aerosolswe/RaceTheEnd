package com.theodore.endlessgame.objects.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.theodore.endlessgame.screens.SplashScreen;

public class Text {

    private BitmapFont font;

    private float x;
    private float y;

    private String text;

    private boolean visible = true;

    public Text(String fontType, String text, float x, float y) {
        this(((BitmapFont) SplashScreen.assetManager.get("ui/" + fontType)), text, x, y);
    }

    public Text(BitmapFont font, String text, float x, float y) {
        this.x = x;
        this.y = y;
        this.text = text;

        this.font = font;
    }

    public Text(String text, float x, float y) {
        this("default.fnt", text, x, y);
    }

    public void draw(SpriteBatch spriteBatch, Color color) {
        if (visible) {
            font.setColor(color);
            font.draw(spriteBatch, text, x, y);
        }
    }

    public void center(String text, float x, float y) {
        this.x = x - font.getBounds(text).width / 2;
        this.y = y - font.getBounds(text).height / 2;
    }

    public void center(float x, float y) {
        center(text, x, y);
    }

    public void dispose() {
        font.dispose();
    }

    public BitmapFont getFont() {
        return font;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
