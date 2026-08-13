# 科研竞赛管理系统 API 接口文档

> **版本：** v2.0 | **更新时间：** 2026-08-12 | **Base URL：** `http://localhost:8080/api`
> **接口总数：** 17 个 Controller，80+ 个 API 端点
> **配套文件：** [Postman接口测试实操指南.md](Postman接口测试实操指南.md) — 环境搭建、断言脚本、执行流程

---

## Postman 测试快速配置

> 详细操作步骤和脚本模板见 [Postman接口测试实操指南.md](Postman接口测试实操指南.md)。

### Postman 环境变量

在 Postman Environment 中配置以下变量后即可开始测试：

| 变量名 | 值 | 说明 |
|--------|-----|------|
| `base_url` | `http://localhost:8080/api` | 所有接口的根路径 |
| `admin_token` | （Pre-request Script 自动获取） | ADMIN 的 JWT |
| `student_token` | （自动获取） | STUDENT 的 JWT |
| `secretary_token` | （自动获取） | SECRETARY 的 JWT |
| `leader_token` | （自动获取） | LEADER 的 JWT |

### 请求通用配置

```
Authorization → Type: Bearer Token → Token: {{admin_token}}
Headers → Content-Type: application/json
```

### Collection Folder 结构

```
竞赛科研管理系统/
├── 00-环境初始化/          ← 4个登录请求获取各角色Token
├── 01-认证模块/auth/       ← 本页 "1.认证模块" 的3个接口
├── 02-用户管理/user/       ← 本页 "2.用户管理" 的15个接口
├── ...（与下方17个模块一一对应）
└── 17-数据统计/statistics/
```

> 每个 Folder 下按接口创建 Request，命名规则：`序号-接口名`，如 `01-登录 POST login`、`02-获取当前用户 GET current-user`。

### 接口文档中的 Postman 标注说明

| 标注 | 含义 |
|------|------|
| 🔑 需认证 | 需在 Authorization 标签携带 `Bearer Token` |
| 🔓 无需认证 | 公开接口，不携带 Token |
| 👤 ADMIN | 仅 ADMIN 角色可访问 |
| 👥 ALL | 所有已认证用户可访问 |
| 📎 文件上传 | Content-Type 为 `multipart/form-data` |
| 📥 文件下载 | 响应为二进制流，非 JSON |

---

## 通用说明

### 认证方式

所有接口（除 `/api/auth/login`、`/uploads/**`、`GET /api/file/**`）均需携带 JWT Token：

```
Authorization: Bearer <token>
```

Token 有效期 **24 小时**，由登录接口签发。

### 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": <具体数据>
}
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 401 | 未登录 / 用户名或密码错误 |
| 403 | 无权限（HTTP 状态码 403，空 body） |
| 500 | 服务器内部错误 |

### 分页响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "pageSize": 20,
    "current": 1,
    "records": []
  }
}
```

### 角色权限

| 角色 | 标识 | 权限范围 |
|------|------|----------|
| 系统管理员 | ADMIN | 全部功能 |
| 科研秘书 | SECRETARY | 审核管理 |
| 学院领导 | LEADER | 审核管理 |
| 学生/教师 | STUDENT | 提交和查看自己的成果 |

### 审核状态枚举

| 值 | 含义 |
|----|------|
| `pending_review` | 待审核 |
| `under_review` | 审核中 |
| `returned` | 已退回 |
| `archived` | 已归档 |

---

## 1. 认证模块 — `/api/auth`

> 🔓 无需认证 | 共 3 个接口

### 1.1 登录

```
POST /api/auth/login
```

**请求体 (JSON)：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码（明文） |

**请求示例：**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**成功响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "role": "ADMIN",
      "roleId": 1,
      "phone": "",
      "email": ""
    }
  }
}
```

