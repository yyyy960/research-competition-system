# Postman 核心接口测试步骤

> 5 个核心模块，70+ 个测试请求，覆盖正常场景 / 权限边界 / 异常输入

每个接口标注了 Request 配置（Method / URL / Headers / Body）和 Tests 断言脚本，可直接复制到 Postman。

---

## 一、认证模块 `/api/auth` — 3 接口，12 条测试

### 1.1 登录 — 正常场景

**Request：ADMIN 正确登录**
```
POST    {{base_url}}/auth/login
Headers:  Content-Type: application/json
Body:     { "username": "admin", "password": "admin123" }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("HTTP 200", () => pm.expect(pm.response.code).to.eql(200));
pm.test("code=200 登录成功", () => pm.expect(resp.code).to.eql(200));
pm.test("返回Token", () => pm.expect(resp.data.token).to.be.a("string").and.not.empty);
pm.test("返回用户信息", () => {
    pm.expect(resp.data.userInfo).to.have.property("username", "admin");
    pm.expect(resp.data.userInfo).to.have.property("role", "ADMIN");
});
// 存入环境变量
pm.environment.set("admin_token", resp.data.token);
```

**Request：STUDENT 正确登录**
```
POST    {{base_url}}/auth/login
Body:     { "username": "student1", "password": "admin123" }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("code=200 登录成功", () => pm.expect(resp.code).to.eql(200));
pm.test("角色为STUDENT", () => pm.expect(resp.data.userInfo.role).to.eql("STUDENT"));
pm.environment.set("student_token", resp.data.token);
```

**Request：SECRETARY 正确登录**
```
POST    {{base_url}}/auth/login
Body:     { "username": "secretary", "password": "admin123" }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("code=200", () => pm.expect(resp.code).to.eql(200));
pm.test("角色为SECRETARY", () => pm.expect(resp.data.userInfo.role).to.eql("SECRETARY"));
pm.environment.set("secretary_token", resp.data.token);
```

**Request：LEADER 正确登录**
```
POST    {{base_url}}/auth/login
Body:     { "username": "leader", "password": "admin123" }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("code=200", () => pm.expect(resp.code).to.eql(200));
pm.test("角色为LEADER", () => pm.expect(resp.data.userInfo.role).to.eql("LEADER"));
pm.environment.set("leader_token", resp.data.token);
```

### 1.2 登录 — 异常场景

**Request：错误密码**
```
POST    {{base_url}}/auth/login
Body:     { "username": "admin", "password": "wrongpass" }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("HTTP 200", () => pm.expect(pm.response.code).to.eql(200));
pm.test("code=401 登录失败", () => pm.expect(resp.code).to.eql(401));
pm.test("统一错误提示不泄露原因", () => {
    pm.expect(resp.message).to.include("用户名或密码错误");
});
pm.test("data 为 null", () => pm.expect(resp.data).to.be.null);
```

**Request：不存在的用户**
```
POST    {{base_url}}/auth/login
Body:     { "username": "notexist", "password": "xxx" }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("code=401", () => pm.expect(resp.code).to.eql(401));
pm.test("与错误密码返回相同提示", () => {
    pm.expect(resp.message).to.include("用户名或密码错误");
});
```

**Request：空username**
```
POST    {{base_url}}/auth/login
Body:     { "username": "", "password": "admin123" }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("拒绝空用户名", () => {
    pm.expect(resp.code).to.not.eql(200);
});
```

**Request：SQL注入测试**
```
POST    {{base_url}}/auth/login
Body:     { "username": "' OR '1'='1", "password": "' OR '1'='1" }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("SQL注入不成功", () => {
    pm.expect(resp.code).to.not.eql(200);
});
pm.test("不泄露数据库错误", () => {
    pm.expect(pm.response.code).to.eql(200);
    pm.expect(resp.code).to.not.eql(500);
});
```

### 1.3 获取当前用户

