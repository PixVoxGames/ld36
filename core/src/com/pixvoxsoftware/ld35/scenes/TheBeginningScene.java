package com.pixvoxsoftware.ld35.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class TheBeginningScene implements Scene {
    private Texture[] storyTextures = new Texture[4];
    private BitmapFont coolFont;
    private BitmapFont font;
    private Stage stage;
    private boolean ended = false;
    private Label[][] labels;
    private float maxDelay = 10;
    private float currentDelay;
    private int ptr = -1;


    public TheBeginningScene() {
        storyTextures[0] = null;
        storyTextures[1] = new Texture(Gdx.files.internal("story1.png"));
        storyTextures[2] = new Texture(Gdx.files.internal("story2.png"));
        storyTextures[3] = null;

        coolFont = new BitmapFont(Gdx.files.internal("fonts/cool_font_small.fnt"));

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        String[] stringsPart0 = new String[] {
            "A long time ago, two good magicians Tink and Merlin had lived in this world",
                "They loved each other and were doing a lot of good things",
        };

        String[] stringsPart1 = new String[] {
                "But people didn't believe them",
                "Everyone said that Tink and Merlin were trying to summon a Daemon in this world",
                "They had burned them down and had imprisoned their souls in magical place..."
        };

        String[] stringsPart2 = new String[] {
                "But Tink found a way to escape from there",
                "And she wants find and save her sweetheart"
        };

        String[] stringsPart3 = new String[] {
                "So, the story begins from there..."
        };

        labels = new Label[4][];

        labels[0] = createLabels(stringsPart0);
        labels[1] = createLabels(stringsPart1);
        labels[2] = createLabels(stringsPart2);
        labels[3] = createLabels(stringsPart3);


        nextPart();

        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                nextPart();
                return true;
            }
        });
    }

    private void nextPart() {
        if (ended) {
            return;
        }
        if (ptr >= 0) {
            for (int i = 0; i < labels[ptr].length; i++) {
                labels[ptr][i].remove();
            }
        }
        ptr++;
        if (ptr >= labels.length) {
            ended = true;
            return;
        }
        currentDelay = maxDelay;
        for (int i = 0; i < labels[ptr].length; i++) {
            stage.addActor(labels[ptr][i]);
            labels[ptr][i].setPosition(Gdx.graphics.getWidth() / 2 - labels[ptr][i].getWidth() / 2, Gdx.graphics.getHeight() - 30 * i - 40);
        }
    }

    public Label[] createLabels(String[] strings) {
        Label[] labels = new Label[strings.length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = createLabel(strings[i]);
        }
        return labels;
    }

    public Label createLabel(String text) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = coolFont;
        style.fontColor = new Color(1, 228f / 255f, 181f / 255f, 1);
        Label label = new Label(text, style);
        return label;
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        currentDelay -= Gdx.graphics.getDeltaTime();
        if (currentDelay <= 0) {
            nextPart();
        }
        if (!ended) {
            stage.getBatch().begin();
            if (storyTextures[ptr] != null) {
                stage.getBatch().draw(storyTextures[ptr], 0, -35);
            }
            stage.act();
            stage.getBatch().end();
            stage.draw();
        }
    }

    @Override
    public Scene nextScene() {
        if (ended) {
            return new GameScene();
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
        nextPart();
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