**失败响应：**
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```
> 所有失败（用户不存在、密码错误、账号已禁用）均返回相同提示，HTTP 状态码为 200。

---

### 1.2 获取当前用户信息

```
GET /api/auth/current-user
```

**响应：** 同登录接口的 `userInfo` 对象。

---

### 1.3 修改密码

```
PUT /api/auth/password
```

**请求体 (JSON)：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| oldPassword | String | 是 | 原密码 |
| newPassword | String | 是 | 新密码 |

**失败响应：**
```json
{
  "code": 500,
  "message": "原密码错误",
  "data": null
}
```

---

## 2. 用户管理 — `/api/user`

> 🔑 需认证 | 👤 ADMIN（除 `GET /{id}` 外全部需要 ADMIN 角色）| 共 15 个接口

### 2.1 分页查询用户列表

```
GET /api/user/page
```

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 1 | 页码 |
| size | int | 10 | 每页条数 |
| keyword | String | 无 | 按用户名或姓名模糊搜索 |

---

### 2.2 查询用户详情

```
GET /api/user/{id}
```

---

### 2.3 创建用户

```
POST /api/user
```

**请求体 (JSON)：**

| 字段 | 类型 | 必填 | 校验 |
|------|------|------|------|
| username | String | 是 | `@NotBlank` |
| password | String | 否 | — |
| realName | String | 否 | — |
| phone | String | 否 | — |
| email | String | 否 | — |
| roleId | Long | 是 | `@NotNull` |
| status | Integer | 否 | 默认 1（启用） |

---

### 2.4 更新用户

```
PUT /api/user/{id}
```

**请求体：** 同创建用户

---

### 2.5 删除用户

```
DELETE /api/user/{id}
```

> 不能删除管理员账号（id=1）和自己。

---

### 2.6 更新用户角色

```
PUT /api/user/{id}/role
```

**请求体：**
```json
{ "roleId": 2 }
```

---

### 2.7 更新用户状态

```
PUT /api/user/{id}/status
```

**请求体：**
```json
{ "status": 0 }
```
> 0=禁用，1=启用。不能修改管理员（id=1）的状态。

---

### 2.8 获取所有角色

```
GET /api/user/roles
```

**响应：** `SysRole[]` — `[{id, roleName, roleDesc, createTime}]`

---

### 2.9 批量删除

```
POST /api/user/batch-delete
```

**请求体：**
```json
[1, 2, 3]
```

---

### 2.10 批量创建

```
POST /api/user/batch-create
```

**请求体：** `UserDTO[]`

---

### 2.11 批量更新角色

```
PUT /api/user/batch-role
```

**请求体：**
```json
{ "ids": [2, 3], "roleId": 2 }
```

---

### 2.12 批量更新状态

```
PUT /api/user/batch-status
```

**请求体：**
```json
{ "ids": [2, 3], "status": 0 }
```

---

### 2.13 导入 Excel 用户（预览）

```
POST /api/user/import-excel
Content-Type: multipart/form-data
```

| 参数 | 类型 | 说明 |
|------|------|------|
| file | MultipartFile | .xlsx / .xls 文件 |

**Excel 列映射：** username, password, realName, phone, email, roleName

**响应：**
```json
{
  "total": 10,
  "validCount": 8,
  "errorCount": 2,
  "validUsers": [...],
  "errorRows": [{ "row": 3, "username": "xxx", "errors": ["角色名称无法识别"] }],
  "filename": "用户列表.xlsx"
}
```

---

### 2.14 确认导入

```
POST /api/user/import-confirm
```

**请求体：** `UserDTO[]`（由预览接口返回的 validUsers）

**响应：**
```json
{ "importedCount": 8 }
```

---

### 2.15 下载导入模板

```
GET /api/user/import-template
```

**响应：** Excel 文件二进制流

---

## 3. 公告管理 — `/api/announcement`

> 🔑 需认证 | 👤 查询：ALL，增删改：ADMIN | 共 6 个接口

### 3.1 分页查询公告

```
GET /api/announcement/page
```

| 参数 | 默认值 |
|------|--------|
| page | 1 |
| size | 10 |

---

### 3.2 查询公告详情

```
GET /api/announcement/{id}
```

---

### 3.3 获取最新公告

```
GET /api/announcement/latest
```

---

### 3.4 创建公告

```
POST /api/announcement
```
> 需要 **ADMIN**

| 字段 | 类型 | 说明 |
|------|------|------|
| title | String | 标题 |
| content | String | 内容 |
| publisher | String | 发布者 |
| publishTime | LocalDate | 发布日期 |
| isTop | Integer | 是否置顶（0/1） |

---

### 3.5 更新公告

```
PUT /api/announcement/{id}
```
> 需要 **ADMIN**，请求体同创建

---

### 3.6 删除公告

```
DELETE /api/announcement/{id}
```
> 需要 **ADMIN**

---

## 4. 系统日志 — `/api/log`

> 🔑 需认证 | 👤 ADMIN 专用 | 共 1 个接口

### 4.1 分页查询日志

```
GET /api/log/page
```

| 参数 | 类型 | 说明 |
|------|------|------|
| page | int | 默认 1 |
| size | int | 默认 20 |
| action | String | 按操作类型过滤 |
| keyword | String | 按用户名或操作内容模糊搜索 |

**响应字段：** `id, username, action, operation, ip, createTime`

---

## 5. 个人中心 — `/api/personal`

> 🔑 需认证 | 👥 ALL（仅返回当前用户自己的数据）| 共 4 个接口

### 5.1 成果概览统计

```
GET /api/personal/overview
```

**响应结构：**
```json
{
  "totalCompetitions": 5,
  "totalInnovations": 2,
  "totalCopyrights": 1,
  "totalPapers": 3,
  "grandTotal": 11,
  "archivedCount": 8,
  "underReviewCount": 1,
  "pendingCount": 2,
  "returnedCount": 0,
  "competitionByCategory": [{"name": "A类", "value": 3}],
  "competitionByGrade": [{"name": "一等奖", "value": 2}],
  "competitionByLevel": [{"name": "国家级", "value": 4}],
  "monthlyCompetition": [0,1,2,0,0,0,0,0,0,0,0,0],
  "monthlyPaper": [0,0,1,0,0,0,0,0,0,0,0,0],
  "monthlyCopyright": [0,0,0,0,0,0,0,0,0,0,0,0],
  "achievementDistribution": [
    {"name": "学科竞赛", "value": 5},
    {"name": "大创项目", "value": 2},
    {"name": "软件著作权", "value": 1},
    {"name": "学术论文", "value": 3}
  ]
}
```

---

### 5.2 成果列表（多类型聚合）

```
GET /api/personal/achievements
```

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| type | String | 无 | competition / innovation / copyright / paper |
| status | String | 无 | pending_review / under_review / returned / archived |
| year | int | 无 | 年份过滤 |
| page | int | 1 | 页码 |
| size | int | 20 | 每页条数 |

---

### 5.3 成果置顶/取消置顶

```
PUT /api/personal/pin/{type}/{id}
```

| 路径参数 | 说明 |
|----------|------|
| type | competition / innovation / copyright / paper |
| id | 成果ID |

---

### 5.4 导出个人成果报告

```
GET /api/personal/export
```

**响应：** UTF-8 BOM CSV 文件

---

## 6. 学科竞赛 — `/api/competition`

> 🔑 需认证 | 👥 ALL（CRUD 所有用户均可操作，⚠️ 无所有权校验）| 共 6 个接口

### 6.1 分页查询

```
GET /api/competition/page
```

| 参数 | 类型 | 说明 |
|------|------|------|
| page | int | 页码 |
| size | int | 每页条数 |
| competitionCategory | String | A类/B类/C类 |
| awardLevel | String | national/provincial/municipal/school/college |
| awardGrade | String | first/second/third |
| status | String | 审核状态 |
| keyword | String | 模糊搜索 |
| year | int | 年份 |

### 6.2 查询详情

```
GET /api/competition/{id}
```

### 6.3 创建

```
POST /api/competition
```

**请求体 (JSON)：**

| 字段 | 类型 | 必填 |
|------|------|------|
| competitionCategory | String | 是 |
| competitionName | String | 是 |
| hostUnit | String | 否 |
| organizerUnit | String | 否 |
| awardUnit | String | 否 |
| awardLevel | String | 否 |
| awardGrade | String | 否 |
| awardTime | LocalDate | 否 |
| workName | String | 否 |
| advisor | String | 否 |
| participants | String | 否 |
| fileIds | Long[] | 否 |

### 6.4 更新

```
PUT /api/competition/{id}
```
> 请求体同创建

### 6.5 删除

```
DELETE /api/competition/{id}
```

### 6.6 撤回

```
PUT /api/competition/{id}/withdraw
```

---

## 7. 大创项目 — `/api/innovation`

> 🔑 需认证 | 👥 ALL（CRUD 所有用户均可操作，⚠️ 无所有权校验）| 共 6 个接口
> ⚠️ `page`/`size` 为原始 int 类型无默认值，前端漏传时绑定为 0

### 7.1 分页查询

```
GET /api/innovation/page
```

| 参数 | 类型 | 说明 |
|------|------|------|
| page | int | 页码 |
| size | int | 每页条数 |
| projectLevel | String | 国家级/省级/校级 |
| projectType | String | 创新训练/创业训练/创业实践 |
| status | String | 审核状态 |
| keyword | String | 模糊搜索 |
| year | int | 年份 |

### 7.2 查询详情

```
GET /api/innovation/{id}
```

### 7.3 创建

```
POST /api/innovation
```

| 字段 | 类型 | 必填 |
|------|------|------|
| projectName | String | 是 |
| projectLevel | String | 否 |
| projectType | String | 否 |
| advisor | String | 否 |
| members | String | 否 |
| startTime | LocalDate | 否 |
| proposalFileId | Long | 否 |
| finalMaterialFileId | Long | 否 |
| certificateFileId | Long | 否 |
| fileIds | Long[] | 否 |

### 7.4 更新

```
PUT /api/innovation/{id}
```

### 7.5 删除

```
DELETE /api/innovation/{id}
```

### 7.6 撤回

```
PUT /api/innovation/{id}/withdraw
```

---

## 8. 软件著作权 — `/api/copyright`

> 🔑 需认证 | 👥 ALL（CRUD 所有用户均可操作，⚠️ 无所有权校验）| 共 6 个接口

### 8.1 分页查询

```
GET /api/copyright/page
```

| 参数 | 类型 | 说明 |
|------|------|------|
| page | int | 默认 1 |
| size | int | 默认 10 |
| status | String | 审核状态 |
| keyword | String | 模糊搜索 |
| year | int | 年份 |

### 8.2 查询详情

```
GET /api/copyright/{id}
```

### 8.3 创建

```
POST /api/copyright
```

| 字段 | 类型 | 必填 |
|------|------|------|
| softwareName | String | 是 |
| organization | String | 否 |
| copyrightOwner | String | 否 |
| registrationNumber | String | 否（格式: `^\d{4}SR\d{7,8}$`） |
| registrationDate | LocalDate | 否 |
| certificateFileId | Long | 否 |
| fileIds | Long[] | 否 |

### 8.4 更新

```
PUT /api/copyright/{id}
```

### 8.5 删除

```
DELETE /api/copyright/{id}
```

### 8.6 撤回

```
PUT /api/copyright/{id}/withdraw
```

---

## 9. 学术论文 — `/api/paper`

> 🔑 需认证 | 👥 ALL（CRUD 所有用户均可操作，⚠️ 无所有权校验）| 共 6 个接口

### 9.1 分页查询

```
GET /api/paper/page
```

| 参数 | 类型 | 说明 |
|------|------|------|
| page | int | 默认 1 |
| size | int | 默认 10 |
| journalLevel | String | 期刊级别 |
| status | String | 审核状态 |
| keyword | String | 模糊搜索 |
| year | int | 年份 |

### 9.2 查询详情

```
GET /api/paper/{id}
```

### 9.3 创建

```
POST /api/paper
```

| 字段 | 类型 | 必填 |
|------|------|------|
| title | String | 是 |
| submissionDate | LocalDate | 否 |
| acceptanceDate | LocalDate | 否 |
| journalName | String | 否 |
| keywords | String | 否 |
| journalLevel | String | 否 |
| authors | String | 否 |
| draftFileId | Long | 否 |
| finalFileId | Long | 否 |
| reviewCommentFileId | Long | 否 |
| fileIds | Long[] | 否 |

### 9.4 更新

```
PUT /api/paper/{id}
```

### 9.5 删除

```
DELETE /api/paper/{id}
```

### 9.6 撤回

```
PUT /api/paper/{id}/withdraw
```

---

## 10. CCF 目录 — `/api/ccf`

> 🔑 需认证 | 👥 ALL（只读查询）| 共 4 个接口

### 10.1 分页查询 CCF 目录

```
GET /api/ccf/page
```

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 1 | 页码 |
| size | int | 20 | 每页条数 |
| venueType | String | 无 | journal / conference |
| area | String | 无 | 研究方向（精确匹配） |
| level | String | 无 | A / B / C |
| keyword | String | 无 | 简称精确匹配 **或** 全称模糊搜索 |

**响应字段：** `id, venueType, area, level, abbreviation, fullName, publisher, url`

---

### 10.2 获取研究方向列表

```
GET /api/ccf/areas
```

**响应：** `String[]` — 去重排序后的研究方向列表

---

### 10.3 获取等级列表

```
GET /api/ccf/levels
```

**响应：** `["A", "B", "C"]`

---

### 10.4 CCF 期刊/会议匹配

```
GET /api/ccf/match
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 期刊/会议名称（简称或全称） |

