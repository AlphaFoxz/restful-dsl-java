package com.github.alphafoxz.spring_boot_starter_restful_dsl.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.RestfulDslConstants;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.configuration.RestfulDslProperties;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.dtos.*;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.gen.restful.enums.SdkFileTypeEnum;
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

    public SdkListResponseDto checkErr() {
        final String restfulPath = RestfulDslConstants.SDK_GEN_RESTFUL_TEMPLATE_PATH;
        SdkListResponseDto result = new SdkListResponseDto().setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
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
                log.error("sdk初始化thrift各目录异常", e);
                errors.add("sdk初始化thrift各目录异常：" + ExceptionUtil.getSimpleMessage(e));
            }
        }
        if (!errors.isEmpty()) {
            result.setData(errors);
            result.setSuccess(false);
        }
        return result;
    }

    public SdkListResponseDto checkRestApiImplements() {
        SdkListResponseDto result = new SdkListResponseDto().setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
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

    public SdkStringResponseDto getJavaNamespace() {
        SdkStringResponseDto result = new SdkStringResponseDto().setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
        result.setData(restfulDslProperties.getBasePackage());
        return result;
    }

    public SdkCodeTemplateResponseDto getTemplateContentByPath(String filePath) {
        SdkCodeTemplateResponseDto result = new SdkCodeTemplateResponseDto()
                .setId(snowflake.nextId())
                .setTaskId(snowflake.nextId())
                .setSuccess(false);
        try {
            File file = FileUtil.file(filePath);
            String content = FileUtil.readUtf8String(file);
            SdkCodeTemplateDto template = new SdkCodeTemplateDto();
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

    public SdkFileTreeResponseDto getRestfulTemplateFileTree() {
        SdkFileTreeResponseDto result = new SdkFileTreeResponseDto().setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
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

    private SdkFileInfoDto readFileTree(File fileOrDir, int level) {
        SdkFileInfoDto dto = new SdkFileInfoDto();
        dto.setSeparator(File.separator);
        dto.setIsReadOnly(level <= 2);
        dto.setFilePath(fileOrDir.getAbsolutePath());
        dto.setFileName(fileOrDir.getName());
        dto.setParentDir(fileOrDir.getParentFile().getAbsolutePath());
        if (FileUtil.isFile(fileOrDir)) {
            // 文件类型
            dto.setExt(FileUtil.getSuffix(fileOrDir));
            dto.setFileType(SdkFileTypeEnum.LOCAL_FILE.getValue());
            dto.setIsEmpty(FileUtil.size(fileOrDir) == 0);
            dto.setContent(FileUtil.readUtf8String(fileOrDir));
            return dto;
        }
        // 目录类型
        dto.setFileType(SdkFileTypeEnum.LOCAL_DIR.getValue());
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

    public SdkListResponseDto deleteFile(String filePath) {
        File file = FileUtil.file(filePath);
        SdkListResponseDto dto = new SdkListResponseDto()
                .setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
        if (!file.exists() || !StrUtil.startWith(file.getAbsolutePath(), RestfulDslConstants.PROJECT_ROOT_PATH)) {
            dto.setMessage("【文件路径非法】" + file.getAbsolutePath());
            dto.setSuccess(false);
            return dto;
        }
        FileUtil.del(file);
        return dto;
    }

    public SdkStringResponseDto renameFile(String filePath, String newPath) {
        SdkStringResponseDto result = new SdkStringResponseDto()
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

    public SdkStringResponseDto createFolder(String folderPath) {
        SdkStringResponseDto result = new SdkStringResponseDto()
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

    public SdkStringResponseDto createOrUpdateFile(String filePath, String content) {
        SdkStringResponseDto result = new SdkStringResponseDto()
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

    public SdkCodeTemplateResponseDto getTemplateContentByImportPath(String tempPath, String importPath) {
        SdkCodeTemplateResponseDto result = new SdkCodeTemplateResponseDto()
                .setId(snowflake.nextId()).setTaskId(snowflake.nextId()).setSuccess(true);
        try {
            File templateFile = FileUtil.file(tempPath);
            String targetPath = templateFile.getPath();
            targetPath = targetPath.substring(0, targetPath.lastIndexOf(File.separator) + 1);
            targetPath += importPath;
            File targetFile = FileUtil.file(targetPath);
            SdkCodeTemplateDto dto = new SdkCodeTemplateDto();
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

    public SdkStringResponseDto getBasePackage() {
        SdkStringResponseDto dto = new SdkStringResponseDto()
                .setId(snowflake.nextId())
                .setTaskId(snowflake.nextId())
                .setSuccess(true);
        dto.setData(restfulDslProperties.getBasePackage());
        return dto;
    }
}