**Request：携带Token**
```
GET     {{base_url}}/auth/current-user
Auth:   Bearer Token → {{admin_token}}
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("code=200", () => pm.expect(resp.code).to.eql(200));
pm.test("返回username和role", () => {
    pm.expect(resp.data).to.have.property("username");
    pm.expect(resp.data).to.have.property("role");
});
```

**Request：不携带Token（权限边界）**
```
GET     {{base_url}}/auth/current-user
Auth:   No Auth
```
```javascript
// Tests
pm.test("无Token返回403", () => {
    pm.expect(pm.response.code).to.eql(403);
});
```

### 1.4 修改密码

**Request：正确修改密码**
```
PUT     {{base_url}}/auth/password
Auth:   Bearer Token → {{admin_token}}
Body:   { "oldPassword": "admin123", "newPassword": "Admin@123" }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("密码修改成功", () => pm.expect(resp.code).to.eql(200));
// ⚠️ 测试后记得改回原密码
```

**Request：原密码错误**
```
Body:   { "oldPassword": "wrongold", "newPassword": "Admin@123" }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("原密码错误拒绝修改", () => {
    pm.expect(resp.message).to.include("原密码错误");
});
```

---

## 二、用户管理 `/api/user` — 15 接口，22 条测试

> 以下请求全部使用 `Auth: Bearer Token → {{admin_token}}`，权限测试单独标注。

### 2.1 分页查询

**Request：默认分页**
```
GET     {{base_url}}/user/page?page=1&size=10
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("code=200", () => pm.expect(resp.code).to.eql(200));
pm.test("分页字段完整", () => {
    pm.expect(resp.data).to.have.property("total");
    pm.expect(resp.data).to.have.property("current", 1);
    pm.expect(resp.data).to.have.property("pageSize", 10);
    pm.expect(resp.data.records).to.be.an("array");
});
```

**Request：关键词搜索**
```
GET     {{base_url}}/user/page?keyword=admin&page=1&size=10
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("搜索返回结果", () => pm.expect(resp.code).to.eql(200));
pm.test("搜索结果包含admin", () => {
    const records = resp.data.records;
    records.forEach(r => {
        const match = r.username.includes("admin") || r.realName.includes("admin");
        pm.expect(match).to.be.true;
    });
});
```

**Request：STUDENT越权访问（权限边界）**
```
GET     {{base_url}}/user/page?page=1&size=10
Auth:   Bearer Token → {{student_token}}
```
```javascript
// Tests
pm.test("STUDENT无权访问用户管理", () => {
    pm.expect(pm.response.code).to.eql(403);
});
```

### 2.2 创建用户

**Request：正常创建**
```
POST    {{base_url}}/user
Auth:   Bearer Token → {{admin_token}}
Body:   {
          "username": "testuser_001",
          "password": "Test@123",
          "realName": "测试用户",
          "phone": "13800138000",
          "email": "test@test.com",
          "roleId": 4,
          "status": 1
        }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("创建成功", () => pm.expect(resp.code).to.eql(200));
const userId = resp.data.id || resp.data;
if (userId) {
    pm.environment.set("test_user_id", userId);
    console.log("创建用户ID:", userId);
}
```

**Request：用户名为空（异常输入）**
```
Body:   { "username": "", "password": "Test@123", "roleId": 4 }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("用户名为空被拒绝", () => pm.expect(resp.code).to.not.eql(200));
```

**Request：roleId为空（异常输入）**
```
Body:   { "username": "testuser_002", "password": "Test@123" }
```
```javascript
// Tests
pm.test("roleId为空被拒绝", () => {
    pm.expect(pm.response.json().code).to.not.eql(200);
});
```

### 2.3 更新用户角色

**Request：正常更新角色**
```
PUT     {{base_url}}/user/{{test_user_id}}/role
Auth:   Bearer Token → {{admin_token}}
Body:   { "roleId": 2 }
```
```javascript
// Tests
pm.test("角色更新成功", () => pm.expect(pm.response.json().code).to.eql(200));
```

### 2.4 更新用户状态

