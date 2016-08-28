package com.pixvoxsoftware.ld35.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;


import java.util.ArrayList;

public class MainMenuScene implements Scene {


    private enum SceneState {
        PLAYING,
        EXIT,
        NEW_GAME;

    }
    private TextButton exitGameButton;
    private TextButton startNewGameButton;
    private Texture texture;
    private SpriteBatch spriteBatch;
    private BitmapFont coolFontSmall;
    private BitmapFont coolFont;
    private BitmapFont font;
    private Stage stage;
    private ArrayList<String> strings = new ArrayList<>();
    private ArrayList<Float> sizes = new ArrayList<>();
    private SceneState state = SceneState.PLAYING;

    public MainMenuScene() {
        texture = new Texture(Gdx.files.internal("the_beginning2.png"));
        spriteBatch = new SpriteBatch();
        coolFont = new BitmapFont(Gdx.files.internal("fonts/cool_font.fnt"));
        coolFontSmall = new BitmapFont(Gdx.files.internal("fonts/cool_font_small.fnt"));
        font = new BitmapFont(Gdx.files.internal("fonts/arial-15.fnt"));
        font.setColor(1, 1, 1, 1);

        addStrings(coolFont, new String[] {
                "START GAME"
        });
        addStrings(font, new String[] {
                "Carzil, PixelIndigo & White Comet with love for Ludum Dare #35",
                "PixVox Software (c) 2016 All rights reserved"
        });

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        startNewGameButton = createButton("START GAME", coolFont);
        startNewGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                state = SceneState.NEW_GAME;
            }
        });
        startNewGameButton.setPosition(Gdx.graphics.getWidth() / 2 - startNewGameButton.getWidth() / 2, stage.getHeight() / 7);

        exitGameButton = createButton("EXIT", coolFontSmall);
        exitGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                state = SceneState.EXIT;
            }
        });
        exitGameButton.setPosition(Gdx.graphics.getWidth() / 2 - exitGameButton.getWidth() / 2, startNewGameButton.getY() - 40);

        stage.addActor(startNewGameButton);
        stage.addActor(exitGameButton);
    }

    private void addStrings(BitmapFont font, String[] strings) {
        GlyphLayout layout = new GlyphLayout();
        for (String string : strings) {
            layout.setText(font, string);
            this.strings.add(string);
            this.sizes.add(layout.width);
        }
    }

    private TextButton createButton(String text, BitmapFont font) {
        TextButton.TextButtonStyle textStyle = new TextButton.TextButtonStyle();
        textStyle.font = font;
        textStyle.overFontColor = Color.WHITE;
        textStyle.fontColor = new Color(1, 228f / 255f, 181f / 255f, 1);
        return new TextButton(text, textStyle);
    }

    @Override
    public void draw() {
//        coolFont.draw(spriteBatch, strings.get(0), Gdx.graphics.getWidth() / 2 - sizes.get(0) / 2, 200);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();
        stage.getBatch().draw(texture, 0, -35);
        stage.act();
        font.draw(stage.getBatch(), strings.get(1), Gdx.graphics.getWidth() / 2 - sizes.get(1) / 2, 34);
        font.draw(stage.getBatch(), strings.get(2), Gdx.graphics.getWidth() / 2 - sizes.get(2) / 2, 15);
        stage.getBatch().end();
        stage.draw();
    }

    @Override
    public Scene nextScene() {
        if (state == SceneState.NEW_GAME) {
            return new TheBeginningScene();
        } else if (state == SceneState.EXIT) {
            return new End();
        }
        return null;
    }

    @Override
    public boolean keyDown(int keycode) {
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
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
        return false;
    }
}
