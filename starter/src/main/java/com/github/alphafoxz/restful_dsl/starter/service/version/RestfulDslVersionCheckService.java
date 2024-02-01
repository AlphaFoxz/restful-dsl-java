package com.github.alphafoxz.restful_dsl.starter.service.version;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.github.alphafoxz.restful_dsl.starter.RestfulDslConstants;
import com.github.alphafoxz.restful_dsl.starter.gen.restl.dtos.RestfulDslVersionCheckDto;
import com.github.alphafoxz.restful_dsl.starter.gen.restl.dtos.RestfulDslVersionCheckResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RestfulDslVersionCheckService {
    @Resource
    private RestfulDslVersionStoreService restfulDslVersionStoreService;
    @Resource
    private Snowflake snowflake;

    public RestfulDslVersionCheckResponse checkRestfulJava(@Nullable Long taskId) {
        RestfulDslVersionCheckResponse response = new RestfulDslVersionCheckResponse();
        response.setId(snowflake.nextId());
        if (taskId == null) {
            taskId = snowflake.nextId();
        }
        response.setTaskId(taskId);
        Map<String, Serializable> versionMap = restfulDslVersionStoreService.genRestfulStore().readFile();
        Map<String, RestfulDslVersionCheckDto> checkResult = MapUtil.newHashMap();
        for (File file : FileUtil.loopFiles(RestfulDslConstants.SDK_GEN_RESTFUL_TEMPLATE_PATH)) {
            RestfulDslVersionCheckDto dto = new RestfulDslVersionCheckDto();
            if (!StrUtil.endWithIgnoreCase(file.getName(), ".restl") || !file.isFile()) {
                continue;
            }
            String key = file.getAbsolutePath().replace(RestfulDslConstants.SDK_GEN_RESTFUL_TEMPLATE_PATH, "");
            String hash = SecureUtil.sha256(FileUtil.readUtf8String(file).replace("\r\n", "\n"));
            String generatedHash = versionMap.containsKey(key) ? versionMap.get(key).toString() : null;
            dto.setFilePath(key);
            dto.setSame(StrUtil.equals(hash, generatedHash));
            dto.setSha256(hash);
            String msg;
            if (dto.getSame()) {
                msg = "校验成功";
            } else if (generatedHash == null) {
                msg = "未检测到版本信息，该文件可能从未生成过java代码";
            } else {
                msg = "版本不一致，请重新生成java代码";
            }
            dto.setMessage(msg);
            checkResult.put(key, dto);
        }
        for (Map.Entry<String, Serializable> entry : versionMap.entrySet()) {
            if (!checkResult.containsKey(entry.getKey())) {
                RestfulDslVersionCheckDto dto = new RestfulDslVersionCheckDto();
                dto.setFilePath(entry.getKey());
                dto.setSha256(entry.getValue().toString());
                dto.setMessage("该文件生成过java代码，但这个restl模板现在已经不存在或改名了，请留意");
                dto.setSame(false);
                checkResult.put(entry.getKey(), dto);
            }
        }
        response.setMessage("检查成功");
        response.setData(checkResult.values().stream().sorted((dto1, dto2) -> StrUtil.compare(dto1.getFilePath(), dto2.getFilePath(), true)).collect(Collectors.toList()));
        response.setSuccess(true);
        return response;
    }

    public RestfulDslVersionCheckResponse getRestfulTemplateHash(@Nullable Long taskId) {
        RestfulDslVersionCheckResponse response = new RestfulDslVersionCheckResponse();
        response.setId(snowflake.nextId());
        if (taskId == null) {
            taskId = snowflake.nextId();
        }
        response.setTaskId(taskId);

        List<RestfulDslVersionCheckDto> resultList = CollUtil.newArrayList();
        for (File file : FileUtil.loopFiles(RestfulDslConstants.SDK_GEN_RESTFUL_TEMPLATE_PATH)) {
            RestfulDslVersionCheckDto dto = new RestfulDslVersionCheckDto();
            if (!StrUtil.endWithIgnoreCase(file.getName(), ".restl") || !file.isFile()) {
                continue;
            }
            String key = file.getAbsolutePath().replace(RestfulDslConstants.SDK_GEN_RESTFUL_TEMPLATE_PATH, "");
            String hash = SecureUtil.sha256(FileUtil.readUtf8String(file));
            dto.setFilePath(key);
            dto.setSame(false);
            dto.setSha256(hash);
            resultList.add(dto);
        }
        response.setMessage("请求成功");
        response.setData(resultList);
        response.setSuccess(true);
        return response;
    }
}
