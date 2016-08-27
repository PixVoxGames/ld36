package com.pixvoxgames.ld36;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.io.File;
import java.util.ArrayList;

public class Editor extends ApplicationAdapter implements InputProcessor{

    private Stage mStage;
    private ShapeRenderer mShapeRenderer;

    private ArrayList<Vector2> mPoints;

    @Override
    public void create() {
        mShapeRenderer = new ShapeRenderer();
        mPoints = new ArrayList<Vector2>();

        mStage = new Stage();

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/texgyre.fnt"));

        String backgroundsPath = Gdx.files.getLocalStoragePath()+"gfx/backgrounds/";
        final AssetManager assetManager = new AssetManager();

        File localDirectory = new File(backgroundsPath);
        File[] files = localDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                assetManager.load(backgroundsPath + file.getName(), Texture.class);
            }
        }
        assetManager.finishLoading();

        final Table root = new Table();
        root.setName("root");
        root.setFillParent(true);
        root.top().left();
        root.setTouchable(Touchable.enabled);
        root.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget().getName() != null) {
                    if (event.getTarget().getName().equals("root")) {
                        mPoints.add(new Vector2(x, y));
                    }
                }
            }
        });

        Table filesList = new Table();
        filesList.top().left();
        for (final String backgroundPath : assetManager.getAssetNames()) {
            String filename = new File(backgroundPath).getName();
            Label fileLabel = new Label(filename, new Label.LabelStyle(font, Color.WHITE));
            fileLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    root.setBackground(new TextureRegionDrawable(
                            new TextureRegion(assetManager.get(backgroundPath, Texture.class))));
                }
            });
            filesList.add(fileLabel).left();
            filesList.row();
        }
        ScrollPane scrollPane = new ScrollPane(filesList);
        root.add(scrollPane);

        mStage.addActor(root);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(mStage);
        inputMultiplexer.addProcessor(this);

        Gdx.input.setInputProcessor(inputMultiplexer);

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mStage.act(Gdx.graphics.getDeltaTime());
        mStage.draw();

        mShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        mShapeRenderer.setColor(Color.WHITE);
        for (int i = 1; i < mPoints.size(); i++) {
            mShapeRenderer.line(mPoints.get(i - 1), mPoints.get(i));
        }
        mShapeRenderer.end();
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ESCAPE:
                if (!mPoints.isEmpty()) {
                    mPoints.remove(mPoints.size() - 1);
                }
                return true;
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
