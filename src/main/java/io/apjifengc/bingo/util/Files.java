package io.apjifengc.bingo.util;

import org.apache.commons.lang.Validate;

import java.io.File;

/**
 * @author Milkory
 */
public class Files {

    public static void deleteDirectory(File dir) {
        if(!dir.exists()) return;
        Validate.isTrue(dir.isDirectory());
        cleanDirectory(dir);
        dir.delete();
    }

    public static void cleanDirectory(File dir) {
        Validate.isTrue(dir.isDirectory());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else file.delete();
        }
    }

}
