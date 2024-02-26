package com.github.alphafoxz.restful_dsl.test.controller;

import com.github.alphafoxz.restful_dsl.test.gen.restl.dtos._compile_only.UserDtoFields;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 确保对生成的DtoFields接口的引用可以直接优化为常量字符串，而不需要导包
 * 这样使用者可以有选择性地在打包时排除掉这些文件了
 */
@RestController
@RequestMapping("/test")
public class CompileTestController {
    @GetMapping("/" + UserDtoFields.USER_TYPE)
    public String testCompile() {
        return UserDtoFields.EMAIL;
    }
}
