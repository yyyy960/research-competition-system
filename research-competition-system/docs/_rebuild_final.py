"""三步修复 + 严格 RFC-4180 生成 merged 115 v2
Step1: 把 docs/v1 (98条 11列) 原地升级为 v2-98 (12列: 在预期结果后插入"测试结果=未执行")
Step2: 按之前已核对过的 TC-ID 精确重建 17 条补测边界用例的 12 列 v2 数据
Step3: 合并+排序+RFC4180写出，并做 115 条 x 12列 全结构校验
"""
import csv, re, os
from collections import defaultdict

DOCS = r'd:\project\research-competition-system\research-competition-system\docs'
V1_98 = os.path.join(DOCS, 'web-testcase-all-v1-20260810.csv')
V2_98_OUT = os.path.join(DOCS, 'web-testcase-all-v2-20260810.csv')   # 主用例 98条(12列)
SUPP_17_OUT = os.path.join(DOCS, 'web-testcase-boundary-supplement-v2-20260810.csv')  # 补测边界 17条
MERGED_115_OUT = os.path.join(DOCS, 'web-testcase-all-v2-merged-115-20260810.csv')
HEADER_V2 = ['用例ID','模块','页面','测试点','用例标题','预置条件','测试步骤','预期结果','测试结果','优先级','严重级别','测试类型']
PRI_ALLOW={'P0','P1','P2'}; SEV_ALLOW={'致命','严重','一般','轻微'}; RES_ALLOW={'未执行','通过','失败','阻塞'}

def write_rfc4180(path, header, data):
    """按 Excel/禅道 最兼容的 CSV RFC 4180 标准写入"""
    with open(path, 'w', encoding='utf-8-sig', newline='') as f:
        w = csv.writer(f, lineterminator='\r\n', quoting=csv.QUOTE_MINIMAL)
        w.writerow(header)
        w.writerows(data)

def verify_csv(path, expect_rows=None):
    """读取并校验结构，返回 (数据行列表, 错误列表, 统计dict)"""
    errs = []
    with open(path, 'rb') as f: bb = f.read()
    bom = bb[:3] == b'\xef\xbb\xbf'
    crlf = bb.count(b'\r\n')
    lf_only = bb.count(b'\n') - crlf
    with open(path, 'r', encoding='utf-8-sig', newline='') as f:
        rows = list(csv.reader(f))
    bad_cols = [(i+1, len(r)) for i, r in enumerate(rows) if len(r) != 12]
    data = rows[1:] if rows else []
    data = [r for r in data if any(x.strip() for x in r)]
    # 列补全
    norm = []
    for i, r in enumerate(data, start=2):
        if len(r) < 12: r = r + [''] * (12 - len(r))
        elif len(r) > 12: r = r[:12]
        norm.append(r)
    ids = [r[0].strip() for r in norm]
    dup_map = defaultdict(list)
    for i, tid in enumerate(ids, start=2): dup_map[tid].append(i)
    repeats = {k: v for k, v in dup_map.items() if len(v) > 1}
    pri = defaultdict(int); sev = defaultdict(int); res = defaultdict(int); mods = defaultdict(int)
    empties = pri_b = sev_b = res_b = 0
    for r in norm:
        for ci in range(12):
            if r[ci].strip() == '': empties += 1
        p, s, x = r[9].strip(), r[10].strip(), r[8].strip()
        if p in PRI_ALLOW: pri[p] += 1
        else: pri_b += 1
        if s in SEV_ALLOW: sev[s] += 1
        else: sev_b += 1
        if x in RES_ALLOW: res[x] += 1
        else: res_b += 1
        m = re.match(r'TC-([A-Z]+)-\d+', r[0].strip())
        if m: mods[m.group(1)] += 1
    stats = {
        'bom': bom, 'crlf': crlf, 'lf_only': lf_only,
        'rows_parsed': len(norm), 'bad_cols_count': len(bad_cols),
        'repeats_count': len(repeats),
        'priority': dict(pri), 'severity': dict(sev), 'result': dict(res), 'modules': dict(mods),
        'empties_cells': empties, 'enum_pri_bad': pri_b, 'enum_sev_bad': sev_b, 'enum_res_bad': res_b
    }
    stats['all_ok'] = (bom and lf_only == 0 and len(bad_cols) == 0 and not repeats
                       and stats['enum_pri_bad'] == 0 and stats['enum_sev_bad'] == 0
                       and stats['enum_res_bad'] == 0
                       and (expect_rows is None or len(norm) == expect_rows))
    return norm, errs, stats