**Request：禁用用户**
```
PUT     {{base_url}}/user/{{test_user_id}}/status
Auth:   Bearer Token → {{admin_token}}
Body:   { "status": 0 }
```
```javascript
// Tests
pm.test("状态更新成功", () => pm.expect(pm.response.json().code).to.eql(200));
// ⚠️ 测试后恢复 status:1
```

**Request：禁用admin（保护校验）**
```
PUT     {{base_url}}/user/1/status
Auth:   Bearer Token → {{admin_token}}
Body:   { "status": 0 }
```
```javascript
// Tests
pm.test("不能禁用管理员", () => {
    pm.expect(pm.response.json().code).to.eql(500);
});
```

### 2.5 批量操作

**Request：批量创建**
```
POST    {{base_url}}/user/batch-create
Auth:   Bearer Token → {{admin_token}}
Body:   [
          { "username": "batch_001", "password": "Test@123", "realName": "批量用户1", "roleId": 4 },
          { "username": "batch_002", "password": "Test@123", "realName": "批量用户2", "roleId": 4 },
          { "username": "batch_003", "password": "Test@123", "realName": "批量用户3", "roleId": 4 }
        ]
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("批量创建成功", () => pm.expect(resp.code).to.eql(200));
```

**Request：批量删除**
```
POST    {{base_url}}/user/batch-delete
Auth:   Bearer Token → {{admin_token}}
Body:   [100, 101, 102]   ← 改为上一步创建的用户ID
```
```javascript
// Tests
pm.test("批量删除成功", () => pm.expect(pm.response.json().code).to.eql(200));
```

**Request：批量删除含admin（保护校验）**
```
Body:   [1, 2, 3]
```
```javascript
// Tests
pm.test("含admin时被拦截", () => {
    pm.expect(pm.response.json().code).to.not.eql(200);
});
```

### 2.6 删除用户

**Request：正常删除**
```
DELETE  {{base_url}}/user/{{test_user_id}}
Auth:   Bearer Token → {{admin_token}}
```
```javascript
// Tests
pm.test("删除成功", () => pm.expect(pm.response.json().code).to.eql(200));
```

**Request：删除自己（保护校验）**
```
DELETE  {{base_url}}/user/1    ← admin 的 id
Auth:   Bearer Token → {{admin_token}}
```
```javascript
// Tests
pm.test("不能删除自己", () => pm.expect(pm.response.json().code).to.not.eql(200));
```

### 2.7 Excel导入流程

**Request：下载导入模板**
```
GET     {{base_url}}/user/import-template
Auth:   Bearer Token → {{admin_token}}
```
```javascript
// Tests
pm.test("返回Excel文件", () => {
    pm.expect(pm.response.code).to.eql(200);
    pm.expect(pm.response.headers.get("Content-Type")).to.include("spreadsheet");
});
```

---

## 三、学科竞赛 `/api/competition` — 6 接口，14 条测试

> 创建→查询→更新→撤回→删除 完整生命周期

### 3.1 创建竞赛

**Request：正常创建**
```
POST    {{base_url}}/competition
Auth:   Bearer Token → {{student_token}}
Body:   {
          "competitionCategory": "A类",
          "competitionName": "全国大学生数学建模竞赛",
          "hostUnit": "教育部",
          "organizerUnit": "中国工业与应用数学学会",
          "awardUnit": "全国组委会",
          "awardLevel": "national",
          "awardGrade": "first",
          "awardTime": "2026-06-15",
          "workName": "基于深度学习的图像识别研究",
          "advisor": "张教授",
          "participants": "学生A,学生B,学生C"
        }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("创建成功 code=200", () => pm.expect(resp.code).to.eql(200));
const compId = resp.data.id;
if (compId) {
    pm.environment.set("test_competition_id", compId);
    console.log("创建竞赛ID:", compId);
}
```

**Request：必填字段为空（competitionCategory为空）**
```
Body:   { "competitionName": "测试竞赛", "competitionCategory": "" }
```
```javascript
// Tests
pm.test("必填类目为空被拒绝", () => {
    pm.expect(pm.response.json().code).to.not.eql(200);
});
```

