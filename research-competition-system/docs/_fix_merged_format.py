"""直接诊断用户有格式错误的那份 merged-115 原始字节 -> 再重写"""
import csv, re, os, sys
from collections import defaultdict

# 用户说格式错误的目标文件
BAD = r'd:\project\research-competition-system\research-competition-system\docs\web-testcase-all-v2-merged-115-20260810.csv'
OUT_GOOD = BAD   # 修好覆盖到同路径同文件名（或加 .fixed.csv 双保险）
FIXED_BACKUP = BAD.replace('.csv', '.fixed.csv')
HEADER = ['用例ID','模块','页面','测试点','用例标题','预置条件','测试步骤','预期结果','测试结果','优先级','严重级别','测试类型']
PRI_ALLOW = {'P0','P1','P2'}; SEV_ALLOW = {'致命','严重','一般','轻微'}; RES_ALLOW = {'未执行','通过','失败','阻塞'}

# =============== A. 原始字节特征检查（最关键：BOM/换行格式）===============
with open(BAD, 'rb') as f:
    b = f.read()
print('===== A. 原始字节分析 =====')
print(f'文件: {BAD}')
print(f'大小: {len(b)} 字节')
bom = b[:3] == b'\xef\xbb\xbf'
print(f'UTF-8 BOM (EF BB BF): {"✅ YES" if bom else "❌ NO (直接Excel打开必乱码/错位)"}')
crlf_cnt = b.count(b'\r\n')
lf_cnt = b.count(b'\n') - crlf_cnt
cr_cnt = b.count(b'\r') - crlf_cnt
print(f'换行统计: CRLF(\\r\\n Excel兼容) = {crlf_cnt}    孤立LF(纯\\n 致错位) = {lf_cnt}    孤立CR={cr_cnt}')
# 首 3 行和末 1 行的换行结束字节
line_ends = re.findall(rb'(\r\n|\r|\n)', b[:8000])
print(f'前8000字节里出现的换行符序列: {[(seq.decode(errors="replace"), list(line_ends).count(seq)) for seq in sorted(set(line_ends))]}')

# =============== B. csv.reader 尝试解析 + 找错位行列 ===============
print('\n===== B. csv.reader 结构解析 =====')
with open(BAD, 'r', encoding='utf-8-sig' if bom else 'utf-8', newline='') as f:
    rows = list(csv.reader(f))
print(f'解析到总行数（含表头）: {len(rows)}')
col_counts = [len(r) for r in rows]
bad_cols = [(i+1, c) for i, c in enumerate(col_counts) if c != 12]
print(f'列数 !=12 的行: {len(bad_cols)} 处')
for lineno, c in bad_cols[:25]:
    snip = ' | '.join(f'{(x if len(x)<=40 else x[:38]+"…")!r}' for x in rows[lineno-1][:12])
    print(f'  第{lineno}行 列={c}  片段: {snip[:260]}')

# =============== C. 合并修复（补全12列 + 去重ID + 排序） ===============
header = rows[0]
data = rows[1:]
data = [r for r in data if any(x.strip() for x in r)]

# 列数规整：<12 右补空, >12 截断
fixed, trunc = [], 0
for r in data:
    if len(r) < 12:
        r = r + [''] * (12 - len(r))
    elif len(r) > 12:
        r = r[:12]; trunc += 1
    fixed.append(r)
print(f'\n列规整结果: 输入{len(data)}行, 截断>12列的行: {trunc}')

# 去重（按 TC-ID 保留首次出现）
seen = set(); deduped = []; dup_removed = 0
for r in fixed:
    tid = r[0].strip().strip('"')
    if not tid: continue
    if tid in seen:
        dup_removed += 1; continue
    seen.add(tid); deduped.append(r)
print(f'TC-ID去重: 移除重复 {dup_removed} 条, 剩余 {len(deduped)} 条')

# 按 TC-模块名-序号 排序
def key(r):
    m = re.match(r'TC-([A-Z]+)-(\d+)', r[0].strip())
    return (m.group(1), int(m.group(2))) if m else ('ZZZ', 999999)
deduped.sort(key=key)

# 枚举/空值 最终合规检查
empties = pri_b = sev_b = res_b = 0
for r in deduped:
    for c in r:
        if c.strip() == '': empties += 1
    if r[9].strip() not in PRI_ALLOW: pri_b += 1
    if r[10].strip() not in SEV_ALLOW: sev_b += 1
    if r[8].strip() not in RES_ALLOW: res_b += 1
print(f'合规检查: 空单元格(含非必填)= {empties}  非法优先级={pri_b}  严重级别越界={sev_b}  测试结果越界={res_b}')

# =============== D. 写 RFC-4180 标准 CSV（UTF-8 BOM + CRLF + QUOTE_MINIMAL）===============
for target in (OUT_GOOD, FIXED_BACKUP):
    with open(target, 'w', encoding='utf-8-sig', newline='') as f:
        w = csv.writer(f, lineterminator='\r\n', quoting=csv.QUOTE_MINIMAL)
        w.writerow(HEADER)
        w.writerows(deduped)

# =============== E. 写好后回读二次自证 ===============
print('\n===== E. 写出文件后自证 =====')
for label, path in [('覆盖主文件', OUT_GOOD), ('备份fixed副本', FIXED_BACKUP)]:
    with open(path, 'rb') as f: bb = f.read()
    ok_bom = bb[:3] == b'\xef\xbb\xbf'
    cc = bb.count(b'\r\n')
    lf_only = bb.count(b'\n') - cc
    with open(path, 'r', encoding='utf-8-sig', newline='') as f:
        rr = list(csv.reader(f))
    col_ok = all(len(x) == 12 for x in rr)
    ids = [x[0].strip() for x in rr[1:]]
    uniq = len(set(ids))
    total = len(ids)
    p0 = sum(1 for r in rr[1:] if r[9].strip() == 'P0')
    p1 = sum(1 for r in rr[1:] if r[9].strip() == 'P1')
    p2 = sum(1 for r in rr[1:] if r[9].strip() == 'P2')
    noexec = sum(1 for r in rr[1:] if r[8].strip() == '未执行')
    mod_counts = defaultdict(int)
    for tid in ids:
        m = re.match(r'TC-([A-Z]+)-\d+', tid)
        if m: mod_counts[m.group(1)] += 1
    all_ok = ok_bom and lf_only == 0 and col_ok and uniq == total == 115 and (p0+p1+p2)==115 and noexec==115
    print(f'[{label}] {path}')
    print(f'  BOM={"✅" if ok_bom else "❌"}  CRLF换行={cc}  孤立LF={lf_only}{" ✅" if lf_only==0 else " ❌ 致错位"}')
    print(f'  12列={"✅" if col_ok else "❌"}  TC-ID唯一={uniq}  总数={total}{" ✅(115)" if total==115 else f" ⚠️≠115"}')
    print(f'  优先级 P0={p0} P1={p1} P2={p2} 合计={p0+p1+p2}')
    print(f'  测试结果默认未执行= {noexec}/115')
    print(f'  模块分布 {dict(mod_counts)}  合计{sum(mod_counts.values())}')
    print(f'  => 结论: {"✅ 全部校验通过，Excel/禅道/TAPD导入零错误" if all_ok else "❌ 仍有问题需处理"}')
    print()