# =============================================
# Step 1: 升级 v1(98,11列) -> v2(98,12列)
# =============================================
with open(V1_98, 'r', encoding='utf-8-sig', newline='') as f:
    v1_rows = list(csv.reader(f))
v1_header = v1_rows[0]
v1_data = [r for r in v1_rows[1:] if any(x.strip() for x in r)]
print(f'Step1 升级 v1: 表头列数={len(v1_header)}  数据行={len(v1_data)}')
# 列位置 v1:
# 0用例ID 1模块 2页面 3测试点 4用例标题 5预置条件 6测试步骤 7预期结果 8优先级 9严重级别 10测试类型   (11列)
# v2 目标: 0-7 相同, 8插入「测试结果=未执行」, 9优先级 10严重级别 11测试类型
v2_98_data = []
for r in v1_data:
    if len(r) < 11:
        r = r + [''] * (11 - len(r))
    v2_row = [
        r[0].strip(), r[1].strip(), r[2].strip(), r[3].strip(), r[4].strip(),
        r[5].strip(), r[6].strip(), r[7].strip(),
        '未执行',
        r[8].strip() if len(r) > 8 else '',
        r[9].strip() if len(r) > 9 else '',
        r[10].strip() if len(r) > 10 else ''
    ]
    v2_98_data.append(v2_row)

write_rfc4180(V2_98_OUT, HEADER_V2, v2_98_data)
_, _, st98 = verify_csv(V2_98_OUT, expect_rows=98)
print(f'  -> 写出 {os.path.basename(V2_98_OUT)}: {"✅ 98条12列全合规" if st98["all_ok"] else f"⚠️  异常: {st98}"}')
print(f'     优先级 {st98["priority"]}  模块 {st98["modules"]}  空字段 {st98["empties_cells"]}')

