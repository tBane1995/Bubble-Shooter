package com.tbane.bubble_shooter;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;

public class AssetsManager {
    public static final AssetManager manager = new AssetManager();
    public static boolean success;
    static {
        manager.setLoader(Model.class, ".obj", new ObjLoader(new InternalFileHandleResolver()));

        success = true;

        loadTexture("tex/mainBoard.png");
        loadTexture("tex/panel.png");
        loadTexture("tex/titleFrame.png");
        loadTexture("tex/topPanel.png");
        loadTexture("tex/bottomPanel.png");

        loadTexture("tex/mainMenuLeftSign.png");
        loadTexture("tex/mainMenuRightSign.png");

        loadTexture("tex/menuButtonNormal.png");
        loadTexture("tex/menuButtonHover.png");
        loadTexture("tex/menuButtonPressed.png");

        loadTexture("tex/panelButtonNormal.png");
        loadTexture("tex/panelButtonHover.png");
        loadTexture("tex/panelButtonPressed.png");

        loadTexture("tex/backButtonNormal.png");
        loadTexture("tex/backButtonHover.png");
        loadTexture("tex/backButtonPressed.png");

        loadTexture("tex/awardCup.png");

        loadTexture("tex/gameBoard.png");
        loadTexture("tex/bubble.png");
        loadTexture("tex/gun.png");
        manager.finishLoading();

        if(success){
            System.out.println("success loaded all textures");
        }else{
            System.out.println("failed loaded all textures");
            System.out.println("exit");
        }
    }

    public static void loadTexture(String path) {
        if (!manager.isLoaded(path, Texture.class)) {
            manager.load(path, Texture.class);
        }
    }

    public static boolean update() {
        return manager.update(); // zwraca true, gdy wszystko załadowane
    }


    public static Texture getTexture(String path) {
        if (manager.isLoaded(path, Texture.class)) {
            return manager.get(path, Texture.class);
        }
        return null;
    }
}
