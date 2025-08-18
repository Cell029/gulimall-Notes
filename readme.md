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
public List<SkuEsModel> up(Long spuId) {
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
  return skuEsModels;
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
    long stock = wareSkuDao.getSkuStock(skuId);
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
return skuEsModels;
```

****






