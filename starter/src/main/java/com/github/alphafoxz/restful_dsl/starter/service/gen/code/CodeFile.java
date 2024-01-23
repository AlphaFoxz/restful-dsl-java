package com.github.alphafoxz.restful_dsl.starter.service.gen.code;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
@Getter
@Setter
public class CodeFile {
    private String content;
    private String path;
    private String templatePath;
    private String fileName;
    private Charset charset = StandardCharsets.UTF_8;

    public boolean writeToLocal() {
        File javaFile = FileUtil.file(path);
        try {
            if (javaFile.exists()) {
                String oriContent = FileUtil.readUtf8String(javaFile);
                if (content.equals(oriContent.replace("\r\n", "\n"))) {
                    return true;
                }
            }
            FileUtil.mkParentDirs(javaFile);
            FileUtil.writeString(content, path, charset);
            log.debug("生成代码：\n路径 {}\n内容 {}", path, content);
        } catch (Throwable e) {
            log.error("写文件失败", e);
            return false;
        }
        return true;
    }

    public boolean equals(Object o) {
        if (o instanceof CodeFile) {
            CodeFile codeFile = (CodeFile) o;
            return StrUtil.equals(this.path, codeFile.getPath());
        }
        return false;
    }
}
