# wxpay

基于 **Spring Boot 3** 与 **Vue 3** 的微信支付 **Native（扫码）** 下单示例：套餐展示、创建订单、生成支付二维码、订单状态查询与支付回调处理。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 17、Spring Boot 3.2、MyBatis-Plus、MySQL |
| 支付 | [wechatpay-java](https://github.com/wechatpay-apiv3/wechatpay-java)（APIv3） |
| 前端 | Vue 3、Vite 5、Vue Router、Pinia、Naive UI、Axios、qrcode |

## 目录结构

```
wxpay/
├── backend/                 # Spring Boot 服务
│   ├── src/main/java/       # 业务与接口（PaymentController 等）
│   └── src/main/resources/
│       ├── application.yml  # 端口、数据源、JWT、微信支付等配置
│       └── db/              # schema.sql / data.sql（表结构与示例数据）
├── frontend/                # Vue 单页应用
│   ├── src/
│   │   ├── api/             # Axios 封装与支付相关接口
│   │   ├── views/           # 首页、支付页、结果页
│   │   └── router/
│   └── vite.config.js       # 开发服务器与 /api 代理
└── README.md
```

## 环境要求

- **JDK 17**、**Maven 3.6+**
- **Node.js 18+**（建议 LTS）
- **MySQL 5.7+ / 8.x**，并具备创建库与执行 SQL 的权限

## 数据库

1. 按 `backend/src/main/resources/db/schema.sql` 创建库表（默认库名为 `root`，可按需修改 SQL 与 `application.yml` 中的 JDBC URL 保持一致）。
2. 按需执行 `data.sql` 导入套餐等初始数据。

`application.yml` 中默认示例：

- JDBC：`jdbc:mysql://localhost:3306/root?...`
- 账号密码：`root` / `root`（请改为你的实际环境）。

## 配置说明

编辑 `backend/src/main/resources/application.yml`：

| 配置项 | 说明 |
|--------|------|
| `server.port` | HTTP 端口，当前为 **8050** |
| `spring.datasource.*` | MySQL 连接 |
| `jwt.*` | JWT 密钥与过期时间（若后续扩展登录可用） |
| `wechat.pay.*` | 商户号、AppID、APIv3 密钥、证书序列号、**notify-url**、商户私钥等 |

**支付回调** `notify-url` 必须为微信服务器可访问的 **HTTPS 公网地址**；本地开发可使用内网穿透（如 natapp、ngrok）指向本机 `http://<本机>:8050/api/pay/notify`。

## 后端接口（前缀 `/api`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/plans` | 套餐列表 |
| POST | `/order/create` | 创建订单（body：`planId`） |
| POST | `/pay/native` | 发起 Native 支付（body：`orderNo`） |
| GET | `/order/status/{orderNo}` | 查询订单状态（待支付时会尝试向微信查询） |
| POST | `/order/cancel/{orderNo}` | 取消订单 |
| POST | `/pay/notify` | 微信支付异步通知（微信服务器调用，勿用于浏览器直接访问） |

统一响应封装为项目内 `Result` 类型。

## 运行方式

### 1. 启动后端

```bash
cd backend
mvn spring-boot:run
```

服务默认：**http://localhost:8050**。

打包：

```bash
cd backend
mvn clean package -DskipTests
# 可执行 jar 位于 target/ 目录
```

### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
```

开发服务器默认 **http://localhost:3000**（若端口被占用，Vite 会自动顺延）。`vite.config.js` 已将 `/api` 代理到 `http://localhost:8050`。

前端 Axios 默认 `baseURL` 为 `http://localhost:8050/api`（见 `frontend/src/api/index.js`）；若只走 Vite 代理，可改为相对路径 `/api` 以避免跨域与端口硬编码。

生产构建：

```bash
cd frontend
npm run build
# 输出目录：frontend/dist
```

## 前端路由

| 路径 | 页面 |
|------|------|
| `/` | 首页 |
| `/payment` | 支付（选套餐、下单、扫码） |
| `/pay-result` | 支付结果 |

## 常见问题

- **回调收不到**：检查 `notify-url` 是否公网可达、是否 HTTPS、防火墙与穿透是否指向正确端口。
- **前端连不上后端**：确认后端已监听 8050，且与 `index.js` / `vite.config.js` 中地址一致。
- **数据库连接失败**：确认 MySQL 已启动、库名与账号密码与配置一致。

## 许可证

若用于学习或二次开发，请自行补充许可证声明；使用微信支付需遵守[微信支付平台规则](https://pay.weixin.qq.com/)。
