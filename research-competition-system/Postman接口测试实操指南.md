# Postman 接口测试实操指南

> **项目：** 竞赛科研管理系统 | **Base URL：** `http://localhost:8080/api` | **接口数：** 80+

---

## 一、Postman 环境搭建

### 1.1 创建环境变量

打开 Postman → Environments → 新建 `科研竞赛管理系统-本地`，添加以下变量：

| 变量名 | 初始值 | 当前值 | 说明 |
|--------|--------|--------|------|
| `base_url` | `http://localhost:8080/api` | `http://localhost:8080/api` | 接口根路径 |
| `admin_token` | （留空，Pre-request自动填充） | 运行时填充 | ADMIN 的 JWT Token |
| `student_token` | （留空） | 运行时填充 | STUDENT 的 JWT Token |
| `secretary_token` | （留空） | 运行时填充 | SECRETARY 的 JWT Token |
| `leader_token` | （留空） | 运行时填充 | LEADER 的 JWT Token |
| `test_competition_id` | （留空） | 运行时填充 | 测试用的竞赛ID |
| `test_user_id` | （留空） | 运行时填充 | 测试用的用户ID |
| `test_file_id` | （留空） | 运行时填充 | 测试用的文件ID |

### 1.2 创建 Collection

新建 Collection 命名为 `科研竞赛管理系统`，按如下结构建立 Folder：

```
科研竞赛管理系统/
├── 00-环境初始化（自动获取Token）
├── 01-认证模块 /auth
├── 02-用户管理 /user
├── 03-公告管理 /announcement
├── 04-系统日志 /log
├── 05-个人中心 /personal
├── 06-学科竞赛 /competition
├── 07-大创项目 /innovation
├── 08-软件著作权 /copyright
├── 09-学术论文 /paper
├── 10-CCF目录 /ccf
├── 11-文件管理 /file
├── 12-审核管理 /review
├── 13-时间线 /timeline
├── 14-通知消息 /notification
├── 15-重复校验 /check
├── 16-OCR识别 /ocr
└── 17-数据统计 /statistics
```

### 1.3 设置 Collection 级别的通用配置

选中 Collection → Pre-request Script 标签页，不填（每个 Folder 按角色单独设置）。

选中 Collection → Tests 标签页，填入**通用断言**：

```javascript
// === 通用响应断言（对所有请求生效） ===

// 1. 检查HTTP状态码
pm.test(`[HTTP] 状态码正常 (${pm.response.code})`, function () {
    pm.expect(pm.response.code).to.be.oneOf([200, 201, 204, 403, 404]);
});

// 2. 如果有响应body，检查响应时间
pm.test("[性能] 响应时间 ≤ 3000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(3000);
});
```

---

## 二、认证处理（核心机制）

### 2.1 方式一：Collection Runner 统一登录（推荐）

在 `00-环境初始化` Folder 中创建 4 个请求：

**请求：ADMIN-获取Token**
```
POST {{base_url}}/auth/login
Body: {"username": "admin", "password": "admin123"}
```
Tests 脚本：
```javascript
const resp = pm.response.json();
if (resp.code === 200) {
    pm.environment.set("admin_token", resp.data.token);
    console.log("✓ ADMIN  Token 已获取");
} else {
    console.error("✗ ADMIN  登录失败:", resp.message);
}
```

同理创建 `STUDENT-获取Token`（student1）、`SECRETARY-获取Token`（secretary）、`LEADER-获取Token`（leader），将 Token 存入对应的环境变量。

### 2.2 方式二：每个请求自动登录（独立调试用）

在需要认证的 Folder 或 Request 的 Pre-request Script 中：

```javascript
// 获取ADMIN Token（仅在token为空时重新获取）
const token = pm.environment.get("admin_token");
if (!token || token === "") {
    pm.sendRequest({
        url: pm.environment.get("base_url") + "/auth/login",
        method: 'POST',
        header: {'Content-Type': 'application/json'},
        body: {mode: 'raw', raw: JSON.stringify({
            username: "admin",
            password: "admin123"
        })}
    }, (err, res) => {
        if (!err && res.json().code === 200) {
            pm.environment.set("admin_token", res.json().data.token);
        }
    });
}
```

### 2.3 每个请求的 Authorization 设置

