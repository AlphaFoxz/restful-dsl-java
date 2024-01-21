package com.github.alphafoxz.spring_boot_starter_restful_dsl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public enum RestfulDslConstants {
    ;
    public static final String RESTFUL_DSL_PACKAGE = "com.github.alphafoxz.spring_boot_starter_restful_dsl";
    public static final String EMPTY_JSON_OBJ = "{}";
    private static final String FILE_SEPARATOR = File.separator;
    public static String PROJECT_ROOT_PATH;
    public static OsTypeEnum OS_TYPE;
    public final static String SDK_PATH;
    public final static String SDK_GEN_PATH;
    public final static String SDK_GEN_RESTFUL_TEMPLATE_PATH;
    public final static String SDK_VERSION_PATH;
    public final static String SDK_BIN_PATH;

    static {
        initProjectPath();
        initOsType();
        SDK_PATH = PROJECT_ROOT_PATH + FILE_SEPARATOR + ".sdk";
        SDK_GEN_PATH = SDK_PATH + FILE_SEPARATOR + "gen";
        SDK_GEN_RESTFUL_TEMPLATE_PATH = SDK_GEN_PATH + FILE_SEPARATOR + "restful";
        SDK_VERSION_PATH = SDK_PATH + FILE_SEPARATOR + "version";
        SDK_BIN_PATH = SDK_PATH + FILE_SEPARATOR + "bin";
    }

    private static void initProjectPath() {
        // 解决Eclipse中user.dir不是根目录的问题
        String path = System.getProperty("user.dir");
        boolean isChange = false;
        while (!FileUtil.file(path, ".git").exists() && !FileUtil.file(path, ".mvn").exists() && !FileUtil.file(path, ".sdk").exists()) {
            path += FILE_SEPARATOR + "..";
            isChange = true;
        }
        if (isChange) {
            PROJECT_ROOT_PATH = FileUtil.file(path).getAbsolutePath();
        } else {
            PROJECT_ROOT_PATH = path;
        }
    }

    private static void initOsType() {
        OsInfo osInfo = SystemUtil.getOsInfo();
        if (osInfo.isMac()) {
            OS_TYPE = OsTypeEnum.MAC;
        } else if (osInfo.isLinux()) {
            OS_TYPE = OsTypeEnum.LINUX;
        } else {
            OS_TYPE = OsTypeEnum.WINDOWS;
        }
    }

    public static enum OsTypeEnum {
        WINDOWS,
        LINUX,
        MAC,
    }
}