**匹配策略（三层）：**
1. 简称精确匹配（`abbreviation = name`）
2. 全称模糊匹配（`fullName LIKE %name%`，取等级最高的）
3. 反向简称模糊匹配（`abbreviation LIKE %name%`）

**成功响应：**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "venueType": "journal",
    "area": "计算机体系结构",
    "level": "A",
    "abbreviation": "TOCS",
    "fullName": "ACM Transactions on Computer Systems",
    "publisher": "ACM"
  }
}
```

**未匹配：**
```json
{
  "code": 404,
  "message": "未在CCF目录中找到匹配",
  "data": null
}
```

---

## 11. 文件管理 — `/api/file`

> 🔓 `GET /file/{id}` 无需认证（⚠️ 安全风险）| 🔑 其余需认证 | 👥 ALL | 共 5 个接口

### 11.1 分页查询文件

```
GET /api/file/page
```

| 参数 | 默认值 | 说明 |
|------|--------|------|
| page | 1 | 页码 |
| size | 10 | 每页条数 |
| fileType | 无 | 按文件类型过滤 |
| keyword | 无 | 按原始文件名模糊搜索 |

---

### 11.2 上传文件

```
POST /api/file/upload
Content-Type: multipart/form-data
```

| 参数 | 类型 | 说明 |
|------|------|------|
| file | MultipartFile | 文件 |
| achievementType | String | 关联成果类型（可选） |
| achievementId | Long | 关联成果ID（可选） |

**响应：**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "originalName": "论文.pdf",
    "url": "/api/file/1"
  }
}
```

