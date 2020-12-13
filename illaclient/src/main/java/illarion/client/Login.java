/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client;

import illarion.client.net.NetComm;
import illarion.client.net.client.LoginCmd;
import illarion.client.util.GlobalExecutorService;
import illarion.client.util.Lang;
import illarion.client.world.World;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import org.illarion.engine.EngineException;
import org.illarion.engine.ui.LoginData;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * This class is used to store the login parameters and handle the requests that
 * need to be send to the server in order to perform the login properly.
 */
public enum Login {
    INSTANCE;

    /**
     * The instance of the logger that is used write the log output of this class.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);
    /**
     * The string that defines the name of a error node
     */
    private static final String NODE_NAME_ERROR = "error";

    @Nonnull
    private final List<CharEntry> charList = new ArrayList<>();

    @Nullable
    private String loginCharacter;




    @Nonnull
    private Servers currentServer = Servers.Illarionserver;

    @Nonnull
    private LoginData[] loginData = new LoginData[0];

    /**
     * Load the saved server from the configuration file and use it
     * to select the default server.
     */
    public Servers restoreLastUsedServer() {
        int restoredServerKey = IllaClient.getConfig().getInteger("server");

        for (Servers server : Servers.values()) {
            if (server.getServerKey() == restoredServerKey) {
                currentServer = server;
                return server;
            }
        }

        LOGGER.warn("Malformed configuration file detected: Illegal Server Key");

        return Servers.Illarionserver;
    }

    /**
     * Restores the login data currently saved in the configuration files
     * @return the login data, indexed via server key
     */
    public LoginData[] restoreLoginData() {
        if (loginData.length == 0) {
            Servers[] servers = Servers.values();

            loginData = new LoginData[servers.length];
            for (Servers server : servers) {
                loginData[server.getServerKey()] = new LoginData(
                        server.getServerKey(),
                        server.getServerName(),
                        restoreLoginName(server),
                        restorePassword(server),
                        restoreStorePassword(server)
                );
            }
        }

        return loginData;
    }

    /**
     * Load the saved password from the configuration file.
     */
    private String restorePassword(Servers server) {
        String encoded;

        if (server == Servers.Customserver) {
            encoded = IllaClient.getConfig().getString("customFingerprint");
        } else {
            encoded = IllaClient.getConfig().getString("fingerprint");
        }

        return (encoded != null) ? shufflePassword(encoded, true) : "";
    }

    /**
     * Load the saved login from the configuration file.
     */
    private String restoreLoginName(Servers server) {
        if (server == Servers.Customserver) {
            return IllaClient.getConfig().getString("customLastLogin");
        } else {
            return IllaClient.getConfig().getString("lastLogin");
        }
    }

    /**
     * Load the saved decision for whether to save the password.
     */
    private boolean restoreStorePassword(Servers server) {
        if (server == Servers.Customserver) {
            return IllaClient.getConfig().getBoolean("customSavePassword");
        } else {
            return IllaClient.getConfig().getBoolean("savePassword");
        }
    }

    /**
     * Changes the current server to the server identified by the server key.
     *
     * @param serverKey the key of the server to switch to
     */
    public void setCurrentServerByKey(int serverKey) {
        for (Servers server : Servers.values()) {
            if (server.getServerKey() == serverKey) {
                currentServer = server;
                return;
            }
        }

        LOGGER.warn("Tried to set a server with an invalid server key: [%s]".formatted(serverKey));

        currentServer = Servers.Illarionserver;
    }

    /**
     * Sets the login data for the currently selected server.
     *
     * @param username the username
     * @param password the password
     * @param savePassword true if the password should be saved
     */
    public void setLoginDataForCurrentServer(String username, String password, boolean savePassword) {
        int serverKey = currentServer.getServerKey();

        LoginData newLoginData = new LoginData(
                currentServer.getServerKey(),
                currentServer.getServerName(),
                username,
                password,
                savePassword
        );

        loginData[serverKey] = newLoginData;
    }

    /**
     * Saves the login data of the current server into the config
     */
    public void storeData() {
        int serverKey = currentServer.getServerKey();
        LoginData serverLoginData = loginData[serverKey];

        IllaClient.getConfig().set("server", serverKey);
        IllaClient.getInstance().setUsedServer(currentServer);

        if (currentServer == Servers.Customserver) {
            IllaClient.getConfig().set("customLastLogin", serverLoginData.username);
            IllaClient.getConfig().set("customSavePassword", serverLoginData.savePassword);
        } else {
            IllaClient.getConfig().set("lastLogin", serverLoginData.username);
            IllaClient.getConfig().set("savePassword", serverLoginData.savePassword);
        }

        if (serverLoginData.savePassword) {
            storePassword(serverLoginData.password);
        } else {
            deleteStoredPassword();
        }

        IllaClient.getConfig().save();
    }