# =============================================
# Step 2: 重建17条补测边界用例（严格按已核对TC-ID列表）
#   原TC-ID清单: LOG-020/021, SUB-029..038, PAG-001..004, SYS-027 （17条）
# =============================================
SUPP_ROWS = [
# 用例ID,模块,页面,测试点,用例标题,预置条件,测试步骤,预期结果,测试结果,优先级,严重级别,测试类型
['TC-LOG-020','登录与权限模块','登录页','验证用户名50字符上限边界','用户名VARCHAR(50)恰好等于边界','数据库sys_user.username列VARCHAR(50)；后端服务正常','1.打开登录页；2.输入精确50字符长的已注册用户名（若不存在则先由admin创建）+ 正确密码；3.点击登录','登录成功返回code=200；完整保留50字符不截断；页面跳转首页；token正确','未执行','P2','一般','边界测试'],
['TC-LOG-021','登录与权限模块','登录页','验证用户名51字符超长提交','用户名超限(50+1)','数据库username列VARCHAR(50)','1.打开登录页；2.输入51字符长度字符串作用户名；3.输入正确密码；4.点击登录','前端或后端校验拦截返回code=400提示用户名长度不能超过50字符；数据库未被截断存储；不返回用户名或密码错误','未执行','P2','一般','边界测试'],
['TC-SUB-029','成果提交模块','竞赛成果创建页','验证竞赛类别A/B/C外非法枚举值提交','枚举白名单外的值','后端服务正常；已登录student1','1.进入竞赛创建页；2.通过devtools绕过前端直接传competitionCategory=D（白名单之外）；3.其他字段填合法；4.提交','后端校验拦截返回code=400提示竞赛类别仅允许A/B/C；competition_achievement表零新增','未执行','P1','一般','边界测试'],
['TC-SUB-030','成果提交模块','竞赛成果创建页','验证获奖级别枚举外值提交','获奖级别白名单外枚举','后端服务正常；已登录student1','1.竞赛成果页直接传awardLevel=country_extreme；2.其他字段正常；3.提交','后端校验拦截返回code=400提示获奖级别不合法；不入库','未执行','P1','一般','边界测试'],
['TC-SUB-031','成果提交模块','大创项目创建页','验证项目名称恰好200字符','项目名VARCHAR(200)边界等于最大值','innovation_project.project_name列VARCHAR(200)','1.大创创建页；2.项目名称精确200字符；3.其他字段合法；4.点击提交','提交成功code=200；200字符完整入库无截断；列表展示完整；不会出现DataIntegrityViolationException','未执行','P2','一般','边界测试'],
['TC-SUB-032','成果提交模块','大创项目创建页','验证项目名称201字符超长','项目名超VARCHAR(200)上限','innovation_project.project_name列VARCHAR(200)','1.大创创建页；2.项目名称201字符超长；3.其他字段合法；4.提交','后端@Size或MySQL DataIntegrityViolationException被捕获；返回code=400/500提示长度超限；不入库；不抛出未处理的500堆栈','未执行','P1','严重','边界测试'],
['TC-SUB-033','成果提交模块','竞赛成果创建页','验证竞赛名称恰好200字符等于上限','竞赛名 VARCHAR(200) 等于边界','competition_name VARCHAR(200) NOT NULL','1.竞赛成果创建页；2.竞赛名称精确200字符；3.其他字段合法；4.点击提交','提交成功code=200；200字符完整存储；列表展示完整名称','未执行','P2','一般','边界测试'],
['TC-SUB-034','成果提交模块','论文创建页','验证论文标题恰好300字符等于上限','标题VARCHAR(300)边界','academic_paper.title列VARCHAR(300)','1.论文创建页；2.标题精确300字符；3.其他字段合法；4.点击提交','提交成功code=200；300字符完整入库不截断；详情页展示完整标题','未执行','P2','一般','边界测试'],
['TC-SUB-035','成果提交模块','论文创建页','验证论文标题301字符超限','标题超VARCHAR(300)','academic_paper.title列VARCHAR(300)','1.论文创建页；2.标题301字符；3.其他字段合法；4.点击提交','返回code=400或DataIntegrityViolationException被捕获为提示；不入库；不会500堆栈泄露','未执行','P1','严重','边界测试'],
['TC-SUB-036','成果提交模块','软著创建页','验证软著登记号恰好50字符','登记号VARCHAR(50)边界','registration_number列VARCHAR(50)','1.软著创建页；2.输入50字符长度的登记号；3.其他字段合法；4.点击提交','提交成功code=200；登记号完整保留50字符不截断；软著列表展示完整','未执行','P2','一般','边界测试'],
['TC-SUB-037','成果提交模块','文件上传','验证上传文件恰好等于50MB边界','文件大小恰好等于max-file-size上限','spring.servlet.multipart.max-file-size=50MB；存在刚好50MB合法pdf文件','1.进入大创创建页；2.上传精确50MB pdf；3.点击提交','上传成功；sys_file表新增记录；文件大小为50*1024*1024字节；下载大小一致；不被413拦截','未执行','P1','严重','边界测试'],
['TC-SUB-038','成果提交模块','软著创建页','验证登记号恰好等于50字符+重复提交冲突','登记号上限边界且唯一冲突','已插入一条登记号长度恰好为50字符的软著记录','1.先提交一条登记号=50字符的软著；2.再用完全相同的50字符登记号创建提交第二条','第二次提交返回登记号已存在冲突提示；UNIQUE索引正确触发；software_copyright无重复','未执行','P1','严重','边界测试'],
['TC-PAG-001','成果提交模块','个人成果中心列表','验证分页参数page=0首页保护','page=0不合法边界','列表QueryDTO字段page=Integer(默认1/0两版本)；已登录student1','1.登录student1；2.直接在成果列表接口传page=0 size=10；3.查看返回','Mybatis-Plus自动将page=0调整为1或返回空列表不报错；不抛出异常；返回结构完整符合{code,message,data.PageResult结构}','未执行','P1','严重','边界测试'],
['TC-PAG-002','成果提交模块','个人成果中心列表','验证分页参数size=0分页数异常','size=0分页数错误','QueryDTO size=10默认值；已登录student1','1.调成果列表接口GET /api/innovation/page传size=0 page=1','后端返回空列表或给出size最小为1提示；size被纠正回1；不出现除零异常/空指针；code=200正常响应','未执行','P1','严重','边界测试'],
['TC-PAG-003','成果提交模块','个人成果中心列表','验证分页size=1000超大量','size极值超大','已登录student1；成果表记录有限','1.调列表接口传size=1000 page=1；2.观察返回','Mybatis-Plus不报错；返回全量记录或系统设定size上限(如500)；不触发OOM；响应JSON中records大小与实际记录数一致','未执行','P2','一般','边界测试'],
['TC-PAG-004','成果提交模块','用户管理/待办/公告等列表','验证全部分页查询接口page=1首页首条正确性','分页首页首条数据一致','已登录admin/secretary；存在多页记录','1.分别对用户管理/待办列表/公告/系统日志/四类成果列表传page=1 size=10；2.记录每条records[0]的ID/主键；3.分别在数据库执行SELECT * ORDER BY create_time DESC LIMIT 10 OFFSET 0；4.对比首条ID','两者首条记录完全一致；分页排序字段统一按create_time DESC/对应ORDER BY；不存在首页少一条或出现顺序错位','未执行','P1','严重','边界测试'],
['TC-SYS-027','统计与系统管理模块','公告发布页','验证公告标题恰好200字符等于VARCHAR(200)上限','标题边界等于上限','announcement.title列VARCHAR(200) NOT NULL','1.admin登录进入/system/announcement发布；2.标题精确200字符；3.正文合法；4.点击发布','POST返回code=200；200字符完整入库不截断；公告列表展示完整标题；中文4字节字符不乱码','未执行','P2','一般','边界测试'],
]
print(f'\nStep2 重建补测17条: 写入行数={len(SUPP_ROWS)}  TC-ID集={sorted([r[0] for r in SUPP_ROWS])}')
write_rfc4180(SUPP_17_OUT, HEADER_V2, SUPP_ROWS)
_, _, st17 = verify_csv(SUPP_17_OUT, expect_rows=17)
print(f'  -> 写出 {os.path.basename(SUPP_17_OUT)}: {"✅ 17条12列全合规" if st17["all_ok"] else f"⚠️ 异常: {st17}"}')