---

### 11.3 下载文件

```
GET /api/file/{id}
```

**响应：** `application/octet-stream` 二进制流

---

### 11.4 预览文件

```
GET /api/file/preview/{id}
```

**支持格式：** PDF（`application/pdf`）、JPG/JPEG（`image/jpeg`）、PNG（`image/png`）
其余格式返回二进制流。

---

### 11.5 删除文件

```
DELETE /api/file/{id}
```

---

## 12. 审核管理 — `/api/review`

> 🔑 需认证 | 👤 查询：ALL，审核操作：ADMIN / SECRETARY / LEADER | 共 3 个接口

### 12.1 待审核列表

```
GET /api/review/todo
```

| 参数 | 类型 | 说明 |
|------|------|------|
| achievementType | String | competition / innovation / copyright / paper |
| status | String | 审核状态过滤 |
| page | int | 默认 1 |
| size | int | 默认 10 |

---

### 12.2 审核通过

```
POST /api/review/approve
```

**请求体 (JSON)：**

| 字段 | 类型 | 说明 |
|------|------|------|
| achievementType | String | 成果类型 |
| achievementId | Long | 成果ID |
| comment | String | 审核意见 |

---

### 12.3 审核退回

```
POST /api/review/reject
```

**请求体：** 同审核通过

