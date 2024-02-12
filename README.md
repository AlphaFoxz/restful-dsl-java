[English](./README.md)

# RESTful-DSL (RESTful领域特定语言)

<img src="./.doc/logo.png" style="height: 8rem; width: auto;" alt="logo.png"/>

restful-dsl是一个RESTful风格接口领域特定语言。是“面向接口开发”的一个实现。

## 拓展名

`.restl`：意为RESTful Language。我想尽量让它的后缀更简单，但由于.rest已经被识别为.http别名了，所以在后面加了个L

## 语法

### 注释

#### 普通注释

注释中的内容将会原样生成到所有前、后端代码中，推荐写得足够详细，比如需求变更之类的

`````
// 这是一段普通注释
`````

#### 文档注释

注释中的内容将会用于文档化，每个字段或结构只支持一段文档注释，暂不支持多行

```
/* 这是一段文档注释 */
```

### 关键字-内置数据类型

#### **boolean**

布尔类型，用法：

```
/*是否为文件*/
boolean isFile
```

#### **byte**

字节（不推荐单独使用），用法：

```
/*下载文件接口*/
list<byte> download()
```

#### **i16** 或 **short**

短整形，i16与short在任何时候都完全等价，经过技术人员协商后约定其中一种即可。用法：

```
/*服务端口号*/
i16 port
```

#### **i32** 或 **int**

整形，i32与int在任何时候都完全等价，经过技术人员协商后约定其中一种即可。用法

```
/*文件数量*/
i32 fileCount
```

#### **i64** 或 **long**

长整型，i64与long在任何时候都完全等价，经过技术人员协商后约定其中一种即可。用法

```
/*文件大小（单位：字节）*/
i64 fileSize
```

#### **double**

浮点数，用法：

````
/*坐标经度*/
double lon
````

#### **binary**

二进制类型，用法：

```
/*上传文件*/
binary file
```

#### **string**

字符串，用法：

```
/*文件名*/
string fileName
```

#### **map**

键值对容器，用法：

````
/*文件树*/
map<string, SdkFileDto> fileMap
````

#### **list**

列表容器，用法：

```
/*文件列表*/
list<SdkFileDto> fileList
```

#### **set**

不重复列表容器，用法：

```
/*权限集合*/
set<string> roleList
```

#### **enum**

当作为字段类型时，表示对枚举的引用（文档级别），但实际上所有enum类型的字段在代码中都是i32类型。但是因为以这种方式进行了引用，所以在代码会生成对应的枚举供开发使用，后续的文档生成也会更详细。用法：

```
/*声明一个性别字段*/
enum<GenderEnum> gender
```

#### **void**

无类型，用在接口返回值，表示不关心返回值，只要请求成功就行了。用法：

```
/*埋点，推送用户操作数据*/
void userHistory(list<string> data)
```

**注：通常埋点会选择udp而非tcp协议，所以此功能暂定**

### 关键字-必填性修饰符号

#### **!**

表示 必填/非空。所有的字段如果不加必填性修饰符号，默认就是必填，所以推荐省略：

```
/*主键*/
i64 id!
```

它和不写`!`的情况完全等价

```
/*主键*/
i64 id
```

#### **?**

表示 非必填/可空。用法：

```
/*筛选标题*/
string title?
```

### 关键字-声明结构

#### **interface**

声明一个接口，这个接口会作为前后端交互的标准。详见下面的示例

#### **class**

声明一个类，这个类会作为复杂对象的建模，被interface所引用。详见下面的示例

#### **enum**

声明一个枚举对象。详见下面的示例

### 关键字-引用文件

#### **import**

引用另一个文件（相对路径），可以使用其中的class或enum，用法：

```
import "../dots/FileDto.restl"
```

### 关键字-命名空间

#### **namespace**

声明指定语言的命名空间，用于决定代码生成的结构、包名。用法：namespace [语言] [包名]

```
namespace java com.example
```

### 注解

注解可以标注在接口或方法上，用于实现各种增强性功能。注解可以是有参或无参，格式为`@someAnno(someValue1, someValue2)`

#### **@uri**

指定interface的uri前缀，用法：

```
@uri(/api/file)
interface FileApi {
}
```

#### **@getUri**

指定具体接口的get请求uri，支持通配符，通配符中的变量将会以请求头的方式传递（拼接为最终的url）。用法：

```
@postUri(/query/{id})
UserFile query(i64 id)
```

#### **@postUri**

指定具体接口的post请求uri，支持通配符，但post通常不会这么使用。用法：

```
@postUri(/edit)
boolean edit(UserSetting setting)
```

