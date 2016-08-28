package com.pixvoxsoftware.ld35.scenes;

import com.badlogic.gdx.InputProcessor;

public interface Scene extends InputProcessor {
    void draw();
    Scene nextScene();
}