所有需要认证的请求（除 `/auth/login`、`GET /file/{id}`），在 **Authorization 标签页**设置：

```
Type: Bearer Token
Token: {{admin_token}}   ← 根据测试的角色切换为对应变量
```

---

## 三、断言模板库（Test Script）

### 3.1 成功响应断言（code=200）
```javascript
const resp = pm.response.json();
pm.test("[业务] code=200 操作成功", function () {
    pm.expect(resp.code).to.eql(200);
    pm.expect(resp.message).to.eql("success");
});
```

### 3.2 分页响应断言
```javascript
const resp = pm.response.json();
pm.test("[业务] code=200", () => pm.expect(resp.code).to.eql(200));
pm.test("[结构] 分页字段完整", function () {
    pm.expect(resp.data).to.have.property("total");
    pm.expect(resp.data).to.have.property("current");
    pm.expect(resp.data).to.have.property("pageSize");
    pm.expect(resp.data.records).to.be.an("array");
});
pm.test("[数据] 返回记录数 ≤ pageSize", function () {
    pm.expect(resp.data.records.length).to.be.at.most(resp.data.pageSize);
});
```

### 3.3 创建成功断言
```javascript
const resp = pm.response.json();
pm.test("[业务] code=200", () => pm.expect(resp.code).to.eql(200));
pm.test("[数据] 返回ID不为空", function () {
    const id = resp.data.id || resp.data;
    pm.expect(id).to.not.be.null;
    // 保存ID到环境变量供后续测试使用
    // pm.environment.set("created_id", id);
});
```

### 3.4 权限拒绝断言（403）
```javascript
pm.test("[权限] 返回403拒绝访问", function () {
    pm.expect(pm.response.code).to.eql(403);
});
```

### 3.5 登录失败断言
```javascript
const resp = pm.response.json();
pm.test("[HTTP] 状态码200", () => pm.expect(pm.response.code).to.eql(200));
pm.test("[业务] code=401 登录失败", () => pm.expect(resp.code).to.eql(401));
pm.test("[安全] 提示不泄露具体原因", function () {
    pm.expect(resp.message).to.include("用户名或密码错误");
});
```

### 3.6 输入校验失败断言
```javascript
const resp = pm.response.json();
pm.test("[校验] 返回错误提示", function () {
    pm.expect(resp.code).to.not.eql(200);
    pm.expect(resp.message).to.not.be.empty;
});
```

---

## 四、接口测试执行流程

### 4.1 冒烟测试（P0 — 每人每模块至少走一次）

按以下顺序在 Postman 中手动执行，验证核心流程：

```
第 1 轮：认证验证
  ├── POST /auth/login（admin 正确密码）
  ├── POST /auth/login（错误密码 → code=401）
  ├── GET /auth/current-user（携带 Token → 返回用户信息）
  └── GET /auth/current-user（不携带 Token → 403）

第 2 轮：权限边界验证（ADMIN 角色）
  ├── GET /user/page（ADMIN → 200）
  ├── GET /user/page（STUDENT → 403 无权限）
  ├── GET /log/page（STUDENT → 403）
  └── POST /announcement（STUDENT → 403）

第 3 轮：核心业务流程
  ├── POST /competition（STUDENT提交竞赛）
  ├── GET /review/todo（SECRETARY查看待审核）
  ├── POST /review/approve（SECRETARY审核通过）
  ├── GET /review/todo（LEADER查看待审核）
  ├── POST /review/approve（LEADER终审通过→归档）
  └── GET /timeline/competition/{id}（验证时间线完整）

第 4 轮：文件流程
  ├── POST /file/upload（上传PDF）
  ├── GET /file/{id}（不带Token→获取成功⚠️）
  ├── GET /file/preview/{id}（PDF预览）
  └── DELETE /file/{id}（删除文件）
```

### 4.2 完整回归测试（P0+P1）

使用 Collection Runner 批量执行：

```
1. 先执行 00-环境初始化 Folder（获取4种角色Token）
2. 按 Folder 顺序依次 Run：
   - 01-认证模块（以ADMIN身份）
   - 02-用户管理（以ADMIN身份 + 以STUDENT身份验证越权）
   - ...
3. 查看 Runner Summary：
   - 通过率 = 通过数 / 总数
   - 失败列表 → 逐个排查
   - 平均响应时间 → 标记 >3s 的接口
```

