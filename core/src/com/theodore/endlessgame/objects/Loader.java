package com.theodore.endlessgame.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.theodore.endlessgame.objects.ui.Text;

public class Loader {
    private boolean doneLoading = false;

    private AssetManager assetManager;
    private Text loadingText;

    public Loader(AssetManager assetManager) {
        this.assetManager = assetManager;

        doneLoading = false;
        loadingText = new Text(new BitmapFont(Gdx.files.internal("ui/default.fnt")), "loading", 1280 / 2, 720 / 2);
    }

    public void draw(SpriteBatch batch) {
        loadingText.center(1280 / 2, 720 / 2);
        loadingText.setText("loading " + (int) (assetManager.getProgress() * 100) + "%");

        if (assetManager.update()) {
            doneLoading = true;
        } else {
            doneLoading = false;
        }

        loadingText.draw(batch, batch.getColor());
    }

    public void dispose() {
        loadingText.dispose();
    }

    public boolean isDoneLoading() {
        return doneLoading;
    }

    public void loadSound() {
        assetManager.load("sound/click.ogg", Sound.class);
        assetManager.load("sound/switch.ogg", Sound.class);
    }

    public void loadOther() {
        loadTexture("fluid/water.png");
        loadTexture("fluid/lava.png");
        loadTexture("fluid/waterdisplacement.png");

        loadTexture("terrain/dirt.png");
        loadTexture("terrain/light_dirt.png");
        loadTexture("terrain/light_rock.png");
        loadTexture("terrain/rock.png");
        loadTexture("terrain/grass.png");
        loadTexture("terrain/snow.png");

        loadTexture("background/cloud0.png");
        loadTexture("background/cloud1.png");
        loadTexture("background/cloud2.png");
        loadTexture("background/cloud3.png");

        loadTexture("background/hill0.png");
        loadTexture("background/hill1.png");
        loadTexture("background/hill2.png");

        loadTexture("background/mountain0.png");
        loadTexture("background/mountain1.png");
        loadTexture("background/mountain2.png");

        loadTexture("background/sky.png");

        loadTexture("planes/airplane.png");

        loadAtlas("powerups/health.pack");
        loadAtlas("powerups/speed.pack");
        loadAtlas("powerups/invulnerability.pack");
        loadTexture("powerups/paradrop.png");
    }

    public void loadParticles() {
        loadTexture("particles/exhaustsmoke.png");
        loadTexture("particles/dirt.png");
        loadTexture("particles/snow.png");
        loadTexture("particles/rock.png");
        loadTexture("particles/lava.png");

        loadTexture("particles/smoke.png");

        loadTexture("explosions/exp0.png");
        loadTexture("explosions/exp1.png");
        loadTexture("explosions/exp2.png");
        loadTexture("explosions/exp3.png");
        loadTexture("explosions/exp4.png");
        loadTexture("explosions/exp5.png");

        loadTexture("explosions/fsexp.png");

        loadAtlas("explosions/explosion0.pack");
    }

    public void loadUI() {
        loadTexture("ui/main_panel.png");
        loadTexture("ui/fade.png");

        loadTexture("ui/play_up.png");
        loadTexture("ui/play_down.png");

        loadTexture("ui/button_up.png");
        loadTexture("ui/button_down.png");

        loadTexture("ui/garage_button_up.png");
        loadTexture("ui/garage_button_down.png");
        loadTexture("ui/stats_button_up.png");
        loadTexture("ui/stats_button_down.png");

        loadTexture("ui/x_button_up.png");
        loadTexture("ui/x_button_down.png");

        loadTexture("ui/back_button_down.png");
        loadTexture("ui/back_button_up.png");

        loadTexture("ui/check_button_down.png");
        loadTexture("ui/check_button_up.png");

        loadTexture("ui/music_switch_off.png");
        loadTexture("ui/music_switch_on.png");

        loadTexture("ui/sound_switch_off.png");
        loadTexture("ui/sound_switch_on.png");

        loadTexture("ui/throttle_up.png");
        loadTexture("ui/throttle_down.png");

        loadTexture("ui/brake_down.png");
        loadTexture("ui/brake_up.png");

        assetManager.load("ui/default.fnt", BitmapFont.class);
        assetManager.load("ui/big.fnt", BitmapFont.class);
    }

    public void loadCarTextures() {
        String carType = "pickup";
        loadCar(carType);

        carType = "offroad";
        loadCar(carType);

        carType = "hatchback";
        loadCar(carType);

        carType = "stationwagon";
        loadCar(carType);

        carType = "mini";
        loadCar(carType);

        carType = "coupe";
        loadCar(carType);

        carType = "sport";
        loadCar(carType);

        carType = "fastsport";
        loadCar(carType);

        carType = "race";
        loadCar(carType);

        /** wheels */
        loadTexture("cars/wheels/basic.png");
    }

    private void loadCar(String carType) {
        loadAtlas("cars/" + carType + "/" + carType + ".pack");
    }

    private void loadAtlas(String atlas) {
        assetManager.load(atlas, TextureAtlas.class);
    }

    private void loadTexture(String texture) {
        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Nearest;
        param.magFilter = Texture.TextureFilter.Nearest;
        param.genMipMaps = true;
        assetManager.load(texture, Texture.class, param);
    }

}