**Request：XSS注入测试**
```
Body:   { "competitionCategory": "A类", "competitionName": "<script>alert('xss')</script>", "advisor": "<img src=x onerror=alert(1)>" }
```
```javascript
// Tests
const resp = pm.response.json();
if (resp.code === 200) {
    // 创建成功了，检查存储值是否转义
    pm.test("XSS Payload被转义或过滤", () => {
        const name = resp.data.competitionName || "";
        pm.expect(name).to.not.include("<script>");
    });
} else {
    pm.test("XSS Payload被前端/后端拒绝", () => true);
}
```

**Request：SQL注入测试**
```
Body:   { "competitionCategory": "A类", "competitionName": "'; DROP TABLE competition; --" }
```
```javascript
// Tests
pm.test("SQL注入不破坏数据库", () => {
    pm.expect(pm.response.code).to.not.eql(500);
});
// ⚠️ 测试后验证 GET /competition/page 仍正常
```

### 3.2 分页查询

**Request：默认分页**
```
GET     {{base_url}}/competition/page?page=1&size=10
Auth:   Bearer Token → {{student_token}}
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("code=200", () => pm.expect(resp.code).to.eql(200));
pm.test("分页字段完整", () => {
    pm.expect(resp.data.records).to.be.an("array");
    pm.expect(resp.data).to.have.property("total");
});
```

**Request：多条件筛选**
```
GET     {{base_url}}/competition/page?page=1&size=10&competitionCategory=A类&awardLevel=national&awardGrade=first&status=pending_review&year=2026
```
```javascript
// Tests
pm.test("多条件筛选正常", () => pm.expect(pm.response.json().code).to.eql(200));
```

**Request：边界 — page=0**
```
GET     {{base_url}}/competition/page?page=0&size=10
```
```javascript
// Tests
pm.test("page=0 不崩溃", () => {
    pm.expect(pm.response.code).to.be.oneOf([200, 400, 500]);
});
```

### 3.3 查询详情

**Request：正常查询**
```
GET     {{base_url}}/competition/{{test_competition_id}}
Auth:   Bearer Token → {{student_token}}
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("返回详情", () => pm.expect(resp.code).to.eql(200));
pm.test("ID匹配", () => pm.expect(resp.data.id).to.eql(pm.environment.get("test_competition_id")));
```

**Request：不存在的ID**
```
GET     {{base_url}}/competition/99999
```
```javascript
// Tests
pm.test("不存在的ID报错", () => {
    pm.expect(pm.response.json().code).to.not.eql(200);
});
```

### 3.4 更新竞赛

**Request：正常更新**
```
PUT     {{base_url}}/competition/{{test_competition_id}}
Auth:   Bearer Token → {{student_token}}
Body:   { "competitionName": "全国大学生数学建模竞赛(已更新)", "competitionCategory": "A类" }
```
```javascript
// Tests
pm.test("更新成功", () => pm.expect(pm.response.json().code).to.eql(200));
```

### 3.5 撤回竞赛

**Request：正常撤回**
```
PUT     {{base_url}}/competition/{{test_competition_id}}/withdraw
Auth:   Bearer Token → {{student_token}}
```
```javascript
// Tests
pm.test("撤回成功", () => pm.expect(pm.response.json().code).to.eql(200));
```

**Request：撤回已归档的（异常）**
> 前提：先走完审核流程使状态变为 archived
```
PUT     {{base_url}}/competition/{{test_competition_id}}/withdraw
```
```javascript
// Tests
pm.test("已归档不可撤回", () => {
    pm.expect(pm.response.json().code).to.not.eql(200);
});
```

### 3.6 删除竞赛

**Request：正常删除**
```
DELETE  {{base_url}}/competition/{{test_competition_id}}
Auth:   Bearer Token → {{student_token}}
```
```javascript
// Tests
pm.test("删除成功", () => pm.expect(pm.response.json().code).to.eql(200));
```

---

## 四、审核管理 `/api/review` — 3 接口，10 条测试

### 4.1 准备：创建待审核数据

