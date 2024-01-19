package com.github.alphafoxz.spring_boot_starter_restful_dsl.service.version;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.RestfulDslConstants;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public interface VersionStore {
    @NonNull
    public File getFile();

    default public void init() {
        File file = getFile();
        FileUtil.mkParentDirs(file);
        if (!file.exists()) {
            FileUtil.writeUtf8String(RestfulDslConstants.EMPTY_JSON_OBJ, file);
        }
    }

    @SuppressWarnings("unchecked")
    default public TreeMap<String, Serializable> readFile() {
        JSON json = JSONUtil.readJSON(getFile(), StandardCharsets.UTF_8);
        return json.toBean(TreeMap.class);
    }

    default public void writeFile(TreeMap<String, Serializable> content) {
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(content), getFile());
    }

    default public void writeFile(String key, Serializable value) {
        TreeMap<String, Serializable> map = readFile();
        map.put(key, value);
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(map), getFile());
    }
}
