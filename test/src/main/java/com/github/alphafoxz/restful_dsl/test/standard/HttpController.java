package com.github.alphafoxz.restful_dsl.test.standard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.URLUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface HttpController {
    public static HttpStatus OK_200 = HttpStatus.OK;
    public static HttpStatus CREATED_201 = HttpStatus.CREATED;
    public static HttpStatus NO_CONTENT_204 = HttpStatus.NO_CONTENT;
    public static HttpStatus MOVED_PERMANENTLY_301 = HttpStatus.MOVED_PERMANENTLY;
    public static HttpStatus FOUND_302 = HttpStatus.FOUND;
    public static HttpStatus BAD_REQUEST_400 = HttpStatus.BAD_REQUEST;
    public static HttpStatus UNAUTHORIZED_401 = HttpStatus.UNAUTHORIZED;
    public static HttpStatus FORBIDDEN_403 = HttpStatus.FORBIDDEN;
    public static HttpStatus NOT_FOUND_404 = HttpStatus.NOT_FOUND;
    public static HttpStatus INTERNAL_SERVER_ERROR_500 = HttpStatus.INTERNAL_SERVER_ERROR;
    public static HttpStatus NOT_IMPLEMENTED_501 = HttpStatus.NOT_IMPLEMENTED;

    /**
     * 基于前端不可信这个原则，对于有默认值的属性每次都判空会导致大量重复代码
     * 所以提供一些简单的方法
     * Tips: 对于不可口空的参数则应该自己根据实际情况返回http错误码
     */
    public static class U {
        public static <T> T toBean(Object source, Class<T> clazz) {
            return BeanUtil.toBean(source, clazz);
        }

        public static ResponseEntity.BodyBuilder fileBodyBuilder(@NonNull String fileName) {
            return fileBodyBuilder(null, fileName);
        }

        /**
         * 创建一个文件响应体
         *
         * @param status   http状态码
         * @param fileName
         * @return
         */
        public static ResponseEntity.BodyBuilder fileBodyBuilder(@Nullable HttpStatus status, @NonNull String fileName) {
            if (status == null) {
                status = OK_200;
            }
            fileName = URLUtil.encode(fileName);
            return ResponseEntity.status(status)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
        }

        public static int intVal(Integer input, int defaultValue) {
            return input == null ? defaultValue : input;
        }

        public static Double doubleVal(Double input, Double defaultValue) {
            return input == null ? defaultValue : input;
        }

        public static String strVal(String input, String defaultValue) {
            return input == null ? defaultValue : input;
        }

        public static boolean boolVal(Boolean input, boolean defaultValue) {
            return input == null ? defaultValue : input;
        }
    }
}
