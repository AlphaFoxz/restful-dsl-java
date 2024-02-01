package com.github.alphafoxz.restful_dsl.starter.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.github.alphafoxz.restful_dsl.starter.RestfulDslConstants;
import com.github.alphafoxz.restful_dsl.starter.configuration.RestfulDslProperties;
import com.github.alphafoxz.restful_dsl.starter.gen.restl.dtos.*;
import com.github.alphafoxz.restful_dsl.starter.gen.restl.enums.RestfulDslFileTypeEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Slf4j
@Getter
@Service
public class RestfulDslInfoService {
    private static final String FILE_SEPARATOR = File.separator;

    @Resource
    private Snowflake snowflake;
    @Resource
    private RestfulDslProperties restfulDslProperties;

    public RestfulDslListResponseDto checkErr() {
        final String restfulPath = RestfulDslConstants.SDK_GEN_RESTFUL_TEMPLATE_PATH;
        RestfulDslListResponseDto result = new RestfulDslListResponseDto().setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
        List<String> errors = CollUtil.newArrayList();
        if (CollUtil.isEmpty(restfulDslProperties.getIncludeModules())) {
            // 单模块
            FileUtil.mkdir(restfulPath + FILE_SEPARATOR + "apis");
            FileUtil.mkdir(restfulPath + FILE_SEPARATOR + "dtos");
            FileUtil.mkdir(restfulPath + FILE_SEPARATOR + "enums");
        } else {
            try {
                // 多模块
                for (String moduleName : restfulDslProperties.getIncludeModules()) {
                    FileUtil.mkdir(restfulPath + FILE_SEPARATOR + moduleName + FILE_SEPARATOR + "apis");
                    FileUtil.mkdir(restfulPath + FILE_SEPARATOR + moduleName + FILE_SEPARATOR + "dtos");
                    FileUtil.mkdir(restfulPath + FILE_SEPARATOR + moduleName + FILE_SEPARATOR + "enums");
                }
            } catch (Exception e) {
                log.error("sdk初始化restl目录异常", e);
                errors.add("sdk初始化restl目录异常：" + ExceptionUtil.getSimpleMessage(e));
            }
        }
        if (!errors.isEmpty()) {
            result.setData(errors);
            result.setSuccess(false);
        }
        return result;
    }

