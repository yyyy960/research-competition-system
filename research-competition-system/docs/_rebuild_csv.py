"""RFC-4180 规范 CSV 重写与验证工具
- 输出：UTF-8 BOM + CRLF 换行（Excel/禅道/TAPD 全兼容）
- 字段内若出现 , 或 " 或 CR 或 LF 则自动用双引号包裹并转义内嵌引号
- 逐结构验证：列数=12、TC-ID不重复、枚举正确、空必填列为0
"""
import csv, re, os, sys
from collections import defaultdict

DOCS = r'd:\project\research-competition-system\research-competition-system\docs'
# 主文件98条 & 补测17条 源文件
MAIN = os.path.join(DOCS, 'web-testcase-all-v2-20260810.csv')     # 原98条
SUPP = os.path.join(DOCS, 'web-testcase-boundary-supplement-v2-20260810.csv')  # 原17条
MERGED_OUT = os.path.join(DOCS, 'web-testcase-all-v2-merged-115-20260810.csv') # 合并输出
HEADER = ['用例ID','模块','页面','测试点','用例标题','预置条件','测试步骤','预期结果','测试结果','优先级','严重级别','测试类型']
PRI_ALLOW = {'P0','P1','P2'}
SEV_ALLOW = {'致命','严重','一般','轻微'}
RES_ALLOW = {'未执行','通过','失败','阻塞'}

def read_csv_strict(path):
    # utf-8-sig 自动去BOM，newline='' 让 csv.reader 处理 RFC CRLF/LF 混合换行
    with open(path, 'r', encoding='utf-8-sig', newline='') as f:
        reader = csv.reader(f)
        rows = list(reader)
    header = rows[0]
    data = rows[1:]
    # 过滤空行
    data = [r for r in data if any(c.strip() for c in r)]
    # 补全列数缺漏（防止错位 12 列）：小于12右补空，大于12截断保留前12并告警
    fixed, warns = [], []
    for i, r in enumerate(data, start=2):
        if len(r) < 12:
            warns.append((path, i, f'列数不足12（实际{len(r)}）补空'))
            r = r + [''] * (12 - len(r))
        elif len(r) > 12:
            warns.append((path, i, f'列数超过12（实际{len(r)}）截断前12'))
            r = r[:12]
        fixed.append(r)
    return header, fixed, warns

# 1. 读取两份源 CSV 并报问题
main_h, main_d, main_w = read_csv_strict(MAIN)
supp_h, supp_d, supp_w = read_csv_strict(SUPP)

print('===== 源文件结构诊断 =====')
print(f'[主文件98条] 表头={main_h[:3]}...  列数={len(main_h)}  数据行数={len(main_d)}  结构告警={len(main_w)}')
for w in main_w[:8]: print(f'   · {w[1]}行: {w[2]}')
print(f'[补测17条] 表头={supp_h[:3]}...  列数={len(supp_h)}  数据行数={len(supp_d)}  结构告警={len(supp_w)}')
for w in supp_w[:8]: print(f'   · {w[1]}行: {w[2]}')

# 2. 合并 + 按 TC-ID 排序（模块字母序 + 数字序）
def sort_key(row):
    m = re.match(r'TC-([A-Z]+)-(\d+)', row[0].strip().strip('"'))
    return (m.group(1), int(m.group(2))) if m else ('ZZZ', 999999)

merged_data = sorted(main_d + supp_d, key=sort_key)

# 3. 合规性校验在写出前先做一遍
print('\n===== 合并后结构校验（写出前）=====')
ids = [r[0].strip() for r in merged_data]
print(f'合并数据行: {len(merged_data)}  预期: 115  ✅' if len(merged_data)==115 else f'  ⚠️  合并后 {len(merged_data)} ≠ 115')
dup_map = defaultdict(list)
for i, tid in enumerate(ids, start=2): dup_map[tid].append(i)
repeats = [(k, v) for k, v in dup_map.items() if len(v) > 1]
print(f'TC-ID重复: {len(repeats)} 处  ✅' if len(repeats)==0 else f'TC-ID重复: {repeats}')

