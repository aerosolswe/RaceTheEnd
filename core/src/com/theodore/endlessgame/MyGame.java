package com.theodore.endlessgame;

import com.badlogic.gdx.Game;
import com.theodore.endlessgame.screens.MainScreen;
import com.theodore.endlessgame.screens.SplashScreen;

public class MyGame extends Game {

    public MainScreen mainScreen;
    public SplashScreen splashScreen;

    @Override
    public void create() {
        mainScreen = new MainScreen();
        splashScreen = new SplashScreen(this);

        setScreen(splashScreen);
    }
}