    /**
     * Store the password in the configuration file or remove the stored password from the configuration.
     *
     * @param password the password that stall be stored to the configuration file
     */
    private void storePassword(@Nonnull String password) {
        if (currentServer == Servers.Customserver) {
            IllaClient.getConfig().set("customSavePassword", true);
            IllaClient.getConfig().set("customFingerprint", shufflePassword(password, false));
        } else {
            IllaClient.getConfig().set("savePassword", true);
            IllaClient.getConfig().set("fingerprint", shufflePassword(password, false));
        }
    }

    /**
     * Remove the stored password.
     */
    private void deleteStoredPassword() {
        if (currentServer == Servers.Customserver) {
            IllaClient.getConfig().set("customSavePassword", false);
            IllaClient.getConfig().remove("customFingerprint");
        } else {
            IllaClient.getConfig().set("savePassword", false);
            IllaClient.getConfig().remove("fingerprint");
        }
    }








    @Nonnull
    public List<CharEntry> getCharacterList() {
        return Collections.unmodifiableList(charList);
    }

    /**
     * Send the given login data to the server
     *
     * @return {@code true} if the login command was SENT successfully
     */
    public boolean login() {
        NetComm netComm = World.getNet();
        if (!netComm.connect()) {
            return false;
        }

        String loginChar = getLoginCharacter();
        if (loginChar == null) {
            return false;
        }

        int clientVersion;
        if (currentServer == Servers.Customserver) {
            if (IllaClient.getConfig().getBoolean("clientVersionOverwrite")) {
                clientVersion = IllaClient.getConfig().getInteger("clientVersion");
            } else {
                clientVersion = currentServer.getClientVersion();
            }
        } else {
            clientVersion = currentServer.getClientVersion();
        }
        World.getNet().sendCommand(new LoginCmd(loginChar, getPassword(), clientVersion));

        return true;
    }

    @Nullable
    private String getLoginCharacter() {
        if (!isCharacterListRequired()) {
            return loginData[currentServer.getServerKey()].username;
        }
        return loginCharacter;
    }

    @Nonnull
    @Contract(pure = true)
    private String getPassword() {
        return loginData[currentServer.getServerKey()].password;
    }

    @Contract(pure = true)
    public boolean isCharacterListRequired() {
        return (currentServer != Servers.Customserver) || IllaClient.getConfig().getBoolean("serverAccountLogin");
    }

    /**
     * Parses the given XML document and
     *
     * @param root The XML document to read
     * @param resultCallback
     */
    private void readXML(@Nonnull Node root, @Nonnull RequestCharListCallback resultCallback) {
        // If the Node is neither the "chars" doc nor "error", recursively call on each child node
        if (!"chars".equals(root.getNodeName()) && !NODE_NAME_ERROR.equals(root.getNodeName())) {
            NodeList children = root.getChildNodes();
            int count = children.getLength();
            for (int i = 0; i < count; i++) {
                readXML(children.item(i), resultCallback);
            }
            return;
        }
        if (NODE_NAME_ERROR.equals(root.getNodeName())) {
            // Gets the node value of the error's id
            int error = Integer.parseInt(root.getAttributes().getNamedItem("id").getNodeValue());
            resultCallback.finishedRequest(error);
            return;
        }
        NodeList children = root.getChildNodes();
        int count = children.getLength();
        // Fetches the Account language and sets the local config language to match
        String accLang = root.getAttributes().getNamedItem("lang").getNodeValue();
        if ("de".equals(accLang)) {
            IllaClient.getConfig().set(Lang.LOCALE_CFG, Lang.LOCALE_CFG_GERMAN);
        } else if ("us".equals(accLang)) {
            IllaClient.getConfig().set(Lang.LOCALE_CFG, Lang.LOCALE_CFG_ENGLISH);
        }
        // Fills the charList with each character the account has on the selected server
        charList.clear();
        for (int i = 0; i < count; i++) {
            Node charNode = Objects.requireNonNull(children.item(i));
            String charName = Objects.requireNonNull(charNode.getTextContent());
            int status = Integer.parseInt(charNode.getAttributes().getNamedItem("status").getNodeValue());
            String charServer = charNode.getAttributes().getNamedItem("server").getNodeValue();

            CharEntry addChar = new CharEntry(charName, status);
            String usedServerName = IllaClient.getInstance().getUsedServer().getServerName();
            if (charServer.equals(usedServerName)) {
                charList.add(addChar);
            }
        }

        resultCallback.finishedRequest(0);
    }

