package com.pixvoxgames.ld36.editor;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.pixvoxgames.ld36.RailPoint;
import com.pixvoxgames.ld36.Scene;
import net.dermetfan.gdx.scenes.scene2d.ui.FileChooser;
import net.dermetfan.gdx.scenes.scene2d.ui.ListFileChooser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Editor extends ApplicationAdapter implements InputProcessor{

    private static final String FILE_FONT = "fonts/texgyre.fnt";
    private static final String FILE_WINDOW_BG = "gfx/editor/window_bg.png";
    private static final AssetDescriptor<Skin> FILE_SKIN =
            new AssetDescriptor<Skin>("editor/skin.json", Skin.class);

    private static final String BACKGROUNDS_DIR = "gfx/backgrounds/";
    private static final String SCENES_DIR = "scenes/";

    private AssetManager assetManager;

    private Stage mStage;
    private Table mRoot;
    private Skin mSkin;

    private Scene mScene;

    private ShapeRenderer mShapeRenderer;

    private RailPoint mDragStart;
    private Vector3 mMousePointer;  // TODO Remove me later

    @Override
    public void create() {
        mShapeRenderer = new ShapeRenderer();
        mMousePointer = new Vector3();

        mStage = new Stage();
        mScene = new Scene();

        BitmapFont font = new BitmapFont(Gdx.files.internal(FILE_FONT));

        assetManager = new AssetManager();

        assetManager.load(FILE_WINDOW_BG, Texture.class);
        assetManager.load(FILE_SKIN);
        assetManager.finishLoading();

        mSkin = assetManager.get(FILE_SKIN);

        mRoot = new Table();
        mRoot.setName("root");
        mRoot.setFillParent(true);
        mRoot.top().left();
        mRoot.setTouchable(Touchable.enabled);
        mRoot.addListener(new DragListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mDragStart = null;
                for (RailPoint railPoint : mScene.getRailPoints()) {
                    if (railPoint.isHovered(x, y)) {
                        mDragStart = railPoint;
                        break;
                    }
                }
                if (mDragStart == null) {
                    mDragStart = new RailPoint(x, y);
                    mScene.getRailPoints().add(mDragStart);
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                RailPoint dragStop = null;
                for (RailPoint railPoint : mScene.getRailPoints()) {
                    if (railPoint.isHovered(x, y)) {
                        dragStop = railPoint;
                        break;
                    }
                }
                if (dragStop == null) {
                    dragStop = new RailPoint(x, y);
                    mScene.getRailPoints().add(dragStop);
                }

                // FIXME Possible memory leak
                mDragStart.addIncidentPoint(dragStop);
                dragStop.addIncidentPoint(mDragStart);
            }
        });

        Table filesList = new Table();
        NinePatch ninePatch = new NinePatch(assetManager.get(FILE_WINDOW_BG, Texture.class),
                                            2, 2, 2, 2);
        filesList.setBackground(new NinePatchDrawable(ninePatch));
        filesList.top().left();

        ScrollPane scrollPane = new ScrollPane(filesList);
        mRoot.add(scrollPane);

        mStage.addActor(mRoot);

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
        // FIXME Renders same edge twice
        for (RailPoint pointA : mScene.getRailPoints()) {
            for (RailPoint pointB : pointA.getIncidentPoints()) {
                mShapeRenderer.line(pointA.getX(), pointA.getY(),
                                pointB.getX(), pointB.getY());
            }
        }
        mShapeRenderer.end();
        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        mMousePointer.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        mMousePointer = mStage.getCamera().unproject(mMousePointer);
        for (RailPoint point : mScene.getRailPoints()) {
            if (point.isHovered(mMousePointer.x, mMousePointer.y)) {
                mShapeRenderer.setColor(Color.BLUE);
            }
            mShapeRenderer.circle(point.getX(), point.getY(), RailPoint.RADIUS);

            mShapeRenderer.setColor(Color.WHITE);
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
                if (!mScene.getRailPoints().isEmpty()) {
                    mScene.getRailPoints().remove(mScene.getRailPoints().size() - 1);
                }
                return true;
            case Input.Keys.O:
                openScene();
                return true;
            case Input.Keys.S:
                saveScene();
                return true;
            case Input.Keys.B:
                setBackground();
                return true;
        }
        return false;
    }

    private void openScene() {
        Skin skin = assetManager.get(FILE_SKIN);
        final Window window = new Window("Choose a file", skin);
        window.setSize(640, 480);
        window.setPosition((mStage.getWidth() - window.getWidth()) / 2,
                (mStage.getHeight() - window.getHeight()) / 2f);
        window.setResizable(true);
        final ListFileChooser chooser = new ListFileChooser(skin, new FileChooser.Listener() {
            @Override
            public void choose(FileHandle file) {
                Json json = new Json();
                mScene = json.fromJson(Scene.class, file.readString());
                loadScene();
                window.getParent().removeActor(window);  //TODO Dirty hack
            }

            @Override
            public void choose(Array<FileHandle> files) {

            }

            @Override
            public void cancel() {
                window.getParent().removeActor(window);  //TODO Dirty hack
            }
        });
        chooser.setDirectory(Gdx.files.internal(SCENES_DIR));

        window.add(chooser).expand().fill();

        mStage.addActor(window);
        System.out.println(mStage.getKeyboardFocus());
        mStage.setKeyboardFocus(chooser.getContents());
    }

    private void saveScene() {
        new SaveDialog("Save As", mSkin) {
            @Override
            protected void result(String filename) {
                Json json = new Json();
                String output = json.toJson(mScene);
                FileHandle file = Gdx.files.local("scenes/"+filename);
                file.writeString(output, false);
            }
        }.show(mStage);
    }

    private void setBackground() {
        Skin skin = assetManager.get(FILE_SKIN);
        final Window window = new Window("Choose a background", skin);
        window.setSize(640, 480);
        window.setPosition((mStage.getWidth() - window.getWidth()) / 2,
                (mStage.getHeight() - window.getHeight()) / 2f);
        window.setResizable(true);
        final ListFileChooser chooser = new ListFileChooser(skin, new FileChooser.Listener() {
            @Override
            public void choose(FileHandle file) {
                mScene.setBackground(file.name());
                loadScene();
                window.getParent().removeActor(window);  //TODO Dirty hack
            }

            @Override
            public void choose(Array<FileHandle> files) {

            }

            @Override
            public void cancel() {
                window.getParent().removeActor(window);  //TODO Dirty hack
            }
        });
        chooser.setDirectory(Gdx.files.internal(BACKGROUNDS_DIR));

        window.add(chooser).expand().fill();

        mStage.addActor(window);
        System.out.println(mStage.getKeyboardFocus());
        mStage.setKeyboardFocus(chooser.getContents());
    }

    private void loadScene() {
        assetManager.load(BACKGROUNDS_DIR + mScene.getBackground(), Texture.class);
        assetManager.finishLoading();

        TextureRegion backgroundTextureRegion = new TextureRegion(
                assetManager.get(BACKGROUNDS_DIR + mScene.getBackground(), Texture.class));

        mRoot.setBackground(new TextureRegionDrawable(backgroundTextureRegion));
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