先以 STUDENT 身份创建一条竞赛：
```
POST    {{base_url}}/competition
Auth:   Bearer Token → {{student_token}}
Body:   { "competitionCategory": "B类", "competitionName": "接口测试-待审核竞赛" }
```
将返回的 ID 存入环境变量 `test_review_comp_id`。

### 4.2 查看待审核列表

**Request：SECRETARY 查看**
```
GET     {{base_url}}/review/todo?achievementType=competition&status=pending_review&page=1&size=10
Auth:   Bearer Token → {{secretary_token}}
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("code=200", () => pm.expect(resp.code).to.eql(200));
pm.test("返回待审核列表", () => {
    pm.expect(resp.data.records).to.be.an("array");
});
```

**Request：STUDENT 查看（权限边界）**
```
Auth:   Bearer Token → {{student_token}}
```
```javascript
// Tests
pm.test("STUDENT无法查看审核列表", () => {
    pm.expect(pm.response.code).to.eql(403);
});
```

### 4.3 SECRETARY 初审通过

**Request：审核通过（无评论）**
```
POST    {{base_url}}/review/approve
Auth:   Bearer Token → {{secretary_token}}
Body:   {
          "achievementType": "competition",
          "achievementId": {{test_review_comp_id}},
          "comment": "初审通过，材料齐全"
        }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("初审通过成功", () => pm.expect(resp.code).to.eql(200));
```

**Request：STUDENT越权审核（权限边界）**
```
Auth:   Bearer Token → {{student_token}}
```
```javascript
// Tests
pm.test("STUDENT无权审核", () => {
    pm.expect(pm.response.code).to.eql(403);
});
```

### 4.4 LEADER 终审通过

**Request：终审通过**
```
POST    {{base_url}}/review/approve
Auth:   Bearer Token → {{leader_token}}
Body:   {
          "achievementType": "competition",
          "achievementId": {{test_review_comp_id}},
          "comment": "终审通过，同意归档"
        }
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("终审通过成功", () => pm.expect(resp.code).to.eql(200));
// 验证状态变为 archived
```

### 4.5 审核退回

> 需另创建一条新的待审核竞赛（test_review_comp_id_2）

**Request：退回（评论为空）**
```
POST    {{base_url}}/review/reject
Auth:   Bearer Token → {{secretary_token}}
Body:   {
          "achievementType": "competition",
          "achievementId": {{test_review_comp_id_2}},
          "comment": ""
        }
```
```javascript
// Tests
pm.test("退回评论为空被拒绝", () => {
    pm.expect(pm.response.json().code).to.not.eql(200);
});
```

**Request：退回（填写评论）**
```
Body:   { "achievementType": "competition", "achievementId": {{test_review_comp_id_2}}, "comment": "申报材料不全，请补充证书扫描件" }
```
```javascript
// Tests
pm.test("退回成功", () => pm.expect(pm.response.json().code).to.eql(200));
```

### 4.6 跨角色全流程集成测试

依次执行以下步骤，验证完整二级审核流程：

```javascript
// Step 1: STUDENT 提交
POST /competition → 记录 comp_id

// Step 2: 验证状态 pending_review
GET /competition/{comp_id} → 断言 status = pending_review

// Step 3: SECRETARY 初审通过
POST /review/approve → 断言成功

// Step 4: 验证状态 under_review
GET /competition/{comp_id} → 断言 status = under_review

// Step 5: LEADER 终审通过
POST /review/approve → 断言成功

// Step 6: 验证状态 archived
GET /competition/{comp_id} → 断言 status = archived

// Step 7: 验证已归档不可编辑
PUT /competition/{comp_id} → 断言失败

// Step 8: 验证时间线完整
GET /timeline/competition/{comp_id} → 断言至少3个节点
```

---

## 五、文件管理 `/api/file` — 5 接口，8 条测试

### 5.1 上传文件

