package com.tbane.bubble_shooter.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;


public class Font {

    public static BitmapFont titleFont, buttonFont;
    public static BitmapFont descriptionFont;
    public static BitmapFont rankingHighscoreFont, rankingTitleFont, rankingFont;
    public static BitmapFont gameTopTextBigFont, gameTopTextSmallFont;
    static {

        buttonFont = generateFont("fonts/ScienceGothic.ttf", 40);
        titleFont = generateFont("fonts/ScienceGothic.ttf", 56);

        descriptionFont = generateFont("fonts/ScienceGothic.ttf", 32);

        rankingHighscoreFont = generateFont("fonts/ScienceGothic.ttf", 48);
        rankingTitleFont = generateFont("fonts/ScienceGothic.ttf", 32);
        rankingFont = generateFont("fonts/ScienceGothic.ttf", 24);

        gameTopTextBigFont = generateFont("fonts/ScienceGothic.ttf", 48);
        gameTopTextSmallFont = generateFont("fonts/ScienceGothic.ttf", 32);

    }

    public static BitmapFont generateFont(String path, int size){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "ąćęłńóśżźĄĆĘŁŃÓŚŻŹ";
        BitmapFont font = generator.generateFont(parameter);
        return font;
    }
}
