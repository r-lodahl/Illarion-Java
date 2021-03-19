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
package illarion.client.util.translation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class TranslateSentenceTask implements Callable<String> {
    @NotNull
    private final TranslationProvider provider;
    @NotNull
    private final TranslationDirection direction;
    @NotNull
    private final String original;

    public TranslateSentenceTask(@NotNull TranslationProvider provider, @NotNull TranslationDirection direction,
                                 @NotNull String original) {
        this.provider = provider;
        this.direction = direction;
        this.original = original;
    }

    @Override
    @Nullable
    public String call() throws Exception {
        return provider.getTranslation(original, direction);
    }
}
