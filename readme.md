# 一、快速开发

## 1. 创建数据库

在 docker 中创建 gmall-mysql 容器，然后在 Navicat 中创建 gmall 连接，接着在 gmall 中创建每个模块对应的数据库，并引入相关表数据：

```shell
docker run -d \
  --name gmall-mysql \
  -p 3306:3306 \
  -e TZ=Asia/Shanghai \
  -e MYSQL_ROOT_PASSWORD=123 \
  -v ./mysql/gmall-data:/var/lib/mysql \
  mysql:8.0.33
```

****
## 2. 拉取人人开源框架

### 2.1 配置后端服务

人人开源官网：[https://gitee.com/renrenio](https://gitee.com/renrenio)

拉取 renren-fast 和 renren-fast-vue：

```shell
git clone https://gitee.com/renrenio/renren-fast.git
git clone https://gitee.com/renrenio/renren-fast-vue.git
```

删除掉里面的 `.git` 文件后，将 renren-fast 拷贝到 gulimall 项目中，然后复制它的 MySQL 建表语句，进入 Navicat 创建 gulimall_admin 数据库，并创建表。
修改 application.yml 文件，将该项目连接的数据库信息修改为自己创建的：

```yaml
url: jdbc:mysql://127.0.0.1:3306/gulimall_admin?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
username: root
password: 123
```

启动 renren-fast 项目，访问 http://localhost:8080/ ，因为还没配置前端页面，所以访问返回错误信息：

```json
{
    "msg": "invalid token",
    "code": 401
}
```

****
### 2.2 配置前端服务

#### 2.2.1 安装 node

node 官网：[https://nodejs.org/zh-cn](https://nodejs.org/zh-cn)

1、双击下载的 .msi 文件，弹出安装向导，点击 Next

2、路径自行选择

3、配置安装选项

- 勾选 Node.js runtime（必选，Node.js 运行环境）
- 勾选 npm package manager（必选，Node.js 包管理工具）
- 可选 Add to PATH（自动添加环境变量）

4、验证安装

```shell
# 查看 Node.js 版本
node -v
# 查看 npm 版本
npm -v
```

5、配置 npm 镜像

因为官方的镜像在国内下载速度较慢，可以配置一些国内的镜像，在命令行中执行以下命令，将 npm 的默认镜像设置为淘宝镜像：

```shell
npm config set registry https://registry.npmmirror.com
```

配置完成后，可以通过以下命令查看当前 npm 使用的镜像源地址：

```shell
npm config get registry
# 打印结果
https://registry.npmmirror.com
```

6、恢复官方镜像

因为某些原因（如需要下载特定只有官方镜像源才有的包），想要恢复使用 npm 官方镜像源，可以执行以下命令：

```shell
npm config set registry https://registry.npmjs.org/
```

****
#### 2.2.2 nvm 安装

> nvm（Node Version Manager）是一款方便管理多个 Node.js 版本的工具，支持在不同项目版本间快速切换 Node 版本，非常适合开发环境使用。
> nvm-windows 官网：[https://github.com/coreybutler/nvm-windows/releases](https://github.com/coreybutler/nvm-windows/releases)

安装前需要先把单独安装的 node.js 卸载，避免与 nvm 管理的版本冲突：

1、进入官网选择 nvm-setup.exe 进行下载

2、按照提示点击 Next

3、因为新版本自动配置环境变量，所以不需要手动配置也可以随意下载 node

4、如果需要手动配置环境变量，就进入电脑系统的高级配置，在系统变量中找到 Path，然后新建一个 Path，把 nvm 的安装路径添加进去就行了

5、验证

```shell
# 查看 nvm 版本
nvm -v
```

使用步骤：

1、安装指定版本 node

因为使用了 nvm 来下载 node，所以 node 会被下载到 nvm 的安装目录，然后在里面创建带有版本号的文件，并且安装 node 时会自动安装 npm

```shell
# 安装最新的 LTS 版本（推荐，长期支持版，稳定）
nvm install lts

# 安装指定版本（例如安装 18.18.0，版本号可从 Node 官网查询）
nvm install 18.18.0

# 安装最新的测试版（Current 版，含最新特性，可能不稳定）
nvm install node  # "node" 代表最新版
```

例如：

```shell
nvm install 16
# 打印结果
Downloading node.js version 16.20.2 (64-bit)...
Extracting node and npm...
Complete
Installation complete.
If you want to use this version, type:

nvm use 16.20.2
```

2、查看已安装的 node 版本

```shell
# 列出所有已安装的版本（带 * 的是当前正在使用的版本）
nvm list

# 查看可安装的所有 Node 版本（可选版本列表）
nvm list available  # Windows 专用
```

```shell
22.18.0
* 18.18.0 (Currently using 64-bit executable)
```

3、切换到已安装的 node 版本

```shell
# 切换到指定版本（例如切换到 18.18.0）
nvm use 18.18.0

# 切换到 LTS 版本（如果安装了多个 LTS，会切换到最新的那个）
nvm use lts
```

4、设置默认 node 版本

```shell
# 把 18.18.0 设为默认版本（替换为你的常用版本）
nvm alias default 18.18.0
```

5、卸载 node

```shell
# 卸载指定版本（例如卸载 16.14.0）
nvm uninstall 16.14.0
```

****
#### 2.2.3 配置 renren-fast-vue

1、安装前端依赖

将 renren-fast-vue 用 VS Code 打开，然后打开内置的控制台并输入：

```shell
npm install
```

虽然在下载 node 的时候包含了 npm，但前端项目运行需要依赖很多第三方模块，而这些模块不会自带，必须通过 npm install 下载项目所需的依赖。一个前端项目的根目录下，
一般会有一个 package.json 文件，它里面记录了这个项目所需要的依赖模块，项目要运行则至少需要这些依赖。

下载过程出现：

```text
npm ERR! code 1
npm ERR! path D:\JavaBasicNotes\gulimall-front\renren-fast-vue\node_modules\chromedriver
npm ERR! command failed
npm ERR! command C:\WINDOWS\system32\cmd.exe /d /s /c node install.js
npm ERR! Downloading https://chromedriver.storage.googleapis.com/2.27/chromedriver_win32.zip
npm ERR! Saving to C:\Users\123\AppData\Local\Temp\chromedriver\chromedriver_win32.zip
npm ERR! node:events:491
npm ERR!       throw er; // Unhandled 'error' event
npm ERR!       ^
npm ERR!
npm ERR! Error: read ECONNRESET
npm ERR!     at TLSWrap.onStreamRead (node:internal/stream_base_commons:217:20)
npm ERR! Emitted 'error' event on ClientRequest instance at:
npm ERR!     at TLSSocket.socketErrorListener (node:_http_client:494:9)
npm ERR!     at TLSSocket.emit (node:events:513:28)
npm ERR!     at emitErrorNT (node:internal/streams/destroy:157:8)
npm ERR!     at emitErrorCloseNT (node:internal/streams/destroy:122:3)
npm ERR!     at processTicksAndRejections (node:internal/process/task_queues:83:21) {
npm ERR!   errno: -4077,
npm ERR!   code: 'ECONNRESET',
npm ERR!   syscall: 'read'
npm ERR! }
```

不知道什么原因报错，但是根据查询可以使用如下命令忽略安装这个依赖，使前端项目可以正常运行：

```shell
npm install --ignore-scripts
```

2、运行前端项目

运行后端 renren-fast，并在前端 renren-fast-vue 的控制台中输入：

```shell
npm run dev
```

输入命令后，前端会开始初始化，但是在此过程中报错：

```text
 error  in ./src/views/demo/ueditor.vue

Module build failed: Error: ENOENT: no such file or directory, scandir 'D:\JavaBasicNotes\gulimall-front\renren-fast-vue\node_modules\node-sass\vendor'
    ...  

 error  in ./src/views/demo/echarts.vue

Module build failed: Error: ENOENT: no such file or directory, scandir 'D:\JavaBasicNotes\gulimall-front\renren-fast-vue\node_modules\node-sass\vendor'
    ...    

 error  in ./src/views/common/login.vue

Module build failed: Error: ENOENT: no such file or directory, scandir 'D:\JavaBasicNotes\gulimall-front\renren-fast-vue\node_modules\node-sass\vendor'
    ...   

 error  in ./src/views/common/404.vue

Module build failed: Error: ENOENT: no such file or directory, scandir 'D:\JavaBasicNotes\gulimall-front\renren-fast-vue\node_modules\node-sass\vendor'
    ...      

 error  in ./src/views/modules/sys/menu-add-or-update.vue

Module build failed: Error: ENOENT: no such file or directory, scandir 'D:\JavaBasicNotes\gulimall-front\renren-fast-vue\node_modules\node-sass\vendor'
    ...      

 error  in ./src/assets/scss/index.scss

Module build failed: Error: ENOENT: no such file or directory, scandir 'D:\JavaBasicNotes\gulimall-front\renren-fast-vue\node_modules\node-sass\vendor'
    ... 
```

虽然全是不同文件的 error，但是它们都是因为找不到 vendor 文件，也就是 node-sass 的一个二进制文件，这是一个常见问题，
node-sass 安装过程中需要下载对应平台的二进制文件（即 vendor 目录下的 .node 文件），如果因为网络、权限或其他问题下载失败，就会报错。所以需要重新下载 node-sass，
命令如下：

```shell
# 配置淘宝镜像
npm install -g cnpm --registry=https://registry.npmmirror.com
cnpm install node-sass --save
# 等价于
npm install node-sass --save --registry=https://registry.npmmirror.com
```

然后重新启动前端项目 npm run dev，进入 8081 人人快速开发平台页面，输入账号密码（都是 admin）进入管理员页面。

****
## 3. 逆向工程搭建

从人人开源框架中拉取代码生成器并放到该项目中：

```s
git clone https://gitee.com/renrenio/renren-generator.git
```

这个服务里面有个 generator.properties 文件，它是用来配置代码生成器的一些基本信息的，例如该文件里就要修改一下扫描的包名等：

```properties
#代码生成器，配置信息

mainPath=com.project
#包名
package=com.project.gulimall
#模块名
moduleName=product
#作者
author=cell
#Email
email=cell002919@gmail.com
#表前缀(用来过滤表前缀，类名不会包含表前缀)
tablePrefix=pms_
```

因为该服务是基于数据库的表来生成 MVC 代码的，所以需要配置一下数据库的信息，让它连接正确的数据库，这里选择的是 pms 数据库：

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    #MySQL配置
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/gulimall_pms?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123
```

配置完成后，运行该服务，它绑定的是 80 端口，直接访问 localhost:80，进入 "人人代码生成器" 页面，在控制台中可以看到选择的 pms 数据库的所有表，勾选住它们，然后点击 "生成代码"，
它会下载一个 .zip 文件，解压后将 main 文件放到对应的 gulimall-product 服务中，因为它自动生成的代码引入了很多功能，例如 MyBatisPlus、PageHelper 和 一些自定义的代码，
针对非自定义的只需要引入对应的依赖即可，而那些自定义的例如 utils、xss 什么的，可以从 renren-fast 里面找到，而大多的自动生成的代码都会用到这些，所以可以把这些放到一个公共模块中，
例如 [gulimall-common](./gulimall-common)。

需要注意的是：在 renren-generator 服务中，有个 template 文件，里面放的都是 xxx.xx.vm 文件，这些就是用来构建具体的包的，例如 [Controller.java.vm](./src/main/resources/template/Controller.java.vm)，
它就是构建 Controller 层的模板，因为它里面用到了一个 @RequiresPermissions("${moduleName}:${pathName}:list") 注解，但是目前阶段用不到，为了避免每次生成的代码都自带这个东西，
就可以在这个模板中注释掉这个注解，然后再把生成的 Controller 重新放到 gulimall-product 中。

****
## 4. 配置并注册微服务基本 CRUD 功能

### 4.1 整合 MyBatis-Plus

1、在 common 服务中导入依赖

```xml
<!--MyBatis-plus-->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.8</version>
</dependency>
<!--mysql 驱动-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

2、在对应服务中配置数据源与端口号

```yaml
spring:
  datasource:
    username: root
    password: 123
    url: jdbc:mysql://127.0.0.1:3306/gulimall_pms?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
mybatis-plus:
  mapper-locations: classpath:/mapper/**/*.xml
  global-config:
    db-config:
      id-type: auto # 主键自增
```

3、在启动类上添加 @MapperScan("com.project.gulimall.product.dao") 扫描 Mapper 文件

4、其余服务配置同理

****
# 二、分布式组件

## 1. Spring Cloud Alibaba

> Spring Cloud Alibaba 致力于提供微服务开发的一站式解决方案。此项目包含开发分布式应用微服务的必需组件，方便开发者通过 Spring Cloud 编程模型轻松使用这些组件来开发分布式应用服务。

SpringCloud 的几大痛点：

- 部分组件停止维护和更新，给开发带来不便 
- 部分环境搭建复杂，没有完善的可视化界面，需要大量的二次开发和定制
- 配置复杂，难以上手，部分配置差别难以区分和合理应用

SpringCloud Alibaba 的优势：

- 阿里使用过的组件经历了考验，性能强悍，设计合理
- 成套的产品搭配完善的可视化界面给开发运维带来极大的便利
- 搭建简单，学习曲线低

结合 SpringCloud Alibaba，该项目最终的技术搭配方案：

- SpringCloud Alibaba - Nacos：注册中心（服务发现/注册）
- SpringCloud Alibaba - Nacos：配置中心（动态配置管理）
- SpringCloud - Ribbon：负载均衡
- SpringCloud - Feign：声明式 HTTP 客户端（调用远程服务）
- SpringCloud Alibaba - Sentinel：服务容错（限流、降级、熔断）
- SpringCloud - Gateway：API 网关（webflux 编程模式）
- SpringCloud - Sleuth：调用链监控
- SpringCloud Alibaba - Seata：原 Fescar，即分布式事务解决方案

项目中的版本选择：

- 2023.x 分支对应的是 Spring Cloud 2023 与 Spring Boot 3.2.x，最低支持 JDK 17。
- 2022.x 分支对应的是 Spring Cloud 2022 与 Spring Boot 3.0.x，最低支持 JDK 17。
- 2021.x 分支对应的是 Spring Cloud 2021 与 Spring Boot 2.6.x，最低支持 JDK 1.8。
- 2020.0 分支对应的是 Spring Cloud 2020 与 Spring Boot 2.4.x，最低支持 JDK 1.8。
- 2.2.x 分支对应的是 Spring Cloud Hoxton 与 Spring Boot 2.2.x，最低支持 JDK 1.8。
- greenwich 分支对应的是 Spring Cloud Greenwich 与 Spring Boot 2.1.x，最低支持 JDK 1.8。
- finchley 分支对应的是 Spring Cloud Finchley 与 Spring Boot 2.0.x，最低支持 JDK 1.8。
- 1.x 分支对应的是 Spring Cloud Edgware 与 Spring Boot 1.x，最低支持 JDK 1.7。

在 common 服务中引入依赖：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2021.0.1.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

****
## 2. Nacos

### 2.1 在 Docker 中下载安装 Nacos

1、因为将来可能部署 Nacos 集群，所以需要配合 MySQL 存储数据，所以需要找一下 Nacos 的初始化 sql 脚本，在 MySQL 中执行（一定要使用对应版本的 sql）。

2、指定 Nacos 的运行环境

因为 Nacos 默认的配置（如内存存储、内置数据库）仅适合快速测试，实际部署时需要根据场景调整，所以可以编写一个配置文件修改这些配置（参考 SpringCloud-Notes 笔记），
但是也可以直接通过命令来指定：

```shell
docker run -d \
  --name gmall-nacos \
  -p 8848:8848 \
  -e PREFER_HOST_MODE=hostname \
  -e MODE=standalone \
  -e SPRING_DATASOURCE_PLATFORM=mysql \
  -e MYSQL_SERVICE_HOST=xxx.xxx.xxx.xxx \ # 使用虚拟机的 ip
  -e MYSQL_SERVICE_DB_NAME=nacos_config \
  -e MYSQL_SERVICE_PORT=3306 \
  -e MYSQL_SERVICE_USER=root \
  -e MYSQL_SERVICE_PASSWORD=123 \
  -e MYSQL_SERVICE_DB_PARAM="characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai" \
  nacos/nacos-server:v2.2.3
```

需要注意的是 wsl2 中的 ip 地址在重启后可能发生改变，所以我使用的是宿主机的本地 ip 127.0.0.1 来连接的。

```text
PREFER_HOST_MODE=hostname
MODE=standalone
SPRING_DATASOURCE_PLATFORM=mysql
MYSQL_SERVICE_HOST=host.docker.internal
MYSQL_SERVICE_DB_NAME=nacos_config
MYSQL_SERVICE_PORT=3306
MYSQL_SERVICE_USER=root
MYSQL_SERVICE_PASSWORD=123
MYSQL_SERVICE_DB_PARAM=characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
```

需要注意的是：必须手动设置了上面的文件（custom.env），host.docker.internal 才能被正常解析，如果直接使用命令的方式，会导致微服务无法连接上 nacos。

```shell
docker run -d \
--name gmall-nacos \
--env-file ./custom.env \
-p 8848:8848 \
-p 9848:9848 \
-p 9849:9849 \
--restart=always \
nacos/nacos-server:v2.2.3
```

- PREFER_HOST_MODE=hostname：指定 Nacos 注册到服务端时使用容器 hostname 而非 IP，因为 Docker 网络中 IP 可能动态变化的问题，用 hostname 更稳定
- MODE=standalone：声明 Nacos 以单机模式运行
- MYSQL_SERVICE_HOST=host.docker.internal：Docker 容器中访问宿主机数据库的特殊地址，避免直接使用 IP（这里 MySQL 使用的是 127.0.0.1）

下载成功后，启动并访问：http://localhost:8848/nacos

****
### 2.2 微服务注册 Nacos

1、引入依赖

```xml
<!--版本由 spring-cloud-alibaba 决定-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

2、在 application.yaml 配置文件中配置 Nacos Server 地址与注册名

```yaml
spring:
  application:
    name: gulimall-coupon
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
```

3、在启动类上使用 @EnableDiscoveryClient 注解开启服务注册与发现功能

```java
@EnableDiscoveryClient
public class GulimallCouponApplication {
}
```

****
### 2.3 Nacos 配置中心

1、引入依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
<!--读取bootstrap文件-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```

2、创建 bootstrap.yaml 文件，该配置文件会优先于 application.yaml 加载。

```yaml
spring:
  application:
    name: gulimall-coupon

  cloud:
    nacos:
      config:
        server-addr: 127.0.0.1:8848
        file-extension: yaml # 文件后缀名
        shared-configs: # 共享配置
          - dataId: xx.yaml
```

需要注意的是，如果这里没有配置共享配置 shared-configs 的话，默认会寻找与设置的服务名同名的文件，也就是会寻找 gulimall_coupon.yaml。并且由于 Nacos 共享配置具有热更新特性，
所以微服务可以实时获取这些定义在共享配置中的数据，但如果想要可以实时完成自动注入的话，就需要在类上添加 @RefreshScope 注解，但有些情况可以不用，
比如使用了 @ConfigurationProperties(prefix = "my.config")，因为这个注解内部自导 @RefreshScope。

****
### 2.4 命名空间与配置分组

命名空间：

命名空间用于实现环境级别的配置隔离，在 Nacos 的控制台中可以创建多个命名空间（初始的默认命名空间为 public），例如 dev、test、prod 等，用来运行不同开发时期的配置，
或者用服务名命名，让每个服务的配置隔离开来。创建命名空间后，会生成一个 ID，通过显示使用它来指定当前服务使用的配置文件属于哪个命名空间（不能直接使用命名空间的名字）：

```yaml
spring:
  application:
    name: gulimall_coupon
  cloud:
    nacos:
      config:
        server-addr: 127.0.0.1:8848
        file-extension: yaml # 文件后缀名
        namespace: 9ac2b1b6-9928-4150-b1b7-893fc53d318b # 创建了一个 dev 命名空间，通过使用 ID 来指定使用该命名空间下的配置文件
```

配置分组：

配置分组是逻辑上的分类，可以对同一个 dataId（取的文件名，具体参考 SpringCloud-Notes）做进一步划分。在配置列表创建配置时，可以自定义 Group（默认 DEFAULT_GROUP），
在配置文件中需要显示使用该分组的名字来指定某个具体的配置文件，不指定表示使用默认分组：

```yaml
spring:
  application:
    name: gulimall_coupon
  cloud:
    nacos:
      config:
        server-addr: 127.0.0.1:8848
        file-extension: yaml # 文件后缀名
        namespace: 9ac2b1b6-9928-4150-b1b7-893fc53d318b # 创建了一个 dev 命名空间，通过使用 ID 来指定使用该命名空间下的配置文件
        group: 1111 # 使用分组为 1111 的 gulimall_coupon.yaml 配置文件
```

以上都能对配置环境进行隔离，命名空间范围更大，配置分组则是进一步的隔离。

****
### 2.5 加载多个配置集

Spring Cloud Alibaba 提供了两种加载多个配置集的方式：

1、extension-configs：用于加载当前项目额外的配置文件，优先级大于 shared-configs，在 shared-configs 之后加载

```yaml
spring:
  application:
    name: gulimall_coupon
  cloud:
    nacos:
      config:
        server-addr: 127.0.0.1:8848
        file-extension: yaml
        extension-configs:
          - data-id: coupon-db.yaml
            group: DEFAULT_GROUP
            refresh: true
          - data-id: common-redis.yaml
            group: COMMON_GROUP
            refresh: true
```

2、shared-configs：用于加载多个项目之间共享的公共配置

```yaml
spring:
  application:
    name: gulimall_coupon
  cloud:
    nacos:
      config:
        server-addr: 127.0.0.1:8848
        file-extension: yaml

        shared-configs:
          - data-id: common-base.yaml
            group: DEFAULT_GROUP
            refresh: true
            namespace: xxx
          - data-id: common-log.yaml
            group: DEFAULT_GROUP
            refresh: true
            namespace: xxx
```

需要注意的是，extension-configs 和 shared-configs 内部不能再写 namespace，也就是说一个 bootstrap.yaml 文件只能指定一个 namespace，如果需要区分共享文件和私有文件的话，
只能按照 group 来隔离。

****
# 三、商品服务

## 1. 基础概念

### 1.1 三级分类

gulimall_pms 中有一张商品分类表 pms_category，表结构如下：

以下是根据你提供的数据库表结构信息生成的 Markdown 表格：

| 名称         | 类型    | 长度 | 小数点 | 不是 null | 虚拟 | 键 | 注释                 |
| ------------ | ------- | ---- | ------ |---------| ---- |---| -------------------- |
| cat_id       | bigint  |      |        | √       |      | √ | 分类 id              |
| name         | char    | 50   |        |         |      |   | 分类名称             |
| parent_cid   | bigint  |      |        |         |      |   | 父分类 id            |
| cat_level    | int     |      |        |         |      |   | 层级                 |
| show_status  | tinyint |      |        |         |      |   | 是否显示[0不显示，1显示] |
| sort         | int     |      |        |         |      |   | 排序                 |
| icon         | char    | 255  |        |         |      |   | 图标地址             |
| product_unit | char    | 50   |        |         |      |   | 计量单位             |
| product_count| int     |      |        |         |      |   | 商品数量             | 

每个商品用 parent_cid 来表示某个商品是我的上一级，如果某个商品是一级存在，那么它就没有父分类 id，即 parent_cid = 0，而每个商品还自带一个 sort，它用来排序，越大的排越后。
现在要实现：查询所有商品分类，并且展示它们的分类关系，即一层包一层的那种。

Controller 层：

@RequestParam 注解用于从请求中获取参数，但其默认属性 required 为 true（即默认要求参数必须存在）。但有一个例外：当参数类型是 `Map<String, Object>` 时，
Spring MVC 会特殊处理，即使没有请求参数，也会自动创建一个空的 Map 对象，而不会抛出 "参数缺失" 的错误。因为 Map 类型被设计为接收所有请求参数的集合，如果没有参数，
就返回一个空集合（类似空列表），而不是报错。

```java
/**
 * 查出所有分类及其子分类，以树形结构组装起来
 */
@RequestMapping("/list/tree")
public R list(@RequestParam Map<String, Object> params){
    List<CategoryEntity> entities = categoryService.listWithTree();
    return R.ok().put("data", entities);
}
```

Service 层：

先利用 dao 获取出所有的 CategoryEntity 实体，然后从里面筛选出一级菜单，也就是上面提到的 parent_cid = 0 的数据。然后需要对每一个一级菜单进行查找它们各自对应的子菜单（即二级菜单），
而每个二级菜单又是三级菜单的父类菜单，所以查找到二级菜单的时候需要递归的查找属于它的三级菜单，以此类推。

```java
@Override
public List<CategoryEntity> listWithTree() {
    // 1. 查出所有分类
    List<CategoryEntity> entities = categoryDao.selectList(null);
    // 2. 组装成父子的树形结构
    // 找出所有的一级分类，即该分类没有父类，也就是父类的 id 为 0
    List<CategoryEntity> level1Menu = entities.stream()
            // 筛选一级菜单
            .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
            // 根据这个父类找它的子菜单
            .map(menu -> {
                // 调用该方法递归地找到所有子菜单
                menu.setChildren(getChildren(menu, entities));
                return menu;
            })
            // 对一级菜单进行升序排序
            .sorted(Comparator.comparingInt(menu -> menu.getSort() == null ? 0 : menu.getSort()))
            .collect(Collectors.toList());
    return level1Menu;
}
```

这个 getChildren 方法与上面的逻辑类似，先找到那些 parent_cid 等于一级菜单的 cat_id 的菜单，它们就是二级菜单。筛选到后就再次找它们的下一级菜单，当查找到最后一级菜单时，
就无法满足 categoryEntity -> categoryEntity.getParentCid() == root.getCatId()，因为它们没有子菜单的 parentCid 指向它们，即结束递归。然后依次从子菜单开始排序，
直到一级菜单。

```java
/**
 * 获取子菜单
 * @param root 当前菜单
 * @param all 所有菜单
 * @return
 */
private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
    List<CategoryEntity> children = all.stream()
            // 二级菜单 categoryEntity
            .filter(categoryEntity -> categoryEntity.getParentCid() == root.getCatId())
            // 找二级菜单的子菜单
            .map(categoryEntity -> {
                categoryEntity.setChildren(getChildren(categoryEntity, all));
                return categoryEntity;
            })
            // 对子菜单进行排序
            .sorted(Comparator.comparingInt(categoryEntity -> categoryEntity.getSort() == null ? 0 : categoryEntity.getSort()))
            .collect(Collectors.toList());
    return children;
}
```

最终结果：

```json
{
  "msg": "success",
  "code": 0,
  "data": [
    {
      "catId": 1,
      "name": "图书、音像、电子书刊",
      "parentCid": 0,
      "catLevel": 1,
      "showStatus": 1,
      "sort": 0,
      "icon": null,
      "productUnit": null,
      "productCount": 0,
      "children": [
        {
          "catId": 22,
          "name": "电子书刊",
          "parentCid": 1,
          "catLevel": 2,
          "showStatus": 1,
          "sort": 0,
          "icon": null,
          "productUnit": null,
          "productCount": 0,
          "children": [
            {
              "catId": 166,
              "name": "网络原创",
              "parentCid": 22,
              "catLevel": 3,
              "showStatus": 1,
              "sort": 0,
              "icon": null,
              "productUnit": null,
              "productCount": 0,
              "children": []
            },
            {
              "catId": 165,
              "name": "电子书",
              "parentCid": 22,
              "catLevel": 3,
              "showStatus": 1,
              "sort": 1,
              "icon": null,
              "productUnit": null,
              "productCount": 0,
              "children": []
            },
            {
              "catId": 167,
              "name": "数字杂志",
              "parentCid": 22,
              "catLevel": 3,
              "showStatus": 1,
              "sort": 2,
              "icon": null,
              "productUnit": null,
              "productCount": 0,
              "children": []
            },
            {
              "catId": 168,
              "name": "多媒体图书",
              "parentCid": 22,
              "catLevel": 3,
              "showStatus": 1,
              "sort": 3,
              "icon": null,
              "productUnit": null,
              "productCount": 0,
              "children": []
            }
          ]
        }
      ]
    }
  ]
}
```

****