    public RestfulDslListResponseDto checkRestApiImplements() {
        RestfulDslListResponseDto result = new RestfulDslListResponseDto().setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
        List<String> errors = CollUtil.newArrayList();
        if (CollUtil.isEmpty(restfulDslProperties.getIncludeModules())) {
            String targetPackage = restfulDslProperties.getBasePackage();
            //单模块
            for (Class<?> apiClass : ClassUtil.scanPackage(targetPackage + "." + restfulDslProperties.getCodePackage() + ".apis")) {
                Set<Class<?>> implClasses = ClassUtil.scanPackageBySuper(targetPackage, apiClass);
                if (CollUtil.isEmpty(implClasses)) {
                    errors.add("【restAPI接口未实现】" + apiClass.getName());
                } else {
                    for (Class<?> implClass : implClasses) {
                        if (implClass.getAnnotation(Controller.class) == null && implClass.getAnnotation(RestController.class) == null) {
                            errors.add("【restAPI未注册为Controller的实现类】" + implClass.getName());
                        }
                    }
                }
            }
        } else {
            //多模块
            for (String moduleName : restfulDslProperties.getIncludeModules()) {
                String targetPackage = restfulDslProperties.getBasePackage() + "." + moduleName;
                for (Class<?> apiClass : ClassUtil.scanPackage(targetPackage + "." + restfulDslProperties.getCodePackage() + ".apis")) {
                    Set<Class<?>> implClasses = ClassUtil.scanPackageBySuper(targetPackage, apiClass);
                    if (CollUtil.isEmpty(implClasses)) {
                        errors.add("【restAPI接口未实现】" + apiClass.getName());
                    } else {
                        for (Class<?> implClass : implClasses) {
                            if (implClass.getAnnotation(Controller.class) == null && implClass.getAnnotation(RestController.class) == null) {
                                errors.add("【restAPI未注册为Controller的实现类】" + implClass.getName());
                            }
                        }
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            result.setSuccess(false);
            result.setData(errors);
        }
        return result;
    }

    public RestfulDslStringResponseDto getJavaNamespace() {
        RestfulDslStringResponseDto result = new RestfulDslStringResponseDto().setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
        result.setData(restfulDslProperties.getBasePackage());
        return result;
    }

    public RestfulDslCodeTemplateResponseDto getTemplateContentByPath(String filePath) {
        RestfulDslCodeTemplateResponseDto result = new RestfulDslCodeTemplateResponseDto()
                .setId(snowflake.nextId())
                .setTaskId(snowflake.nextId())
                .setSuccess(false);
        try {
            File file = FileUtil.file(filePath);
            String content = FileUtil.readUtf8String(file);
            RestfulDslCodeTemplateDto template = new RestfulDslCodeTemplateDto();
            template.setContent(content);
            template.setFilePath(file.getAbsolutePath());
            template.setFileSeparator(File.separator);
            template.setNamespace(MapUtil.newHashMap());
            template.setImports(MapUtil.newHashMap());
            result.setData(template);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error("{} 读取文件异常", filePath);
            result.setMessage(filePath + "读取文件异常");
        }
        return result;
    }

    public RestfulDslFileTreeResponseDto getRestfulTemplateFileTree() {
        RestfulDslFileTreeResponseDto result = new RestfulDslFileTreeResponseDto().setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
        try {
            result.setData(readFileTree(FileUtil.file(RestfulDslConstants.SDK_GEN_RESTFUL_TEMPLATE_PATH), 0));
            return result;
        } catch (Exception e) {
            log.error("{} 读取文件异常", RestfulDslConstants.SDK_GEN_RESTFUL_TEMPLATE_PATH);
            result.setMessage(RestfulDslConstants.SDK_GEN_RESTFUL_TEMPLATE_PATH + " 读取文件异常");
            result.setSuccess(false);
            return result;
        }
    }

    private RestfulDslFileInfoDto readFileTree(File fileOrDir, int level) {
        RestfulDslFileInfoDto dto = new RestfulDslFileInfoDto();
        dto.setSeparator(File.separator);
        dto.setIsReadOnly(level <= 2);
        dto.setFilePath(fileOrDir.getAbsolutePath());
        dto.setFileName(fileOrDir.getName());
        dto.setParentDir(fileOrDir.getParentFile().getAbsolutePath());
        if (FileUtil.isFile(fileOrDir)) {
            // 文件类型
            dto.setExt(FileUtil.getSuffix(fileOrDir));
            dto.setFileType(RestfulDslFileTypeEnum.LOCAL_FILE.getValue());
            dto.setIsEmpty(FileUtil.size(fileOrDir) == 0);
            dto.setContent(FileUtil.readUtf8String(fileOrDir));
            return dto;
        }
        // 目录类型
        dto.setFileType(RestfulDslFileTypeEnum.LOCAL_DIR.getValue());
        dto.setChildren(CollUtil.newArrayList());
        File[] innerFiles = FileUtil.ls(fileOrDir.getAbsolutePath());
        boolean isEmpty = innerFiles == null || innerFiles.length == 0;
        dto.setIsEmpty(isEmpty);
        if (isEmpty) {
            return dto;
        }
        for (File innerFile : innerFiles) {
            dto.getChildren().add(readFileTree(innerFile, level + 1));
        }
        return dto;
    }

    public RestfulDslListResponseDto deleteFile(String filePath) {
        File file = FileUtil.file(filePath);
        RestfulDslListResponseDto dto = new RestfulDslListResponseDto()
                .setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
        if (!file.exists() || !StrUtil.startWith(file.getAbsolutePath(), RestfulDslConstants.PROJECT_ROOT_PATH)) {
            dto.setMessage("【文件路径非法】" + file.getAbsolutePath());
            dto.setSuccess(false);
            return dto;
        }
        FileUtil.del(file);
        return dto;
    }

    public RestfulDslStringResponseDto renameFile(String filePath, String newPath) {
        RestfulDslStringResponseDto result = new RestfulDslStringResponseDto()
                .setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
        File file = FileUtil.file(filePath);
        if (!file.exists() || !StrUtil.startWith(file.getAbsolutePath(), RestfulDslConstants.PROJECT_ROOT_PATH) || !newPath.startsWith(RestfulDslConstants.PROJECT_ROOT_PATH)) {
            result.setMessage("【文件路径非法】" + file.getAbsolutePath());
            result.setSuccess(false);
            return result;
        }
        try {
            file.renameTo(FileUtil.file(newPath));
        } catch (Exception e) {
            result.setMessage("【文件重命名失败】" + e.getMessage());
            result.setSuccess(false);
            return result;
        }
        return result;
    }

    public RestfulDslStringResponseDto createFolder(String folderPath) {
        RestfulDslStringResponseDto result = new RestfulDslStringResponseDto()
                .setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
        if (StrUtil.isBlank(folderPath) || !folderPath.startsWith(RestfulDslConstants.PROJECT_ROOT_PATH)) {
            result.setMessage("【文件路径非法】" + folderPath);
            result.setSuccess(false);
            return result;
        }
        try {
            FileUtil.mkdir(folderPath);
        } catch (Exception e) {
            result.setMessage("【文件夹创建失败】" + e.getMessage());
            result.setSuccess(false);
            return result;
        }
        return result;
    }

    public RestfulDslStringResponseDto createOrUpdateFile(String filePath, String content) {
        RestfulDslStringResponseDto result = new RestfulDslStringResponseDto()
                .setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
        File file = FileUtil.file(filePath);
        if (StrUtil.isBlank(filePath) || !filePath.startsWith(RestfulDslConstants.PROJECT_ROOT_PATH)) {
            result.setMessage("【文件路径非法】" + file.getAbsolutePath());
            result.setSuccess(false);
            return result;
        } else if (file.isDirectory()) {
            result.setMessage("【不能以文件夹为目标】" + file.getAbsolutePath());
            result.setSuccess(false);
            return result;
        }
        FileUtil.writeUtf8String(content, file);
        return result;
    }

    public RestfulDslCodeTemplateResponseDto getTemplateContentByImportPath(String tempPath, String importPath) {
        RestfulDslCodeTemplateResponseDto result = new RestfulDslCodeTemplateResponseDto()
                .setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
        try {
            File templateFile = FileUtil.file(tempPath);
            String targetPath = templateFile.getPath();
            targetPath = targetPath.substring(0, targetPath.lastIndexOf(File.separator) + 1);
            targetPath += importPath;
            File targetFile = FileUtil.file(targetPath);
            RestfulDslCodeTemplateDto dto = new RestfulDslCodeTemplateDto();
            dto.setContent(FileUtil.readString(targetFile, StandardCharsets.UTF_8));
            dto.setFilePath(targetFile.getAbsolutePath());
            dto.setFileSeparator(File.separator);
            dto.setNamespace(MapUtil.newHashMap());
            dto.setImports(MapUtil.newHashMap());
            result.setData(dto);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error("{} 读取文件异常", importPath);
            result.setMessage(importPath + " 读取文件异常");
        }
        return result;
    }

    public RestfulDslStringResponseDto getBasePackage() {
        RestfulDslStringResponseDto dto = new RestfulDslStringResponseDto()
                .setId(snowflake.nextId())
                .setTaskId(snowflake.nextId())
                .setSuccess(true);
        dto.setData(restfulDslProperties.getBasePackage());
        return dto;
    }
}
