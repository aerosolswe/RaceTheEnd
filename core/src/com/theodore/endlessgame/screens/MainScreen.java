package com.theodore.endlessgame.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.theodore.endlessgame.GameWorld;
import com.theodore.endlessgame.Menu;
import com.theodore.endlessgame.utils.Util;

public class MainScreen implements Screen, InputProcessor, GestureDetector.GestureListener {
    public static boolean restart = false;

    private Color color;

    public Menu menu;
    public GameWorld gameWorld;

    private boolean fadeIn = false;

    public MainScreen() {
    }

    @Override
    public void show() {
        color = new Color(0, 0, 0, 1);

        menu = new Menu(this);
        gameWorld = new GameWorld(this);

        InputMultiplexer inputProcessor = new InputMultiplexer();
        inputProcessor.addProcessor(this);
        inputProcessor.addProcessor(new GestureDetector(this));
        inputProcessor.addProcessor(gameWorld.getHud().getStage());
        inputProcessor.addProcessor(menu.getStage());

        Gdx.input.setInputProcessor(inputProcessor);

        fadeIn = true;
    }

    public void update(float delta) {
        menu.update(delta);
        gameWorld.update(delta);

        if (fadeIn) {
            if (color.r < 1) {
                Util.fade(color, new Color(1, 1, 1, 1), 0.5f);
            } else {
                fadeIn = false;
            }
        }
    }

    public void draw(float delta) {
        gameWorld.draw(color);
        menu.draw();
        gameWorld.drawHud(color);

        if (restart) {
            restart();
        }
    }

    private void restart() {
        gameWorld = new GameWorld(this);
        InputMultiplexer inputProcessor = new InputMultiplexer();
        inputProcessor.addProcessor(this);
        inputProcessor.addProcessor(new GestureDetector(this));
        inputProcessor.addProcessor(gameWorld.getHud().getStage());
        inputProcessor.addProcessor(menu.getStage());

        Gdx.input.setInputProcessor(inputProcessor);
        color.set(0, 0, 0, 1);

        fadeIn = true;

        restart = false;
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        draw(delta);

        gameWorld.clean();
        menu.cleanUp();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        gameWorld.dispose();
        menu.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.F1) {
            gameWorld.restart();
        }

        if (keycode == Input.Keys.F2) {
//            gameWorld.shaker.shake(1f, 0.3f);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        gameWorld.touchDown(screenX, screenY, pointer, button);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        gameWorld.touchUp(screenX, screenY, pointer, button);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        gameWorld.scrolled(amount);
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        menu.tap(x, y, count, button);
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        gameWorld.pan(x, y, deltaX, deltaY);
        menu.pan(x, y, deltaX, deltaY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        menu.panStop(x, y, pointer, button);
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }


    public Menu getMenu() {
        return menu;
    }
}
