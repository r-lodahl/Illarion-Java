/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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
package illarion.client.util.account;

import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class Credentials {
    @NotNull
    private static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    private static Reference<SecretKey> SECRET_KEY_REFERENCE;

    @NotNull
    private final String userName;

    @NotNull
    private final String password;

    public Credentials(@NotNull String userName, @NotNull String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Create the secret key that is required to encrypt and decrypt the password.
     *
     * @return the secret key, either newly create or a cashed instance
     * @throws GeneralSecurityException in case generating the key fails
     */
    @NotNull
    private static SecretKey getSecretKey() throws GeneralSecurityException {
        if (SECRET_KEY_REFERENCE != null) {
            SecretKey oldKey = SECRET_KEY_REFERENCE.get();
            if (oldKey != null) {
                return oldKey;
            }
        }

        Charset usedCharset = StandardCharsets.UTF_8;
        Path userDir = DirectoryManager.getInstance().getDirectory(Directory.User);
        KeySpec keySpec = new DESKeySpec(userDir.toAbsolutePath().toString().getBytes(usedCharset));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey newKey = keyFactory.generateSecret(keySpec);

        SECRET_KEY_REFERENCE = new SoftReference<>(newKey);

        return newKey;
    }

    @Nullable
    private static String getDecryptedPassword(@NotNull String encryptedPassword) {
        try {
            Charset usedCharset = StandardCharsets.UTF_8;
            Cipher cipher = Cipher.getInstance("DES");
            SecretKey key = getSecretKey();

            Decoder decoder = Base64.getDecoder();
            byte[] encryptedPadBytes = decoder.decode(encryptedPassword.getBytes(usedCharset));
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedPadBytes = cipher.doFinal(encryptedPadBytes);

            return new String(decryptedPadBytes, usedCharset);
        } catch (GeneralSecurityException e) {
            LOGGER.warn("Error while decrypting password.", e);
        }
        return null;
    }

    @Nullable
    private static String getEncryptedPassword(@NotNull String clearPassword) {
        try {
            Charset usedCharset = StandardCharsets.UTF_8;
            Cipher cipher = Cipher.getInstance("DES");
            SecretKey key = getSecretKey();

            Encoder encoder = Base64.getEncoder();
            byte[] cleartext = clearPassword.getBytes(usedCharset);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new String(encoder.encode(cipher.doFinal(cleartext)), usedCharset);
        } catch (GeneralSecurityException e) {
            LOGGER.warn("Error while encrypting password.", e);
        }
        return null;
    }

    @NotNull
    public String getUserName() {
        return userName;
    }

    @NotNull
    public String getPassword() {
        return password;
    }
}
