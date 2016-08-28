package com.pixvoxsoftware.ld35;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.pixvoxsoftware.ld35.scenes.*;

public class Game extends ApplicationAdapter {

	private Scene currentScene;

	@Override
	public void create () {
//		currentScene = new MainMenuScene();
//		currentScene = new TheBeginningScene(`);
        currentScene = new GameScene();
//		currentScene = new TheEndScene(true);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        currentScene.draw();

		Scene newScene = currentScene.nextScene();
		if (newScene instanceof End) {
			Gdx.app.exit();
		} else if (newScene != null) {
			currentScene = newScene;
		}
	}
}
