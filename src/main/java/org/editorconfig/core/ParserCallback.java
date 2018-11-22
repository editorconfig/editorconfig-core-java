package org.editorconfig.core;

import java.io.File;

/**
 * Allows to handle an EditorConfig file, a line in it or an option. In every case {@code process...()} method may
 * return {@code false} to terminate processing of a certain part.
 */
public interface ParserCallback {
    /**
     * Called before EditorConfig files are processed for the given source file.
     *
     * @param file The file to process
     * @return False if EditorConfig processing should be completely skipped for the given source file.
     */
    boolean processFile(File file);

    /**
     * Called before EditorConfig file is searched in a directory. The directory may not necessarily contain
     * .editorconfig file.
     *
     * @param dir The current directory to look for .editorconfig file.
     * @return False if no further processing should be done in the directory or any parent directories.
     */
    boolean processDir(File dir);

    /**
     * Called when an EditorConfig file is found.
     *
     * @param configFile The config file that has been found.
     * @return True if the file should be further processed by EditorConfig library, true to skip any further
     * processing.
     * @throws EditorConfigException If the file is invalid for some reason.
     */
    boolean processEditorConfig(File configFile) throws EditorConfigException;

    /**
     * Called when a line from EditorConfig file has been read.
     *
     * @param line The read line. The line is just any line including empty lines, comment lines etc. with spaces
     *             trimmed.
     * @return False if the line should be further handled by EditorConfig library, true if the line is to be skipped.
     * @throws EditorConfigException If the line contains an error.
     */
    boolean processLine(String line) throws EditorConfigException;

    /**
     * Called when an option has been parsed.
     *
     * @param key   The option key.
     * @param value The option value.
     * @return False if the option should be added to the returned list of pairs, true if not (the option is consumed
     * by the callback object itself).
     * @throws EditorConfigException for improper key-value pair.
     */
    boolean processOption(String key, String value) throws EditorConfigException;

    /**
     * Called when all .editorconfig files have been processed.
     *
     * @param file The initial source file.
     */
    void processingFinished(File file);
}