# =============================================
# Step 3: 合并98+17，排序，写出 merged-115
# =============================================
merged_all = v2_98_data + SUPP_ROWS

def tc_sort(r):
    m = re.match(r'TC-([A-Z]+)-(\d+)', r[0].strip())
    return (m.group(1), int(m.group(2))) if m else ('ZZZ', 999999)
merged_all.sort(key=tc_sort)

# 合并前再次严格去重 TC-ID
seen = set(); merged_unique = []
for r in merged_all:
    tid = r[0].strip()
    if tid in seen:
        print(f'  ⚠️  合并时发现重复TC-ID，跳过: {tid}')
        continue
    seen.add(tid); merged_unique.append(r)

write_rfc4180(MERGED_115_OUT, HEADER_V2, merged_unique)
_, _, st115 = verify_csv(MERGED_115_OUT, expect_rows=115)
s = st115  # 简化引用，规避f-string嵌套反斜杠
bad_cols = s["bad_cols_count"]
cols_msg = "✅ 每行12列" if bad_cols == 0 else f"❌ {bad_cols}行列错"
row_match = "✅" if len(merged_unique) == 115 else "⚠️ 偏差 " + str(115 - len(merged_unique))
bom_ok = "✅" if s["bom"] else "❌"
lf_msg = " ✅(0致错)" if s["lf_only"] == 0 else " ❌错位源"
dup_msg = "  ✅" if s["repeats_count"] == 0 else " ❌"
enum_total = s["enum_pri_bad"] + s["enum_sev_bad"] + s["enum_res_bad"]
enum_msg = "  ✅全合规" if enum_total == 0 else "  ❌"
mod_sum = sum(s["modules"].values())
pri_sum = sum(s["priority"].values())
noexec_val = s["result"].get("未执行", 0)
noexec_msg = " (100%)✅" if noexec_val == 115 else ""
final_msg = "✅ 115条 × 12列 × UTF-8 BOM + CRLF 全合规！Excel打开 / 禅道TAPD导入 0格式错误" if s["all_ok"] else "❌ 仍不通过需排查"
print()
print("Step3 合并写出 " + os.path.basename(MERGED_115_OUT) + ":")
print(f"  合并98+17={len(merged_all)}  去重后={len(merged_unique)}  预期115 {row_match}")
print(f"  BOM={bom_ok}   CRLF换行={s['crlf']}   孤立LF={s['lf_only']}{lf_msg}")
print(f"  12列全匹配={cols_msg}")
print(f"  TC-ID重复={s['repeats_count']}{dup_msg}")
print(f"  枚举越界: 优先级={s['enum_pri_bad']}  严重级={s['enum_sev_bad']}  测试结果={s['enum_res_bad']}{enum_msg}")
print(f"  模块分布 {s['modules']}  合计{mod_sum}")
print(f"  优先级 P0={s['priority'].get('P0',0)}  P1={s['priority'].get('P1',0)}  P2={s['priority'].get('P2',0)}  合计{pri_sum}")
print(f"  测试结果「未执行」 {noexec_val}/115{noexec_msg}")
print(f"  严重级别分布 {s['severity']}")
print()
print("  ===> 最终结论: " + final_msg)