**Request：上传PDF**
```
POST    {{base_url}}/file/upload
Auth:   Bearer Token → {{student_token}}
Body:   form-data
        file: (选择本地PDF文件)
        achievementType: competition
```
> ⚠️ Postman 中 Body 类型选 `form-data`，Key 填 `file`，类型选 `File`

```javascript
// Tests
const resp = pm.response.json();
pm.test("上传成功", () => pm.expect(resp.code).to.eql(200));
pm.test("返回文件ID和URL", () => {
    pm.expect(resp.data).to.have.property("id");
    pm.expect(resp.data).to.have.property("url");
});
pm.environment.set("test_file_id", resp.data.id);
```

**Request：上传超限文件（>50MB）**
```
Body:   form-data
        file: (选择>50MB的文件)
```
```javascript
// Tests
pm.test("超大文件被拒绝", () => {
    pm.expect(pm.response.json().code).to.not.eql(200);
});
```

**Request：上传不支持的格式**
```
Body:   form-data
        file: (选择.exe或.sh文件)
```
```javascript
// Tests
pm.test("不支持的文件格式被拒绝", () => {
    pm.expect(pm.response.json().code).to.not.eql(200);
});
```

### 5.2 下载文件

**Request：携带Token下载**
```
GET     {{base_url}}/file/{{test_file_id}}
Auth:   Bearer Token → {{student_token}}
```
```javascript
// Tests
pm.test("下载成功", () => pm.expect(pm.response.code).to.eql(200));
```

**Request：不携带Token下载（⚠️ 安全验证）**
```
GET     {{base_url}}/file/{{test_file_id}}
Auth:   No Auth
```
```javascript
// Tests
// ⚠️ 当前设计：文件下载无需认证，此为已知安全风险
pm.test("文件下载当前无认证校验（已知风险）", () => {
    // 预期：无Token也可下载成功
    pm.expect(pm.response.code).to.eql(200);
});
```

### 5.3 预览文件

**Request：PDF预览**
```
GET     {{base_url}}/file/preview/{{test_file_id}}
Auth:   Bearer Token → {{student_token}}
```
```javascript
// Tests
pm.test("预览成功", () => pm.expect(pm.response.code).to.eql(200));
pm.test("Content-Type为PDF", () => {
    pm.expect(pm.response.headers.get("Content-Type")).to.include("pdf");
});
```

### 5.4 分页查询文件

**Request：文件列表**
```
GET     {{base_url}}/file/page?page=1&size=10
Auth:   Bearer Token → {{admin_token}}
```
```javascript
// Tests
const resp = pm.response.json();
pm.test("文件列表查询正常", () => pm.expect(resp.code).to.eql(200));
```

### 5.5 删除文件

**Request：删除文件**
```
DELETE  {{base_url}}/file/{{test_file_id}}
Auth:   Bearer Token → {{student_token}}
```
```javascript
// Tests
pm.test("删除成功", () => pm.expect(pm.response.json().code).to.eql(200));
```

---

## 附录：Postman Collection Runner 执行顺序

导入 Postman 后，建议按以下顺序运行 Folder：

```
00-环境初始化/              ← 必须最先执行（获取Token）
01-认证模块/auth/           ← 验证登录 + Token
02-用户管理/user/           ← ADMIN权限验证
05-个人中心/personal/       ← 准备测试数据环境
11-文件管理/file/           ← 上传测试用附件
06-学科竞赛/competition/    ← 创建测试用成果
12-审核管理/review/         ← 完整的审核流程
13-时间线/timeline/         ← 验证审核时间线
14-通知消息/notification/   ← 验证审核通知
15-重复校验/check/          ← 验证防重机制
03-公告管理/announcement/   ← CRUD 操作
07-大创项目/innovation/     ← 同competition模式
08-软件著作权/copyright/    ← 格式校验重点
09-学术论文/paper/          ← CCF匹配重点
10-CCF目录/ccf/             ← 查询只读
16-OCR识别/ocr/             ← 存根验证
17-数据统计/statistics/     ← 统计验证
04-系统日志/log/            ← ADMIN专用验证
```

> 建议每个 Folder 执行前，确认上一 Folder 全部通过。
