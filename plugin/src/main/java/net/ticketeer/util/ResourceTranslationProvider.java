package net.ticketeer.util;

import java.io.InputStream;

import net.ticketeer.util.Translator.TranslationProvider;

public class ResourceTranslationProvider implements TranslationProvider {
    private final Class<?> clazz;
    private final String name;

    public ResourceTranslationProvider(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public InputStream createInputStream() {
        return clazz.getResourceAsStream('/' + name);
    }

    @Override
    public String getName() {
        return name;
    }
}