#### **@putUri**

指定具体接口的put请求uri（因为中国有很多公司要求禁用put请求，所以使用之前先确认网安防火墙规则），支持通配符。用法：

```
@put(/create)
UserSetting create(UserSetting setting)
```

#### **@patchUri**

指定具体接口的patch请求uri（因为中国有很多公司要求禁用patch请求，所以使用之前先确认网安防火墙规则），支持通配符。用法：

```
@patchUri(/update)
UserSetting update(UserSetting setting)
```

#### **@deleteUri**

指定具体接口的delete请求uri（因为中国有很多公司要求禁用delete请求，所以使用之前先确认网安防火墙规则），支持通配符。用法：

```
@deleteUri(/del/{id})
boolean del(i64 id)
```

#### **@page**

指定当前接口返回值为分页数据，生成的代码返回值会包装在分页对象中。用法：

```
@page
@getUri(/queryPage)
list<UserSetting> queryPage(i32 pageNum, i32 pageSize)
```

#### **@formData**

指定当前接口为表单提交的数据，请求头类型会设置为`Content-Type: multipart/form-data`。通常只在上传时使用。用法：

```
@formData
@postUri(/upload)
boolean upload(binary file, string aCustomAttr)
```

## 示例

### 例1：增删改查

`apis/UserApi.restl`内容

```
namespace java com.github.alphafoxz.restful_dsl.gen.restl.apis
namespace ts gen.biz_test.apis

import "../dtos/UserDto.restl"

/*用户接口*/
@uri(/user)
interface UserApi {
    /*根据id查询用户*/
    @getUri(/query/{id})
    UserDto.UserDto query(/*主键*/i64 id)

    //无需增加实体，就像调用方法一样去设计这个接口
    /*查询分页*/
    @page
    @getUri(/queryPage/{pageNum}/{pageSize})
    UserDto.UserDto queryPage(
            /*页码*/i32 pageNum,
            /*每页条数*/i32 pageSize,
            /*可选 筛选用户名*/string username?
    )

    /*注册用户*/
    @postUri(/register)
    UserDto.UserDto register()

    /*修改用户信息*/
    @postUri(/update)
    UserDto.UserSimpleDto update(/*用户信息*/UserDto.UserSimpleDto user)

    /*删除用户*/
    @getUri(/delete/{id})
    boolean delete(/*主键*/i64 id)
}
```

`dtos/UserDto.restl`内容

```
namespace java com.github.alphafoxz.restful_dsl.gen.restl.dtos
namespace ts gen.biz_test.dtos

import "../enums/UserEnum.restl"

/*用户简易dto*/
class UserSimpleDto {
    /*主键*/
    i64 id

    /*姓名*/
    string username

    /*用户类型*/
    enum<UserEnum.UserTypeEnum> userType

    /*权限*/
    list<string> roles
}

/*用户dto*/
class UserDto {
    /*主键*/
    i64 id

    /*姓名*/
    string username

    /*用户类型*/
    enum<UserEnum.UserTypeEnum> userType

    /*权限*/
    list<string> roles

    /*邮箱*/
    string email?

    /*性别*/
    UserEnum.UserGenderEnum gender?
}
```

`enums/UserEnum.restl`内容

```
namespace java com.github.alphafoxz.restful_dsl.gen.restl.enums
namespace ts gen.biz_test.enums

/*用户性别枚举*/
enum UserGenderEnum {
    /*男*/
    MALE = 0
    /*女*/
    FEMALE = 1
    /*未知*/
    UNKNOWN = 2
}

/*用户类型枚举*/
enum UserTypeEnum {
    /*管理员*/
    ADMIN = 0
    /*普通用户*/
    USER = 1
}
```

### 例2：上传/下载文件

`apis/FileApi.restl`内容

```
namespace java com.github.alphafoxz.restful_dsl.gen.restl.apis
namespace ts gen.biz_test.apis

/*文件接口*/
@uri(/file)
interface FileApi {
    //客户端对应类似这样一个表单：
    //<form>
    //   <input name="file" title="*请上传文件" type="file" />
    //   <input name="desc" title="[可选]输入一段描述" />
    //</from>
    /*上传文件*/
    @formData
    @postUri(/upload)
    boolean upload(
            /*二进制文件*/binary file,
            /*可选说明*/string desc?
    )

    //当返回值为list<byte>时，实际上生成的代码会自动优化为byte数组，而不是反直觉的list容器
    /*下载文件*/
    @getUri(/download)
    list<byte> download(/*主键*/i64 id)
}
```
