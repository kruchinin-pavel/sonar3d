package org.kpa.util.io;

import com.google.common.base.Preconditions;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLClassLoader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileUtils {
    private static final DateTimeFormatter ldtBackupFileF = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static InputStream classOrPath(String url) throws FileNotFoundException {
        InputStream resourceAsStream = URLClassLoader.getSystemResourceAsStream(url);
        return resourceAsStream != null ? resourceAsStream : new FileInputStream(url);
    }

    public static String tempInSameDir(String fileName) throws IOException {
        return new File(fileName).getParent() + "/" +
                FilenameUtils.getBaseName(fileName) + ".tmp." + UUID.randomUUID() + ".csv";
    }

    public static void replaceFile(File replace, File replaceBy, boolean doBackup) {
        Preconditions.checkArgument(replaceBy.exists(), "Source file doesn't exist %s", replaceBy);
        File backup = backupIfExists(replace);
        try {
            Preconditions.checkArgument(replaceBy.renameTo(replace), "Didn't manage to rename %s to %s", replaceBy, replace);
            if (!doBackup && backup != null && !backup.delete()) {
                logger.error("Didn't manage to delete backup backup {} ", backup);
            }
        } catch (Exception e) {
            if (backup != null && !backup.renameTo(replace)) {
                logger.error("Didn't manage to rollback backup {} to {}", backup, replace);
            }
            throw e;
        }

    }

    public static File backupIfExists(File file) {
        return backupIfExists(file, null);
    }

    public static File backupIfExists(File file, String backupSubDir) {
        if (file.exists()) {
            File backupFile = proposeNameWithTs(file, "backup", backupSubDir);
            Preconditions.checkArgument(file.renameTo(backupFile), "Didn't manage to make backup %s from original file %s", backupFile, file.getAbsolutePath());
            return backupFile;
        } else {
            file.getParentFile().mkdirs();
            return null;
        }
    }

    public static File proposeNameWithTs(File file, String suffix, String subDir) {
        String ext = FilenameUtils.getExtension(file.toString());
        String name = FilenameUtils.getBaseName(file.toString());
        if (!StringUtils.isEmpty(suffix)) {
            suffix = "-" + suffix;
        }
        File resFile = new File(FilenameUtils.concat(
                file.getAbsoluteFile().getParent() +
                        (StringUtils.isEmpty(subDir) ? "" : "/" + subDir),
                String.format("%s%s-%s.%s", name, suffix, ldtBackupFileF.format(LocalDateTime.now()), ext)));
        resFile.getParentFile().mkdir();
        Preconditions.checkArgument(!resFile.exists(), "File already exists: %s", resFile);
        return resFile;
    }

}