---

## 13. 时间线 — `/api/timeline`

> 🔑 需认证 | 👥 ALL | 共 1 个接口

### 13.1 获取成果时间线

```
GET /api/timeline/{type}/{id}
```

| 路径参数 | 说明 |
|----------|------|
| type | competition / innovation / copyright / paper |
| id | 成果ID |

**响应：** `AchievementTimeline[]`

---

## 14. 通知消息 — `/api/notification`

> 🔑 需认证 | 👥 ALL（仅操作自己的通知）| 共 6 个接口

### 14.1 分页查询通知

```
GET /api/notification/page
```

| 参数 | 默认值 |
|------|--------|
| page | 1 |
| size | 10 |

---

### 14.2 未读通知数

```
GET /api/notification/unread-count
```

**响应：** `Long`

---

### 14.3 标记已读

```
PUT /api/notification/{id}/read
```

---

### 14.4 全部标记已读

```
PUT /api/notification/read-all
```

---

### 14.5 删除通知

```
DELETE /api/notification/{id}
```

---

### 14.6 删除全部通知

```
DELETE /api/notification/all
```

---

## 15. 重复检测与校验 — `/api/check`

> 🔑 需认证 | 👥 ALL | 共 2 个接口

### 15.1 重复检测

```
POST /api/check/duplicate
```

