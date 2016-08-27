package com.pixvoxgames.ld36;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;


public class Game extends ApplicationAdapter {
    private static Scene currentScene;

	@Override
	public void create() {
        Loggers.graphics.info("OpenGL {}", Gdx.gl.glGetString(GL20.GL_VERSION));
        Loggers.graphics.info("we're running on {} ({})", Gdx.gl.glGetString(GL20.GL_RENDERER), Gdx.gl.glGetString(GL20.GL_VENDOR));
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	@Override
	public void dispose() {
	}

    public static Scene getCurrentScene() {
        return currentScene;
    }
}
