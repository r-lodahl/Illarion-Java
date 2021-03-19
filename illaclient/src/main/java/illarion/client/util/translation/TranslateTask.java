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

import illarion.client.util.Lang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class TranslateTask implements Callable<String> {
    @NotNull
    private final ExecutorService executorService;
    @NotNull
    private final TranslationProvider provider;
    @NotNull
    private final TranslationDirection direction;
    @NotNull
    private final String original;
    @NotNull
    private final TranslatorCallback callback;

    TranslateTask(@NotNull ExecutorService executorService, @NotNull TranslationProvider provider,
                  @NotNull TranslationDirection direction, @NotNull String original,
                  @NotNull TranslatorCallback callback) {
        this.executorService = executorService;
        this.provider = provider;
        this.direction = direction;
        this.original = original;
        this.callback = callback;
    }

    @Override
    @Nullable
    public String call() throws Exception {
        String header = findHeader(original);
        String usedText = (header == null) ? original : original.substring(header.length());
        boolean foundOocMarkers = false;
        if (usedText.startsWith("((") && usedText.endsWith("))")) {
            foundOocMarkers = true;
            usedText = usedText.substring(2, usedText.length() - 2);
        }

        BreakIterator iterator = BreakIterator.getSentenceInstance(Lang.INSTANCE.getLocale());
        iterator.setText(usedText);

        Collection<Future<String>> translationTasks = new ArrayList<>();
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String line = usedText.substring(start, end).trim();
            translationTasks.add(executorService.submit(new TranslateSentenceTask(provider, direction, line)));
        }

        StringBuilder resultBuilder = new StringBuilder();
        if (header != null) {
            resultBuilder.append(header);
        }
        if (foundOocMarkers) {
            resultBuilder.append("((");
        }

        for (Future<String> task : translationTasks) {
            String translated = task.get();
            if (translated == null) {
                callback.sendTranslation(null);
                return null;
            }
            resultBuilder.append(task.get());
            resultBuilder.append(' ');
        }

        if (resultBuilder.length() == 0) {
            callback.sendTranslation(null);
            return null;
        }
        resultBuilder.setLength(resultBuilder.length() - 1);
        if (foundOocMarkers) {
            resultBuilder.append("))");
        }
        String result = resultBuilder.toString();
        callback.sendTranslation(result);
        return result;
    }

    @NotNull
    private static final Pattern PATTERN_SAY = Pattern.compile("^(.+?)\\s" + Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("log.say") + ":\\s");
    @NotNull
    private static final Pattern PATTERN_SHOUT = Pattern.compile("^(.+?)\\s" + Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("log.shout") + ":\\s");
    @NotNull
    private static final Pattern PATTERN_WHISPER = Pattern.compile("^(.+?)\\s" + Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("log.whisper") + ":\\s");

    @Nullable
    private String findHeader(@NotNull String input) {
        if (input.startsWith(Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("chat.distantShout") + ": ")) {
            return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("chat.distantShout") + ": ";
        }
        if (input.startsWith(Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("chat.broadcast") + ": ")) {
            return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("chat.broadcast") + ": ";
        }
        if (input.startsWith(Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("chat.textto") + ": ")) {
            return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("chat.textto") + ": ";
        }
        if (input.startsWith(Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("chat.scriptInform") + ": ")) {
            return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("chat.scriptInform") + ": ";
        }
        String sayHeader = findPattern(input, PATTERN_SAY);
        if (sayHeader != null) {
            return sayHeader;
        }
        String shoutHeader = findPattern(input, PATTERN_SHOUT);
        if (shoutHeader != null) {
            return shoutHeader;
        }
        String whisperHeader = findPattern(input, PATTERN_WHISPER);
        return whisperHeader;
    }

    @Nullable
    private String findPattern(@NotNull String input, @NotNull Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