**请求体 (JSON)：**

```json
{
  "type": "paper",
  "data": { "title": "xxx", "journalName": "xxx" }
}
```

**匹配规则：**

| type | 匹配逻辑 |
|------|----------|
| competition | 竞赛名称或作品名称相同 **且** 指导教师相同 |
| innovation | 项目名称相同 **且** 指导教师相同 |
| copyright | 登记号相同 |
| paper | 标题相同 **且** 期刊名称相同 |

**响应：**
```json
{
  "code": 200,
  "data": {
    "hasDuplicate": false,
    "duplicates": []
  }
}
```

---

### 15.2 字段校验

```
POST /api/check/validate
```

**请求体：** 同重复检测

**校验规则：**

| type | 规则 |
|------|------|
| competition | competitionName/category/awardLevel/awardGrade 必填 |
| innovation | projectName/projectLevel 必填 |
| copyright | softwareName 必填；registrationNumber 必填且格式 `^\d{4}SR\d{7,8}$` |
| paper | title/journalLevel 必填 |

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "valid": false,
    "errors": ["论文标题为必填项"],
    "warnings": []
  }
}
```

---

## 16. OCR 识别 — `/api/ocr`

> 🔑 需认证 | 👥 ALL | 共 1 个接口（存根，始终返回不可用）

### 16.1 OCR 状态检查

```
GET /api/ocr/status
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "available": false,
    "message": "当前环境不支持自动OCR识别，请使用证书图片参考功能手动填写表单"
  }
}
```
> 当前为存根接口，OCR 始终不可用。

---

## 17. 数据统计 — `/api/statistics`

> 🔑 需认证 | 👥 ALL（全局统计，不受角色限制）| 共 5 个接口

### 17.1 系统总览统计

```
GET /api/statistics/overview
```

**响应结构：**
```json
{
  "totalCompetitions": 0,
  "competitionStatusBreakdown": [{"name": "已归档", "value": 10}],
  "competitionByCategory": [{"name": "A类", "value": 5}],
  "competitionByLevel": [{"name": "国家级", "value": 3}],
  "competitionByGrade": [{"name": "一等奖", "value": 2}],
  "totalInnovations": 0,
  "innovationStatusBreakdown": [],
  "innovationByLevel": [],
  "totalCopyrights": 0,
  "totalPapers": 0,
  "paperByLevel": [],
  "grandTotal": 0,
  "archivedCount": 0,
  "underReviewCount": 0,
  "pendingCount": 0,
  "returnedCount": 0,
  "monthlyCompetition": [0,0,0,0,0,0,0,0,0,0,0,0],
  "monthlyPaper": [0,0,0,0,0,0,0,0,0,0,0,0],
  "monthlyCopyright": [0,0,0,0,0,0,0,0,0,0,0,0],
  "monthlyInnovation": [0,0,0,0,0,0,0,0,0,0,0,0]
}
```
> 全局统计，不受用户角色限制。

---

### 17.2 学科竞赛统计

```
GET /api/statistics/competition?year=2026
```

| 参数 | 类型 | 说明 |
|------|------|------|
| year | int | 年份过滤（可选） |

**响应：** `{categoryStats, awardLevelStats, awardGradeStats}`

---

### 17.3 大创项目统计

```
GET /api/statistics/innovation?year=2026
```

**响应：** `{levelStats, typeStats}`

---

### 17.4 软件著作权统计

```
GET /api/statistics/copyright?year=2026
```

**响应：** `{yearStats}` — 按年份分组统计

---

### 17.5 学术论文统计

```
GET /api/statistics/paper?year=2026
```

**响应：** `{journalLevelStats}`

---

## 附录 A：返回状态码汇总

| HTTP 状态码 | body.code | 场景 |
|-------------|-----------|------|
| 200 | 200 | 所有正常响应 |
| 200 | 401 | 登录失败（用户不存在/密码错误/账号禁用）/ 未登录 |
| 200 | 404 | CCF 未匹配 |
| 200 | 500 | 服务端业务异常 |
| 403 | — | 无 Token / Token 无效 / Token 过期 / 权限不足（空 body） |
| 401 | — | Token 过期（拦截器触发 → 跳转登录页） |

---

## 附录 B：接口测试注意事项

1. **⚠️ InnovationQueryDTO 的 page/size 是原始 int 类型无默认值** — 如果前端漏传，会绑定为 `0`（其他模块的 QueryDTO 默认 `page=1, size=10`）。
2. **⚠️ 成果模块（competition/innovation/copyright/paper）无所有权校验** — 任何已认证用户可修改/删除任意用户的成果，仅 `PersonalCenterController.togglePin` 做了所有权校验。
3. **返回类型不一致** — competition/innovation 的 create/update 返回完整 VO，paper/copyright 的 create 仅返回 `Long` id，update 返回 `Void`。
4. **登录接口始终返回 HTTP 200** — 错误通过 body 的 `code: 401` 区分，HTTP 状态码层面不抛 401。
5. **Token 过期/无效返回 HTTP 403（空 body）** — 不是 401，前端需据此处理跳转。
6. **登录页演示账号遗漏** — `teacher1` 在数据库中存在但未在登录页列出。
7. **无暴力破解防护** — 登录接口无限流/无锁定/无验证码。
8. **非 JSON 响应的端点** — 导入模板（xlsx）、导出报告（CSV）、文件下载/预览直接写 `HttpServletResponse` 流，不返回 `Result` JSON。
9. **`PUT /api/notification/read-all` 和 `DELETE /api/notification/all`** — 注意与 `PUT /{id}/read` 和 `DELETE /{id}` 的路径区分，不会冲突。

---

## 附录 C：测试账号

| 用户名 | 角色 | 密码 |
|--------|------|------|
| admin | ADMIN（系统管理员） | admin123 |
| student1 | STUDENT（学生/教师） | admin123 |
| teacher1 | STUDENT（学生/教师） | admin123 |
| secretary | SECRETARY（科研秘书） | admin123 |
| leader | LEADER（学院领导） | admin123 |
