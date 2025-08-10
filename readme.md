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