### 4.3 权限矩阵测试（关键）

对每个模块的 CRUD 接口，使用 4 种角色 Token 分别测试：

| 接口 | ADMIN | SECRETARY | LEADER | STUDENT |
|------|-------|-----------|--------|---------|
| GET /user/page | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 403 |
| GET /competition/page | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 |
| POST /competition | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 |
| POST /review/approve | ✅ 200 | ✅ 200 | ✅ 200 | ❌ 403 |
| GET /log/page | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 403 |
| GET /personal/achievements | N/A | N/A | N/A | ✅ (仅自己的) |

> 在 Postman 中为同一个接口创建 4 个 Request 变量副本，分别使用不同的 Token。

---

## 五、Collection Runner + Newman（CI集成）

### 5.1 导出 Collection + Environment

```
Postman → Collection → ⋯ → Export → 选择 v2.1 格式
Postman → Environment → ⋯ → Export
```

### 5.2 Newman 命令行执行

```bash
# 安装
npm install -g newman

# 执行全部接口测试
newman run 竞赛科研竞赛管理系统.postman_collection.json \
  -e 科研竞赛管理系统-本地.postman_environment.json \
  --reporters cli,html \
  --reporter-html-export report.html \
  --timeout-request 10000

# 只执行某个 Folder（如认证模块）
newman run 科研竞赛管理系统.postman_collection.json \
  -e 科研竞赛管理系统-本地.postman_environment.json \
  --folder "01-认证模块" \
  --reporters cli
```

### 5.3 输出报告示例
```
┌─────────────────────────┬─────────────┬────────────┐
│                         │   executed  │    failed   │
├─────────────────────────┼─────────────┼────────────┤
│        iterations       │      1      │      0      │
│        requests         │     85      │      3      │
│    test-scripts         │     85      │      0      │
│      prerequest-scripts │     20      │      0      │
│        assertions       │    340      │     12      │
├─────────────────────────┴─────────────┴────────────┤
│ total run duration: 45.2s                          │
│ total data received: 1.24MB                        │
│ average response time: 287ms                       │
└────────────────────────────────────────────────────┘
```

---

## 六、常见问题排查

| 问题 | 原因 | 解决 |
|------|------|------|
| 所有请求返回403空body | Token未设置或过期 | 重新执行 00-环境初始化 获取Token |
| 分页接口返回0条数据 | 数据库无数据 | 先执行POST创建测试数据 |
| 文件上传返回500 | 文件>50MB或格式不支持 | 检查文件大小和类型 |
| POST /user 返回403 | 当前Token角色非ADMIN | 切换为 `{{admin_token}}` |
| 审核接口返回500 | 成果类型/ID不存在 | 先确认成果已创建且状态正确 |
| 响应时间>5s | 后端未启动或数据库连接慢 | 检查 `http://localhost:8080` 是否可访问 |

---

## 七、测试账号速查

| 用户名 | 密码 | 角色 | Token变量 | 用途 |
|--------|------|------|-----------|------|
| admin | admin123 | ADMIN（管理员） | `{{admin_token}}` | 全部操作 |
| secretary | admin123 | SECRETARY（秘书） | `{{secretary_token}}` | 初审 |
| leader | admin123 | LEADER（领导） | `{{leader_token}}` | 终审 |
| student1 | admin123 | STUDENT（学生） | `{{student_token}}` | 提交成果 |
| teacher1 | admin123 | STUDENT（教师） | `{{student_token}}` | 提交成果 |

---

## 八、测试优先级与执行策略

| 轮次 | 范围 | 用例数 | 触发时机 | 预期耗时 |
|------|------|--------|---------|---------|
| **冒烟测试** | P0（40条核心接口×1场景） | ~60条 | 每次提交前 | 10分钟 |
| **功能回归** | P0+P1（全部接口×正常场景） | ~120条 | 每日/发版前 | 20分钟 |
| **权限回归** | P0+P1（全部接口×4种角色） | ~300条 | 每周 | 30分钟 |
| **异常输入** | P1+P2（边界/非法参数） | ~200条 | 发版前 | 20分钟 |
| **性能基线** | P0（JMeter 50并发） | ~15条 | 每周/发版前 | 10分钟 |
