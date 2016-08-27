package com.pixvoxgames.ld36.editor;


import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class SaveDialog extends Dialog {

    TextField mTextField;

    public SaveDialog(String title, Skin skin) {
        super(title, skin);

        TextButton saveButton = new TextButton("Save", skin);
        mTextField = new TextField("", skin);
        getContentTable().add(mTextField);
        button(saveButton);
    }

    @Override
    protected void result(Object object) {
        super.result(object);
        result(mTextField.getText());
    }

    protected void result(String filename) {

    }

}
