> 有问题联系Q573503318
```
因为不是springboot项目,所以没有也没有一个restful风格的后缀.  
不像boot的接口同名根据method = RequestMehtod.POST或者Delete
```
[数据库sql脚本](/帮助文档/数据库Sql脚本.zip) 
>Demo简述
```
    纯后端项目,配置和接口,nginx的配置反向代理
    因为时间关系目前只做了用户模块,品类模块,商品模块.
项目初始化配置
    maven引入了mybatis三剑客
    MyBatis-Generate
        这个maven插件可以快速生成Dao类, mapper配置文件和Model类, 可以自动查询数据库中的所有表, 然后生成可以访问表的基础对象类型, 和一些简单的增删改查操作, 但是复杂业务仍然需要手写SQL.
    Mybatis-plugin
        简称MP, 在mybatis的基础上只做增强不做改变，为简化开发、提高效率而生. 
    Mybatis-PageHelper
          分页插件, 使用时mybatis版本在3.3以上,
          原理: 通过aop再截获我们执行的sql的时候把相关数据再执行一次.
```

 

> 因为时间关系就不写接口文档了,也因为在公司开发的时候接口文档和测试都是写在数据库里,用浏览器去做测试的

>用户模块功能简述

会有一个高复用服务响应对象作为返回值-->[高复用类](src/main/java/com/mmall/common/ServerResponse.java)
```
用户密码MD5加密+盐
在登录成功时,把用户名密码set为null,再把对象传给前端
判断用户问题和答案一致时,创建个token,UUID+用户名为键,防止重置密码横向越权,避免接口暴露对数据库造成损失
重置密码时使用token验证
```     
> 品类模块
```
获取品类子节点--平级
节点的增删改查
获取当前分类id及递归子节点categoryId
```
[品类遍历递归算法](src/main/java/com/mmall/service/impl/CategoryServerImpl.java)

> 商品列表
```
商品列表
商品搜索
商品详情
商品上下架
新增
图标上传 springmvc的MultipartFile
富文本上传图片
分页用的PageHelper
```
上传成功之后
{"status":0,"data":{"uri":"8d355f65-6312-414b-8555-6f6323d016b8.png","url":"http://img.zqf.com/8d355f65-6312-414b-8555-6f6323d016b8.png"}}
图片是先存到Tomcat下的一个文件夹里,然后去链接ftp登录账号密码链接成功存到反向代理img文件夹下
前面的这个img.zqf.com网址是我用Nginx反向代理的

##Nginx配置
C:\Windows\System32\drivers\etc\hosts   
里面配置 127.0.0.1 img.zqf.com  
在Nginx/conf/nginx.conf里配置修改为[nginx.conf](/帮助文档/nginx.conf)  
并在Nginx/conf/下创建vhost文件夹    
vhost文件夹放置img.zqf.com.conf [img.zqf.conf](/帮助文档/img.zqf.com.conf)-->注意后缀是conf   

