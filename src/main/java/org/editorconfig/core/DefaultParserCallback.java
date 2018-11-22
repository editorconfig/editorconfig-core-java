package org.editorconfig.core;

import java.io.File;

/**
 * Default callback which does nothing and doesn't influence .editorconfig handling.
 */
public class DefaultParserCallback implements ParserCallback {

    @Override
    public boolean processFile(File file) {
        return true;
    }

    @Override
    public boolean processDir(File dir) {
        return true;
    }

    @Override
    public boolean processEditorConfig(File configFile) throws EditorConfigException {
        return true;
    }

    @Override
    public boolean processLine(String line) throws EditorConfigException {
        return true;
    }

    @Override
    public boolean processOption(String key, String value) throws EditorConfigException {
        return true;
    }

    @Override
    public void processingFinished(File file) {
    }
}
