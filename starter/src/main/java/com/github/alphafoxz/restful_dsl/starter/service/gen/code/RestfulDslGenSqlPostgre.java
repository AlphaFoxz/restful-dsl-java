package com.github.alphafoxz.restful_dsl.starter.service.gen.code;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.alphafoxz.restful_dsl.starter.toolkit.ParseRestfulSyntaxTreeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.StringJoiner;

/**
 * 通过restful文件创建数据库sql
 */
@Slf4j
@Service
public class RestfulDslGenSqlPostgre implements RestfulCodeGenerator {
    private static final String TAB = "    ";

    @Override
    public Set<CodeFile> genCodeFileSet(@NonNull ParseRestfulSyntaxTreeUtil.RestfulRootBean restfulRoot, String targetDir) {
        Set<CodeFile> result = CollUtil.newHashSet();
        CodeFile codeFile;
        codeFile = genSqlFile(restfulRoot, targetDir);
        if (codeFile != null) {
            result.add(codeFile);
        }
        for (ParseRestfulSyntaxTreeUtil.RestfulIncludeBean includeBean : restfulRoot.getIncludeBeanSet()) {
            codeFile = genSqlFile(includeBean, targetDir);
            if (codeFile != null) {
                result.add(codeFile);
            }
        }
        return result;
    }

    /*

create table if not exists preset_sys.psys_abac_dynamic_authorization
(
id                 bigint not null,
authorization_type char   not null,
subject_attr_set   jsonb  not null,
timeout_until      timestamp with time zone,
resource_id        bigint not null,
owner_subject_id   bigint not null,
target_subject_id  bigint,
constraint psys_abac_dynamic_authorization_pk
    primary key (id)
);

comment on table preset_sys.psys_abac_dynamic_authorization is '动态访问控制_授权表';

comment on column preset_sys.psys_abac_dynamic_authorization.id is '主键';

comment on column preset_sys.psys_abac_dynamic_authorization.authorization_type is '授权类型 0主动 1被动';

comment on column preset_sys.psys_abac_dynamic_authorization.subject_attr_set is '授权主体属性集合';

comment on column preset_sys.psys_abac_dynamic_authorization.timeout_until is '授权过期时间';

comment on column preset_sys.psys_abac_dynamic_authorization.resource_id is '资源属性id';

comment on column preset_sys.psys_abac_dynamic_authorization.owner_subject_id is '资源所有者主体Id';

comment on column preset_sys.psys_abac_dynamic_authorization.target_subject_id is '授权目标主体Id';

 */
    @Nullable
    public CodeFile genSqlFile(ParseRestfulSyntaxTreeUtil.RestfulRootIface restfulRoot, @NonNull String genDir) {
        CodeFile codeFile = new CodeFile();
        String prefix = genDir + "/" + StrUtil.replace(restfulRoot.getRootBean().getTsNameSpace(), ".", "/");
        codeFile.setPath(prefix + "/" + restfulRoot.getFileName() + ".sql");
        codeFile.setFileName(restfulRoot.getFileName());
        ParseRestfulSyntaxTreeUtil.RootBean rootBean = restfulRoot.getRootBean();
        StringJoiner code = new StringJoiner("\n");

        for (ParseRestfulSyntaxTreeUtil.ClassBean classBean : rootBean.getClassList()) {
            String tableName = StrUtil.toUnderlineCase(classBean.getClassName());
            for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : classBean.getCommentList()) {
                code.add("-- " + commentBean.getCommentValue());
            }
            code.add("create table if not exists " + tableName);
            code.add("(");
            for (ParseRestfulSyntaxTreeUtil.ClassBean.ClassFieldBean fieldBean : classBean.getClassFieldList()) {
                String sqlTypeString = fieldBean.getType().sqlString() + (ParseRestfulSyntaxTreeUtil.Modifier.OPTIONAL.equals(fieldBean.getModifier()) ? "" : " not null") + ",";
                code.add(StrUtil.toUnderlineCase(fieldBean.getFieldName()) + TAB + sqlTypeString);
            }
            code.add("constraint " + tableName + "_pk\n" +
                    "    primary key (id)");
            code.add(");");
            if (classBean.getDoc() != null) {
                String formatStr = "comment on table {} is '{}';";
                code.add(StrUtil.format(formatStr, tableName, classBean.getDoc().getCommentValue()));
            }
            for (ParseRestfulSyntaxTreeUtil.ClassBean.ClassFieldBean fieldBean : classBean.getClassFieldList()) {
                for (ParseRestfulSyntaxTreeUtil.CommentBean commentBean : fieldBean.getCommentList()) {
                    code.add("-- " + commentBean.getCommentValue());
                }
                if (fieldBean.getDoc() != null) {
                    String formatStr = "comment on column {}.{} is '{}';";
                    code.add(StrUtil.format(formatStr, tableName, StrUtil.toUnderlineCase(fieldBean.getFieldName()), fieldBean.getDoc().getCommentValue()));
                }
            }
            code.add("");
        }
        String codeString = code.toString();
        if (StrUtil.isBlank(codeString)) {
            return null;
        }
        codeFile.setContent(codeString);
        return codeFile;
    }
}
