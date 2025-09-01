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

## 1. 三级分类

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
            .filter(categoryEntity -> Objects.equals(categoryEntity.getParentCid(), root.getCatId()))
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
## 2. 配置网关

### 2.1 配置路由

启动 renren-fast-vue 前端项目，进入人人快速开发平台后，通过系统管理的菜单管理，创建一个新的一级目录，取名商品系统，在这个目录里面放有关商品的数据，然后再创建一个菜单归属于商品系统，
叫做分类服务，就在这个页面展示后端获取到的分类数据。在前端页面通过一个树形结构展示分类数据，所以需要在这个页面发送一个查询的请求：

```vue
getMenus () {
    this.$http({
      url: this.$http.adornUrl('/product/category/list/tree'),
      method: 'get'
    }).then(({data}) => {
      console.log('成功获取到菜单数据...', data)
    })
}
```

但是在前端的 index.js 文件中可以看到它发送的请求地址是以前端作为基础的：

```js
/**
 * 开发环境
 */
;(function () {
  window.SITE_CONFIG = {};

  // api接口请求地址
  window.SITE_CONFIG['baseUrl'] = 'http://localhost:8001/renren-fast';

  // cdn地址 = 域名 + 版本号
  window.SITE_CONFIG['domain']  = './'; // 域名
  window.SITE_CONFIG['version'] = '';   // 版本号(年月日时分)
  window.SITE_CONFIG['cdnUrl']  = window.SITE_CONFIG.domain + window.SITE_CONFIG.version;
})();
```

而后端查询分类数据的请求路径为：localhost:10000/product/category/list/tree，所以这里需要配置网关路由，让网关来动态的发送请求。
所以需要在 gulimall_gateway 中配置路由器并引入相关依赖：

```yaml
spring:
  application:
    name: gulimall-gateway
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
    gateway:
      routes:
        - id: ...
          uri: lb://...
          predicates:
            - Path=...
```

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

这里就把前端的发送请求更换成：

```js
// api接口请求地址
window.SITE_CONFIG['baseUrl'] = 'http://localhost:88/api'
```

而需要让网关能够识别到这个发送的请求，就需要配置：

```yaml
spring:
  application:
    name: gulimall-gateway
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
    gateway:
      routes:
        - id: admin_route
          uri: lb://renren-fast # 负载均衡到 renren-fast 服务
          predicates:
            - Path=/api/** # 只要发送的请求以 /api 开头则会被 admin_route 拦截
          filters:
            - RewritePath=/api/(?<segment>.*),/renren-fast/$\{segment} # 将前端请求的 /api/.. 转换成 /renren-fast/..
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
```

因为登录的功能在 renren-fast 服务中，所以先写这个，让前端发送的登录请求能够被网关路由到 8080 端口，而前端发送的 url 中是带有 /api/... 的，因为就是靠这个标识一下，
所以需要重写路径，因为在 renren-fast 的配置文件中添加了：

```yaml
server:
  servlet:
    context-path: /renren-fast
```

也就是说这个服务中的所有路径都要以 /renren-fast 开头才能正确匹配路径，例如 /renren-fast/captcha.jpg 。路径配置成功后，要想让网关成功负载均衡到对应的服务，
就需要在 Nacos 中注册 renren-fast，也就是添加服务名和注册发现路径：

```yaml
spring:
  application:
    name: renren-fast
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
```

需要注意的是：因为网关服务是完全基于响应式编程的，所以它不能使用某些 Spring MVC 的依赖或者配置，例如：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

如果使用了可能导致服务启动失败或者无法使用网关中配置的那些路由，例如：在接收到前端发送的请求后，正准备对路由进行转发，
结果使用的是 SpringMVC 的 DispatcherServlet（Initializing Spring DispatcherServlet 'dispatcherServlet'）。另外，因为用到了负载均衡，所以需要配置相关依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

****
### 2.2 CORS

前端的请求可以成功被网关识别并路由后，证明配置成功：

```text
Mapping [Exchange: GET http://localhost:88/api/sys/config/list?t=1754651883848&page=1&limit=10&paramKey=] to Route{id='admin_route', uri=lb://renren-fast, order=0, predicate=Paths: [/api/**], match trailing slash: true, gatewayFilters=[[[RewritePath /api/(?<segment>.*) = '/renren-fast/${segment}'], order = 1]], metadata={}}
```

此时刷新页面重新登录，发现登录不了：

```text
:8001/#/login:1 Access to XMLHttpRequest at 'http://localhost:88/api/sys/login' from origin 'http://localhost:8001' has been blocked by CORS policy: Response to preflight request doesn't pass access control check: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

这个报错提示：浏览器拦截了跨域请求，因为服务器没有正确返回 CORS（跨域资源共享）相关的响应头。关于 CORS：

> 浏览器出于安全考虑，默认阻止来自 不同源（协议、域名、端口不同） 的网页对服务器资源的访问，这就是“同源策略”。

例如：

- 前端项目运行在 http://localhost:8001
- 网关（后端服务）运行在 http://localhost:88

它们端口不同，因此被视为不同源，浏览器会默认禁止这种跨域请求。但是在过去的项目中，因为使用了 nginx 所以没有出现这种问题，nginx 作为一种反向代理服务器，可以通过配置统一处理跨域请求，
让浏览器认为请求是同源的：

```nginx
server {
    listen 80;
    server_name example.com;  # 前端页面的域名
    # 前端静态资源
    location / {
        root /path/to/frontend;  # 前端文件目录
    }
    # 反向代理API请求
    location /api/ {
        proxy_pass http://api-server:8080/;  # 转发到真实后端
    }
}
```

而当前的项目并没有使用 nginx，所以必须手动开启全局跨域配置，也就是编写一个配置文件，允许哪些来源、哪些请求头、哪些请求方法可以被放行，在 gulimall_gateway 中编写：

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 配置跨域
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("http://localhost:8001"); // 允许前端发送的请求
        corsConfiguration.setAllowCredentials(true); // 允许携带 cookie 跨域
        source.registerCorsConfiguration("/**", corsConfiguration); // 放行所有路径
        return new CorsWebFilter(source);
    }
}
```

需要注意的是：并不是所有的非同源请求都会被拦截，跨域请求如果使用了：

- GET、HEAD 或 POST 
- POST 的 Content-Type 是： 
- application/x-www-form-urlencoded 
- multipart/form-data text/plain 
- 没有自定义请求头（如 Authorization、Token）

这种情况下，浏览器不会先发 OPTIONS 请求，而是直接发目标请求。其余的请求则会先发一个 OPTIONS 请求（称为预检请求 preflight），然后由服务端返回是否允许跨域，
若允许，浏览器才会真正发起实际请求（比如 POST）。所以在 gulimall_gateway 中配置 CorsConfig 就是用来处理 OPTIONS 请求的。

```text
Request URL http://localhost:88/api/sys/login
Request Method OPTIONS
Status Code 200 OK
Remote Address [::1]:88
Referrer Policy strict-origin-when-cross-origin
```

因为所有的请求都会先经过网关，所以优先在网关配置全局 CORS 处理，不过在原有的 renren-fast 服务中已经有了，所以两个处理会产生冲突：

```text
:8001/#/login:1 Access to XMLHttpRequest at 'http://localhost:88/api/sys/login' from origin 'http://localhost:8001' has been blocked by CORS policy: The 'Access-Control-Allow-Origin' header contains multiple values 'http://localhost:8001, http://localhost:8001', but only one is allowed.
```

此时注释掉 renrne-fast 中的全局 CORS 处理即可。

****
### 2.3 树形展示三级分类数据

因为已经写好了获取分类数据的方法，所以只需要配置一下网关路由，让前端发送的查询请求可以被路由到对应的服务的控制层即可：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product_route
          uri: lb://gulimall-product
          predicates:
            - Path=/api/product/**
          filters:
            - RewritePath=/api/(?<segment>.*),/$\{segment}
```

需要注意的是：因为 admin_route 监听的路径是 /api/** ，所以上面写的这个路径也会被转发到 renren-fast，因此在配置这种更精细的路径时就需要让它先被监听到，
也就是写在 admin_route 的上面。因为多个 routes 是按配置文件中出现的顺序依次匹配的，一旦有路由匹配成功，就不会继续向下匹配。

****
## 3. 删除第三级分类

### 3.1 逻辑删除

1、配置全局的逻辑删除规则，默认是自带的可以不写：

```yaml
mybatis-plus:
  global-config:
    db-config:
      id-type: auto
      logic-delete-value: 1
      logic-not-delete-value: 0
```

当使用逻辑删除时，数据库并不会真的 DELETE 掉这条数据，而是通过一个字段标记该数据是否有效。例如：

```sql
-- 执行查询操作时会自动在 SQL 中加上条件 is_deleted = 0，只查未被删除的数据
SELECT * FROM user WHERE is_deleted = 0;
```

当调用 MyBatis-Plus 的删除方法时，例如：

```sql
userService.removeById(1);
```

它内部就会生成如下 SQL（假设逻辑删除字段为 is_deleted）：

```sql
UPDATE user SET is_deleted = 1 WHERE id = 1;
```

2、在表示逻辑删除的字段上添加 @TableLogic

因为默认的是用 1 表示删除，0 表示不删除，这与数据表中设置的相反，所以手动设置 1 为不删除，0 为删除：

```java
/**
* 是否显示[0-不显示，1显示]
*/
@TableLogic(value = "1", delval = "0")
private Integer showStatus;
```

Controller 层：

目前只实现简单的删除功能，后续添加判断是否当前菜单被引用

```java
/**
 * 删除
 * @RequestBody:获取请求体，所以必须发送 POST 请求
 */
@RequestMapping("/delete")
public R delete(@RequestBody Long[] catIds){
    // 1. 检查当前删除的菜单是否被别的地方引用
    categoryService.removeMenuByIds(Arrays.asList(catIds));
    return R.ok();
}
```

Service 层：

```java
@Override
public void removeMenuByIds(List<Long> list) {
    // TODO 1. 检擦当前删除的菜单是否被别的地方引用
    categoryDao.delete(new LambdaQueryWrapper<CategoryEntity>().in(CategoryEntity::getCatId, list));
}
```

****
## 4. 新增和修改三级分类

### 4.1 新增

新增操作就是点击前端的一个 Append 按钮后，就应该弹出一个 dialog 对话框，然后在对话框中输入需要新增的数据的信息：

```vue
<el-button v-if="node.level <= 2" type="text" size="mini" @click="() => append(data)">Append</el-button>
```

```vue
<el-dialog
    :title="title"
    :visible.sync="dialogVisible"
    width="30%"
    :close-on-click-modal="false">
  <el-form :model="category">
    <el-form-item label="分类名称">
      <el-input v-model="category.name" auto-complete="off"></el-input>
    </el-form-item>
    <el-form-item label="图标">
      <el-input v-model="category.icon" auto-complete="off"></el-input>
    </el-form-item>
    <el-form-item label="计量单位">
      <el-input v-model="category.productUnit" auto-complete="off"></el-input>
    </el-form-item>
  </el-form>
  <span slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="submitType">确 定</el-button>
    </span>
</el-dialog>
```

需要注意的是点击 Apend 触发的方法是 append(data) 方法，它实际上是用来重置表单数据（防止下次点击 Append 还是显示上一次添加时提交的数据）和设置分类的等级：

```vue
// 添加某个分类
append (data) {
  console.log('append', data)
  this.dialogVisible = true
  this.dialogType = 'add'
  this.title = '添加分类'
  this.category.parentCid = data.catId
  this.category.catLevel = data.catLevel * 1 + 1 // 防止 catLevel 为字符串，转换为 int 再加一
  // 重置对话框表单的数据
  this.category.name = ''
  this.category.catId = null
  this.category.icon = ''
  this.category.productUnit = ''
  this.category.sort = 0
  this.category.showStatus = 1
},
```

真实的发送添加请求是在对话框中点击确定时触发的方法，而修改操作也是使用的这个对话框，所以需要在这个触发的地方进行区分，到底使用的是添加还是修改：

```vue
// 判断当前打开对话框的是哪个方法
submitType () {
  if (this.dialogType === 'add') {
    this.addCategory()
  } else if (this.dialogType === 'edit') {
    this.editCategory()
  }
},
```

关于 addCategory() 方法，就是发送新增请求的地方，因为在对话框中输入的数据已经绑定到 category 中了，所以直接把 category 作为请求参数传递给后端即可：

```vue
// 添加三级分类
addCategory () {
  console.log('提交的三级分类数据', this.category)
  this.$http({
    url: this.$http.adornUrl('/product/category/save'),
    method: 'post',
    data: this.$http.adornData(this.category, false)
  }).then(({data}) => {
    this.$message({
      message: '菜单保存成功',
      type: 'success'
    })
    this.dialogVisible = false
    // 添加成功后刷新出新的菜单
    this.getMenus()
    // 设置需要默认展开的菜单
    this.expandedKey = [this.category.parentCid]
  })
},
```

后端的新增操作则是由逆向工程生成的，也就是使用普通的 mybatis-plus 方法：

```java
@RequestMapping("/save")
public R save(@RequestBody CategoryEntity category){
    categoryService.save(category);
    return R.ok();
}
```

****
### 4.2 修改操作

修改操作同理，也是点击 Edit 按钮后触发 edit (data)，此方法不是真实发送修改请求的，而是获取当前数据库中最新的数据，根据当前分类的 catId 查询数据库，然后把查到的数据回显到对话框中：

```vue
<el-button type="text" size="mini" @click="() => edit(data)">Edit</el-button>
```

```vue
// 修改分类
edit (data) {
  console.log('要修改的数据', data)
  // 打开对话框
  this.dialogVisible = true
  this.dialogType = 'edit'
  this.title = '修改分类'
  // 发送请求，获取当前分类的最新信息
  this.$http({
    url: this.$http.adornUrl(`/product/category/info/${data.catId}`),
    method: 'get'
  }).then(({data}) => {
    console.log('成功获取到要回显的数据：', data.data)
    // this.category.name = data.data.name
    // this.category.catId = data.data.catId
    // this.category.icon = data.data.icon
    // this.category.productUnit = data.data.productUnit
    // this.category.parentCid = data.data.parentCid
    this.category = {
      ...this.category, // 先用默认值，保证完整
      ...data.data // 再覆盖接口返回的最新数据
    }
  })
},
```

对对话框中的数据进行修改后，就会把它们绑定在 category 中，因为当前的新增与修改操作不涉及 sort、catLevel 等字段，所以这些字段不进行赋值操作，也就是传一个空值，
这样后端修改时便不会修改这些字段：

```vue
// 具体修改三级分类操作
editCategory () {
  const {catId, name, icon, productUnit} = this.category
  const data = {catId: catId, name: name, icon: icon, productUnit: productUnit}
  this.$http({
    url: this.$http.adornUrl('/product/category/update'),
    method: 'post',
    data: this.$http.adornData(data, false)
  }).then(({data}) => {
    this.$message({
      message: '菜单修改成功',
      type: 'success'
    })
    this.dialogVisible = false
    // 删除成功后刷新出新的菜单
    this.getMenus()
    // 设置需要默认展开的菜单
    this.expandedKey = [this.category.parentCid]
  })
}
```

****
## 5. 拖动效果 

### 5.1 层数判断

在编写拖动方法前，可以看一下这三个数据分别代表什么：

```vue
// 监听拖放逻辑
handleNodeDrop (draggingNode, dropNode, dropType, ev) {
  console.log('拖动节点', draggingNode)
  console.log('目标节点', dropNode)
  console.log('拖放类型', dropType)
  // 这里写后端更新分类父子关系逻辑，比如调用接口更新 parentCid 等
  // 更新完后刷新菜单树
  this.getMenus()
},
```

拖动节点 allowDrop：

如果是第三级分类，则没有孩子：

```text
data: Object
  catId: 1440
  catLevel: 3
  children: Array(0)
```

如果是一二级分类，则有孩子：

```text
data: Object
  catId: 22
  catLevel: 2
  childre: Array(4)
    0:
      catId: 165
      catLevel: 3
      children: Array(0)
```

目标节点 dropNode：

```text
data: Object
  catId: 1441
  catLevel: 3
  children: Array(0) 
  icon: "yyy"
  name: "中中"
  parentCid: 1433
  productCount: null
  productUnit: "秒"
  showStatus: 1
  sort: 0
```

拖放类型 type：

```text
next
```

然后编写拖动方法，主要就是判断当前拖动的节点总共有几层，拖进的那个分类有几层，拖进去后是否超过设置的 3 层：

```vue
// 允许拖动的方法，用来判断哪些层级可以拖进去
allowDrop (draggingNode, dropNode, type) {
  // 被拖动的当前节点以及所在的父节点总层数不能大于 3
  // 1. 判断被拖动的当前节点总层数
  // 2. 统计当前节点总层数
  this.maxLevel = draggingNode.data.catLevel  // 初始化成当前节点的层级
  this.countNodeLevel(draggingNode.data)
  // 当前正在拖动的节点 + 父节点所在深度不大于 3 即可
  const deep = this.maxLevel - draggingNode.data.catLevel + 1
  console.log('当前正在拖动节点的深度：', deep)
  if (type === 'inner') {
    return (deep + dropNode.level) <= 3
  } else {
    return (deep + dropNode.parent.level) <= 3
  }
},
```

这个方法就是判断当前节点的层数，其实就是看它有几层孩子，根据上面返回的拖动节点，就是判断这个拖动节点有没有 children，如果有就计算它的 children 的 catLevel（结构中有这个，可以直接获取），
然后递归判断这个拖动节点的孩子是否还有孩子，然后看它的 catLevel，如果有就更新 maxLevel：

```vue
// 统计当前节点层数
countNodeLevel (node) {
    // 找到所有子节点，求出最大深度
    if (node.children != null && node.children.length > 0) {
        for (let i = 0; i < node.children.length; i++) {
            if (node.children[i].catLevel > this.maxLevel) {
                this.maxLevel = node.children[i].catLevel
            }
        this.countNodeLevel(node.children[i])
        }
    }
},
```

找到拖动节点的嘴小分类层级的 catLevel 后（此时已更新为 maxLevel），用它减去当前拖动节点的 catLevel 然后再加一，例如：

- 此时节点为分类一级，它有二级、三级孩子，则此时的 maxLevel 为 3
- 而这个节点的 catLevel 为 1
- maxLevel - catLevel + 1 = 3 - 1 + 1 = 3
- 代表当前拖动节点的层数为 3

- 若此时节点为分类二级，即只有三级孩子，但此时的 maxLevel 也为 3
- 二这个节点的 catLevel 为 2
- maxLevel - catLevel + 1 = 2 - 1 + 1 = 2
- 代表当前拖动的节点层数为 2

判断完层数后就可以根据当前拖动的节点要拖进哪个地方进行判断：

```vue
// 如果是拖入某个分类层级，那就要判断当前节点和要拖入的那个节点的层数加起来是否小于 3
if (type === 'inner') {
    return (deep + dropNode.level) <= 3
} else {
    // 如果是拖到某个分类层级的旁边，即拖进它所属的层级，就需要判断当前节点加上拖入的层级的层数是否小于 3 
    return (deep + dropNode.parent.level) <= 3
}
```

****
### 5.2 顺序和层级的修改

在拖动节点时，除了需要判断可以拖动到哪，还需要对该节点的顺序和层级的更行，例如将原本是第二层级的拖动到最外层，也就是层级从 2 变成了 1；而排序顺序也要根据拖动到的地方进行修改，
例如原本手机排第二的，现在把电脑拖动到了手机前面，那么手机的排序就要修改成第三，电脑修改成第二，其他的类推。而此时就需要思考拖动的类型是哪种：

1. 拖动到一个节点内，拖动类型为 inner
2. 如果是拖动到某个节点的前后，拖动类型为 before 和 after

```vue
// 监听拖放逻辑
handleNodeDrop (draggingNode, dropNode, dropType, ev) {
  console.log('拖动节点', draggingNode)
  console.log('目标节点', dropNode)
  console.log('拖放类型', dropType)
  // 这里写后端更新分类父子关系逻辑，比如调用接口更新 parentCid 等
  // 1. 获取当前节点最新的父节点 id
  let pCid = 0
  let siblings = null
  if (dropType === 'before' || dropType === 'after') {
    pCid = dropNode.parent.data.catId === undefined ? 0 : dropNode.parent.data.catId
    siblings = dropNode.parent.childNodes
  } else {
    pCid = dropNode.data.catId
    siblings = dropNode.childNodes
  }
  // 2. 获取当前节点的最新顺序和最新层级
  for (let i = 0; i < siblings.length; i++) {
    // 如果遍历的是当前正在拖拽的节点
    if (siblings[i].data.catId === draggingNode.data.catId) {
      let catLevel = draggingNode.level
      if (siblings[i].level !== draggingNode.level) {
        catLevel = siblings[i].level
        // 因为当前拖动的节点层级发生了改变，如果它有子节点，那字节点的层级也要改变
        this.updateChildNodeLevel(siblings[i])
      }
      this.updateNodes.push({catId: siblings[i].data.catId, sort: i, parentCid: pCid, catLevel: catLevel})
    } else {
      this.updateNodes.push({catId: siblings[i].data.catId, sort: i})
    }
  }
  console.log('updateNodes:', this.updateNodes)
  // 更新完后刷新菜单树
  // this.getMenus()
},

// 修改子节点层级
updateChildNodeLevel (node) {
  if (node.childNodes.length > 0) {
    for (let i = 0; i < node.childNodes.length; i++) {
      const currentNode = node.childNodes[i].data
      this.updateNodes.push({catId: currentNode.catId, catLevel: node.childNodes[i].level})
      this.updateChildNodeLevel(node.childNodes[i])
    }
  }
},
```

首先要根据操作类型来获取当前拖动的节点放入的地方的父节点是谁，如果是拖动到某个节点前后，那此时的目标节点 dropNode 就是拖动节点的兄弟节点，因为它们是同一级的，
此时就通过兄弟节点来获取它们此时共同的父节点是谁，所以调用 dropNode.parent.data.catId 来获取，当然也可以直接用 dropNode.data.parentCid 获取。

当拖动类型是直接拖进某个节点内部，称为它的子节点，那就可以直接通过 dropNode.data.catId 获取。不管哪种拖动类型，都需要将当前拖动后的节点所在的层级的所有节点放进 siblings 中，
即当前节点的所有兄弟节点，方便后续对这个数据进行排序和层级的修改：

```vue
if (dropType === 'before' || dropType === 'after') {
    // 当将当前节点拖动到第一层级后，兄弟节点的 parent 就变成数组类型了，所以不能直接通过 .data.catId 获取，所以会变成 undefined 类型，所以要进行判断
    pCid = dropNode.parent.data.catId === undefined ? 0 : dropNode.parent.data.catId
    siblings = dropNode.parent.childNodes
  } else {
    pCid = dropNode.data.catId
    siblings = dropNode.childNodes
  }
```

然后就是修改顺序和层级。因为上面已经把所有的兄弟节点放到 siblings 中了，所以直接遍历它，当遍历到当前拖动的节点时，就需要从它开始修改排序（实际上从第一个节点就开始修改了，
只不过修改的值和原值一样），总之不管怎么样，最终都是在拖入后按照顺序遍历的。而关于修改层级，因为其它兄弟节点不涉及拖动操作，所以不用考虑层级的修改，只需要修改拖动的那个节点。
而拖动的那个节点可能自带孩子，所以也需要修改它的孩子节点的层级。并且，由于拖动了节点，可能存在父节点改变的情况，所以也要把最新获取到的父节点 ID，即 pCid 传递给后端一起更新：

```vue
// 2. 获取当前节点的最新顺序和最新层级
for (let i = 0; i < siblings.length; i++) {
    // 如果遍历的是当前正在拖拽的节点
    if (siblings[i].data.catId === draggingNode.data.catId) {
      let catLevel = draggingNode.level // 获取拖拽前的层级
      if (siblings[i].level !== draggingNode.level) { // 如果当前节点的层级与拖拽时的层级不一致
        catLevel = siblings[i].level // 将拖拽前的层级更新为拖拽后的
        // 因为当前拖动的节点层级发生了改变，如果它有子节点，那字节点的层级也要改变
        this.updateChildNodeLevel(siblings[i])
      }
      // 将需要更新的新数据放进 updateNodes 中，后续将这个作为请求参数传递给后端，进行修改
      // 因为是拖入数据，所以拖动节点的父节点也会改变，这里也需要更新
      this.updateNodes.push({catId: siblings[i].data.catId, sort: i, parentCid: pCid, catLevel: catLevel})
    } else {
      this.updateNodes.push({catId: siblings[i].data.catId, sort: i})
    }
}
```

创建一个更新节点的方法，当当前拖动的节点的层级和目标节点的层级不一致时，也就是 siblings[i].level !== draggingNode.level，就将新的层级赋值给拖拽节点的层级，
然后处理拖拽节点的孩子的层级，这里也是用到了递归处理，直到孩子的孩子...处理完毕

```vue
updateChildNodeLevel (node) {
  // 如果有孩子
  if (node.childNodes.length > 0) {
    // 遍历孩子节点
    for (let i = 0; i < node.childNodes.length; i++) {
      const currentNode = node.childNodes[i].data
      this.updateNodes.push({catId: currentNode.catId, catLevel: node.childNodes[i].level})
      this.updateChildNodeLevel(node.childNodes[i])
    }
  }
},
```

前端基本拖拽逻辑完成后，就可以天机发送修改请求的后端逻辑了，在监听拖放逻辑的代码中添加发送请求，并且在拖拽前进行清空 updateNodes 的操作，避免一次发送过多数据，
在 allowDrop (draggingNode, dropNode, type) 方法中添加 this.updateNodes = []。

```vue
// 监听拖放逻辑
handleNodeDrop (draggingNode, dropNode, dropType, ev) {
  ...
  console.log('updateNodes:', this.updateNodes)
  // 发送修改请求
  this.$http({
    url: this.$http.adornUrl('/product/category/update/sort'),
    method: 'post',
    data: this.$http.adornData(this.updateNodes, false)
  }).then(({data}) => {
    this.$message({
      message: '拖拽成功',
      type: 'success'
    })
    // 添加成功后刷新出新的菜单
    this.getMenus()
    // 设置需要默认展开的菜单
    this.expandedKey = [pCid]
  })
},
```

Controller 层：

这里需要注意的是，前端的请求参数的接收是用一个 CategoryEntity 类型的 List 接收的，因为前端在将数据添加进数组 updateNodes 时是这样的：

```vue
this.updateNodes.push({catId: siblings[i].data.catId, sort: i, parentCid: pCid, catLevel: catLevel})
```

这种数组对象在前端会被整合成 Json：

```json
[
  { catId: 100, sort: 0, parentCid: 0, catLevel: 1 },
  { catId: 101, sort: 1, parentCid: 0, catLevel: 1 },
  ...
]
```

所以这种类型在后端用使用集合类型接收是比较好的，不过不建议用数组类型，因为 Spring Boot 会自动把 JSON 数组转换成 List<实体类>，如果使用数组接收还得进行一次类型转换。

```java
/**
 * 批量修改
 */
@RequestMapping("/update/sort")
public R updateSort(@RequestBody List<CategoryEntity> categories){
    categoryService.updateBatchById(categories);
    return R.ok();
}
```

****
# 四、品牌管理

## 1. 品牌信息添加显示状态按钮

在逆向生成的 brand.vue 代码处，在显示状态的地方添加一个开关，用来代替原本显示状态的 0 和 1，它也用来直接修改该条数据库记录的状态，即修改 0 和 1，所以绑定的方法需要发送一条请求，
让后端对该条数据进行修改：

```vue
<el-table-column
    prop="showStatus"
    header-align="center"
    align="center"
    label="显示状态">
  <template slot-scope="scope">
    <el-switch
        v-model="scope.row.showStatus"
        active-color="#13ce66"
        :active-value="1"
        inactive-color="#ff4949"
        :inactive-value="0"
        @change="updateBrandStatus(scope.row)"
    >
    </el-switch>
  </template>
</el-table-column>
```

而按钮绑定的数据是 scope，所以它可以获取整行的数据，也就是当前开关所属的那个品牌的所有数据（即整个 el-table），所以可以获取到 brandId 和 showStatus（显示状态绑定的值定义为 showStatus），
然后让关绑定 0，开绑定 1 即可。

```vue
// 更新品牌状态
updateBrandStatus (data) {
    console.log('品牌最新信息：', data)
    let {brandId, showStatus} = data
    // 发送请求修改状态
    this.$http({
      url: this.$http.adornUrl('/product/brand/update'),
      method: 'post',
      data: this.$http.adornData({brandId, showStatus}, false)
    }).then(({data}) => {
      this.$message({
        type: 'success',
        message: '状态更新成功'
      })
    })
},
```

而后端也是利用逆向工程生成的代码使用 mybatis-plus 修改数据库数据：

```java
/**
 * 修改
 */
@RequestMapping("/update")
public R update(@RequestBody BrandEntity brand){
    brandService.updateById(brand);
    return R.ok();
}
```

****
## 2. 文件存储

### 2.1 OSS 的使用

> 关于阿里云的详细使用可以参考 Takeout-Project 笔记，那里如何使用原生账号上传文件。这里记录一下如何使用子账号上传，用 RAM 子账号可以按最小权限授权、定期轮换 AccessKey、
必要时用 STS 发放临时凭证，可以避免把主账号（root）AccessKey 放到开发工具里，降低风险。

在阿里云控制台：

1. 登录阿里云控制台 -> 访问控制 (RAM) -> 用户 -> 创建用户（选择 Programmatic access / 允许编程访问）
2. 在用户详情页 -> 认证管理 -> 创建 AccessKey，把 AccessKey ID / AccessKey Secret 保存好（只在创建时能看到一次）
3. 给子账号授权，一般授予操控某个 OSS 即可

使用：

1、引入依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alicloud-oss</artifactId>
    <version>2.2.0.RELEASE</version>
</dependency>
```

2、在 application 文件中添加相关配置

```yaml
spring:
  cloud:
    alicloud:
      access-key: ...
      secret-key: ...
      oss:
        endpoint: oss-cn-beijing.aliyuncs.com
    util:
      enabled: false
```

3、注入 OSSClient 并使用

```java
@Autowired
private OSSClient ossClient;

@Test
void testUpload() throws FileNotFoundException {
    InputStream inputStream = new FileInputStream("F:\\pictures\\100.jpg");
    ossClient.putObject("cell-gmall", "100.jpg", inputStream);
    ossClient.shutdown();
    System.out.println("上传成功！");
}
```

然而在实际的使用中却频繁出现错误，首先是一个报错，虽然不影响最终的程序执行，但可能后续会对 JSON 相关的操作造成影响：

```text
Found multiple occurrences of org.json.JSONObject on the class path:

	jar:file:/E:/maven/repository/org/json/json/20170516/json-20170516.jar!/org/json/JSONObject.class
	jar:file:/E:/maven/repository/com/vaadin/external/google/android-json/0.0.20131108.vaadin1/android-json-0.0.20131108.vaadin1.jar!/org/json/JSONObject.class

You may wish to exclude one of them to ensure predictable runtime behavior
```

这个报错的字面意思就是有 2 个地方的 json 依赖发生冲突，需要排除一个，这通常是 Spring 框架和 Vaadin 框架的版本问题造成的，所以一般只保留自己添加的哪个 json 依赖，
所以选择排除 android-json 依赖，但直接在 pom 文件中搜是搜不到的，它一般是包含在某些依赖中的，而这个就是由 spring-boot-starter-test 引入的，所以在这个依赖中排除即可：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>com.vaadin.external.google</groupId>
            <artifactId>android-json</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

还有一个报错就是，明明 pom 文件中引入了 spring cloud 和 alibaba cloud 的依赖管理，但是却无法引入：

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alicloud-oss</artifactId>
</dependency>
```

```xml
Unresolved dependency: 'com.alibaba.cloud:spring-cloud-starter-alicloud-oss:jar:unknown'
```

这应该也是依赖版本的冲突，只能让这个依赖带上版本号后纳入依赖管理，再引入：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2021.0.5</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2021.0.5.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alicloud-oss</artifactId>
            <version>2.2.0.RELEASE</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

虽然这可以解决依赖引入的问题，但是又出现问题：

```text
***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 0 of method inetIPv6Utils in com.alibaba.cloud.nacos.util.UtilIPv6AutoConfiguration required a single bean, but 2 were found:
	- spring.cloud.inetutils-org.springframework.cloud.commons.util.InetUtilsProperties: defined in null
	- inetUtilsProperties: defined by method 'inetUtilsProperties' in class path resource [org/springframework/cloud/commons/util/UtilAutoConfiguration.class]
Action:

Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, or using @Qualifier to identify the bean that should be consumed
```

经过搜索，发现在配置文件中添加：

```yaml
    util:
      enabled: false
```

可以解决 product 服务启动的问题，也可以通过 test 成功上传图片到 OSS，但是此时除了　product 以外的所有服务全部启动报错，报错内容就是上面的，这个报错的大概意思就是：
Spring 容器里同时存在了两个 InetUtilsProperties 类型的 Bean，一个是 spring.cloud.inetutils-org.springframework.cloud.commons.util.InetUtilsProperties，
另一个是 inetUtilsProperties（由 UtilAutoConfiguration 中的方法创建），但是 UtilIPv6AutoConfiguration 需要注入唯一的 InetUtilsProperties，所以就报错了。
而在 gulimall_product 的 application 中添加了 spring.cloud.util.enabled=false，它就可以关闭 spring-cloud-commons 中的 Util 相关自动配置（UtilAutoConfiguration），
也就不会创建 inetUtilsProperties 这个 Bean，所以可以选择在其它服务的 application 中都添加 spring.cloud.util.enabled=false。

虽然直接使用 spring.cloud.util.enabled=false 可以解决上面的报错，但是启动后又出现了新的报错：

```text
Caused by: java.lang.ClassNotFoundException: com.aliyun.oss.ClientBuilderConfiguration
```

又或者：

```text
java.lang.IllegalArgumentException: Oss endpoint can't be empty.
```

一个是因为缺失依赖而导致的项目启动失败，一个是因为配置缺失导致无法创建 OSS Client，但是其它服务明明没有用到相关的东西，只是引入了 common 依赖，所以应该是添加进 common 的依赖和其它服务产生了版本的冲突，
所以直接把添加进 common 的 oss 相关依赖放进 gulimall_product 中，避免和其它项目产生冲突。

所以最终关于依赖的导入就是直接导入 gulimall_product 服务：

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alicloud-oss</artifactId>
    <version>2.2.0.RELEASE</version>
</dependency>
```

****
### 2.2 获取服务端签名

上面的使用方法是通过微服务将文件上传，这样就太麻烦了，每次上传文件都要经过一次服务器，应该直接让浏览器将文件提交给 OSS 存储，只通过服务器拿到签名数据以保证安全和可靠性。
所以这里新建一个服务，用来集成第三方的 OSS 服务。

编写一个 controller 用来接收前端发送来的请求，然后将相关的签名返回给前端，前端带着签名等信息直接把文件 POST 到 OSS 服务器：

```java
@RestController
public class OssController {
    @Autowired
    OSS ossClient;
    @Value("${spring.cloud.alicloud.oss.endpoint}")
    String endpoint;
    @Value("${spring.cloud.alicloud.oss.bucket}")
    String bucket;
    @Value("${spring.cloud.alicloud.access-key}")
    String accessId;
    @Value("${spring.cloud.alicloud.secret-key}")
    String accessKey;

    @GetMapping("/oss/policy")
    public Map<String, String> policy() {
        // host的格式为 bucketname.endpoint
        String host = "https://" + bucket + "." + endpoint;
        // 上传目录（可按日期分目录）
        String dir = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
        Map<String, String> respMap = null;
        try {
            long expireTime = 30; // 签名有效期（秒）
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // 允许的最大文件大小，这里 10 MB
            long maxSize = 10 * 1024 * 1024;
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxSize);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            respMap = new LinkedHashMap<>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return respMap;
    }
}
```

因为是一个新的服务，所以网关也要进行相关的配置：

```yaml
spring:
  cloud:
    gateway:
      routes:
        
        - id: third_party_route
          uri: lb://gulimall-third-party
          predicates:
            - Path=/api/thirdparty/**
          filters:
            - RewritePath=/api/thirdparty/(?<segment>.*),/$\{segment}
```

访问 http://localhost:88/api/thirdparty/oss/policy ：

```json
{
    "accessid": "LTAI5tPE3AMHW1ewpzPH3emY",
    "policy": "eyJleHBpcmF0aW9uIjoiMjAyNS0wOC0xMVQxMjozMzo1OS44NjdaIiwiY29uZGl0aW9ucyI6W1siY29udGVudC1sZW5ndGgtcmFuZ2UiLDAsMTA0ODU3NjBdLFsic3RhcnRzLXdpdGgiLCIka2V5IiwiMjAyNS0wOC0xMS8iXV19",
    "signature": "Mm7t5Csymg5z5d+t8k3m7V0yhko=",
    "dir": "2025-08-11/",
    "host": "https://cell-gmall.oss-cn-beijing.aliyuncs.com",
    "expire": "1754915639"
}
```

****
### 2.3 前端使用签名

前端会发送一个请求，向后端获取签名，然后把获取到的数据赋值给 dataObj，将来把这个 dataObj 上传给 OSS：

```vue
<el-upload
    action="http://cell-gmall.oss-cn-beijing.aliyuncs.com" <!--一定要注意格式:bucket.endpoint-->
    :data="dataObj"
    list-type="picture"
    :multiple="false" :show-file-list="showFileList"
    :file-list="fileList"
    :before-upload="beforeUpload"
    :on-remove="handleRemove"
    :on-success="handleUploadSuccess"
    :on-preview="handlePreview">
  <el-button size="small" type="primary">点击上传</el-button>
  <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过10MB</div>
</el-upload>
```

```js
export function policy () {
  return new Promise((resolve, reject) => {
    http({
      url: http.adornUrl('/thirdparty/oss/policy'),
      method: 'get',
      params: http.adornParams({})
    }).then(({data}) => {
      resolve(data)
    })
  })
}
```

```vue
beforeUpload(file) {
  return new Promise((resolve, reject) => {
    policy().then(response => {
      this.dataObj.policy = response.data.policy;
      this.dataObj.signature = response.data.signature;
      this.dataObj.ossaccessKeyId = response.data.accessid;
      this.dataObj.key = response.data.dir + getUUID() + '_${filename}';
      this.dataObj.dir = response.data.dir;
      this.dataObj.host = response.data.host;
      resolve(true);
    }).catch(err => {
      reject(false);
    });
  });
}
```

点击上床文件进行测试,却发现浏览器报错了:

```text
Access to XMLHttpRequest at 'http://cell-gmall.oss-cn-shanghai.aliyuncs.com/' from origin 'http://localhost:8001' has been blocked by CORS policy: Response to preflight request doesn't pass access control check: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

这又是一个跨域拦截,既然是从某个浏览器向另一个地方发送请求,那就存在跨域问题,解决办法就是在 OSS 中配置允许跨域:


```text
创建跨域规则

* 来源
┌──────────────────────────────────────────────┐
│ *                                            │
└──────────────────────────────────────────────┘
来源可以设置多个，每行一个，每行最多能有一个通配符 *

* 允许 Methods   [ ] GET   [x] POST   [ ] PUT   [ ] DELETE   [ ] HEAD  

允许 Headers
┌──────────────────────────────────────────────┐
│ *                                            │
└──────────────────────────────────────────────┘
允许 Headers 可以设置多个，每行一个，每行最多能有一个通配符 *

暴露 Headers
┌──────────────────────────────────────────────┐
│                                              │
└──────────────────────────────────────────────┘
暴露 Headers 可以设置多个，每行一个，不允许出现通配符 *

缓存时间（秒）  [ 3600 ]
```

****
## 3. 数据校验

### 3.1 前端表单校验

前端的表单校验就是在用表单填写数据时会对填写的内容进行一次检查，看看是否复合定义的规则，避免提交错误的数据给后端，造成不必要的麻烦。前端表单校验的写法较为统一：

```vue
dataRule: {
      name: [
        { required: true, message: '品牌名不能为空', trigger: 'blur' }
      ],
      logo: [
        { required: true, message: '品牌logo地址不能为空', trigger: 'blur' }
      ],
      descript: [
        { required: true, message: '介绍不能为空', trigger: 'blur' }
      ],
      showStatus: [
        { required: true, message: '显示状态[0-不显示；1-显示]不能为空', trigger: 'blur' }
      ],
      firstLetter: [
        { validator: (rule, value, callback) => {
          if (value === '') {
            callback(new Error('首字母必须填写'))
          } else if (!/^[a-zA-Z]$/.test(value)) {
            callback(new Error('首字母必须是26个字母之中的一个'))
          } else {
            // 成功则不传错误信息
            callback()
          }
          }, trigger: 'blur' }
      ],
      sort: [
        { validator: (rule, value, callback) => {
            if (value === '') {
              callback(new Error('排序字段必须填写'))
            } else if (!Number.isInteger(value) || value < 0) {
              callback(new Error('排序字段必须是不小于0的整数'))
            } else {
              // 成功则不传错误信息
              callback()
            }
          }, trigger: 'blur' }
      ]
    }
  }
},
```

```vue
this.$refs['dataForm'].validate((valid) => {
  if (valid) {
    // 校验通过，执行提交逻辑
  } else {
    // 校验失败，阻止提交
  }
})
```

****
### 3.2 JSR303 校验

虽然在前端提交表单时进行过一次校验了，但是那只局限于使用表单，如果是通过 postman 等软件发送请求，则可以直接跳过前端的校验，所以需要在后端也添加一层校验作为保障。
而常用的一种校验方式就是 JSR 303 校验，它是 Java 规范请求 303 号，它规范了一个通用的、标准化的 Bean 校验 API。它定义了一组注解和接口，用来声明和执行对 Java 对象属性的校验规则。
常用注解：

| 注解                | 说明                       | 备注               |
| ----------------- | ------------------------ | ---------------- |
| `@NotNull`        | 不能为空，null 会校验失败          | 只校验 null，不校验空字符串 |
| `@NotBlank`       | 字符串不能为空，且不能全为空格          | 适用于 String       |
| `@NotEmpty`       | 字符串、集合、数组不能为空（不为null且非空） | 适用于集合和字符串        |
| `@Size(min, max)` | 字符串、集合长度范围               |                  |
| `@Min`            | 数值最小值限制                  |                  |
| `@Max`            | 数值最大值限制                  |                  |
| `@Pattern`        | 正则表达式校验                  |                  |
| `@Email`          | 邮箱格式校验                   |                  |
| `@Past`           | 日期必须是过去时间                |                  |
| `@Future`         | 日期必须是将来时间                |                  |

相关依赖：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

```

例如在品牌实体中添加这些注解：

```java
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 品牌id
	 */
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名必须提交")
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotEmpty
	@URL(message = "logo 必须是一个合法的 url 地址")
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty
	@Pattern(regexp = "^[a-zA-Z]$", message = "品牌首字母必须是26个字母之一")
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull
	@Min(value = 0, message = "排序字段不能小于0")
	private Integer sort;
}
```

然后在控制器方法参数上加 @Valid:

```java
@RequestMapping("/save")
public R save(@Valid @RequestBody BrandEntity brand, BindingResult bindingResult){
    if(bindingResult.hasErrors()){
        Map<String, String> map = new HashMap<>();
        // 获取校验的错误结果
        bindingResult.getFieldErrors().forEach(fieldError -> {
            // 获取到错误提示
            String message = fieldError.getDefaultMessage();
            // 获取错误的字段
            String field = fieldError.getField();
            map.put(field, message);
        });
        return R.error(400, "提交的数据不合法").put("data", map);
    } else {
        brandService.save(brand);
        return R.ok();
    }
}
```

- @Valid 
  - 标记需要对 brand 对象进行 JSR 303 校验。这个注解会触发 Spring MVC 自动调用底层 Validator（如 Hibernate Validator）对 brand 进行校验。

- BindingResult bindingResult 
  - 用于接收校验的结果，必须紧跟在 @Valid 参数之后。
  - 里面包含了校验失败时的错误信息列表，如果没有错误则为空

通过 apiFox 发送一个请求：

```json
{"name":"","logo":"sfa","sort":"","firstLetter": "asf"}
```

收到返回结果：

```json
{
  "msg": "提交的数据不合法",
  "code": 400,
  "data": {
    "name": "品牌名必须提交",
    "logo": "logo 必须是一个合法的 url 地址",
    "sort": "不能为null",
    "firstLetter": "品牌首字母必须是26个字母之一"
  }
}
```

****
### 3.3 统一处理异常

因为后续可能处理很多异常类型，如果每个 Controller 里都写 BindingResult 的判断，就会造成代码重复，逻辑分散，违背了 Controller 层的设计，所以需要统一异常处理，
统一处理 BindingResult 的判断。

例如，编写一个 ExceptionControllerAdvice 类，专门处理本服务的异常，因为 Controller 层中使用了 @Valid，可以触发对目标参数的校验，如果出现异常就会在 Controller 层抛出：

```java
@Slf4j
@RestControllerAdvice(basePackages = "com.project.gulimall.product.controller")
public class ExceptionControllerAdvice {
    @ExceptionHandler(value = Exception.class)
    public R handleValidException(Exception e){
        log.error("数据校验出现问题：{}，异常类型为：{}", e.getMessage(), e.getClass());
        return R.error();
    }
}
```

- @RestControllerAdvice 相当于 @ControllerAdvice + @ResponseBody
- @ControllerAdvice：全局异常通知，所有控制器异常都能捕获
- @ExceptionHandler(ExceptionType.class)：指定捕获的异常类型
- 当 Controller 或 Spring 内部抛出异常时，匹配对应异常处理方法执行

先简单捕获一下刚刚写的 save() 方法抛出的异常：

```text
异常类型为：class org.springframework.web.bind.MethodArgumentNotValidException
```

可以看到捕获的异常是 MethodArgumentNotValidException，那么就可以直接精准捕获，然后对触发该类型异常的 BindingResult 进行判断：

```java
@ExceptionHandler(value = MethodArgumentNotValidException.class)
public R handleValidException(MethodArgumentNotValidException e){
    log.error("数据校验出现问题：{}，异常类型为：{}", e.getMessage(), e.getClass());
    BindingResult bindingResult = e.getBindingResult();
    Map<String, String> map = new HashMap<>();
    bindingResult.getFieldErrors().forEach(fieldError -> {
        map.put(fieldError.getField(), fieldError.getDefaultMessage());
    });
    return R.error(400, "数据校验出现问题").put("data", map);
}
```

当然触发的异常类型不止这一种，所以可以编写一个更广范围的异常捕获：

```java
// 无论是程序抛出的异常，还是程序错误，都可以被捕获
@ExceptionHandler(value = Throwable.class)
public R handleException(Throwable throwable){
    log.error("数据校验出现问题：{}，异常类型为：{}", throwable.getMessage(), throwable.getClass());
    return R.error();
}
```

不过目前的错误状态码返回的都是 400，这是不符合规范的，状态码应该准确的显示当前发生的错误是什么，所以应该定义一个枚举类型，用来管理不通状态码对应的错误信息，
将来可能会发生各种错误，到时候添加进去即可：

```java
/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表：
 *  10: 通用
 *      001：参数格式校验
 *  11: 商品
 *  12: 订单
 *  13: 购物车
 *  14: 物流
 */
public enum BizCodeEnum {
    UNKNOW_EXEPTION(10000,"系统未知异常"),

    VALID_EXCEPTION( 10001,"参数格式校验失败");

    private int code;
    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
```

修改上面对应的捕获异常的代码：

```java
@ExceptionHandler(value = MethodArgumentNotValidException.class)
public R handleValidException(MethodArgumentNotValidException e){
    ...
    return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data", map);
}
```

```java
@ExceptionHandler(value = Throwable.class)
public R handleException(Throwable throwable){
    ...   
    return R.error(BizCodeEnum.UNKNOW_EXEPTION.getCode(), BizCodeEnum.UNKNOW_EXEPTION.getMsg());
}
```

注释掉 Controller 层写的 BindingResult，直接正常返回数据，异常会由上面的统一捕获（但 @Valid 不能注释掉）：

```java
@RequestMapping("/save")
public R save(@Valid @RequestBody BrandEntity brand/*, BindingResult bindingResult*/){
    /*if(bindingResult.hasErrors()){
        Map<String, String> map = new HashMap<>();
        // 获取校验的错误结果
        bindingResult.getFieldErrors().forEach(fieldError -> {
            // 获取到错误提示
            String message = fieldError.getDefaultMessage();
            // 获取错误的字段
            String field = fieldError.getField();
            map.put(field, message);
        });
        return R.error(400, "提交的数据不合法").put("data", map);
    } else {
        brandService.save(brand);
        return R.ok();
    }*/

    brandService.save(brand);
    return R.ok();
}
```

模拟错误请求返回结果：

```text
{
    "msg": "参数格式校验失败",
    "code": 10001,
    "data": {
        "name": "品牌名必须提交",
        "logo": "logo 必须是一个合法的 url 地址",
        "sort": "不能为null",
        "firstLetter": "品牌首字母必须是26个字母之一"
    }
}
```

****
### 3.4 分组校验

在实际开发中，同一个实体类可能会在多个场景中使用，但不同场景对字段的校验规则往往不同。例如：

- 新增用户时：id 字段由数据库自动生成，不需要传递，因此不需要校验 id 是否存在；但 username 和 password 必须非空
- 修改用户时：id 是必须的（用于定位要修改的用户），因此需要校验 id 非空；同时 username 和 password 仍需非空
- 重置密码时：id 是必须的（用于定位要修改的用户），但只需要验证新密码即可

如果没有分组校验，就需要为新增、修改、重置分别创建实体类，但是它们使用的字段都是一样的，只是校验规则不同，这就会导致代码冗余。而分组校验允许在同一个实体类中，
为不同场景定义不同的校验规则，避免冗余。

分组的本质是创建一个标记接口（无任何方法的接口），用于标识一组校验规则。例如：AddGroup（新增场景的分组）、UpdateGroup（修改场景的分组）。
而 JSR303 的所有约束注解（如 @NotNull、@Min 等）都有一个 groups 属性，用于指定该注解属于哪个或哪些分组。在触发校验时（如 Controller 层接收参数时），
通过指定分组，只执行该分组下的校验规则。而之前使用的 @Valid 注解就要替换成 @Validated，因为这个新注解可以指定需要执行的分组，即可只校验该分组下的规则。具体使用：

```java
@NotNull(message = "修改必须指定品牌 id", groups = {UpdateGroup.class})
@Null(message = "新增不能指定品牌 id", groups = {AddGroup.class})
@TableId
private Long brandId;
/**
 * 品牌名
 */
@NotBlank(message = "品牌名必须提交", groups = {AddGroup.class, UpdateGroup.class})
private String name;
/**
 * 品牌logo地址
 */
@NotEmpty(groups = {AddGroup.class})
@URL(message = "logo 必须是一个合法的 url 地址", groups = {AddGroup.class, UpdateGroup.class})
private String logo;
/**
 * 介绍
 */
private String descript;
/**
 * 显示状态[0-不显示；1-显示]
 */
private Integer showStatus;
/**
 * 检索首字母
 */
@NotEmpty(groups = {AddGroup.class})
@Pattern(regexp = "^[a-zA-Z]$", message = "品牌首字母必须是26个字母之一", groups = {AddGroup.class, UpdateGroup.class})
private String firstLetter;
/**
 * 排序
 */
@NotNull(groups = {AddGroup.class})
@Min(value = 0, message = "排序字段不能小于0", groups = {AddGroup.class, UpdateGroup.class})
private Integer sort;
```

- 调用 update 方法时，会校验 UpdateGroup 分组的规则：brandId 非空、name 非空、logo 的 URL 必须复合规范（但可以为空）、首字母必须符合规范（但可以为空）、排序字段必须复合规范（但可以为空）
- 调用 save 方法时，会检验 AddGroup 分组的规则：brandId 必须为空、name 非空、logo 的 URL 必须复合规范且非空、首字母必须符合规范且非空、排序字段必须复合规范且非空

```java
@RequestMapping("/update")
public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
  ...
}
```

```java
@RequestMapping("/save")
public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand){
  ...
}
```

当然如果某个分组包含另一个分组的所有校验规则，就可以通过接口继承实现，例如：定义一个 AllGroup 分组，继承 AddGroup 和 UpdateGroup，
则校验 AllGroup 时会执行这两个分组的所有规则。而如果 @Validated 没有指定分组的话，它的效果就等同于 @Valid，只能校验那些也没有使用分组的注解。
如果需要同时使用别的分组和默认分组，就需要显示包含 Default.class（@Validated({AddGroup.class, Default.class})）。

****
### 3.5 自定义校验注解

一个完整的自定义校验注解需要包含 3 个部分：

- 自定义注解类：定义注解的基本信息（如名称、适用目标、属性等）
- 校验器（Validator）：实现具体的校验逻辑（实现 ConstraintValidator 接口）
- 默认错误信息：指定校验失败时的默认提示信息（通常通过资源文件配置）

以 @NotNull 为模板参考，一个校验注解需要哪些东西：

```java
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(List.class)
@Documented
@Constraint(validatedBy = {})
public @interface NotNull {
    String message() default "{javax.validation.constraints.NotNull.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        NotNull[] value();
    }
}
```

- @Target：限制这个注解可以用在什么地方
- @Retention：指定注解的生命周期
- @Documented：让该注解出现在 Javadoc 中
- @Repeatable：可以在 List 上重复使用
- @Constraint：标识这是一个 Bean Validation 约束注解，并指定它的校验器类。但这里是一个空数组，因为有些注解是由 Bean Validation 的默认实现直接提供校验逻辑（内置约束），
所以不需要自己指定校验器，但是自定义注解必须写自己的校验器类，否则框架不知道用什么逻辑来验证
- message()：定义校验失败时的默认提示信息，这个里面写的已经定义好的默认提示信息，例如：javax.validation.constraints.NotNull.message = must not be null。
这里的 {} 是 占位符语法，意思是不要直接把里面的字符串当成错误信息，而是把它当成一个资源 key，去国际化资源文件中查找对应的文本。
自定义注解时推荐写成类似 {注解的包名.message}，这样方便统一管理提示语。但是因为自定义的 message 是不在国际化资源文件中的，如果不自定义默认的错误信息，就会直接输出 key 本身。 
所以一般会在配置文件里定义。
- groups()：分组校验支持，指定该约束属于哪些校验组。
- payload()：承载元信息，不参与校验逻辑，但 JSR 303 规范要求所有约束注解必须有这个属性，否则不算规范实现，所以直接复制即可。

综上，根据 JSR303 规范，message()、groups()、payload() 是必须要有的，因为校验框架是通过反射查找这些方法的。如果漏了，Hibernate Validator 这样的实现类会直接抛异常。
现在对 showStatus 字段进行自定义一个注解，用来限制输入的内容只能为 0 或 1：

```java
/**
 * 显示状态[0-不显示；1-显示]
 */
private Integer showStatus;
```

自定义一个 @ValueList 注解：

```java
@Documented
@Constraint(validatedBy = { ListValueConstraintValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ListValue {

    String message() default "{com.project.common.valid.ListValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] values() default { };
}
```

关于提示的默认错误信息，JSR 303 规范规定，默认会去类路径下查找 ValidationMessages.properties 这个文件，并把它作为默认的国际化资源文件。所以可以在 resources 目录下创建，
然后自定以需要返回的默认错误信息：

```properties
com.project.common.valid.ListValue.message=必须提交指定的值
```

除了上面的三个必须包含的内容，这里还自定义了一个 int 类型的 values 数组，它是自定义注解里的属性，在使用这个注解时可以通过 values 传值，所以需要解读传入的值是否满足当前想定义的条件，
就需要在校验器的数组中自定义一个校验器，它用来自定义校验规则：

引入依赖：

```xml
<!--validation-->
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
</dependency>
```

通过实现 ConstraintValidator 的方式完成自定义校验器，实现它里面的两个方法：

- initialize() 方法会在校验前执行一次，用来读取注解里的 values
- isValid() 方法才是真正判断字段是否合法的地方

```java
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {
    private Set<Integer> set = new HashSet<>();
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] values = constraintAnnotation.values(); // 从注解上拿到 values 的值
        for (int i : values) {
            set.add(i);
        }
    }
  // 需要校验的值
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
      return value != null && set.contains(value);
    }
}
```

当在 showStatus 字段上这样定义时:

```java
@ListValue(values = { 0, 1 })
private Integer showStatus;
```

initialize 方法就会读取 @ListValue(values = {0, 1}) 注解参数，然后把它们存进一个 Set<Integer>。当 Controller 拿到传入的 showStatus 的值时，
就会用 set.contains(value) 检查值是否在 {0, 1} 里，然后根据结果选择是否拦截。

****
## 4. 属性分组

### 4.1 SPU 与 SKU

- SPU（Standard / Stock / Standard Product Unit）：

表示标准化产品单元，它描述一类产品的不变属性与业务概念，例如：品牌、型号、描述、分类、基础图文等。例如：iPhone 14 128GB（蓝色）的产品型号抽象成 SPU（有时 SPU 会不包括颜色/容量这些可变规格，依实现而定）。

- SKU（Stock Keeping Unit）：

表示“最小库存单元/售卖单元”。可直接买到、需要独立管理库存与条码的具体商品。通常由 SPU + 一组规格（规格值）唯一确定。例如：T恤（SPU: Basic Tee） + 颜色: 红 + 尺码: M -> 一个 SKU。

主要区别：

- 身份与作用：SPU 用于组织、展示和搜索；SKU 用于下单、库存、价格、配送
- 数据关注点：SPU 关注商品描述、类目、品牌、长文本；SKU 关注价格、库存、条码、重量、尺寸、货位
- 变更频率：SPU 属性相对稳定；SKU 价格/库存变动频繁
- 业务场景：
  - 列表页 / 搜索通常返回 SPU（同时展示该 SPU 所有 SKU 的最小/最大价格与可选的颜色/尺码集合） 
  - 详情页（用户点颜色/尺码）切换到某个 SKU 展示具体库存/价格/物流信息 
  - 下单、出入库、结算都基于 SKU

例如：

- 服装：SPU = “品牌 X 基础款短袖”，SKU = (“红色”, “M”)、(“蓝色”, “L”) 等 
- 手机：SPU = “Phone Model A”，SKU = (“128GB”, “黑色”), (“256GB”, “白色”)
- 书籍：有时一本书本身就是 SKU（没有变体），SPU 与 SKU 可一一对应

****
### 4.2 获取分类属性分组

现在在前端新增了一个页面，左侧展示三级分类，右侧展示某个分类属性的分组信息，在点击到某个第三级分类的时候，右侧就会查询下面的表，展示信息。因为每个第三级分类都代表了一个品类，
例如手机，它就包含各种分组信息，例如处理器、重量、尺寸等：

| 名称          | 类型    | 长度 | 小数点 | 不是 null | 虚拟 | 键 | 注释     |
| ------------- | ------- | ---- | ------ |---------| ---- |---| -------- |
| attr_group_id | bigint  |      |        | √       |      | √ | 分组 id  |
| attr_group_name | char | 20   |        |         |      |   | 组名     |
| sort          | int     |      |        |         |      |   | 排序     |
| descript      | varchar | 255  |        |         |      |   | 描述     |
| icon          | varchar | 255  |        |         |      |   | 组图标   |
| catalog_id    | bigint  |      |        |         |      |   | 所属分类 id | 

由前端发送分页查询，发送的参数格式如下：

```json
{
  "page": 1, // 当前页码
  "limit": 10, // 每页记录条数
  "sidx": "id", // 排序字段
  "order": "asc/desc", // 排序方式
  "key": "华为" // 检索关键字
}
```

```vue
getDataList () {
  this.dataListLoading = true
  this.$http({
    url: this.$http.adornUrl(`/product/attrgroup/list/${this.catId}`),
    method: 'get',
    params: this.$http.adornParams({
      'page': this.pageIndex,
      'limit': this.pageSize,
      'key': this.dataForm.key
    })
  }).then(({data}) => {
    if (data && data.code === 0) {
      this.dataList = data.page.list
      this.totalPage = data.page.totalCount
    } else {
      this.dataList = []
      this.totalPage = 0
    }
    this.dataListLoading = false
  })
},
```

所以后端可以使用一个 Map 来接收：

```java
@RequestMapping("/list/{catelogId}")
public R list(@RequestParam Map<String, Object> params, @PathVariable Long catelogId) {
    PageUtils page = attrGroupService.queryPage(params, catelogId);
    return R.ok().put("page", page);
}
```

Service 层：

```java
@Override
public PageUtils queryPage(Map<String, Object> params) {
    IPage<AttrGroupEntity> page = this.page(
            new Query<AttrGroupEntity>().getPage(params),
            new QueryWrapper<AttrGroupEntity>()
    );
    return new PageUtils(page);
}
```

this.page(...) 是 MyBatis-Plus 框架封装的分页查询方法：

```java
IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper);
```

它根据传入的分页对象和查询条件，查询数据库对应表并返回分页结果。返回类型 IPage<AttrGroupEntity> 是 MyBatis-Plus 的分页结果接口，包含：

- 当前页数据列表 
- 总记录数 
- 总页数 
- 前页码 
- 每页大小等信息

而传入 page() 方法的两个参数：

1、new Query<AttrGroupEntity>().getPage(params)： 

- Query<T> 是由逆向工程生成的一个工具类，它负责从 params 里解析分页参数，然后把这些参数构造成 IPage 对象：

```java
/**
 * 查询参数
 */
public class Query<T> {
  public IPage<T> getPage(Map<String, Object> params) {
    return this.getPage(params, null, false);
  }
  public IPage<T> getPage(Map<String, Object> params, String defaultOrderField, boolean isAsc) {
    // 分页参数
    long curPage = 1;
    long limit = 10;
    if(params.get(Constant.PAGE) != null){
      curPage = Long.parseLong((String)params.get(Constant.PAGE));
    }
    if(params.get(Constant.LIMIT) != null){
      limit = Long.parseLong((String)params.get(Constant.LIMIT));
    }
    // 分页对象
    Page<T> page = new Page<>(curPage, limit);
    // 分页参数
    params.put(Constant.PAGE, page);
    // 排序字段
    // 防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
    String orderField = SQLFilter.sqlInject((String)params.get(Constant.ORDER_FIELD));
    String order = (String)params.get(Constant.ORDER);
    // 前端字段排序
    if(StringUtils.isNotEmpty(orderField) && StringUtils.isNotEmpty(order)){
      if(Constant.ASC.equalsIgnoreCase(order)) {
        return  page.addOrder(OrderItem.asc(orderField));
      }else {
        return page.addOrder(OrderItem.desc(orderField));
      }
    }
    // 没有排序字段，则不排序
    if(StringUtils.isBlank(defaultOrderField)){
      return page;
    }
    // 默认排序
    if(isAsc) {
      page.addOrder(OrderItem.asc(defaultOrderField));
    }else {
      page.addOrder(OrderItem.desc(defaultOrderField));
    }
    return page;
  }
}
```

2、new QueryWrapper<AttrGroupEntity>()：

- 这是 MyBatis-Plus 的条件构造器，空构造意味着没有任何 WHERE 限制条件，即搜索所有（无 WHERE 条件）
- 所以 Mybati-Plus 的分页为：this.page(IPage<T>, Wrapper<T>)

通过传递分类 id 查询的方法如下：

```java
@Override
public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
  String key = (String) params.get("key");
  // select * from pms_attr_group where catelog_id = ? and (attr_group_id = key or attr_group_name like %key%)
  QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
  if (!StringUtils.isNullOrEmpty(key)) {
    wrapper.and((obj) -> {
      obj.eq("attr_group_id", key).or().like("attr_group_name", key);
    });
  }
  if (catelogId == 0) {
    return this.queryPage(params);
  } else {
    wrapper.eq("catelog_id", catelogId);
    IPage<AttrGroupEntity> page = this.page(
            new Query<AttrGroupEntity>().getPage(params),
            wrapper
    );
    return new PageUtils(page);
  }
}
```

因为前端传入的请求参数除了分类 id，还有可能传入某些关键字（例如分组 id、分组名称），所以需要把这些关键字作为查询数据库的条件，但硬性前提条件一定是分类 id，
即前端点击的第三级分类的 id。同样，使用 Mybatis-Plus 的分页查询时需要传入封装成 IPage 类型的请求参数和查询条件。

既然使用到了 Mybatis-Plus 自带的分页查询，那就也要配置一下它的分页插件，

```java
@Configuration
@MapperScan("com.project.gulimall.product.dao")
public class MyBatisPlusConfig {
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
    pagination.setOverflow(false); // 溢出页的处理：true/false，true 表示请求溢出页时返回第一页数据；false 表示返回空数据
    pagination.setMaxLimit(500L); // 单页最大条数（-1 不限制）
    interceptor.addInnerInterceptor(pagination);
    return interceptor;
  }
}
```

****
### 4.3 回显三级分类 id 关系

在前端点击修改按钮时，需要将对话框中需要显示的相关数据全部展示出来，而修改属性分组信息功能中，有一个修改所属分类的选项，也就是可以选择当前分组属于哪个具体的三级分类，
既然要让它显示类似 "家用电器/大家电/空调" 的这种效果，就需要获取到这一组分类对应的 id，这一组对应的 id 就是 [3/37/251]。

Controller 层：

因为前端点击修改按钮时是通过传递当前行的分组 id，即 attr_group_id（不是三级分类的 catId），所以需要通过它获取它所属的三级分类是哪个，而由于上面定义的规则，
存储的三级分类 id 是最小级，所以只能通过它去获取当前最消极三级分类的父类是哪些，然后封装进一个数组，作为 AttrGroupEntity 的字段，一起返回给前端：

```java
@RequestMapping("/info/{attrGroupId}")
public R info(@PathVariable("attrGroupId") Long attrGroupId){
    AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
    Long catelogId = attrGroup.getCatelogId();
    Long[] path = categoryService.findCatelogPath(catelogId);
    attrGroup.setCatelogPath(path);
    return R.ok().put("attrGroup", attrGroup);
}
```

Service 层：

因为需要查询三级分类的父类 id，所以就需要用到 CategoryService 中的方法。查找父类的 id 也挺方便的，因为 CategoryEntity 有个 parentCid 字段，
所以可以直接通过 get 方法判断它是否有父类，既然能这样获取到父类 id，那就直接通过递归获取到所有的父类，不过由于递归的特性，最终得到的 id 数组是逆序的，即子在父前，
所以需要反转一下顺序，不然前端展示的数据就是倒过来的：

```java
@Override
public Long[] findCatelogPath(Long catelogId) {
    List<Long> paths = new ArrayList<>();
    // 如果有父分类，就向上找
    // 因为添加进 path 的 id 是从最小层级开始的，所以 path 中的 id 排序为 三级 -> 二级 -> 一级
    // 所以进行一下反转操作
    Collections.reverse(findParentPath(catelogId, paths));
    return paths.toArray(new Long[0]);
}

private List<Long> findParentPath(Long catelogId, List<Long> paths) {
    // 收集当前节点 id
    paths.add(catelogId);
    // 根据 catelogId 查出当前分类的信息
    CategoryEntity category = this.getById(catelogId);
    if (category.getParentCid() != 0) {
      findParentPath(category.getParentCid(), paths);
    }
    return paths;
}
```

****
### 4.4 品牌关联分类与级联更新

#### 4.4.1 品牌关联分类

品牌分类关联功能是用来选择该品牌是属于哪个或哪几个分类的，当前端选择好分类后，将 catelogPath[] 数组的最后一个元素，即最小级分类（上面有记录怎么获取）的 id 和品牌 id 发送给后端：

```vue
<el-button type="text" size="small" @click="updateCatelogHandle(scope.row.brandId)">关联分类</el-button>

addCatelogSelect() {
  this.popCatelogSelectVisible = false;
  this.$http({
    url: this.$http.adornUrl("/product/categorybrandrelation/save"),
    method: "post",
    data: this.$http.adornData({
    brandId: this.brandId,
    catelogId: this.catelogPath[this.catelogPath.length - 1]
    }, false)
  }).then(({data}) => {
  this.getCateRelation();
  });
},

updateCatelogHandle(brandId) {
  this.cateRelationDialogVisible = true;
  this.brandId = brandId;
  this.getCateRelation();
},

getCateRelation() {
  this.$http({
    url: this.$http.adornUrl("/product/categorybrandrelation/catelog/list"),
    method: "get",
    params: this.$http.adornParams({
    brandId: this.brandId
    })
  }).then(({data}) => {
  this.cateRelationTableData = data.data;
  });
},
```

新增分类关联：

Controller 层：

```java
@RequestMapping("/save")
public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
    categoryBrandRelationService.saveDetail(categoryBrandRelation);
    return R.ok();
}
```

Service 层：

虽然品牌名称和分类名称都有对应的表，可以直接通过夺标联查获取，但是考虑到后期数据较多，可能导致频繁的进行多表联查，对数据库造成压力，所以直接在分类关联表中添加品牌名和分类名字段，
然后在这里通过传入的品牌 id 和分类 id 查询对应的名称即可。

```java
@Override
public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
    Long brandId = categoryBrandRelation.getBrandId();
    Long catelogId = categoryBrandRelation.getCatelogId();
    // 查询品牌名称
    BrandEntity brandEntity = brandDao.selectById(brandId);
    CategoryEntity category = categoryDao.selectById(catelogId);
    categoryBrandRelation.setBrandName(brandEntity.getName());
    categoryBrandRelation.setCatelogName(category.getName());
    this.save(categoryBrandRelation);
}
```

展示已添加的分类关联信息：

Controller 层：

```java
@RequestMapping(value = "/catelog/list", method = RequestMethod.GET)
public R catelogList(@RequestParam Long brandId){
    List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(
            new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId)
    );
    return R.ok().put("data", data);
}
```

****
#### 4.4.2 级联更新

上面有提及品牌关联分类表中使用了品牌名和分类名这两个字段，所以它们是两个单独的字段，当品牌表和分类表中对数据进行更新，由于它们没有和品牌关联分类表关联，所以这两个字段不会被同时更新，
这就需要在品牌表和分类表进行更新的同时，对品牌关联分类表的这两字段一起更新。

Controller 层：

这里将原来的 updateById() 修改为手动编写的方法 updateCascade()。

```java
@RequestMapping("/update")
public R update(@RequestBody CategoryEntity category){
    categoryService.updateCascade(category);
    return R.ok();
}
```

Service 层：

不管如何，既然是调用了更新分类表的控制器方法，那肯定要对分类表进行更新，所以先调用 updateById() 方法简单更新分类表，然后就是判断传入的 CategoryEntity 是否包含名称字段，
当然前端的表单验证是不允许名称为空的，但是为了保证代码的健壮性，还是添加了是否为空的判断。然后就是调用 categoryBrandRelationService#updateCategory() 方法，
对品牌关联分类表的分类名称进行同步的更新：

```java
@Override
public void updateCascade(CategoryEntity category) {
    this.updateById(category);
    if (!StringUtils.isEmpty(category.getName())) {
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }
}
```

品牌名称的同步修改同理：

```java
@RequestMapping("/update")
public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
    brandService.updateDetail(brand);
    return R.ok();
}

@Override
public void updateDetail(BrandEntity brand) {
    // 保证冗余字段的数据一致
    this.updateById(brand);
    if (!StringUtils.isEmpty(brand.getName())) {
      // 同步更新其它关联表中的数据
      categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());
      // TODO 更新其它关联
    }
}
```

****
## 5. 规格参数属性

### 5.1 规格参数新增功能

在属性分组里面有个关联功能，它用来关联当前属性分组的关联关系，例如属性分组里有一个主芯片，那么它的关联关系中就可以有 CPU 品牌、CPU 型号等。而规格参数就是用来充当这份关联关系的，
在规格参数中，就需要先录入一些属性，才能让属性分组的关联功能生效。前端发送的表单数据如下：

```json
{
  attrGroupId: 7, // 属性的分组 id
  attrName: "CPU 型号", // 属性名
  attrType: 1, // 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
  catelogId: 225, // 所属分类
  enable: 1, // 启用状态[0 - 禁用，1 - 启用]
  ico: "xl", // 属性图标
  searchType: 1, // 是否需要检索[0-不需要，1-需要]
  showDesc: 1, // 快速展示【是否展示在介绍上；0-否 1-是】
  t: 1755153318219, 
  valueSelect: "晓龙 888", // 可选值列表[用逗号分隔]
  valueType: 1 // 值类型[0-为单个值，1-可以选择多个值]
}
```

因为在添加这些规格参数的时候需要和属性分组关联起来，所以除了要将当前规格参数存入对应的 pms_attr 表中，还需要与 pms_attr_group 属性分组表产生关联，也就是新建一张表，
pms_attr_attrgroup_relation，它用来记录规格参数的 id 和属性分组的 id：

Controller 层：

```java
@RequestMapping("/save")
  public R save(@RequestBody AttrVo attr){
      attrService.saveAttr(attr);
      return R.ok();
  }
```

Service 层：

因为规格参数的表中没有属性分组 id 这个字段，所以需要用到 VO 视图对象，接收页面传递来的数据并风转成对象返回给前端页面。

```java
@Transactional
@Override
public void saveAttr(AttrVo attr) {
    AttrEntity attrEntity = new AttrEntity();
    BeanUtils.copyProperties(attr, attrEntity);
    // 保存基本数据
    this.save(attrEntity);
    // 保存关联关系
    if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
      AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
      attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
      attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
      attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
    }
}
```

****
### 5.2 查询规格参数列表功能

Controller 层：

同样的，该查询也分为单个查询和模糊查询，就看前端发送的请求中是否携带分类 id。

```java
@GetMapping("/base/list/{catelogId}")
public R baseAttrList(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
    PageUtils page = attrService.queryBaseAttrPage(params, catelogId);
    return R.ok().put("page", page);
}
```

Service 层：

所以在进行查询操作前要先判断传入的分类 id 是否为 0，不为 0 则需要添加上分类 id 作为查询条件，为 0 则为查询所有分类的数据，此时仅需模糊判断参数 id 和参数名即可。

```java
@Override
public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId) {
    QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
    // 当 catelogId 为 0 代表没有点击某个具体的三级分类，即查询所有三级分类下的数据
    if (catelogId != 0) {
        queryWrapper.eq("catelog_id", catelogId);
    }
    String key = (String) params.get("key");
    if (!StringUtils.isEmpty(key)) {
        queryWrapper.and((wrapper) -> {
            wrapper.eq("attr_id", key).or().like("attr_name", key);
        });
    }
    IPage<AttrEntity> page = this.page(
            new Query<AttrEntity>().getPage(params),
            queryWrapper
    );
    ...
}
```

当然前端的展示除了规格参数的所有信息外，还额外添加了所属分类名和所属分组名的字段，所以除了查询 pms_attr 表，还需要利用到关联表中的分类 id 和分组 id 去查询它们对应的名称。
因此在获取到规格参数列表时，需要一一对应的利用规格参数的 id 查询关联表。

```java
List<AttrEntity> records = page.getRecords();
List<AttrResVo> attrResVoList = records.stream().map(attrEntity -> {
    AttrResVo attrResVo = new AttrResVo();
    BeanUtils.copyProperties(attrEntity, attrResVo);
    // 获取三级分类和分组的名字
    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(
            new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId())
    );
    if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId() != null) {
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
        if (attrGroupEntity != null) {
            attrResVo.setGroupName(attrGroupEntity.getAttrGroupName());
            CategoryEntity categoryEntity = categoryService.getById(attrGroupEntity.getCatelogId());
            if (categoryEntity != null) {
                attrResVo.setCatelogName(categoryEntity.getName());
            }
        }
    }
    return attrResVo;
}).collect(Collectors.toList());
PageUtils pageUtils = new PageUtils(page);
pageUtils.setList(attrResVoList);
return pageUtils;
```

先从查询到的列表中依次处理每个实体类，通过规格参数的 id 获取到关联表中对应的实体，再从实体中获取分类 id 和分组 id。最后封装进 AttrResVo 对象中，该对象继承  AttrVo，
仅在父类的基础上添加了分类名和分组名字段：

```java
@Data
public class AttrResVo extends AttrVo {
    /**
     * 所属三级分类名
     */
    private String catelogName;

    /**
     * 所属属性分组名
     */
    private String groupName;
}
```

最后先通过 IPage 对象创建好自定义的分页对象，获取基本的分页骨架，最后把里面包含的 AttrEntity 列表替换成 AttrResVo 列表：

```java
PageUtils pageUtils = new PageUtils(page);
pageUtils.setList(attrResVoList);
```

```java
private int totalCount;
private int pageSize;
private int totalPage;
private int currPage;
private List<?> list;

public PageUtils(IPage<?> page) {
    this.list = page.getRecords();
    this.totalCount = (int)page.getTotal();
    this.pageSize = (int)page.getSize();
    this.currPage = (int)page.getCurrent();
    this.totalPage = (int)page.getPages();
}
```

****
### 5.3 规格参数修改功能

因为修改某条数据需要先看到原始的数据，所以会发送一条查询请求，而规格参数的展示表单中需要包含完整的分类路径，所以原始的 AttrEntity 也不够用，此时可用直接在 AttrResVo 中添加，
这样并不会影响上面的列表展示，因为不存在赋值的情况，前端就算获取到了也不会展示出来。响应的数据应该为：

```json
{
  "msg": "success",
  "code":0,
  "attr": {
    "attrId": 4,
    "attrName": "aad",
    "searchType": 1,
    "valueType": 1,
    "icon": "qq",
    "valueSelect": "v; q;w"
    "attrType": 1,
    "enable": 1,
    "showDesc": 1,
    "attrGroupId": 1, // 分组 id
    "catelogId": 225, // 分类 id
    "catelogPath": [2, 34, 225]// 分类完整路径
  }
}
```

查询：

Controller 层：

```java
@RequestMapping("/info/{attrId}")
public R info(@PathVariable("attrId") Long attrId){
    AttrResVo attrResVo = attrService.getAttrInfo(attrId);
    return R.ok().put("attr", attrResVo);
}
```

Service 层：

```java
@Override
public AttrResVo getAttrInfo(Long attrId) {
    AttrEntity attrEntity = this.getById(attrId);
    AttrResVo attrResVo = new AttrResVo();
    BeanUtils.copyProperties(attrEntity, attrResVo);
    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(
            new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId())
    );
    if (attrAttrgroupRelationEntity != null) {
        // 设置分组信息
        attrResVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
        if (attrGroupEntity != null) {
            attrResVo.setGroupName(attrEntity.getAttrGroupName());
        }
    }
    // 设置分类信息
    Long catelogId = attrEntity.getCatelogId();
    // findCatelogPath 方法在 CatelogService 中定义过，可用直接使用
    Long[] catelogPath = categoryService.findCatelogPath(catelogId);
    attrResVo.setCatelogPath(catelogPath);
    CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
    if (categoryEntity != null) {
        attrResVo.setCatelogName(categoryEntity.getName());
    }
    return attrResVo;
}
```

修改：

Controller 层：

```java
@RequestMapping("/update")
public R update(@RequestBody AttrVo attrVo){
    attrService.updateAttr(attrVo);
    return R.ok();
}
```

Service 层：

```java
@Override
public void updateAttr(AttrVo attrVo) {
    AttrEntity attrEntity = new AttrEntity();
    BeanUtils.copyProperties(attrVo, attrEntity);
    this.updateById(attrEntity);
    // 修改关联的分组
    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
    attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
    attrAttrgroupRelationEntity.setAttrId(attrVo.getAttrId());
    attrAttrgroupRelationDao.update(
            attrAttrgroupRelationEntity,
            new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId())
    );
}
```

当前的代码逻辑只有在规格参数的所有数据都完整时才能生效，因为规格参数表中没有分组 id 这个字段，所以修改没有分组的规格参数时，会因为在关联表中找不到对应的规格参数 id 而导致修改失败，
因此需要判断数据是否完整，不完整则需要将代码逻辑修改为新增。

****
### 5.4 销售属性

在规格参数中有一个 attr_type 字段，它用来区分当前的规格参数属于销售类型（0）还是基本参数类型（1)，在前端查询销售属性的规格参数时，会在请求路径中携带 sale，
而基于前面的查询基本属性的代码，可以动态的修改后端接收的路径：

```java
@GetMapping("/{attrType}/list/{catelogId}")
public R baseAttrList(@RequestParam Map<String, Object> params,
                      @PathVariable("catelogId") Long catelogId,
                      @PathVariable("attrType") String attrType) {
    PageUtils page = attrService.queryBaseAttrPage(params, catelogId, attrType);
    return R.ok().put("page", page);
}
```

需要注意的是，销售属性的展示列表没有分组信息，所以在查询、新增和修改操作的时候，需要对当前的规格参数类型进行判断，避免浪费资源：

```java
@Override
public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
    QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
            // 在查询前先对类型进行判断
            .eq("attr_type", "base".equalsIgnoreCase(attrType) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
    ...
}
```

```java
@Override
public AttrResVo getAttrInfo(Long attrId) {
    ...
    if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
        if (attrAttrgroupRelationEntity != null) {
            // 设置分组信息
            attrResVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
            if (attrGroupEntity != null) {
                attrResVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }
    }
    // 设置分类信息
    ...
    return attrResVo;
}
```

```java
@Override
public void updateAttr(AttrVo attrVo) {
    ...
    if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
        // 修改关联的分组
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
        attrAttrgroupRelationEntity.setAttrId(attrVo.getAttrId());
        if (count > 0) {
            attrAttrgroupRelationDao.update(
                    attrAttrgroupRelationEntity,
                    new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId())
            );
        } else {
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }
}
```

****
### 5.5 分组与规格参数的关联功能

查询分组关联：

Controller 层：

```java
@GetMapping("/{attrgroupId}/attr/relation")
public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
    List<AttrEntity> attrEntityList = attrService.getRelationAttr(attrgroupId);
    return R.ok().put("data", attrEntityList);
}
```

Service 层：

因为点击关联按钮时可以获取到当前行的信息，即可以获取到分组的 id，通过该 id 查询关联表即可获取到规格参数的 id。不过有些分组还未关联到数据，通过 selectList 查询会获取到空集合，
所以需要对该集合进行判断，如果为空就返回空值，而不是直接把该集合作为参数传递给 listByIds。

```java
@Override
public List<AttrEntity> getRelationAttr(Long attrgroupId) {
    List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities =
            attrAttrgroupRelationDao.selectList(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId)
            );
    List<Long> attrIds = attrAttrgroupRelationEntities.stream()
            .map(AttrAttrgroupRelationEntity::getAttrId)
            .collect(Collectors.toList());
    if (attrIds.isEmpty()) {
        return Collections.emptyList(); // 返回空列表，避免 SQL 报错
    }
    return this.listByIds(attrIds);
}
```

删除分组中的关联信息：

Controller 层：

因为前端设置了批量删除，所以可能传递的是多条数据，而传递的数据格式如下：

```json
[{"attrId": 1, "attrGroupId": 3}]
```

所以接收参数时需要定义一个数组对象接收，而最终删除的是关联表中的数据，所以进行的操作是在 AttrService 层。

```java
@Data
public class AttrGroupRelationVo {
    private Long attrId;
    private Long attrGroupId;
}
```

Controller 层：

```java
@PostMapping("/attr/relation/delete")
public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos){
    attrService.deleteRelation(vos);
    return R.ok();
}
```

Service 层：

```java
@Override
public void deleteRelation(AttrGroupRelationVo[] vos) {
    // delete from pms_attr_attrgroup_relation where (attr_id = ? and attr_group_id = ?) or (attr_id ...)
    /*List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map(item -> {
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
        return attrAttrgroupRelationEntity;
    }).collect(Collectors.toList());
    attrAttrgroupRelationDao.deleteBatchRelation(entities);*/
    attrAttrgroupRelationDao.deleteBatchRelation(Arrays.asList(vos));
}
```

Mapper 层：

```sql
<delete id="deleteBatchRelation">
  DELETE FROM pms_attr_attrgroup_relation
  WHERE (attr_id, attr_group_id) IN
    <foreach collection="list" item="item" open="(" separator="),(" close=")">
      #{item.attrId}, #{item.attrGroupId}
    </foreach>
</delete>
```

****
### 5.6 查询分组未关联的规格参数

在属性分组页面中会展示各种属性，每种属性有对应的三级分类，这些属性也可以与规格参数进行相关联，但这些关联必须满足两点：

- 点击关联按钮的当前分组只能关联自己所属的分类里面的所有规格参数
- 点击关联按钮的当前分组不能关联任何已经被本分类下任意分组（包括当前分组）引用的属性

Controller 层：

```java
@GetMapping("/{attrgroupId}/noattr/relation")
public R attrNoRelation(@RequestParam Map<String, Object> params, @PathVariable("attrgroupId") Long attrgroupId) {
    PageUtils page = attrService.getNoRelationAttr(params, attrgroupId);
    return R.ok().put("page", page);
}
```

Service 层：

```java
@Override
public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
    // 1. 当前分组只能关联自己所属的分类里面的所有属性
    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
    Long catelogId = attrGroupEntity.getCatelogId();
    // 查出当前分类下的所有分组
    List<AttrGroupEntity> group = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>()
            .eq("catelog_id", catelogId));
    // 获取所有的分组 id
    List<Long> attrGroupIds = group.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
    // 通过分组 id 从关联表中获取所有关联信息
    List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = new ArrayList<>();
    if (!attrGroupIds.isEmpty()) {
      attrAttrgroupRelationEntities = attrAttrgroupRelationDao
              .selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", attrGroupIds));
    }
    // 从关联表中获取所有规格参数 id
    List<Long> attrIds = attrAttrgroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
    // 构造条件 where catelog_id = ? and attr_type = base
    QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
            .eq("catelog_id", catelogId)
            .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
    if (!attrIds.isEmpty()) {
      // 2. 当前分组只能关联别的分组没有引用的属性，所以查出的数据不能在关联表中存在
      queryWrapper.notIn("attr_id", attrIds);
    }
    String key = (String) params.get("key");
    if (!StringUtils.isEmpty(key)) {
      queryWrapper.and(wrapper -> {
        wrapper.eq("attr_id", key).or().like("attr_name", key);
      });
    }
    IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),
            queryWrapper
    );
    return new PageUtils(page);
}
```

既然关联的所有规格参数必须和本分组所在同一个三级分类，那就需要查询当前分组的三级分类是哪个，这可以通过分组 id 查询到。查到了该分类，就需要查看该分类下创建了多少个分组，
因为某个分组关联了规格参数的话，就会在关联表中添加数据，那就可以去关联表中找找有多少分组 id 是符合该分类下的分组的。查到了这些分组后，就需要排除与这些分组关联的规格参数 id，
也就是要用到 notIn(...)。不能引入的规格参数的条件都满足了，就只需要再根据分类 id 查询规格参数表即可。需要注意的是：当前操作的分组已经引入过的规格参数也会排除在外，
因为查询该分类下的所有分组 id 时就已经包括了自己，所以查出的需要排除的规格参数的 id 也包含其中。最后把所有的条件封装成 QueryWrapper 后传递给 PageUtils 并返回给前端。

****
### 5.7 确认新增关联关系

在点击新增按钮时需要弹出可以新增的规格参数，也就是上面的功能。然后选择它们并进行添加，也就是在关联表中新增对应的 attr_id 与 attr_group_id。

Controller 层：

```java
@PostMapping("/attr/relation")
public R attrRelation(@RequestBody List<AttrGroupRelationVo> vos) {
    attrAttrgroupRelationService.saveBatch(vos);
    return R.ok();
}
```

Service 层：

```java
@Override
public void saveBatch(List<AttrGroupRelationVo> vos) {
    List<AttrAttrgroupRelationEntity> entities = vos.stream().map(item -> {
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
        return attrAttrgroupRelationEntity;
    }).collect(Collectors.toList());
    this.saveBatch(entities);
}
```

****
## 6. 发布商品

### 6.1 获取分类关联的品牌

在前端发布商品页面，在增填商品的时候是先选择具体的分类，然后再根据该分类下展示的一些品牌来进行选择，所以在选择分类的时候就会发送一个查找该分类的品牌的请求。

Controller 层：

前端发送分类 id 给后端，在查询到所有属于该分类的品牌实体后，将需要展示的品牌信息封装进 VO（实际需要的只是 brandName，brandId 便于后续的交互使用）。

```java
@GetMapping("/brands/list")
public R relationBrandList(@RequestParam(value = "catId", required = true) Long catId){
    List<BrandEntity> brandEntities = categoryBrandRelationService.getBrandsById(catId);
    List<BrandVo> BrandVos = brandEntities.stream().map(brandEntity -> {
        BrandVo brandVo = new BrandVo();
        brandVo.setBrandId(brandEntity.getBrandId());
        brandVo.setBrandName(brandEntity.getName());
        return brandVo;
    }).collect(Collectors.toList());
    return R.ok().put("data", BrandVos);
}
```

Service 层：

分类和品牌有一个中间表 pms_category_brand_relation，它存储分类和品牌的 id 与 name，所以可以通过它来获取该分类下的所有品牌 id，然后查询品牌表获取品牌实体类集合，
其实这里通过中间表即可获取该功能需要展示的内容，但为了后续可以方便其它功能调用该接口，所以进行了进一步的封装。但需要注意的是，可能由于关联表的数据更新不及时，
前期需要对查到的数据进行非空处理，如果是空数据则过滤掉。

```java
@Override
public List<BrandEntity> getBrandsById(Long catId) {
    List<CategoryBrandRelationEntity> categoryBrandRelationEntityList =
            categoryBrandRelationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
    List<BrandEntity> brandEntityList =
            categoryBrandRelationEntityList.stream().map(categoryBrandRelationEntity -> {
                        Long brandId = categoryBrandRelationEntity.getBrandId();
                        return brandDao.selectById(brandId);
                    })
                    .filter(Objects::nonNull) // 过滤掉 null 对象
                    .collect(Collectors.toList());
    return brandEntityList;
}
```

****
### 6.2 获取分类下所有分组以及属性

在选择完分类和该分类下对应的品牌后，填写完基本信息，点击 "下一步：设置基本参数" 按钮，进入对该品牌的规格参数的填写。在上一步获取到了分类信息，可以通过分类 id 获取到该分类下的分组信息，
获取到分组信息后又可以获取到分组信息关联的规格参数的所有信息，而规格参数中设置了每个参数的可选值等信息，由管理员自行选择即可，主要的就是这两层的获取过程。

因为发送该请求的页面需要获取到所有分组和每个分组的规格参数信息，一个分组对应该分类的一个信息框架，该分组的规格参数则是更细致的信息，所以需要封装成一个新的对象来返回数据。
该对象封装了分组的信息和规格参数信息列表：

```java
@Data
public class AttrGroupWithAttrsVo {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    /**
     * 封装规格参数 attr 表的所有信息
     */
    private List<AttrEntity> attrs;
}
```

Controller 层：

```java
/**
 * 获取分类下所有分组以及属性
 */
@GetMapping("/{catelogId}/withattr")
public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId) {
    // 查出当前分类下的所有属性分组
    // 查出每个属性分组的规格参数属性
    List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
    return R.ok().put("data", vos);
}
```

Service 层：

```java
@Override
public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
    // 1. 查询分组信息
    List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
    // 2. 查询每个分组的所有属性
    List<AttrGroupWithAttrsVo> AttrGroupWithAttrsVoList = attrGroupEntities.stream().map(attrGroupEntity -> {
        AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
        BeanUtils.copyProperties(attrGroupEntity, attrsVo);
        // 根据分组 id 查找关联的所有规格参数基本信息（AttrService 层封装的方法）
        List<AttrEntity> attrEntities = attrService.getRelationAttr(attrsVo.getAttrGroupId());
        attrsVo.setAttrs(attrEntities);
        return attrsVo;
    }).collect(Collectors.toList());
    return AttrGroupWithAttrsVoList;
}
```

****
### 6.3 管理员新增商品

#### 6.3.1 流程

新增商品的整体流程为：

1、填写商品的基本信息

```scss
┌──────────────────────────────────────────┐
│ 商品名称 [ 华为______________________ ]    │
│ 商品描述 [ 华为______________________ ]    │
│ 选择分类 [ 手机/手机通讯/手机   ▼ ]          │
│ 选择品牌 [ 华为               ▼ ]          │
│ 商品重量(Kg) [-] 0.000 [+]                │
│ 设置信息：金币 [...] 成长值 [...]           │
│----------------------------------------- │
│ 商品介绍 *                                │
│ [预览图1][预览图2][ + ]                    │
│------------------------------------------│
│ 商品图集 *                                │
│ [图1] [图2] [图3] [ + ]                   │
│------------------------------------------│
│ [ 下一步：设置基本参数 ]                    │
└──────────────────────────────────────────┘
```

在该页面填写一些基本的信息后，进入下一步，主要是从这里获取对应的分类下的分组和分组的关联规格参数属性。

2、填写规格参数

```text
┌── 基本信息\主芯片 ──────────────────────┐
│ 入网型号         ...     □ 快速展示     │
│ 上市年份         ...     □ 快速展示     │
│ 颜色             ...    □ 快速展示      │
│ 机身颜色         ...     ☑ 快速展示     │
│ 机身长度 (mm)    ...     □ 快速展示     │
│ 机身材质工艺      ...     □ 快速展示     │
│ 上市年份         ...     □ 快速展示     │
│                   ──                 │
│ □ 上一步   ☐ 下一步：设置销售属性        │
└──────────────────────────────────────┘
```

因为选择了分类，所以可以获取到该分类下的分组，而每个分组又有对应的规格参数，这些信息最后都会展示在页面。

3、填写销售属性

┌─────────────────────────────────────────────────────────────┐
│ 选择销售属性                                                  │
│-------------------------------------------------------------│
│ 入网型号   [ ] A2217  [ ] C3J  [ ] 以官网信息为准  [+自定义]     │
│ 颜色      [ ] 黑色    [ ] 白色  [ ] 蓝色            [+自定义]   │
│ 内存      [ ] 4GB     [ ] 6GB   [ ] 8GB   [ ] 12GB  [+自定义] │
└─────────────────────────────────────────────────────────────┘

这个步骤和上面的类似，因为规格参数分为基本属性和销售属性，第二步填写的就是基本属性。基本属性一般是下滑商品页面后看到的关于该型号商品的总体信息，
而销售属性则是选择商品时的一些独有属性，例如手机的内存、颜色等信息。

4、设置 sku 信息

通过上面三步的信息完善，这一步会通过笛卡尔积生成多个商品信息，例如：

- 华为 A2217 黑色 4GB
- 华为 A2217 白色 4GB
- 华为 A2217 黑色 8GB
- 华为 A2217 白色 4GB
- ...

然后需要对每个具体的商品设置图集和默认展示的图片、折扣、满减、会员优惠、会员积分等。

****
#### 6.3.2 保存 spu 信息

这里需要保存的 spu 大概有：商品名称、描述、分类、品牌、重量、金币、成长值、商品介绍、商品图集，这些信息都是对整个商品类别统一的描述，不区分规格，所以它们属于 SPU。
无论这个手机是黑色还是白色，这些基本信息在 SPU 层面上都是相同的。所以填写的规格参数（基本属性）就是需要保存的 spu 基本信息。

Controller 层：

```java
@RequestMapping("/save")
public R save(@RequestBody SpuSaveVo vo){
    spuInfoService.saveSpuInfo(vo);
    return R.ok();
}
```

因为一个 spu 信息包含很多东西，所以需要额外创建一个对象来接收、封装它们，例如上面提到的商品的名称、描述、分类...但为了前端展示，这里把 spu 对应的 sku 作为列表嵌套在 spu 对象里，
方便一次性渲染。但需要注意的是：spu 不包含 sku 本身，它只描述商品大类的共性信息，sku 通过 spu 的 id 关联到 spu。所以这里只调用了这一个接口。

```java
@Data
public class SpuSaveVo {
    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;
}
```

Service 层：

1、保存 spu 的基本信息，表 pms_spu_info

因为接收的 SpuSaveVo 是扩展后的对象，所以先要保存一下基本的 SpuInfoEntity 实体

```java
// 1. 保存 spu 的基本信息，表 pms_spu_info
SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
BeanUtils.copyProperties(vo, spuInfoEntity);
this.saveBaseSpuInfo(spuInfoEntity);
```

```java
/**
 * 保存 spu 的基本信息，表 pms_spu_info
 */
@Override
public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
    spuInfoDao.insert(spuInfoEntity);
}
```

不过 SpuInfoEntity 的 createTime 和 updateTime 是 SpuSaveVo 没包含的，所以需要手动赋值：

```java
@Data
@TableName("pms_spu_info")
public class SpuInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 商品id
	 */
	@TableId
	private Long id;
	/**
	 * 商品名称
	 */
	private String spuName;
	/**
	 * 商品描述
	 */
	private String spuDescription;
	/**
	 * 所属分类id
	 */
	private Long catalogId;
	/**
	 * 品牌id
	 */
	private Long brandId;
	/**
	 * 
	 */
	private BigDecimal weight;
	/**
	 * 上架状态[0 - 新建，1 - 上架，2-下架]
	 */
	private Integer publishStatus;

	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updateTime;
}
```

而 MyBatis-Plus 提供了一种自动填充功能，通过实现 MetaObjectHandler 接口，用来指定插入和更新时如何填充值：

```java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    
    // 插入时自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充创建时间和更新时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    // 更新时自动填充
    @Override
    public void updateFill(MetaObject metaObject) {
        // 只更新更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
```

然后在指定的字段上添加 @TableField(fill = FieldFill.INSERT)（插入时生效）/@TableField(fill = FieldFill.INSERT_UPDATE)（插入和更新时生效）即可。

2、保存 spu 的描述图片集合，表 pms_spu_info_desc

在前端填写基本信息时有要求上传一些描述图片集合，这个就是一些图片集合，里面是一些总体的描述，因为它是另一张表，所以需要通过 spuId 进行关联。
因为可能上传不止一张描述图片，所以需要将这多条 url 拼接成字符串然后再存入表中。

```java
// 2. 保存 spu 的描述图片集合，表 pms_spu_info_desc
List<String> decriptList = vo.getDecript();
SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
// 拼接 decriptList 集合中的商品介绍的图片的 url，用 “，” 隔开
spuInfoDescEntity.setDecript(String.join(",", decriptList));
// 将数据存入表 pms_spu_info_desc
spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);
```

SpuInfoDescService：

```java
@Override
public void saveSpuInfoDesc(SpuInfoDescEntity spuInfoDescEntity) {
    spuInfoDescDao.insert(spuInfoDescEntity);
}
```

3、保存 spu 的图片集，表 pms_spu_images

同样的，关于该商品的一些图片也需要通过 spuId 进行关联，所以需要传入 spuId。

```java
List<String> images = vo.getImages();
// 传入 spu 的 id 和图片集
spuImagesService.saveImages(spuInfoEntity.getId(), images);
```

SpuImagesService：

当图片不为空时，则把它添加进表 pms_spu_images。

```java
@Transactional
@Override
public void saveImages(Long id, List<String> images) {
    if (images.isEmpty()) {

    } else {
        List<SpuImagesEntity> SpuImagesEntities = images.stream().map(img -> {
            SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
            spuImagesEntity.setSpuId(id);
            spuImagesEntity.setImgUrl(img);
            return spuImagesEntity;
        }).collect(Collectors.toList());
        this.saveBatch(SpuImagesEntities);
    }
}
```

4、保存 spu 的规格参数，表 pms_product_attr_value

表 pms_product_attr_value 是用来关联商品和规格参数的，所以需要先获取到规格参数的 attrId，然后去查找对应的规格参数的相信信息，把获取到的 AttrName、AttrValue、ShowDesc
存入 ProductAttrValueEntity 实体，将它们关联起来。

```java
List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
List<ProductAttrValueEntity> ProductAttrValueEntities = baseAttrs.stream().map(baseAtr -> {
    ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
    productAttrValueEntity.setAttrId(baseAtr.getAttrId());
    AttrEntity attrEntity = attrService.getById(baseAtr.getAttrId());
    productAttrValueEntity.setAttrName(attrEntity.getAttrName());
    productAttrValueEntity.setAttrValue(baseAtr.getAttrValues());
    productAttrValueEntity.setQuickShow(baseAtr.getShowDesc());
    productAttrValueEntity.setSpuId(spuInfoEntity.getId());
    return productAttrValueEntity;
}).collect(Collectors.toList());
productAttrValueService.saveProductAttr(ProductAttrValueEntities);
```

ProductAttrValueService：

```java
@Transactional
@Override
public void saveProductAttr(List<ProductAttrValueEntity> productAttrValueEntities) {
    this.saveBatch(productAttrValueEntities);
}
```

****
#### 6.3.3 保存 sku 基本信息

上面有记录，前端存储 sku 的基本信息时是把它作为 spu 的一个列表字段存储的，这是方便前后端的数据传递，所以可以直接从 SpuSaveVo 中获取 sku 的信息集合。因为共用一个接口，
所以这里只记录 Service 层。

```java
// 6. 保存当前 spu 对应的所有 sku 信息
List<Skus> skusList = vo.getSkus();
if (skusList != null && !skusList.isEmpty()) {
    skusList.forEach(skus -> {
        String defaultImg = "";
        for (Images image : skus.getImages()) {
            if (image.getDefaultImg() == 1) {
                defaultImg = image.getImgUrl();
            }
        }
        /**
         * 只有这四个字段一样
         * private String skuName;
         * private BigDecimal price;
         * private String skuTitle;
         * private String skuSubtitle;
         */
        // 6.1 保存 sku 的基本信息，表 pms_sku_info
        SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
        BeanUtils.copyProperties(skus, skuInfoEntity);
        skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
        skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
        skuInfoEntity.setSaleCount(0L);
        skuInfoEntity.setSpuId(spuInfoEntity.getId());
        skuInfoEntity.setSkuDefaultImg(defaultImg);
        skuInfoService.saveSkuInfo(skuInfoEntity);

        // 6.2 保存 sku 的图片信息，表 pms_sku_images
        Long skuId = skuInfoEntity.getSkuId();
        List<SkuImagesEntity> skuImagesEntities = skus.getImages().stream().map(img -> {
            SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
            skuImagesEntity.setSkuId(skuId);
            skuImagesEntity.setImgUrl(img.getImgUrl());
            skuImagesEntity.setDefaultImg(img.getDefaultImg());
            return skuImagesEntity;
        }).collect(Collectors.toList());
        skuImagesService.saveBatch(skuImagesEntities);

        // 6.3 保存 sku 的销售规格参数信息，表 pms_sku_sale_attr_value
        List<Attr> attrList = skus.getAttr();
        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrList.stream().map(attr -> {
            SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
            BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
            skuSaleAttrValueEntity.setSkuId(skuId);
            return skuSaleAttrValueEntity;
        }).collect(Collectors.toList());
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

        // 6.4 保存 sku 的优惠、满减等信息，表 gulimall_sms -> sms_sku_ladder、sms_sku_full_reduction、sms_member_price、sms_spu_bounds

    });
}
```

1、保存 sku 的基本信息，表 pms_sku_info

因为会获取到的很多条 sku 数据，所以需要依次遍历集合中的每条数据，也就是用到 forEach(...)，然后就是保存 sku 的基本信息到表 pms_sku_info，并给前端未赋值的字段进行手动赋值。

```java
// 6.1 保存 sku 的基本信息，表 pms_sku_info
SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
BeanUtils.copyProperties(skus, skuInfoEntity);
skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
skuInfoEntity.setSaleCount(0L);
skuInfoEntity.setSpuId(spuInfoEntity.getId());
skuInfoEntity.setSkuDefaultImg(defaultImg);
skuInfoService.saveSkuInfo(skuInfoEntity);
```

这里对 sku 中的图片集合遍历，找出默认图片的 url，然后赋值给 SkuInfoEntity。

```java
String defaultImg = "";
for (Images image : skus.getImages()) {
    if (image.getDefaultImg() == 1) {
        defaultImg = image.getImgUrl();
    }
}
```

SkuInfoService：

```java
@Override
public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
    this.save(skuInfoEntity);
}
```

2、保存 sku 的图片信息，表 pms_sku_images

sku 的图片信息也是使用的另一张表，所以要用 skuId 进行关联，然后从获取到的图片集合中依次处理每张图片的信息，并存入表 pms_sku_images。

```java
// 6.2 保存 sku 的图片信息，表 pms_sku_images
Long skuId = skuInfoEntity.getSkuId();
List<SkuImagesEntity> skuImagesEntities = skus.getImages().stream().map(img -> {
    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
    skuImagesEntity.setSkuId(skuId);
    skuImagesEntity.setImgUrl(img.getImgUrl());
    skuImagesEntity.setDefaultImg(img.getDefaultImg());
    return skuImagesEntity;
}).collect(Collectors.toList());
skuImagesService.saveBatch(skuImagesEntities);
```

3、保存 sku 的销售规格参数信息，表 pms_sku_sale_attr_value

```java
// 6.3 保存 sku 的销售规格参数信息，表 pms_sku_sale_attr_value
List<Attr> attrList = skus.getAttr();
List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrList.stream().map(attr -> {
    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
    skuSaleAttrValueEntity.setSkuId(skuId);
    return skuSaleAttrValueEntity;
}).collect(Collectors.toList());
skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
```

****
#### 6.3.4 调用远程服务保存优惠等信息

##### 1. 使用 Feign 完成远程调用

因为保存 spu 积分信息和 sku 的一些优惠信息需要调用到别的服务中的 Service 或 Dao 层，而当前程序使用的是微服务框架，所以不能直接调用，需要用到 OpenFeign，
具体使用可以参考 SpringCloud-Notes。

在 gulimall_product 服务下新建 Feign 接口，它们用来处理远程调用，需要注意的是，需要把对应的服务注册进 Nacos 并且支持 Feign，
在启动类上也要标注扫描 Feign（@EnableFeignClients(basePackages = "com.project.gulimall.product.feign")）。通常也会创建一个 XxxTo 类，
专门用来进行服务之间的数据传递，例如这里需要保存 spu 积分的信息：

```java
@Data
public class SpuBoundsTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
```

sku 优惠等信息：

```java
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}

@Data
public class MemberPrice {
    private Long id;
    private String name;
    private BigDecimal price;
}
```

在一般情况下，建议 Feign 接口的参数和对应的 Controller 一致，但也可以不是同一个类，不过必须能够被 Spring MVC / Jackson 序列化和反序列化。
也就是说只要传递给 Controller 的对象的字段可以被它需要接收的对象字段兼容，那就不会出问题。

```java
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);

}
```

例如 SpuBoundsTo 中的所有字段 SpuBoundsEntity 都有且一一对应，那就可以正常传递。

```java
@RequestMapping("/update")
public R update(@RequestBody SpuBoundsEntity spuBounds){
    spuBoundsService.updateById(spuBounds);
    return R.ok();
}

@PostMapping("/saveinfo")
public R saveInfo(@RequestBody SkuReductionTo skuReductionTo){
  skuFullReductionService.saveSkuReduction(skuReductionTo);
  return R.ok();
}
```

****
##### 2. 保存 spu 积分信息

Service 层：

```java
// 5. 保存 spu 的积分信息，表 gulimall_sms -> sms_spu_bounds
Bounds bounds = vo.getBounds();
SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
BeanUtils.copyProperties(bounds, spuBoundsTo);
spuBoundsTo.setSpuId(spuInfoEntity.getId());

R r = couponFeignService.saveSpuBounds(spuBoundsTo);
if (r.getCode() != 0) {
    log.error("远程保存 spu 级分信息失败！");
}
```

因为 Bounds 只有购买获得的积分和成长积分字段，要想关联到具体的 spu，就需要传入 spuId，所以封装了一个 SpuBoundsTo 类。

```java
@Data
public class Bounds {
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
```

然后通过 Feign 接口调用远程接口即可。

****
##### 2. 保存 sku 优惠信息

目前在前端页面设置的优惠包含：

- 商品满件数打折
- 商品满金额打折
- 会员独属优惠价

为了方便调试，就直接把这些数据全部封装进 SkuReductionTo 类中，它里面包含了每个商品对应设置的优惠力度，不过存在多个会员的情况，需要根据它们的等级设置不同的会员价，
所以会员价的信息会封装成集合。不管如何，最终都是靠 skuId 进行关联。

```java
// 6.4 保存 sku 的优惠、满减等信息，表 gulimall_sms -> sms_sku_ladder、sms_sku_full_reduction、sms_member_price
SkuReductionTo skuReductionTo = new SkuReductionTo();
BeanUtils.copyProperties(skus, skuReductionTo);
skuReductionTo.setSkuId(skuId);
// 有满几件打折或者满减优惠时才远程调用
if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
    if (r1.getCode() != 0) {
        log.error("远程保存 sku 优惠信息失败！");
    }
}
```

1、保存商品满件数打折

通过前端传递的 SpuSaveVo 中的 Skus 集合中的信息封装进 SkuReductionTo，然后从该对象中取出满件数打折的信息封装进 SkuLadderEntity 并存入表中。
不过存在未设置优惠的情况，所以需要对其进行判断（商品打折满足的件数需要大于 0）再进行存表操作。

```java
// 1. 满几件优惠多少，sms_sku_ladder
SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
if (skuReductionTo.getFullCount() > 0) {
    skuLadderService.save(skuLadderEntity);
}
```

2、商品满金额打折

满减折扣同理，也要对满足金额进行判断再进行村表操作，不过这里用的字段类型是 BigDecimal，所以判断大小时要用 compareTo，最终结果大于 0 表示位正数。

```java
// 2. 保存满减打折，sms_sku_full_reduction
SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
if (skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
    this.save(skuFullReductionEntity);
}
```

3、保存会员独属优惠价

从会员价集合中获取数据，然后依次封装进 MemberPriceEntity 实体，需要注意的是，要对会员价是否为空格进行判断，避免存入空数据进表。

```java
// 3. 保存会员价，sms_member_price
List<MemberPrice> memberPriceList = skuReductionTo.getMemberPrice();
if (memberPriceList != null && !memberPriceList.isEmpty()) {
    List<MemberPriceEntity> memberPriceEntities = memberPriceList.stream().map(memberPrice -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                memberPriceEntity.setMemberLevelId(memberPrice.getId());
                memberPriceEntity.setMemberLevelName(memberPrice.getName());
                memberPriceEntity.setMemberPrice(memberPrice.getPrice());
                memberPriceEntity.setAddOther(1);
                return memberPriceEntity;
            })
            .filter(memberPrice -> {
                return memberPrice.getMemberPrice().compareTo(new BigDecimal("0")) >= 0;
            })
            .collect(Collectors.toList());
    if(!memberPriceEntities.isEmpty()){
        memberPriceService.saveBatch(memberPriceEntities);
    }
}
```

****

新增商品整体代码：

```java
@Transactional
@Override
public void saveSpuInfo(SpuSaveVo vo) {
    // 1. 保存 spu 的基本信息，表 pms_spu_info
    SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
    BeanUtils.copyProperties(vo, spuInfoEntity);
    this.saveBaseSpuInfo(spuInfoEntity);

    // 2. 保存 spu 的描述图片集合，表 pms_spu_info_desc
    List<String> decriptList = vo.getDecript();
    SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
    spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
    // 拼接 decriptList 集合中的商品介绍的图片的 url，用 “，” 隔开
    spuInfoDescEntity.setDecript(String.join(",", decriptList));
    // 将数据存入表 pms_spu_info_desc
    spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

    // 3. 保存 spu 的图片集，表 pms_spu_images
    List<String> images = vo.getImages();
    // 传入 spu 的 id 和图片集
    spuImagesService.saveImages(spuInfoEntity.getId(), images);

    // 4. 保存 spu 的规格参数，表 pms_product_attr_value
    List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
    List<ProductAttrValueEntity> ProductAttrValueEntities = baseAttrs.stream().map(baseAtr -> {
        ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
        productAttrValueEntity.setAttrId(baseAtr.getAttrId());
        AttrEntity attrEntity = attrService.getById(baseAtr.getAttrId());
        productAttrValueEntity.setAttrName(attrEntity.getAttrName());
        productAttrValueEntity.setAttrValue(baseAtr.getAttrValues());
        productAttrValueEntity.setQuickShow(baseAtr.getShowDesc());
        productAttrValueEntity.setSpuId(spuInfoEntity.getId());
        return productAttrValueEntity;
    }).collect(Collectors.toList());
    productAttrValueService.saveProductAttr(ProductAttrValueEntities);

    // 5. 保存 spu 的积分信息，表 gulimall_sms -> sms_spu_bounds
    Bounds bounds = vo.getBounds();
    SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
    BeanUtils.copyProperties(bounds, spuBoundsTo);
    spuBoundsTo.setSpuId(spuInfoEntity.getId());

    R r = couponFeignService.saveSpuBounds(spuBoundsTo);
    if (r.getCode() != 0) {
        log.error("远程保存 spu 级分信息失败！");
    }

    // 6. 保存当前 spu 对应的所有 sku 信息
    List<Skus> skusList = vo.getSkus();
    if (skusList != null && !skusList.isEmpty()) {
        skusList.forEach(skus -> {
            String defaultImg = "";
            for (Images image : skus.getImages()) {
                if (image.getDefaultImg() == 1) {
                    defaultImg = image.getImgUrl();
                }
            }
            /**
             * 只有这四个字段一样
             * private String skuName;
             * private BigDecimal price;
             * private String skuTitle;
             * private String skuSubtitle;
             */
            // 6.1 保存 sku 的基本信息，表 pms_sku_info
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(skus, skuInfoEntity);
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfoEntity.setSaleCount(0L);
            skuInfoEntity.setSpuId(spuInfoEntity.getId());
            skuInfoEntity.setSkuDefaultImg(defaultImg);
            skuInfoService.saveSkuInfo(skuInfoEntity);

            // 6.2 保存 sku 的图片信息，表 pms_sku_images
            Long skuId = skuInfoEntity.getSkuId();
            List<SkuImagesEntity> skuImagesEntities = skus.getImages().stream().map(img -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        skuImagesEntity.setSkuId(skuId);
                        skuImagesEntity.setImgUrl(img.getImgUrl());
                        skuImagesEntity.setDefaultImg(img.getDefaultImg());
                        return skuImagesEntity;
                    })
                    .filter(img -> {
                        // 返回 true 就是需要，返回 false 就是不需要，也就不会保存进数据库
                        return !StringUtils.isEmpty(img.getImgUrl());
                    })
                    .collect(Collectors.toList());
            skuImagesService.saveBatch(skuImagesEntities);

            // 6.3 保存 sku 的销售规格参数信息，表 pms_sku_sale_attr_value
            List<Attr> attrList = skus.getAttr();
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrList.stream().map(attr -> {
                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                skuSaleAttrValueEntity.setSkuId(skuId);
                return skuSaleAttrValueEntity;
            }).collect(Collectors.toList());
            skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

            // 6.4 保存 sku 的优惠、满减等信息，表 gulimall_sms -> sms_sku_ladder、sms_sku_full_reduction、sms_member_price
            SkuReductionTo skuReductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(skus, skuReductionTo);
            skuReductionTo.setSkuId(skuId);
            // 有满几件打折或者满减优惠时才远程调用
            if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
                R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                if (r1.getCode() != 0) {
                    log.error("远程保存 sku 优惠信息失败！");
                }
            }
        });
    }

}
```

因为前端允许某些值为空，所以在后端进行表操作的时候一定要对数据是否为空进行判断，否则表与表之间的关联可能发生错误。

****
### 6.4 商品管理

#### 6.4.1 spu 检索

在上面的过程中完成了商品的新增，而每个商品又有 spu 和 sku 属性，所以需要区分开来管理它们，而 spu 检索，就是查询 spu 的关键信息找到对应的商品，检索条件分为：

- 分类
- 品牌
- 状态（新建/上架/下架）
- 模糊查询（检索 id 为输入的内容，或 spu 的名称中包含输入的内容）

Controller 层：

```java
@RequestMapping("/list")
public R list(@RequestParam Map<String, Object> params){
    PageUtils page = spuInfoService.queryPageByCondition(params);
    return R.ok().put("page", page);
}
```

Service 层：

对检索条件进行判断，非空时才让它们作为查询条件，否则使用空的 queryWrapper，即无查询条件。需要注意的是，模糊查询里面有用到 or，所以需要把这个 or 用 and 包含起来，
否则会导致查询条件与预期的不同。

```java
@Override
public PageUtils queryPageByCondition(Map<String, Object> params) {
    QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
    // 模糊检索
    String key = (String) params.get("key");
    if (!StringUtils.isEmpty(key)) {
        queryWrapper.and(wrapper -> {
            wrapper.eq("id", key).or().like("spu_name", key);
        });
    }
    // 状态条件
    String status = (String) params.get("status");
    if (!StringUtils.isEmpty(status)) {
        queryWrapper.eq("publish_status", status);
    }
    String brandId = (String) params.get("brandId");
    // 若 id 为 0 则不作为查询条件
    if (!StringUtils.isEmpty(brandId)  && !"0".equals(brandId)) {
        queryWrapper.eq("brand_id", brandId);
    }
    String catelogId = (String) params.get("catelogId");
    if (!StringUtils.isEmpty(catelogId)  && !"0".equals(catelogId)) {
        queryWrapper.eq("catalog_id", catelogId);
    }
    IPage<SpuInfoEntity> page = this.page(
            new Query<SpuInfoEntity>().getPage(params),
            queryWrapper
    );
    return new PageUtils(page);
}
```

****
#### 6.4.2 sku 检索

sku 检索和上面记录的类似，只不过检索的条件不一样：

- 分类
- 品牌
- 价格区间
- 模糊查询（检索 id 为输入的内容，或 spu 的名称中包含输入的内容）

Controller 层：

```java
@RequestMapping("/list")
public R list(@RequestParam Map<String, Object> params){
    PageUtils page = skuInfoService.queryPageByCondition(params);

    return R.ok().put("page", page);
}
```

Service 层：

这里需要注意的是，价格区间的默认值是 0 - 0，如果不对这个数据进行处理的话，那默认查询的就是价格为 0 的 sku 信息，这显然是不可用的，所以需要对传递过来的值进行非 0 的判断。

```java
@Override
public PageUtils queryPageByCondition(Map<String, Object> params) {
    QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
    String key = (String) params.get("key");
    if (!StringUtils.isEmpty(key)) {
        queryWrapper.and(wrapper -> {
            queryWrapper.eq("sku_id", key).or().like("sku_name", key);
        });
    }
    String catelogId = (String) params.get("catelogId");
    if (!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)) {
        queryWrapper.eq("catalog_id", catelogId);
    }
    String brandId = (String) params.get("brandId");
    if (!StringUtils.isEmpty(brandId) && !"0".equals(brandId)) {
        queryWrapper.eq("brand_id", brandId);
    }
    String min = (String) params.get("min");
    if (!StringUtils.isEmpty(min)) {
        BigDecimal bigDecimal = new BigDecimal(min);
        if (bigDecimal.compareTo(new BigDecimal("0")) > 0) {
            // 大于等于
            queryWrapper.ge("price", min);
        }
    }
    String max = (String) params.get("max");
    if (!StringUtils.isEmpty(max)) {
        BigDecimal bigDecimal = new BigDecimal(max);
        if (bigDecimal.compareTo(new BigDecimal("0")) > 0) {
            // 小于等于
            queryWrapper.le("price", max);
        }
    }
    IPage<SkuInfoEntity> page = this.page(
            new Query<SkuInfoEntity>().getPage(params),
            queryWrapper
    );
    return new PageUtils(page);
}
```

****
## 7. 仓储服务

### 7.1 获取仓库列表

因为逆向生成的代码中包含了简单的增删改查功能，所以关于仓库只需要修改检索功能即可，也较为简单。

Controller 层：

```java
@RequestMapping("/list")
public R list(@RequestParam Map<String, Object> params){
    PageUtils page = wareInfoService.queryPage(params);

    return R.ok().put("page", page);
}
```

Service 层：

因为仓库列表的检索包含了所有字段，所以检索条件之间用 or 连接即可。

```java
@Override
public PageUtils queryPage(Map<String, Object> params) {
    QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
    String key = (String) params.get("key");
    if (!StringUtils.isEmpty(key)) {
        queryWrapper
                .eq("id", key)
                .or().like("name", key)
                .or().like("address", key)
                .or().like("areacode", key);
    }
    IPage<WareInfoEntity> page = this.page(
            new Query<WareInfoEntity>().getPage(params),
            queryWrapper
    );
    return new PageUtils(page);
}
```

****
### 7.2 商品库存

#### 7.2.1 检索商品库存与采购需求

在商品库存页面，某个具体的商品（sku）是和仓库进行关联的，所以检索的条件为具体的仓库和 skuId。

Controller 层：

```java
@RequestMapping("/list")
// @RequiresPermissions("ware:waresku:list")
public R list(@RequestParam Map<String, Object> params){
    PageUtils page = wareSkuService.queryPage(params);

    return R.ok().put("page", page);
}
```

Service 层：

```java
@Override
public PageUtils queryPage(Map<String, Object> params) {
    QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
    String skuId = (String) params.get("skuId");
    if (!StringUtils.isEmpty(skuId) && !"0".equals(skuId)) {
        queryWrapper.eq("sku_id", skuId);
    }
    String wareId = (String) params.get("wareId");
    if (!StringUtils.isEmpty(wareId) && !"0".equals(wareId)) {
        queryWrapper.eq("ware_id", wareId);
    }
    IPage<WareSkuEntity> page = this.page(
            new Query<WareSkuEntity>().getPage(params),
            queryWrapper
    );

    return new PageUtils(page);
}
```

不过新增库存并不是在商品库存页面进行新增，这个页面只是一个可视化快捷页面。对于库存操作有个重要的业务叫做采购单，由采购人员将商品采购进对应的仓库。而再进行采购前需要设置一下采购需求，
也就是采购的商品对应的仓库与数量，在该页面需要新增一下检索功能。

Controller 层：

```java
@RequestMapping("/list")
public R list(@RequestParam Map<String, Object> params){
    PageUtils page = purchaseDetailService.queryPage(params);
    return R.ok().put("page", page);
}
```

Service 层：

```java
@Override
public PageUtils queryPage(Map<String, Object> params) {
    QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();
    String key = (String) params.get("key");
    if (!StringUtils.isEmpty(key)) {
        queryWrapper.and((wrapper) -> {
            wrapper.eq("purchase_id", key).or().eq("sku_id", key);
        });
    }
    String status = (String) params.get("status");
    if (!StringUtils.isEmpty(status)) {
        queryWrapper.eq("status", status);
    }
    String wareId = (String) params.get("wareId");
    if (!StringUtils.isEmpty(wareId) && !"0".equals(wareId)) {
        queryWrapper.eq("ware_id", wareId);
    }
    IPage<PurchaseDetailEntity> page = this.page(
            new Query<PurchaseDetailEntity>().getPage(params),
            queryWrapper
    );
    return new PageUtils(page);
}
```

****
#### 7.2.2 合并采购需求成采购单

##### 7.2.2.1 查询未领取的采购单

Controller 层：

```java
@RequestMapping("/unreceive/list")
// @RequiresPermissions("ware:purchase:list")
public R unreceiveList(@RequestParam Map<String, Object> params){
    PageUtils page = purchaseService.queryPageUnreceive(params);

    return R.ok().put("page", page);
}
```

Service 层：

因为采购单一旦完成，就无法再对其进行某些操作了，所以合并采购需求到采购单就得找那些处于新建或已分配状态的采购单。

```java
@Override
public PageUtils queryPageUnreceive(Map<String, Object> params) {
    IPage<PurchaseEntity> page = this.page(
            new Query<PurchaseEntity>().getPage(params),
            new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
    );

    return new PageUtils(page);
}
```

****
##### 7.2.2.2 合并

合并操作就是把采购需求中选中的一些数据分配给采购单中那些状态为新建或已分配采购人的数据，例如采购需求页面的数据为：

|id|采购单id|采购商品id|采购数量|采购金额|仓库id|状态|操作|
| ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- |
|1|5|1|10| |1|已分配|修改、删除|
|2|5|2|100| |2|已分配|修改、删除|

采购单页面的数据：

|  | 采购单id | 采购人id | 采购人名 | 联系方式       | 优先级 | 状态   | 仓库id | 总金额 | 创建日期 | 更新日期 | 操作        |
| ---- | ------ | ------ | ---- | ----------- | ---- | ---- | ---- | ---- |------|------|-----------|
|  | 1      | 2      | jack | 1357826987 1 | 1    | 已分配  |      |      |      |      | 分配、修改、删除  |
|  | 5      | 1      | admin | 1361234567 8 |      | 已分配  |      |      |      |      | 分配、修改、删除  | 

目的就是让这两页的数据关联起来。

Controller 层：

进行合并操作，前端会传递采购单 id 和 采购需求的 id 集合，所以可以封装成对象接收。

```java
@Data
public class MergeVo {
    // 采购单 id
    private Long purchaseId;
    // 采购需求 id 集合
    private List<Long> items;
}
```

```java
@PostMapping("/merge")
public R merge(@RequestBody MergeVo mergeVo){
    purchaseService.mergePurchase(mergeVo);
    return R.ok();
}
```

Service 层：

不过在合并的时候可以不选择某个具体的采购单，此时就属于新建一个采购单，所以在后端需要进行判断前端是否传递了采购单 id，如果没有则进行新建操作。不管最终是否为新增采购单，
都需要修改修改采购需求的信息，即让它们关联上采购单 id 并设置默认值为已分配采购单。所以最终修改的只有采购需求表，除非是新增一个采购单。

```java
@Transactional
@Override
public void mergePurchase(MergeVo mergeVo) {
  Long purchaseId = mergeVo.getPurchaseId();
  // 如果没有采购单 id，则需要新建采购单
  if (purchaseId == null) {
    PurchaseEntity purchaseEntity = new PurchaseEntity();
    purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
    this.save(purchaseEntity);
    // mybatis-plus 会自动把主键 id 返回给实体类上标注了 @TableId(type = IdType.AUTO) 的字段
    purchaseId = purchaseEntity.getId();
  }
  // 确认采购单状态，只有状态是新建或已分配才可以合并
  PurchaseEntity purchaseEntity = this.getById(purchaseId);
  if (purchaseEntity.getStatus().equals(WareConstant.PurchaseStatusEnum.CREATED.getCode())
          || purchaseEntity.getStatus().equals(WareConstant.PurchaseStatusEnum.ASSIGN.getCode())) {
    List<Long> itemIds = mergeVo.getItems();
    if (itemIds != null && !itemIds.isEmpty()) {
      Long finalPurchaseId = purchaseId;
      List<PurchaseDetailEntity> purchaseDetailEntities = itemIds.stream().map(itemId -> {
        PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
        purchaseDetailEntity.setId(itemId);
        purchaseDetailEntity.setPurchaseId(finalPurchaseId);
        purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGN.getCode());
        return purchaseDetailEntity;
      }).collect(Collectors.toList());
      purchaseDetailService.updateBatchById(purchaseDetailEntities);
    }
  }
}
```

****
#### 7.2.3 领取采购单

领取采购订单后才能进行商品的采购，所以前端会传递需要领取的采购单的 id 集合给后端，后端接收后主要是修改采购单的状态为已领取，采购需求的状态修改为正在采购。

Controller 层：

```java
@PostMapping("/received")
public R received(@RequestBody List<Long> purchaseIds){
    purchaseService.received(purchaseIds);
    return R.ok();
}
```

Service 层：

首先需要判断采购单是否为新建或者已分配的状态（已分配采购人员），然后才能修改它们的状态为已领取。接着再通过采购单的 id 获取到对应的采购需求表的信息，最后修改状态为正在领取即可。

```java
@Transactional
@Override
public void received(List<Long> purchaseIds) {
    // 1. 确认当前采购单是新建或者已分配状态
    List<PurchaseEntity> purchaseEntities = purchaseIds.stream().map(purchaseId -> {
      PurchaseEntity purchaseEntity = this.getById(purchaseId);
      return purchaseEntity;
    }).filter(purchaseEntity -> {
      return purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() || purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGN.getCode();
    }).map(purchaseEntity -> {
      Date now = new Date();
      purchaseEntity.setUpdateTime(now);
      purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
      return purchaseEntity;
    }).collect(Collectors.toList());
    if (purchaseEntities.isEmpty()) {
      return; // 没有可处理的采购单
    }
    // 2. 改变采购单的状态
    this.updateBatchById(purchaseEntities);
    // 3. 改变采购需求的状态
    purchaseEntities.forEach(purchaseEntity -> {
      List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.listDetailByPurchaseId(purchaseEntity.getId());
      purchaseDetailEntities.forEach(purchaseDetailEntity -> {
        purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
      });
      purchaseDetailService.updateBatchById(purchaseDetailEntities);
    });
}
```

****
#### 7.2.4 完成采购

完成采购操作由对应的采购人员进行操作，他需要传递采购单的 id、采购需求的 id 集合、采购需求的状态和失败原因，因为没有该功能对应的前端页面，所以用接口请求代替：

```json
{
    "purchaseId": 1,
    "items": [
        {
            "purchaseDetailId": 1,
            "status": 3,
            "reason": ""
        },
        {
            "purchaseDetailId": 2,
            "status": 4,
            "reason": "无货"
        }
    ]
}
```

这些参数用一个对象来封装接收，需要将它们的字段一一对应，否则需要用 @JsonProperty("...") 指定为前端发送的哪个字段。

```java
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long purchaseId;
    private List<PurchaseItemDoneVo> items;
}

@Data
public class PurchaseItemDoneVo {
    private Long purchaseDetailId;
    private Integer status;
    private String reason;
}
```

Controller 层：

```java
@PostMapping("/done")
public R done(@RequestBody PurchaseDoneVo purchaseDoneVo){
    purchaseService.done(purchaseDoneVo);
    return R.ok();
}
```

Service 层：

完成采购订单主要的功能就是修改采购单和采购需求的状态，通过前端传递的 status 可以很简单的判断出采购需求的状态（直接使用），而采购单的状态则需要通过采购需求的状态来进行判断，
只有当前采购单关联的所有采购需求的状态都为已完成时，才能将采购单的状态修改为已完成，否则就必须修改成异常。

```java
@Override
public void done(PurchaseDoneVo purchaseDoneVo) {
    Long purchaseId = purchaseDoneVo.getPurchaseId();
    // 改变采购需求状态
    Boolean flag = true;
    List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
    List<PurchaseDetailEntity> purchaseDetailEntities = new ArrayList<>();
    for (PurchaseItemDoneVo item : items) {
        PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
        // 采购失败
        if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.ERROR.getCode()) {
            // 只要有一条采购需求的状态为异常，那么就将该标识符置为 false
            flag = false;
            purchaseDetailEntity.setStatus(item.getStatus());
        } else { // 采购成功
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
            // 将成功采购的商品进行入库（加库存数量）
            PurchaseDetailEntity entity = purchaseDetailService.getById(item.getPurchaseDetailId());
            // 增加库存，需要传入商品 id，仓库 id，商品采购数目
            wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
        }
        purchaseDetailEntity.setId(item.getPurchaseDetailId());
        purchaseDetailEntities.add(purchaseDetailEntity);
    }
    purchaseDetailService.updateBatchById(purchaseDetailEntities);
    // 改变采购单状态
    PurchaseEntity purchaseEntity = new PurchaseEntity();
    purchaseEntity.setId(purchaseId);
    // 根据标识符判断应该将采购单的状态设置成什么
    purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.ERROR.getCode());
    this.updateById(purchaseEntity);
}
```

然后将采购成功的商品的数量添加商品库存 wms_ware_sku 表：

| id  | sku_id | ware_id | stock | sku_name        | stock_locked |
| --- | ------ | ------- | ----- | --------------- | ------------ |
| 1   | 29     | 1       | 40    | 华为 A2217黑色8 | 0            |
| 2   | 30     | 2       | 100   | 华为 A2217黑色1 | 0            |

如果是该表中存在对应的 skuId，那么就是修改操作，让 stock 字段增加，如果没有，则需要新建一条数据。而商品名称在库存服务中是无法查询到的，所以只能通过 product 服务远程调用，
然后查询到商品名。

```java
@Transactional
@Override
public void addStock(Long skuId, Long wareId, Integer skuNum) {
    // 如果没有这个库存记录，那就是新增
    List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
    // 为空，进行新增操作
    if (wareSkuEntities == null || wareSkuEntities.isEmpty()) {
        WareSkuEntity wareSkuEntity = new WareSkuEntity();
        wareSkuEntity.setSkuId(skuId);
        wareSkuEntity.setWareId(wareId);
        wareSkuEntity.setStock(skuNum);
        wareSkuEntity.setStockLocked(0);
        try {
            R info = productFeignService.info(skuId);
            if (info.getCode() == 0) {
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                wareSkuEntity.setSkuName(data.get("skuName").toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        wareSkuDao.insert(wareSkuEntity);
    } else {
        // 更新操作（加库存）
        wareSkuDao.addStock(skuId, wareId, skuNum);
    }
}
```

wareSkuDao.addStock()：

```sql
<update id="addStock">
    update wms_ware_sku set stock = stock + #{skuNum} where sku_id = #{skuId} and ware_id = #{wareId}
</update>
```

****
## 8. spu 管理商品规格

在商品维护的 spu 管理页面，每个 spu 都有一个规格按钮，它就是用来回显在添加商品时填写的那些规格参数基本属性，同样的，既然能回显就能进行修改操作。

Controller 层：

```java
@GetMapping("/base/listforspu/{spuId}")
public R baseAttrListForSpu(@PathVariable("spuId") Long spuId){
    List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseAttrListForSpu(spuId);
    return R.ok().put("data", productAttrValueEntities);
}
```

Service 层：

回显操作较为简单，因为有一张 pms_sku_sale_attr_value 表，里面记录了每个 spu 对应的规格参数数据，所以直接根据 spuId 查询即可。

```java
@Override
public List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId) {
    List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueDao.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
    return productAttrValueEntities;
}
```

Controller 层：

```java
@PostMapping("/update/{spuId}")
public R updateSpuAttr(@PathVariable("spuId") Long spuId, @RequestBody List<ProductAttrValueEntity> productAttrValueEntities) {
    productAttrValueService.updateSpuAttr(spuId, productAttrValueEntities);
    return R.ok();
}
```

Service 层：

修改操作的实现与以往记录的不太一样了，他不是接收某个表单的数据后封装成对象然后将该对象作为修改参数。现在是直接接收前端传递的所有有关该规格参数的信息，为什么这样？
因为规格参数对应的数据较多，如果封装从对象就要进行多次的更新操作，不如直接把原有数据清空，然后将新数据再添加进去，然后还是用 spuId 进行关联（spuId 不是主键）。

```java
@Transactional
@Override
public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> productAttrValueEntities) {
    // 1. 删除该 spuId 之前对应的所有属性
    productAttrValueDao.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
    // 2. 插入新的数据
    List<ProductAttrValueEntity> productAttrValueEntities1 = productAttrValueEntities.stream().map(productAttrValueEntity -> {
        productAttrValueEntity.setSpuId(spuId);
        return productAttrValueEntity;
    }).collect(Collectors.toList());
    this.saveBatch(productAttrValueEntities1);
}
```

****
# 五、商城业务

## 1. Elasticsearch 

### 1.1 安装

因为 7.x 版本的 Elasticsearch 没有 8.x 版本的安全验证，所以安装较为简单，无需设置密码也能访问。

```shell
docker run -d \
  --name es \
  -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
  -e "discovery.type=single-node" \
  -v ./es/data:/usr/share/elasticsearch/data \
  -v ./es/plugins:/usr/share/elasticsearch/plugins \
  -v ./es/config/es.yaml:/usr/share/elasticsearch/config/elasticsearch.yml \
  --privileged \
  --network gmall-net \
  -p 9200:9200 \
  -p 9300:9300 \
  elasticsearch:7.12.1
```

```shell
docker run -d \
--name kibana \
-e ELASTICSEARCH_HOSTS=http://es:9200 \
--network=gmall-net \
-p 5601:5601  \
kibana:7.12.1
```

****
### 1.2 初步检索

#### 1. _cat

1、集群健康：/_cat/health

```http request
GET /_cat/health?v
GET /_cat/health?format=json # 需要 JSON 时
```

```text
epoch      timestamp cluster       status node.total node.data shards pri relo init unassign pending_tasks max_task_wait_time active_shards_percent
175..      07:12:09  elasticsearch green           1         1      6   6    0    0        0             0                  -                100.0%
```

2、节点信息：/_cat/nodes，看各节点角色、负载、内存、磁盘等

```http request
GET /_cat/nodes?v&h=ip,nodeRole,name,cpu,load_1m,heap.percent,ram.percent,disk.avail
GET /_cat/nodes
```

```text
172.18.0.2 61 92 32 2.25 2.44 1.68 cdfhilmrstw * 782c524cb5ff
```

3、索引列表：/_cat/indices

```http request
GET /_cat/indices?v
GET /_cat/indices?bytes=gb&s=store.size:desc
GET /_cat/indices?h=health,status,index,pri,rep,docs.count,store.size&s=index
```

```text
health status index                           uuid                   pri rep docs.count docs.deleted store.size pri.store.size
green  open   .kibana_7.12.1_001              IFxNxKDsQr-9Yk6ENSL3NQ   1   0         29           27      2.1mb          2.1mb
green  open   .apm-custom-link                n2GHTEW9TBaETfR3M9_u6A   1   0          0            0       208b           208b
green  open   .apm-agent-configuration        7Cnh2GAhQZqf_uIJXF1Cuw   1   0          0            0       208b           208b
green  open   .kibana_task_manager_7.12.1_001 2tbWcVfOQuaY5kocHsfu8Q   1   0          9          184    136.8kb        136.8kb
green  open   .kibana-event-log-7.12.1-000001 OX1RHsDgSmWvpIpQTqDXcA   1   0          1            0      5.6kb          5.6kb
```

4、文档计数：/_cat/count

```http request
GET /_cat/count?v
GET /_cat/count/my-index
```

```text
epoch      timestamp count
1755501352 07:15:52  42
```

****
### 1.3 安装 ik 分词器

通过网络获取安装包后挂载到本地的 es 插件目录，不过需要把插件放到对应的目录，比如 ik 分词器就要放到 /plugins/ik（新建一个目录），在 es 容器启动时会自动加载。

```shell
wget https://get.infini.cloud/elasticsearch/analysis-ik/7.12.1/elasticsearch-analysis-ik-7.12.1.zip
unzip elasticsearch-analysis-ik-7.12.1.zip -d ./es/plugins/ik
```

验证：

```http request
POST /_analyze
{
  "analyzer": "ik_smart",
  "text": "程序员学习java太棒了"
}
```

```json
{
  "tokens" : [
    {
      "token" : "程序员",
      "start_offset" : 0,
      "end_offset" : 3,
      "type" : "CN_WORD",
      "position" : 0
    },
    {
      "token" : "学习",
      "start_offset" : 3,
      "end_offset" : 5,
      "type" : "CN_WORD",
      "position" : 1
    },
    {
      "token" : "java",
      "start_offset" : 5,
      "end_offset" : 9,
      "type" : "ENGLISH",
      "position" : 2
    },
    {
      "token" : "太棒了",
      "start_offset" : 9,
      "end_offset" : 12,
      "type" : "CN_WORD",
      "position" : 3
    }
  ]
}
```

****
### 1.4 Elasticsearch-Rest-Client

#### 1.4.1 简单使用

因为 7.x 和 8.x 版本差距较大，而之前记录的是 8.x 版本的 SpringBoot 整合 es，所以这里简单记录下 7.x 版本。

1、引入依赖

```xml
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
    <version>7.12.1</version>
</dependency>
```

需要注意的是：SpringBoot 会自动对 elasticsearch 的依赖进行管理，如果要使用自己的版本的话就需要强制使用：

```xml
<properties>
    <elasticsearch.version>7.12.1</elasticsearch.version>
</properties>
```

2、创建配置文件

将 Elasticsearch 的客户端 RestHighLevelClient 注入到 Spring 容器中，指定 ES 的主机地址和端口（localhost:9200），并通过 builder 初始化客户端。

```java
@Configuration
public class GulimallElasticSearchConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
        return new RestHighLevelClient(builder);
    }
}
```

3、使用

```java
@Autowired
private RestHighLevelClient restHighLevelClient;

@Test
void contextLoads() {
    log.info("restHighLevelClient: {}", restHighLevelClient);
}

@Test
void indexData() throws IOException {
    IndexRequest indexRequest = new IndexRequest("users");
    indexRequest.id("1");
    // indexRequest.source("userName", "张三", "age", 18, "gender", "男");
    User user = new User();
    user.setUserName("张三");
    user.setAge(18);
    user.setGender("男");
    String jsonString = JSON.toJSONString(user);
    // java.lang.IllegalArgumentException: The number of object passed must be even but was [1]
    indexRequest.source(jsonString, XContentType.JSON);
    // 执行操作
    IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    // 提取有用的响应数据
    System.out.println(index);
}

@Data
class User {
    private String userName;
    private String gender;
    private Integer age;
}
```

IndexRequest.source() 有几种重载方式：

```java
// 方式1：以 key-value 形式传入
indexRequest.source("userName", "张三", "age", 18, "gender", "男");

// 方式2：以 Map 形式传入
Map<String, Object> map = new HashMap<>();
map.put("userName", "张三");
map.put("age", 18);
map.put("gender", "男");
indexRequest.source(map);

// 方式3：以 JSON 字符串传入
indexRequest.source(jsonString, XContentType.JSON);
```

但如果用 key-value 方式，传入的参数必须成对出现（也就是 key-value 的形式），否则会报错：

```java
java.lang.IllegalArgumentException: The number of object passed must be even but was [1]
```

而使用 JSON 字符串的方式需要指定为 XContentType.JSON，否则也会报上面同样的错误。

****
#### 1.4.2 复杂检索

首先需要创建检索请求指定检索的索引，这里指定的是 bank 索引库，然后简单的指定一个查询条件，查询 address 字段包含 mill 的数据。然后创建一个分组聚合，名字为 ageAgg，
ES 会根据 age 字段的值去做分桶（bucket），每个不同的年龄值生成一个桶，最终只取前十个，然后把它作为查询条件进行聚合查询。而查询平均薪资也是利用聚合，
通过 AggregationBuilders.avg("balanceAvg") 创建一个平均值聚合。

而结果分析则需要连续调用两次 hits，因为 es 展示的结构就是一个大 hits 包裹一个小 hits，然后小 hits 里面包裹多个 _resource。
通过 searchResponse.getAggregations() 可以从返回结果中获取本次查询的所有聚合结果，聚合结果是一个 Aggregation 集合，每个 Aggregation 对象对应 DSL 里定义的聚合。
因为年龄这个字段可以有多条不同的值，所以每个 bucket 对应一个 age 值及其文档数量，而平均薪资则可以直接获取值。

```java
@Autowired
private RestHighLevelClient restHighLevelClient;

@Test
void searchData() throws IOException {
    // 1. 创建检索请求
    SearchRequest searchRequest = new SearchRequest();
    // 指定索引
    searchRequest.indices("bank");
    // 指定 DSL 检索条件
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
    // 按照年龄的值分布进行聚合
    TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
    searchSourceBuilder.aggregation(ageAgg);
    searchRequest.source(searchSourceBuilder);
    // 计算平均薪资
    AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
    searchSourceBuilder.aggregation(balanceAvg);
    System.out.println("检索条件：" + searchSourceBuilder.toString());
    // 2. 执行检索
    SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

    // 3. 分析结果
    // 获取所有查到的数据
    SearchHits hits = searchResponse.getHits();
    SearchHit[] searchHits = hits.getHits();
    for (SearchHit hit : searchHits) {
        String source = hit.getSourceAsString();
        JsonRootBean account = JSON.parseObject(source, JsonRootBean.class);
        System.out.println("account：" + account);
    }
    // 获取这次检索到的分析信息
    Aggregations aggregations = searchResponse.getAggregations();
    for (Aggregation aggregation : aggregations) {
        System.out.println("当前聚合：" + aggregation.getName());
    }
    Terms ageAgg1 = aggregations.get("ageAgg");
    ageAgg1.getBuckets().forEach(bucket -> {
        String keyAsString = bucket.getKeyAsString();
        System.out.println("年龄：" + keyAsString + "，数量：" + bucket.getDocCount());
    });
    Avg balanceAvg1 = aggregations.get("balanceAvg");
    System.out.println("平均薪资：" + balanceAvg1.getValue());
}
```

```java
@Data
@ToString
static class JsonRootBean {
    private int account_number;
    private int balance;
    private String firstname;
    private String lastname;
    private int age;
    private String gender;
    private String address;
    private String employer;
    private String email;
    private String city;
    private String state;
}
```

****
## 2. 商品上架

### 2.1 构建 es 模型

一般的商品就是按照如下的属性检索，这种写法是直接把需要检索的字段写进去以此达到方便检索的目的，但是这会产生一些冗余字段。例如 spuId 为 11 的商品下有对应的 sku 8 个，
那这种写法就需要写八次 spu 的 attr 属性，那就很浪费空间。

```json
{
  "skuId": 1,
  "spuId": 11,
  "skuTitle": 华为,
  "price": 998,
  "saleCount": 99,
  "attrs": [
    {
      "尺寸": 5寸
    },
    {
      "CPU": 高通
    },
    {
      "分辨率": 全高清
    }
  ]
}
```

如果将他们分开存储，一个存储 sku 信息，一个存储 spu 信息，当需要用到 spu 的 attr 属性时就可以通过 spuId 找到 attr 的索引库，这样一个 spu 就只需要写一次 attr，占用更少的空间。

```json
{
  "skuId": 1,
  "spuId": 11,
  "xxxx": ...,
}
```

```json
{
  "spuId": 11,
  "attrs": [
    {
      "尺寸": 5寸
    },
    {
      "CPU": 高通
    },
    {
      "分辨率": 全高清
    }
  ]
}
```

但这种写法存在一种问题，那就是在前端页面展示的时候，每点击一个查询属性，其它的属性就会动态的发生改变，例如搜索小米，此时可能有如下分类：

- 粮食
- 手机
- 电器

此时有很多的 sku 都包含小米这个 spu，如果要检索出这些 sku 涉及的所有属性，那就需要发送多次查询。先查询出小米这个 spu 包含的所有可能属性，此时要通过 es 发送大量的查询请求，
然后再查询 attr 属性，当查询的请求数量较大，那就会占用大量的网络资源，甚至导致系统卡死崩溃。所以 es 中的数据模型还是选择第一种，用空间来换取时间的稳定性。

整体模型：

```json
PUT product
{
    "mappings": {
        "properties": {
            "skuId": {
                "type": "long"
            },
            "spuId": {
                "type": "keyword"
            },
            "skuTitle": {
                "type": "text",
                "analyzer": "ik_smart"
            },
            "skuPrice": {
                "type": "keyword"
            },
            "skuImg": {
                "type": "keyword",
                "index": false,
                "doc_values": false
            },
            "saleCount": {
                "type": "long"
            },
            "hasStock": {
                "type": "boolean"
            },
            "hotScore": {
                "type": "long"
            },
            "brandId": {
                "type": "long"
            },
            "catalogId": {
                "type": "long"
            },
            "brandName": {
                "type": "keyword",
                "index": false,
                "doc_values": false
            },
            "brandImg": {
                "type": "keyword",
                "index": false,
                "doc_values": false
            },
            "catalogName": {
                "type": "keyword",
                "index": false,
                "doc_values": false
            },
            "attrs": {
                "type": "nested",
                "properties": {
                    "attrId": {
                        "type": "long"
                    },
                    "attrName": {
                        "type": "keyword",
                        "index": false,
                        "doc_values": false
                    },
                    "attrValue": {
                        "type": "keyword"
                    }
                }
            }
        }
    }
}
```

在这个 es 索引库中，只有 skuTitle 是支持分词检索的，也就是模糊查询，其余字段只支持精确匹配，而有些字段用到了：

- index：默认 true，如果为 false，表示该字段不会被索引，但是检索结果里面有，但字段本身不能
- doc_values：默认 true，设置为 false，表示不可以做排序、聚合以及脚本操作，这样更节省磁盘空间。还可以通过设定 doc_values 为 true，index 为 false 来让字段不能被搜索但可以用于排序、聚合以及脚本操作

****
### 2.2 es 的扁平化处理

在 es 中，普通的字段的默认行为就是扁平化，即不区分标量还是数组，把数组看作是是多值字段。当索引对象数组时，es 会把同名字段的值打平到各自的字段上，
而这会导致丢失同一条对象里字段之间的配对关系。例如：

```json
{
  "name": "shoe",
  "attrs": [
    {"color": "red",  "size": "M"},
    {"color": "blue", "size": "L"}
  ]
}
```

如果 attrs 映射为默认，索引后可理解为：

```json
attrs.color: ["red",  "blue"]
attrs.size:  ["M",    "L"]
```

这时查询 color=red AND size=L 会误命中，因为 es 看见的是有文档包含 attrs.color = red，也包含 attrs.size = L，但它不知道这两个值来自不同的对象条目。
这就导致只要 color 中存在查询的值即判定为找到，size 字段同理。而这种情况是需要避免的，所以 es 提供了对象之间的内联关系，把 attrs 定义为 nested，
es 会把每个对象条目当成一个隐藏子文档存储，配对关系得到保留。而查询时也必须使用 nested 查询：

```json
{
  "nested": {
    "path": "attrs",
    "query": {
      "bool": {
        "must": [
          { "term": { "attrs.color": "red" }},
          { "term": { "attrs.size": "L" }}
        ]
      }
    }
  }
}
```

****
### 2.3 spu 商品上架功能

#### 2.3.1 整体代码

在上面已经完成了 es 模型的设计，现在就要通过访问后端接口来返回这个模型的具体数据。而在前端点击商品上架后，就需要把该商品的数据封装为上面的 es 设计模型。

Controller 层：

```java
@PostMapping("/{spuId}/up")
public R spuUp(@PathVariable("spuId") Long spuId) {
    spuInfoService.up(spuId);
    return R.ok();
}
```

Service 层：

```java
@Override
public void up(Long spuId) {
  // 查出当前 spuId 对应的所有 sku 信息，包括 skuName
  List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
  // 获取传入的 spuId 对应的 sku 的 id 集合
  List<Long> skuIds = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

  // 查询当前 sku 的所有可以被用来检索的规格属性
  List<ProductAttrValueEntity> productAttrs = productAttrValueService.baseAttrListForSpu(spuId);
  // 通过 pms_product_attr_value 表获取该 spu 的 attrId
  List<Long> attrIds = productAttrs.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
  // 通过 attrId 获取可被检索的 attrId，即查询条件包含 search_type = 1
  List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
  Set<Long> idSet = new HashSet<>(searchAttrIds);
  // 将可被检索的 attr 与该 spuId 对应的所有 attr 进行对比，相同的就直接拷贝 attr 数据
  List<SkuEsModel.Attr> esAttrs = productAttrs.stream().filter(productAttrValueEntity -> {
    return idSet.contains(productAttrValueEntity.getAttrId());
  }).map(productAttrValueEntity -> {
    SkuEsModel.Attr attr = new SkuEsModel.Attr();
    BeanUtils.copyProperties(productAttrValueEntity, attr);
    return attr;
  }).collect(Collectors.toList());

  Map<Long, Boolean> hasStockMap = null;
  try {
    // 远程调用库存系统查询是否有库存
    R<List<SkuHasStockVo>> skusHaveStock = wareFeignService.getSkusHaveStock(skuIds);
    List<SkuHasStockVo> skuHasStockVos = skusHaveStock.getData();
    hasStockMap = skuHasStockVos.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
  } catch (Exception e) {
    log.error("远程调用库存服务查询是否有库存出现异常:{}", e.getMessage());
  }

  // 封装每个 sku 的信息
  Map<Long, Boolean> finalHasStockMap = hasStockMap;
  List<SkuEsModel> skuEsModels = skuInfoEntities.stream().map(sku -> {
    // 组装需要的数据
    SkuEsModel skuEsModel = new SkuEsModel();
    BeanUtils.copyProperties(sku, skuEsModel);
    // skuPrice、skuImg、hasStock、hotScore、brandName、brandImg、catelogName、attrs[]
    skuEsModel.setSkuPrice(sku.getPrice());
    skuEsModel.setSkuImg(sku.getSkuDefaultImg());

    // TODO 热度评分，刚上架默认为 0
    skuEsModel.setHotScore(0L);

    // 查询品牌名称
    BrandEntity brand = brandService.getById(skuEsModel.getBrandId());
    CategoryEntity category = categoryService.getById(skuEsModel.getCatalogId());
    skuEsModel.setBrandName(brand.getName());
    skuEsModel.setBrandImg(brand.getLogo());
    skuEsModel.setCatalogName(category.getName());

    if (finalHasStockMap == null) {
      // 设置库存
      skuEsModel.setHasStock(true);
    } else {
      skuEsModel.setHasStock(finalHasStockMap.get(sku.getSkuId()));
    }

    // 设置 es 里的检索属性 attr，因为是同一个 spu，所以每次设置的 attr 都是一样的
    skuEsModel.setAttrs(esAttrs);
    return skuEsModel;
  }).collect(Collectors.toList());
}
```

```java
/**
 * 在 attr 表中跳出可以被检索的属性
 * @param attrIds
 * @return
 */
@Override
public List<Long> selectSearchAttrIds(List<Long> attrIds) {
    // select attr_id from pms_attr where attr_id in (?) and search_type = 1
    return attrDao.selectSearchAttrIds(attrIds);
}
```

远程调用查询库存：

```java
@GetMapping("/haveStock")
public R<List<SkuHasStockVo>> getSkusHaveStock(@RequestBody List<Long> skuIds){
    List<SkuHasStockVo> vos = wareSkuService.getSkusHaveStock(skuIds);
    R<List<SkuHasStockVo>> ok = R.ok();
    ok.setData(vos);
    return ok;
}
```

```java
@Override
public List<SkuHasStockVo> getSkusHaveStock(List<Long> skuIds) {
    List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map(skuId -> {
        SkuHasStockVo vo = new SkuHasStockVo();
        // 查询当前 sku 的总库存量
        // select sum(stock - stock_locked) from wms_ware_sku where sku_id = ?
        long stock = wareSkuDao.getSkuStock(skuId);
        vo.setSkuId(skuId);
        vo.setHasStock(stock > 0);
        return vo;
    }).collect(Collectors.toList());
    return skuHasStockVos;
}
```

****
#### 2.3.2 步骤

商品上架功能是在 spu 管理页面中的，而上架的是一个 spu，所以要先通过前端传递的 spuId 查询出对应的 sku 以及 skuName。而查询出的 sku 又要封装成 es 模型数据，
所以先设计一个传递对象，专门接收 es 模型数据：

```java
@Data
public class SkuEsModel {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotScore;
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    private List<Attr> attrs;
    @Data
    public static class Attr {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
```

和具体的 SkuInfoEntity 对比后发现，有些数据不能直接通过 BeanUtils 拷贝给 SkuEsModel，而这些数据则需要手动赋值：

- skuPrice
- skuImg
- hasStock
- hotScore
- brandName
- brandImg
- catalogName
- attrs[]

而一个 spu 对应多个 sku，所以需要把它们封装成集合的形式返回，集合的类型就是 SkuEsModel。这里就是先通过 spu 查询出对应的 sku 的 id。

```java
// 查出当前 spuId 对应的所有 sku 信息，包括 skuName
List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
// 获取传入的 spuId 对应的 sku 的 id 集合
List<Long> skuIds = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
```

然后查询 pms_product_attr_value 表中关于当前 spuId 的 attr 的一些信息和 attr 的 id 集合。接着查询 pms_attr 表中那些 search_type = 1 的 attr，这样获取到这些可被检索的 attr 的 id 后，
就和刚刚查出的 pms_product_attr_value 表中的 attr 的 id 对比，然后把 id 一致的拷贝，为什么这样做？因为 es 的 attr 中的字段和 ProductAttrValueEntity 的字段一致，
可以直接拷贝，所以这样比较。而查询的是同一个 spu 下的 attr，所以这个 attr 属性是多个 sku 共用的，但因为 es 的设计模型，还是得对每个 SkuEsModel 的 attrs 字段赋值。

```java
// 查询当前 sku 的所有可以被用来检索的规格属性
List<ProductAttrValueEntity> productAttrs = productAttrValueService.baseAttrListForSpu(spuId);
// 通过 pms_product_attr_value 表获取该 spu 的 attrId
List<Long> attrIds = productAttrs.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
// 通过 attrId 获取可被检索的 attrId，即查询条件包含 search_type = 1
List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
Set<Long> idSet = new HashSet<>(searchAttrIds);
// 将可被检索的 attr 与该 spuId 对应的所有 attr 进行对比，相同的就直接拷贝 attr 数据
List<SkuEsModel.Attr> esAttrs = productAttrs.stream().filter(productAttrValueEntity -> {
    return idSet.contains(productAttrValueEntity.getAttrId());
}).map(productAttrValueEntity -> {
    SkuEsModel.Attr attr = new SkuEsModel.Attr();
    BeanUtils.copyProperties(productAttrValueEntity, attr);
    return attr;
}).collect(Collectors.toList());
```

在 SkuEsModel 中有个库存的字段 hasStock，这个字段并不是展示还剩多少库存，而是展示是否还有库存，所以它的类型是 Boolean，有则为 true，反之则为 false。而库存功能需要调用别的服务，
所以又要用到 Feign 接口。

```java
@FeignClient("gulimall-ware")
public interface WareFeignService {
    @GetMapping("/ware/waresku/haveStock")
    R<List<SkuHasStockVo>> getSkusHaveStock(@RequestBody List<Long> skuIds);
}
```

最终的返回结果只需要告知商品服务是否还有库存，所以可以简单的设计一个 Vo 对象封装结果，里面只需要包含 sku 的 id 和是否有库存即可。

```java
@GetMapping("/haveStock")
public R<List<SkuHasStockVo>> getSkusHaveStock(@RequestBody List<Long> skuIds){
    List<SkuHasStockVo> vos = wareSkuService.getSkusHaveStock(skuIds);
    R<List<SkuHasStockVo>> ok = R.ok();
    ok.setData(vos);
    return ok;
}

@Override
public List<SkuHasStockVo> getSkusHaveStock(List<Long> skuIds) {
  List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map(skuId -> {
    SkuHasStockVo vo = new SkuHasStockVo();
    // 查询当前 sku 的总库存量
    // select sum(stock - stock_locked) from wms_ware_sku where sku_id = ?
    Long stock = wareSkuDao.getSkuStock(skuId);
    vo.setSkuId(skuId);
    vo.setHasStock(stock > 0);
    return vo;
  }).collect(Collectors.toList());
  return skuHasStockVos;
}
```

因为是远程调用，那就涉及网络请求，那就要用 try-catch 包起来，防止后续程序因为远程调用的异常而崩溃。这里调用的接口返回的是 R<List<SkuHasStockVo>> 类型，
为了后面方便比较，就把它转换成 Map 类型，SkuHasStockVo 的 id 作为 key，hasStock 作为 value。在后面赋值的时候判断 Map 集合中的 key 来获取对应的 value（是否有库存）。

```java
Map<Long, Boolean> hasStockMap = null;
try {
    // 远程调用库存系统查询是否有库存
    R<List<SkuHasStockVo>> skusHaveStock = wareFeignService.getSkusHaveStock(skuIds);
    List<SkuHasStockVo> skuHasStockVos = skusHaveStock.getData();
    hasStockMap = skuHasStockVos.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
} catch (Exception e) {
    log.error("远程调用库存服务查询是否有库存出现异常:{}", e.getMessage());
}
```

前置条件都完成了，现在就需要将它们赋值给 SkuEsModel。像 skuPrice、skuImg 只是名称不一样，可以直接通过前面获取到的 skuInfoEntities 中取出对应的数据然后赋值。
而 brandName、brandImg、catalogName 则需要查询对应的表来获取。

```java
// 封装每个 sku 的信息
Map<Long, Boolean> finalHasStockMap = hasStockMap;
List<SkuEsModel> skuEsModels = skuInfoEntities.stream().map(sku -> {
    // 组装需要的数据
    SkuEsModel skuEsModel = new SkuEsModel();
    BeanUtils.copyProperties(sku, skuEsModel);
    // skuPrice、skuImg、hasStock、hotScore、brandName、brandImg、catalogName、attrs[]
    skuEsModel.setSkuPrice(sku.getPrice());
    skuEsModel.setSkuImg(sku.getSkuDefaultImg());

    // TODO 热度评分，刚上架默认为 0
    skuEsModel.setHotScore(0L);

    // 查询品牌名称
    BrandEntity brand = brandService.getById(skuEsModel.getBrandId());
    CategoryEntity category = categoryService.getById(skuEsModel.getCatalogId());
    skuEsModel.setBrandName(brand.getName());
    skuEsModel.setBrandImg(brand.getLogo());
    skuEsModel.setCatalogName(category.getName());

    if (finalHasStockMap == null) {
        // 设置库存
        skuEsModel.setHasStock(true);
    } else {
        // 根据 skuId 获取 Map 集合中的 value
        skuEsModel.setHasStock(finalHasStockMap.get(sku.getSkuId()));
    }

    // 设置 es 里的检索属性 attr，因为是同一个 spu，所以每次设置的 attr 都是一样的
    skuEsModel.setAttrs(esAttrs);
    return skuEsModel;
}).collect(Collectors.toList());
```

****
### 2.6 R<T> 泛型类的问题

在上面远程调用库存服务查询是否还有库存时对 R 类进行了添加泛型的操作：

```java
public class R<T> extends HashMap<String, Object> {
    private T data;
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    ...
}
```

R 继承了 HashMap<String, Object>，它本身就是一个 Map，现在又让它再额外挂了一个 data 字段，可是在构造返回结果的时候，并没有把 data 字段放到 HashMap 里，
只是单独调用了 ok.setData(vos)。

```java
@GetMapping("/haveStock")
public R<List<SkuHasStockVo>> getSkusHaveStock(@RequestBody List<Long> skuIds){
    List<SkuHasStockVo> vos = wareSkuService.getSkusHaveStock(skuIds);
    R<List<SkuHasStockVo>> ok = R.ok();
    ok.setData(vos);
    return ok;
}
```

在远程调用时，Feign 会把返回的 JSON 反序列化成 R 对象。比如返回 JSON 可能是这样的：

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    { "skuId": 1, "hasStock": true },
    { "skuId": 2, "hasStock": false }
  ]
}
```

但是此时 R 类本质是一个 HashMap，当 Controller 返回 R 时，Spring/Jackson 会把它当成一个 Map 来序列化（因为继承了 HashMap），此时只会把 Map 里的键值对写进 JSON；
对于单独的 data 字段，默认是不会写进去的。到了 Feign 客户端反序列化时，Jackson 也会把响应当成 Map 来解析（因为类型实现了 Map）。如果响应里真的有 "data" 字段，
它会被放进这个 Map 的键 "data" 下； 但它不会去调用 setData 给那个私有字段赋值。所以拿到的 r.get("data") 是一个 List<LinkedHashMap>，而不是 List<SkuHasStockVo>。

```java
public class R extends HashMap<String, Object> {

    public <T> T getData(TypeReference<T> typeReference) {
        Object data = get("data"); // data 默认为 Map 类型
        String jsonString = JSON.toJSONString(data); // 先转 JSON
        T t = JSON.parseObject(jsonString, typeReference); // 再反序列化为指定类型
        return t;
    }

    public R setData(Object data) {
        this.put("data", data);
        return this;
    }
    ...
}
```

上面有说到 Feign/Jackson 反序列化时只会把 JSON 映射到 Map 里，所以可以在它反序列化后先重新转换成 JSON，再用 JSON.parseObject 和 TypeReference<T> 指定目标类型反序列化。
而 Java 的泛型有类型擦除机制，如果使用普同的泛型进行反序列化，那在运行时就会发生擦除，导致无法被序列化成想要的类型，所以要用到 TypeReference<T>，它可以保留泛型参数信息，
让 FastJSON 正确反序列化。

这种二次序列化的做法可以让第一次序列化时获得的 List<LinkedHashMap> 转成纯 JSON 字符串，然后明确告诉 FastJSON 需要返回一个指定类型的 List<...>，
此时就能用反射把 JSON 解析成真正的 SkuHasStockVo 对象。所以获取是否还有库存的代码应该修改成：

```java
Map<Long, Boolean> hasStockMap = null;
try {
    // 远程调用库存系统查询是否有库存
    R r = wareFeignService.getSkusHaveStock(skuIds);
    // 受保护的对象，要写成内部类的形式
    TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<>() {
    };
    hasStockMap = r.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
} catch (Exception e) {
    log.error("远程调用库存服务查询是否有库存出现异常:{}", e.getMessage());
}
```

### 2.5 将数据发送给 es 进行保存

product 服务 Service 层：

远程调用 search 服务上传数据到 es，然后根据返回码结果修改 spu 的状态。

```java
@Override
public void up(Long spuId) {
    ...
    R r = searchFeignService.productStatusUp(skuEsModels);
    if (r.getCode() == 0) {
        // 成功，修改当前 spu 的状态为上架
        spuInfoDao.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
    } else {
        // 失败
    }
}
```

search 服务 Controller 层：

这里接收的不是来自前端的数据，而是 product 服务远程发送的数据，当接收到 service 层返回的结果为 true 时证明保存数据到 es 客户端失败，需要响应错误信息给前端，
为 false 即没有发生错误。

```java
@PostMapping("/product")
public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
    boolean b = false;
    try {
        b = productSaveService.productStatusUp(skuEsModels);
    } catch (IOException e) {
        log.error("ElasticSaveController 商品上架功能异常:{}", e.getMessage());
        return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
    }
    if (!b) {
        return R.ok();
    } else {
        return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
    }
}
```

Service 层：

因为接收到的 sku 可能会有多条，所以需要用到批量操作，而批量操作的本质还是调用多条操作指令，所以这里还是要遍历每个 es 模型数据，然后给每条数据赋值 id 为 skuId，
存储的对象转换为 JSON 后发送给 es 客户端。在返回对象 BulkResponse 里有个方法可以查看操作是否成功（bulk.hasFailures()），当它返回 true 的时候就是发生错误，
此时就需要记录日志，而用一个 boolean 值接收并返回，在 Controller 层进行判断返回给前端具体的信息。

```java
@Override
public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
    // 保存到 es
    BulkRequest bulkRequest = new BulkRequest();
    // 构造批量保存的请求
    for (SkuEsModel skuEsModel : skuEsModels) {
        IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
        indexRequest.id(skuEsModel.getSkuId().toString());
        String skuEsModelJson = JSON.toJSONString(skuEsModel);
        indexRequest.source(skuEsModelJson, XContentType.JSON);
        bulkRequest.add(indexRequest);
    }

    BulkResponse bulk  = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    boolean b = false;
    if (bulk != null) {
        // 如果发生错误，b 就变为 true
        b = bulk.hasFailures();
        List<String> bulkItemIds = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
        log.info("商品上架 es 完成：{}", bulkItemIds);
    }
    return b;
}
```

****
## 3. 首页渲染三级分类

### 3.1 查询一级分类

Controller 层：

这里是发送 / 和 /index.html 请求路径时跳转到本服务的 classpath:/templates/index.html 页面。通过 Service 获取到所有的一级分类，然后通过 Model 视图存入域中。
这里在 product 服务中引入了商城页面，所以在 Controller 层返回数据时直接进行请求跳转即可，数据则存入请求域中。

```java
@GetMapping({"/", "/index.html"})
public String indexPage(Model model) {
    // 查出所有的一级分类
    List<CategoryEntity> categoryEntities = categoryService.getLevel1Categories();
    model.addAttribute("categoryEntities", categoryEntities);
    // 默认前缀 classpath:/templates/
    // 默认后缀 .html
    return "index";
}
```

Service 层：

```java
@Override
public List<CategoryEntity> getLevel1Categories() {
    return categoryDao.selectList(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, 0));
}
```

在 html 页面则通过域来获取数据。

```html
<div class="header_main_left">
  <ul>
    <li th:each="category:${categoryEntities}">
      <a href="#" class="header_main_left_a" th:attr="ctg-data=${category.catId}"><b th:text="${category.name}"></b></a>
    </li>
  </ul>
</div>
```

****
### 3.2 查询二、三级分类

在前端页面用于展示三级分类的具体结构如下，它里面有四个字段，一个用于记录一级分类的 id，一个记录三级分类的结构，一个记录当前分类的 id，一个记录当前分类的名字。从这个结构可以看出，
它是二级分类的结构，所以在处理这些数据的时候，就可以参考它创建一个二级分类的对象，接收传递数据。

```json
"1": [
  {
    "catalog1Id": "1",
    "catalog3List": [
      {
      "catalog2Id": "22",
      "id": "165",
      "name": "电子书"
      },
    ],
    "id": "22",
    "name": "电子书刊"    
  }
]
```

创建一个 Vo 对象用于接收管理前端需要的数据结构，这里给每个字段的名称需要和前端获取的一致：

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog2Vo { // 2 级分类 vo
    private String catalog1Id; // 1 级父分类 id
    private List<Catalog3Vo> catalog3List; // 3 级子分类
    private String id;
    private String name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog3Vo { // 3 级分类 vo
        private String catalog2Id; // 2 级父分类 id
        private String id;
        private String name;
    }
}
```

Controller 层：

这里选择返回一个 Map 集合，用一级分类的 id 作为 key，二级分类的结构作为 value，这种结构整合符合前端的需要。

```java
@ResponseBody
@GetMapping("index/catalog.json")
public Map<String, List<Catalog2Vo>> getCatalogJson() {
    Map<String, List<Catalog2Vo>> catalogJson = categoryService.getCatalogJson();
    return catalogJson;
}
```

Service 层：

```java
@Override
public Map<String, List<Catalog2Vo>> getCatalogJson() {
    // 1. 查出所有一级分类
    List<CategoryEntity> level1Categories = getLevel1Categories();
    // 2. 封装数据
    Map<String, List<Catalog2Vo>> collect = level1Categories.stream().collect(Collectors.toMap(level1Category -> {
        return level1Category.getCatId().toString();
    }, level1Category -> {
        // 查询此一级分类的所有二级分类
        List<CategoryEntity> category2Entities = categoryDao.selectList(
                // select * from category where parent_cid = 一级分类的cat_id
                new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, level1Category.getCatId())
        );
        List<Catalog2Vo> catelog2Vos = null;
        if (!category2Entities.isEmpty()) {
            catelog2Vos = category2Entities.stream().map(category2Entity -> {
                Catalog2Vo catelog2Vo = new Catalog2Vo(
                        level1Category.getCatId().toString(),
                        null,
                        category2Entity.getCatId().toString(),
                        category2Entity.getName()
                );
                // 找三级分类
                List<CategoryEntity> category3Entities = categoryDao.selectList(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, category2Entity.getCatId()));
                if (!category3Entities.isEmpty()) {
                    List<Catalog2Vo.Catalog3Vo> catelog3Vos = category3Entities.stream().map(category3Entity -> {
                        Catalog2Vo.Catalog3Vo catelog3Vo = new Catalog2Vo.Catalog3Vo(
                                category2Entity.getCatId().toString(),
                                category3Entity.getCatId().toString(),
                                category3Entity.getName()
                        );
                        return catelog3Vo;
                    }).collect(Collectors.toList());
                    catelog2Vo.setCatalog3List(catelog3Vos);
                }
                return catelog2Vo;
            }).collect(Collectors.toList());
        }
        if (catelog2Vos != null && !catelog2Vos.isEmpty()) {
            return catelog2Vos;
        } else {
            return Collections.emptyList();
        }
    }));
    return collect;
}
```

因为是要封装成一个 Map 集合的形式，但实际查出的一级分类的数据是一个 List，所以需要对它进行转型，全部转换成 Map，然后封装它的 key 和 value：

```java
Map<String, List<Catalog2Vo>> collect = level1Categories.stream().collect(Collectors.toMap(level1Category -> {
    return level1Category.getCatId().toString();
}, level1Category -> {
    ...
}));
```

这里就是先获取一级分类的 id 作为 key，不过在执行转换前就已经获取了所有的一级分类，所以这里直接通过查出的对象集合挨个获取 id 即可。复杂的是封装二级分类结构。

```java
level1Category -> {
    // 查询此一级分类的所有二级分类
    List<CategoryEntity> category2Entities = categoryDao.selectList(
            // select * from category where parent_cid = 一级分类的cat_id
            new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, level1Category.getCatId())
    );
    List<Catalog2Vo> catelog2Vos = null;
    if (!category2Entities.isEmpty()) {
        catelog2Vos = category2Entities.stream().map(category2Entity -> {
            Catalog2Vo catelog2Vo = new Catalog2Vo(
                    level1Category.getCatId().toString(),
                    null,
                    category2Entity.getCatId().toString(),
                    category2Entity.getName()
            );
            ...
        }).collect(Collectors.toList());
    }
    if (catelog2Vos != null && !catelog2Vos.isEmpty()) {
        return catelog2Vos;
    } else {
        return Collections.emptyList();
    }
}
```

因为是对查出的一级分类的集合进行转换，所以通过 Stream 获取到的流中的每个对象都是一个一级分类的 CategoryEntity 对象，如果想要获取二级分类 id，就需要查找那些 catId 等于一级分类 id 的数据，
也就是代码中写的：

```java
new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, level1Category.getCatId())
```

获取到二级分类后，就需要遍历它给手动创建的 Catalog2Vo 对象赋值，参照对象结构：第一个参数填写一级分类 id，也就是 level1Category 对象的 catId；
第二个参数填写三级分类集合，这里先写 null；第三个参数填写二级分类的 id，也就是 category2Entity 的 catId；第三个参数填写二级分类的名称，也就是 category2Entity 的 name。
数据都获取完毕后，这个二级分类就作为 Map 集合的 value 而存在了。

```java
// 找三级分类
List<CategoryEntity> category3Entities = categoryDao.selectList(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, category2Entity.getCatId()));
if (!category3Entities.isEmpty()) {
    List<Catalog2Vo.Catalog3Vo> catelog3Vos = category3Entities.stream().map(category3Entity -> {
        Catalog2Vo.Catalog3Vo catelog3Vo = new Catalog2Vo.Catalog3Vo(
                category2Entity.getCatId().toString(),
                category3Entity.getCatId().toString(),
                category3Entity.getName()
        );
        return catelog3Vo;
    }).collect(Collectors.toList());
    catelog2Vo.setCatalog3List(catelog3Vos);
}
return catelog2Vo;
```

关于三级分类集合的获取，其实和获取二级分类时类似，查找那些父类 id 为二级分类的 catId 的对象就好了，也就是代码中的：

```java
new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, category2Entity.getCatId())
```

然后对照填写属性：第一个为二级分类的 id，也就是 category2Entity 的 catId；第二个为当前分类的 id，也就是 category3Entity 的 catId；最后一个就是当前分类的名称，
也就是 category3Entity 的 name。

****
## 4. 搭建 nginx 

### 4.1 正向代理和反向代理

1、正向代理

正向代理是位于客户端（例如本机的浏览器）和目标服务器（如 Google）之间的一个服务器。客户端会将自己的请求先发送给这个代理服务器，由代理服务器去访问目标服务器，
然后将结果返回给客户端。

流程：

1. 客户端（浏览器）明确配置要使用代理服务器（例如设置代理IP和端口）。 
2. 客户端发起对 www.google.com 的请求。 
3. 这个请求被直接发送到正向代理服务器。 
4. 正向代理服务器代表客户端，向 www.google.com 发起请求。
5. www.google.com 将响应返回给正向代理服务器。Google 看到的是代理服务器的IP，而不是客户端的真实IP。 
6. 正向代理服务器将响应返回给客户端。

2、反向代理

反向代理是位于目标服务器和客户端之间的一个服务器。客户端直接访问反向代理，反向代理接收请求后，会将请求转发给内部网络中的一台或多台服务器（真正的处理者），
并将从服务器得到的结果返回给客户端。

流程：

1. 客户端发起对 www.example.com 的请求。 
2. DNS 解析将 www.example.com 指向反向代理服务器的IP地址。 
3. 反向代理服务器接收请求。 
4. 反向代理根据预设的规则（负载均衡策略、请求内容等），将请求转发到内部网络中的某台真实服务器（也叫后端服务器）。 
5. 真实服务器处理请求，并将响应返回给反向代理。 
6. 反向代理将响应最终返回给客户端。

### 4.2 安装 nginx

1、创建宿主机目录，准备将 Nginx 容器里的配置文件、静态资源、日志，全部挂载到宿主机目录

```shell
# 创建宿主机目录
mkdir -p /nginx/conf
mkdir -p /nginx/conf.d
mkdir -p /nginx/html
mkdir -p /nginx/logs
```

2、拷贝 Nginx 容器的默认配置出来，相当于把容器里的 nginx.conf 和 default.conf 拷贝到宿主机，作为初始配置

```shell
# 拷贝主配置
docker run --rm nginx cat /etc/nginx/nginx.conf > /nginx/conf/nginx.conf

# 如果有 default.conf 就拷贝出来
docker run --rm nginx cat /etc/nginx/conf.d/default.conf > /nginx/conf.d/default.conf
```

3、运行时挂载目录

```shell
docker run -d \
  -p 80:80 \
  -v /nginx/conf/nginx.conf:/etc/nginx/nginx.conf \
  -v /nginx/conf.d:/etc/nginx/conf.d \
  -v /nginx/html:/usr/share/nginx/html \
  -v /nginx/logs:/var/log/nginx \
  --name nginx \
  nginx
```

****
### 4.3 使用 nginx 访问

通过上面的安装后，可以查看一下 /nginx/conf.d/default.conf 文件，就是在这里面进行一些反向代理，例如现在要把本地 SpringBoot 的 10000 端口代理到 nginx，就需要：

```shell
server {
    listen       80;
    listen  [::]:80;
    server_name  localhost;
    location / {
        proxy_pass http://host.docker.internal:10000;
    }
}
```

注意这里的 ip 地址不能写 127.0.0.1，因为在 Docker 容器中 127.0.0.1 指向的是容器自身的网络空间而不是宿主机，如果配置的是 127.0.0.1：10000 的话，
Nginx 会尝试访问容器内部的 10000 端口，但 Spring Boot 服务不是运行在容器内，因此会提示 “连接失败”。在 WSL2 + Docker Desktop 环境中，
Docker 专门提供了 host.docker.internal 这个特殊域名，用于容器访问宿主机。配置成功后访问 localhost 可以直接进入 10000 端口。

不过通常情况下会启用多个服务，所以它们会有多个端口，如果直接使用 nginx 反向代理到它们的话就需要配置多个端口地址，如果直接让 nginx 反向代理到网关，那就可以让网关动用它的负载均衡能力，
只需要告知服务名即可负载访问多个端口的服务，修改 nginx：

```shell
server {
    listen       80;
    listen  [::]:80;
    server_name  localhost;
    location / {
        proxy_pass http://host.docker.internal:88;
    }
}
```

为网关配置监听 nginx 请求路径：

```yaml
spring:
  cloud:
    gateway:
      routes:
        ...
        - id: nginx_route
          uri: lb://gulimall-product
          predicates:
            - Path=/**
```

需要注意的是，这个 nginx 的监听路径要写在所有的网关路由下面，否则有关 localhost 的请求路径都会转发到 gulimall-product 服务，也就是说，只要是 localhost 的请求，
就一定会经过 nginx 反向代理。

****
## 5. 性能压测

压力测试考察当前软硬件环境下系统所能承受的最大负荷并帮助找出系统瓶颈所在，压力测试都是为了系统在线上的处理能力和稳定性维持在一个标准范围内。

### 5.1 性能指标

1、响应时间

指用户发出请求到接收到响应的总时间，常用统计：

- 平均响应时间 
- P95 响应时间（95% 请求的响应时间不超过某值） 
- P99 响应时间（99% 请求的响应时间不超过某值）
- 例如：电商网站下单接口，平均 300ms，P95 800ms，P99 1.5s。

2、吞吐量

指单位时间内系统处理的请求数。单位：TPS（Transactions Per Second，每秒事务次数）、RPS（Requests Per Second，每秒请求数）、QPS（Queries Per Second，每秒处理查询次数）。
对于互联网业务中，如果某些业务有且仅有一个请求连接，那么 TPS=RPS=QPS，一般情况下用 TPS 来衡量整个业务流程，用 QPS 来衡量接口查询次数，用 RPS 来衡量对服务器的请求数。
一个事务 (Transaction) = 多个请求 (Request)；一个请求 (Request) = 多个查询 (Query)。因此：

- TPS 粒度最大，强调业务流程（如下单）
- RPS 粒度中等，强调接口层面的请求数
- QPS 粒度最细，强调数据库/搜索引擎的查询数

3、并发用户数

同一时刻活跃的用户数。而并发连接数是指一个用户可能打开多个页面或发多个请求。

4、错误率

指请求失败的比例。

5、吞吐-响应时间曲线

正常情况：随着并发量增加，吞吐量线性上升，响应时间保持稳定。当达到瓶颈后：吞吐量不再上升，响应时间急剧增加，错误率升高（这个点就是性能拐点）。

****
### 5.2 Jmeter 压力测试

Apache JMeter 是 Apache 基金会出品的开源性能测试工具，最初用于 Web 测试，现在已支持多种测试：

- Web (HTTP/HTTPS)
- FTP 
- JDBC (数据库)
- SOAP/REST API 
- JMS (消息)
- TCP、SMTP、POP3 等协议

但其本质是 JMeter 通过模拟并发用户请求，来测试系统的性能和稳定性。在官网下载并解压好后找到 jmeter.bat 双击启动即可。在 JMeter 中：

- 一个线程就是一个虚拟用户，一个线程能模拟用户从发请求 -> 等响应 -> 再发下一个请求的全过程。
- 线程组就是线程的集合，用于控制压测规模，可以设置：线程数（并发用户数）、Ramp-Up 时间（多久启动完所有线程）、循环次数（每个线程执行请求的次数）。例如：线程数=100，Ramp-Up=10s，循环次数=5，表示在 10 秒内启动 100 个用户，每个用户执行 5 次请求。
- 采样器则是具体的请求动作。比如一个 HTTP Sampler 就代表一次 HTTP 请求。
- 监听器用于收集和展示测试结果，如表格、图形、日志。
- 逻辑控制器用于控制请求的执行逻辑，如顺序、循环、条件。

一个典型的 Jmeter 压力测试脚本结构：

```scss
测试计划 (Test Plan)
 └── 线程组 (Thread Group)
      ├── HTTP请求默认值 (配置元件)
      ├── HTTP请求 (Sampler)
      ├── CSV 数据文件 (参数化)
      ├── 定时器 (控制请求间隔)
      └── 监听器 (结果收集)
```

****
### 5.3 jvisualvm

在 JDK 8 以前，jvisualvm 是自带的，可以在命令窗口输入 jvisualvm 直接访问，但后面的版本就不再自带了，就需要取官网手动安装。它是一个基于 JDK Attach/JMX 的 Java 进程观察与分析工具，
适合排查性能与稳定性问题。只要本地启动了进程，那么就可以监控到，例如 CPU、堆内存、类加载、线程、GC 活动曲线（需下载 GC 插件，直观观察年轻代/老年代/幸存区变化）。

****
### 5.4 中间件对性能的影响

中间件是介于客户端与后端服务之间的软件层，负责处理路由转发、负载均衡、安全验证、缓存、日志等核心功能（如 Nginx、网关、Redis、Kafka 等）。它们对系统性能的影响是双向的：
既能通过优化流量分配、减少重复计算等提升整体性能，也可能因额外的处理逻辑、网络跳转等引入性能损耗。

1、反向代理 / 网关类中间件（如 Nginx、Spring Cloud Gateway）

这类中间件是请求进入系统的入口，负责请求转发、负载均衡、鉴权等，对性能的影响体现在延迟、吞吐量、资源占用三个维度。

- 网络延迟增加：

客户端请求需先经过中间件，再转发到后端服务，多一次网络跳转（从客户端 -> 服务变为客户端 -> 中间件 -> 服务）。即使中间件与服务部署在同一台设备，单次转发也可能增加 0.1~1ms 的延迟（取决于网络环境）；
跨设备署时，延迟可能增至 10~100ms。例如 Nginx 反向代理时，需解析请求头、匹配路由规则，再建立与后端服务的 TCP 连接，这些操作都会消耗时间。

- 计算资源消耗：

中间件需处理请求解析（如 HTTP 头、参数）、路由匹配（正则表达式、路径匹配）、负载均衡算法（轮询、哈希、权重计算）等逻辑，会占用 CPU 和内存。

  - 高并发场景下（如每秒 10 万条请求），Nginx 的 CPU 使用率可能从 0% 飙升至 50% 以上
  - 网关若开启复杂功能（如 JWT 鉴权、请求加密 / 解密），单请求处理时间可能增加1~5ms，直接降低吞吐量

- 连接数压力：

中间件需同时维护与客户端、后端服务的连接。若客户端使用短连接（如 HTTP/1.1 无 keep-alive，一次通信完成就断开的网络连接），中间件会频繁创建与关闭连接，
导致等待状态的连接堆积，占用系统端口资源，最终限制并发量。

虽然中间件越多造成的性能损耗越多，但这些中间件的正面优化通常是更高的，所以合理搭配使用时反而能提高系统的工作效率。

****
### 5.5 代码对性能的影响

1、算法的影响

算法的执行时间随数据量增长的趋势（如 O (1)、O (log n)、O (n)、O (n²)），在业务初期数据量小时，低效算法（如嵌套循环）可能表现正常，
但随着数据增长（如用户量从 1 万到 100 万），性能会突然崩溃。若空间复杂度高（如 O (n²)），会导致内存占用激增，触发频繁 GC（垃圾回收），甚至 OOM（内存溢出）。

2、数据结构的选择

不同数据结构的操作效率（查询、插入、删除）差异显著，需根据业务场景匹配，例如：

- ArrayList 和 LinkedList：

ArrayList 基于数组，访问（get(index)）效率高（O (1)），但插入与删除中间元素需移动数据（O (n)）；LinkedList 基于链表，插入、删除中间元素效率高（O (1)，只需修改指针），
但访问则需遍历整个链表（O (n)）。

- HashMap 和 TreeMap：

HashMap 查询、插入效率高（O (1)，基于哈希），但整体无序；TreeMap 基于红黑树，查询、插入效率为 O (log n)，但支持有序遍历。

3、IO 操作

IO 操作（磁盘 IO、网络 IO、数据库 IO）是代码性能的主要瓶颈，而 CPU 的速度（纳秒级）远快于 IO 速度（毫秒级，差距约 10^6 倍），所以代码中对 IO 的处理方式可以直接决定性能上限。

4、并发处理

服务通常通过多线程提升并发能力，但代码中对线程、锁的处理不当，会导致并发降低性能。所以需要合理配置线程池与锁的使用。

5、内存管理

代码中对内存的使用方式，会影响 JVM 的垃圾回收效率，严重时甚至导致内存泄漏。例如频繁创建短生命周期的大对象，这就会导致年轻代（Young GC）频繁触发（每几秒一次），
每次 GC 会暂停线程，累积延迟影响用户体验。而无用对象未被及时回收时，它会持续占用内存，最终导致内存不够实用而发生泄露。

****
### 5.6 nginx 优化之动静分离

核心思想就是将静态资源和动态资源的请求分开处理，让 nginx 专注于静态资源，后端服务器处理动态请求，从而提升性能和并发能力。Nginx 擅长高并发静态文件的访问，不用启动应用服务器或查询数据库就可以获取静态资源。
后端服务器只需要处理动态请求，这样就可以降低很多压力。Nginx 可以通过 location 匹配规则将不同类型的请求转发到不同的处理方式：

1. 静态资源请求 -> Nginx 本地目录或缓存 
2. 动态请求 -> 后端应用服务器（如 Tomcat、Node.js、PHP-FPM）

例如：

```shell
http://localhost/index.html -> 静态文件
http://localhost/api/getUser -> 动态接口
```

Nginx 会根据请求路径或文件后缀判断请求类型，静态资源直接读取磁盘或缓存返回。动态资源通过 proxy_pass 转发到后端服务器。可以将 product 服务中的 index 静态资源放进 nginx 容器中，
然后设置一个 location 的请求路径，例如 location  /static，那么访问 localhost/static 的时候就会查找里面填写的路径：

```shell
location /static/ {
    root /usr/share/nginx/html;
}
```

1、先把本地的静态资源拷贝进 nginx 挂载的宿主机目录

```shell
cp -r /mnt/d/docker_dataMountDirectory/gulimall-product/index /nginx/html/static/
```

2、修改 default.conf 文件，配置静态资源路径

```shell
location /static/ {
    root    /usr/share/nginx/html;
}
```

因为 nginx 挂载到了本地，所以访问以上路径的时候其实就是访问挂载到本地的那个 html 目录。

3、将 product 服务中的 index.html 中的 index 路径修改为 /static/index

当启动 product 服务时，页面会跳转到 index.html 页面，该页面会通过发送 /static/index/... 请求取寻找静态资源，而此时的静态资源全部放入了 nginx 且也配置了反向代理，
所以会直接在 nginx 中进行查找。

****
### 5.7 三级分类数据获取的优化

在原先的查询三级分类功能中写的代码是一种不断嵌套查询数据库获取数据的方法，这种写法就会导致与数据库进行多次交互，这就会导致性能较差。

```java
// 一级分类循环
for (CategoryEntity level1Category : level1Categories) {
    // 查询二级分类
    List<CategoryEntity> category2Entities = categoryDao.selectList(
            new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, level1Category.getCatId())
    );
    
    // 二级分类循环
    for (CategoryEntity category2Entity : category2Entities) {
        // 查询三级分类
        List<CategoryEntity> category3Entities = categoryDao.selectList(
                new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, category2Entity.getCatId())
        );
    }
}
```

原始的写法中一级分类数量 x 二级分类数量会导致大量 SQL 查询：

- 如果一级分类 10 个，每个一级分类 10 个二级分类，三级分类又各 10 个 -> 总共 SQL 查询次数 = 1 + 10 + 100 = 111 次
- 每次 categoryDao.selectList(...) 都要访问数据库 -> 性能瓶颈明显
- 所以这种写法就是典型的 N+1 查询问题，也就是需要查询 1 个父对象及其关联的 N 个子对象

既然发现了问题所在，那么就可以通过预加载的方式一次性查询所有关联数据。

```java
@Override
public Map<String, List<Catalog2Vo>> getCatalogJson() {
  List<CategoryEntity> categoryEntities = categoryDao.selectList(null);
  // 1. 查出所有一级分类
  // List<CategoryEntity> level1Categories = getLevel1Categories();
  List<CategoryEntity> level1Categories = getCategoryParent(categoryEntities, 0L);
  // 2. 封装数据
  Map<String, List<Catalog2Vo>> collect = level1Categories.stream().collect(
          Collectors.toMap(level1Category -> {
            return level1Category.getCatId().toString();
          }, level1Category -> {
            // 查询此一级分类的所有二级分类
            List<CategoryEntity> category2Entities = getCategoryParent(categoryEntities, level1Category.getCatId());
            ...
              catelog2Vos = category2Entities.stream().map(category2Entity -> {
                Catalog2Vo catelog2Vo = new Catalog2Vo(
                        level1Category.getCatId().toString(),
                        null,
                        category2Entity.getCatId().toString(),
                        category2Entity.getName()
                );
                // 找三级分类
                List<CategoryEntity> category3Entities = getCategoryParent(categoryEntities, category2Entity.getCatId());
                ...
              }).collect(Collectors.toList());
            }
            ...
}
```

```java
private List<CategoryEntity> getCategoryParent(List<CategoryEntity> categoryEntities, Long parentCid) {
    // 查询那些 parent_cid = 当前分类 id 的数据
    List<CategoryEntity> collect = categoryEntities.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(parentCid)).collect(Collectors.toList());
    return collect;
}
```

修改后的代码则是先查询出所有的三级分类集合，此时整个 category 表的数据都加载到内存中，存在了 categoryEntities 这个 List 里，数据库访问结束，
后续对 categoryEntities 的任何操作都只是操作 Java 内存中的对象，不会再访问数据库。后续调用的 getCategoryParent 方法则是对内存中 categoryEntities 这个 List 的遍历。
filter(...) 是对对象属性的判断，整个过程没有任何数据库操作，只是 Java 内存里的循环和判断，只要那些 parentCid = 传递过来的当前分类的 id。
既然此时是对内存的操作，那么就必然会提高程序的性能。

****
## 6. 缓存

### 6.1 本地缓存与分布式缓存

1、本地缓存

本地缓存就是缓存数据存储在应用进程的本地内存中，程序可以直接从内存读取，无需网络请求。但它的生命周期与应用进程一致（进程重启后缓存数据丢失）。本地缓存的实现简单灵活，常见方式包括：

- 基础数据结构：如 Java 中的 HashMap、ConcurrentHashMap
- 专用缓存库：如 Java 的 Guava Cache、Caffeine（支持自动过期、容量限制、淘汰策略）
- 框架内置缓存：如 Spring 的 @Cacheable（可配置本地缓存管理器）

本地缓存的优势：

- 本地缓存数据存储在应用内存中，访问速度可达纳秒级（远快于分布式缓存的毫秒级网络开销，或数据库的磁盘 IO）。适合高频访问、低延迟要求的场景（如热点配置、字典表查询）
- 无需部署独立缓存服务（如 Redis），无需处理网络连接、序列化等问题，仅通过进程内代码即可实现（如用 HashMap 缓存查询结果）
- 不依赖外部服务，避免了分布式缓存的网络故障、服务宕机等依赖风险（如 Redis 集群不可用时，本地缓存仍可提供数据）

本地缓存存在的问题：

- 数据一致性问题

本地缓存的最大问题是分布式场景下的数据不一致，当应用以集群方式部署时，每个实例都拥有自己的本地缓存，但数据更新不好同步，这就会导致不同实例的缓存数据存在差异。例如：
电商系统中，多个订单服务实例都缓存了商品库存（本地缓存）。当某一实例处理订单扣减库存后，却只能更新自己的缓存，但数据库是通用的，其他实例的本地缓存仍保留旧库存值。
此时，用户可能看到虚假库存，导致超卖或下单失败。

- 内存资源消耗失控

本地缓存占用应用进程的内存，如果处理不正确则可能导致内存耗尽，影响应用稳定性。

2、分布式缓存

分布式缓存则是独立部署的缓存服务集群，数据存储在专门的缓存节点中，而非应用进程内存，应用程序通过网络协议（如 TCP、HTTP）访问缓存数据。核心特点包括：

- 存储位置：独立的缓存服务集群（如 Redis 集群、Memcached 集群）
- 访问方式：通过网络 IO（如 Socket）与缓存服务交互，存在一定网络开销
- 生命周期：与缓存服务进程一致，不受单个应用实例重启影响
- 数据共享：集群内所有应用实例共享同一套缓存数据，天然支持分布式场景

优势：

- 全局数据一致性

分布式缓存是所有应用实例共享的数据源，数据更新后所有实例都可以读取到最新值，从根本上避免了本地缓存的数据不一致问题。但既然是缓存，就一定存在与数据库数据的数据不一致问题。

- 集群资源利用率高

分布式缓存的数据只需要存储一份（或按分片存储），因为它是通用的，所以避免了本地缓存的数据重复存储问题，这可以降低内存资源的浪费。

- 缓存与应用解耦

分布式缓存作为独立服务存在，与应用程序解耦，不需要在程序中专门写一大堆代码去获取。

存在问题：

- 网络开销导致性能损耗

既然分布式缓存是一种中间件，那它就依赖网络通信，每次访问都存在网络 IO 开销（通常为毫秒级），性能低于本地缓存（纳秒级）。在极端高频访问场景，例如每秒数十万次查询，
网络延迟可能成为瓶颈。

- 依赖外部服务的可用性风险

若缓存服务宕机或网络中断，将会直接影响应用可用性。此时所有请求可能穿透到数据库，导致数据库压力骤增，甚至引发雪崩问题。又或者因为应用与缓存服务之间发生网络分区，无法访问缓存，
导致超时失败。

- 数据一致性与更新策略复杂

分布式缓存虽解决了本地缓存的多实例数据不一致问题，但仍需解决缓存与数据库一致性的情况。

- 序列化与兼容性问题

分布式缓存中的数据需经过序列化才能存储，例如 Redis 只存储字节数组，通常需要由客户端将数据序列化成字符串或者 JSON。这无疑会提高性能消耗。

****
### 6.2 redis

#### 6.2.1 安装

```shell
# 创建数据目录（用于持久化Redis数据）
mkdir -p /redis/data
# 创建配置目录，并下载默认配置文件（或手动创建自定义配置）
mkdir -p /redis/conf
# 从Redis官网下载默认配置文件（可选，也可手动创建）
wget https://raw.githubusercontent.com/redis/redis/7.0/redis.conf -O /redis/conf/redis.conf
```

```shell
docker run -d \
  --name redis \
  -p 6379:6379 \
  -v /redis/data:/data \
  -v /redis/conf/redis.conf:/etc/redis/redis.conf \
  redis:latest redis-server /etc/redis/redis.conf
```

****
#### 6.2.2 使用

1、引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

2、配置 yaml 文件

```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    password: 123
```

3、注入 Redis Bean

```java
@Autowired
private StringRedisTemplate stringRedisTemplate;
@Test
void testStringRedisTemplate() {
    stringRedisTemplate.opsForValue().set("hello", "world_" + UUID.randomUUID().toString());
    // 查询
    String hello = stringRedisTemplate.opsForValue().get("hello");
    System.out.println(hello);
}
```

****
### 6.3 使用 redis 改造三级分类

这里直接把原始的方法修改为 getCatalogJsonFromDb()，当 redis 中没有缓存时则调用此方法查询数据库；有缓存则查询缓存。但需要注意的是：因为当时封装的对象为一个 Map 类型，
如果直接使用 catalogJsonFromDb.toString() 作为 JSON 传入 redis 的话，就会出现 JSON 解析错误，因为 Map.toString() 方法生成的字符串不是标准 JSON，
而是 Java 对象的默认字符串表示（例如 {11=[...], 12=[...]}），其特点是：

- 键没有双引号（如 11 = 而非 "11":）
- 字符串值用单引号（如 'name' 而非 "name"）
- 不符合 JSON 语法规范，导致 ObjectMapper 解析失败

所以需要使用工具来将该对象转换成一个标准的 JSON 对象，可以使用 fastjson 的 parseObject 方法，但是它存在一定的安全泄露问题，所以还是推荐 SpringMVC 的 ObjectMapper。
当然解析 JSON 时也尽量使用 SpringMVC 的 ObjectMapper，它是 Jackson 库中用于处理所有 Java 类型 与 JSON 之间相互转换的工具。而对于 List、Map、数组等复杂结构，
需要通过 TypeReference 指定泛型类型（避免泛型擦除导致解析错误）。

```java
@Override
public Map<String, List<Catalog2Vo>> getCatalogJson() {
    String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
    if (StringUtils.isEmpty(catalogJson)) {
        // 缓存未命中，查询数据库
        Map<String, List<Catalog2Vo>> catalogJsonFromDb = getCatalogJsonFromDb();
        // 存入缓存
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 转换成 JSON
            String jsonStr = objectMapper.writeValueAsString(catalogJsonFromDb);
            // 存入 Redis 的是标准 JSON
            stringRedisTemplate.opsForValue().set("catalogJson", jsonStr);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败：{}", e.getMessage());
        }
        // 直接返回数据库查询结果，避免重复解析
        return catalogJsonFromDb;
    }
    log.info("catalogJson: {}", catalogJson);
    // 将获取到的缓存数据转换成需要的对象类型
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, List<Catalog2Vo>> result = null;
    try {
        result = objectMapper.readValue(
                catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {}
        );
    } catch (Exception e) {
        log.error("JSON解析失败：{}", e.getMessage());
        // 清除错误缓存
        stringRedisTemplate.delete("catalogJson");
    }
    return result;
}
```

****
### 6.4 缓存问题

1、缓存穿透

缓存穿透是指用户请求查询的数据在缓存和数据库中都不存在，导致请求每次都穿透缓存，直接访问数据库，且数据库也无法返回结果。由于缓存无法命中（不存在数据），所有请求都会直达数据库。
如果请求量巨大（比如恶意攻击），数据库会被高频无效查询压垮，甚至宕机。

解决方法：

- 缓存空值：当数据库查询结果为 null 时，将 “空值” 写入缓存（如key:null），并设置较短的过期时间（如 5-10 分钟），后续相同 key 的请求会先命中缓存的空值，直接返回，避免再次访问数据库。这也是常用的方法。
- 布隆过滤器：布隆过滤器是一种概率性数据结构，可快速判断一个元素是否存在于集合中（存在一定误判率，但效率极高）
- 接口层校验：对请求参数进行合法性校验（如用户 ID 必须为正数、商品 ID 范围限制），直接拦截明显无效的请求（如 key=-1），从源头阻挡无效请求发起者

2、缓存击穿

缓存击穿是指一个热点 key（被高频访问的 key）在缓存中过期的瞬间，大量请求同时到达，导致所有请求都穿透缓存，直接访问数据库。

解决方法：

- 热点 key 不设置过期时间：从逻辑上让热点 key 不过期（或者直接不设置过期时间），后台启动定时任务，定期主动更新该 key 的缓存（如每 10 分钟更新一次），保证缓存数据与数据库一致。
- 使用互斥锁：当缓存失效时，通过分布式锁（如 Redis 的 setnx 命令）保证只有一个线程能去数据库查询并重建缓存，其他线程等待缓存重建完成后再从缓存获取数据。

3、缓存雪崩

缓存雪崩是指大量缓存 key 在同一时间点过期，或缓存服务（如 Redis 集群）整体宕机，导致缓存层整体失效，所有请求全部打向数据库，引发数据库崩溃。若数据库宕机后，缓存仍未恢复，
系统会陷入请求 -> 数据库失败 -> 重试的恶性循环，最终导致整个系统崩溃。

解决方法：

- 过期时间加随机值：对缓存 key 的过期时间添加随机偏移量，让缓存的过期时间分散，避免同一时间点集中过期。
- 服务熔断与降级：当缓存失效且数据库压力剧增时，通过熔断与降级操作限制请求流量，保护数据库

****
### 6.5 本地锁解决缓存击穿问题

本地锁的核心思想是只允许一个请求去重建（查询数据库并写入）缓存，其它请求则等待，当缓存重建完成后，就可以直接从缓存中获取数据，避免全部查询数据库。

流程：

1. 请求到来，首先尝试从缓存（如Redis）中获取数据
2. 如果数据存在，直接返回
3. 缓存未命中（击穿发生）：
   - 在尝试查询数据库之前，先尝试获取一个锁，这个锁的目的是保证在同一时间，只有一个请求线程可以执行查询数据库的操作
   - 在成功获取锁之后，再次检查缓存，因为可能在获取锁的过程中，另一个抢先获取到锁的请求已经完成了数据库查询和缓存写入的操作。如果此时缓存中已有数据，就直接返回，避免垃圾查询。
   - 如果第二次检查缓存依然为空，则执行真正的业务逻辑：查询数据库
   - 将从数据库查到的数据写入缓存，并设置过期时间
   - 最后释放锁，让其他被阻塞的线程可以继续执行

```java
public Map<String, List<Catalog2Vo>> getCatalogJsonFromDb() {
    synchronized (this) {
        // 得到锁后应该再去缓存中查询一遍，如果没有再进行查询数据库
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            System.out.println("getCatalogJsonFromDb 缓存命中...");
            // 将获取到的缓存数据转换成需要的对象类型
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, List<Catalog2Vo>> result = null;
            try {
                result = objectMapper.readValue(
                        catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
                        }
                );
            } catch (Exception e) {
                log.error("JSON解析失败：{}", e.getMessage());
                // 清除错误缓存
                stringRedisTemplate.delete("catalogJson");
            }
            return result;
        }
        System.out.println("开始查询数据库...");
        ...
        // 存入缓存
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 转换成 JSON
            String jsonStr = objectMapper.writeValueAsString(collect);
            // 存入 Redis 的是标准 JSON
            stringRedisTemplate.opsForValue().set("catalogJson", jsonStr);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败：{}", e.getMessage());
        }
        return collect;
    }
}
```

这种方法在单服务的情况下可以保证只有一个线程执行查询数据库的操作，但在分布式系统环境中仍存在问题，因为本地锁（如 synchronized 或 ReentrantLock）只在当前 JVM 进程内有效，
假设商品服务部署在三台服务器上（A, B, C），当缓存失效时，三个来自不同用户的请求可能分别被负载均衡到服务器 A、B、C 上。每个服务器上的本地锁只能锁住本机的线程，于是，
这三个请求会各自成功获取到自己服务器上的本地锁，然后各自去查询一次数据库。这会导致数据库同时承受 3 个查询请求，如果节点数更多，压力会更大。
这完全违背了只允许一个请求去查数据库的初衷。并且，如果该节点在处理数据库查询时崩溃，可能导致锁无法释放，缓存也重建失败，后续请求可能会再次触发击穿流程。

****
### 6.6 分布式锁

本地锁的存在的局限就是只能锁住本服务，无法锁住其它服务，而分布式锁就能解决这个问题。一个正确的分布式锁必须至少满足以下三个基本属性：

- 互斥性：在任意时刻，只有一个客户端能持有锁
- 无死锁：即使持有锁的客户端崩溃或者网络分区，锁最终也能被释放，从而允许其他客户端获取锁
- 容错性：只要大部分 Redis 节点正常运行，客户端就能获取和释放锁

最初，人们使用 SETNX（SET if Not exists）命令来实现：

```redis
SETNX key value
```

- 如果返回 1，说明设置成功，客户端获取了锁
- 如果返回 0，说明 key 已存在，获取锁失败

解锁则是直接通过删除该 key 来完成：

```redis
DEL key
```

基于以上特征，可以利用 Redis 的 SETNX 来作为分布式锁，因为 Redis 天然具有全局一致性，配合该命令就能保证只有一个服务的一个线程可以拿到锁：

```java
public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {
    // 1. 占用分布式锁
    Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", "111");
    if (lock) {
        // 加锁成功
        Map<String, List<Catalog2Vo>> dataFromDb = getDataFromDb();
        stringRedisTemplate.delete("lock"); // 删除锁
        return dataFromDb;
    } else { // 加锁失败，自旋重试
        return getCatalogJsonFromDbWithRedisLock();
    }
}
```

不过这种方式存在一些问题，那就是当程序发生异常而无法正常释放锁时，那该锁就变成了死锁，不过既然是存在 Redis 中的 key，那就可以设置过期时间，让它自动释放锁。

```java
if (lock) { // 加锁成功
    // 2. 设置过期时间
    stringRedisTemplate.expire("lock", 30, TimeUnit.SECONDS);
    Map<String, List<Catalog2Vo>> dataFromDb = getDataFromDb();
    stringRedisTemplate.delete("lock"); // 删除锁
    return dataFromDb;
} else { // 加锁失败，自旋重试
    return getCatalogJsonFromDbWithRedisLock();
}
```

但是，这种增加过期时间的操作并不是原子的，如果在增加过期时间前就发生异常，那么结果还是和之前一样变成死锁。因为 SETNX 和 EXPIRE 是两条命令，
如果客户端在执行完 SETNX 后、执行 EXPIRE 前崩溃了，那么锁依然永远不会释放，导致死锁。所以在 Redis 2.6.12 之后，SET 命令增加了 NX、EX 等选项，
使得加锁和设置超时时间可以原子性地完成，这是目前实现单节点 Redis 锁的标准方法。

```java
Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);
```

死锁的问题解决了，可是如果：

1. 客户端 A 获取锁（lock_key，过期时间 30 秒）
2. 客户端 A 因为某些操作（如 GC、网络延迟）导致耗时超过 30 秒，锁自动过期释放了
3. 此时客户端 B 成功获取了同一个锁
4. 客户端 A 操作完成，执行 DEL lock_key，错误地将客户端 B 的锁释放了

所以应该在删除锁之前，先判断当前锁是否属于自己，也就是检查该锁对应的 value 是否还是自己当初设置的那个值，如果是，才能删除。

```java
String uuid = UUID.randomUUID().toString();
if (uuid.equals(stringRedisTemplate.opsForValue().get("lock"))) {
    stringRedisTemplate.delete("lock"); // 删除锁
}
```

不过现在又有问题出现，那就是判断和删除锁的操作并不是原子性的，可能出现：

1. 线程 A 获取锁 lock:order，值为 uuidA，过期时间 5 秒，此时正好判断完锁是否属于自己。
2. 线程 A 完成整个业务流程，花了 6 秒，但锁在第 5 秒自动过期。
3. 线程 B 在第 5 秒时获取锁，值为 uuidB。
4. 第 6 秒，线程 A 业务执行完，进入释放逻辑，由于已经判断过该锁属于自己（但实际上 value 已经修改为 uuidB），就直接执行删除锁操作。
5. 结果误删线程 B 的锁。

所以现在还必须解决判断与删除操作的原子性问题，而 Redis 的 lua 脚本正好可以保证操作的原子性，释放锁的脚本代码如下：

```lua
if redis.call("get", KEYS[1]) == ARGV[1] then
    return redis.call("del", KEYS[1])
else
    return 0
end
```

- KEYS[1]：锁的 key，这里是 "lock"
- ARGV[1]：锁的唯一值，这里是 uuid

执行流程：

1. 获取当前锁的值：redis.call('get', KEYS[1])
2. 比较值是否匹配：== ARGV[1]
3. 如果匹配：删除锁并返回1（成功） 
4. 如果不匹配：返回0（失败）

使用 Java 代码表示则如下：

```java
try {
    dataFromDb = getDataFromDb();
} catch (Exception e) {
    log.error("getDataFromDb 发生错误：{}", e.getMessage());
} finally {
    String script =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "    return redis.call('del', KEYS[1]) " +
                    "else " +
                    "    return 0 " +
                    "end";
    Long lock1 = stringRedisTemplate.execute(
            new DefaultRedisScript<>(script, Long.class), // 脚本对象
            Arrays.asList("lock"), // KEYS 列表
            uuid // ARGV 参数
    );
}
```

调用 StringRedisTemplate 的 execute 方法，它要求传递脚本对象、KEYS 列表、多个参数（如果有），因为 Redis 规定 KEYS 和 ARGV 必须分开传递。

```java
public <T> T execute(RedisScript<T> script, List<K> keys, Object... args) {
    return (T)this.scriptExecutor.execute(script, keys, args);
}
```

****
### 6.7 Redisson

Redisson 是基于 Netty 的 Redis Java 客户端，除了基本的 K-V 操作外，它把大量分布式数据结构与并发原语封装成了与 Java 标准库近似的 API，开箱即用，极大简化了分布式开发。

#### 6.7.1 常见部署

1、引入依赖

这两个依赖都是 Redisson 的，不过第一个是专门为 Spring Boot 项目提供的自动配置支持：

- 自动加载 redisson.yaml / redisson.json 配置文件。 
- 自动配置 RedissonClient Bean，直接注入即可使用。 
- 和 Spring Boot 的配置体系整合（application.yml 里就能配置）。 
- 内置了对 Spring Cache、Spring Transaction、Spring Session 等的支持。

```xml
<dependency>
  <groupId>org.redisson</groupId>
  <artifactId>redisson-spring-boot-starter</artifactId>
  <version>3.18.0</version>
</dependency>
```

第二个则是 Redisson 的 核心库，包含了所有分布式对象（锁、集合、队列、ExecutorService 等）的实现，它不依赖 Spring Boot，可以在任意 Java 项目中使用，但需要手动创建和配置 RedissonClient。

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.17.7</version>
</dependency>
```

2、配置客户端信息

如果是引入了第一个依赖，那么就可以直接通过该 Redis 的配置文件进行使用

```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    password: 123   # 若 Redis 开了密码，必须配上
```

如果是第二个依赖，就需要手动写一个配置类：

```java
@Configuration
public class MyRedissonConfig {
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379") // 需要带上 redis:// ，如果启用了加密 SSL，那就需要用 rediss://
                .setPassword("123");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
```

****
#### 6.7.2 lock 锁测试

Redisson 提供了加锁功能，通过从 Redis 中获取一个指定名称的 key 来达到加锁的目的（不存在则会自动创建），该锁是阻塞式等待的，当锁被其他线程持有时，当前线程会一直阻塞等待，直到获取到锁。
它没有超时时间，会一直等待，并且默认是非公平锁，但可以通过配置改为公平锁。该锁同样具有可重入性（RLock extends Lock），并且自带自动续期机制，也就是说它解决了传统分布式锁的局限。
这也是 Redisson 锁的最重要特性之一。另外一个就是可以自动避免死锁，它底层设置了自动过期时间（默认 30 s），到期自动释放锁。

```java
@ResponseBody
@GetMapping("/hello")
public String hello() {
    // 1. 获取一把锁，只要锁的名字一样，那就是同一把锁
    RLock rLock = redisson.getLock("my-lock");
    // 2. 加锁
    rLock.lock(); // 阻塞时等待
    try {
        System.out.println("加锁成功，执行业务..." + Thread.currentThread().getName());
        Thread.sleep(10000);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        System.out.println("释放锁..." + Thread.currentThread().getName());
        rLock.unlock();
    }
    return "hello";
}
```

该锁在 Redis 中的存储结构：

```json
Key: "my-lock" // 指定的锁名称
Type: Hash // 哈希表结构
Value: 
  field: "b983c153-4721-4b12-bb5c-d5c6b6a886a8:1" // 锁持有者的标识，UUID:threadId 
  value: 1 // 重入次数计数器
```

使用 Redisson 修改原有代码：

```java
public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
    RLock rLock = redisson.getLock("CatalogJson-lock");
    rLock.lock();
    Map<String, List<Catalog2Vo>> dataFromDb = null;
    try {
        dataFromDb = getDataFromDb();
    } catch (Exception e) {
        log.error("getDataFromDb 发生错误：{}", e.getMessage());
    } finally {
        rLock.unlock();
    }
    return dataFromDb;
}
```

****
#### 6.7.3 看门狗机制

当给 lock() 方法指定过期时间后，则会进入下面这个放方法：

```java
public void lock(long leaseTime, TimeUnit unit) {
    try {
        this.lock(leaseTime, unit, false);
    } catch (InterruptedException var5) {
        throw new IllegalStateException();
    }
}
```

然后会调用底层代码设置过期时间，第一个参数 -1L 表示重试时间。然后便会进入一个死循环，这也是为什么 Redisson 的锁为阻塞等待式的原因，而在不断循环的过程也会检查过期时间。

```java
private void lock(long leaseTime, TimeUnit unit, boolean interruptibly) throws InterruptedException {
    long threadId = Thread.currentThread().getId();
    Long ttl = this.tryAcquire(-1L, leaseTime, unit, threadId);
    if (ttl != null) {
        ...
        try {
            while(true) {
                ttl = this.tryAcquire(-1L, leaseTime, unit, threadId);
                if (ttl == null) {
                    return;
                }
                if (ttl >= 0L) {
                    try {
                        entry.getLatch().tryAcquire(ttl, TimeUnit.MILLISECONDS);
                    } 
                    ...
                } 
                ...
            }
        } 
    }
}
```

获取过期时间的方法如下，判断传入的过期时间是否大于 0（若未手动设置过期时间，leaseTime 就为 -1）。不管是否手动设置过期时间，都要执行 tryLockInnerAsync() 方法，
只是传入的参数不同。

```java
private <T> RFuture<Long> tryAcquireAsync(long waitTime, long leaseTime, TimeUnit unit, long threadId) {
    RFuture<Long> ttlRemainingFuture;
    if (leaseTime > 0L) {
        ttlRemainingFuture = this.<Long>tryLockInnerAsync(waitTime, leaseTime, unit, threadId, RedisCommands.EVAL_LONG);
    } else {
        ttlRemainingFuture = this.<Long>tryLockInnerAsync(waitTime, this.internalLockLeaseTime, TimeUnit.MILLISECONDS, threadId, RedisCommands.EVAL_LONG);
    }
    CompletionStage<Long> f = ttlRemainingFuture.thenApply((ttlRemaining) -> {
        if (ttlRemaining == null) {
            if (leaseTime > 0L) {
                this.internalLockLeaseTime = unit.toMillis(leaseTime);
            } else {
                this.scheduleExpirationRenewal(threadId);
            }
        }
        ...
    });
    return new CompletableFutureWrapper(f);
}
```

该方法最终都是把传递的参数作为 lua 脚本执行的参数，

```java
<T> RFuture<T> tryLockInnerAsync(long waitTime, long leaseTime, TimeUnit unit, long threadId, RedisStrictCommand<T> command) {
    return this.evalWriteAsync(this.getRawName(), LongCodec.INSTANCE, command,
            "if (redis.call('exists', KEYS[1]) == 0) " +
                    "then redis.call('hincrby', KEYS[1], ARGV[2], 1); " +
                    "redis.call('pexpire', KEYS[1], ARGV[1]); return nil; " +
                    "end; " +
                    "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) " +
                    "then redis.call('hincrby', KEYS[1], ARGV[2], 1);" +
                    " redis.call('pexpire', KEYS[1], ARGV[1]); " +
                    "return nil; " +
                    "end; " +
                    "return redis.call('pttl', KEYS[1]);",
            Collections.singletonList(this.getRawName()), new Object[]{unit.toMillis(leaseTime), this.getLockName(threadId)});
}
```

在 tryAcquireAsync 方法中，如果没手动设置过期时间，那就会执行如下方法，该方法就是用来刷新过期时间的。该方法内部会执行 renewExpirationAsync()，
而该方法每 30 / 3 = 10 s 则会重新检测一遍。

```java
private void renewExpiration() {
    ...
    if (ee != null) {
        Timeout task = this.commandExecutor.getConnectionManager().newTimeout(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                ...
                if (ent != null) {
                    Long threadId = ent.getFirstThreadId();
                    if (threadId != null) {
                        CompletionStage<Boolean> future = RedissonBaseLock.this.renewExpirationAsync(threadId);
                        ...
                    }
                }
            }
        }, this.internalLockLeaseTime / 3L, TimeUnit.MILLISECONDS);
        ee.setTimeout(task);
    }
}
```

通过执行 lua 脚本，将新的看门狗时间作为过期时间：

```java
protected CompletionStage<Boolean> renewExpirationAsync(long threadId) {
    return this.<Boolean>evalWriteAsync(this.getRawName(), LongCodec.INSTANCE, RedisCommands.EVAL_BOOLEAN, "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then redis.call('pexpire', KEYS[1], ARGV[1]); return 1; end; return 0;", Collections.singletonList(this.getRawName()), this.internalLockLeaseTime, this.getLockName(threadId));
}
```

不过在实际开发中并不建议使用看门狗机制一直延长过期时间，因为 30 s 的时间内还没执行完业务流程那证明是某些地方发生异常或崩溃，应该主动检查才对，所以建议直接设置一个合理的过期时间，
避免无效演唱过期时间。

****
#### 6.7.4 读写锁

读写锁允许多个读操作同时进行，但写操作是独占的，也就是说：多个线程可以同时持有读锁，但同一时间只能有一个线程持有写锁，并且读写互斥，有写锁时不能加读锁，有读锁时不能加写锁。

```java
@ResponseBody
@GetMapping("/write")
public String write() {
    RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
    String s = "";
    RLock rLock = readWriteLock.writeLock();
    // 1. 该数据加写锁，读数据加读锁
    rLock.lock();
    System.out.println("写锁加锁成功..." + Thread.currentThread().getName());
    try {
        s = UUID.randomUUID().toString();
        Thread.sleep(10000);
        stringRedisTemplate.opsForValue().set("writeValue", s);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        System.out.println("写锁释放..." + Thread.currentThread().getName());
        rLock.unlock();
    }
    return s;
}

@ResponseBody
@GetMapping("/read")
public String read() {
    RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
    RLock rLock = readWriteLock.readLock();
    String s = "";
    rLock.lock();
    System.out.println("读锁加锁成功..." + Thread.currentThread().getName());
    try {
        s = stringRedisTemplate.opsForValue().get("writeValue");
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        System.out.println("读锁释放..." + Thread.currentThread().getName());
        rLock.unlock();
    }
    return s;
}
```

经过测试：

- 读 + 读：相当于无锁，只会在 Redis 中记录当前的读锁，多个线程之间可以同时加锁成功
- 写 + 读：需要等待写锁释放才能成功加锁读锁
- 写 + 写：第二个线程需要等待写锁释放后才能获取
- 读 + 写：写锁需要等待读锁释放后才能加锁成功

****
#### 6.7.5 信号量

在并发编程里，信号量是一种控制并发访问资源数量的机制。它类似一个许可池，每个线程在访问资源前必须申请许可证，用完后归还许可证，如果许可证用光了，后续线程就必须等待，
直到有其他线程释放许可证。而 Redisson 实现了两种分布式信号量：

1、RSemaphore，不带租约时间的信号量

获取到的许可证如果没有主动释放，就会一直占用，适合需要严格手动释放 的场景，比如限流。

```java
// 创建
RSemaphore semaphore = redisson.getSemaphore("mySemaphore");

// 初始化许可证数量
semaphore.trySetPermits(5);

// 获取许可证（阻塞直到成功）
semaphore.acquire();

// 获取许可证（非阻塞）
boolean success = semaphore.tryAcquire();

// 获取多个许可证
semaphore.acquire(3);

// 释放许可证
semaphore.release();

// 释放多个许可证
semaphore.release(2);
```

- trySetPermits(n) 只会在第一次调用时生效，用于初始化信号量值
- acquire() 会阻塞直到获取成功
- tryAcquire() 会立即返回 true/false
- tryAcquire(timeout, unit) 会在超时时间内等待

2、RPermitExpirableSemaphore。带租约时间的信号量

每个许可证都有一个有效期，如果线程崩溃、服务宕机，许可证在租约到期后会自动归还。避免了许可证泄漏的问题，适合不可靠环境，比如分布式微服务。

```java
// 创建
RPermitExpirableSemaphore semaphore = redisson.getPermitExpirableSemaphore("myExpirableSemaphore");

// 初始化
semaphore.trySetPermits(3);

// 获取许可证，返回一个带 ID 的 permit
String permitId = semaphore.acquire();

// 或者带超时等待
String permitId = semaphore.tryAcquire(5, 10, TimeUnit.SECONDS);

// 归还许可证时，需要传入 ID
semaphore.release(permitId);
```

- acquire() 返回的是一个 String permitId
- 每个 permit 有一个 有效期，到期会自动释放
- 必须用 ID 来释放对应的许可证

****
#### 6.7.6 闭锁

闭锁的作用是：设置一个计数器，多个参与方不断把计数减到 0；等待方阻塞在门口，直到计数归零才一起放行。Redisson 提供的是 分布式闭锁，计数存在 Redis 里，多个进程、服务都能共同参与。
典型的就是关门案例。

```java
// 创建
RCountDownLatch latch = redisson.getCountDownLatch("latch:import-batch");

// 仅当当前计数为 0 时，才能设置初始计数，成功返回 true
latch.trySetCount(10);

// 阻塞等待直到计数归零
latch.await();

// 带超时等待，超时返回 false，不再阻塞
boolean ok = latch.await(30, TimeUnit.SECONDS);

// 参与方：把计数减 1（最常用）
latch.countDown();

// 查询剩余计数
long left = latch.getCount();

// 清理 Redis 中的状态（可选）
latch.delete();
```

例如关门案例：

```java
@GetMapping("/lockDoor")
@ResponseBody
public String lockDoor() throws InterruptedException {
    RCountDownLatch latch = redisson.getCountDownLatch("door");
    latch.trySetCount(5);
    latch.await();
    return "放假了";
}

@GetMapping("/gogogo/{id}")
@ResponseBody
public String gogogo(@PathVariable("id") Integer id) {
    RCountDownLatch latch = redisson.getCountDownLatch("door");
    latch.countDown(); // 计数减一
    return id + " 班的人走了";
}
```

****
### 6.8 缓存一致性解决方案

缓存一致性指的是数据库中的数据与缓存中的数据保持一致的状态。而产生该问题的主要原因如下：

1. 先更新数据库，后更新缓存 -> 网络延迟导致缓存更新失败
2. 先更新缓存，后更新数据库 -> 数据库更新失败但缓存已更新
3. 并发读写 -> 多个操作顺序错乱导致数据不一致

解决方案：

1、双写模式

该模式同时更新数据库和缓存，保证两者的强一致性。虽然这种模式的一致性可以得到解决，但是写的性能较低，并且网络问题可能导致数据不一致。

- 线程 A：更新数据库为 valueA，但网络延迟缓存更新慢 
- 线程 B：更新数据库为 valueB，更新缓存为 valueB 
- 线程 A：终于更新缓存为 valueA -> 产生脏数据

这种情况可以使用分布式锁或利用版本号控制来解决。对同一个业务 key（如商品ID）加细粒度分布式写锁，把写库 + 写缓存放到同一临界区。这样 A/B 不会交错执行，谁先拿到锁，
谁就先完整地把库和缓存都更新，后来的再更新一次即可，不会出现后者覆盖的情况；或者每次更新生成单调递增的版本号，写缓存时不直接覆盖，而是检查版本后再写，
只有当本次的版本 >= 缓存中的版本才允许覆盖；否则跳过，这样旧写值不会覆盖新写值。

2、失效模式

该模式则是更新数据库后删除缓存，在下次读取时再重新加载。该模式追求的是一种最终一致的状态（在分布式系统中，强一致性代价极高），但这些失效模式会使系统长时间甚至永久处于不一致状态。
例如先更新数据库再删缓存：

1. 时刻 T1：线程 A 执行写操作，它首先成功更新了数据库（例如，将值从 10 设置为 20）。 
2. 时刻 T2：在线程 A 删除缓存之前，线程 B 执行读操作，它发现缓存失效（或未命中），于是去读取数据库。 
3. 时刻 T3：线程 B 读到了线程 A 更新后的新值（20）。
4. 时刻 T4：线程 B 将读取到的数据 value=20 写入缓存。 
5. 时刻 T5：线程 A 才执行删除缓存的操作。 
6. 最终结果：缓存中被设置为了正确的值 20，一切正常。

但存在：

1. 时刻 T1：线程 A 更新数据库（set 20）。 
2. 时刻 T2：线程 A 删除了缓存（del key）。（注意：这一步提前了） 
3. 时刻 T3：线程 B 来读，缓存未命中，去读数据库。此时数据库可能还未完成事务提交（例如主从同步延迟），或者线程 A 的更新操作还未最终提交，线程 B 读到了旧值 10。 
4. 时刻 T4：线程 B 将 value=10 写入缓存。 
5. 时刻 T5：数据库更新操作最终提交完成。 
6. 最终结果：数据库是 20，但缓存是 10，此时数据不一致了，并且这个不一致会持续到下一次更新或缓存过期。

根本原因就是更新数据库和删除缓存这两个步骤之间存在一个时间窗口，在这个窗口内，另一个读请求可能会读到数据库的中间状态（旧值）并将其重新加载到缓存中。

再例如先删缓存再更新数据库，虽然可以解决上述问题，但仍然存在新的问题：

1. 时刻 T1：线程 A 执行写操作，它首先删除了缓存。
2. 时刻 T2：在线程 A 更新数据库之前，线程 B 来读。发现缓存不存在，去读数据库，读到旧值 10。 
3. 时刻 T3：线程 B 将 value=10 写入缓存。 
4. 时刻 T4：线程 A 才执行更新数据库的操作（set 20）。 
5. 最终结果： 数据库是 20，但缓存是 10，数据不一致。

而这些问题也可以使用分布式锁解决，只允许一个请求去数据库加载数据并回填缓存，其他请求等待。

****
## 7. SpringCache

### 7.1 概述

Spring Cache 并不是一个具体的缓存实现（比如 Redis），而是一个缓存抽象层，它提供了一组统一的注解和 API，允许开发者以声明式的方式使用方法级别的缓存，
而无需关心底层的缓存提供商是啥。通过在方法上添加简单的注解（如 @Cacheable），来定义方法的返回值应该被缓存，以及如何被缓存。它的实现原理是 Spring 的 AOP，
Spring 在运行时为被注解的方法创建代理，由代理来处理缓存的逻辑（检查缓存、调用方法、更新缓存）。

1、引入依赖

```xml
<!-- Spring Cache-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<!-- Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

2、启用缓存

在 Spring Boot 的主配置类上添加 @EnableCaching 注解，这是必须的一步，它告诉 Spring 开启对注解式缓存的支持。

3、配置缓存提供商

```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    password: 123
  cache:
    type: redis # 明确指定使用redis作为缓存实现
    redis:
      time-to-live: 3600000 # 缓存 ttl，单位毫秒
```

4、在方法上使用 @Cacheable 注解

如果缓存存在，则使用缓存；否则执行方法，并将结果存入缓存：

```java
@Cacheable(value = "category", value = "'Level1Categories'")
@Override
public List<CategoryEntity> getLevel1Categories() {
    System.out.println("getLevel1Categories...");
    return categoryDao.selectList(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, 0));
}
```

在查询一级分类的方法上添加注解并指定一个工作区（文件夹），然后 key 的名称为 Level1Categories。上面有提到，Spring Cache 的功能（检查缓存、存入缓存等）并不是直接修改方法代码来实现的，
而是通过 Spring AOP 技术实现的，Spring 在启动时，会寻找并发现的 CategoryService Bean 上有些方法有 @Cacheable 注解，它就会创建一个这个类的代理对象，
然后把这个代理对象作为 CategoryService Bean 注册到容器里，所以如果使用 @Cacheable 注解的那个方法调用了本类中的方法，即使用了 this. ，那么就会跳过缓存逻辑，
导致该注解失效。

****
### 7.2 自定义缓存配置

CacheAutoConfiguration 是 Spring Boot 缓存自动配置的总入口，当引入了相关缓存的依赖并在 application 文件中配置了 spring.cache.type=redis，那么这个自动配置类就会生效。
在这个配置类中有个方法：

```java
static class CacheConfigurationImportSelector implements ImportSelector {
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        CacheType[] types = CacheType.values();
        String[] imports = new String[types.length];
        for(int i = 0; i < types.length; ++i) {
            imports[i] = CacheConfigurations.getConfigurationClass(types[i]);
        }
        return imports;
    }
}
```

这个方法就是用来获取所有的缓存自动配置类，进入 getConfigurationClass()：

```java
static String getConfigurationClass(CacheType cacheType) {
    String configurationClassName = (String)MAPPINGS.get(cacheType);
    Assert.state(configurationClassName != null, () -> "Unknown cache type " + cacheType);
    return configurationClassName;
}
```

而 MAPPINGS 在这个类初始化时就会加载：

```java
private static final Map<CacheType, String> MAPPINGS;

static {
    Map<CacheType, String> mappings = new EnumMap(CacheType.class);
    mappings.put(CacheType.GENERIC, GenericCacheConfiguration.class.getName());
    mappings.put(CacheType.EHCACHE, EhCacheCacheConfiguration.class.getName());
    mappings.put(CacheType.HAZELCAST, HazelcastCacheConfiguration.class.getName());
    mappings.put(CacheType.INFINISPAN, InfinispanCacheConfiguration.class.getName());
    mappings.put(CacheType.JCACHE, JCacheCacheConfiguration.class.getName());
    mappings.put(CacheType.COUCHBASE, CouchbaseCacheConfiguration.class.getName());
    mappings.put(CacheType.REDIS, RedisCacheConfiguration.class.getName());
    mappings.put(CacheType.CAFFEINE, CaffeineCacheConfiguration.class.getName());
    mappings.put(CacheType.SIMPLE, SimpleCacheConfiguration.class.getName());
    mappings.put(CacheType.NONE, NoOpCacheConfiguration.class.getName());
    MAPPINGS = Collections.unmodifiableMap(mappings);
}
```

而这里面就有 RedisCacheConfiguration 自动配置类，进入看看，它里面有个 RedisCacheManager Bean，这个 Bean 就是用来负责所有缓存分区的创建和管理。而在这个配置类中，
也对缓存的相关数据进行初始化，如果在配置文件中配置了，那么就用配置文件的，否则就用默认的：

```java
private org.springframework.data.redis.cache.RedisCacheConfiguration createConfiguration(CacheProperties cacheProperties, ClassLoader classLoader) {
    CacheProperties.Redis redisProperties = cacheProperties.getRedis();
    org.springframework.data.redis.cache.RedisCacheConfiguration config = org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig();
    config = config.serializeValuesWith(SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(classLoader)));
    if (redisProperties.getTimeToLive() != null) {
        config = config.entryTtl(redisProperties.getTimeToLive());
    }
    if (redisProperties.getKeyPrefix() != null) {
        config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
    }
    if (!redisProperties.isCacheNullValues()) {
        config = config.disableCachingNullValues();
    }
    if (!redisProperties.isUseKeyPrefix()) {
        config = config.disableKeyPrefix();
    }
    return config;
}
```

所以自定义一个 RedisCacheConfiguration Bean 后，Spring 在初始化时会发现这个由用户提供的、更高优先级的 Bean，从而放弃使用自动配置类内部的默认配置。

这里自定义一个 Redis 缓存的配置类：

```java
@EnableConfigurationProperties(CacheProperties.class)
@Configuration
@EnableCaching
public class MyCacheConfig {

    @Autowired
    private CacheProperties cacheProperties;

    @Bean
    RedisCacheConfiguration redisCacheConfiguration() {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        // 让配置文件中的数据生效
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }

        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }

        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }

        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }
}
```

参照官方的写法，先 new 一个默认的 RedisCacheConfiguration，然后通过赋值的方式进行自定义配置。需要注意的是：没有引入 CacheProperties 使它生效的话，那配置文件中的内容就无法生效，
CacheProperties.class 是 Spring Boot 官方提供的一个配置属性类，专门用来绑定配置文件中以 spring.cache 开头的所有属性，也就是正好和配置文件中的属性对应，
在 CacheAutoConfiguration 中也使用这个 @EnableConfigurationProperties({CacheProperties.class})。

****
### 7.3 @CacheEvict

@CacheEvict 的核心目的是在方法执行后（或前）清除一个或多个缓存条目。当数据库中的数据发生变化时（如更新、删除操作），缓存中旧的、失效的数据就必须被清理掉，
否则后续的读取操作将会拿到错误的数据。@CacheEvict 就是用来完成这个清理任务的，即失效模式中使用。

清除 books 缓存分区中的数据，通常需要指定要清除的缓存条目的唯一键，如果不指定，默认使用所有方法参数组合生成的键，然后删除这个键对应的缓存。

```java
@CacheEvict(value = "books")
public void deleteBookById(Long id) {
    // ... 删除逻辑
}
```

如果需要删除某个缓存分区下的多个缓存，则可以使用 allEntries = true，但该属性和 key 属性是互斥的，不能同时指定。

```java
@CacheEvict(cacheNames = "books", allEntries = true)
public void reloadAllBooks() {
    // ... 重新加载所有书籍的逻辑，可能涉及大量更新
}
```

beforeInvocation 是用来决定清除操作执行的时机：

- false (默认)：在方法成功执行之后执行清除。如果方法执行抛出异常，则清除操作不会进行
- true：在方法执行之前执行清除，无论方法成功还是抛出异常，缓存都会被清除

如果希望无论方法执行成功与否，都必须清除掉可能脏的缓存时。例如，一个删除操作：即使删除本身失败了（比如要删除的资源不存在），仍然希望清除缓存，
因为缓存中的数据可能本身就是无效的或已经被别的方式修改了。

```java
// 无论删除是否成功，都先清除缓存
@CacheEvict(cacheNames = "books", key = "#id", beforeInvocation = true)
public void deleteBookById(Long id) {
    // ... 删除操作
}
```

condition 是一个 SpEL 表达式，用于条件判断。只有当表达式求值为 true 时，才会执行缓存清除操作。

```java
// 只有当 id 大于 10 时才清除缓存
@CacheEvict(cacheNames = "books", key = "#id", condition = "#id > 10")
public void updateBook(Long id, Book book) {
    // ...
}
```

****
### 7.4 SpringCache 解决缓存问题

在上面有提到缓存数据存在三个问题：缓存穿透、缓存击穿、缓存雪崩，而 SpringCache 也有对应的方发来解决这些问题。

#### 7.4.1 解决缓存穿透

缓存穿透是指查询一个根本不存在的数据，这个数据在缓存和数据库中都不存在，导致每次请求都会直接打到数据库上，给数据库带来巨大压力。例如 @Cacheable 的 unless 属性：

```java
@Cacheable(cacheNames = "users", key = "#id", 
           unless = "#result == null") // 当结果为 null 时不缓存
public User findUserById(Long id) {
    return userRepository.findById(id).orElse(null);
}
```

第一次查询不存在的 ID，会访问数据库，得到 null，由于 unless 条件，这个 null 不会被缓存，后续相同的请求依然会访问数据库。这并没有完全解决穿透，但防止了缓存被大量的无意义空值占满。
如果需要缓存空值来防止大量请求查询数据库的话，就可以在配置文件中配置 spring.cache.redis.cache-null-values=true。为了完全解决，通常需要与布隆过滤器配合，
或者在缓存中显式地设置一个短暂的空值（例如缓存 5 分钟的 NULL），而 Spring Cache 允许通过自定义 CacheManager 或 Cache 实现来达成后者。

在自定义配置类前先了解一下 RedisCache 里方法的调用时机，核心触发点为 @Cacheable 注解，当调用使用该注解的方法时，Spring 的缓存拦截器会介入：

1. Spring 会根据 @Cacheable 的 key 属性和缓存名称 value="category" 生成一个唯一的 Redis 键，例如 category::123。
2. 调用 lookup(key)

   - Spring 会调用 cacheManager.getCache("category") 拿到自定义的 RedisCache 实例，然后调用其 lookup 方法（最终会执行 Redis 的 GET 命令）。
   - 在执行业务方法之前会检查缓存中是否已存在所需数据，这是一个读操作

3. 调用 put(key, value)

   - 如果第 2 步 lookup 返回 null（代表缓存未命中），Spring 拦截器就会去查询数据库，拿到结果后，它会调用 RedisCache 的 put 方法，将结果缓存起来。
   - 它会判断要缓存的值 value 是否是 null，是 null 则抛出异常
   - 该操作是在执行业务方法之后（缓存未命中时）执行的，目的是将业务方法的执行结果存储到缓存中，这是一个写操作

所以可以从重写 put 方法这里入手解决缓存空值的问题，步骤如下：

1、创建一个特殊的、可序列化的标记对象，用来在 Redis 中代表 null

```java
public class NullValue implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final NullValue INSTANCE = new NullValue();

    private NullValue() {}

    public String getType() { // 添加 getter
      return "NullValue";
    }
}
```

这个类用来区分“缓存未命中”和“缓存了空值”的情况，如果直接在 Redis 里存一个普通的空字符串 "" 或者字符串 "null"，在反序列化时，就无法确定这到底是业务数据本身就是一个空字符串，
还是缓存的 null 结果。因此使用一个独一无二的 NullValue 对象可以明了地进行区分。而 Redis 存储的是二进制数据（byte[]），
所以需要一个实现了 Serializable 接口的对象才能被正确地序列化和反序列化。

不过已经有自定义一个 RedisCacheConfiguration，里面配置了缓存数据的序列化与反序列化器，而 Jackson 序列化对象的规则：

- 默认只会序列化有 getter 属性或字段的类
- Jackson 默认配置 FAIL_ON_EMPTY_BEANS=true，如果一个类没有可序列化的属性，即空对象，那序列化就会报 InvalidDefinitionException

所以在设置空对象时需要提供可以被序列化的属性，即一个 getter 方法，让它能序列化。

2、写一个 CustomRedisCache 类来自定义缓存逻辑的核心

```java
public class CustomRedisCache extends RedisCache {
    private final RedisCacheWriter cacheWriter;
    private final RedisCacheConfiguration cacheConfig;
    private final Duration nullValueTtl;
    // 构造方法，完成初始化
    public CustomRedisCache(String name, // 缓存的唯一标识符，对应 Redis 中的 key 前缀
                            RedisCacheWriter cacheWriter, // 真正执行 Redis 读写操作的核心组件
                            RedisCacheConfiguration cacheConfig, // 缓存的配置信息容器，包含所有序列化和行为配置
                            Duration nullValueTtl // 专门为 null 值设置的独立过期时间
    ) { 
        super(name, cacheWriter, cacheConfig);
        this.cacheWriter = cacheWriter;
        this.cacheConfig = cacheConfig;
        this.nullValueTtl = nullValueTtl;
    }

    @Override
    public void put(Object key, Object value) {
        if (value == null) {
            // 如果是 null，缓存 NullValue 占位符
            byte[] cacheKey = this.serializeCacheKey(key.toString());
            byte[] cacheValue = this.serializeCacheValue(NullValue.INSTANCE);
            cacheWriter.put(getName(), cacheKey, cacheValue, nullValueTtl);
        } else {
            super.put(key, value);
        }
    }

    @Override
    protected Object lookup(Object key) {
        Object value = super.lookup(key);
        if (value instanceof NullValue) {
            return null;
        }
        return value;
    }
}
```

2.1、提供构造方法

CustomRedisCache 继承自 RedisCache，调用 super() 是为了正确初始化父类的状态，父类 RedisCache 的构造方法会设置缓存名称（name）、初始化缓存写入器（cacheWriter）、
配置缓存序列化器等组件以及完成其他必要的初始化工作。如果不调用 super()，就需要在子类中重新实现父类的所有初始化逻辑，这是重复且容易出错的。
所以为了确保子类与父类具有相同的行为基础，需要写上一个 super()。


2.2、在默认的 RedisCache.put() 方法中，如果传入的 value 是 null，它不会执行 SET 操作：

```java
public void put(Object key, @Nullable Object value) {
    Object cacheValue = this.preProcessCacheValue(value);
    if (!this.isAllowNullValues() && cacheValue == null) {
        throw new IllegalArgumentException(String.format("Cache '%s' does not allow 'null' values. Avoid storing null via '@Cacheable(unless=\"#result == null\")' or configure RedisCache to allow 'null' via RedisCacheConfiguration.", this.name));
    } else {
        this.cacheWriter.put(this.name, this.createAndConvertCacheKey(key), this.serializeCacheValue(cacheValue), this.cacheConfig.getTtl());
    }
}
```

但当前的目的是即使数据不存在，也要用一个占位符把坑占住，告诉后续的请求“这个 key 已经查过了，没有数据，别再查数据库了”，因此，必须重写 put 方法来覆盖框架的这个默认行为，
将 null 视为一个需要被特殊缓存的值。重写 put 方法后可以将防空穿透的细节封装在缓存层，而对上层的业务代码完全透明。即：

1. 没有自定义 put 的情况，业务代码需要自己处理空值

```java
@Cacheable(value = "users")
public User getUserById(Long id) {
    User user = userDao.findById(id);
    if (user == null) {
        // 业务层需要知道缓存穿透，并做一些特殊操作
    }
    return user;
}
```

2. 有自定义 put 的情况，业务代码可以只关心业务，不需要考虑缓存层的空值问题

```java
@Cacheable(value = "users") // 只需一个注解，其他不用管
public User getUserById(Long id) {
    return userDao.findById(id); // 直接返回，哪怕是 null
}
```

并且在处理空值缓存的情况时，正常的数据缓存和空值缓存的失效策略应该是不同的，也就是它们应该使用不通的 TTL，但在配置文件中只能统一设置。而正常数据 TTL 可能较长，
但空值数据的 TTL 应该较短，因为缓存只是临时防护手段，没必要长期占用内存；其次较短的 TTL 使得缓存能更快地自动失效，从而有机会重新查询数据库获取新值。
通过重写 put 方法，可以为空值单独指定一个 TTL 而不影响全局的缓存配置，这在默认的缓存实现中是无法做到的。

```java
@Override
public void put(Object key, Object value) {
    if (value == null) {
        // 如果是 null，缓存 NullValue 占位符
        byte[] cacheKey = this.serializeCacheKey(key.toString()); // 把字符串 key 序列化成 Redis 可存储的字节数组（byte[]）
        byte[] cacheValue = this.serializeCacheValue(NullValue.INSTANCE);
        cacheWriter.put(getName(), cacheKey, cacheValue, nullValueTtl);
    } else {
        super.put(key, value);
    }
}
```

2.3、重写 lookup 方法

重写完 put() 方法其实就已经解决了缓存穿透的问题了，但是为了完成存入 NullValue，读出 null 的闭环，就也需要重写一下 lookup()。当从缓存中查询数据时，lookup 方法会被调用。
它首先通过父类方法从 Redis 中拿到数据（此时可能是一个业务对象，也可能是之前存的 NullValue 实例）。如果发现取到的值是 NullValue 类型，
它就明白这代表的是一个数据库中的 null 结果，于是向调用方（Service 层代码）返回 Java 中的 null。对于业务代码来说，它完全感知不到底层用了 NullValue 这个占位符，
它只知道查到了数据或者没查到数据（返回null），整个过程是透明的。

```java
@Override
protected Object lookup(Object key) {
    Object value = super.lookup(key);
    if (value instanceof NullValue) {
        return null;
    }
    return value;
}
```

3、CacheManager 配置，组装自定义组件

使用这个的目的就是让 Spring 使用自定义的 CustomRedisCache 来代替默认的 RedisCache，通过匿名内部类重写了 RedisCacheManager 的 createRedisCache 工厂方法，
这样每当 Spring 需要为一个缓存分区（如 @Cacheable(value = "product")中的 "product"）创建 Cache 实例时，它创建的就是 CustomRedisCache，而不是原来的 RedisCache。
并且这里将 null 值的 TTL（5分钟）与全局缓存的 TTL 分开了。

```java
@Bean
public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
    RedisCacheConfiguration config = redisCacheConfiguration();
    // null 缓存 TTL（可配置化，先写死 5 分钟）
    Duration nullValueTtl = Duration.ofMinutes(5);
    return new RedisCacheManager(writer, config) {
        @Override
        protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
            return new CustomRedisCache(name, writer, cacheConfig, nullValueTtl);
        }
    };
}
```

```json
{
  "@class": "com.project.gulimall.product.config.NullValue",
  "type": "NullValue"
}
```

不过由于之前重写过 RedisConfiguration 配置类，所以为了让自定义的 CustomRedisCache 能够使用自定义的配置信息，就需要对原有的代码进行一些修改：

```java
@Override
public void put(Object key, Object value) {
    String cacheName = getName();
    byte[] cacheKey = serializeCacheKey(key.toString());
    byte[] cacheValue;
    Duration ttl;

    if (value == null) {
        // 处理null值
        cacheValue = serializeCacheValue(NullValue.INSTANCE);
        ttl = nullValueTtl;
        System.out.println("缓存空值: " + key + ", TTL: " + ttl);
    } else {
        // 处理正常值
        cacheValue = serializeCacheValue(value);
        ttl = normalValueTtl; // 使用显式存储的TTL
        System.out.println("缓存正常值: " + key + ", TTL: " + ttl);
    }

    // 确保TTL不为null或负数
    if (ttl == null || ttl.isNegative() || ttl.isZero()) {
        ttl = Duration.ofMinutes(10); // 默认值
    }

    cacheWriter.put(cacheName, cacheKey, cacheValue, ttl);
}
```

其实这个 put 方法可以不用修改也能使用自定义的配置，因为在创建 CacheManager 时就已经使用了自定义的配置，这里的 redisCacheConfiguration() 就是自定义的，
不过为了初期好理解，还是修改了一下。

```java
@Bean
@Primary
public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
    RedisCacheConfiguration redisCacheConfiguration = redisCacheConfiguration();
    // null 缓存 TTL（可配置化，先写死 5 分钟）
    Duration nullValueTtl = Duration.ofMinutes(5);

    return new RedisCacheManager(writer, redisCacheConfiguration) {
        @Override
        protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
            return new CustomRedisCache(name, writer, cacheConfig, nullValueTtl);
        }
    };
}
```

基本的配置都完成了，但是目前的 null 缓存的 TTL 是写死在代码里的，这不利于后期的修改，像这种属性应该把它写进配置文件中，不过 Redis 的配置中并没有该设置，所以只能自定义：

```yaml
spring:
  cache:
    redis:
      null-value-ttl: 2m # 自定义 null 占位 TTL
```

在需要用到该属性的配置类中使用：

```java
// 扫描配置文件，如果没有找到则用默认值 5m
@Value("${spring.cache.redis.null-value-ttl:5m}")
private Duration nullValueTtl;
```

****
#### 7.4.2 解决缓存击穿

缓存击穿是指某个热点 key 在过期的瞬间，同时有大量的请求进来，导致所有请求都无法从缓存中拿到数据，从而全部并发地打到数据库上。但标准的 @Cacheable 注解在缓存未命中时，
会允许多个线程同时执行方法体去数据库查询，但 Spring Cache 抽象层为了保持通用性，没有在注解层面提供锁的配置，解决击穿通常需要引入分布式锁并在业务代码中显式处理。

****
#### 7.4.3 解决缓存雪崩

缓存雪崩是指缓存中大量的 key 在同一时间过期，导致所有这些数据的请求瞬间都打到数据库上，引起数据库压力激增甚至崩溃。同样的，SpringCache 也没有提供方法解决，但可以通过简单的配置来有效避免。
为缓存设置不同的过期时间，这是最有效的方法：

```java
@Configuration
public class MyCacheConfig {
    // 定义不同缓存的TTL
    private Map<String, Duration> cacheTtls = Map.of(
        "category", Duration.ofHours(6), // 分类缓存6小时
        "product", Duration.ofMinutes(30), // 商品缓存30分钟
        "brand", Duration.ofHours(2), // 品牌缓存2小时
        "attr", Duration.ofHours(4) // 属性缓存4小时
    );
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        RedisCacheConfiguration defaultConfig = redisCacheConfiguration();
        return new RedisCacheManager(writer, defaultConfig) {
            @Override
            protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
                // 为特定缓存创建自定义配置
                Duration specificTtl = cacheTtls.getOrDefault(name, cacheConfig.getTtl()); // 如果 cacheTtls 中有对应缓存名，则使用自定义 TTL
                RedisCacheConfiguration specificConfig = cacheConfig.entryTtl(specificTtl); // 返回一个新的 RedisCacheConfiguration 实例，并覆盖默认 TTL
                return new CustomRedisCache(name, writer, specificConfig, nullValueTtl);
            }
        };
    }
}
```

****
# 六、检索服务 

## 1. 搭建页面环境

### 1.1 自定义域名搭配 nginx 反向代理

之前有记录过通过 nginx 反向代理到网关，不过当时是用 localhost 来进行代理转发的，现在利用本机的 hosts 来进行自定义域名，通过该域名来访问 nginx 并进行反向代理。
Hosts 文件可以把域名映射到某个 IP，通常在 C:\Windows\System32\drivers\etc\hosts，不过需要用管理员身份打开：

```text
172.23.0.1 gulimall.com
```

例如像这样配置，将 wsl2 的 IP 映射为 gulimall.com，当访问 http://gulimall.com 时就相当于访问本机的 172.23.0.1。此时再配置 nginx 的反向代理，
就可以通过 gulimall.com 访问到本机的 88 端口，也就是配置的网关的端口，然后再由网关转发到各个服务。

```nginx
server {
    listen       80;
    listen  [::]:80;
    server_name  *.gulimall.com;

    location /static/ {
        root    /usr/share/nginx/html;
    }
    
    location / {
        proxy_set_header Host $host;
        proxy_pass http://host.docker.internal:88;
    }
}
```

这里不直接使用 localhost 是因为后续会有多个不通的服务经过 nginx 的反向代理，它们都需要经过网关的负载均衡与转发，如果使用 localhost 的话就需要配置多个请求路径来进行区分，
因为不同的服务都处于不通的端口，使用 localhost 的话每次添加新服务都需要修改 nginx 路径映射，配置不够灵活。如果直接用自定义的域名的话就较为便捷一点，因为使用域名后，
请求头中携带的 Host 就会变成该域名，在网关中对域名进行筛选并转发到对应的服务，例如：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: nginx_search_route
          uri: lb://gulimall-search
          predicates:
            - Host=search.gulimall.com

        - id: nginx_host_route
          uri: lb://gulimall-product
          predicates:
            - Host=gulimall.com
```

然后配置静态资源，在 /nginx/html/static/ 下新建一个 search 目录，然后把静态资源拷贝进这个目录，因为已经配置了反向代理到该目录，所以可以直接使用：

```shell
cp -r /mnt/d/docker_dataMountDirectory/gmall-static-resource/search/. /nginx/html/static/search
```

****
### 1.2 本地 hosts 失效原因

通过判断发送请求的 Host 来进行区分就较为方便，不用考虑路径的问题了，不过需要在 nginx 的文件中添加上 proxy_set_header Host $host;因为只有写了这个，
nginx 才能将原始 Host 转发给网关，因为 nginx 默认是不携带 Host 的。

需要注意的是：Hosts 文件是操作系统最底层的 DNS 解析方式，当访问一个域名时，操作系统会先查 hosts 文件，看它里面是否有匹配条目，如果有，就直接返回对应 IP，
如果没有再通过 DNS 服务器解析。所以上面的配置中访问 http://gulimall.com 就会直接使用 172.23.0.1（wsl2 IP），再经由 nginx 反向代理到 gulimall-product 服务。
但开启 VPN 后会发生路由和 DNS 被 VPN 接管的情况，因为 VPN 会强制使用远程 DNS 来覆盖本地 DNS 设置（正向代理），所以本地的 hosts 文件会被绕过，并且某些 VPN 会阻止本地 DNS 查询。

而 DNS 在访问互联网中的作用就是进行域名解析，平常访问网址时浏览器并不知道 IP，系统通过 DNS 把域名解析成 IP，然后连接对应的服务器。VPN 则会提供它自己的 DNS 服务器，
通过 VPN DNS，所有域名解析结果都是海外可访问的 IP（可以正确解析被屏蔽网站的 IP），这就是科学上网的关键，也是本地　DNS　失效的原因。

****
## 2. 检索查询

### 2.1 查询参数模型

前端在搜索页面发起请求时会携带很多查询条件，往往这些条件都是直接传递给后端的，如果用一个参数接收一种条件，那么在 Controller 层就需要接收很多个字段，不如把它封装成一个对象，
利用 SpringMVC 的特点自动匹配字段，这样就可以简化接收参数列表了。后端拿到这个对象之后，会根据里面的字段拼接 Elasticsearch DSL 语句（或者 SQL），再去执行查询，
最终返回结果。而本案例选择封装一个 VO 对象取名 SearchParam：

```java
/**
 * 封装页面可能传递的所有查询条件
 */
@Data
public class SearchParam {
    // 全文匹配关键字
    private String keyword;
    // 三级分类 id
    private Long catalog3Id;
    /**
     * sort = saleCount_asc/desc，销量排序
     * sort = skuPrice_asc/desc，价格排序
     * sort = hotScore_asc/desc，综合排序（热度评分）
     */
    private String sort; // 排序条件
    /**
     * 过滤条件
     *   hasStock（是否有货）、skuPrice 区间、brandId、catalog3Id、attrs
     *   hasStock = 0/1
     *   skuPrice = 1_500/_500/500_（1 - 500/500 以内/大于 500）
     */
    private Integer hasStock; // 是否只显示有货
    private String skuPrice; // 价格区间
    private List<Long> brandId; // 按品牌筛选（品牌 Id），允许多选
    private List<String> attrs; // 按属性筛选
    private Integer pageNum; // 页码
}
```

1、全文匹配关键字

用户在搜索框输入的内容，比如 "华为手机"、"轻薄笔记本"，后端通常会在商品标题、商品描述等建立分词索引，通过 match 或 multi_match 查询。如果 keyword == null，
就表示用户是通过筛选条件进入的分类页，而不是直接搜索。

2、分类筛选

表示三级分类 ID（本电商系统有三级分类，例如：一级：手机数码 -> 二级：手机通讯 -> 三级：手机）。如果用户点击了分类菜单（三级分类），这个参数就会有值。
检索时可以直接用 term 或者 filter 限定 catalog3Id 字段。

3、排序条件

在完成初步检索后，商城页面往往会有一个排序的按钮，里面可以有多种排序条件，常见的就是销量、价格与热度，而本模型的约定格式为排序字段_asc/desc（升序/降序），例如：

- saleCount_asc/desc：按销量升序/降序 
- skuPrice_asc/desc：按价格升序/降序 
- hotScore_asc/desc：按热度评分升序/降序

解析时就可以：

```java
if(sort != null) {
   String[] parts = sort.split("_"); 
   String field = parts[0]; // saleCount, skuPrice, hotScore
   String order = parts[1]; // asc / desc
   // 构建排序条件
}
```

4、库存过滤

这个功能就是用来显示有货商品或者全部（含无货）商品，一般用 1 显示有货商品，0 显示全部。后端映射到 es 时，会有个布尔字段 hasStock，可以用 term 过滤。

5、价格区间

这个就是很常见的一个检索方式了，通过设置不通的价格展示对应的商品，而本模型选择的表达方式用 "_" 代替大小号：

- "1_500"：价格在 1 到 500 之间
- "_500"：价格小于等于 500
- "500_"：价格大于等于 500

6、品牌筛选

用户可能勾选多个品牌（如华为、小米、OPPO），所以用 List 接收，查询时就是 terms 过滤。

7、属性筛选

电商商品有很多规格参数，例如："2:6.5寸"，代表属性 ID = 2（屏幕大小），值 = 6.5寸；"3:8G"，代表属性 ID = 3（运行内存），值 = 8G，不过这里并没有选择使用 Map<Long, String> 来接受，
而是使用的 List<String>，因为这样前端传参时更灵活，可以用 URL 方式：

```text
/list.html?attrs=2:6.5寸&attrs=3:8G
```

8、分页参数

后端一般还会配合一个 pageSize（每页显示多少条），因为电商场景里一个分类下可能有成千上万件商品，如果不分页，一次性把所有商品都查出来返回给前端，那系统性能就会收到影响，
通过传递分页参数来限制查询的数据条数，控制性能。

****
### 2.2 返回结果模型

同样的，有请求参数模型，也就需要有返回结果模型，就是对商城搜索结果的返回数据的封装，它跟上面的 SearchParam 是一对，后端根据条件查完 Elasticsearch（或数据库），
把结果按结构封装好，返回给前端页面渲染。

```java
@Data
public class SearchResult {
    /**
     * 商品信息
     */
    private List<SkuEsModel> products; // 查询到的所有商品信息
    /**
     * 分页信息
     */
    private Integer pageNum; // 当前页码
    private Long total; // 总记录数
    private Integer totalPages; // 总页码

    private List<BrandVo> brands; // 当前查询到的结果，所涉及到的所有品牌

    private List<AttrVo> attrs; // 当前查询到的结果，所涉及到的所有属性

    private List<CatalogVo> catalogs; // 当前查询到的结果，所涉及到的所有分类

    @Data
    private static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    private static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    private static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }
}
```

1、商品信息部分

该字段用来存放搜索到的商品集合，也就是之前封装过的 es 模型（SkuEsModel），前端会用这个 list 渲染商品卡片列表，同时它也是搜索结果的核心数据。

2、分页信息部分

这些字段是配合前端分页条用的，例如：搜索结果总共有 500 件商品，每页显示 20 条，那么 total = 500，totalPages = 25。前端据此渲染分页按钮，比如：

```text
<< 上一页   1  2  3 ... 25   下一页 >>
```

3、结果聚合部分

电商搜索的特色就是除了商品列表，还要显示品牌、属性、分类等过滤条件，方便用户二次筛选。封装一个品牌列表来封装当前结果涉及到的所有品牌，前端通常在侧边栏或顶部展示，
方便用户进一步勾选，里面包含 id（查询条件）、品牌名和品牌图片，有了 brandImg，前端可以显示图片，比如“华为、苹果、小米”。

而检索结果中涉及到的所有商品规格参数，比如“内存”、“屏幕大小”、“颜色”，它们可以作为前端渲染的多选过滤条件，但这些数据必须和已经进行筛选的条件相关联，也就是展示对应的规格参数，
例如选择了华为后，就要显示华为的所有规格参数。

而当前搜索结果涉及到的所有分类（通常是三级分类），就是便于前端在搜索结果页顶部显示“分类导航”。

****
### 2.3 检索 DSL 查询部分

在通过后端进行 es 查询前，先手动写一下 es 的查询语句，这样后续对照着写 Java 代码较为方便。对于电商的查询来说，它是一个复合布尔查询，它应该结合全文搜索、精确过滤、
嵌套对象查询、范围查询，并且需要包含排序、分页和高亮功能（高亮全文搜索的关键词）。而查询的结构大致应该为：

```json
{
  "query": {
    "bool": {
      "must": [ ... ],
      "filter": [ ... ]
    }
  }
}
```

因为 bool 允许组合多个查询子句，而不同的子句类型（must, should, must_not, filter）有不同的逻辑和行为。在电商系统中，用户通常会输入关键词，同时还会选择分类、品牌、价格区间、库存情况等过滤条件。
如果不用 bool 就没法在一个请求里同时把 “关键词匹配” + “属性过滤” + “价格区间” + “库存条件” 结合起来。所以 bool 查询就是逻辑拼接器，它有 4 种子句：

- must：条件必须成立（相当于 sql 里的 and） 
- should：条件可以成立也可以不成立，如果成立会提高相关性得分（相当于 sql 的 or） 
- must_not：条件必须不成立（相当于 sql 的 not） 
- filter：条件必须成立，但不会影响相关性算分（类似 where 里的过滤，不计算得分，更快）

当然为了性能和相关性评分的优化，需要区分 must 和 filter：

- must：子句必须出现在匹配的文档中，并且会影响文档的相关性得分（_score），在全文搜索时得分高的文档（匹配度更高的）排名会更靠前
- filter：子句必须出现在匹配的文档中，但不会影响得分，而且，es 会自动缓存常用的过滤器子句，极大地提升了查询性能。对于精确匹配、范围查询等不需要相关性计算的场景，都应该放在 filter 中

前置条件解析完，就需要开始编写检索条件：

1、全文搜索

使用 match 查询进行全文检索，它会对查询文本进行分词，然后在设定的 field 中查找包含该词的文档，例如找到所有商品标题中包含 “华为” 这个词的商品：

```json
"query": {
  "bool": {
    "must": [
      {
        "match": {
          "skuTitle": "华为"
        }
      }
    ]
  }
}
```

2、精确过滤

通过上面的全文检索后可以查询出大致的商品，但还需要用到更精细的过滤器，用于快速的缩小范围。

1. 按分类 ID 过滤

```json
{
  "term": {
    "catalogId": "225"
  }
}
```

term 查询用于精确值匹配，它不会对查询值 “225” 进行分词，而是直接去倒排索引中查找完全匹配的文档，通常 catalogId 这种标识符会被定义为 keyword 类型以确保精确匹配。

2. 按多个品牌 ID 过滤

```json
{
  "terms": {
    "brandId": ["1", "2", "16"]
  }
}
```

terms 是 term 的复数形式，用于匹配多个精确值，相当于 sql 中的 brandId in ('1', '2', '16')，只要有符合条件的就查询出来。

3. 按是否有库存过滤

```json
{
  "term": {
    "hasStock": {
      "value": "true"
    }
  }
}
```

只显示有库存 (hasStock: true) 的商品，这也是精确匹配。

4. 按价格区间过滤

```json
{
  "range": {
    "skuPrice": {
      "gte": 0,
      "lte": 6500
    }
  }
}
```

使用 range 匹配数值在某个范围内的文档，这里是查询价格在 0 到 6500 之间的商品。

5. 按属性筛选

```json
{
  "nested": {
    "path": "attrs",       // 1. 指定嵌套对象的路径
    "query": {             // 2. 定义在嵌套文档上执行的查询
      "bool": {
        "must": [          // 3. 嵌套文档必须同时满足两个条件
          {
            "term": {
              "attrs.attrId": { // 4. 条件一：属性 ID 必须为 15（例如“CPU型号”）
                "value": "15"
              }
            }
          },
          {
            "terms": {
              "attrs.attrValue": [ // 5. 条件二：属性值必须是“骁龙665”或“高通(Qualcomm)”
                "骁龙665",
                "高通(Qualcomm)"
              ]
            }
          }
        ]
      }
    }
  }
}
```

之前有记录过 es 的扁平化特性，所以对于嵌套的对象属性对它定义为 nested 类型，所以进行查询时也需要用 nested 查询，然后再进行嵌套查询。

5. 排序、分页、高亮

```json
"sort": [ { "skuPrice": { "order": "desc" } } ],
"from": 0,
"size": 20,
"highlight": {
  "fields": { "skuTitle": {} }, // 要高亮的字段
  "pre_tags": "<b style='colro:red'>", // 自定义高亮前缀标签
  "post_tags": "</b>" // 自定义高亮后缀标签
}
```

这些东西就不属于需要过滤的属性了，所以不用写在 filter 里面。

整体 es 查询语句代码：

```json
GET product/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "skuTitle": "华为"
          }
        }
      ],
      "filter": [
        {
          "term": {
            "catalogId": "225"
          }
        },
        {
          "terms": {
            "brandId": [
              "1",
              "2",
              "16"
            ]
          }
        },
        {
          "nested": {
            "path": "attrs",
            "query": {
              "bool": {
                "must": [
                  {
                    "term": {
                      "attrs.attrId": {
                        "value": "15"
                      }
                    }
                  },
                  {
                    "terms": {
                      "attrs.attrValue": [
                        "骁龙665",
                        "高通(Qualcomm)"
                      ]
                    }
                  }
                ]
              }
            }
          }
        },
        {
          "term": {
            "hasStock": {
              "value": "true"
            }
          }
        },
        {
          "range": {
            "skuPrice": {
              "gte": 0,
              "lte": 6500
            }
          }
        }
      ]
    }
  },
  "sort": [
    {
      "skuPrice": {
        "order": "desc"
      }
    }
  ],
  "from": 0,
  "size": 20,
  "highlight": {
    "fields": {
      "skuTitle": {}
    }, 
    "pre_tags": "<b style='colro:red'>",
    "post_tags": "</b>"
  }
}
```

当然这只是一部分的查询，具体的查询还需要根据查询出的东西动态显示，这种动态查询就需要添加聚合（brands/attrs/catalogs 的聚合 buckets）。

****
### 2.4 聚合检索

当初步筛选出商品后，还需要对更加精细的分类条件进行聚合筛选，具体包括价格、系统、尺寸等商品的规格参数，这些都是根据查出的商品动态展示的，并不是写死的。目前需要动态展示的有：
品牌、分类以及规格属性，所以 es 的聚合查询大致结构可以写成：

```json
{
  "query": { "match_all": {} }, 
  "aggs": { 
    "brand_agg": { ... }, // 品牌维度聚合
    "catalog_agg": { ... }, // 分类维度聚合
    "attr_agg": { ... } // 规格属性维度聚合
  }
}
```

接下来分开记录这三者的聚合：

1、品牌聚合

```json
"brand_agg": {
  "terms": { // 1. 使用词项聚合
    "field": "brandId", // 2. 按 brandId 字段分组
    "size": 10 // 3. 只返回前 10 个最常见的品牌 ID
  },
  "aggs": { // 4. 在同一个品牌 ID 桶内，再做子聚合
    "brand_name_agg": {
      "terms": {
        "field": "brandName", // 5. 获取该品牌 ID 对应的品牌名
        "size": 10
      }
    },
    "brand_img_agg": {
      "terms": {
        "field": "brandImg", // 6. 获取该品牌 ID 对应的品牌图片 URL
        "size": 10
      }
    }
  }
}
```

因为 brandId 是标识品牌的唯一键，所以用它做聚合可以确保统计准确，并且一个 brandId 对应一个桶，不过只 brandId 并不能拿到关键信息，前端显示需要的是品牌的名称和 logo 图片，
所以需要利用到子聚合，在每个 brandId 桶内再去查找对应的 brandName 和 brandImg，最终返回的结果：

```json
"brand_agg" : {
  "doc_count_error_upper_bound" : 0,
  "sum_other_doc_count" : 0,
  "buckets" : [
    {
      "key" : 16,
      "doc_count" : 2,
      "brand_img_agg" : {
        "doc_count_error_upper_bound" : 0,
        "sum_other_doc_count" : 0,
        "buckets" : [
          {
            "key" : "https://cell-gmall.oss-cn-beijing.aliyuncs.com/2025-08-15//3dfdee1d-38e0-42d2-8a30-392e50dded5a_huawei.png",
            "doc_count" : 2
          }
        ]
      },
      "brand_name_agg" : {
        "doc_count_error_upper_bound" : 0,
        "sum_other_doc_count" : 0,
        "buckets" : [
          {
            "key" : "华为",
            "doc_count" : 2
          }
        ]
      }
    }
  ]
}
```

2、分类聚合

```json
"catalog_agg": {
  "terms": {
    "field": "catalogId", // 按分类ID分组
    "size": 10
  },
  "aggs": {
    "catalog_name_agg": {
      "terms": {
        "field": "catalogName", // 获取分类ID对应的分类名称
        "size": 10
      }
    }
  }
}
```

其原理与品牌聚合相同，通过 catalogId 聚合出桶，再通过子聚合 catalog_name_agg 获取分类名称：

```json
"catalog_agg" : {
  "doc_count_error_upper_bound" : 0,
  "sum_other_doc_count" : 0,
  "buckets" : [
    {
      "key" : 225,
      "doc_count" : 2,
      "catalog_name_agg" : {
        "doc_count_error_upper_bound" : 0,
        "sum_other_doc_count" : 0,
        "buckets" : [
          {
            "key" : "手机",
            "doc_count" : 2
          }
        ]
      }
    }
  ]
},
```

3、规格属性聚合

```json
"attr_agg": {
  "nested": { // 1. 进入嵌套文档路径
    "path": "attrs"  
  },
  "aggs": { // 2. 在嵌套文档范围内做聚合
    "attr_id_agg": { // 3. 按属性 ID 分组
      "terms": {
        "field": "attrs.attrId",
        "size": 10
      },
      "aggs": { // 4. 在同一个属性 ID 桶内，做子聚合
        "attr_name_agg": { // 5. 获取属性 ID 对应的属性名（如 “CPU 型号”）
          "terms": {
            "field": "attrs.attrName",
            "size": 10
          }
        },
        "attr_value_agg": { // 6. 获取该属性下的所有属性值（如 “骁龙 665”）
          "terms": {
            "field": "attrs.attrValue",
            "size": 10
          }
        }
      }
    }
  }
}
```

因为 attrs 是 nested 类型，所以进行聚合时需要和查询时一样，使用 nested 语法并指定对象。不过这里并没有完全和查询时一样，因为查询时是把查询语句嵌套进 nested 中，
而这里的聚合是和它同级编写的，即起一个指定作用。

****
### 2.5 SearchRequest 的构建

#### 2.5.1 构建查询部分

Controller 层：

```java
/**
 * 携带检索参数跳转到页面
 */
@GetMapping({"/list.html"})
public String indexPage(SearchParam param, Model model) {
    // 1. 根据传递来的页面的查询参数去 es 中检索商品
    SearchResult result = mallSearchService.search(param);
    model.addAttribute("result", result);
    return "list";
}
```

Service 层：

将数据从 es 端查询出来就需要构造查询语句，而要将数据发送给前端页面，就需要将查询出来的数据封装成对应的格式，所以这里定义了两个方法，一个用于构建查询请求，
一个分析查询出的响应数据。

```java
@Override
public SearchResult search(SearchParam param) {
    // 动态构建出查询需要的 DSL 语句
    SearchResult result = null;
    // 1. 准备检索请求
    SearchRequest searchRequest = buildSearchRequest(param);
    SearchResponse searchResponse;
    try {
        // 2. 执行检索请求
        searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 3. 分析响应数据，封装成需要的格式
        result = buildSearchResult(searchResponse);
    } catch (IOException e) {
        log.error("构建检索失败：{}", e.getMessage());
    }
    return result;
}
```

后端的整体代码类似于上面记录的 es 代码，所以也需要构建一个复合的 bool 查询，以此包含全文搜索和多种过滤条件。所以需要通过代码先创建一个布尔查询容器，然后添加全文搜索条件：

```java
// 1. 构建 bool-query
BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
// 构建 must 模糊匹配
if (StringUtils.isNotBlank(param.getKeyword())) {
    boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
}
```

接着就是处理过滤条件了，es 的写法就是用 filter 包裹住需要过滤的条件，里面的查询用 term 或 terms 进行精确匹配，主要需要进行过滤的为：三级分类 id、品牌 id、具体属性、是否有库存：

1、分类 id

```java
// 构建 filter
// 按照三级分类 id 查询
if (param.getCatalog3Id() != null) {
    boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
}
```

2、按品牌 id 集合过滤

```java
// 按照品牌 id 集合查询
if (param.getBrandId() != null && !param.getBrandId().isEmpty()) {
    boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
}
```

3、按属性过滤

这里拟定的前端传递的属性参数格式为：1_5寸:8寸，即 attrId = 1 和 attrValue[] = ["5寸", "8寸"]，因为它是直接作为一个字符串传递的，所以需要对其进行拆分，用 "_" 拆分 id 和 value，
用 ":" 拆分不同的 value。通常就是直接构建一个 bool-query，然后里面执行检索条件，但这个对象是 nested 类型，所以得封装为 nested 查询。而 Java 代码的写法与 es 的略有不同，
es 中是先写 nested，然后把查询嵌套写进去；但这里是先封装查询条件，然后把它作为参数封装为 nested 查询。不管怎样，都是把查询封装为 nested 查询。因为要处理多个属性值，
所以是利用循环处理，依次遍历传入的属性列表，然后依次封装为 nested 查询。

```java
// 按照属性查询
if (param.getAttrs() != null && !param.getAttrs().isEmpty()) {
    for (String attrStr : param.getAttrs()) {
        BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
        // attrs=1_5寸:8寸&attrs=2_16G:8G
        String[] s = attrStr.split("_");
        String attrId = s[0]; // 检索的属性 id
        String[] attrValues = s[1].split(":"); // 检索的属性值
        nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
        nestedBoolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
        // 每一个 attr 都要生成一个 nested 查询
        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None);
        boolQueryBuilder.filter(nestedQueryBuilder);
    }
}
```

4、按库存过滤

这里需要注意的则是需要把传入的值转换为 boolean 类型，因为 es 中存储的 hasStock 字段就是该类型。

```java
// 按照是否有库存查询
if (param.getHasStock() != null) {
    boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
}
```

处理完过滤后，就需要处理一些其它条件：

1、按价格区间过滤

这里拟定的前端传递的价格区间的格式为 1_500/_500/500_（1 - 500/小于 500/大于 500），所以可以和上面获取属性一样对字符串进行分割，但需要注意的是：
String.split() 方法在分隔符位于开头或结尾时，会产生空字符串的元素，所以进行区分时需要对首尾元素进行非空判断再进行赋值。

```java
// 按照价格区间查询
if (StringUtils.isNotBlank(param.getSkuPrice())) {
    // 约定格式：1_500/_500/500_
    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
    // 解析 skuPrice 格式
    String[] s = param.getSkuPrice().split("_");
    if (s.length == 2) {
        if (!s[0].isEmpty()) rangeQueryBuilder.gte(s[0]);
        if (!s[1].isEmpty())rangeQueryBuilder.lte(s[1]);
    } else {
        // 判断是大于还是小于
        if (param.getSkuPrice().startsWith("_")) {
            // 以 "_" 开头证明是小于
            if (!s[0].isEmpty()) { // 对于 "_500", s[0] 是空字符串，所以不会执行
                rangeQueryBuilder.lte(s[0]);
            }
            if (s.length > 1 && !s[1].isEmpty()) {
                rangeQueryBuilder.lte(s[1]);
            }
            // rangeQueryBuilder.lte(s[0]);
        } else if (param.getSkuPrice().endsWith("_")) {
            // 处理 500_
            if (!s[0].isEmpty()) {
                rangeQueryBuilder.gte(s[0]);
            }
        }
    }
    boolQueryBuilder.filter(rangeQueryBuilder);
}
```

至此，查询条件封装完毕，把它们放作为参数传递给 SearchSourceBuilder

```java
searchSourceBuilder.query(boolQueryBuilder);
```

接着就是非查询条件，也就是排序、分页、高亮操作：

```java
/**
 * 排序、分页、高亮
 */
// 排序
if (StringUtils.isNotBlank(param.getSort())) {
    String sort = param.getSort();
    String[] s = sort.split("_");
    if (!s[0].isEmpty() && !s[1].isEmpty()) {
        SortOrder sortOrder = s[1].equals("asc") ? SortOrder.ASC : SortOrder.DESC;
        searchSourceBuilder.sort(s[0], sortOrder);
    }
}
// 分页
searchSourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
searchSourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
// 高亮
if (StringUtils.isNotBlank(param.getKeyword())) {
    HighlightBuilder highlightBuilder = new HighlightBuilder();
    highlightBuilder.field("skuTitle");
    highlightBuilder.preTags("<span style='color:red'>");
    highlightBuilder.postTags("</span>");
    searchSourceBuilder.highlighter(highlightBuilder);
}
```

****
#### 2.5.2 聚合分析

根据 es 的代码，对品牌、三级分类以及属性进行聚合分析：

1、品牌聚合和三级分类聚合

按 brandId 字段对商品进行分组，统计每个品牌下的商品数量，和 es 操作的一样，使用 terms 进行聚合，将字段的每个不同值作为一个桶，并计算每个桶里的文档数量。
然后调用 subAggregation() 方法生成对应的子聚合。

```java
// 1. 品牌聚合
TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
brand_agg.field("brandId").size(50); // 按 brandId 分桶
// 品牌聚合的子聚合
brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1)); // 获取品牌名
brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(10)); // 获取品牌图片
searchSourceBuilder.aggregation(brand_agg); // 加入查询

// 2. 分类聚合
TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
catalog_agg.field("catalogId").size(50); // 按 catalogId 分桶
// 分类聚合的子聚合
catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1)); // 获取分类名
searchSourceBuilder.aggregation(catalog_agg);
```

2、属性聚合

属性聚合则需要使用 nested 聚合，通过调用 nested() 方法将聚合的上下文从主文档（商品）切换到嵌套文档，然后在嵌套文档的上下文中，按 attrId 对所有的属性项进行分组。
这里与 es 的写法不同，es 中是平级关系（但实际为嵌套），这里则是直接把所有的聚合都作为 nested 的子聚合，名称和属性值则作为子聚合的子聚合。

```java
// 3. 属性聚合
NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs"); // 进入嵌套文档
// 作为 nested 的子聚合
TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(50); // 按属性 id 分桶
// 作为 nested 的子聚合的子聚合，即名字和属性值
attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1)); // 获取属性名
attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50)); // 获取属性值
attr_agg.subAggregation(attr_id_agg); // 将属性 id 聚合设为嵌套聚合的子聚合
// 放入 SearchSourceBuilder
searchSourceBuilder.aggregation(attr_agg);
```

****
### 2.6 SearchResponse 的分析

上面记录了查询条件的构建，现在则需要对响应结果进行解析然后封装为具体的对象返回给前端页面展示。在设计返回模型 SearchResult 的就记录了需要返回哪些数据，所以这里直接按顺序记录。

1、商品列表处理

首先通过 hit.getSourceAsString() 获取文档的 JSON 字符串表示，然后将其转换为 SkuEsModel 模型，其实整个 es 就是该模型，但是并没有选择直接返回它给前端，
因为前端不仅仅需要该对象用来展示，还需要一些相关联的属性用于动态展示，为了避免再通过 SkuEsModel 中的字段再查询数据库获取关联数据，就直接把它们全封装为一个新的对象，
把需要用到的数据用更方便的形式展示与获取。

```java
// 1. 返回所有查询到的商品
List<SkuEsModel> skuEsModels = new ArrayList<>();
SearchHits hits = searchResponse.getHits();
if (hits.getHits() != null && hits.getHits().length > 0) {
    for (SearchHit hit : hits.getHits()) {
        String sourceAsString = hit.getSourceAsString();
        SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
        skuEsModels.add(skuEsModel);
        if (StringUtils.isNotBlank(param.getKeyword())) {
            HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
            String string = skuTitle.getFragments()[0].string();
            skuEsModel.setSkuTitle(string);
        }
    }
}
searchResult.setProducts(skuEsModels);
```

在这里还需要对检索的关键词进行高亮处理，es 在返回搜索结果时，hits 里每个文档都会多一个 highlight 字段，在 Java 中这个高亮部分会被解析成：

```java
Map<String, HighlightField> highlightFields = hit.getHighlightFields();
```

highlightFields.get("skuTitle") 得到的是一个 HighlightField 对象，而 HighlightField 里面保存了多个片段（Text[] fragments）。
每个 Text 对象就是一段高亮后的字符串，[0]：取第一个片段（通常 ES 高亮结果只有一个片段，如果字段内容很长可能会被拆成多个），但要拿真正的字符串，要调用 string() 方法。

```json
"highlight": {
  "fields": {
    "skuTitle": {}
  }
}
```

2、品牌聚合处理

es 聚合通常是多层次的，并且上面在记录 es 的查询代码时也验证了，通过获取到的聚合获取到该聚合里面的桶数据，而品牌 id 聚合的桶数据的 key 则为 id，不过通常该数据有多条，
所以获取到的桶有多个，那么就需要进行遍历。在遍历的过程中获取它的子聚合，而子聚合通常只有一条数据，所以直接获取第一个桶数据的 key 即可获取到对应的值。

```java
"brand_agg" : {
  "buckets" : [
    {
      "key" : 12,
      "doc_count" : 18,
      "brand_img_agg" : {
        "buckets" : [
          {
            "key" : "https://cell-gmall.oss-cn-beijing.aliyuncs.com/2025-08-11/d89a6efc-e070-4ebf-8328-6f130eb8cda4_a34a3e5ed1b162dd71d9ac48f9d48974.jpg",
            "doc_count" : 18
          }
        ]
      },
      "brand_name_agg" : {
        "buckets" : [
          {
            "key" : "Apple",
            "doc_count" : 18
          }
        ]
      }
    },
    {
      "key" : 16,
      "doc_count" : 4,
      "brand_img_agg" : {
        "buckets" : [
          {
            "key" : "https://cell-gmall.oss-cn-beijing.aliyuncs.com/2025-08-15//3dfdee1d-38e0-42d2-8a30-392e50dded5a_huawei.png",
            "doc_count" : 4
          }
        ]
      },
      "brand_name_agg" : {
        "buckets" : [
          {
            "key" : "华为",
            "doc_count" : 4
          }
        ]
      }
    }
  ]
}
```

```java
// 2. 当前查询到的结果，所涉及到的所有品牌
List<SearchResult.BrandVo> brandVos = new ArrayList<>();
Terms brand_agg = searchResponse.getAggregations().get("brand_agg");
if (brand_agg != null && !brand_agg.getBuckets().isEmpty()) {
    brand_agg.getBuckets().forEach(bucket -> {
        SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
        // 获取品牌 id
        brandVo.setBrandId(bucket.getKeyAsNumber().longValue());
        // 获取品牌名称
        Terms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
        if (brand_name_agg != null && !brand_name_agg.getBuckets().isEmpty()) {
            brandVo.setBrandName(brand_name_agg.getBuckets().get(0).getKeyAsString());
        }
        // 获取品牌的图片
        Terms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
        if (brand_img_agg != null && !brand_img_agg.getBuckets().isEmpty()) {
            brandVo.setBrandImg(brand_img_agg.getBuckets().get(0).getKeyAsString());
        }
        brandVos.add(brandVo);
    });
}
```

3、分类聚合处理

该聚合处理同理：

```java
// 3. 当前查询到的结果，所涉及到的所有分类
List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
Terms catalog_agg = searchResponse.getAggregations().get("catalog_agg");
if (catalog_agg != null && !catalog_agg.getBuckets().isEmpty()) {
    catalog_agg.getBuckets().forEach(bucket -> {
        SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
        // 得到分类 id
        String keyAsString = bucket.getKeyAsString();
        catalogVo.setCatalogId(Long.valueOf(keyAsString));
        // 得到分类名
        Terms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
        if (catalog_name_agg != null && !catalog_name_agg.getBuckets().isEmpty()) {
            String catalogName = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
        }
        catalogVos.add(catalogVo);
    });
}
```

4、属性聚合处理

而属性聚合的嵌套相比上面就更复杂一些，因为它是 nested 类型的对象，所以它的聚合天生嵌套在 nested 中，所以要获取第一层属性 id 的聚合就要从 nested 中获取，
即使用 searchResponse.getAggregations().get("attr_agg"); 获取第一层聚合，然后再获取子聚合得到属性 id 的聚合。而属性值的聚合则不再是单一值了，
它可能存在多个值，所以不能直接通过获取桶数据的第一个值来获取，而是需要遍历整个桶数据了，就和遍历属性 id 聚合的桶一致，不过最终是把数据转换成 List<String> 形式，
所以得用到流的 map() 方法。

```java
// 4. 当前查询到的结果，所涉及到的所有属性
List<SearchResult.AttrVo> attrVos = new ArrayList<>();
Nested attr_agg = searchResponse.getAggregations().get("attr_agg");
if (attr_agg != null) {
    Terms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
    if (attr_id_agg != null && !attr_id_agg.getBuckets().isEmpty()) {
        attr_id_agg.getBuckets().forEach(bucket -> {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            // 1. 得到属性 id
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            Terms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            if (attr_name_agg != null && !attr_name_agg.getBuckets().isEmpty()) {
                // 2. 得到属性名称
                attrVo.setAttrName(attr_name_agg.getBuckets().get(0).getKeyAsString());
            }
            Terms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            if (attr_value_agg != null && !attr_value_agg.getBuckets().isEmpty()) {
                List<String> attrValueList = attr_value_agg.getBuckets().stream()
                    .map(MultiBucketsAggregation.Bucket::getKeyAsString)
                    .collect(Collectors.toList());
                attrVo.setAttrValue(attrValueList);
            }
            attrVos.add(attrVo);
        });
    }
    searchResult.setAttrs(attrVos);
}
```

5、分页信息处理

这里主要是处理总记录条数、总页数和当前页码，当前页码前端会传递过来，而总条数和总页数则需要手动计算，总条数在 es 的返回结果中可以看到，它有显示，所以可以直接获取第一个 hits 来获取。
而总页数则需要注意不满一页的情况，不满一页也应该算为一页，所以在计算时需要加上每页的条数 - 1 依次达到向上取整的目的，确保未满一页的数据也能算作一页，而恰好一页则不会受影响，
例如：total=10，pageSize=8 -> (10+8-1)/8 = 17/8 = 2.125 -> 取整为 2 页;total=8 -> (8+8-1)/8 = 15/8 = 1.875 -> 取整为 1 页。

```json
{
  "took": 16,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 22,
      "relation": "eq"
    }
  }
}
```

```java
// 5. 分页信息
searchResult.setPageNum(param.getPageNum());
if (hits.getTotalHits() != null) {
    long total = hits.getTotalHits().value;
    searchResult.setTotal(total);
    // 向上取整，不满一页的时候也算作一页
    int totalPages = (int) ((total + EsConstant.PRODUCT_PAGESIZE - 1) / EsConstant.PRODUCT_PAGESIZE);
    searchResult.setTotalPages(totalPages);
}
```

****
## 3. 检索页面渲染

### 3.1 页面基本数据渲染

当前的检索界面是写死的，所以需要对其进行改造，让它动态获取存储在 es 中的数据，而上面的记录中已经完成了查询的基本逻辑，并且把解析后的 es 的响应结果以 "result" 存储在 域中，

```java
@GetMapping({"/", "/list.html"})
public String indexPage(SearchParam param, Model model) {
    // 1. 根据传递来的页面的查询参数去 es 中检索商品
    SearchResult result = mallSearchService.search(param);
    model.addAttribute("result", result);
    return "list";
}
```

所以前端页面只需要从域中获取到 result 便可以获取到所有封装好的数据。先在前端获取到封装好的 SkuEsModel 模型数据，它作为 result 的 products 字段，所以在域中获取后，
便可以获取到 products 字段中的所有属性，例如标题 skuTitle、skuImg、skuPrice 等数据：

```html
<div class="rig_tab">
    <div th:each="product:${result.getProducts()}">
        <div class="ico">
            <i class="iconfont icon-weiguanzhu"></i>
            <a href="/static/search#">关注</a>
        </div>
        <p class="da">
            <a href="/static/search#">
                <img th:src="${product.getSkuImg()}" class="dim">
            </a>
        </p>
        <ul class="tab_im">
            <li><a href="/static/search#" title="黑色">
                <img th:src="${product.getSkuImg()}"></a></li>
        </ul>
        <p class="tab_R">
            <span th:text="'¥' + ${product.getSkuPrice()}">¥5199.00</span>
        </p>
        <p class="tab_JE">
            <a href="/static/search#" th:utext="${product.skuTitle}">
                Apple iPhone 7 Plus (A1661) 32G 黑色 移动联通电信4G手机
            </a>
        </p>
        <p class="tab_PI">已有<span>11万+</span>热门评价
            <a href="/static/search#">二手有售</a>
        </p>
        <p class="tab_CP"><a href="/static/search#" title="谷粒商城Apple产品专营店">谷粒商城Apple产品...</a>
            <a href='#' title="联系供应商进行咨询">
                <img src="/static/search/img/xcxc.png">
            </a>
        </p>
        <div class="tab_FO">
            <div class="FO_one">
                <p>自营
                    <span>谷粒商城自营,品质保证</span>
                </p>
                <p>满赠
                    <span>该商品参加满赠活动</span>
                </p>
            </div>
        </div>
    </div>
</div>
```

而响应结果中的其它属性也可以这样获取，在 result 中获取对应的字段拿到已经封装好的属性，然后依次渲染即可。

```html
<div class="JD_nav_logo">
    <!--品牌-->
    <div class="JD_nav_wrap">
        <div class="sl_key">
            <span><b>品牌：</b></span>
        </div>
        <div class="sl_value">
            <div class="sl_value_logo">
                <ul>
                    <li th:each="brand:${result.brands}">
                        <a href="/static/search#">
                            <img th:src="${brand.brandImg}" alt="">
                            <div th:text="${brand.brandName}">
                                华为(HUAWEI)
                            </div>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="sl_ext">
            <a href="/static/search#">
                更多
                <i style='background: url("image/searchele.png")no-repeat 3px 7px'></i>
                <b style='background: url("image/searchele.png")no-repeat 3px -44px'></b>
            </a>
            <a href="/static/search#">
                多选
                <i>+</i>
                <span>+</span>
            </a>
        </div>
    </div>
    <!--分类-->
    <div class="JD_pre">
        <div class="sl_key">
            <span><b>分类：</b></span>
        </div>
        <div class="sl_value">
            <ul>
                <li th:each="catalog:${result.catalogs}"><a href="/static/search#" th:text="${catalog.catalogName}">5.56英寸及以上</a></li>
            </ul>
        </div>
        <div class="sl_ext">
            <a href="/static/search#">
                更多
                <i style='background: url("image/searchele.png")no-repeat 3px 7px'></i>
                <b style='background: url("image/searchele.png")no-repeat 3px -44px'></b>
            </a>
            <a href="/static/search#">
                多选
                <i>+</i>
                <span>+</span>
            </a>
        </div>
    </div>
    <!--其它需要展示的属性-->
    <div class="JD_pre" th:each="attr:${result.attrs}">
        <div class="sl_key">
            <span th:text="${attr.attrName}">屏幕尺寸：</span>
        </div>
        <div class="sl_value">
            <ul>
                <li th:each="val:${attr.attrValue}"><a href="/static/search#" th:text="${val}">5.56英寸及以上</a></li>
            </ul>
        </div>
    </div>
</div>
```

****
### 3.2 页面筛选条件渲染

上面有记录过，在电商检索页面会提供除全文检索外的一些其它检索条件，而这里主要为品牌、三级分类以及其它属性的检索，这些检索都是通过聚合动态获取的，现在想要实现的是：
点击这些动态显示的东西，让它作为检索条件显示相关内容。而实现原理则是在原有的浏览器 url 上拼接点击的那些东西的属性。例如原始的首页 url 为：http://search.gulimall.com/list.html，
此时想要拼接其它查询条件就应该为：http://search.gulimall.com/list.html?catalog3Id=225， 也就是在后面拼接了一个 "?" 和之前约定的分类查询的请求路径格式，
如果再查询一个，那 url 应该为：http://search.gulimall.com/list.html?catalog3Id=225&brandId=16， 也就是在后面拼接了一个 "&" 和约定的品牌查询的请求路径格式，
那么现在就很明确了，应该写一个方法，在用户点击这些地方的时候实现同台拼接条件，如果原始路径中没有 "?" 那就需要拼接上，否则直接拼接 "&"，所以方法可以写为：

```html
function searchProducts( name , val ) {
  // 获取原来页面的 url
  if (location.href.indexOf("?") != -1) {
    location.href = location.href + "&" + name + "=" + val;
  } else {
    location.href = location.href + "?" + name + "=" + val;
  }
}
```

这里利用 indexOf 方法判断路径中的 "?" 在那个位置，如果返回值不为 -1 那就证明存在 "?"（无需具体判断在哪个位置）。

方法写完，接着就得在前端页面展示相关过滤条件的地方写上能够调用该方法的语句了，先从品牌开始：

```html
<div class="sl_value">
    <div class="sl_value_logo">
        <ul>
            <li th:each="brand:${result.brands}">
                <a href="javascript:void(0);"  th:onclick="|searchProducts('brandId', ${brand.brandId})|">
                    <img th:src="${brand.brandImg}" alt="">
                    <div th:text="${brand.brandName}">
                        华为(HUAWEI)
                    </div>
                </a>
            </li>
        </ul>
    </div>
</div>
```

品牌的信息是通过 th:each 从域中的 result 中获取的，可以包含多个品牌，所以每一次的点击查询也应该包含在里面。这里的品牌信息用的是一个 `<a>` 标签，
通常该标签默认会跳转到 href 指定的 url，所以为了避免他进行跳转并正确执行 js 代码中的方法，就使用了 `javascript:void(0);`，它是一个 JS 表达式：

- void(0) 的意思是返回 undefined
- 结合 javascript: 前缀，就变成了执行 JS 表达式，但不跳转任何页面

这样写的好处是可以让 `<a>` 标签像按钮一样使用，用 onclick 执行 JS 方法，并阻止 `<a>` 的默认跳转行为避免点击后页面刷新。

```html
<div class="sl_value">
    <ul>
        <li th:each="catalog:${result.catalogs}">
            <a href="javascript:void(0);"
               th:onclick="|searchProducts('catalog3Id', ${catalog.catalogId})|"
               th:text="${catalog.catalogName}">5.56英寸及以上</a>
        </li>
    </ul>
</div>
```

不过在 attr 属性这里就不能像上面那样写 th:onclick 了，因为这里的查询拼接是这样的：http://search.gulimall.com/list.html?catalog3Id=225&brandId=16&attrs=16_骁龙665 。
所以需要把获取到的 attrId 和 attrValue 用 "_" 拼接起来，而 Thymeleaf 3.0 默认不允许直接把字符串表达式放在 onclick 这种事件属性里，因此换了一种写法，
用 th:attr="onclick=|...|" 替代 th:onclick，让内部可以直接使用字符串表达式。

```html
<div class="sl_value">
    <ul>
        <li th:each="val:${attr.attrValue}">
            <a href="javascript:void(0);"
               th:attr="onclick=|searchProducts('attrs','${attr.attrId}_${val}')|"
               th:text="${val}">5.56英寸及以上</a>
        </li>
    </ul>
</div>
```

关于检索页面的全文搜索框，这个其实也可以直接调用上面写的 searchProducts() 方法，因为本质上还是在浏览器的 url 后面拼接 keyword=华为..：

```html
<div class="header_form">
    <input id="keyword_input" type="text" placeholder="手机" th:value="${param.keyword}"/>
    <a href="javascript:void(0);"
       th:onclick="|searchByKeyword()|">搜索</a>
</div>
```

在全文搜索框那里添加一下 id，后续可以通过 id 获取到这个搜索框中输入的值，然后直接进行拼接：

```html
function searchByKeyword() {
    searchProducts("keyword", $("#keyword_input").val())
}
```

****
### 3.3 页面分页数据渲染

```html
<a class="page_a"
   th:attr="pn=${result.pageNum - 1}"
   th:if="${result.pageNum > 1}">
    < 上一页
</a>
```

只有当当前页 result.pageNum > 1 时才渲染上一页按钮，避免在第 1 页还出现上一页的无效按钮，所以应该添加 th:if 进行判断，
而 th:attr 则是给 `<a>` 添加一个自定义属性 pn（pageNum 的缩写），值是目标页码 -1，以此达到跳转上一页的目的。这么做的好处是：
所有分页按钮（上一页/下一页/数字页）都用同一个 click 逻辑，前端可以统一从 pn 里取目标页码。

```html
<a class="page_a"
   th:attr="pn=${nav}, style=${nav == result.pageNum ? 'border: 0;color:#ee2222;background: #fff' : ''}"
   th:each="nav:${result.pageNavs}">
    [[${nav}]]
</a>
```

这段的逻辑就是遍历获取所有页码，不过原始的后端并没有添加这个 pageNavs 字段，所以需要增加一下，通过分页总数来获取，定义为一个页码数组，以此来为为每个页码渲染一个 `<a>`。

```java
List<Integer> pageNavs = new ArrayList<>();
for (int i = 1; i <= searchResult.getTotalPages(); i++) {
    pageNavs.add(i);
}
searchResult.setPageNavs(pageNavs);
```

这里同样写入 pn=${nav}，点击就能跳到该页，同时根据 nav == result.pageNum 决定是否内联样式渲染当前页的按钮。

关于点击的处理方法，所有分页 `<a>` 都没直接写 href="pageNum=..."，而是只标记一个 pn，因为给每个 `<a>` 添加了 class="page_a" 标识，只要点击了该标识所在地，
就会触发方法，点击后由 js 读取当前 url：

- 如果已有 pageNum，就把它的值替换掉（避免一直拼接 &pageNum 导致丢失其它查询条件，比如关键字、分类、排序等）
- 如果没有 pageNum，就追加一个 pageNum=pn 参数

```html
$(".page_a").click(function () {
    var pn = $(this).attr("pn"); // 当前被点击元素
    var href = location.href;
    if (href.indexOf("pageNum") !== -1) {
        // 替换 pageNum 的值
        location.href = replaceParamVal(href, "pageNum", pn);
    } else {
        location.href = location.href + "&pageNum=" + pn;
    }
    return false;
})

function replaceParamVal(url, paramName, replaceVal) {
    var oUrl = url.toString();
    var re = eval('/(' + paramName + '=)([^&]*)/gi');
    var nUrl = oUrl.replace(re, paramName + '=' + replaceVal);
    return nUrl;
}
```

****
### 3.4 页面排序功能

排序功能的核心逻辑就是点击排序按钮时，前端给 URL 增加或替换 sort=xxx 参数，带上新的参数去后端查询，同时改变按钮样式，让当前点击的按钮显示选中状态，如：背景变红，
文字加箭头 ⬆/⬇ 表示升降序。

```html
function replaceAndAddParamVal(url, paramName, replaceVal) {
    var oUrl = url.toString();
    if (oUrl.indexOf(paramName) !== -1) {
        var re = eval('/(' + paramName + '=)([^&]*)/gi');
        var nUrl = oUrl.replace(re, paramName + '=' + replaceVal);
        return nUrl;
    } else {
        var nUrl = "";
        if (oUrl.indexOf("?") !== -1) {
            nUrl = oUrl + "&" + paramName + '=' + replaceVal;
        } else {
            nUrl = oUrl + "?" + paramName + '=' + replaceVal;
        }
        return nUrl;
    }
}
```

这里修改了一下原来的 replaceParamVal 方法，让它能够同时实现新增与修改，通过调用它检查 url 里是否已经有当前传入的 paramName 参数，如果有就替换成新值（replaceVal），
否则根据是否有 "?" 拼接上新的参数。这样就能做到：

```text
http://xxx.com/search?keyword=手机
-> 点击价格按钮后
http://xxx.com/search?keyword=手机&sort=skuPrice_asc
-> 再点一次（切换降序）
http://xxx.com/search?keyword=手机&sort=skuPrice_desc
```

关于点击事件，则是和分页处理时类似，也是在 `<a>` 标签中定义一个标识，点击时先改样式，确定当前是升序还是降序，然后根据样式拼出 sort=xxx_asc/desc，
最后用 replaceAndAddParamVal 更新 url 并刷新。

```html
<a class="sort_a" sort="hotScore" href="/static/search#">综合排序</a>
<a class="sort_a" sort="saleCount" href="/static/search#">销量</a>
<a class="sort_a" sort="skuPrice" href="/static/search#">价格</a>
```

这里是动态的拼接 "_desc" 与 "_asc"，通过当前点击按钮的 sort 获取参与排序的字段，然后根据是否携带 desc 字段拼接为升降序，最后将条件拼接上 url。

```html
$(".sort_a").click(function () {
    // 1. 当前被点击的元素变为选中状态，也就是添加渲染样式
    // 改变当前元素以及兄弟元素的样式
    changeStyle(this);
    // 跳转到指定位置 sort=skuPrice_desc/asc
    var sort = $(this).attr("sort");
    sort = $(this).hasClass("desc") ? sort + "_desc" : sort + "_asc";
    location.href = replaceAndAddParamVal(location.href, "sort", sort)
    // 禁用默认行为
    return false;
})
```

这里把更新样式抽取为一个方法，在修改某个按钮的样式前，先把原有的 "⬇"/"⬆" 清空，保证每次点击都只显示一个，然后再用 toggleClass("desc") 来切换升降状态。
toggleClass 方法是在当前元素没有 desc 时给它加上，如果当前元素有 desc 则会给它去掉，也就是说它会在点击时在有与没有这两种状态之间反复切换。
然后进行判断并加上符号。

```html
function changeStyle(element) {
    $(".sort_a").css({"color":"#333", "border-color":"#CCC", "background":"#FFF"});
    $(".sort_a").each(function () {
        var text = $(this).text().replace("⬇", "").replace("⬆", "");
        $(this).text(text);
    });
    $(element).css({"color":"#FFF", "border-color":"#e4393c", "background":"#e4393c"});
    // 改变升降序
    $(element).toggleClass("desc");// 加上就是降序，不加就是升序
    if ($(element).hasClass("desc")) {
        // 降序
        var text = $(element).text().replace("⬇", "").replace("⬆", "");
        text = text + "⬇";
        $(element).text(text);
    } else {
        // 升序
        var text = $(element).text().replace("⬇", "").replace("⬆", "");
        text = text + "⬆";
        $(element).text(text);
    }
}
```

****
### 3.5 页面排序的字段回显

上面记录的页面排序存在一个问题，那就是虽然点击排序按钮后可以拼接排序字段，但是并不会把排序按钮渲染的状态显示出来，因为它会进行以此刷新，就导致渲染白写了。
所以这些渲染应该由 Thymeleaf 进行，点击按钮时只让 js 携带排序字段跳转 url，因为 Thymeleaf 不会因为刷新就丢失渲染数据，因此可以根据 param.sort 动态判断，给对应的按钮加样式、箭头。

```html
<div class="filter_top_left" th:with="p = ${param.sort}">
    <a th:class="${(!#strings.isEmpty(p) && #strings.startsWith(p, 'hotScore') && #strings.endsWith(p, 'desc')) ? 'sort_a desc' : 'sort_a'}"
       th:attr="style=${(#strings.isEmpty(p) || #strings.startsWith(p, 'hotScore')) ?
       'color:#FFF; border-color:#e4393c; background:#e4393c' :
       'color:#333; border-color:#CCC; background:#fff'}"
       sort="hotScore" href="/static/search#">综合排序 [[${(!#strings.isEmpty(p) && #strings.startsWith(p, 'hotScore') && #strings.endsWith(p, 'desc')) ? '⬇' : '⬆'}]]</a>
    <a th:class="${(!#strings.isEmpty(p) && #strings.startsWith(p, 'saleCount') && #strings.endsWith(p, 'desc')) ? 'sort_a desc' : 'sort_a'}"
       th:attr="style=${(!#strings.isEmpty(p) && #strings.startsWith(p, 'saleCount')) ?
       'color:#FFF; border-color:#e4393c; background:#e4393c' :
       'color:#333; border-color:#CCC; background:#fff'}"
       sort="saleCount" href="/static/search#">销量 [[${(!#strings.isEmpty(p) && #strings.startsWith(p, 'saleCount') && #strings.endsWith(p, 'desc')) ? '⬇' : '⬆'}]]</a>
    <a th:class="${(!#strings.isEmpty(p) && #strings.startsWith(p, 'skuPrice') && #strings.endsWith(p, 'desc')) ? 'sort_a desc' : 'sort_a'}"
       th:attr="style=${(!#strings.isEmpty(p) && #strings.startsWith(p, 'skuPrice')) ?
       'color:#FFF; border-color:#e4393c; background:#e4393c' :
       'color:#333; border-color:#CCC; background:#fff'}"
       sort="skuPrice" href="/static/search#">价格 [[${(!#strings.isEmpty(p) && #strings.startsWith(p, 'skuPrice') && #strings.endsWith(p, 'desc')) ? '⬇' : '⬆'}]]</a>
</div>
```

首先在包裹排序按钮的 div 中添加 th:with="p = ${param.sort}"，它可以把请求参数 sort（例如：hotScore_desc、saleCount_asc）存到局部变量 p，供下面多处复用。
每个 `<a>` 标签中都有 th:class=" ... ? 'sort_a desc' : 'sort_a'" 这个代码，它是用来判断 class 是否包含 desc，如果包含就把它动态拼接到 class 中，
在 js 的方法中就是通过判断当前点击的按钮的 class 是否包含 desc 字段，来拼 xxx_desc / xxx_asc 的，所以 Thymeleaf 渲染时把这个状态还原，刷新后前端还能当前是升还是降。

接着就是处理当前被选中的按钮的高亮（给当前被选中的排序字段上红色高亮样式，其他是灰色边黑字白底），对综合排序（hotScore）：#strings.isEmpty(p) || #strings.startsWith(p, 'hotScore')，
只有当没有选择排序字段或 sort 以它开头时才高亮，其余按钮同理，在选择了排序并且以对应的 sort 开头时就进行高亮处理，否则渲染成灰色边黑字白底。

最后给按钮重新弄上上下箭头，[[${(!#strings.isEmpty(p) && #strings.startsWith(p, 'hotScore') && #strings.endsWith(p, 'desc')) ? '⬇' : '⬆'}]]，
对当前按钮的开头与是否携带 desc 字段进行判断，如果都满足那就是降序，则拼接 "⬇"，否则拼接 "⬆"。

既然所有的渲染都交给 Thymeleaf 了，那之前写的渲染的方法就不需要用到了，在点击的方法中直接进行 url 字段的处理与拼接即可，

```html
$(".sort_a").click(function () {
    var sort = $(this).attr("sort"); // 读取 sort="hotScore/saleCount/skuPrice"
    // 如果当前是 desc，就拼 asc，否则拼 desc
    var isDesc = $(this).hasClass("desc");
    sort = isDesc ? sort + "_asc" : sort + "_desc";
    location.href = replaceAndAddParamVal(location.href, "sort", sort);
    return false;
});
```

****
### 3.6 页面价格区间搜索与是否有库存搜索

同样的，这里页面价格区间的回显操作也由 Thymeleaf 进行管理，这里设置了前置价格与后置价格，通过这个可以很方便的就进行 "_" 的拼接：

```html
<div class="filter_top_left" th:with="p = ${param.sort}, priceRange = ${param.skuPrice}">
  <input id="skuPriceFrom" type="number" style="width: 100px; margin-left: 30px"
      th:value="${#strings.isEmpty(priceRange) ? '' : #strings.substringBefore(priceRange, '_') }"
  > ~
  <input id="skuPriceTo" type="number" style="width: 100px"
      th:value="${#strings.isEmpty(priceRange) ? '' : #strings.substringAfter(priceRange, '_') }"
  >
  <button id="skuPriceSearchBtn">确定</button>
</div>
```

```html
$("#skuPriceSearchBtn").click(function () {
    // 拼接上两个价格的区间
    var from = $("#skuPriceFrom").val();
    var to = $("#skuPriceTo").val();
    var query = from + "_" + to;
    location.href = replaceAndAddParamVal(location.href, "skuPrice", query);
})
```

对于是否有库存的过滤条件，把它做成一个勾选框的形式，用 id 绑定监听事件，触发方法后根据勾选框的值为 true 还是 false 动态拼接 1 或者 ''，
拼接 1 代表查询有库存的 hasStock=1，拼接 '' 代表查询所有。不过由于这个拼接比较特殊，从查询有库存到全部的情况时需要清空 &hasStock=1 这个字段，所以需要编写额外的去除逻辑。

```html
<li>
  <a href="/static/search#" th:with="check=${param.hasStock}">
    <input id="showHasStock" type="checkbox" th:checked="${#strings.equals(check, '1')}">
    仅显示有货
  </a>
</li>
```

```html
$("#showHasStock").change(function () {
    var url = location.href + "";
    if ($(this).prop('checked')) {
        url = replaceAndAddParamVal(url, "hasStock", 1);
    } else {
        var re = /(hasStock=)([^&]*)/gi;
        url = url.replace(re, '');
        // 额外清理可能出现的多余符号
        url = url.replace(/[&]{2,}/g, "&"); // 把多个 && 合并成一个 &
        url = url.replace(/\?&/, ""); // 把 ?& 去掉
        url = url.replace(/&$/, ""); // 去掉末尾的 &
    }
    location.href = url;
    return false;
})
```

****
### 3.7 面包屑导航

关于面包屑导航功能，它是一种能让使用者更直观地看见自己勾选了哪些属性条件，在用户点击了某个属性后，它会在三级分类的边上进行展示，展示的内容为属性名和其对应的值，并且支持删除功能，
也就是点击这个展示的属性的叉号后，本页面的检索条件就会取消掉刚刚叉掉的那个值，也就是进行一次类似页面回退的操作。所以目的很明确，需要构建一个新的对象属性用来管理属性名、属性值、
以及记录点击某个属性值前的查询 url。

因为这是展示给前端页面的数据，所以直接把它封装进解析 es 响应数据的对象内：

```java
public class SearchResult {
    // 面包屑导航数据
    private List<NavVo> navs;
    @Data
    public static class NavVo {
      private String navName;
      private String navValue;
      private String link;
    }
}
```
 
当然，如果要获取点击某个属性值前的查询 url，那就需要用到 HttpServletRequest 了，通过 HttpServletRequest 的 getQueryString() 方法可以直接获取浏览器中 url 的 "?" 后的所有数据，
所以可以在 Controller 层进行接收后，封装进 SearchParam 对象中，这样可以较为方便地传递给 Service 层。

```java
public class SearchParam {
    // 前端查询的 url 中 "?" 后的所有查询条件
    private String queryString;
}
```

条件准备完毕，进行数据的封装。在前端点击某个属性值后，它就会作为请求参数被封装进 SearchParam 对象中，所以关于 attr 的数据都可以通过该对象获取，不需要再次远程调用别的服务查询数据库。
通过 SearchParam 获取所有的 attr 数据，然后依次遍历它们获取它们的 id 和 value。这里的数据可以不用 es 中的，因为 es 中的数据是还没封装好的，并且 es 的 attr 聚合的 value 值是所有值，
它没有对其进行区分，而 SearchParam 在封装时就对这些数据进行了详细的区分，通过前端的点击可以获取某个 attrId 对应的唯一的那个 value。

```java
// 6. 构建面包屑 NavVo
List<SearchResult.NavVo> navVos = new ArrayList<>();
if (param.getAttrs() != null) { // 通过传递的参数获取所有属性
    for (String attr : param.getAttrs()) { // attr=16_A13仿生
        String[] split = attr.split("_", 2); // 只拆分成 2 个部分
        if (split.length != 2) continue;
        // 获取 attrId
        String attrId = split[0];
        // 获取 attrValue
        String attrValue = split[1];
        // 封装为 NavVo 对象
        SearchResult.NavVo navVo = new SearchResult.NavVo();
        navVo.setNavValue(attrValue);
        // 获取属性名称
        // attrVos 是上面处理 es 的聚合时创建的对象，主要是用来通过 attrId 获取对应的 attrName，避免调用别的服务查询数据库
        attrVos.stream()
                .filter(attrVo -> attrVo.getAttrId().toString().equals(attrId))
                .findFirst()
                .ifPresent(attrVo -> navVo.setNavName(attrVo.getAttrName()));
        // 生成取消面包屑的链接
        String queryString = Optional.ofNullable(param.getQueryString()).orElse("");
        String encodedAttr = URLEncoder.encode(attr, StandardCharsets.UTF_8);
        String attrParam1 = "&attrs=" + encodedAttr;
        String attrParam2 = "?attrs=" + encodedAttr;
        queryString = queryString.replace(attrParam1, "").replace(attrParam2, "");
        // 如果获取到的请求参数以 & 开头，就需要把 & 去掉
        if (queryString.startsWith("&")) {
            queryString = queryString.substring(1);
        }
        // 对原始的请求条件进行判断，如果为空，那么取消面包屑后直接拼接空值；否则拼接原有的 queryString
        navVo.setLink("http://search.gulimall.com/list.html" + (queryString.isEmpty() ? "" : "?" + queryString));
        navVos.add(navVo);
    }
}
searchResult.setNavs(navVos);

return searchResult;
```

面包屑的 attrName 和 attrValue 封装完毕，就需要处理返回连接了，这里的逻辑就是先接收当前的 queryString，然后让本次请求点击的那个属性的请求参数与其进行对比，
如果有一致的，就把它替换成 ""，以此达到回退的效果。例如：当前 queryString：attrs=15_高通(Qualcomm)&attrs=16_骁龙665，本次前端点击的属性条件为 attrs=16_骁龙665，
那么就需要让 attrs=16_骁龙665 与 queryString 进行匹配，找到 attrs=16_骁龙665 并把它删除或置空，所以最初的逻辑就是直接调用字符串的 replace() 方法，
把匹配到的数据替换为 ""，不过当时忽略了浏览器的 url 会自动使用 UTF-8 对中文进行编码，所以后端接收到的 queryString 其实为 attrs=15_%E9%AB%98%E9%80%9A(Qualcomm)&attrs=16_%E9%AA%81%E9%BE%99665。
而 SearchParam 中封装的 attr 对象存储的是字符串，这就会导致匹配过程无法达到效果，总是匹配失败，因此要对 attr 的 attrName 和 attrValue 进行 UTF-8 的编码。
其次，replace() 只能做字面替换， 

```java
// 生成取消面包屑的链接
String queryString = Optional.ofNullable(param.getQueryString()).orElse("");
String encodedAttr = URLEncoder.encode(attr, StandardCharsets.UTF_8);
String attrParam1 = "&attrs=" + encodedAttr;
String attrParam2 = "?attrs=" + encodedAttr;
queryString = queryString.replace(attrParam1, "").replace(attrParam2, "");
// 如果获取到的请求参数以 & 开头，就需要把 & 去掉
if (queryString.startsWith("&")) {
    queryString = queryString.substring(1);
}
```

但最初的这种写法还是无法达到去除掉效果，经过 debug 发现进行替换后的 queryString 仍为前端传递来的 queryString。首先，URLEncoder.encode 的编码规则和浏览器实际传输的 query string 编码规则不完全一样，
浏览器会对空格编码为 %20，而 URLEncoder.encode 会转成 "+"，如果参数里有 `()`、`-`、`_` 等符号，编码结果也可能不一致。而 replace() 是完全依赖字符串的匹配，
如果匹配值有点不一样都会匹配失败。而对 queryString 里的 url 参数做解码时，不管浏览器怎么编码的 %28、+、中文 %E9%AB%98 等，解码后得到的值就是原始字符串，也就是在代码里写的 attr。
所以原来的写法是不行的，必须把 queryString 解码成字符串后再比较。

```java
List<SearchResult.NavVo> navVos = new ArrayList<>();
if (param.getAttrs() != null) { // 通过传递的参数获取所有属性
    for (String attr : param.getAttrs()) { // attr=16_A13仿生
        String[] split = attr.split("_", 2); // 只拆分成 2 个部分
        if (split.length != 2) continue;
        // 获取 attrId
        String attrId = split[0];
        // 获取 attrValue
        String attrValue = split[1];
        // 封装为 NavVo 对象
        SearchResult.NavVo navVo = new SearchResult.NavVo();
        navVo.setNavValue(attrValue);
        // 获取属性名称
        // attrVos 是上面处理 es 的聚合时创建的对象，主要是用来通过 attrId 获取对应的 attrName，避免调用别的服务查询数据库
        attrVos.stream()
                .filter(attrVo -> attrVo.getAttrId().toString().equals(attrId))
                .findFirst()
                .ifPresent(attrVo -> navVo.setNavName(attrVo.getAttrName()));
        // 生成取消面包屑的链接
        String queryString = Optional.ofNullable(param.getQueryString()).orElse("");
        List<String> queryAttrParams = new ArrayList<>();
        // 根据 & 进行分割
        for (String p : queryString.split("&")) {
            if (!p.isEmpty()) queryAttrParams.add(p);
        }
        String targetAttr = attr; // 原始 attr，例如 "15_高通(Qualcomm)"
        // 从 queryString 里删除目标属性参数
        queryAttrParams.removeIf(p -> {
            String[] kv = p.split("=", 2); // limit=2 的作用：即便 value 里还有 "="，也只切一次，避免被继续拆开
            if (kv.length != 2) {
                return false;
            }
            // 只处理 key 为 "attrs" 的参数；其他参数（如 catalog3Id、brandId）一律保留
            if (!kv[0].equals("attrs")) {
                return false;
            }
            try {
                // 对 value 做 URL 解码（把 %E4%BB%A5… 还原成中文；把 '+' 按表单规则解码成空格）
                String value = java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                // 如果解码后的值正好等于要移除的那个 attr（未编码的原始字符串），就返回 true -> 删除
                return value.equals(targetAttr);
            } catch (Exception e) {
                return false;
            }
        });
        String newQuery = String.join("&", queryAttrParams);
        // 对原始的请求条件进行判断，如果为空，那么取消面包屑后直接拼接空值；否则拼接原有的 queryString
        navVo.setLink("http://search.gulimall.com/list.html" + (newQuery.isEmpty() ? "" : "?" + newQuery));
        navVos.add(navVo);
    }
}
searchResult.setNavs(navVos);
```

新的写法则是先对 queryString 的 & 进行分割，拿到每个 attr 数据（需要进行对比是否为 attrs），然后获取 attrs 等号后面的数据，例如：15_%E9%AB%98%E9%80%9A(Qualcomm)。
接着对其进行解码成字符串，再用字符串进行对比，如果一致则从 queryString 中删除，删除后则为前端添加某个属性前的原始请求路径，把它作为 navVo 的 link 参数。

```html
<div class="JD_ipone_one c">
    <!--面包屑导航-->
    <a th:href="${nav.link}" th:each="nav : ${result.navs}"><span th:text="${nav.navName}"></span>:<span th:text="${nav.navValue}"></span> × </a>
</div>
```

前端的跳转逻辑较为简单，直接把 navVo 对象的 link 属性作为原始路径进行跳转即可。处理完回退功能后，发现点击某个属性值后，属于该属性值的那个 name 仍然存在，例如：
高通(Qualcomm) 属于 CPU 品牌，在选中高通(Qualcomm) 时，CPU 品牌这一行的所有数据都不应该再显示了，因为它们是互斥的，所以需要在前端页面进行数据显示的管理。
而后端这里可以选则再响应返回结果对象 SearchResult 中添加一个字段，专门统计那些已经被选中的属性的 id，前端再判断哪些属性属于这个 id，然后取消显示即可。

```java
public class SearchResult {
    private List<Long> blankAttrIds; // 添加某个属性值后，该属性所属的那个 name 不显
}
```

在处理面包屑的代码中可以把获取到的 attrId 添加进 blankAttrIds，但需要注意它的类型必须和原来封装的 attrId 类型一致，也就是都用 Long 类型，否则前端进行判断时会判断错误。

```java
private SearchResult buildSearchResult(SearchResponse searchResponse, SearchParam param) {
    ...
    // 6. 构建面包屑 NavVo
    List<SearchResult.NavVo> navVos = new ArrayList<>();
    List<Long> blankAttrIds = new ArrayList<>();
    if (param.getAttrs() != null) { // 通过传递的参数获取所有属性
        for (String attr : param.getAttrs()) { // attr=16_A13仿生
            String[] split = attr.split("_", 2); // 只拆分成 2 个部分
            if (split.length != 2) continue;
            // 获取 attrId
            String attrId = split[0];
            blankAttrIds.add(Long.valueOf(attrId));
            ...
        }
    }
    searchResult.setNavs(navVos);
    searchResult.setBlankAttrIds(blankAttrIds);
}
```

前端则通过域获取 result 中的 blankAttrIds，用它和当前 div 中的 attrId 对比，如果该 attrId 在 blankAttrIds 存在，证明这个属性是已经添加过的，则不显示。

```html
<div class="JD_pre" th:each="attr:${result.attrs}" th:if="${!#lists.contains(result.blankAttrIds, attr.attrId)}">
    ...
</div>
```

****
# 七、商品详情

## 1. 异步

### 1.1 概述

为什么需要异步？这里以服务员在餐厅服务为例：

- 同步（Synchronous）：服务员接到顾客 A 点单后，亲自去后厨炒菜，炒好后再端给 A。在这期间，他无法为顾客 B 服务，整个餐厅效率极低，如果顾客多起来，那他们就需要等待很久
- 异步（Asynchronous）：服务员接到顾客 A 点单后，将菜单交给后厨（另一个线程），然后立即去为顾客 B 服务，当后厨做好菜后，通过某种方式（比如摇铃）通知服务员，服务员再去取菜。这样服务员（主线程）的时间得到了充分利用，餐厅吞吐量大大提升

异步的核心思想就是：避免让主线程（通常是处理用户请求或UI操作的线程）等待那些耗时的操作（如 IO 操作、网络请求、复杂计算），主线程只需发起任务，然后立刻返回去处理其他工作。
耗时任务在后台执行，执行完成后通过回调、通知等方式告知主线程结果。以此提高响应速度与吞吐量，能更好的资源利用，如在等待 IO 时，CPU 可以做别的事情。

****
### 1.2 线程的基本创建方式

1、继承 Thread 类

这是最基础的方式，通过继承 Thread 类并重写其 run() 方法再调用实例的 start() 方法来启动新线程。

```java
public class MyThread extends Thread {
    private String threadName;
    public MyThread(String name) {
        this.threadName = name;
    }

    @Override
    public void run() {
        // 在新线程中执行的代码
        for (int i = 0; i < 5; i++) {
            System.out.println(threadName + " is running: " + i);
            try {
                Thread.sleep(500); // 模拟耗时操作，让效果更明显
            } catch (InterruptedException e) {
                System.out.println(threadName + " was interrupted.");
            }
        }
        System.out.println(threadName + " exiting.");
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Main thread started.");
        // 创建线程对象
        MyThread threadA = new MyThread("Thread-A");
        MyThread threadB = new MyThread("Thread-B");
        // 启动线程
        threadA.start();
        threadB.start();
        // 主线程继续执行自己的代码
        for (int i = 0; i < 3; i++) {
            System.out.println("Main thread is doing other work: " + i);
        }
        System.out.println("Main thread finished. (但程序不会结束，要等待所有线程结束)");
    }
}
```

输出顺序是不可预测的（交错执行），每次运行都可能不同：

```text
Main thread started.
Main thread is doing other work: 0
Thread-A is running: 0
Main thread is doing other work: 1
Thread-B is running: 0
Main thread is doing other work: 2
Main thread finished.
Thread-A is running: 1
Thread-B is running: 1
...
```

2、实现 Runnable 接口

创建一个实现 Runnable 接口的类并重写 run() 方法，然后创建该实现类的实例（这是一个任务对象）与 Thread 对象，并将 Runnable 实例作为参数传递给 Thread 的构造函数，
最后调用 Thread 对象的 start() 方法。

```java
public class MyRunnable implements Runnable {
    private String taskName;
    public MyRunnable(String name) {
        this.taskName = name;
    }
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(taskName + " is executing: " + i + " on " + Thread.currentThread().getName());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Main thread: " + Thread.currentThread().getName());
        // 创建任务对象（而不是线程对象）
        Runnable task1 = new MyRunnable("Task-1");
        Runnable task2 = new MyRunnable("Task-2");
        // 创建线程对象并传入任务，然后启动
        Thread thread1 = new Thread(task1, "MyThread-1"); // 第二个参数可指定线程名
        Thread thread2 = new Thread(task2);
        thread2.setName("MyThread-2"); // 也可以通过 setName 方法设置线程名
        thread1.start();
        thread2.start();
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("running");
            }
        });
        thread3.start();
    }
}
```

因为是实现接口的缘故，实现了 Runnable 接口的类仍然可以继承其他类，所以灵活性更高，并且 run() 与线程分离，同一个任务可以被多个线程执行。

3、实现 Callable 接口 + FutureTask

Runnable 的 run() 方法返回类型是 void，且不能抛出异常，如果需要异步任务有返回值或能抛出异常，就需要使用 Callable 接口。但 Callable<V> 接口其实类似于 Runnable，
只不过它的 call() 方法可以返回泛型类型 V 的值，并且可以抛出异常。而 FutureTask<V> 实现了 RunnableFuture 接口，它既是一个 Runnable（可以传给 Thread），
又可以用来在将来获取 Callable 的计算结果（通过 Future 接口的方法）。

具体步骤为：创建一个实现 Callable 接口的类并实现 call() 方法与返回结果，接着创建 FutureTask 对象，封装 Callable 实例，传入 FutureTask 给 Thread 并启动线程。

```java
public class MyCallable implements Callable<String> {
    private int taskId;
    public MyCallable(int taskId) {
        this.taskId = taskId;
    }
    @Override
    public String call() throws Exception { // 可以返回 String，也可以抛出异常
        Thread.sleep(2000);
        return "Result:" + taskId; // 返回计算结果
    }
}

public class Main {
    public static void main(String[] args) {
        // 创建 Callable
        Callable<String> callableTask = new MyCallable(100);
        // 用 FutureTask 包装 Callable
        FutureTask<String> futureTask = new FutureTask<>(callableTask);
        // 创建线程并启动
        Thread thread = new Thread(futureTask);
        thread.start();
        System.out.println("主线程空闲中...");
        try {
            // 获取异步任务的结果（这是一个阻塞调用）
            String result = futureTask.get(); // 会等待 call() 方法执行完毕
            System.out.println("Got " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

****
### 1.3 线程池

线程池就是预先创建一定数量线程的容器，用于执行大量任务，核心目的是复用线程，降低线程创建和销毁的开销，提高系统吞吐量。像上面的哪三种创建线程的方法不存在约束，
只要有调用某个方法来创建线程的话，那就会无止境地创建线程，直到系统内存爆满崩溃。而线程池可以可以复用线程来控制线程的创建与销毁次数，减少系统开销，
并且可以限制同时运行线程数量，避免因为线程过多导致系统资源耗尽。Java 提供了 java.util.concurrent 包来实现线程池，核心类是 ThreadPoolExecutor，其相关接口与类：

- Executor：最顶层的执行接口，只有一个 execute(Runnable command) 方法，所以只负责执行任务，不提供线程管理、任务状态获取等功能。当只需要把任务提交给某种执行机制，而不关心线程池内部管理时，可以使用。
- ExecutorService：继承了 Executor，是真正的线程池接口，提供了更强大的功能，如提交 Callable 任务、关闭线程池、获取任务执行状态（Future）等
- ScheduledExecutorService：继承了 ExecutorService，支持定时或周期性任务的线程池接口
- ThreadPoolExecutor：这是最核心、最标准的线程池实现类，是 Java 线程池的标准实现，通常通过工厂类配置它。Java 线程池的 标准实现，是真正控制线程池行为的核心类，所有线程池工厂（Executors）创建的线程池，内部最终都是这个类。
- Executors：线程池的工厂工具类，提供了许多静态方法来创建配置好的 ExecutorService 实例（内部仍然是 ThreadPoolExecutor）

```text
Executor (接口)
  └─ ExecutorService (接口)
        └─ ScheduledExecutorService (接口)
  └─ ThreadPoolExecutor (类，实现 ExecutorService)
Executors (工具类) --> 创建 ThreadPoolExecutor
```

ThreadPoolExecutor 核心构造参数（七参数）：

```java
public ThreadPoolExecutor(
    int corePoolSize,
    int maximumPoolSize,
    long keepAliveTime,
    TimeUnit unit,
    BlockingQueue<Runnable> workQueue,
    ThreadFactory threadFactory,
    RejectedExecutionHandler handler
)
```

1、corePoolSize (核心线程数)

线程池中长期维持的线程数量，即使它们是空闲的也不会被销毁（除非设置了 allowCoreThreadTimeOut）

2、maximumPoolSize (最大线程数)

线程池中允许存在的最大线程数量，包括核心线程数

3、keepAliveTime + unit (线程空闲存活时间)

当线程数超过 corePoolSize 时，多余的空闲线程在等待新任务时的最长时间，超过这个时间就会被终止销毁。

4、workQueue (工作队列)

用于保存等待执行的任务的阻塞队列，这是一个非常重要的参数，不同类型的队列决定了线程池的排队策略。常用队列类型：

| 队列类型                    | 特点           | 适用场景          |
| ----------------------- | ------------ | ------------- |
| `ArrayBlockingQueue`    | 有界队列，先进先出    | 限流场景，防止任务无限积压 |
| `LinkedBlockingQueue`   | 可有界也可无界，FIFO | 任务生产速度快、线程数固定 |
| `SynchronousQueue`      | 不存储元素，直接交给线程 | 高并发短任务，缓存最少   |
| `PriorityBlockingQueue` | 优先级队列        | 需要任务优先级控制     |
| `DelayQueue`            | 延迟队列         | 定时或延迟任务       |


5、threadFactory (线程工厂)

用于创建新线程的工厂，可以用于设置线程名、优先级、守护线程状态等，便于监控和调试。默认使用 Executors.defaultThreadFactory() 创建非守护线程（JVM 会等待线程执行完毕才会退出）

6、handler (拒绝策略)

当线程池和队列都已满，无法处理新提交任务时，采取的拒绝策略。默认是 AbortPolicy 拒绝策略：

| 策略类                   | 说明                                      |
| --------------------- | --------------------------------------- |
| `AbortPolicy`         | 默认策略，直接抛异常 `RejectedExecutionException` |
| `CallerRunsPolicy`    | 调用线程自己执行任务，降低提交速度                       |
| `DiscardPolicy`       | 直接丢弃任务，不抛异常                             |
| `DiscardOldestPolicy` | 丢弃队列中最老的任务，腾出空间执行新任务                    |

Executors 工厂类提供了几种预设的配置：

1、newFixedThreadPool(int nThreads) 固定大小线程池

默认配置为 corePoolSize = maximumPoolSize = nThreads，workQueue 为 LinkedBlockingQueue，线程数固定且为无界队列，适用于负载较重、需要限制线程数量的服务器场景。

2、newCachedThreadPool() 可缓存线程池

默认配置为 corePoolSize = 0，maximumPoolSize = Integer.MAX_VALUE（int 类型的最大值，2^31 - 1），keepAliveTime = 60s，
workQueue 为 SynchronousQueue（不存储元素的队列）。理论上可无限创建线程，但可能创建过多线程导致 CPU 和内存耗尽。

3、newSingleThreadExecutor() 单线程线程池

默认配置为 corePoolSize = maximumPoolSize = 1，workQueue 为 LinkedBlockingQueue（无界队列）。用于保证所有任务按提交顺序串行执行，当需要保证任务顺序执行，并且同时有后台线程异步处理时使用。

4、newScheduledThreadPool(int corePoolSize) 定时任务线程池

默认配置为 maximumPoolSize = Integer.MAX_VALUE，使用 DelayedWorkQueue，常用于执行定时或周期性任务

```java
public class ThreadPoolDemo {
    public static void main(String[] args) {
        // 1. 手动创建线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2, // corePoolSize: 常驻核心线程数
                5, // maximumPoolSize: 最大线程数
                60, // keepAliveTime: 临时线程空闲存活时间
                TimeUnit.SECONDS, // unit: 时间单位
                new ArrayBlockingQueue<>(10), // workQueue: 有界队列，容量为10
                Executors.defaultThreadFactory(), // threadFactory: 使用默认工厂
                new ThreadPoolExecutor.AbortPolicy() // handler: 默认拒绝策略，直接抛出异常
        );
        // 2. 提交任务 (20 个任务，测试线程池行为)
        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            try {
                executor.execute(() -> {
                    System.out.println("Task " + taskId + " is running on " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (RejectedExecutionException e) {
                System.err.println("Task " + taskId + " was rejected! (队列和线程池已满)");
            }
        }
        // 3. 关闭线程池
        executor.shutdown(); // 平滑关闭，不再接受新任务，等待已提交任务执行完成
        // executor.shutdownNow(); // 立即尝试停止所有正在执行的任务，并返回等待执行的任务列表
    }
}
```

```text
Task 15 was rejected! (队列和线程池已满)
Task 16 was rejected! (队列和线程池已满)
Task 17 was rejected! (队列和线程池已满)
Task 18 was rejected! (队列和线程池已满)
Task 19 was rejected! (队列和线程池已满)
Task 0 is running on pool-1-thread-1
Task 14 is running on pool-1-thread-5
Task 1 is running on pool-1-thread-2
Task 12 is running on pool-1-thread-3
Task 13 is running on pool-1-thread-4
Task 2 is running on pool-1-thread-1
Task 3 is running on pool-1-thread-5
Task 4 is running on pool-1-thread-4
Task 6 is running on pool-1-thread-3
Task 5 is running on pool-1-thread-2
Task 7 is running on pool-1-thread-1
Task 8 is running on pool-1-thread-5
Task 9 is running on pool-1-thread-4
Task 10 is running on pool-1-thread-3
Task 11 is running on pool-1-thread-2
```

使用强制关闭：

```text
Task 15 was rejected! (队列和线程池已满)
Task 16 was rejected! (队列和线程池已满)
Task 17 was rejected! (队列和线程池已满)
Task 18 was rejected! (队列和线程池已满)
Task 19 was rejected! (队列和线程池已满)
java.lang.InterruptedException: sleep interrupted
	at java.base/java.lang.Thread.sleep(Native Method)
	at MyThreadPool.lambda$main$0(MyThreadPool.java:22)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	at java.base/java.lang.Thread.run(Thread.java:833)
...
Task 13 is running on pool-1-thread-4
Task 14 is running on pool-1-thread-5
Task 12 is running on pool-1-thread-3
Task 1 is running on pool-1-thread-2
Task 0 is running on pool-1-thread-1
```

****
### 1.4 CompletableFuture 异步编排

异步编排指的是管理和协调多个异步任务的执行流程，包括串行化、并行化、聚合、异常处理等操作，传统的线程返回值需要通过 Future 获取结果且需要阻塞调用 get()，
而 CompletableFuture 提供了大量的 API 将多个异步任务以各种方式组合拼接起来，形成一个完整的、非阻塞的异步工作流。

#### 1.4.1 启动异步任务

CompletableFuture 提供了两个静态方法来启动异步任务：supplyAsync 和 runAsync，它们的区别在于任务是否有返回值。

1、supplyAsync：启动有返回值的异步任务，它接收一个 Supplier 函数式接口，该接口的定义是 () -> T，即一个不接受参数但返回结果的函数。

```java
// 使用默认的 ForkJoinPool.commonPool() 作为线程池
static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)

// 使用自定义的 Executor 作为线程池
static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)
```

```java
public class SupplyAsyncDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 使用默认线程池
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            // 在新的线程中执行的耗时任务
            System.out.println("Task 1 : " + Thread.currentThread().getName());
            simulateLongRunningTask(2); // 模拟耗时 2 秒的操作
            return "Result from Task 1";
        });

        // 使用 Lambda 表达式引用已有方法
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(SupplyAsyncDemo::calculateSomething);

        // 使用自定义线程池
        // 创建一个固定大小的自定义线程池
        ExecutorService customExecutor = Executors.newFixedThreadPool(3, r -> {
            Thread thread = new Thread(r);
            thread.setName("自定义线程：" + thread.getId());
            return thread;
        });

        CompletableFuture<Double> future3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Task 3 : " + Thread.currentThread().getName());
            simulateLongRunningTask(1);
            return 42.0;
        }, customExecutor); // 显式指定自定义线程池

        // 主线程继续执行，不会被阻塞
        System.out.println("主线程可以执行其它任务: " + Thread.currentThread().getName());

        // 如果需要获取结果，可以调用 get()（这会阻塞主线程）
        String result1 = future1.get(); // 阻塞，直到 future1 完成
        Integer result2 = future2.get();
        Double result3 = future3.get();

        System.out.println("Result 1: " + result1);
        System.out.println("Result 2: " + result2);
        System.out.println("Result 3: " + result3);

        // 关闭自定义线程池，CompletableFuture 中的默认线程池由 JVM 管理生命周期，所以不用手动关闭
        customExecutor.shutdown();
    }

    // 模拟耗时操作
    private static void simulateLongRunningTask(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private static Integer calculateSomething() {
        System.out.println("线程计算中: " + Thread.currentThread().getName());
        simulateLongRunningTask(3);
        return 100 * 2;
    }
}
```

supplyAsync 调用会立即返回一个 CompletableFuture<String> 对象，而不会阻塞调用线程，传入的 Supplier 任务也会被提交到线程池中异步执行，而任务的返回值则会被自动设置到 CompletableFuture 对象中。
如果不指定 Executor，默认使用 ForkJoinPool.commonPool()，其生命周期由 JVM 管理，被整个 JVM 共享且配置固定，随 JVM 一起消逝。

```text
线程计算中: ForkJoinPool.commonPool-worker-2
Task 1 : ForkJoinPool.commonPool-worker-1
主线程可以执行其它任务: main
Task 3 : 自定义线程：18
Result 1: Result from Task 1
Result 2: 200
Result 3: 42.0
```

2、runAsync：启动无返回值的异步任务，它接收一个 Runnable 接口，定义是 () -> void

```java
// 使用默认的 ForkJoinPool.commonPool()
static CompletableFuture<Void> runAsync(Runnable runnable)

// 使用自定义的 Executor
static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)
```

```java
public class RunAsyncDemo {
    public static void main(String[] args) {
        // 执行异步任务但不返回结果
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " 执行无返回值的异步任务 2 s");
            simulateLongRunningTask(2);
            System.out.println(Thread.currentThread().getName() + " 执行完毕");
            // 没有 return 语句
        });

        // 记录日志或发送通知等副作用操作
        CompletableFuture<Void> logFuture = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " [INFO] 开始记录日志");
        });

        // 使用自定义线程池执行资源释放任务
        ExecutorService ioBoundExecutor = Executors.newCachedThreadPool();
        CompletableFuture<Void> fileOperationFuture = CompletableFuture.runAsync(() -> {
            System.out.println("执行 IO 操作: " + Thread.currentThread().getName());
            // 模拟文件I/O操作
            simulateLongRunningTask(1);
        }, ioBoundExecutor);

        // 虽然不需要结果，但可以等待任务完成（例如，在主程序退出前）
        future1.join(); // join() 与 get() 类似，阻塞当前线程，直到对应的异步任务执行完成，但不抛出受检异常
        fileOperationFuture.join();

        System.out.println("主线程执行完毕");
        ioBoundExecutor.shutdown();
    }

    private static void simulateLongRunningTask(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

runAsync 返回 CompletableFuture<Void>，因为 Runnable 没有返回值，它非常适合执行无需返回结果的操作，如日志记录、发送通知、清理临时资源等。这里返回的 Future 对象是泛型 Void，
它的 get() 方法总是返回 null，它主要用于表示任务完成的状态，而不是携带数据。

****
#### 1.4.2 完成回调和异常感知

1、回调

传统的 Future.get() 是主动拉取返回数据，需要主动调用一个阻塞的方法去询问结果。CompletableFuture 的完成回调是被动通知，需要先告诉 Future 完成后该做什么，
它就会在任务结束时自动通知并执行相应操作，调用线程完全不会被阻塞。回调方法：

- thenApply(Function<T,U>)：接收上一个任务的结果 T，进行转换，返回新结果 U
- thenAccept(Consumer<T>)：接收上一个任务的结果 T，进行消费（如打印），无返回值
- thenRun(Runnable)：不关心上一个任务的结果，只在它完成后执行一个 Runnable 接口

```java
CompletableFuture<Void> voidCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("第一阶段：获取用户ID");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 123;
        })
        .thenApply(userId -> { // 转换：userId -> userObject
            System.out.println("第二阶段：根据ID(" + userId + ")查询用户详情");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "User_" + userId; // 模拟返回用户对象
        })
        .thenAccept(user -> { // 消费：使用用户对象
            System.out.println("第三阶段：发送欢迎邮件给 " + user);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        })
        .thenRun(() -> { // 最终回调：不依赖任何结果
            System.out.println("第四阶段：所有流程完成，记录日志");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
// 主线程立即继续，不会被上面的任何操作阻塞
System.out.println("主线程已启动异步流水线，现在继续处理其他请求...");
// 等待一段时间让异步任务执行
voidCompletableFuture.get();
```

```text
第一阶段：获取用户ID
主线程已启动异步流水线，现在继续处理其他请求...
第二阶段：根据ID(123)查询用户详情
第三阶段：发送欢迎邮件给 User_123
第四阶段：所有流程完成，记录日志
```

需要注意的是，同步回调和异步回调是有区别的，thenApply / thenAccept / thenRun 为同步执行，回调函数会在完成上一个任务的同一个线程中立即执行，
而 thenApplyAsync / thenAcceptAsync / thenRunAsync 为异步执行，回调函数会被提交到线程池（默认或指定的），在另一个线程中执行。

```java
ExecutorService customExecutor = Executors.newFixedThreadPool(2);
CompletableFuture.supplyAsync(() -> {
    System.out.println("SupplyAsync in: " + Thread.currentThread().getName());
    return "data";
}, customExecutor)
.thenApply(data -> { // 同步回调：在 supplyAsync 的同一个线程执行
    System.out.println("thenApply (sync) in: " + Thread.currentThread().getName());
    return data.toUpperCase();
})
.thenApplyAsync(data -> { // 异步回调：提交到线程池，可能换另一个线程执行
    System.out.println("thenApplyAsync (async) in: " + Thread.currentThread().getName());
    return data + "!";
}, customExecutor)
.thenAcceptAsync(result -> {
    System.out.println("最终结果: " + result + " in: " + Thread.currentThread().getName());
}, customExecutor);
```

2、异常感知与处理

CompletableFuture 的异常不会立即抛出，而是会沿着异步链传播，直到遇到一个异常处理方法，这类似于 try-catch 的工作方式。

1) exceptionally：捕获异常并提供降级结果，类似于 catch 块，它允许在发生异常时提供一个备用的默认值，使流程能够继续

```java
CompletableFuture.supplyAsync(() -> {
                    if (Math.random() > 0.5) {
                        throw new RuntimeException("查询数据库失败！");
                    }
                    return "Data from DB";
                })
                .thenApply(data -> data.toUpperCase()) // 如果上一步异常，此步不会执行
                .exceptionally(ex -> { // 捕获任何阶段的异常
                    System.err.println("Exception occurred: " + ex.getMessage());
                    return "Default Data"; // 提供降级结果，类型必须与正常结果一致(String)
                })
                .thenAccept(data -> System.out.println("Processing: " + data)); // 会接收到正常数据或降级数据
```

异常结果：

```text
Exception occurred: java.lang.RuntimeException: 查询数据库失败！
Processing: Default Data
```

正常结果：

```text
Processing: DATA FROM DB
```

2) handle：统一处理成功和失败，该方法无论前一阶段是成功完成还是异常完成，都会被调用，它接收两个参数：结果和异常

```java
CompletableFuture.supplyAsync(() -> {
                    if (Math.random() > 0.5) {
                        throw new RuntimeException("网络错误!");
                    }
                    return 100;
                })
                .handle((result, ex) -> { // 处理正常结果和异常
                    if (ex != null) {
                        System.out.println("操作失败，错误原因: " + ex.getMessage());
                        return 0; // 异常时返回降级值
                    } else {
                        System.out.println("操作成功！");
                        return result * 0.9; // 成功时转换结果
                    }
                })
                .thenAccept(finalPrice -> System.out.println("最终结果: $" + finalPrice));
```

正常结果：

```text
操作成功！
最终结果: $90.0
```

异常结果：

```text
操作失败，错误原因: java.lang.RuntimeException: 网络错误!
最终结果: $0
```

3) whenComplete：感知完成状态但不改变结果，whenComplete 与 handle 类似，总能被执行，但关键区别在于它不改变最终结果

```java
CompletableFuture.supplyAsync(() -> "Result")
        .whenComplete((result, ex) -> {
            if (ex != null) {
                // 只能记录日志或执行副作用，无法恢复或改变结果
                System.out.println("任务失败，返回结果: " + result); // 返回 null
            } else {
                System.out.println("任务成功: " + result);
            }
        });
// whenComplete 返回的 Future 结果与原始 Future 相同（成功则相同，失败则相同异常）
```

****
#### 1.4.3 线程串行化

线程串行化指的是将多个异步任务按照特定的顺序连接起来，形成一个任务流水线，前一个任务的输出是后一个任务的输入，而后一个任务必须等待前一个任务完成才能开始。
它与并行化（多个任务同时执行）相对，强调的是任务之间的依赖关系和执行顺序。在 CompletableFuture 中，串行化通过 thenApply、thenAccept、thenRun 等方法实现，
并返回一个新的 CompletableFuture，从而支持无限的链式调用。

串行化主要分为四类，转换结果、消费结果、单纯回调和异步展开，前三个也就是分别调用 thenApply/thenApplyAsync、thenAccept/thenAcceptAsync、thenRun/thenRunAsync 方法，
而异步展开则是用于解决嵌套的 CompletableFuture，当你有一个返回 CompletableFuture<U> 的任务，如果直接在 thenApply 中使用，则会得到嵌套对象 CompletableFuture<CompletableFuture<U>>，
而 thenCompose 方法它接收一个函数，该函数根据前一个结果返回一个新的 CompletableFuture<U>，最终返回 CompletableFuture<U>

```java
public static void main(String[] args) throws ExecutionException, InterruptedException {

    // 错误用法: 会产生 CompletableFuture<CompletableFuture<String>>
    CompletableFuture<CompletableFuture<String>> badFuture =
            CompletableFuture.supplyAsync(() -> "123")
                    .thenApply(id -> fetchNameAsync(id)); // 返回 Future<String>
  
    // 正确用法: 使用 thenCompose 展开嵌套的 CompletableFuture
    CompletableFuture<String> goodFuture =
            CompletableFuture.supplyAsync(() -> "123")
                    .thenCompose(id -> fetchNameAsync(id)); // 返回的是 Future<String>
  
    System.out.println(badFuture.get()); // java.util.concurrent.CompletableFuture@1cf4f579[Completed normally]
    System.out.println(goodFuture.get()); // User_123
}

private static CompletableFuture<String> fetchNameAsync(String id) {
    return CompletableFuture.supplyAsync(() -> "User_" + id);
}
```

而在串行化中又分为同步执行（默认行为）与异步执行，它们调用的方法区别就是异步执行后面带有 ...Async。同步回调方法（thenApply, thenAccept, thenRun）会由完成前一个任务的同一个线程来立即执行；
异步回调方法（thenApplyAsync, thenAcceptAsync 等）会将回调任务提交到线程池中执行，可能与上一个任务不在同一个线程。

****
#### 1.4.4 组合任务

CompletableFuture 提供了两大类任务组合方法，这类方法强调两个任务的结果之间的关系，需要把它们结合在一起使用：

1) 并行组合:两个任务独立运行，等它们都完成后再处理结果

- thenCombine()：两个任务都完成后，把它们的结果合并
- thenAcceptBoth()：两个任务都完成后，消费它们的结果，不返回新值
- runAfterBoth()：两个任务都完成后，执行一个新的 Runnable，不关心结果

2) 竞争组合:谁先完成就用谁的结果

- applyToEither()：任意一个任务先完成，就用它的结果做转换
- acceptEither()：任意一个任务先完成，就消费它的结果
- runAfterEither()：任意一个任务先完成，就运行一个新的 Runnable，不关心结果

1、thenCombine，两个任务并行执行，等它们都完成后，把结果合并成一个新的结果

```java
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");

CompletableFuture<String> result =
        future1.thenCombine(future2, (s1, s2) -> s1 + " " + s2);
System.out.println(result.get()); // Hello World
```

2、thenAcceptBoth，两个任务都完成后，消费它们的结果，不返回值

```java
future1.thenAcceptBoth(future2, (s1, s2) -> {
    System.out.println(s1 + " & " + s2); // Hello & World
});
```

3、runAfterBoth，两个任务完成后，只执行一个 Runnable，不关心结果

```java
future1.runAfterBoth(future2, () -> {
        System.out.println("Both done!");
});
```

4、applyToEither，任意一个任务先完成，就用它的结果做转换

```java
CompletableFuture<String> fast = CompletableFuture.supplyAsync(() -> "Fast");
CompletableFuture<String> slow = CompletableFuture.supplyAsync(() -> {
    try {
        Thread.sleep(1000);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return "Slow";
});
// 使用任意一个 CompletableFuture 对象调用 applyToEither 方法
CompletableFuture<String> result = fast.applyToEither(slow, s -> "Winner: " + s);
System.out.println(result.get()); // Winner: Fast
```

5、acceptEither，任意一个任务先完成，就消费它的结

```java
fast.acceptEither(slow, s -> {
    System.out.println("First result: " + s); // First result: Fast
});
```

6、runAfterEither，任意一个任务完成，就执行一个 Runnable

```java
fast.runAfterEither(slow, () -> {
    System.out.println("一个任务完成");
});
```

CompletableFuture 还提供了两个方法用于多任务的聚合等待，这类方法更像是并发任务的协调器，主要作用是等待多个任务完成，并不关心它们结果的组合逻辑：

1、allOf，等待所有任务完成

用于等待一组 CompletableFuture 全部完成，它返回一个新的 CompletableFuture<Void>，它只关心所有任务是否完成，不关心它们的结果，它返回的 Future 没有聚合结果，需要手动从每个原始的 Future 中获取结果，
如果任何一个任务异常完成，返回的 Future 也会异常完成。

```java
public class AllOfDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 模拟三个独立的异步任务
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> fetchUserInfo("user123"));
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> fetchProductInfo("prod456"));
        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> checkStock("prod456"));
        // 组合它们，等待所有完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3);
        // 当所有任务完成后，thenRun() 或 thenAccept() 会被触发
        CompletableFuture<Void> finalFuture = allFutures.thenRun(() -> {
            // 在这里安全地获取每个任务的结果（因为它们肯定完成了），所以调用 get() 不会阻塞
            try {
                String userInfo = future1.get();
                String productInfo = future2.get();
                Integer stock = future3.get();

                System.out.println("整合所有数据:");
                System.out.println(" - " + userInfo);
                System.out.println(" - " + productInfo);
                System.out.println(" - 库存: " + stock);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        // 等待最终结果
        finalFuture.get();
    }

    // 模拟异步服务调用
    static String fetchUserInfo(String userId) {
        sleep(1000);
        return "用户信息 " + userId;
    }

    static String fetchProductInfo(String prodId) {
        sleep(1500);
        return "商品信息 " + prodId;
    }

    static Integer checkStock(String prodId) {
        sleep(800);
        return 42;
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

```text
整合所有数据:
 - 用户信息 user123
 - 商品信息 prod456
 - 库存: 42
```

2、anyOf()，等待任意任务完成

用于等待一组 CompletableFuture 中的任意一个完成，它返回一个新的 CompletableFuture<Object>，注意返回的 Future 的结果是第一个完成的任务的结果（类型为 Object），
其他未完成的任务会继续在后台执行，但它们的結果会被忽略。

```java
public class AnyOfDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 模拟向三个不同的镜像源请求同一个数据
        CompletableFuture<String> mirror1 = CompletableFuture.supplyAsync(() -> {
            try {
                return fetchFromMirror("Mirror1", 2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<String> mirror2 = CompletableFuture.supplyAsync(() -> {
            try {
                return fetchFromMirror("Mirror2", 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }); // 这个最快
        CompletableFuture<String> mirror3 = CompletableFuture.supplyAsync(() -> {
            try {
                return fetchFromMirror("Mirror3", 3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // 组合它们，等待任意一个完成
        CompletableFuture<Object> anyFuture = CompletableFuture.anyOf(mirror1, mirror2, mirror3);

        // 处理第一个返回的结果
        anyFuture.thenAccept(result -> {
            System.out.println("第一个响应被接收: " + result);
        });

        // 等待并获取结果（结果是Object类型，需要强转）
        String firstResult = (String) anyFuture.get();
        System.out.println("镜像: " + firstResult);
    }

    static String fetchFromMirror(String mirrorName, int delay) throws InterruptedException {
        sleep(delay);
        return "数据来自 " + mirrorName;
    }
}
```

****
## 2. 商品详情页的环境搭建

搭建一个新的 html 页面就需要创建新的自定义域名，这里取名为 item.gulimall.com，在 hosts 文件中添加即可：

```text
192.168.0.110 item.gulimall.com
```

接着把静态资源都添加进 nginx，并修改 html 页面的访问路径为 nginx 中的路径：

```shell
cp -r /mnt/d/docker_dataMountDirectory/gmall-static-resource/item/. /nginx/html/static/item
```

接着便是修改网关的路径转发，因为商品详情是归为 product 服务的，所以直接在原有的网关配置下新增一个 Host 的要求即可：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: nginx_host_route
          uri: lb://gulimall-product
          predicates:
            - Host=gulimall.com,item.gulimall.com
```

成功后便需要新增一个 Controller 来处理页面跳转请求，让请求转发到对应的 html 页面：

```java
@GetMapping("/{skuId}.html")
public String item(@PathVariable("skuId") Long skuId, Model model) {
    System.out.println("准备查询的 skuId:" + skuId);
    return "item";
}
```

因为商品详情页是针对某个具体的商品的，所以需要接收它的 skuId，后端处理完毕，则需要修改前端的跳转路径了，在前端的检索页面，点击某个商品后应该携带它的 skuId 进行跳转，
并且跳转到当前自定义的域名 item.gulimall.com，所以需要修改 search 服务下的 list.html 页面：

```html
<div class="rig_tab">
    <div th:each="product:${result.getProducts()}">
        <p class="da">
            <a th:href="|http://item.gulimall.com/${product.skuId}.html|">
                <img th:src="${product.getSkuImg()}" class="dim">
            </a>
        </p>
    </div>
</div>
```

这里的 skuId 是动态获取的，因为检索页的商品也是根据后端查询 es 得到的数据，所以这些数据都存在了域中，因此可以较为方便的动态获取 skuId。

****
## 3. 商品详情页的模型设计

商品详情页需要展示商品的完整信息，包括SKU基本信息、图片、销售属性、SPU描述和规格参数等。这个模型设计采用分层结构，将不同类型的信息组织在不同的子对象中，
确保数据结构的清晰性和可维护性。

在电商系统里，一个商品详情页（用户点击某个 sku 进入的商品详情页面），要展示的信息非常多，来源也很分散，比如：

- sku 基本信息来自 sku_info 表 
- 图片来自 sku_images 表 
- 销售属性（比如颜色、内存大小组合）来自 sku_sale_attr_value 表 
- 商品介绍来自 spu_info_desc 表 
- 规格参数来自 attr/attr_group/product_attr_value 表

如果每个部分都让前端去调接口，那就需要多次请求，所以在后端就需要聚合这些分散的信息，打包成一个 VO 对象，一次性返回给前端：

```java
@Data
public class SkuItemVo {

    // sku 基本信息
    private SkuInfoEntity skuInfo;

    // sku 图片信息
    private List<SkuImagesEntity> images;

    // 获取 spu 的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;

    // 获取 spu 的介绍
    private SpuInfoDescEntity spuInfoDesc;

    // 获取 spu 规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;

    @Data
    public static class SkuItemSaleAttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValues;
    }

    @Data
    public static class SpuItemAttrGroupVo {
        private String groupName;
        private List<SpuBaseAttrVo> attrs;
    }

    @Data
    public static class SpuBaseAttrVo {
        private String attrName;
        private String attrValue;
    }

}
```

该 Vo 对象封装了商品详情页的一些常见属性：

1、skuInfo：SKU 基本信息

该属性对应数据库中的 sku_info 表，它用于展示某个具体 sku 的基础信息，如价格、重量、标题、副标题、默认图片等，而前端页面的商品标题、价格等就是直接从这里获取的。

2、images：SKU 图片信息

用于封装当前 sku 对应的图片集。

3、saleAttr：SPU 层面的销售属性组合

该属性是 spuId 下所有 sku 的销售属性值组合，同一个 spu 比如 "iPhone 14 Pro Max" 下，会有多个 sku（比如不同颜色、不同内存），销售属性则是用来描述该商品有哪些可选的销售属性，
以及每个属性有哪些值。前端详情页的可选规格如手机颜色、版本、内存、套餐等就是从这里获取的。SkuItemSaleAttrVo 内部结构：

```java
@Data
public static class SkuItemSaleAttrVo {
    private Long attrId; 
    private String attrName; // 属性名，比如 "颜色"
    private List<String> attrValues; // 属性可选值，比如 ["黑色", "紫色", "银色"]
}
```

4、spuInfoDesc：商品描述

该数据从 spu_info_desc 表获取，它并不是普通的文本描述，而是一组图片描述，是对 spu 的图文描述，通常是后台商家录入的商品介绍（图片版），
前端详情页的商品详情板块（文字描述 + 图文详情）就是从这获取的。

5、groupAttrs：商品规格参数

该属性是用来将商品的参数信息例如 CPU 型号、电池容量、尺寸重量等按照分组整理展示，它与 skuInfo 是有区别的，它通常是作为前端详情页的某个商品下的总体信息。内部结构：

```java
@Data
public static class SpuItemAttrGroupVo {
    private String groupName; // 参数组名称，比如 "主体参数"
    private List<SpuBaseAttrVo> attrs; // 该组下的参数项
}

@Data
public static class SpuBaseAttrVo {
    private String attrName; // 参数名，比如 "机身颜色"
    private String attrValue; // 参数值，比如 "深空黑"
}
```

****
## 4. SkuItemVo 对象的数据封装

Controller 层：

因为是前端点击某个具体的商品后再显示该商品的详情页面，所以直接在这个 Controller 的控制方法中进行该商品的展示数据的封装。

```java
@GetMapping("/{skuId}.html")
public String item(@PathVariable("skuId") Long skuId, Model model) {
    System.out.println("准备查询的 skuId:" + skuId);
    SkuItemVo skuItemVo = skuInfoService.item(skuId);
    model.addAttribute("item", skuItemVo);
    return "item";
}
```

Service 层：

在创建 SkuItemVo 模型时有记录总共创建了五个关键字段用于展示数据，所以在封装时要获取这五个字段所需的信息：

```java
@Override
public SkuItemVo item(Long skuId) {
    SkuItemVo skuItemVo = new SkuItemVo();
    Long spuId = 0L;
    Long catalogId = 0L;
    // 1. sku 基本信息获取
    SkuInfoEntity skuInfoEntity = getById(skuId);
    if (skuInfoEntity != null) {
        skuItemVo.setSkuInfo(skuInfoEntity);
        spuId = skuInfoEntity.getSpuId();
        catalogId = skuInfoEntity.getCatalogId();
    }

    // 2. sku 图片信息
    List<SkuImagesEntity> SkuImagesEntities =  skuImagesService.getImagesBySkuId(skuId);
    if (SkuImagesEntities != null && !SkuImagesEntities.isEmpty()) {
        skuItemVo.setImages(SkuImagesEntities);
    }
    // 3. 获取 spu 销售属性组合
    List<SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
    if (skuItemSaleAttrVos != null && !skuItemSaleAttrVos.isEmpty()) {
        skuItemVo.setSaleAttr(skuItemSaleAttrVos);
    }

    // 4. 获取 spu 描述属性
    SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
    if (spuInfoDescEntity != null) {
        skuItemVo.setSpuInfoDesc(spuInfoDescEntity);
    }
    // 5. 获取 spu 规格参数
    List<SpuItemAttrGroupVo> spuItemAttrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
    if (spuItemAttrGroupVos != null && !spuItemAttrGroupVos.isEmpty()) {
        skuItemVo.setGroupAttrs(spuItemAttrGroupVos);
    }
    return skuItemVo;
}
```

1、sku 基本信息获取

该数据的获取较为简单，因为前端会传递一个 skuId 过来，可以直接利用它查询出一个 SkuInfoEntity 对象，然后把该对象赋值给 SkuItemVo 对象的 skuInfo 字段。

2、sku 图片信息获取

这个同理，直接通过 skuId 查询对应的数据库即可。

3、spu 销售属性组合获取

因为时获取 spu 的相关信息，所以肯定要用到 spuId，而 spuId 可以通过查询出的 SkuInfoEntity 获取（通过 spuId 关联），所以此时创建一个方法，通过 spuId 查询出销售属性。

```java
@Override
public List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId) {
    return skuSaleAttrValueDao.getSaleAttrsBySpuId(spuId);
}
```

```xml
<select id="getSaleAttrsBySpuId" resultType="com.project.gulimall.product.domain.vo.SkuItemSaleAttrVo">
    SELECT
        psav.attr_id,
        psav.attr_name,
        GROUP_CONCAT(DISTINCT psav.attr_value ORDER BY psav.attr_value SEPARATOR ',') AS attr_values
    FROM
        pms_sku_sale_attr_value psav
            LEFt JOIN
        pms_sku_info psi ON psav.sku_id = psi.sku_id
    WHERE
        psi.spu_id = #{spuId}
    GROUP BY
        psav.attr_id, psav.attr_name;
</select>
```

数据库中关于销售属性的表为 pms_sku_sale_attr_value：

| 名称      | 类型    | 长度 | 小数点 | 不是 null | 虚拟 | 键 | 注释         |
| --------- | ------- | ---- | ------ | --------- | ---- | ---- | ------------ |
| id        | bigint  |      |        | √         |      | 1    | id           |
| sku_id    | bigint  |      |        |           |      |      | sku_id       |
| attr_id   | bigint  |      |        |           |      |      | attr_id      |
| attr_name | varchar | 200  |        |           |      |      | 销售属性名   |
| attr_value| varchar | 200  |        |           |      |      | 销售属性值   |
| attr_sort | int     |      |        |           |      |      | 顺序         |

可以看到它并没有关联 spu，所以只能通过查询 pms_sku_info 表来获取对应的 skuId 然后再查询此表获取所有的 attr_name 和对应的 attr_value。不过这张表中的数据展示为：

| id  | sku_id | attr_id | attr_name | attr_value   | attr_sort |
| --- | ------ | ------- | --------- | ------------ | --------- |
| 1   | 1      | 9       | 颜色      | 星河银       | (Null)    |
| 2   | 1      | 12      | 版本      | 8GB+256GB    | (Null)    |
| 3   | 2      | 9       | 颜色      | 星河银       | (Null)    |
| 4   | 2      | 12      | 版本      | 8GB+128GB    | (Null)    |
| 5   | 3      | 9       | 颜色      | 亮黑色       | (Null)    |
| 6   | 3      | 12      | 版本      | 8GB+256GB    | (Null)    |
| 7   | 4      | 9       | 颜色      | 亮黑色       | (Null)    |
| 8   | 4      | 12      | 版本      | 8GB+128GB    | (Null)    |
| 9   | 5      | 9       | 颜色      | 翡冷翠       | (Null)    |
| 10  | 5      | 12      | 版本      | 8GB+256GB    | (Null)    |
| 11  | 6      | 9       | 颜色      | 翡冷翠       | (Null)    |
| 12  | 6      | 12      | 版本      | 8GB+128GB    | (Null)    |
| 13  | 7      | 9       | 颜色      | 罗兰紫       | (Null)    |
| 14  | 7      | 12      | 版本      | 8GB+256GB    | (Null)    |
| 15  | 8      | 9       | 颜色      | 罗兰紫       | (Null)    |
| 16  | 8      | 12      | 版本      | 8GB+128GB    | (Null)    |
| 17  | 9      | 9       | 颜色      | 黑色         | (Null)    |
| 18  | 9      | 12      | 版本      | 128GB        | (Null)    |
| 19  | 10     | 9       | 颜色      | 黑色         | (Null)    |
| 20  | 10     | 12      | 版本      | 256GB        | (Null)    |
| 21  | 11     | 9       | 颜色      | 黑色         | (Null)    |
| 22  | 11     | 12      | 版本      | 64GB         | (Null)    |
| 23  | 12     | 9       | 颜色      | 白色         | (Null)    |
| 24  | 12     | 12      | 版本      | 128GB        | (Null)    |

可以发现，如果直接通过 skuId 查询的话就会获取到许多重复的数据，因此得对这些数据进行分组与去重，一个 skuId 对应属性名以及该属性名下的非重复的属性值。所以需要对 attrId 和 attrName 进行分组，
但是光分组还是会存在重复的值，而 GROUP_CONCAT(DISTINCT psav.attr_value ORDER BY psav.attr_value SEPARATOR ',') 函数就是对分组的值进行去重的，并且对这些值进行 "," 拼接。

GROUP_CONCAT(...) AS attr_values：

- GROUP_CONCAT：MySQL 的字符串聚合函数，把多行的值拼接成一个字符串
- DISTINCT：去重，避免重复的属性值（比如有多个 SKU 都是“黑色”）
- ORDER BY psav.attr_value：让拼接结果有序，比如值是 128G,256G,512G
- SEPARATOR ','：用逗号分隔

不过最终查询出的 attr_values 是一个字符串，并不能直接封装进 SkuItemSaleAttrVo 中，所以这里直接将 SkuItemSaleAttrVo 的 attrValues 字段修改为 String 类型，
后续再通过 Java 代码对 "," 进行分割取值即可。

```java
@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private String attrValues;
}
```

拼接结果：

| attr_id | attr name | attr values                  |
| ------- | --------- | ---------------------------- |
| 9       | 颜色      | 白色,紫色,红色,绿色,黄色,黑色 |
| 12      | 版本      | 128GB,256GB,64GB             | 

测试：

```java
@Test
void testGetSaleAttrsBySpuId() {
    List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueService.getSaleAttrsBySpuId(16L);
    System.out.println(saleAttrsBySpuId);
}
```

```text
[
  SkuItemSaleAttrVo(attrId=7, attrName=入网型号, attrValues=A2217), 
  SkuItemSaleAttrVo(attrId=9, attrName=颜色, attrValues=白色,黑色), 
  SkuItemSaleAttrVo(attrId=10, attrName=内存, attrValues=12GB,8GB)
]
```

4、spu 描述属性获取

同理，直接通过 spuId 查询 pms_spu_info_desc 表即可。

5、spu 规格参数获取

```java
@Override
public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
    return attrGroupDao.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
}
```

```xml
<select id="getAttrGroupWithAttrsBySpuId" resultMap="SpuItemAttrGroupVo">
    SELECT pav.spu_id,
           ag.attr_group_name,
           ag.attr_group_id,
           aar.attr_id,
           attr.attr_name,
           pav.attr_value
    FROM pms_attr_group ag
             LEFT JOIN
         pms_attr_attrgroup_relation aar ON aar.attr_group_id = ag.attr_group_id
             LEFT JOIN
         pms_attr attr ON attr.attr_id = aar.attr_id
             LEFT JOIN
         pms_product_attr_value pav ON pav.attr_id = attr.attr_id AND pav.spu_id = #{spuId}
    WHERE ag.catelog_id = #{catalogId};
</select>
```

关于 spu 的规格参数，首先需要先从 pms_attr_group 表中获取到分组信息的 id 和 name（通过三级分类 id 可以查询到），name 是用于封装进 SpuItemAttrGroupo 的 groupName 字段的，
而 id 则是用于查询 pms_attr_attrgroup_relation 表的，这样才能拿到每个分组下的对应的 attrId，再通过 attrId 查询 pms_attr 表获取到分组下的每个 attrId 对应的名称，
当然除了获取到 attrName，还需要获取到 attrValue，而 attrValue 则需要通过 pms_product_attr_value 表查询，再通过 spuId 和 attrId 准确查询 pms_product_attr_value 表中的数据，
最终再将联合的表数据通过分类 id 进行筛选。

| spu_id | attr_group_name | attr_group_id | attr_id | attr_name | attr_value |
| ------ | --------------- | ------------- | ------- | --------- | ---------- |
| 1001   | 主体参数            | 101           | 201     | 颜色        | 黑色         |
| 1001   | 主体参数            | 101           | 201     | 颜色        | 白色         |
| 1001   | 主体参数            | 101           | 202     | 重量        | 180g       |
| 1001   | 屏幕参数            | 102           | 203     | 屏幕尺寸      | 6.7英寸      |

不过这里封装的 SpuItemAttrGroupVo 对象的结构为：

```java
@Data
public class SpuItemAttrGroupVo {
    private String groupName;
    private List<Attr> attrs;
}
```

也就是说它是一个嵌套对象类型，从数据库中查询出的数据是无法直接封装进去的，所以得使用自定义类型映射，将查询出的字段准确的映射到某个字段或者内置对象中的某个字段：

```xml
<resultMap id="SpuItemAttrGroupVo" type="com.project.gulimall.product.domain.vo.SpuItemAttrGroupVo">
    <result property="groupName" column="attr_group_name"></result>
    <collection property="attrs" ofType="com.project.gulimall.product.domain.vo.Attr">
        <result property="attrName" column="attr_name"></result>
        <result property="attrValue" column="attr_value"></result>
    </collection>
</resultMap>
```

测试：

```java
@Test
void testGetAttrGroupWithAttrsBySpuId() {
    List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupService.getAttrGroupWithAttrsBySpuId(16L, 225L);
    System.out.println(attrGroupWithAttrsBySpuId);
}
```

```text
[SpuItemAttrGroupVo(groupName=基本信息, attrs=[
Attr(attrId=null, attrName=上市年份, attrValue=2018),
Attr(attrId=null, attrName=颜色, attrValue=黑色), 
Attr(attrId=null, attrName=上市年份, attrValue=2001), 
Attr(attrId=null, attrName=机身材质工艺, attrValue=陶瓷;玻璃), 
Attr(attrId=null, attrName=机身长度（mm）, attrValue=158.3), 
Attr(attrId=null, attrName=机身颜色, attrValue=黑色), 
Attr(attrId=null, attrName=入网型号, attrValue=A2217)]),
SpuItemAttrGroupVo(groupName=主芯片, attrs=[
Attr(attrId=null, attrName=内存, attrValue=12GB), 
Attr(attrId=null, attrName=CPU型号, attrValue=骁龙665), 
Attr(attrId=null, attrName=CPU品牌, attrValue=高通(Qualcomm))
])]
```

****
## 5. 详情页渲染

### 5.1 基本数据展示

从上面封装的 SkuItemVo 对象中可以获取到商品的基本信息，这里只需要动态的从域中获取并显示在页面即可，这里展示的是商品的标题和副标题：

```html
<div class="box-name" th:text="${item.skuInfo.skuTitle}">
    华为 HUAWEI Mate 10 6GB+128GB 亮黑色 移动联通电信4G手机 双卡双待
</div>
<div class="box-hide" th:text="${item.skuInfo.skuSubtitle}">预订用户预计11月30日左右陆续发货！麒麟970芯片！AI智能拍照！
    <a href="/static/item/"><u></u></a>
</div>
```

动态显示价格：

```html
<span th:text="${#numbers.formatDecimal(item.skuInfo.price, 3, 2)}">4499.00</span>
```

商品详情页一般会直接展示商品的默认图片，所以这里直接获取封装的 SkuItemVo 的 SkuInfoEntity 对象的默认图片数据即可：

```html
<div class="probox">
    <img class="img1" alt="" th:src="${item.skuInfo.skuDefaultImg}">
    <div class="hoverbox"></div>
</div>
```

既然有展示默认图片，那也要展示其它可看的图片，也就是默认图片下面的那一栏小图片，这些在上架商品时进行过添加，是一些图片集，同样的，封装时是一个 SkuImagesEntity 对象，
所以需要从它里面获取图片集的 url，当时封装该对象时并不是直接把所有图片拼接成字符串再存储，而是一张图片就是一个对象，所以这里遍历获取对象即可获取到所有图片，
但当时为了测试添加过多个空值，所以这里需要对图片的 url 是否为空进行判断：

```html
<div class="box-lh-one">
  <ul>
    <li th:each="img : ${item.images}" th:if="${!#strings.isEmpty(img.imgUrl)}"><img th:src="${img.imgUrl}" /></li>
  </ul>
</div>
```

这里则是动态的展示商品的销售属性，也就是商品详情页选择某个商品的具体参数时的展示效果，从数据库中查到了几个属于该商品的销售属性，那就展示它们与对应的值。
上面有记录，封装的销售属性的值是 String 类型的字符串，而详情页需要展示对应的值，所以这里要把字符串通过 "," 分割为集合，一个元素对应一个具体的销售属性的值：

```html
<div class="box-attr-3">
    <div class="box-attr clear" th:each="attr : ${item.saleAttr}">
        <dl>
            <dt>选择[[${attr.attrName}]]</dt>
            <dd th:each="val : ${#strings.listSplit(attr.attrValues, ',')}">
                <a href="/static/item/#">
                    <!--<img src="/static/item/img/59ddfcb1Nc3edb8f1.jpg" />-->
                    [[${val}]]
                </a>
            </dd>
        </dl>
    </div>
</div>
```

商品 spu 的描述图也是通过封装的 SkuItemVo 动态获取，不过封装的是一个 SkuImagesEntity 对象，所以需要从该对象中获取图片的 url，而当时存储也是直接把多个 url 拼接成字符串，
所以这里的操作和上面一样：

```html
<img class="xiaoguo" th:src="${describe}" th:each="describe : ${#strings.listSplit(item.spuInfoDesc.decript, ',')}"/>
```

这里展示的就是 spu 的规格参数了，它是在商品详情页下滑，在描述图片后面的信息，因为后端已经封装好了，这里直接获取即可：

```html
<div class="guiGe" th:each="group : ${item.groupAttrs}">
    <h3 th:text="${group.groupName}">主体</h3>
    <dl>
        <div th:each="attr : ${group.attrs}">
            <dt th:text="${attr.attrName}">品牌</dt>
            <dd th:text="${attr.attrValue}">华为(HUAWEI)</dd>
        </div>
    </dl>
</div>
```

****
### 5.2 销售属性渲染

在商品详情页面的销售属性通常有多个属性名与其对应的多个属性值，而用户需要购买或者查看某个具体的产品时，后端就需要获取其具体的 skuId 才能查询数据库获取信息。
例如一部手机的销售参数有两个，分别为颜色和内存，而这两个有多个属性值：

- 颜色：白色、黑色
- 内存：8 GB、12 GB

此时就会有 2 x 2 = 4 种选择，而后端的操作就是在用户选定某个颜色与内存时感知到其 skuId。在查询商品的销售属性时查询的表为 pms_sku_sale_attr_value 和 pms_sku_info，
通过 pms_sku_info 获取 spu 对应的 skuId 再去查询其对应的销售属性，但是当时拼接查询的结果为：


| attr_id | attr name | attr values                  |
| ------- | --------- | ---------------------------- |
| 9       | 颜色      | 白色,紫色,红色,绿色,黄色,黑色 |
| 12      | 版本      | 128GB,256GB,64GB             | 

这种结果虽然可以较为直观并方便的获取具体的数据展示在前端，但是无法体现出某个颜色和版本的组合为哪个 skuId，所以此时应该考虑如何在查询销售属性时带上 skuId，
例如颜色属性带上其对应的 skuId，版本属性也带上其对应的 skuId，当用户选择的颜色和版本号对应的 skuId 出现重复时，证明当前 skuId 为真正的 skuId。例如：

| attr_id | attr_name | attr_value | sku_ids |
| ------- | --------- | ---------- | ------- |
| 9       | 颜色        | 白色         | 1,2     |
| 9       | 颜色        | 黑色         | 3,4     |
| 12      | 内存        | 8GB        | 1,3     |
| 12      | 内存        | 12GB       | 2,4     |
 
此时如果选中 "白色8GB"，那么 skuId 就有 "1、2、1、3"，其中重复的为 1，那么 "白色8GB" 这个商品的 skuId 就是 1，后续查询该 sku 的信息即可。那么就需要修改原来写的 sql，
不应该直接把每个属性名对应的属性值进行分组与去重，而是应该再加上属性值进行分组：

```xml
<select id="getSaleAttrsBySpuId" resultMap="SkuItemSaleAttrVo">
    SELECT
        ssav.`attr_id` attr_id,
        ssav.`attr_name` attr_name,
        ssav.`attr_value`,
        GROUP_CONCAT(DISTINCT info.`sku_id`) sku_ids
    FROM
        `pms_sku_info` info
            LEFT JOIN
        `pms_sku_sale_attr_value` ssav ON ssav.`sku_id` = info.`sku_id`
    WHERE
        info.`spu_id` = #{spuId}
    GROUP BY
        ssav.`attr_id`, ssav.`attr_name`, ssav.`attr_value`;
</select>
```

新的 sql 查询语句就是再分组的基础上新增了属性值，并且不再使用 GROUP_CONCAT 把多行的不同的值拼接成一个字符串（如果还拼接就没法区分某个具体的颜色有哪些 skuId 了），
而是把查询出的 skuId 拼接在一起，因为参与分组的为属性名和属性值，所以每个 "颜色：白色"... 就会有其对应的 skuId 存在，"内存:8GB"... 同理，这样就达到了目的。
既然修改了 sql 查询结果的返回值，那么就需要修改对应的进行封装的对象：

```java
@Data
@ToString
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
```

```java
@Data
public class AttrValueWithSkuIdVo {
    private String attrValue;
    private String skuIds;
}
```

用 AttrValueWithSkuIdVo 接收属性名和 skuId，因为一个属性名下会有多个对应的属性值和 skuId，所以把它作为 SkuItemSaleAttrVo 的一个字段的集合类型。同理，修改了接收返回结果的对象，
并且这里还使用了嵌套对象，所以在 Mapper 层还需要自定义返回结果集映射：

```xml
<resultMap id="SkuItemSaleAttrVo" type="com.project.gulimall.product.domain.vo.SkuItemSaleAttrVo">
    <result column="attr_id" property="attrId"></result>
    <result column="attr_name" property="attrName"></result>
    <collection property="attrValues" ofType="com.project.gulimall.product.domain.vo.AttrValueWithSkuIdVo">
        <result column="attr_value" property="attrValue"></result>
        <result column="sku_ids" property="skuIds"></result>
    </collection>
</resultMap>
```

测试：

```text
[
  SkuItemSaleAttrVo(attrId=7, attrName=入网型号, 
    attrValues=[AttrValueWithSkuIdVo(attrValue=A2217, skuIds=29,30,31,32)]
  ), 
  SkuItemSaleAttrVo(attrId=9, attrName=颜色, 
    attrValues=[AttrValueWithSkuIdVo(attrValue=白色, skuIds=31,32), AttrValueWithSkuIdVo(attrValue=黑色, skuIds=29,30)]
  ), 
  SkuItemSaleAttrVo(attrId=10, attrName=内存, 
    attrValues=[AttrValueWithSkuIdVo(attrValue=12GB, skuIds=30,32), AttrValueWithSkuIdVo(attrValue=8GB, skuIds=29,31)]
  )
]
```

因为修改了封装的对象结构，所以前端动态展示销售属性的地方也要进行修改：

```html
<div class="box-attr clear" th:each="attr : ${item.saleAttr}">
    <dl>
        <dt>选择[[${attr.attrName}]]</dt>
        <dd th:each="vals : ${attr.attrValues}">
            <a href="/static/item/#">
                <!--<img src="/static/item/img/59ddfcb1Nc3edb8f1.jpg" />-->
                [[${vals.attrValue}]]
            </a>
        </dd>
    </dl>
</div>
```

因为上面把销售属性的属性值封装进了另一个对象，所以它是嵌套对象，因此需要从获取到的 saleAttr 中获取到 List<AttrValueWithSkuIdVo> 字段，
然后再通过它获取到 AttrValueWithSkuIdVo 中的 attrValue 字段。

目前关于从检索页面点击某个商品进入详情页后只展示了部分内容，而关于销售属性，应该实时显示当前点击进详情页的那个商品对应的具体销售属性，例如在检索页面点击的商品为 “华为白色8GB”，
那在销售属性那个地方就应该标志出当前的选择为 “白色8GB”，需要这样动态显示，就要在销售属性上面进行判断，判断点击的商品的 skuId 是否包含在某个属性值里。因为这里是一个循环，
所以每一次的遍历都会进行一次判断，即判断 "颜色：白色：1、2、3..." 中是否包含从检索页面点击的商品的 skuId：

```html
<dd th:each="vals : ${attr.attrValues}">
    <a th:attr="class=${#lists.contains(#strings.listSplit(vals.skuIds, ','), item.skuInfo.skuId.toString()) ? 'sku_attr_value checked' : 'sku_attr_value'}">
          <!--<img src="/static/item/img/59ddfcb1Nc3edb8f1.jpg" />-->
          [[${vals.attrValue}]]
    </a>
</dd>
```

如果包含，就让该标签携带一个 checked 标识，用于 js 判断是否需要给它添加渲染：

```html
$(function () {
    $(".sku_attr_value").parent().css({"border":"solid 1px #CCC"});
    $("a[class='sku_attr_value checked']").css({"border":"solid 1px red"});
})
```

****
### 5.3 sku 组合切换与跳转

在上面记录了从商品检索页面进入某个商品详情页时动态显示当前商品的销售属性值为哪些，而现在需要完成的是根据当前选择的销售属性，获取唯一的商品 skuId，然后通过该 skuId 挑战到对应的详情页面。
而在获取唯一 skuId 前，需要完成点击销售属性时取消上次对某个销售属性进行的标记：

```js
$(".sku_attr_value").click(function () {
    // 1. 找到当前属性分组（颜色、版本等）
    var currentAttrGroup = $(this).closest('.box-attr');
    // 2. 只移除当前分组内的 checked 类
    currentAttrGroup.find(".sku_attr_value").removeClass("checked");
    // 3. 只移除当前分组内的内联样式
    currentAttrGroup.find(".sku_attr_value").css("border", "");
    // 4. 给当前点击的元素添加checked类
    $(this).addClass("checked");
});
```

在点击某个销售属性后，需要先找到当前销售属性值对应的销售属性名，因为页面里有多个属性分组（颜色、内存、版本等），每个分组下面都有很多 `<a>` 元素，
所以需要先找到当前属性值的所属分组，再在这个分组内部操作移除 checked，.closest(...) 的作用就是从当前元素开始向上查找，找到第一个符合条件的祖先元素，
而 .box-attr 就是当前 HTML 中定义的分组容器类，例如：

```html
<div class="box-attr">
  <a class="sku_attr_value">白色</a>
  <a class="sku_attr_value">黑色</a>
</div>
<div class="box-attr">
  <a class="sku_attr_value">8GB</a>
  <a class="sku_attr_value">12GB</a>
</div>
```

如果点击 “白色”，$(this).closest('.box-attr') 就会返回第一个 <div class="box-attr">，也就是白色和黑色所在的颜色分组，然后移除当前销售属性名内的所有带有 checked 的 class。
接着同样遍历当前分组内的所有属性值，利用 .css("border", "") 移除之前设置的边框样式，以此去掉红色边框，使得点击前的状态被重置。因为之前写了个方法，给带有 checked 添加样式：


```js
$(function () {
    $(".sku_attr_value").parent().css({"border":"solid 1px #CCC"});
    $("a[class='sku_attr_value checked']").css({"border":"solid 1px red"});
})
```

点击逻辑完成后，还需要对 skuId 进行筛选合并。首先就是需要获取到每个属性名对应的属性值的 skuIds，所以需要遍历每个分组，然后在分组中找到携带 checked 的元素，找到该元素后，
把它携带的 skuIds 添加进一个数组，后续对数组中的值进行判断。所以这里需要在 `<a>` 中添加一个 skuIds 集合，取名为 skus，用来存放当前属性值对应的 sukIds：

```html
<a th:attr="class=${#lists.contains(#strings.listSplit(vals.skuIds, ','), item.skuInfo.skuId.toString()) ? 'sku_attr_value checked' : 'sku_attr_value'}"
   th:attrappend="skus=${vals.skuIds}"
>
```

```js
$(".sku_attr_value").click(function () {
    // 5. 获取 skuId
    var checkedSkuArrays = [];
    $(".box-attr").each(function () {
        var checked = $(this).find(".sku_attr_value.checked");
        if (checked.length > 0) {
            var skuIds = checked.attr("skus").split(",");
            checkedSkuArrays.push(skuIds);
        }
    });

    // 计算交集
    var result = checkedSkuArrays.reduce(function(a, b){
        return a.filter(v => b.includes(v));
    });
    console.log("选中的 SKUS:", checkedSkuArrays);
    console.log("选中的 SKU:", result);

    // 6. 跳转到该 skuId 的详情页面
    location.href = "http://item.gulimall.com./" + result + ".html"
});
```

获取到 skuIds 数组后，就要计算每个分组的这些数组的交集，reduce 就是对数组进行归约操作，这里是计算多个数组的交集，通过添加一个过滤来保留只在另一个数组中存在的值。
通过上面的方法，可以知道 checkedSkuArrays 其实是一个二维数组，例如：

```text
checkedSkuArrays = [
    ["31","32"], // 颜色分组选中的 SKU
    ["31","33"], // 内存分组选中的 SKU
    ["31","34"] // 网络类型分组选中的 SKU
]
```

而 reduce 的本质是循环遍历数组，并通过回调函数把数组缩减成一个值：

```js
var result = checkedSkuArrays.reduce(function(a, b){
    return a.filter(v => b.includes(v));
});
```

这里的 a 是累积值，也就是上一轮计算出来的交集，b 则是当前处理的小数组（当前分组的 SKU），reduce 循环步骤：

1、第一轮

- a = ["31","32"]（默认取数组第一个元素） 
- b = ["31","33"]
- 计算 a.filter(v => b.includes(v)) -> ["31"]

2、第二轮

- a = ["31"]（上一轮结果） 
- b = ["31","34"]
- 计算 a.filter(v => b.includes(v)) -> ["31"]

3、如果还有更多小数组，会继续处理直到最后一个小数组

当获取到唯一的 skuId 后就可以拼接成该商品的详细路径了。

****
## 6. 异步编排优化

Java 自带的 Executors 工厂方法（如 newFixedThreadPool、newCachedThreadPool）虽然简单，但存在一些风险:
FixedThreadPool 和 SingleThreadExecutor 使用 LinkedBlockingQueue（无界队列），在任务过多时可能导致内存溢出；CachedThreadPool 最大线程数是 Integer.MAX_VALUE，
并发过大时可能创建过多线程，压垮系统。并且这些线程池没有统一的配置入口，难以根据不同环境灵活调整参数。所以可以自定义一个线程池，根据自己的开发过程进行配置。

```java
@Data
@Component
@ConfigurationProperties(prefix = "gulimall.thread")
public class ThreadPoolConfigProperties {
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer keepAliveTime;
}
```

一般自定义一个线程池的时候都顺便定义一个配置属性类，用它来达到在配置文件中动态绑定自定义线程池中的一些属性参数，这里就是定义了核心线程数、最大线程数和存活时间作为绑定外部配置文件中的属性。
所以需要在类上面使用 @ConfigurationProperties 从配置文件里读取 gulimall.thread（自定义的前缀）前缀下的参数，例如：

```yaml
gulimall:
  thread:
    core-pool-size: 20
    max-pool-size: 200
    keep-alive-time: 10
```

而在自定义的线程池配置类中则可以使用上面定义好的配置属性，这里要使用 @EnableConfigurationProperties(ThreadPoolConfigProperties.class) 来确保 ThreadPoolConfigProperties 类被启用为一个配置属性类，
然后就可以在 threadPoolExecutor 方法中直接通过方法参数注入的方式（因为 ThreadPoolConfigProperties 会被 Spring 自动注入，所以可以直接使用）。

```java
@Configuration
@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
public class MyThreadConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties threadPoolConfigProperties) {
        return new ThreadPoolExecutor(
                threadPoolConfigProperties.getCorePoolSize(), // 20
                threadPoolConfigProperties.getMaxPoolSize(), // 200
                threadPoolConfigProperties.getKeepAliveTime(), // 10
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
```

SkuItemVo 是商品详情页的数据对象，要组装这个对象，需要查询很多不同的来源：

1. SKU 基本信息：getById(skuId)
2. SKU 图片：skuImagesService.getImagesBySkuId(skuId)
3. SPU 销售属性组合：skuSaleAttrValueService.getSaleAttrsBySpuId(spuId)
4. SPU 描述：spuInfoDescService.getById(spuId)
5. SPU 规格参数：attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId)

如果按照原来的写法：

```java
SkuInfoEntity skuInfo = getById(skuId);
List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
List<SkuItemSaleAttrVo> saleAttrs = skuSaleAttrValueService.getSaleAttrsBySpuId(skuInfo.getSpuId());
SpuInfoDescEntity desc = spuInfoDescService.getById(skuInfo.getSpuId());
List<SpuItemAttrGroupVo> groups = attrGroupService.getAttrGroupWithAttrsBySpuId(skuInfo.getSpuId(), skuInfo.getCatalogId());
```

那么这几次调用都会串行执行，每个 IO 调用都要等前一个结束才能去执行，所以点击一次商品，需要使用五个 IO 的时间，所以为了减少串行带来的时间开销，应该让它们并行执行 IO。
不过这里需要注意的是，skuInfoEntity 对象的获取对于后面的查询具有强依赖性，因为需要通过查询 sku_info 表来获取 spuId 和 catalogId，这样后面的 IO 才能正常执行，
因此 infoFuture 是基础，后续多个任务（3,4,5 步）都依赖于这个查询结果。不过 spu 销售属性、spu 描述性息和 spu 规格参数的获取之间并没有依赖关系，所以它们可以并行执行。

```java
@Override
public SkuItemVo item(Long skuId) {
    SkuItemVo skuItemVo = new SkuItemVo();

    CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
        // 1. sku 基本信息获取
        SkuInfoEntity skuInfoEntity = getById(skuId);
        if (skuInfoEntity != null) {
            skuItemVo.setSkuInfo(skuInfoEntity);
        }
        return skuInfoEntity;
    }, threadPoolExecutor);

    CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync(info -> {
        // 3. 获取 spu 销售属性组合
        List<SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(info.getSpuId());
        if (skuItemSaleAttrVos != null && !skuItemSaleAttrVos.isEmpty()) {
            skuItemVo.setSaleAttr(skuItemSaleAttrVos);
        }
    }, threadPoolExecutor);

    CompletableFuture<Void> spuDescribeFuture = infoFuture.thenAcceptAsync(info -> {
        // 4. 获取 spu 描述属性
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(info.getSpuId());
        if (spuInfoDescEntity != null) {
            skuItemVo.setSpuInfoDesc(spuInfoDescEntity);
        }
    }, threadPoolExecutor);

    CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync(info -> {
        // 5. 获取 spu 规格参数
        List<SpuItemAttrGroupVo> spuItemAttrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(info.getSpuId(), info.getCatalogId());
        if (spuItemAttrGroupVos != null && !spuItemAttrGroupVos.isEmpty()) {
            skuItemVo.setGroupAttrs(spuItemAttrGroupVos);
        }
    }, threadPoolExecutor);

    CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
        // 2. sku 图片信息
        List<SkuImagesEntity> SkuImagesEntities =  skuImagesService.getImagesBySkuId(skuId);
        if (SkuImagesEntities != null && !SkuImagesEntities.isEmpty()) {
            skuItemVo.setImages(SkuImagesEntities);
        }
    }, threadPoolExecutor);

    // 等待所有任务都完成
    try {
        CompletableFuture.allOf(saleAttrFuture, spuDescribeFuture, baseAttrFuture, imageFuture).get();
    } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
    }
    return skuItemVo;
}
```

而查询 sku 的图片信息则和上面的所有 IO 都没有依赖关系，它只依赖 skuId，而 skuId 又是前端传递过来的，所以它完全可以和 sku 基本信息获取的任务并行执行，并且它不需要像 infoFuture 一样需要返回值，
所以直接调用 runAsync() 执行一个异步任务即可。最后创建一个新的 Future（CompletableFuture.allOf(...)），它会在所有传入的 Future 都完成时才完成，而调用 get() 方法则是让主线程阻塞等待，
不过这里并没有等待 infoFuture，因为后面的那三个任务是依赖它的，所以只要它们三完成了，那代表 infoFuture 也完成了。

需要注意的是：不要在方法里关闭线程池，在方法里只需要注入线程池即可，因为自定义的线程池已经纳入 Spring 容器管理了，Spring 容器在关闭时会自动关闭它，如果在业务代码中手动关闭，
这就会导致第一次调用后线程池就被关闭，后续所有请求都无法使用线程池并抛出 RejectedExecutionException 异常，最后整个服务不可用。所以，是 Spring 管理的，那就不用手动关。

****
# 八、认证服务

## 1. 环境搭配

创建新的模块取名为 gulimall-auth-server，引入相关依赖以及注入 nacos：

```xml
<dependencies>
    <!--引入 common 服务-->
    <dependency>
        <groupId>com.project</groupId>
        <artifactId>gulimall-common</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
        <exclusions>
            <exclusion>
                <groupId>com.vaadin.external.google</groupId>
                <artifactId>android-json</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <!--thymeleaf-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <!--devtools-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

因为引入的 common 中带有数据库的依赖，而目前的初始项目用不到数据库，所以需要在启动类上排除数据库的使用：

```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GulimallAuthServerApplication {
    ...
}
```

```yaml
spring:
  application:
    name: gulimall-auth-server
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848

server:
  port: 20000
```

```yaml
spring:
  application:
    name: gulimall-auth-server

  cloud:
    nacos:
      config:
        server-addr: 127.0.0.1:8848
        file-extension: yaml # 文件后缀名
        namespace: 00f674fd-e07b-4f78-b4ea-136f0bcc5184 # auth-server
```

接着就是把静态资源放进 nginx 容器，并部署本地 hosts 与网关：

```shell
cp -r /mnt/d/docker_dataMountDirectory/gmall-static-resource/reg/. /nginx/html/static/reg
cp -r /mnt/d/docker_dataMountDirectory/gmall-static-resource/login/. /nginx/html/static/login
```

```xml
192.168.1.110 auth.gulimall.com
```

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: nginx_auth_route
          uri: lb://gulimall-auth-server
          predicates:
            - Host=auth.gulimall.com
```

然后就是配置页面跳转：

```java
@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/reg.html")
    public String regPage() {
        return "reg";
    }
}
```

配置好后即可正常访问，如果需要在其它前端页面点击相关的登录或者注册按钮也能正常跳转，那就需要在它们的 href 处讲请求路径修改为：

```http request
http://auth.gulimall.com/reg.html
http://auth.gulimall.com/login.html
```

不过只在控制层做页面跳转的话就太麻烦了，这里就可以把这些请求跳转写进视图控制器，让 Spring MVC 进行管理：

```java
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }
}
```

****
## 2. 验证码

### 2.1 验证码倒计时

```html
<a id="sendCode">发送验证码</a>
```

在用户注册页面有个发送验证码的功能，该功能由前端完成，在 `<a>` 标签里写一个 id="sendCode" 用来在 js 中获取这个元素，后续的验证码的相关功能就能绑定到它。
在用户点击发送验证码的按钮后就会进入倒计时的状态，而这个状态则是依次调用该方法实现的。setTimeout("timeoutChangeStyle()", 1000) 方法就是在 1s 后执行 timeoutChangeStyle() 方法，
只要让它循环执行 60 次即可，也就是需要设置一个全局变量，每次执行让该变量减一，直到为 0 后便不再执行 setTimeout 方法，而为了防止用户可以多次点击发送验证码按钮让多个方法同时执行加快倒计时，
就需要给按钮增加一个标识，当具有该标识时则再次点击不会触发倒计时功能。因此在第一次触发时会给该 `<a>` 标签添加一个 class 为 disable，只有在倒计时结束时才清除，
依次达到只能点击一次的目的。

```js
$(function(){
    $("#sendCode").click(function(){
        // 1. 给指定的手机发送验证码

        // 2. 倒计时
        if ($(this).hasClass("disable")) {
            // 正在倒计时
        } else {
            timeoutChangeStyle();
        }

    })
});

var num = 60;
function timeoutChangeStyle() {
    $("#sendCode").attr("class", "disable");
    if (num === 0) {
        $("#sendCode").text("发送验证码");
        num = 60;
        $("#sendCode").attr("class", "");
    } else {
        var str = num + " 秒后再次发送验证码";
        $("#sendCode").text(str);
        setTimeout(timeoutChangeStyle, 1000)
    }
    num --;
}
```

****
### 2.2 验证短信

这里使用 redis 来存储验证码，因为 redis 可以设置过期时间，所以对于存储验证码来说还是比较方便的。

Controller 层：

```java
@GetMapping("/sms/sendcode")
public R sendCode(@RequestParam("phone") String phone) {
    boolean result = sendCodeService.sendCode(phone);
    if (!result) {
       return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
    }
    return R.ok();
}
```

Service 层：

因为在前端页面刷新时就能重新点击一次发送验证码，如果不对此进行限制那么在 60s 内就可以被恶意发送多次验证码进行攻击，所以在生成随机验证码的时候添加上当前系统时间，
从 redis 中获取验证码时拿当前时间和存入的时间进行对比，如果小于 60s，那就不允许再次生成验证码，并返回错误信息给前端。

```java
@Override
public boolean sendCode(String phone) {
    String redisCode = stringRedisTemplate.opsForValue().get(PhoneCodeConstant.LOGIN_CODE_KEY + phone);
    if (redisCode != null) {
        long time = Long.parseLong(redisCode.split("_")[1]);
        if (System.currentTimeMillis() - time < 60 * 1000) {
            return false;
        }
    }

    String code = RandomUtil.randomNumbers(6) + "_" + System.currentTimeMillis();
    // 保存验证码到 redis
    stringRedisTemplate.opsForValue().set(PhoneCodeConstant.LOGIN_CODE_KEY + phone, code, PhoneCodeConstant.LOGIN_CODE_TTL, TimeUnit.MINUTES);
    // 防止同一个 phone 在 60s 内再次返送验证码
    log.debug("发送短信验证码成功，验证码：{}", code.split("_")[0]);
    return true;
}
```

而前端只需要在点击发送按钮时发送一个请求给 Controller 即可，然后根据后端返回的状态码是否成功来给出错误提示：

```js
$(function(){
    $("#sendCode").click(function(){
        // 倒计时
        if ($(this).hasClass("disable")) {
            // 正在倒计时
        } else {
            // 给指定的手机发送验证码
            $.get("/sms/sendcode?phone=" + $("#phoneNum").val(), function (data) {
                if (data.code != 0) {
                    alert(data.msg)
                }
            })
            timeoutChangeStyle();
        }
    })
});
```

****
### 2.3 注册页面填写信息的格式判断

在前端的注册页面会有一个注册表单，通过在表单中填写数据并发送请求给后端进行处理，而填写的数据会一起发送给后端，所以后端需要封装一个对象来接收数据，在接收数据时顺便对这些数据进行格式的判断：

```java
@Data
public class UserRegisterVo {
    @NotEmpty(message = "用户名必须提交")
    @Length(min = 6, max = 18, message = "用户名必须是 6 ~ 18 位字符")
    private String username;
    @NotEmpty(message = "密码必须提交")
    @Length(min = 6, max = 18, message = "密码必须是 6 ~ 18 位字符")
    private String password;
    @NotEmpty(message = "手机号须提交")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    @NotEmpty(message = "验证码必须提交")
    private String code;
}
```

然后在控制层判断格式是否正确，如果发生格式错误则把错误信息存入域中并重新跳转到注册页面：

```java
@PostMapping("/register")
public String register(@Valid UserRegisterVo userRegisterVo, BindingResult bindingResult, Model model) {
    // 如果校验出错就跳转到注册页面
    if (bindingResult.hasErrors()) {
        Map<String, String> errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        model.addAttribute("errors", errors);
        return "forward:/reg.html";
    }
    // 注册成功回到登录页
    return "redirect:/login.html";
}
```

此时前端再在表单处显示错误信息：

```html
<div class="tips" style="color:red" th:text="${errors != null ? errors.userName : ''}"></div>
```

但经过测试模式错误格式数据却发现无法重新进入注册页面，这是因为提交表单为 POST 请求，而在 Controller 用的是转发，并且当前配置了路径映射，虽然默认的转发请求并不会改变请求方式，
可是路径映射的默认方式就是 GET，它就相当于在 Controller 里写了两个方法：

```java
@GetMapping("/login.html")
public String loginPage() {
    return "login";
}

@GetMapping("/reg.html")
public String regPage() {
    return "reg";
}
```

这就会导致无法正确匹配到页面，所以此时就不适用路径映射，应该直接返回模板名，也就是 return "reg"。修改后页面能正常跳转，但是当填写的数据并没有全部错误时，
例如 errors 集合中只存储了用户名和密码的错误信息，那么此时程序又会报错，因为在前端进行判断时只对 errors 集合是否为空进行了判断，然后直接取值，并没有对每个详细的键值对进行非空判断，
所以这里应该修改一下它的判断逻辑，在取值时应该对该键值对进行判断：

```html
<div class="tips" style="color:red" th:text="${errors != null ? (#maps.containsKeys(errors, 'userName') ? errors.userName : '') : ''}"></div>
```

这里用的方法就是对集合中是否存在该 key 进行判断。不过，在刷新注册页面时并没有达到刷新的效果，而是直接进行了一次表单提交，因为上面直接返回了模板名，浏览器地址栏仍然是 /register，
而且请求方法还是 POST，此时就会再次进入该页面，浏览器金牛会重新发起 POST /register 然后触发表单重复提交。而最好的解决办法就是重定向 return "redirect:/reg.html"，
让浏览器再自动去请求 /reg.html，此时地址栏就是 /reg.html 而不是 /register，如果用户刷新页面就只会重新发起 GET /reg.html，不会重复提交表单。但重定向不能共享数据，
也就是说产生错误信息后，页面重定向到了注册页，但此时存入域中的 errors 数据就丢失了，那就失去了这么编写代码的意义了，所以这里不能再适用 Model 将数据存入域中，
而是使用 RedirectAttributes，让它携带数据进行重定向。但重定向是默认适用当前服务器的 IP + 端口的形式进行重定向，并没有使用之前自定义域名，所以不能直接重定向，
应该重定向一个完整的路径：return "redirect:http://auth.gulimall.com/reg.html" 。不过 addFlashAttribute 的数据只会暂存在 session，仅在下一次请求中可用，
一旦 /reg.html 渲染完，数据就会从 session 中移除。而分布式的项目中 session 通常不共享，因为每个请求可能被网关负载均衡到不同端口的服务，而这些服务的 session 不共享，
这就会导致 /reg.html 页面进行渲染时获取的 errors 数据为空，用户无法看到任何错误提示信息。但当前单服务的情况下没有问题，所以这个问题后续解决。

```html
<form th:action="/register" method="post" class="one">
    <div class="register-box">
        <label class="username_label">用 户 名
            <input name="userName" maxlength="20" type="text" placeholder="您的用户名和登录名">
        </label>
        <div class="tips" style="color:red" th:text="${errors != null ? (#maps.containsKeys(errors, 'userName') ? errors.userName : '') : ''}"></div>
    </div>
    <div class="register-box">
        <label class="other_label">设 置 密 码
            <input name="password" maxlength="20" type="password" placeholder="建议至少使用两种字符组合">
        </label>
        <div class="tips" style="color:red" th:text="${errors != null ? (#maps.containsKeys(errors, 'password') ? errors.password : '') : ''}"></div>
    </div>
    <div class="register-box">
        <label class="other_label">确 认 密 码
            <input maxlength="20" type="password" placeholder="请再次输入密码">
        </label>
        <div class="tips"></div>
    </div>
    <div class="register-box">
        <label class="other_label">
            <span>中国 0086∨</span>
            <input name="phone" maxlength="20" type="text" placeholder="建议使用常用手机">
        </label>
        <div class="tips" style="color:red" th:text="${errors != null ? (#maps.containsKeys(errors, 'phone') ? errors.phone : '') : ''}"></div>
    </div>
    <div class="register-box">
        <label class="other_label">验 证 码
            <input name="code" maxlength="20" type="text" placeholder="请输入验证码" class="caa">
        </label>
        <a id="sendCode">发送验证码</a>
        <div class="tips" style="color:red" th:text="${errors != null ? (#maps.containsKeys(errors, 'code') ? errors.code : '') : ''}"></div>
    </div>
</form>
```

****
## 3. 注册

### 3.1 数据封装

在 gulimall-auth-server 服务中，先对验证码进行判断是否正确，如果正确再远程调用 gulimall-member 服务进行注册。

Controller 层：

```java
@PostMapping("/register")
public String register(@Valid UserRegisterVo userRegisterVo, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    // 如果校验出错就跳转到注册页面
    if (bindingResult.hasErrors()) {
        Map<String, String> errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:http://auth.gulimall.com/reg.html";
    }
    boolean result = sendCodeService.checkCode(userRegisterVo.getPhone(), userRegisterVo.getCode());
    if (!result) {
        Map<String, String> errors = new HashMap<>();
        errors.put("code", "验证码错误");
        redirectAttributes.addFlashAttribute("errors", errors);
        // 校验出错，转发到注册页
        return "redirect:http://auth.gulimall.com/reg.html";
    }
    // 注册成功回到登录页
    return "redirect:/login.html";
}
```

Service 层：

该方法就是对前端传递的验证码与存入 redis 中的验证码进行比较，如果正确再进行远程调用，否则返回 false 告诉 Controller 层校验失败。

```java
@Override
public boolean checkCode(String phone, String code) {
    String redisCode = stringRedisTemplate.opsForValue().get(PhoneCodeConstant.LOGIN_CODE_KEY + phone);
    if (redisCode == null) {
        return false;
    } else {
        if (code.equals(redisCode.split("_")[0])) {
            // 验证码正确则删除验证码，确保只能使用一次
            stringRedisTemplate.delete(PhoneCodeConstant.LOGIN_CODE_KEY + phone);
            // 进行注册
            ...
        } else {
            return false;
        }
    }
    return false;
}
```

关于 gulimall-member 服务，则是需要进行注册功能的实现，所以需要对传过来的用户名、手机号和密码进行封装与唯一性的判断。

Controller 层：

因为在 Service 层会对用户名和手机号等数据进行唯一性的判断，如果查询数据库后发现存在相同的数据那就抛出异常，而 Service 层抛出的异常在 Controller 是可以捕获到的，
如果捕获到了那就可以进行相关处理，这样写起来比较方便。

```java
@PostMapping("/register")
public R register(@RequestBody MemberRegisterVo memberRegisterVo){
    try {
        memberService.regist(memberRegisterVo);
    } catch (Exception e) {
        ...
    }
    return R.ok();
}
```

Service 层：

在这里则需要定义两个异常类与两个方法，让它们用用户名和手机号作为条件进行查询数据库，如果存在则抛异常。

```java
@Override
public void regist(MemberRegisterVo memberRegisterVo) {
    MemberEntity memberEntity = new MemberEntity();
    // 检查用户名和手机号是否唯一，为了让 Controller 感知，可以使用异常机制
    checkPhoneUnique(memberRegisterVo.getPhone());
    checkUserNameUnique(memberRegisterVo.getUsername());

    memberEntity.setUsername(memberRegisterVo.getUsername());
    memberEntity.setMobile(memberRegisterVo.getPhone());

    // 获取会员等级对应的 id
    MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
    memberEntity.setLevelId(memberLevelEntity.getId());
    save(memberEntity);
}

@Override
public void checkPhoneUnique(String phone) {
    Long mobile = memberDao.selectCount(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getMobile, phone));
    if (mobile > 0L) {
        throw new PhoneExistException();
    }
}

@Override
public void checkUserNameUnique(String username) {
    Long username1 = memberDao.selectCount(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, username));
    if (username1 > 0L) {
        throw new UserNameExistException();
    }
}
```

```java
public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("手机号已存在");
    }
}
```

```java
public class UserNameExistException extends RuntimeException {
    public UserNameExistException() {
        super("用户名已存在");
    }
}
```

****
### 3.2 密码加密

#### 3.2.1 md5 加密

md5 可以任意长度的数据（比如一个字符串、一个文件）转换为一个固定长度（128 位，即 16 字节） 的哈希值，通常用一个 32 位十六进制数字的字符串表示。例如：
输入："hello world" 则会输出："5eb63bbbe01eeed093cb22bb8f5acdc3"。主要特点为：

1、不可逆性（单向性）

可以将任何数据转化为某个唯一的 md5 值，并且该值不会改变，除非原值发生变化才会改变。

2、唯一性（抗碰撞性）

理论上，不同的输入数据会产生不同的 md5 值，对于两个不同的数据，计算出相同 md5 值的概率极低（虽然已被证明存在碰撞，但对于绝大多数非安全敏感场景，仍可视为唯一）

3、不可预测性

只要加密值发生变化就会导致输出的 md5 值发生不可预测的变化。

主要用途：

1、数据完整性校验

文件下载时网站会提供文件的 md5 值，下载后计算本地文件的 md5 与之对比，如果一样，则说明文件下载完整无误，未被篡改。

2、数字签名

对消息生成 md5 摘要，然后用私钥对摘要进行加密，形成签名；接收方用公钥解密签名得到摘要，再计算消息的 md5 进行对比，验证消息的真实性和完整性。

3、口令加密 

以前在用户注册时，系统不存储用户的明文密码，而是存储密码的 md5 值，在登录时，系统将用户输入的密码进行 md5 计算，因为其不可逆性的特征，可以与数据库存储的 md5 值对比。
但由于 MD5 的快速计算和“彩虹表”的存在（暴力破解法，将大量的随机密码加密成 md5 然后进行对比），所以直接存储 md5 密码非常危险，并且现在也不推荐使用。

```java
public static String getMD5(String input) {
    try {
        // 1. 获取 md5 摘要器实例
        MessageDigest md = MessageDigest.getInstance("MD5");
        // 2. 将输入字符串转换为字节数组
        byte[] messageDigest = md.digest(input.getBytes());
        // 3. 将字节数组转换为字符串
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);
        // 4. 确保生成的字符串是 32 位长（前面可能补0）
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    }
}

public static void main(String[] args) {
    String s = "hello world";
    System.out.println("Your HashCode for '" + s + "' is: " + getMD5(s));
    // 输出: 5eb63bbbe01eeed093cb22bb8f5acdc3
    String s2 = "hello world.";
    System.out.println("Your HashCode for '" + s2 + "' is: " + getMD5(s2));
    // 输出一个完全不同的值：3c4292ae95be58e0c58e4e5511f09647
}
```

****
#### 3.2.2 盐值加密

如果用户 A 和用户 B 都使用密码 123456，那么直接使用 md5 加密会生成一样的随机数字，攻击者一旦破解一个密码，就等同于破解了所有使用相同密码的账户。而 “彩虹表” 的存在，
让攻击者只需简单地在这个表中查找匹配的值就能立刻得到对应的明文密码。而 “盐” 就是为了解决这两个问题诞生的。

盐是一段随机生成的、固定长度的、与用户相关联的字符串，核心特点：

- 唯一性：每个用户的盐都应该是独一无二的，通常使用密码学安全的随机数生成器生成
- 长度足够：盐的长度应足够长（例如 16 字节或更长），以确保其唯一性，增加暴力破解的难度
- 明文存储：盐不需要保密，它可以明文形式存储在数据库的用户记录中

在注册或设置密码时会为用户生成一个随机的盐，将用户输入的明文密码和盐拼接（password + salt 或 salt + password）在一起，将拼接后的字符串送入加密哈希函数进行计算并得到哈希值。
最后将最终的哈希值和盐一起存入数据库的该用户记录中。而验证密码时从数据库中取出该用户的盐和存储的哈希值，将用户本次登录输入的明文密码与取出的盐进行拼接，
然后使用相同的哈希函数计算拼接后字符串的哈希值，将计算出的新哈希值与数据库中存储的旧哈希值进行比对，如果一致，则密码正确，反之则错误。

可以使用 BCrypt 加密器进行加密，它每次加密自带一个随机的盐，存储时直接包含在结果中，不需要额外字段。

```java
public static void main(String[] args) {
    // cost = 12，安全性和性能的平衡
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    // 注册时：加密密码
    String rawPassword = "MySecurePassword123!";
    String hashedPassword = encoder.encode(rawPassword);
    System.out.println("原始密码: " + rawPassword);
    System.out.println("BCrypt哈希: " + hashedPassword); // $2a$12$X9Im/C5nGc45G2BsOH6Cy./aR231gNIXy5sZJybshzJuEZV/a2dD2
    // 登录时：验证密码是否正确
    boolean matches = encoder.matches("MySecurePassword123!", hashedPassword); // true
    System.out.println("密码是否匹配: " + matches);
}
```

同一个密码每次 encode() 出来的值都不同，因为 salt 是随机的，但只要原密码一样，那么就能匹配成功。而在实例化 BCryptPasswordEncoder 传入的 12 为 cost 构造因子，
它是用于平衡安全性和性能的东西，一般使用 10 或 12，如果不填写，默认是 10。

****
### 3.3 注册完成

在注册页面提交注册表单后会进入该控制器的方法，在完成注册前就会先进行输入数据的格式校验，如果有问题那就将错误信息保存在域中并展示给前端；若没有错误则对输入的验证码和存在 redis 的验证码进行对比，
如果一致再进行注册，不一致则提交错误信息。

Controller 层：

```java
@PostMapping("/register")
public String register(@Valid UserRegisterVo userRegisterVo, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    // 如果校验出错就跳转到注册页面
    if (bindingResult.hasErrors()) {
        Map<String, String> errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:http://auth.gulimall.com/reg.html";
    } else {
        boolean result = sendCodeService.checkCode(userRegisterVo.getPhone(), userRegisterVo.getCode());
        if (!result) {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            // 校验出错，转发到注册页
            return "redirect:http://auth.gulimall.com/reg.html";
        } else {
            R r = memberFeignService.register(userRegisterVo);
            log.debug("注册结果：{}", r);
            if (r.getCode() == 0) {
                // 注册成功回到登录页
                return "redirect:http://auth.gulimall.com/login.html";
            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("msg", r.getData(new TypeReference<String>() {}));
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        }
    }
}
```

这里的注册是远程调用的 gulimall-member 服务的方法，所以要利用到 OpenFeign 进行远程调用，然后就是对用户名和手机号的唯一性进行判断，不重复才能正确注册保存进数据库。

```java
@Override
public void regist(MemberRegisterVo memberRegisterVo) {
    MemberEntity memberEntity = new MemberEntity();
    // 检查用户名和手机号是否唯一，为了让 Controller 感知，可以使用异常机制
    checkPhoneUnique(memberRegisterVo.getPhone());
    checkUserNameUnique(memberRegisterVo.getUsername());
    memberEntity.setUsername(memberRegisterVo.getUsername());
    memberEntity.setMobile(memberRegisterVo.getPhone());

    // 密码进行加密存储
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    String encode = bCryptPasswordEncoder.encode(memberRegisterVo.getPassword());
    memberEntity.setPassword(encode);

    // 获取会员等级对应的 id
    MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
    memberEntity.setLevelId(memberLevelEntity.getId());

    save(memberEntity);
}
```

****
## 4. 登录功能

登录功能就是在前端提交用户的登录账号和密码给后端，让后端进行验证，验证通过则跳转到商城首页，否则提示错误，而这一操作由前端提交表单，所以后端接收数据不需要用 @RequestBody。

```html
<form action="/login" method="post">
    <div style="color: red" th:text="${errors != null and errors.containsKey('msg') ? errors['msg'] : ''}"></div>
    <ul>
        <li class="top_1">
            <img src="/static/login/JD_img/user_03.png" class="err_img1" />
            <input type="text" name="loginacct" placeholder=" 邮箱/用户名/已验证手机" class="user" />
        </li>
        <li>
            <img src="/static/login/JD_img/user_06.png" class="err_img2" />
            <input type="password" name="password" placeholder=" 密码" class="password" />
        </li>
        <li class="bri">
            <a href="/static/login/">忘记密码</a>
        </li>
        <li class="ent"><button class="btn2" type="submit"><a>登 &nbsp; &nbsp;录</a></button></li>
    </ul>
</form>
```

Controller 层：

这里封装了一个 UserLoginVo 来接收用户提交的账号和密码，然后远程调用 gulimall-member 服务进行查询数据库的操作。

```java
@PostMapping("/login")
public String login(UserLoginVo userLoginVo, RedirectAttributes redirectAttributes) {
    // 调用远程登录
    R r = memberFeignService.login(userLoginVo);
    if (r.getCode() == 0) {
        // 成功
        return "redirect:http://gulimall.com";
    } else {
        Map<String, String> errors = new HashMap<>();
        errors.put("msg", r.getData("msg", new TypeReference<String>() {}));
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:http://auth.gulimall.com/login.html";
    }
}
```

Service 层：

这里先查询数据库是否存在用户输入的登录账号，如果不存在就返回空值，那么 Controller 层就知道没有该账号，直接返回错误信息；如果查到了再验证密码的正确性，
因为采用了加密，所以需要对存在数据库的加密密码和从前端获取到的密码进行加密比对，一致再返回查到的 MemberEntity 对象。

```java
@Override
public MemberEntity login(MemberLoginVo memberLoginVo) {
    String loginacct = memberLoginVo.getLoginacct();
    String password = memberLoginVo.getPassword();
    MemberEntity memberEntity = getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, loginacct).or().eq(MemberEntity::getMobile, loginacct));
    if (memberEntity == null) {
        // 登录失败
        return null;
    } else {
        String passwordDb = memberEntity.getPassword();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(password, passwordDb);
        if (matches) {
            return memberEntity;
        } else {
            return null;
        }
    }
}
```

****
## 5. 社交登录

### 5.1 OAuth 

OAuth 是一个开放标准，用于在不暴露密码的情况下，允许第三方应用访问用户在服务提供商（如 Google、GitHub、微信）的资源。用户不需要把自己的账号密码给第三方应用，
便可以授权第三方应用访问部分资源（比如读取邮箱、微博好友列表）。OAuth 定义了 4 种角色：

- 资源拥有者：通常是用户，拥有受保护的资源
- 客户端：第三方应用，需要访问资源的应用
- 授权服务器：负责用户授权，颁发访问令牌（Access Token）
- 资源服务器：存储用户资源，接收访问令牌来提供资源（在很多系统中，授权服务器和资源服务器可以是同一台服务器，但概念上是分开的）

OAuth 2.0 主要包含：

1、Access Token（访问令牌）

它用于访问用户资源的凭证，通常有有效期。

2、Refresh Token（刷新令牌）

当 Access Token 到期后，用 Refresh Token 获取新的 Access Token，它可以避免用户频繁登录重新获取 Access Token。

3、Scope（作用域）

它用于限制客户端访问资源的范围，例如 read:user email repo 表示可以读取用户信息、邮箱和仓库。

4、Redirect URI（回调地址）

授权服务器授权成功后会将用户重定向到客户端的地址，所以必须提前注册，防止恶意重定向。

OAuth 2.0 提供了多种授权方式：

1、授权码模式（最常用）

用户在客户端点击“用第三方登录”，客户端会重定向到授权服务器，等待用户登录并授权，随后授权服务器会返回授权码，客户端需要用授权码向授权服务器换取访问令牌（Access Token），
客户端获取到令牌后就可以用令牌访问资源服务器了。这种授权方式的优点就是 Access Token 不暴露给用户浏览器，安全性较高。

2、简化模式

该模式主要用于前端单页应用，可以直接从授权服务器获取 Access Token，无需中间授权码，虽然流程简化，但安全性低，因为令牌会暴露在浏览器中。

3、密码模式

用户直接把用户名和密码给客户端，让客户端换取 Access Token，这种模式风险较高，不推荐使用，通常只在自有应用中使用。

4、客户端模式

该模式用于应用访问自己的资源或服务端之间的服务调用，它不涉及用户，客户端直接用自己的 client_id 和 client_secret 获取 Access Token。

****
### 5.2 GitHub 登录

GitHub 提供 OAuth 2.0 授权机制，允许第三方应用获取用户在 GitHub 上的部分信息（如用户名、邮箱）而无需用户暴露密码。主要会用到以下字段：

- Client ID：应用标识，由 GitHub 分配
- Client Secret：应用密钥，用于服务端交换 Access Token
- Authorization Code：用户授权后生成的临时码，用于获取 Access Token
- Access Token：应用用来访问用户 GitHub 数据的凭证
- Redirect URI / Callback URL：用户授权后 GitHub 回跳到应用的 URL

注册 GitHub OAuth 应用的步骤：

1. 登录 GitHub -> 右上角头像 -> Settings -> 左下角 Developer settings -> OAuth Apps -> New OAuth App。
2. 填写信息对应的信息，主要有如下内容：

   - Application Name：你的应用名字
   - Homepage URL：应用主页，例如 https://gulimall.com
   - Authorization callback URL：用户授权完成后 GitHub 跳转回你的 URL，例如 https://gulimall.com/success
3. 注册完成后，GitHub 会生成 Client ID 和 Client Secret，Client Secret 需要好好保管，不能放到前端

注册完成后让前端发起授权请求，当用户点击 “用 GitHub 登录” 时，浏览器重定向到 GitHub 授权页面：

```http request
GET https://github.com/login/oauth/authorize?client_id=YOUR_CLIENT_ID&state=随机字符串
```

这里的 state 是用来防 CSRF 攻击的随机值，登录完成后会原样返回，需要验证。当用户同意授权后 GitHub 会跳转到上面设置的 callback URL 回调地址，并附带 code 和 state，例如：

```http request
http://gulimall.com/success?code=1442c4a...&redirect_uri=http://auth.gulimall.com/oauth/github/success&state=2
```

此时就需要让服务端用授权码换取 Access Token，所以要让服务端接收到 code 后向 GitHub 请求 Access Token，这里用 Apifox 模拟：

```http request
POST https://github.com/login/oauth/access_token?client_id=...&client_secret=...&code=...&redirect_uri=http://gulimall.com/success
```

- client_id：必填，是从 GitHub 收到的 OAuth app 的客户端 ID
- client_secret：必填，是从 GitHub 收到的 OAuth app 的客户端密码
- code：必填，收到的作为对上面步骤的响应的代码
- redirect_uri：必填，是用户获得授权后被发送到的应用程序中的 URL

发送请求后会得到如下信息：

```text
access_token=gho_rHt5...&scope=&token_type=bearer
```

最后，用请求到的 Access Token 获取用户信息：

```http request
GET https://api.github.com/user
Authorization: token ACCESS_TOKEN
```

需要注意的是，GitHub 不允许把 access_token 放在 URL 查询参数里，必须通过 HTTP Header 发送，所以必须在请求头上添加，最终访问成功，得到用户信息：

```json
{
    "login": "Cell029",
    "id": 15245...,
    "node_id": "U_kgDO...",
    "avatar_url": "https://avatars.githubusercontent.com/u/152457414?v=4",
    "gravatar_id": "",
    "url": "https://api.github.com/users/Cell029",
    "html_url": "https://github.com/Cell029",
    "followers_url": "https://api.github.com/users/Cell029/followers",
    "following_url": "https://api.github.com/users/Cell029/following{/other_user}",
    ...
}
```

最终流程：

```text
用户点击 GitHub 登录
        ⬇
浏览器跳转到 GitHub 授权页面 
        ⬇
      用户授权
        ⬇
GitHub 回调你的 redirect_uri 并带 code 
        ⬇
服务端用 code 换 Access Token 
        ⬇
服务端用 Access Token 获取用户信息
        ⬇
  创建或登录本地账号
```

****
### 5.3 GitHub 账号社交登录回调

上面简单记录了一下 GitHub 账号如何获取 code 后再通过 code 获取到 Access Token 取获取 GitHub 用户的信息，现在就需要用 Java 代码来实现。所以整体的思路就是：
用前端传来的 code 去 GitHub 换取一个访问令牌，用得到的 Access Token 去请求 GitHub 的 API 获取用户的详细信息（如 ID、用户名），
将 GitHub 返回的用户信息发送给本地的用户服务，进行登录或注册操作。

Controller 层：

因为前端点击 GitHub 登录时就会去 GitHub 获取一个 code，该 code 是通过 url 传递的，所以用 @RequestParam 接收后就可以使用了，控制层就是调用一个 loginOrRegister() 方法，
用来判断该 GitHub 用户是否注册过，在该方法内部会通过传递的 code 来获取 Access Token 和用户信息。

```java
@GetMapping("/oauth/github/success")
public String githubSuccess(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
    // 根据 code 换取 Access Token
    try {
        MemberResponseVo userInfo = gitHubOAuthService.loginOrRegister(code);
        System.out.println(userInfo);
      if (userInfo != null) {
          // 登录成功，跳转首页
          log.info("GitHub 用户登录成功");
          return "redirect:http://gulimall.com";
      } else {
          log.error("GitHub 用户登录失败");
          return "redirect:http://auth.gulimall.com/login.html";
      }
    } catch (OAuthException e) {
        redirectAttributes.addFlashAttribute("errors", e.getMessage());
        return "redirect:http://auth.gulimall.com/login.html";
    }
}
```

Service 层：

该方法主要就是起到一个调用别的方法的作用，一个调用获取 Access Token，一个通过 Access Token 来获取 GitHub 用户信息进行登录与注册的判断。

```java
@Override
public MemberResponseVo loginOrRegister(String code) throws OAuthException {
    // 用授权码换取 AccessToken
    String accessToken = getAccessToken(code);
    // 用 AccessToken 换取用户信息
    GitHubUserInfoVo userInfo = getUserInfo(accessToken);
    // 将用户信息发送给内部会员服务，进行登录或注册
    if (r.getCode() == 0) {
        MemberResponseVo memberEntity = r.getData("memberEntity", new TypeReference<MemberResponseVo>() {
    });
        return memberEntity;
    }
    return null;
}
```

```java
public String getAccessToken(String code) throws OAuthException {
    // 构建请求 URL(GitHub 的令牌端点)
    String url = "https://github.com/login/oauth/access_token";
    // 1. 设置请求头
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); // 告诉 GitHub 希望返回 JSON 格式的数据
    // 2. 构建请求体 (OAuth2 标准参数)
    Map<String, String> body = new HashMap<>();
    body.put("client_id", clientId); // 应用的 id，在 GitHub 上注册时获得
    body.put("client_secret", clientSecret); // 应用的密钥
    body.put("code", code); // 前端传来的授权码
    body.put("redirect_uri", redirectUri); // 必须与注册应用时填写的和前端请求时用的 redirect_uri 一致
    // 3. 将头和体组装成 HTTP 实体
    HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
    // 4. 发送POST请求
    ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
    // 5. 处理响应
    Map<String, Object> resp = response.getBody();
    if (resp != null && resp.get("access_token") != null) {
        return resp.get("access_token").toString();
    } else {
      throw new OAuthException("获取 AccessToken 失败");
    }
}
```

要获取 Access Token 就需要指定获取的 URL，而 https://github.com/login/oauth/access_token 是 OAuth 2.0 标准规定的令牌端点，然后就要通过携带请求头的方式传递 code，
Accept: application/json 是关键，因为 GitHub 默认可能返回 application/x-www-form-urlencoded 格式，通过这个请求头明确要求返回 JSON，方便后续用 Map 解析。
前面有记录，在获取 Access Token 时需要携带一些必要的参数，而这四个参数就是 OAuth 2.0 标准中用授权码换取令牌时必须的，client_id 和 client_secret 用于证明是合法应用在请求，
code 是临时凭证，redirect_uri 则是用于二次校验（虽然 GitHub 会自动跳转，但还是写上比较好）。最后将请求头和请求体包装在一起，方便 RestTemplate 发送，
并检查响应体中是否包含 access_token 字段，如果有则成功返回，否则视为失败。

需要注意的是：因为 GitHub 属于外网，如果直接连接是连不上的，所以需要使用到 clash-verge，但是 Spring Boot 的 RestTemplate 默认不会自动走系统代理，所以就算本地能访问 GitHub，
Java 程序也可能还是走的直连，导致超时。因此需要单独配置 RestTemplate，给它设置代理，在发送令牌请求时直接通过 clash 访问：

```java
@Component
public class RestTemplateClashVerge {
    @Bean
    public RestTemplate restTemplate() {
        // 设置代理
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7897));
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setProxy(proxy);
        return new RestTemplate(factory);
    }
}
```

这里 new SimpleClientHttpRequestFactory() 的作用就是创建一个请求工厂（RequestFactory），RestTemplate 需要通过这个工厂来生成具体的 HTTP 请求对象。
因为 RestTemplate 本身只是一个客户端工具，它本身不直接发送请求，它需要一个底层的请求工厂（ClientHttpRequestFactory）来创建 HttpURLConnection 或 HttpClient 请求对象。
然后让这个工厂设置代理，再把工厂传递给 RestTemplate，如果直接写 return new RestTemplate(); 也会默认使用 SimpleClientHttpRequestFactory，
但是这就没法设置代理对象了，所以才要显式地创建工厂。

```java
public GitHubUserInfoVo getUserInfo(String accessToken) throws OAuthException {
    // 构建请求URL (GitHub 的用户信息 API 端点)
    String url = "https://api.github.com/user";
    // 1. 设置请求头 (身份验证的核心)
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "token " + accessToken); // 标准 Bearer Token 认证格式
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); // 期望以 JSON 返回
    // 2. 组装请求实体（这里没有请求体，只有头）
    HttpEntity<String> entity = new HttpEntity<>(headers);
    // 3. 发送GET请求
    ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
    // 4. 处理响应
    Map<String, Object> body = response.getBody();
    if (body != null && body.get("id") != null) { // 通常用id作为用户唯一标识
        // 成功就将 Map 中的数据映射到自定义的 GitHubUserInfoVo 对象中
        GitHubUserInfoVo gitHubUserInfoVo = new GitHubUserInfoVo();
        gitHubUserInfoVo.setId(Long.parseLong(body.get("id").toString()));
        gitHubUserInfoVo.setLogin(body.get("login").toString());
        return gitHubUserInfoVo;
    } else {
        throw new OAuthException("获取 GitHub 用户信息失败");
    }
}
```

首先设置一个请求路径 https://api.github.com/user ，它是 GitHub 提供的资源服务器端点，用于获取已验证用户的个人信息，接着封装请求头，GitHub 强制要求以请求头的形式传递，
它告诉 GitHub 已经有访问令牌了，现在需要要访问受保护的资源，这是 OAuth 2.0 标准中携带访问令牌的方式之一（Bearer Token），因为这是一个 GET 请求，没有请求体，
所以只需要传入 headers 并封装为 HttpEntity，接着指定 HTTP 方法为 GET，完整地传入构建的 HttpEntity。最后对响应结果进行解析：GitHub 用户 API 返回一个 JSON 对象，
这里将其解析为 Map，然后手动提取所需的字段（如 id, login）并填充到自定义的 GitHubUserInfoVo 对象中。如果获取失败，这里还会抛出异常，抛出的异常会由 Controller 层接收到，
由它进行异常的处理。

```java
/**
 * 社交账号登录
 */
@PostMapping("/oauth/login")
public R oauthLogin(@RequestBody MemberGitHubUserInfoVo memberGitHubUserInfoVo) {
    MemberEntity memberEntity = memberService.oauthLogin(memberGitHubUserInfoVo);
    return R.ok().put("memberEntity", memberEntity);
}

@Override
public MemberEntity oauthLogin(MemberGitHubUserInfoVo memberGitHubUserInfoVo) {
    String gitHubId = String.valueOf(memberGitHubUserInfoVo.getId());
    String githubLogin = memberGitHubUserInfoVo.getLogin();
    MemberEntity one = getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, gitHubId));
    if (one == null) {
        // 如果没查到就注册
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUsername(gitHubId);
        // 获取会员等级对应的 id
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());
        memberEntity.setNickname(githubLogin);
        memberEntity.setCreateTime(new Date());
        save(memberEntity);
        return memberEntity;
    } else {
        return one;
    }
}
```

最后调用远程接口，对该 GitHub 用户进行账号的检查，看看是否已注册，如果没注册，那就把它的信息存入数据库中，如果已注册，那就直接将数据返回即可。

****
## 6. 分布式服务 Session 共享问题

在单体应用中，用户登录后会在服务器内存中生成一个 Session，浏览器通过 Cookie 保存 JSESSIONID，每次请求会带上这个 ID，服务器通过这个 ID 找到对应的 Session，完成用户状态管理。

```text
浏览器 --> 服务器 --> Session 存储在服务器内存
```

这种方式在单台服务器下很简单，但在分布式架构（多台服务器）中就会出现问题：

```text
浏览器请求 --> 服务器A（有Session）  
下一次请求 --> 服务器B（没有Session） --> 用户被当作未登录
```

所以 Session 无法在多台服务器之间共享同步。而目前的项目的架构为一服务对应一个域名，浏览器的 Session 是基于 Cookie 的，Cookie 默认只在当前域名或子域名下有效，
不同域名之间 Cookie 无法共享，所以多域名 + 独立服务 = Session 不能自动共享。

上述问题可以通过哈希一致性来解决，让每个 session 根据 userId 或 sessionId 映射到一个固定节点，访问同一个用户时总是路由到同一节点，这样不管如何，使用的 session 一定是属于自己的，
不存在共享问题，也可以用 redis 与它配合使用，把所有的 session 存在 redis 中，这样不管使用的是哪个服务，最终都是按照计算的哈希值来查找对应的 session，
这样就可以解决使用不同服务造成的大量 session 迁移问题，不过 session 通常都有有效期的，所以只有少量数据发生迁移也不影响。

****
## 7. SpringSession

### 7.1 使用

SpringSession 把原来容器内存里的 HttpSession 挪到外部存储（最常用 Redis），并且支持多实例、多节点共享会话，它不再依赖 Tomcat 或者 Jetty 的会话实现，
通过一个过滤器把 HttpSession 委托给选定的仓库（例如 redis）。它还支持会话的创建、删除、过期等操作，引入相关组件后即可使用，不用大量修改原有代码。

```java
// 传统 Servlet 会话管理的问题
HttpSession session = request.getSession(); // 只能在单机环境下工作
session.setAttribute("user", user); // 集群环境下无法共享
```

具体使用：

1、引入依赖

```xml
<!--spring session-->
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

2、指定使用的存储仓库（需要提前配置好 redis）

```yaml
spring:
  session:
    store-type: redis
```

3、在启动类上添加 @EnableRedisHttpSession 注解

4、创建自定义配置类，修改 session 的作用域大小

SpringSession 通过一个 CookieSerializer（通常是 DefaultCookieSerializer）来读写浏览器中的会话 Cookie，通过修改 DefaultCookieSerializer 的一些默认属性来达到效果，
这里把默认名换成 GULISESSION，并且把 Cookie 的 Domain 属性设为顶级域，从而允许子域间共享同一 Cookie，也就是只要有 gulimall.com 那就共享一个 Cookie 数据。

SpringSession 默认用 Java 原生序列化（java.io.Serializable 的二进制）存储进 redis，虽然能保证数据的存储，但是却肉眼不可读，所以最好修改一下序列化器，
让数据序列化成 JSON 格式，这里就是使用的 GenericJackson2JsonRedisSerializer 序列化器，也是常用的。需要注意的是：Spring Session 文档明确写明，
“You can customize the serializer by defining a bean named springSessionDefaultRedisSerializer”，也就是序列化器的 Bean 的名字必须叫 springSessionDefaultRedisSerializer，
否则仍然会使用 JDK 默认的二进制序列化方式。

```java
@Configuration
public class GulimallSessionConfig {
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setCookieName("GULISESSION");
        defaultCookieSerializer.setDomainName("gulimall.com");
        return defaultCookieSerializer;
    }

    @Bean
    public RedisSerializer<Object> redisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
```

在跨服务时，每个服务里都需要配上相同的 CookieSerializer + springSessionDefaultRedisSerializer，这里记录一下 SpringSession 的工作机制，它大致为三步：

1. 会话存储：Spring Session 把会话内容存在 Redis
2. 会话标识：客户端（浏览器）带的 Cookie 里存的 sessionId（比如 GULISESSION=xxx）
3. 会话读取：服务端收到请求 -> 解析 Cookie → 拿到 sessionId -> 去 Redis 查找 session -> 反序列化成 Java 对象 -> 放到 HttpSession 里

所以在不通的服务与域名中，Cookie 的名称和使用域名的范围需要统一，否则浏览器不会在不同子域之间共享 Cookie，例如：如果 A 服务写了 SESSION，B 服务写了 GULISESSION，
那两个 Cookie 就是不同的，浏览器访问 B 服务时不会带上 A 的 Cookie；如果 A 用 .gulimall.com，B 用 b.gulimall.com，Cookie 作用域不一样，也没法共享。
并且 redis 里的数据序列化方式要统一，否则 A 服务存进去是 JDK 序列化，B 服务却用 JSON 解析，必然报错。因此，需要在所有可能用到共享 session 的数据的服务中都引入自定义的这个配置类，
确保共享数据能够成功。

****
### 7.2 核心原理

进入 @EnableRedisHttpSession 注解，可以看到它引入了一个 @Import({RedisHttpSessionConfiguration.class})，而 RedisHttpSessionConfiguration 继承 SpringHttpSessionConfiguration，
在这个类里面就是对 CookieSerializer 的初始化，CookieSerializer 负责会话 ID 在客户端和服务器之间的传输管理，而这也是为什么默认使用 DefaultCookieSerializer：

```java
@PostConstruct
public void init() {
    // 检查是否有自定义的 cookieSerializer
    CookieSerializer cookieSerializer = this.cookieSerializer != null ? this.cookieSerializer : this.createDefaultCookieSerializer();
    // 设置到 HttpSessionIdResolver 中
    this.defaultHttpSessionIdResolver.setCookieSerializer(cookieSerializer);
}
```

在这个类中还使用了 SessionRepositoryFilter，也就是一个过滤器，最终实现的是 Filter 的过滤方法，在 SessionRepositoryFilter 中的核心方法就是：

```java
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    request.setAttribute(SESSION_REPOSITORY_ATTR, this.sessionRepository);
    // 包装请求和响应
    SessionRepositoryFilter<S>.SessionRepositoryRequestWrapper wrappedRequest = new SessionRepositoryRequestWrapper(request, response);
    SessionRepositoryFilter<S>.SessionRepositoryResponseWrapper wrappedResponse = new SessionRepositoryResponseWrapper(wrappedRequest, response);
    try {
        // 执行过滤器链
        filterChain.doFilter(wrappedRequest, wrappedResponse);
    } finally {
        // 提交会话
        wrappedRequest.commitSession();
    }
}
```

request.setAttribute(SESSION_REPOSITORY_ATTR, this.sessionRepository); SESSION_REPOSITORY_ATTR 是常量 "SESSION_REPOSITORY"，
把 SessionRepository 放到请求属性里供下游代码或框架组件，把仓库暴露出来，方便后续对其进行操作。后面就是封装原始的 HttpServletRequest 和 HttpServletResponse，
通过包装实现拦截效果，防止使用原始的 session 存储在容器内存里（只有本地有效），虽然还是使用 request.getSession() 获取，但底层变成存 Redis/数据库，而不是容器内存。
SessionRepositoryRequestWrapper 覆盖了几个关键方法，getSession() 和 getSession(boolean) 起拦截调用，从 Redis（或别的 SessionRepository）加载创建 Session，
并返回一个代理对象（实现了 HttpSession 接口，但内部调用的是 SessionRepository），这样使用 request.getSession() 就直接变成了访问 redis 或别的仓库了。

```java
@Override
public HttpSessionWrapper getSession(boolean create) {
    // 如果已经有缓存的 session wrapper，直接返回
    if (this.requestedSession != null) {
        return this.requestedSession;
    }
    // 根据 resolver（通常是 Cookie）解析客户端带来的 sessionId
    String sessionId = getRequestedSessionId();
    if (sessionId != null) {
        S session = this.sessionRepository.findById(sessionId);
        if (session != null) {
            this.requestedSession = new HttpSessionWrapper(session, getServletContext());
            return this.requestedSession;
        }
    }
    // 如果允许创建新 session
    if (!create) {
        return null;
    }
    S session = this.sessionRepository.createSession();
    this.requestedSession = new HttpSessionWrapper(session, getServletContext());
    return this.requestedSession;
}
```

该方法覆盖了原始的 getSession() 方法，没有调用容器原生 session，通过 sessionRepository 获取或者创建 session，而在一开始就对 sessionRepository 进行了绑定，
request.setAttribute(...) 把 sessionRepository 存到了 request 的属性里，当使用了 redis 后，就会加载 RedisIndexedSessionRepository，而获取 session 的操作，
就是在 RedisIndexedSessionRepository 里进行的。

而 SessionId 需要写回到客户端，通常是通过 Cookie 或 Header 的方式，如果不包装 Response，Spring Session 没法在恰当的时机拦截响应，屏蔽原始的 JSESSIONID，
因为当业务调用 request.getSession() 创建了新 session 时，此时新的 sessionId 还在服务器端（Redis）里，浏览器并不知道这个新 sessionId，必须通过 Set-Cookie 写回客户端。
但是当页面进行重定向时会立即提交 response，此时如果 sessionId 还没写回，浏览器就收不到新的 cookie，这里就封装了一个 commitSession() 方法，在所有响应前都会调用：

```java
void commitSession() {
    if (session == null) return;
    if (session.isInvalidated()) {
        sessionRepository.deleteById(session.getId());  // 删除 Redis session
        httpSessionIdResolver.expireSession(this, this.response); // 删除客户端 cookie
    } else {
        sessionRepository.save(session); // 保存 Redis session
        httpSessionIdResolver.setSessionId(this, this.response, session.getId()); // 写回客户端 cookie
    }
}
```

Response wrapper 并不去 Redis 读取 session，而是调用 commitSession()，把 request wrapper 中已经获取或修改的 session 保存到 Redis，确保能把最新的 sessionId 同步到浏览器。

关于 SessionId 的作用，则是为了能够成为 session 的唯一标识，在浏览器每次请求服务器时，必须携带这个 sessionId，服务器才能找到对应的 session 数据。
而 HTTP 是 无状态协议，也就是说它的每次请求都是独立的，服务器不会自动记住客户端的 session，因此服务器必须把 sessionId 发送给客户端，客户端在后续请求带上这个 id。
HTTP 提供了两种常用方法传递 sessionId，使用 Cookie（最常用）或者URL 重写（JSESSIONID=xxx 追加到 URL），而 Spring Session 默认使用 Cookie 来传递 sessionId。
当 Spring Session 创建或更新 session 时，会在响应中写入：

```http request
HTTP/1.1 200 OK
Set-Cookie: GULISESSION=abc123; Path=/; HttpOnly
```

浏览器在后续请求中自动携带 Cookie:

```java
GET /member/info HTTP/1.1
Host: gulimall.com
Cookie: GULISESSION=abc123
```

****
### 7.3 修改显示用户效果

在上面已经解决了 session 共享的问题，接下来就是修改原有代码，把登录的用户信息保存进 session 中，而目前只涉及两个登录，一个页面输入账号密码登录，一个使用  GitHub 登录，
因为登录都是进行远程调用 gulimall-member 服务区查询表 pms_member，所以需要把查询到的 MemberEntity 对象放入 session 中：

```java
@PostMapping("/login")
public String login(UserLoginVo userLoginVo, RedirectAttributes redirectAttributes, HttpSession session) {
    // 调用远程登录
    R r = memberFeignService.login(userLoginVo);
    if (r.getCode() == 0) {
        // 成功
        MemberResponseVo memberEntity = r.getData("memberEntity", new TypeReference<MemberResponseVo>() {
        });
        // 登录成功就将数据存到 spring session 中（实际在 redis 中）
        session.setAttribute(LoginConstant.LOGIN_USER, memberEntity);
        return "redirect:http://gulimall.com";
    } else {
        Map<String, String> errors = new HashMap<>();
        errors.put("msg", r.getData("msg", new TypeReference<String>() {}));
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:http://auth.gulimall.com/login.html";
    }
}
```

```java
@GetMapping("/oauth/github/success")
public String githubSuccess(@RequestParam("code") String code, RedirectAttributes redirectAttributes, HttpSession session) {
    // 根据 code 换取 Access Token
    try {
        MemberResponseVo userInfo = gitHubOAuthService.loginOrRegister(code);
        System.out.println(userInfo);
        if (userInfo != null) {
            // 登录成功，跳转首页
            log.info("GitHub 用户登录成功");
            session.setAttribute(LoginConstant.LOGIN_USER, userInfo);
            return "redirect:http://gulimall.com";
        } else {
            log.error("GitHub 用户登录失败");
            return "redirect:http://auth.gulimall.com/login.html";
        }
    } catch (OAuthException e) {
        redirectAttributes.addFlashAttribute("errors", e.getMessage());
        return "redirect:http://auth.gulimall.com/login.html";
    }
}
```

当然，前端页面也需要修改为动态的，在用户登录成功后，应该显示用户的名称，并且不能再点击登录按钮，目前一共有首页、检索页、详情页需要修改：

```html
<li>
    <a th:if="${session.loginUser == null}"
       href="http://auth.gulimall.com/login.html">
        你好，请登录
    </a>
    <span th:if="${session.loginUser != null}">
       你好，[[${session.loginUser.nickname}]]
    </span>
</li>

<li>
    <a th:if="${session.loginUser == null}"
       href="http://auth.gulimall.com/reg.html"
       class="li_2">
        免费注册
    </a>
    <span th:if="${session.loginUser != null}" class="li_2 disabled">
       已注册
    </span>
</li>
```

当然涉及到其它服务时，也需要进行对应的 SpringSession 的操作，引入依赖、添加 @EnableRedisHttpSession 注解、自定义配置类等操作，确保能够正确获取与解析同一个 session。

还有一个功能，就是当 session 中有用户信息时，证明该用户是已经登录成功过的，所以如果此时再访问登录页面，应该直接跳转到首页，不用重复登录，除非点击退出。

```java
/**
 * 进入登录页面时，如果 session 存在，那么就直接跳转到首页，而不是登录页面
 */
@GetMapping("/login.html")
public String loginPage(HttpSession session) {
    if (session.getAttribute(LoginConstant.LOGIN_USER) != null) {
        return "redirect:http://gulimall.com";
    } else {
        return "login";
    }
}
```

需要注意的是：这里不能直接重定向到 http://auth.gulimall.com/login.html ，因为当用户没有登录时，session.getAttribute(LoginConstant.LOGIN_USER) 返回 null，
如果使用的是重定向的话，那么浏览器访问 /login.html → 服务器又重定向到 /login.html → 又进入这个 Controller → 又重定向 → 无限循环重定向，所以这里应该直接返回模板，
此时浏览器就会直接渲染 login.html 页面，也就不会触发 Controller 的方法。

****
## 8. 单点登录

单点登录（SSO）指用户只需登录一次（在一个统一认证中心），就能无感访问同一组织下的多个应用或其子系统，而无需在每个系统重复登录。例如登录了 QQ，那么对应的 QQ 音乐、QQ 邮箱、
QQ 游戏大厅就应该可以直接登录。而实现的本质就是让多服务共享同一个 “已认证标识”，例如 session。大致流程如下：

1. 用户访问受保护资源（用户尝试访问商城系统）中需要登录才能操作的功能，如加入购物车。 
2. 网关拦截并重定向至认证中心，如果判断用户未登录，就会将用户的请求重定向到单点登录系统（例如 sso.com）的登录页面，并携带一个回调地址（redirect_url），通常是用户最初想访问的那个页面。 
3. 用户在认证中心登录，输入用户名和密码进行认证。 
4. 认证中心颁发凭证并重定向，而认证中心验证用户凭证通过后，会做两件重要的事：

   - 生成一个全局的、代表用户身份的令牌（通常是写入一个 Cookie，这个 Cookie 的域名是单点登录系统的顶级域名，例如 sso.com，这样所有子域都能在后续步骤中间接访问到它）。 
   - 将浏览器重定向回最初用户请求的那个回调地址，并附上这个令牌作为参数。

5. 当商城系统接收到重定向请求后，会提取出令牌，并向认证中心发送一个请求，验证此令牌的有效性并换取用户信息。 
6. 验证通过后，商城系统会在自己的域下（例如 gulimall.com）创建局部的会话 Session 并设置 Cookie，然后将用户最初请求的资源返回给浏览器。 
7. 当已登录的用户再去访问另一个子系统的受保护资源时，该系统的网关同样会判断其未登录（因为没有该子系统的局部会话 Cookie），并重定向到认证中心。 
8. 在浏览器在访问认证中心时，会自动携带第 4 步中颁发的那个顶级域 Cookie（sso.com），认证中心通过此 Cookie 发现用户已经登录，于是不再显示登录页，而是直接再次重定向回刚刚访问的系统并附上令牌。 
9. 该系统会重复第 5 和第 6 步，验证令牌并建立自己的局部会话，最终让用户无感地访问到资源。


```text
用户浏览器         商城系统(mall.com)         认证中心(sso.com)         营销系统(sales.com)
    |                     |                           |                           |
    |---1.访问资源-------> |                           |                           |
    |<--2.重定向至SSO------|                           |                           |
    |---------------------3.访问登录页----------------->|                           |
    |---------------------4.提交登录凭据--------------->|                           |
    |                     |<----验证凭据，生成令牌----->|                           |
    |<--5.设置SSO Cookie，带令牌重定向-------------------|                           |
    |---6.携带令牌访问----->|                           |                           |
    |                     |----7.验证令牌------------->|                           |
    |                     |<----8.返回用户信息----------|                           |
    |                     |-----9.建立局部会话----------|                           |
    |<-----10.设置Mall Cookie，返回资源------------------|                           |

    # 二次访问其他子系统（营销）
    |                     |                           |                           |
    |----------------------11.访问资源------------------------------------------->|
    |<---12.重定向至SSO----|                           |                           |
    |---------------------13.访问SSO，带SSO Cookie---->|                           |
    |                     |<----14.Cookie发现已登录-----|                           |
    |<---15.带令牌重定向--------------------------------|                           |
    |-------------------------16.携带令牌访问--------------------------------------->|
    |                     |                           |-----17.验证令牌------------>|
    |                     |                           |<----18.返回用户信息---------|
    |                     |                           |-----19.建立局部会话---------|
    |<----------20.设置Sales Cookie，返回资源----------------------------------------|
```

认证中心负责两件事：颁发令牌和验证令牌，所以需要模拟一个认证中心，当用户在未登录状态访问某些页面时直接跳转到认证中心的登录页面，登陆成功后这里使用 UUID 模拟全局令牌，
并把它存入 redis 中，例如："SSO:TOKEN:540e8490-e29b-41d4-a716-474981589765164"。存入完成后，就重定向到原来访问的页面。

```html
<form action="/sso/login" method="post">
    <input type="text" name="username" placeholder="用户名">
    <input type="password" name="password" placeholder="密码">
    <input type="hidden" name="redirectUrl" value="${redirectUrl}">
    <button type="submit">登录</button>
</form>
```

通过 Spring 提供的一个可自定义 HTTP 响应的对象 ResponseEntity 来设置状态码，把状态码设置为 302，也就是重定向（HttpStatus.FOUND），
并设置 HTTP 响应头 Location，告诉浏览器要跳转到哪个 url。

```java
@RestController
@RequestMapping("/sso")
public class SsoController {
    @Autowired
    private StringRedisTemplate redisTemplate;
    // token 有效期 30 分钟
    private static final long TOKEN_EXPIRE = 30 * 60;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String redirectUrl) throws UnsupportedEncodingException {
        // 用户名密码校验通过
        String token = UUID.randomUUID().toString();
        // 存入 Redis
        redisTemplate.opsForValue().set("SSO:TOKEN:" + token, username, TOKEN_EXPIRE, TimeUnit.SECONDS);
        // 返回重定向 URL
        String redirect = redirectUrl + (redirectUrl.contains("?") ? "&" : "?") + "token=" + token;
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirect))
                .build();
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        String username = redisTemplate.opsForValue().get("SSO:TOKEN:" + token);
        if (username != null) {
            // 刷新 token TTL
            redisTemplate.expire("SSO:TOKEN:" + token, 30, TimeUnit.MINUTES);
            return ResponseEntity.ok(username);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
}
```

当用户需要验证在认证中心获取到的 token 是否失效时，就会发送一个 http://sso.com/sso/login?redirectUrl=... url 请求，如果从 redis 中获取到的 token 信息没有过期的话，
那就给它重置过期时间并把该 token 作为响应体返回，如果没获取到该 token（已过期），那么就返回 401，并用响应体告诉客户端 token 无效。

```java
@RestController
@RequestMapping("/mall")
public class MallController {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RestTemplate restTemplate;
    private static final long SESSION_EXPIRE = 30 * 60;

    @GetMapping("/cart")
    public Object cart(@RequestParam(required = false) String token,
                       HttpServletRequest request,
                       HttpServletResponse response) throws UnsupportedEncodingException {
        // 1. 检查本地 session
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("MALLSESSION".equals(c.getName())) {
                    String username = redisTemplate.opsForValue().get("MALLSESSION:" + c.getValue());
                    if (username != null) {
                        // 刷新 TTL
                        redisTemplate.expire("MALLSESSION:" + c.getValue(), SESSION_EXPIRE, TimeUnit.SECONDS);
                        return "购物车页面，用户：" + username;
                    }
                }
            }
        }

        // 2. 如果有 token，从 SSO 验证
        if (token != null) {
            String username;
            try {
                username = restTemplate.getForObject("http://sso.com/sso/verify?token=" + token, String.class);
            } catch (Exception e) {
                username = null;
            }
            if (username != null) {
                // 建立本地 session
                String mallSessionId = UUID.randomUUID().toString();
                redisTemplate.opsForValue().set("MALLSESSION:" + mallSessionId, username, SESSION_EXPIRE, TimeUnit.SECONDS);
                // 写 Cookie，顶级域可共享子域
                Cookie cookie = new Cookie("MALLSESSION", mallSessionId);
                cookie.setPath("/");
                cookie.setDomain("mall.com"); // 顶级域
                cookie.setHttpOnly(true);
                cookie.setSecure(false); // 如果有 HTTPS，改为 true
                response.addCookie(cookie);
                return "购物车页面，用户：" + username;
            }
        }
        // 3. 没有登录，跳转 SSO 登录页
        String redirectUrl = URLEncoder.encode("http://mall.com/mall/cart", "UTF-8");
        return "redirect:http://sso.com/sso/login?redirectUrl=" + redirectUrl;
    }
}
```

而业务处理的部分则会优先检查自己的本地会话，如果没有，再去找 SSO 中心帮忙验证。所以先获取 cookies 检查是否有商城页登录时存储的 MALLSESSION，如果没有，那就得去认证中心判断是否登陆过，
通过 restTemplate.getForObject(...) 发起一个 GET 请求，接收请求返回的响应。如果当前的请求路径中包含 token，那么就可以拿这个 token 去询问 SSO 认证中心这个 Token 是否有效，
如果发现该 token 有效，那么就可以为这个用户建立本地 session 了，然后再返回刚刚用户点击的页面；如果 token 失效，那么就再跳回认证中心获取 token 后再返回该页面。

如果有另一个服务要在上面的服务登录后也能直接登录，那它的处理和上面的差不多：

```java
// 1. 检查本地 session
Cookie[] cookies = request.getCookies();
if (cookies != null) {
    for (Cookie c : cookies) {
        if ("BSERVICESESSION".equals(c.getName())) {
            String username = redisTemplate.opsForValue().get("BSERVICESESSION:" + c.getValue());
            if (username != null) {
                redisTemplate.expire("BSERVICESESSION:" + c.getValue(), SESSION_EXPIRE, TimeUnit.SECONDS);
                return "B 服务页面，用户：" + username;
            }
        }
    }
}
```

```java
// 2. 如果请求里有 token，去 SSO 验证
if (token != null) {
    String username = restTemplate.getForObject(
        "http://sso.com/sso/verify?token=" + token, String.class);
    if (username != null) {
        String bSessionId = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("BSERVICESESSION:" + bSessionId, username, 30 * 60, TimeUnit.SECONDS);
        Cookie cookie = new Cookie("BSERVICESESSION", bSessionId);
        cookie.setPath("/");
        cookie.setDomain("bservice.com"); // B 服务自己的域
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return "B 服务页面，用户：" + username;
    }
}
```

```java
// 3. 没有登录，也没有 token，那就跳转到 SSO
String redirectUrl = URLEncoder.encode("http://bservice.com/page", "UTF-8");
return "redirect:http://sso.com/sso/login?redirectUrl=" + redirectUrl;
```

****
# 九、购物车

## 1. 环境搭建

创建新的模块取名为 gulimall-cart，引入相关依赖以及注入 nacos：

```xml
<dependencies>
    <!--引入 common 服务-->
    <dependency>
        <groupId>com.project</groupId>
        <artifactId>gulimall-common</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
        <exclusions>
            <exclusion>
                <groupId>com.vaadin.external.google</groupId>
                <artifactId>android-json</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <!--thymeleaf-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <!--devtools-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

因为引入的 common 中带有数据库的依赖，而目前的初始项目用不到数据库，所以需要在启动类上排除数据库的使用：

```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GulimallAuthServerApplication {
    ...
}
```

```yaml
spring:
  application:
    name: gulimall-auth-server
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848

server:
  port: 30000
```

```yaml
spring:
  application:
    name: gulimall-auth-server

  cloud:
    nacos:
      config:
        server-addr: 127.0.0.1:8848
        file-extension: yaml # 文件后缀名
        namespace: 4a8970e8-50c0-4076-a3e5-41a0c4eb9c65 # cart
```

接着就是把静态资源放进 nginx 容器，并部署本地 hosts 与网关：

```shell
cp -r /mnt/d/docker_dataMountDirectory/gmall-static-resource/cart/. /nginx/html/static/cart
```

```xml
192.168.1.110 cart.gulimall.com
```

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: nginx_cart_route
          uri: lb://gulimall-cart
          predicates:
            - Host=cart.gulimall.com
```

****
## 2. 购物车数据模型分析

### 2.1 存储结构

在购物车功能这一块分为未登录状态下添加商品和登录后添加商品的情况。对于未登录购物车功能：用户未登录时，仍然可以把商品加入购物车，但数据需要与用户账号无关，属于临时存储，
等用户登录后可以合并到账号绑定的购物车，而这些数据可以选择的保存位置有:

1、浏览器 cookie

直接将购物车商品 JSON 数据保存在客户端 Cookie，这样实现简单，直接跟随请求传递，但 Cookie 存储大小有限（一般 4KB 左右），并且存储在浏览器的数据安全性差，容易被篡改，
还可能会导致请求头过大，影响网络传递的性能，因此一般不会选择这个。

2、LocalStorage 或 SessionStorage

这个也是用前端存储商品信息，不过它的存储容量比 cookie 要大（通常 5MB），并且不会每次请求都带给后端，不过仍然是存储在浏览器，所以数据仅在本地生效，虽然关闭浏览器不会导致数据丢失，
但是清理浏览器缓存或者更换浏览器会导致数据的丢失。

3、Redis

在使用购物车功能时会生成一个临时匿名 ID（UUID）存储到 Cookie 或者 LocalStorage，后端则基于这个匿名 ID 在 Redis 中保存购物车中的数据，这种方式属于后端存储，
数据存储不会因为浏览器而被限制，并且后端统一管理数据，还能对这些数据进行偏好的计算，不过实现较为复杂，并且 Redis 是基于内存的，如果出现宕机等问题，也会造成数据丢失，
但总体是比存在浏览器要好的。

对于登录后的购物车功能，由于用户已登录，所以购物车需要和账号绑定，购物车的数据应该可以跨设备共享（例如 PC 添加，手机也能看到），因此需要支持购物车数据的长期存储。
它的数据存储方式有：

1、数据库

因为需要长期存储，所以并不适合用浏览器进行存储，因此可以使用数据库存储，而数据库的优点就是持久化，按用户 ID 存储购物车表：user_id + sku_id + 数量 + 勾选状态。
不过购物车是个读写都多的操作，所以频繁的读写数据库可能造成数据库性能压力过大（高并发场景下不推荐单独依赖数据库）。

2、Redis 

Redis 的优点就是访问速度快，适合高并发的场景，并且可以设置过期时间，数据的存储较为灵活，但数据较多时会占用大量内存，无法保证数据的长期持久化，需要定期处理或将数据存进数据库。

3、混合方案

可以让热数据存 Redis，冷数据或历史数据存入 MySQL，用户查询购物车时优先读 Redis，Redis 没获取到时再去数据库加载。当然，某个用户在登录后通常需要把未登录的购物车中的商品合并到登录后的购物车，
通过判断匿名 ID 是否一致来决定能否合并。而数据存储在 Redis 中通常使用 Hash 结构，key 为用户的 ID，field 为商品 ID，value 为该商品的详细信息。例如：

```redis
HSET cart:user:123 1001 '{"qty":2,"checked":1,"price":199.00,"title":"T-shirt"}'
HSET cart:user:123 1002 '{"qty":1,"checked":0,"price":299.00,"title":"Shoes"}'
```

```redis
127.0.0.1:6379> HGETALL cart:user:123
1) "1001"
2) "{\"qty\":2,\"checked\":1,\"price\":199.00,\"title\":\"T-shirt\"}"
3) "1002"
4) "{\"qty\":1,\"checked\":0,\"price\":299.00,\"title\":\"Shoes\"}"
```

因为 Redis 对小 Hash 会做专门的编码优化（ziplist/hashtable），同一 Key 下多个 field 存储比多个 Key 更节省内存。而且这样存储操作也更方便，因为购物车经常需要修改数据信息，
如果写为多个 key-value 的形式的话，那就需要从 value 中拿出完整的数据再进行判断是否为某个用户的某个商品，而用 Hash 结构，就可以直接通过 key 找到该用户的所有数据，
然后再根据 field 字段对应修改即可。

****
### 2.2 对象封装

购物车功能需要封装两个对象，一个用于管理某个 skuId 的商品信息，一个用于管理购物车界面所有的 skuId 集合。所以这里第一个 CartItem 封装的数据为商品 skuId、是否选中标识、
标题、图片、商品规格参数、价格、数量以及总价，因为价格需要手动计算，所以这里手机编写 getter/setter 方法，对价格进行计算。

```java
public class CartItem {
    private Long skuId;
    private Boolean check = true;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(this.count));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
```

关于 Cart 对象，它是用来封装多个 CartItem 对象的，所以要用一个集合接收 CartItem，然后计算所有商品的总数量、商品类型数量（不同的商品有几种）、商品总价和优惠价格。
所以这里也是手动编写 setter/getter 方法，但需要根据 CartItem 的数量来进行动态的计算所有商品的总价格和数量，有几个 CartItem 就是有几个 countType，
countNum 则为 CartItem 中的总数量累加之和，并且只计算勾选中的商品，totalAmount 则为 CartItem 的总价累加之和减去优惠价格。

```java
public class Cart {
    List<CartItem> cartItems;
    private Integer countNum;
    private Integer countType;
    private BigDecimal totalAmount; // 商品总价
    private BigDecimal reduceAmount = new BigDecimal("0.00"); // 优惠价格

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public Integer getCountNum() {
        int count = 0;
        if (!cartItems.isEmpty()) {
            for (CartItem cartItem : cartItems) {
                count += cartItem.getCount();
            }
        }
        return count;
    }


    public Integer getCountType() {
        int count = 0;
        if (!cartItems.isEmpty()) {
            for (CartItem cartItem : cartItems) {
                count += 1;
            }
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0.00");
        // 1. 计算购物项总价
        if (!cartItems.isEmpty()) {
            for (CartItem cartItem : cartItems) {
                if (cartItem.getCount() > 0 && cartItem.getCheck()) {
                    BigDecimal totalPrice = cartItem.getTotalPrice();
                    amount = amount.add(totalPrice);
                }
            }
        }
        // 2. 减去优惠价
        BigDecimal subtract = amount.subtract(this.getReduceAmount());
        return subtract;
    }

    public BigDecimal getReduceAmount() {
        return reduceAmount;
    }

    public void setReduceAmount(BigDecimal reduceAmount) {
        this.reduceAmount = reduceAmount;
    }
}
```

****
## 3. 创建临时用户

在使用购物车的功能时需要先区分用户是否登录，所以需要统一在请求进入时识别用户身份（登录用户/临时用户），并在请求结束时补发临时用户的 Cookie。临时用户的核心作用就是在用户未登录时，
为其提供一个唯一且稳定的标识，从而将购物车等临时数据与这个标识关联起来，它通过一个名为 user-key 的 Cookie 来记住数据，让购物车数据在多次访问中得以保留，即使还没有登录账号。
因此需要创建一个拦截器对购物车服务的所有请求进行拦截，判断是否为临时用户。再这之前，需要先封装一个对象，用于接收 user-key 标识：

```java
/**
 * 用来判断用户登录信息的对象
 */
@Data
public class LoginUserInfoTo {
    private Long userId;
    private String userKey;
    private boolean tempUser = false; // 标志位，判断是否有 userKey
}
```

这里有三个字段，分别接收已登录用户的 id，临时用户识别标志 userKey，以及判断是否有 userKey 的标志字段 tempUser，用这个对象把当前访问购物车功能的用户抽象成一个统一模型，
把登录用户和临时用户的逻辑统一封装。所以后续的代码就需要去 session 中获取到登录用户的 id，也就说该服务也需要配置 SpringSession。后面就根据是否有 userId 进行判断，
有 userId，就用 userId 查 redis，没有就用 user-key 查 redis，又因为这里需要有一个合并购物车的功能，所以不管是否登录没登录，都需要设置要给 user-key。

在拦截器中，先对所有的请求进行前置拦截，从 session 获取登录信息再判断该用户（已登录/临时）是否能从浏览器中获取到 user-key，为什么说去浏览器找呢，因为某个临时用户之前访问过购物车服务，
那时就应该给该用户生成一个 user-key，而下一次还是使用这个浏览器，那么就应该从浏览器中找。找到了就赋值，没找到证明是第一次，那就使用 UUID 给它赋值。但不管有没有登录，
都需要给该用户一个 user-key，因为后续合并购物车会用到这个。

```java
@Component
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<LoginUserInfoTo> threadLocal = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberResponseVo memberResponseVo = (MemberResponseVo) session.getAttribute(LoginConstant.LOGIN_USER);
        LoginUserInfoTo loginUserInfoTo = new LoginUserInfoTo();
        if (memberResponseVo != null) {
            // 用户登录
            loginUserInfoTo.setUserId(memberResponseVo.getId());
        }

        // 不管未登录还是登录，都从浏览器获取 user-key
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if (cookieName.equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    loginUserInfoTo.setUserKey(cookie.getValue());
                    loginUserInfoTo.setTempUser(true); // 获取到 user-key 就将标志位设为 true
                }
            }
        }
        
        // 如果没有分配临时用户，那就需要手动分配一个
        if (StringUtils.isEmpty(loginUserInfoTo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            loginUserInfoTo.setUserKey(uuid);
            loginUserInfoTo.setTempUser(true);
        }
        threadLocal.set(loginUserInfoTo);
        return true;
    }
}
```

前置拦截也就是给当前操作购物车服务者添加 user-key 标识，处理完后（在请求结束前）当然就需要把这个标识发送给浏览器存储，所以这里需要设置 cookie 的一些相关信息，设置它的 user-key、
使用域名范围以及过期时间。这样，下次请求时浏览器就会自动带上 Cookie，实现“同一个匿名用户”的购物车数据能持续生效。

```java
@Override
public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    LoginUserInfoTo loginUserInfoTo = threadLocal.get();
    if (loginUserInfoTo != null) {
        if (loginUserInfoTo.isTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, loginUserInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
```

这里调用 response.addCookie(cookie); 就是往响应头里写入一条 Set-Cookie：

```http request
HTTP/1.1 200 OK
Content-Type: text/html
Set-Cookie: user-key=abc-123-xyz; Domain=gulimall.com; Max-Age=2592000; Path=/
```

而需要让拦截器生效，就需要让它注入容器，所以这里通过实现 WebMvcConfigurer 接口来重写方法进行拦截器的注册与指定拦截器的匹配路径，因为想让这个逻辑对整个服务都生效，
就需要把拦截器注册到 MVC 拦截器链里让其生效。

```java
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {
    @Autowired
    private CartInterceptor cartInterceptor;
  
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(cartInterceptor).addPathPatterns("/**");
    }
}
```

****
## 4. 添加购物车

添加购物车功能就是点击某个商品后，选择好对应的销售属性和数量，然后点击加入购物车，此时就会跳转到购物车页面，在该页面会显示刚刚添加的商品信息。如果要把商品信息展示在购物车页面，
那就需要查询具体的数据库信息，所以 skuId 是必须要有的，而添加的商品数量也可以通过前端传递，这样后端就方便计算商品价格了。

```html
<div class="box-btns-one">
    <input type="text" name="" id="numInput" value="1" />
    <div class="box-btns-one1">

        <div>
            <button id="jia">
        +
        </button>
        </div>
        <div>
            <button id="jian">
            -
        </button>
        </div>
    </div>
</div>
```

```html
<div class="box-btns-two">
    <a href="#" id="addToCartA" th:attr="skuId=${item.skuInfo.skuId}">
        加入购物车
    </a>
</div>
```

```js
$("#addToCartA").click(function () {
    var num = $("#numInput").val();
    var skuId = $(this).attr("skuId");
    location.href="http://cart.gulimall.com/addToCart?skuId=" + skuId + "&num=" + num;
    return false;
})
```

Controller 层：

在控制层就是接收 skuId 和商品数量 num，然后把封装好的 CartItem 对象存进域中，这样前端就可以获取到了。

```java
@GetMapping("/addToCart")
public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, Model model) {
    CartItem cartItem = cartService.addToCart(skuId, num);
    model.addAttribute("cartItem", cartItem);
    return "success";
}
```

Service 层：

添加商品到购物车的流程就是先对当前的操作用户进行判断，需要知道该用户是否登录，如果登录了，那就把数据存入以用户 id 为 key 的 redis 中，否则就用 user-key 的值作为 key，
而在操作 redis 时需要先指定 key 然后再 put 数据进去，所以这里为了方便就封装成一个方法，让 redis 的操作提前绑定好 key，后续获取到这个绑定好 key 的操作直接 put 数据即可。
而判断是否登录也很简单，在创建临时用户时就已经解释了，只要判断从 ThreadLocal 获取到的 LoginUserInfoTo 对象有无 userId 即可。

```java
/**
 * 获取要操作的购物车
 */
private BoundHashOperations<String, Object, Object> getCartOps() {
    // 1. 判断是否为登录用户进行购物车操作
    LoginUserInfoTo loginUserInfoTo = CartInterceptor.threadLocal.get();
    String cartKey = "";
    if (loginUserInfoTo.getUserId() != null) {
        // 已登录
        cartKey = CartConstant.CART_PREFIX + loginUserInfoTo.getUserId();
    } else {
        cartKey = CartConstant.CART_PREFIX + loginUserInfoTo.getUserKey();
    }
    // 2. 绑定 redis 操作的 key
    BoundHashOperations<String, Object, Object> boundHashOperations = stringRedisTemplate.boundHashOps(cartKey);
    return boundHashOperations;
}
```

设置好 redis 要操作的 key 后，就可以开始对购物车商品对象 CartItem 的封装了，对于添加商品到购物车而言，它分两种情况：一种是购物车中有相同的商品，
那么此时添加就是在原商品的基础上增加数量即可；另一种是购物车中没有该商品，那么就是直接新增即可。也就是说，一个是修改 redis 中的数据，一个是把数据新增进 redis。

所以需要先从 redis 中获取该 skuId 的商品信息，如果不能获取到，那证明就是新增商品：新增商品这里采用的是异步编程，因为这个过程涉及多个查询数据库的操作，使用异步效率更高。
首先就是远程查询当前要添加的商品信息，所以需要获取到 SkuInfoEntity 对象，远程调用 gulimall-product 服务的方法即可，该方法较为简单，直接查询表 pms_sku_info 即可。
从获取到的 SkuInfoEntity 中可以拿到要添加进购物车的商品的图片、标题、单价等信息，而关于商品的销售属性则需要远程调用另一个方法。

```java
@Override
public CartItem addToCart(Long skuId, Integer num) {
    BoundHashOperations<String, Object, Object> cartOps = getCartOps();
    // 判断 Redis 中是否有相同的商品
    String redisSku = (String) cartOps.get(skuId.toString());
    if (StringUtils.isEmpty(redisSku)) {
        // 购物车中没有此商品
        CartItem cartItem = new CartItem();
        // 1. 远程查询当前要添加的商品信息
        CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
            R r = productFeignService.getSkuInfo(skuId);
            if (r.getCode() == 0) {
                SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItem.setSkuId(skuId);
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setTitle(skuInfo.getSkuTitle());
                cartItem.setPrice(skuInfo.getPrice());
            }
        }, threadPoolExecutor);
}
```

关于获取商品的销售属性，需要远程调用以下方法，主要就是从数据库中根据 skuId 查询表 pms_sku_sale_attr_value，通过 concat(attr_name, ": ", attr_value) 方法，
把属性名和属性值拼接成一个字符串，比如 "颜色: 红色" "尺码: XL"，所以最终返回结果是一个字符串列表，每个字符串是一个 "属性名: 属性值"，这样就不需要再通过 R 对象获取数据再转型了。

```java
@Override
public List<String> getSkuSaleAttrValueList(Long skuId) {
    return skuSaleAttrValueDao.getSkuSaleAttrValueList(skuId);
}
```

```xml
<select id="getSkuSaleAttrValueList" resultType="java.lang.String">
    select concat(attr_name, ": ", attr_value) from pms_sku_sale_attr_value where sku_id = #{skuId}
</select>
```

当然查询销售属性也是一个异步任务，它和上面的远程调用查询不互相依赖，所以都是用的是 runAsync() 方法，最后等待这两个异步任务都完成后就可以把封装好的 CartItem 写进 redis 了。

```java
@Override
public CartItem addToCart(Long skuId, Integer num) {
    BoundHashOperations<String, Object, Object> cartOps = getCartOps();
    // 判断 Redis 中是否有相同的商品
    String redisSku = (String) cartOps.get(skuId.toString());
    if (StringUtils.isEmpty(redisSku)) {
        // 购物车中没有此商品
        ...
        // 2. 远程查询 sku 组合信息
        CompletableFuture<Void> getSkuSaleAttrValueListFuture = CompletableFuture.runAsync(() -> {
            List<String> skuSaleAttrValueList = productFeignService.getSkuSaleAttrValueList(skuId);
            cartItem.setSkuAttr(skuSaleAttrValueList);
        }, threadPoolExecutor);
        // 等待异步任务完成
        try {
            CompletableFuture.allOf(getSkuInfoFuture, getSkuSaleAttrValueListFuture).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        // 3. 将 CartItem 对象写进 Redis
        try {
            cartOps.put(skuId.toString(), objectMapper.writeValueAsString(cartItem));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return cartItem;
    } 
}
```

对于已添加的商品，再次添加时只需要修改数量即可，所以先从 Redis 中获取到数据，接着把该数据从 JSON 转换成原对象类型，通过 setter 方法修改数量，然后再转换成 JSON 并存入 redis。

```java
@Override
public CartItem addToCart(Long skuId, Integer num) {
    BoundHashOperations<String, Object, Object> cartOps = getCartOps();
    // 判断 Redis 中是否有相同的商品
    String redisSku = (String) cartOps.get(skuId.toString());
    if (StringUtils.isEmpty(redisSku)) {
        ...
    } else {
        // 有此商品，修改数量即可
        CartItem cartItem;
        try {
            cartItem = objectMapper.readValue(redisSku, CartItem.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        cartItem.setCount(cartItem.getCount() + num);
        try {
            cartOps.put(skuId.toString(), objectMapper.writeValueAsString(cartItem));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return cartItem;
    }
}
```

当然为了能够看到某个用户的购物车信息，也需要在这些页面获取到 session 中的登录用户信息：

```html
<li>
    <a th:if="${session.loginUser == null}"
       href="http://auth.gulimall.com/login.html">
        你好，请登录
    </a>
    <a th:if="${session.loginUser != null}">
        你好，[[${session.loginUser.nickname}]]
    </a>
</li>

<li>
    <a th:if="${session.loginUser == null}"
       href="http://auth.gulimall.com/reg.html"
       class="li_2">
        免费注册
    </a>
    <span th:if="${session.loginUser != null}" class="li_2 disabled">
       已注册
    </span>
</li>
```

不过目前的代码存在一个问题:当前已经添加了一个商品，此时浏览器的 url 为 http://cart.gulimall.com/addToCart?skuId=31&num=4 ，如果刷新页面，那就会再次发送该请求，
就等于又添加了一次购物车，这显然是不合理的，因此需要修改原来的 Controller，不能直接跳转到 success 页面，而是执行完添加逻辑后，
使用重定向把 skuId 作为参数传递给另一个 Controller 方法，由这个方法通过 skuId 查询刚刚添加的数据信息，这样再次刷新页面进入的就是 addToCartSuccessPage() 方法了，
该方法不会添加商品，只进行查询展示。

```java
@GetMapping("/addToCart")
public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes redirectAttributes) {
    CartItem cartItem = cartService.addToCart(skuId, num);
    redirectAttributes.addAttribute("skuId", skuId); // 重定向时会自动把 skuId 拼接到 url 后面：.../addToCartSuccess.html?skuId=31
    return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
}
```

```java
@GetMapping("/addToCartSuccess.html")
public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
    // 通过 skuId 再查一遍购物车信息
    CartItem cartItem = cartService.getCartItem(skuId);
    model.addAttribute("cartItem", cartItem);
    return "success";
}
```

```java
@Override
public CartItem getCartItem(Long skuId) {
    BoundHashOperations<String, Object, Object> cartOps = getCartOps();
    String redisSku = (String) cartOps.get(skuId.toString());
    CartItem cartItem;
    try {
        cartItem = objectMapper.readValue(redisSku, CartItem.class);
    } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
    }
    return cartItem;
}
```

****
## 5. 展示购物车中添加的商品

Controller 层：

定义一个 getCart() 方法获取到一个 Cart 对象，该对象里封装的数据就是购物车页面整体展示需要的数据，所以只需要获取一个这个对象并存入域中即可。

```java
@GetMapping("/cart.html")
public String cartListPage(Model model) {
    Cart cart = cartService.getCart();
    model.addAttribute("cart", cart);
    return "cartList";
}
```

Service 层：

对于查询购物车来说，也同样需要区分两种情况。如果用户只在登录情况下进行过商品添加至购物车，那么就可以直接查询 redis 获取到数据并返回；如果在未登录的时候也进行过添加操作，
那么在他登陆后，就需要把它临时添加进购物车的商品数据合并到此时登录的用户的购物车中，也就是把 user-key 为 key 的 redis 数据合并到以 userId 为 key 的 redis 数据中，
如果不存在以 userId 为 key 的 redis 数据，那么就直接以当前登录用户的 userId 作为 key 新建一个即可。对于合并操作来说，其实就是再调用一次添加购物车的那个方法而已，
把查到的商品中的 skuId 和数量 count 传递给 addToCart 方法，由它把数据添加进对应的 redis 数据。

```java
@Override
public Cart getCart() {
    Cart cart = new Cart();
    LoginUserInfoTo userInfo = CartInterceptor.threadLocal.get();
    String tempCartKey = CartConstant.CART_PREFIX + userInfo.getUserKey();
    // 最终返回的购物车数据
    List<CartItem> cartItems = new ArrayList<>();
    if (userInfo.getUserId() != null) {
        // 登录用户
        String userCartKey = CartConstant.CART_PREFIX + userInfo.getUserId();
        // 1. 获取临时购物车
        List<CartItem> tempItems = getCartItems(tempCartKey);
        // 2. 如果临时购物车有数据，就合并到登录购物车
        if (tempItems != null) {
            for (CartItem item : tempItems) {
                addToCart(item.getSkuId(), item.getCount());
            }
            // 3. 清空临时购物车
            stringRedisTemplate.delete(tempCartKey);
        }
        // 4. 查询合并后的登录购物车
        cartItems = getCartItems(userCartKey);
    } else {
        // 未登录，直接查临时购物车
        cartItems = getCartItems(tempCartKey);
    }
    cart.setCartItems(cartItems);
    return cart;
}
```

因为是查询所有，所以这里不需要传递 key 值，直接查询 redis 的 Hash 中的所有 value，然后遍历它们转换成 CartItem 对象并封装进集合返回。

```java
private List<CartItem> getCartItems(String cartKey) {
    BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(cartKey);
    List<Object> values = ops.values();
    if (values == null || values.isEmpty()) return new ArrayList<>();
    return values.stream()
            .filter(Objects::nonNull) // 过滤掉 null
            .map(obj -> {
        try {
            return objectMapper.readValue((String) obj, CartItem.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }).collect(Collectors.toList());
}
```

前端也需要动态的展示这些数据：

```html
<div>
    <ol>
        <li><input type="checkbox" class="check" th:checked="${cartItem.check}"></li>
        <li>
            <dt><img th:src="${cartItem.image}" alt="">图片</dt>
            <dd style="width: 400px">
                <p>
                    <span th:text="${cartItem.title}">标题</span>
                    <br/>
                    <span th:each="saleAttr : ${cartItem.skuAttr}" th:text="${saleAttr}">销售属性</span>
                </p>
            </dd>
        </li>
        <li>
            <p class="dj" th:text="'￥' + ${#numbers.formatDecimal(cartItem.price, 3, 2)}">价格</p>
        </li>
        <li>
            <p>
                <span>-</span>
                <span th:text="${cartItem.count}">5</span>
                <span>+</span>
            </p>
        </li>
        <li style="font-weight:bold"><p class="zj">￥[[${#numbers.formatDecimal(cartItem.totalPrice, 1, 2)}]]</p></li>
    </ol>
</div>
```

****