    public void requestCharacterList(@Nonnull RequestCharListCallback resultCallback) {
        GlobalExecutorService.getService().submit(new RequestCharacterListTask(resultCallback));
    }

    private void requestCharacterListInternal(@Nonnull RequestCharListCallback resultCallback) {
        String serverURI = IllaClient.DEFAULT_SERVER.getServerHost();
        try {
            URL requestURL = new URL("https://" + serverURI + "/account/xml_charlist.php");

            String query = "name=" +
                    URLEncoder.encode(loginData[currentServer.getServerKey()].username, StandardCharsets.UTF_8) +
                    "&passwd=" +
                    URLEncoder.encode(getPassword(), StandardCharsets.UTF_8);

            HttpsURLConnection conn = (HttpsURLConnection) requestURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(query.getBytes(StandardCharsets.UTF_8).length));
            conn.setUseCaches(false);

            //SSLSocketFactory sslSocketFactory = IllarionSSLSocketFactory.getFactory();
            //if (sslSocketFactory != null) {
            //    conn.setSSLSocketFactory(sslSocketFactory);
            //}

            conn.connect();
            // Send the query to the server
            try (OutputStreamWriter output = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
                output.write(query);
                output.flush();
            }
            // Grabs the XML returned by the server
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(conn.getInputStream());
            // Interprets the server's XML
            readXML(doc, resultCallback);
        } catch (@Nonnull UnknownHostException e) {
            resultCallback.finishedRequest(2);
            LOGGER.error("Failed to resolve hostname, for fetching the charlist");
        } catch (@Nonnull Exception e) {
            resultCallback.finishedRequest(2);
            LOGGER.error("Loading the charlist from the server failed");
        }
    }

    /**
     * Shuffle the letters of the password around a bit.
     *
     * @param pw     the encoded password or the decoded password that stall be
     *               shuffled
     * @param decode false for encoding the password, true for decoding.
     * @return the encoded or the decoded password
     */
    @Nonnull
    private static String shufflePassword(@Nonnull String pw, boolean decode) {
        try {
            Charset usedCharset = StandardCharsets.UTF_8;
            // creating the key
            Path userDir = DirectoryManager.getInstance().getDirectory(Directory.User);
            KeySpec keySpec = new DESKeySpec(userDir.toAbsolutePath().toString().getBytes(usedCharset));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES");
            if (decode) {
                byte[] encrypedPwdBytes = Base64.getDecoder().decode(pw.getBytes(usedCharset));
                cipher.init(Cipher.DECRYPT_MODE, key);
                encrypedPwdBytes = cipher.doFinal(encrypedPwdBytes);
                return new String(encrypedPwdBytes, usedCharset);
            }

            byte[] cleartext = pw.getBytes(usedCharset);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new String(Base64.getEncoder().encode(cipher.doFinal(cleartext)), usedCharset);
        } catch (@Nonnull GeneralSecurityException | IllegalArgumentException e) {
            if (decode) {
                LOGGER.warn("Decoding the password failed");
            } else {
                LOGGER.warn("Encoding the password failed");
            }
            return "";
        }
    }

    @FunctionalInterface
    public interface RequestCharListCallback {
        void finishedRequest(int errorCode);
    }

    /**
     * Internal class to hold the name and status of each character to display for selection
     */
    public static final class CharEntry {
        @Nonnull
        private final String charName;
        private final int charStatus;

        public CharEntry(@Nonnull String name, int status) {
            charName = name;
            charStatus = status;
        }

        @Nonnull
        public String getName() {
            return charName;
        }

        public int getStatus() {
            return charStatus;
        }
    }

    private final class RequestCharacterListTask implements Callable<Void> {
        @Nonnull
        private final RequestCharListCallback callback;

        private RequestCharacterListTask(@Nonnull RequestCharListCallback callback) {
            this.callback = callback;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Exception if unable to compute a result
         */
        @Nullable
        @Override
        public Void call() throws Exception {
            requestCharacterListInternal(callback);
            return null;
        }
    }
}
