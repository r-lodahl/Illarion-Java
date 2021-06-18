package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import org.illarion.engine.ui.NullSecureResourceBundle;
import org.illarion.engine.ui.login.AccountCreationData;

public final class AccountCreationView extends Table {
    private final TextField tfAccountName, tfPassword, tfEmail, tfPasswordRepeat, tfEmailRepeat;
    private final TextButton btCreate, btBack;

    AccountCreationView(Skin skin, NullSecureResourceBundle resourceBundle) {
        btCreate = new TextButton(resourceBundle.getLocalizedString("create"), skin);
        btBack = new TextButton(resourceBundle.getLocalizedString("back"), skin);

        tfAccountName = new TextField("", skin);
        tfPassword = new TextField("", skin);
        tfPasswordRepeat = new TextField("", skin);
        tfEmail = new TextField("", skin);
        tfEmailRepeat = new TextField("", skin);

        var accountCreateGroup = new Table();
        accountCreateGroup.row();
        accountCreateGroup.add(new Label(resourceBundle.getLocalizedString("account"), skin));
        accountCreateGroup.add(tfAccountName);
        accountCreateGroup.row();
        accountCreateGroup.add(new Label(resourceBundle.getLocalizedString("password"), skin));
        accountCreateGroup.add(tfPassword);
        accountCreateGroup.row();
        accountCreateGroup.add(new Label(resourceBundle.getLocalizedString("passwordRepeat"), skin));
        accountCreateGroup.add(tfPasswordRepeat);
        accountCreateGroup.row();
        accountCreateGroup.add(new Label(resourceBundle.getLocalizedString("email"), skin));
        accountCreateGroup.add(tfEmail);
        accountCreateGroup.row();
        accountCreateGroup.add(new Label(resourceBundle.getLocalizedString("emailRepeat"), skin));
        accountCreateGroup.add(tfEmailRepeat);

        row();
        add(accountCreateGroup);
        row();
        HorizontalGroup buttonRow = new HorizontalGroup();
        buttonRow.addActor(btCreate);
        buttonRow.addActor(btBack);
        add(buttonRow);
    }

    public boolean isPasswordMatching() {
        return tfPassword.getText().equals(tfPasswordRepeat.getText());
    }

    public boolean isEmailMatching() {
        return tfEmail.getText().equals(tfEmailRepeat.getText());
    }

    public AccountCreationData getAccountCreationData() {
        return new AccountCreationData(tfAccountName.getText(), tfEmail.getText(), tfPassword.getText());
    }

    public void setOnCreateAccountCallback(EventListener onClick) {
        btCreate.addListener(onClick);
    }

    public void setOnBackCallback(EventListener onClick) {
        btBack.addListener(onClick);
    }
}