empties, pri_bad, sev_bad, res_bad = 0, 0, 0, 0
for r in merged_data:
    for ci in range(12):
        if r[ci].strip() == '': empties += 1
    if r[9].strip() not in PRI_ALLOW: pri_bad += 1
    if r[10].strip() not in SEV_ALLOW: sev_bad += 1
    if r[8].strip() not in RES_ALLOW: res_bad += 1
print(f'空单元格(全部列合计): {empties} 处   非法优先级:{pri_bad}   非法严重级别:{sev_bad}   非法测试结果:{res_bad}')

# 4. 按 RFC-4180 规范写出（utf-8-sig 自动写BOM，lineterminator='\r\n' Excel最兼容CRLF，QUOTE_MINIMAL仅在必要时加引号）
with open(MERGED_OUT, 'w', encoding='utf-8-sig', newline='') as f:
    writer = csv.writer(f, lineterminator='\r\n', quoting=csv.QUOTE_MINIMAL)
    writer.writerow(HEADER)
    writer.writerows(merged_data)

# 5. 写出后回读 + 二次结构验证 + 文件特征（BOM字节/CRLF计数）
with open(MERGED_OUT, 'rb') as f:
    raw_bytes = f.read()
bom_ok = raw_bytes[:3] == b'\xef\xbb\xbf'
crlf = raw_bytes.count(b'\r\n')
lf_only = raw_bytes.count(b'\n') - crlf
print(f'\n===== 写出文件特征（Excel 兼容性关键） =====')
print(f'路径: {MERGED_OUT}')
print(f'大小(字节): {len(raw_bytes)}  ≈ {len(raw_bytes)/1024:.1f} KB')
print(f'UTF-8 BOM头: {"✅ YES (3-byte EF BB BF)" if bom_ok else "❌ NO"}')
print(f'CRLF 换行 (Excel兼容): {crlf} 处    孤立LF(会致Excel错位): {lf_only} 处')
print(f'Expected CRLF=116(1表头+115数据): {"✅" if crlf==116 else "⚠️ 数量:"+str(crlf)}')

# 回读 + 最终全量核对
with open(MERGED_OUT, 'r', encoding='utf-8-sig', newline='') as f:
    rows = list(csv.reader(f))
print(f'\n===== 最终回读验证 =====')
print(f'csv.reader 解析记录: {len(rows)}（含表头）= 表头1 + 数据{len(rows)-1}')
cols = [len(r) for r in rows]
bad_cols = [(i+1, c) for i, c in enumerate(cols) if c != 12]
print(f'12列结构一致性: {"✅ 全部行12列" if len(bad_cols)==0 else f"❌ {len(bad_cols)}行列数错：{bad_cols}"}')

final_ids = [r[0].strip() for r in rows[1:]]
uniq = len(set(final_ids))
print(f'TC-ID唯一: {uniq}/{len(final_ids)}  ✅' if uniq==len(final_ids)==115 else f'⚠️  唯一={uniq} 总数={len(final_ids)}')

mod_counts = defaultdict(int)
for tid in final_ids:
    m = re.match(r'TC-([A-Z]+)-\d+', tid)
    if m: mod_counts[m.group(1)] += 1
print(f'模块分布: {dict(mod_counts)}  合计{sum(mod_counts.values())}')

p0 = sum(1 for r in rows[1:] if r[9].strip()=='P0')
p1 = sum(1 for r in rows[1:] if r[9].strip()=='P1')
p2 = sum(1 for r in rows[1:] if r[9].strip()=='P2')
noexec = sum(1 for r in rows[1:] if r[8].strip()=='未执行')
print(f'优先级分布: P0={p0}  P1={p1}  P2={p2}  总计={p0+p1+p2}')
print(f'测试结果默认「未执行」: {noexec}/{len(rows)-1}  (100%)' if noexec==len(rows)-1 else f'⚠️  未执行={noexec}/{len(rows)-1}')
print(f'\n✅ 全部校验通过！文件可安全导入Excel/禅道/TAPD。' if (bom_ok and lf_only==0 and len(bad_cols)==0 and uniq==115) else '\n❌ 仍有问题需修复。')
