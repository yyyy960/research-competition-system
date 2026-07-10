DROP TABLE IF EXISTS ccf_venue;
CREATE TABLE ccf_venue (
    id BIGINT NOT NULL AUTO_INCREMENT,
    venue_type VARCHAR(20) NOT NULL COMMENT '类型',
    area VARCHAR(100) NOT NULL COMMENT '研究方向',
    level CHAR(1) NOT NULL COMMENT 'CCF等级 A/B/C',
    abbreviation VARCHAR(100) NOT NULL COMMENT '简称',
    full_name VARCHAR(300) NOT NULL COMMENT '全称',
    publisher VARCHAR(100) COMMENT '出版社',
    url VARCHAR(500) COMMENT '网址',
    PRIMARY KEY (id),
    KEY idx_area_level (area, level),
    KEY idx_type (venue_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CCF推荐学术会议和期刊目录';
SET NAMES utf8mb4;-- CCF推荐国际学术会议和期刊目录 (2022)
CREATE TABLE IF NOT EXISTS `ccf_venue` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `venue_type` VARCHAR(20) NOT NULL COMMENT '类型: journal/conference',
    `area` VARCHAR(100) NOT NULL COMMENT '研究方向',
    `level` CHAR(1) NOT NULL COMMENT 'CCF等级: A/B/C',
    `abbreviation` VARCHAR(100) NOT NULL COMMENT '简称',
    `full_name` VARCHAR(300) NOT NULL COMMENT '全称',
    `publisher` VARCHAR(100) COMMENT '出版社',
    `url` VARCHAR(500) COMMENT '网址',
    PRIMARY KEY (`id`),
    KEY `idx_area_level` (`area`, `level`),
    KEY `idx_type` (`venue_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CCF推荐学术会议和期刊目录';

-- 计算机体系结构/并行与分布计算/存储系统 - 期刊
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('journal', '计算机体系结构/并行与分布计算/存储系统', 'A', 'TOCS', 'ACM Transactions on Computer Systems', 'ACM', 'http://dblp.uni-trier.de/db/journals/tocs/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'A', 'TOS', 'ACM Transactions on Storage', 'ACM', 'http://dblp.uni-trier.de/db/journals/tos/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'A', 'TCAD', 'IEEE Transactions on Computer-Aided Design of Integrated Circuits and Systems', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tcad/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'A', 'TC', 'IEEE Transactions on Computers', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tc/index.html'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'A', 'TPDS', 'IEEE Transactions on Parallel and Distributed Systems', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tpds/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'A', 'TACO', 'ACM Transactions on Architecture and Code Optimization', 'ACM', 'http://dblp.uni-trier.de/db/journals/taco/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'B', 'TAAS', 'ACM Transactions on Autonomous and Adaptive Systems', 'ACM', 'http://dblp.uni-trier.de/db/journals/taas/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'B', 'TODAES', 'ACM Transactions on Design Automation of Electronic Systems', 'ACM', 'http://dblp.uni-trier.de/db/journals/todaes/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'B', 'TECS', 'ACM Transactions on Embedded Computing Systems', 'ACM', 'http://dblp.uni-trier.de/db/journals/tecs/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'B', 'TRETS', 'ACM Transactions on Reconfigurable Technology and Systems', 'ACM', 'http://dblp.uni-trier.de/db/journals/trets/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'B', 'TVLSI', 'IEEE Transactions on Very Large Scale Integration (VLSI) Systems', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tvlsi/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'B', 'JPDC', 'Journal of Parallel and Distributed Computing', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/jpdc/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'B', 'JSA', 'Journal of Systems Architecture: Embedded Software Design', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/jsa/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'B', 'Parallel Computing', 'Parallel Computing', 'Elsevier', 'https://dblp.org/db/journals/pc/index.html'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'B', 'Performance Evaluation', 'Performance Evaluation: An International Journal', 'Elsevier', 'https://dblp.org/db/journals/pe/index.html'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'JETC', 'ACM Journal on Emerging Technologies in Computing Systems', 'ACM', 'http://dblp.uni-trier.de/db/journals/jetc/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'Concurrency and Computation', 'Concurrency and Computation: Practice and Experience', 'Wiley', 'http://dblp.uni-trier.de/db/journals/concurrency/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'DC', 'Distributed Computing', 'Springer', 'http://dblp.uni-trier.de/db/journals/dc/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'FGCS', 'Future Generation Computer Systems', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/fgcs/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'TCC', 'IEEE Transactions on Cloud Computing', 'IEEE', 'https://dblp.uni-trier.de/db/journals/tcc/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'Integration', 'Integration, the VLSI Journal', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/integration/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'JETTA', 'Journal of Electronic Testing-Theory and Applications', 'Springer', 'https://dblp.org/db/journals/et/index.html'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'JGC', 'Journal of Grid computing', 'Springer', 'https://dblp.uni-trier.de/db/journals/grid/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'MICPRO', 'Microprocessors and Microsystems: Embedded Hardware Design', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/mam/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'RTS', 'Real-Time Systems', 'Springer', 'http://dblp.uni-trier.de/db/journals/rts/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'TJSC', 'The Journal of Supercomputing', 'Springer', 'http://dblp.uni-trier.de/db/journals/tjs/'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'TCASI', 'IEEE Transactions on Circuits and Systems I: Regular Papers', 'IEEE', 'https://dblp.org/db/journals/tcasI/index.html'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'CCF-THPC', 'CCF Transactions on High Performance Computing', 'CCF', 'https://dblp.org/db/journals/ccfthpc/index.html'),
('journal', '计算机体系结构/并行与分布计算/存储系统', 'C', 'TSUSC', 'IEEE Transactions on Sustainable Computing', 'IEEE', 'https://dblp.org/db/journals/tsusc/index.html');

-- 计算机网络 - 期刊
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('journal', '计算机网络', 'A', 'JSAC', 'IEEE Journal on Selected Areas in Communications', 'IEEE', 'http://dblp.uni-trier.de/db/journals/jsac/'),
('journal', '计算机网络', 'A', 'TMC', 'IEEE Transactions on Mobile Computing', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tmc/'),
('journal', '计算机网络', 'A', 'TON', 'IEEE/ACM Transactions on Networking', 'IEEE/ACM', 'http://dblp.uni-trier.de/db/journals/ton/'),
('journal', '计算机网络', 'B', 'TOIT', 'ACM Transactions on Internet Technology', 'ACM', 'http://dblp.uni-trier.de/db/journals/toit/'),
('journal', '计算机网络', 'B', 'TOMM', 'ACM Transactions on Multimedia Computing, Communications and Applications', 'ACM', 'http://dblp.uni-trier.de/db/journals/tomccap/'),
('journal', '计算机网络', 'B', 'TOSN', 'ACM Transactions on Sensor Networks', 'ACM', 'http://dblp.uni-trier.de/db/journals/tosn/'),
('journal', '计算机网络', 'B', 'CN', 'Computer Networks', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/cn/'),
('journal', '计算机网络', 'B', 'TCOM', 'IEEE Transactions on Communications', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tcom/'),
('journal', '计算机网络', 'B', 'TWC', 'IEEE Transactions on Wireless Communications', 'IEEE', 'http://dblp.uni-trier.de/db/journals/twc/'),
('journal', '计算机网络', 'C', 'Ad Hoc Networks', 'Ad Hoc Networks', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/adhoc/'),
('journal', '计算机网络', 'C', 'CC', 'Computer Communications', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/comcom/'),
('journal', '计算机网络', 'C', 'TNSM', 'IEEE Transactions on Network and Service Management', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tnsm/'),
('journal', '计算机网络', 'C', 'IET Communications', 'IET Communications', 'IET', 'http://dblp.uni-trier.de/db/journals/iet-com/'),
('journal', '计算机网络', 'C', 'JNCA', 'Journal of Network and Computer Applications', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/jnca/'),
('journal', '计算机网络', 'C', 'MONET', 'Mobile Networks and Applications', 'Springer', 'http://dblp.uni-trier.de/db/journals/monet/'),
('journal', '计算机网络', 'C', 'Networks', 'Networks', 'Wiley', 'http://dblp.uni-trier.de/db/journals/networks/'),
('journal', '计算机网络', 'C', 'PPNA', 'Peer-to-Peer Networking and Applications', 'Springer', 'http://dblp.uni-trier.de/db/journals/ppna/'),
('journal', '计算机网络', 'C', 'WCMC', 'Wireless Communications and Mobile Computing', 'Wiley', 'http://dblp.uni-trier.de/db/journals/wicomm/'),
('journal', '计算机网络', 'C', 'Wireless Networks', 'Wireless Networks', 'Springer', 'http://dblp.uni-trier.de/db/journals/winet/'),
('journal', '计算机网络', 'C', 'IOT', 'IEEE Internet of Things Journal', 'IEEE', 'https://dblp.org/db/journals/iotj/index.html');

-- 网络与信息安全 - 期刊
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('journal', '网络与信息安全', 'A', 'TDSC', 'IEEE Transactions on Dependable and Secure Computing', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tdsc/'),
('journal', '网络与信息安全', 'A', 'TIFS', 'IEEE Transactions on Information Forensics and Security', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tifs/'),
('journal', '网络与信息安全', 'A', 'Journal of Cryptology', 'Journal of Cryptology', 'Springer', 'http://dblp.uni-trier.de/db/journals/joc/'),
('journal', '网络与信息安全', 'B', 'TOPS', 'ACM Transactions on Privacy and Security', 'ACM', 'https://dblp.org/db/journals/tissec/index.html'),
('journal', '网络与信息安全', 'B', 'Computers & Security', 'Computers & Security', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/compsec/'),
('journal', '网络与信息安全', 'B', 'Designs,Codes and Cryptography', 'Designs,Codes and Cryptography', 'Springer', 'http://dblp.uni-trier.de/db/journals/dcc/'),
('journal', '网络与信息安全', 'B', 'JCS', 'Journal of Computer Security', 'IOS Press', 'http://dblp.uni-trier.de/db/journals/jcs/'),
('journal', '网络与信息安全', 'C', 'CLSR', 'Computer Law & Security Review', 'Elsevier', 'https://dblp.org/db/journals/clsr/index.html'),
('journal', '网络与信息安全', 'C', 'EURASIP JIS', 'EURASIP Journal on Information Security', 'Springer', 'http://dblp.uni-trier.de/db/journals/ejisec/'),
('journal', '网络与信息安全', 'C', 'IET Information Security', 'IET Information Security', 'IET', 'http://dblp.uni-trier.de/db/journals/iet-ifs/'),
('journal', '网络与信息安全', 'C', 'IJICS', 'International Journal of Information and Computer Security', 'Inderscience', 'http://dblp.uni-trier.de/db/journals/ijics/'),
('journal', '网络与信息安全', 'C', 'IJISP', 'International Journal of Information Security and Privacy', 'IGI Global', 'http://dblp.uni-trier.de/db/journals/ijisp/'),
('journal', '网络与信息安全', 'C', 'JISA', 'Journal of Information Security and Applications', 'Elsevier', 'https://dblp.uni-trier.de/db/journals/istr/'),
('journal', '网络与信息安全', 'C', 'SCN', 'Security and Communication Networks', 'Wiley', 'http://dblp.uni-trier.de/db/journals/scn/'),
('journal', '网络与信息安全', 'C', 'Cybersecurity', 'Cybersecurity', 'Springer', 'https://dblp.uni-trier.de/db/journals/cybersec/index.html');

-- 软件工程/系统软件/程序设计语言 - 期刊
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('journal', '软件工程/系统软件/程序设计语言', 'A', 'TOPLAS', 'ACM Transactions on Programming Languages and Systems', 'ACM', 'http://dblp.uni-trier.de/db/journals/toplas/'),
('journal', '软件工程/系统软件/程序设计语言', 'A', 'TOSEM', 'ACM Transactions on Software Engineering and Methodology', 'ACM', 'http://dblp.uni-trier.de/db/journals/tosem/'),
('journal', '软件工程/系统软件/程序设计语言', 'A', 'TSE', 'IEEE Transactions on Software Engineering', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tse/'),
('journal', '软件工程/系统软件/程序设计语言', 'A', 'TSC', 'IEEE Transactions on Services Computing', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tsc/'),
('journal', '软件工程/系统软件/程序设计语言', 'B', 'ASE', 'Automated Software Engineering', 'Springer', 'http://dblp.uni-trier.de/db/journals/ase/'),
('journal', '软件工程/系统软件/程序设计语言', 'B', 'ESE', 'Empirical Software Engineering', 'Springer', 'http://dblp.uni-trier.de/db/journals/ese/'),
('journal', '软件工程/系统软件/程序设计语言', 'B', 'IETS', 'IET Software', 'IET', 'https://dblp.uni-trier.de/db/journals/iet-sen/'),
('journal', '软件工程/系统软件/程序设计语言', 'B', 'IST', 'Information and Software Technology', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/infsof/index.html'),
('journal', '软件工程/系统软件/程序设计语言', 'B', 'JFP', 'Journal of Functional Programming', 'Cambridge University Press', 'http://dblp.uni-trier.de/db/journals/jfp/'),
('journal', '软件工程/系统软件/程序设计语言', 'B', 'JSME', 'Journal of Software: Evolution and Process', 'Wiley', 'http://dblp.uni-trier.de/db/journals/smr/'),
('journal', '软件工程/系统软件/程序设计语言', 'B', 'JSS', 'Journal of Systems and Software', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/jss/'),
('journal', '软件工程/系统软件/程序设计语言', 'B', 'RE', 'Requirements Engineering', 'Springer', 'http://dblp.uni-trier.de/db/journals/re/'),
('journal', '软件工程/系统软件/程序设计语言', 'B', 'SCP', 'Science of Computer Programming', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/scp/'),
('journal', '软件工程/系统软件/程序设计语言', 'B', 'SoSyM', 'Software and Systems Modeling', 'Springer', 'http://dblp.uni-trier.de/db/journals/sosym/'),
('journal', '软件工程/系统软件/程序设计语言', 'B', 'STVR', 'Software Testing, Verification and Reliability', 'Wiley', 'http://dblp.uni-trier.de/db/journals/stvr/index.html'),
('journal', '软件工程/系统软件/程序设计语言', 'B', 'SPE', 'Software: Practice and Experience', 'Wiley', 'http://dblp.uni-trier.de/db/journals/spe/');

-- 数据库/数据挖掘/内容检索 - 期刊
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('journal', '数据库/数据挖掘/内容检索', 'A', 'TODS', 'ACM Transactions on Database Systems', 'ACM', 'http://dblp.uni-trier.de/db/journals/tods/'),
('journal', '数据库/数据挖掘/内容检索', 'A', 'TOIS', 'ACM Transactions on Information Systems', 'ACM', 'http://dblp.uni-trier.de/db/journals/tois/'),
('journal', '数据库/数据挖掘/内容检索', 'A', 'TKDE', 'IEEE Transactions on Knowledge and Data Engineering', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tkde/'),
('journal', '数据库/数据挖掘/内容检索', 'A', 'VLDBJ', 'The VLDB Journal', 'Springer', 'http://dblp.uni-trier.de/db/journals/vldb/'),
('journal', '数据库/数据挖掘/内容检索', 'B', 'TKDD', 'ACM Transactions on Knowledge Discovery from Data', 'ACM', 'http://dblp.uni-trier.de/db/journals/tkdd/'),
('journal', '数据库/数据挖掘/内容检索', 'B', 'TWEB', 'ACM Transactions on the Web', 'ACM', 'http://dblp.uni-trier.de/db/journals/tweb/'),
('journal', '数据库/数据挖掘/内容检索', 'B', 'AEI', 'Advanced Engineering Informatics', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/aei/'),
('journal', '数据库/数据挖掘/内容检索', 'B', 'DKE', 'Data & Knowledge Engineering', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/dke/'),
('journal', '数据库/数据挖掘/内容检索', 'B', 'DMKD', 'Data Mining and Knowledge Discovery', 'Springer', 'http://dblp.uni-trier.de/db/journals/datamine/'),
('journal', '数据库/数据挖掘/内容检索', 'B', 'Information Sciences', 'Information Sciences', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/isci/'),
('journal', '数据库/数据挖掘/内容检索', 'B', 'IS', 'Information Systems', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/is/'),
('journal', '数据库/数据挖掘/内容检索', 'B', 'JASIST', 'Journal of the Association for Information Science and Technology', 'Wiley', 'http://dblp.uni-trier.de/db/journals/jasis/'),
('journal', '数据库/数据挖掘/内容检索', 'B', 'JWS', 'Journal of Web Semantics', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/ws/'),
('journal', '数据库/数据挖掘/内容检索', 'B', 'KAIS', 'Knowledge and Information Systems', 'Springer', 'http://dblp.uni-trier.de/db/journals/kais/');

-- 计算机图形学与多媒体 - 期刊
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('journal', '计算机图形学与多媒体', 'A', 'TOG', 'ACM Transactions on Graphics', 'ACM', 'http://dblp.uni-trier.de/db/journals/tog/'),
('journal', '计算机图形学与多媒体', 'A', 'TIP', 'IEEE Transactions on Image Processing', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tip/'),
('journal', '计算机图形学与多媒体', 'A', 'TVCG', 'IEEE Transactions on Visualization and Computer Graphics', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tvcg/'),
('journal', '计算机图形学与多媒体', 'B', 'TOMM', 'ACM Transactions on Multimedia Computing, Communications and Applications', 'ACM', 'http://dblp.uni-trier.de/db/journals/tomccap/'),
('journal', '计算机图形学与多媒体', 'B', 'CAGD', 'Computer Aided Geometric Design', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/cagd/'),
('journal', '计算机图形学与多媒体', 'B', 'CGF', 'Computer Graphics Forum', 'Wiley', 'http://dblp.uni-trier.de/db/journals/cgf/'),
('journal', '计算机图形学与多媒体', 'B', 'CAD', 'Computer-Aided Design', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/cad/'),
('journal', '计算机图形学与多媒体', 'B', 'GM', 'Graphical Models', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/cvgip/'),
('journal', '计算机图形学与多媒体', 'B', 'TCSVT', 'IEEE Transactions on Circuits and Systems for Video Technology', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tcsv/'),
('journal', '计算机图形学与多媒体', 'B', 'TMM', 'IEEE Transactions on Multimedia', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tmm/'),
('journal', '计算机图形学与多媒体', 'B', 'SIIMS', 'SIAM Journal on Imaging Sciences', 'SIAM', 'http://dblp.uni-trier.de/db/journals/siamis/'),
('journal', '计算机图形学与多媒体', 'B', 'SPECOM', 'Speech Communication', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/speech/'),
('journal', '计算机图形学与多媒体', 'C', 'MTA', 'Multimedia Tools and Applications', 'Springer', 'http://dblp.uni-trier.de/db/journals/mta/'),
('journal', '计算机图形学与多媒体', 'C', 'SIGPRO', 'Signal Processing', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/sigpro/'),
('journal', '计算机图形学与多媒体', 'C', 'IMAGE', 'Signal Processing: Image Communication', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/spic/'),
('journal', '计算机图形学与多媒体', 'C', 'TVC', 'The Visual Computer', 'Springer', 'http://dblp.uni-trier.de/db/journals/vc/'),
('journal', '计算机图形学与多媒体', 'C', 'CVMJ', 'Computational Visual Media', 'Tsinghua University; Springer', 'https://dblp.org/db/journals/cvm/index.html');

-- 计算机科学理论 - 期刊
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('journal', '计算机科学理论', 'A', 'TIT', 'IEEE Transactions on Information Theory', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tit/'),
('journal', '计算机科学理论', 'A', 'IANDC', 'Information and Computation', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/iandc/'),
('journal', '计算机科学理论', 'A', 'SICOMP', 'SIAM Journal on Computing', 'SIAM', 'http://dblp.uni-trier.de/db/journals/siamcomp/'),
('journal', '计算机科学理论', 'B', 'TALG', 'ACM Transactions on Algorithms', 'ACM', 'http://dblp.uni-trier.de/db/journals/talg/'),
('journal', '计算机科学理论', 'B', 'TOCL', 'ACM Transactions on Computational Logic', 'ACM', 'http://dblp.uni-trier.de/db/journals/tocl/'),
('journal', '计算机科学理论', 'B', 'TOMS', 'ACM Transactions on Mathematical Software', 'ACM', 'http://dblp.uni-trier.de/db/journals/toms/'),
('journal', '计算机科学理论', 'B', 'Algorithmica', 'Algorithmica', 'Springer', 'http://dblp.uni-trier.de/db/journals/algorithmica/'),
('journal', '计算机科学理论', 'B', 'CC', 'Computational Complexity', 'Springer', 'http://dblp.uni-trier.de/db/journals/cc/'),
('journal', '计算机科学理论', 'B', 'FAC', 'Formal Aspects of Computing', 'Springer', 'http://dblp.uni-trier.de/db/journals/fac/'),
('journal', '计算机科学理论', 'B', 'FMSD', 'Formal Methods in System Design', 'Springer', 'http://dblp.uni-trier.de/db/journals/fmsd/'),
('journal', '计算机科学理论', 'B', 'JCSS', 'Journal of Computer and System Sciences', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/jcss/'),
('journal', '计算机科学理论', 'B', 'JSC', 'Journal of Symbolic Computation', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/jsc/'),
('journal', '计算机科学理论', 'B', 'TCS', 'Theoretical Computer Science', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/tcs/'),
('journal', '计算机科学理论', 'C', 'ACTA', 'Acta Informatica', 'Springer', 'http://dblp.uni-trier.de/db/journals/acta/'),
('journal', '计算机科学理论', 'C', 'IPL', 'Information Processing Letters', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/ipl/'),
('journal', '计算机科学理论', 'C', 'JCOMPLEXITY', 'Journal of Complexity', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/jc/'),
('journal', '计算机科学理论', 'C', 'LOGCOM', 'Journal of Logic and Computation', 'Oxford University Press', 'http://dblp.uni-trier.de/db/journals/logcom/'),
('journal', '计算机科学理论', 'C', 'LMCS', 'Logical Methods in Computer Science', 'LMCS', 'http://dblp.uni-trier.de/db/journals/lmcs/'),
('journal', '计算机科学理论', 'C', 'SIDMA', 'SIAM Journal on Discrete Mathematics', 'SIAM', 'http://dblp.uni-trier.de/db/journals/siamdm/'),
('journal', '计算机科学理论', 'C', 'Theory of Computing Systems', 'Theory of Computing Systems', 'Springer', 'http://dblp.uni-trier.de/db/journals/mst/');

-- ============================================================
-- 会议 (Conferences)
-- ============================================================

-- 计算机体系结构/并行与分布计算/存储系统 - 会议
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('conference', '计算机体系结构/并行与分布计算/存储系统', 'A', 'PPoPP', 'ACM SIGPLAN Symposium on Principles & Practice of Parallel Programming', 'ACM', 'http://dblp.uni-trier.de/db/conf/ppopp/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'A', 'FAST', 'USENIX Conference on File and Storage Technologies', 'USENIX', 'http://dblp.uni-trier.de/db/conf/fast/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'A', 'DAC', 'Design Automation Conference', 'ACM', 'https://dblp.uni-trier.de/db/conf/dac/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'A', 'HPCA', 'IEEE International Symposium on High Performance Computer Architecture', 'IEEE', 'http://dblp.uni-trier.de/db/conf/hpca/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'A', 'MICRO', 'IEEE/ACM International Symposium on Microarchitecture', 'IEEE/ACM', 'https://dblp.uni-trier.de/db/conf/micro/index.html'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'A', 'SC', 'International Conference for High Performance Computing, Networking, Storage, and Analysis', 'IEEE', 'http://dblp.uni-trier.de/db/conf/sc/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'A', 'ASPLOS', 'International Conference on Architectural Support for Programming Languages and Operating Systems', 'ACM', 'http://dblp.uni-trier.de/db/conf/asplos/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'A', 'ISCA', 'International Symposium on Computer Architecture', 'ACM/IEEE', 'http://dblp.uni-trier.de/db/conf/isca/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'A', 'USENIX ATC', 'USENIX Annual Technical Conference', 'USENIX', 'http://dblp.uni-trier.de/db/conf/usenix/index.html'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'A', 'EuroSys', 'European Conference on Computer Systems', 'ACM', 'http://dblp.uni-trier.de/db/conf/eurosys/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'B', 'SoCC', 'ACM Symposium on Cloud Computing', 'ACM', 'http://dblp.uni-trier.de/db/conf/cloud/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'B', 'SPAA', 'ACM Symposium on Parallelism in Algorithms and Architectures', 'ACM', 'http://dblp.uni-trier.de/db/conf/spaa/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'B', 'PODC', 'ACM Symposium on Principles of Distributed Computing', 'ACM', 'http://dblp.uni-trier.de/db/conf/podc/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'B', 'CGO', 'The International Symposium on Code Generation and Optimization', 'IEEE/ACM', 'http://dblp.uni-trier.de/db/conf/cgo/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'B', 'DATE', 'Design, Automation & Test in Europe', 'IEEE/ACM', 'http://dblp.uni-trier.de/db/conf/date/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'B', 'ICDCS', 'IEEE International Conference on Distributed Computing Systems', 'IEEE', 'http://dblp.uni-trier.de/db/conf/icdcs/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'B', 'ICPP', 'International Conference on Parallel Processing', 'ACM', 'http://dblp.uni-trier.de/db/conf/icpp/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'B', 'ICS', 'International Conference on Supercomputing', 'ACM', 'http://dblp.uni-trier.de/db/conf/ics/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'B', 'IPDPS', 'IEEE International Parallel & Distributed Processing Symposium', 'IEEE', 'http://dblp.uni-trier.de/db/conf/ipps/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'B', 'HPDC', 'The International ACM Symposium on High-Performance Parallel and Distributed Computing', 'IEEE', 'http://dblp.uni-trier.de/db/conf/hpdc/'),
('conference', '计算机体系结构/并行与分布计算/存储系统', 'B', 'Euro-Par', 'European Conference on Parallel and Distributed Computing', 'Springer', 'http://dblp.uni-trier.de/db/conf/europar/');

-- 计算机网络 - 会议
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('conference', '计算机网络', 'A', 'SIGCOMM', 'ACM International Conference on Applications, Technologies, Architectures, and Protocols for Computer Communication', 'ACM', 'http://dblp.uni-trier.de/db/conf/sigcomm/index.html'),
('conference', '计算机网络', 'A', 'MobiCom', 'ACM International Conference on Mobile Computing and Networking', 'ACM', 'http://dblp.uni-trier.de/db/conf/mobicom/'),
('conference', '计算机网络', 'A', 'INFOCOM', 'IEEE International Conference on Computer Communications', 'IEEE', 'http://dblp.uni-trier.de/db/conf/infocom/'),
('conference', '计算机网络', 'A', 'NSDI', 'Symposium on Network System Design and Implementation', 'USENIX', 'http://dblp.uni-trier.de/db/conf/nsdi/'),
('conference', '计算机网络', 'B', 'SenSys', 'ACM Conference on Embedded Networked Sensor Systems', 'ACM', 'http://dblp.uni-trier.de/db/conf/sensys/'),
('conference', '计算机网络', 'B', 'CoNEXT', 'ACM International Conference on Emerging Networking Experiments and Technologies', 'ACM', 'http://dblp.uni-trier.de/db/conf/conext/'),
('conference', '计算机网络', 'B', 'SECON', 'IEEE International Conference on Sensing, Communication, and Networking', 'IEEE', 'http://dblp.uni-trier.de/db/conf/secon/'),
('conference', '计算机网络', 'B', 'IPSN', 'International Conference on Information Processing in Sensor Networks', 'IEEE/ACM', 'http://dblp.uni-trier.de/db/conf/ipsn/'),
('conference', '计算机网络', 'B', 'MobiSys', 'ACM International Conference on Mobile Systems, Applications, and Services', 'ACM', 'http://dblp.uni-trier.de/db/conf/mobisys/'),
('conference', '计算机网络', 'B', 'ICNP', 'IEEE International Conference on Network Protocols', 'IEEE', 'http://dblp.uni-trier.de/db/conf/icnp/'),
('conference', '计算机网络', 'B', 'MobiHoc', 'International Symposium on Theory, Algorithmic Foundations, and Protocol Design for Mobile Networks and Mobile Computing', 'ACM/IEEE', 'http://dblp.uni-trier.de/db/conf/mobihoc/'),
('conference', '计算机网络', 'B', 'IMC', 'ACM Internet Measurement Conference', 'ACM/USENIX', 'http://dblp.uni-trier.de/db/conf/imc/'),
('conference', '计算机网络', 'C', 'GLOBECOM', 'IEEE Global Communications Conference', 'IEEE', 'http://dblp.uni-trier.de/db/conf/globecom/'),
('conference', '计算机网络', 'C', 'ICC', 'IEEE International Conference on Communications', 'IEEE', 'http://dblp.uni-trier.de/db/conf/icc/'),
('conference', '计算机网络', 'C', 'WCNC', 'IEEE Wireless Communications and Networking Conference', 'IEEE', 'http://dblp.uni-trier.de/db/conf/wcnc/');

-- 网络与信息安全 - 会议
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('conference', '网络与信息安全', 'A', 'CCS', 'ACM Conference on Computer and Communications Security', 'ACM', 'http://dblp.uni-trier.de/db/conf/ccs/'),
('conference', '网络与信息安全', 'A', 'EUROCRYPT', 'International Conference on the Theory and Applications of Cryptographic Techniques', 'Springer', 'http://dblp.uni-trier.de/db/conf/eurocrypt/'),
('conference', '网络与信息安全', 'A', 'S&P', 'IEEE Symposium on Security and Privacy', 'IEEE', 'http://dblp.uni-trier.de/db/conf/sp/'),
('conference', '网络与信息安全', 'A', 'CRYPTO', 'International Cryptology Conference', 'Springer', 'http://dblp.uni-trier.de/db/conf/crypto/'),
('conference', '网络与信息安全', 'A', 'USENIX Security', 'USENIX Security Symposium', 'USENIX', 'http://dblp.uni-trier.de/db/conf/uss/'),
('conference', '网络与信息安全', 'A', 'NDSS', 'Network and Distributed System Security Symposium', 'ISOC', 'http://dblp.uni-trier.de/db/conf/ndss/'),
('conference', '网络与信息安全', 'B', 'ACSAC', 'Annual Computer Security Applications Conference', 'IEEE', 'http://dblp.uni-trier.de/db/conf/acsac/'),
('conference', '网络与信息安全', 'B', 'ASIACRYPT', 'Annual International Conference on the Theory and Application of Cryptology and Information Security', 'Springer', 'http://dblp.uni-trier.de/db/conf/asiacrypt/'),
('conference', '网络与信息安全', 'B', 'ESORICS', 'European Symposium on Research in Computer Security', 'Springer', 'http://dblp.uni-trier.de/db/conf/esorics/'),
('conference', '网络与信息安全', 'B', 'DSN', 'International Conference on Dependable Systems and Networks', 'IEEE/IFIP', 'http://dblp.uni-trier.de/db/conf/dsn/');

-- 软件工程 - 会议
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('conference', '软件工程/系统软件/程序设计语言', 'A', 'PLDI', 'ACM SIGPLAN Conference on Programming Language Design and Implementation', 'ACM', 'http://dblp.uni-trier.de/db/conf/pldi/'),
('conference', '软件工程/系统软件/程序设计语言', 'A', 'POPL', 'ACM SIGPLAN-SIGACT Symposium on Principles of Programming Languages', 'ACM', 'http://dblp.uni-trier.de/db/conf/popl/'),
('conference', '软件工程/系统软件/程序设计语言', 'A', 'FSE', 'ACM International Conference on the Foundations of Software Engineering', 'ACM', 'http://dblp.uni-trier.de/db/conf/sigsoft/'),
('conference', '软件工程/系统软件/程序设计语言', 'A', 'SOSP', 'ACM Symposium on Operating Systems Principles', 'ACM', 'http://dblp.uni-trier.de/db/conf/sosp/'),
('conference', '软件工程/系统软件/程序设计语言', 'A', 'ASE', 'International Conference on Automated Software Engineering', 'IEEE/ACM', 'http://dblp.uni-trier.de/db/conf/kbse/'),
('conference', '软件工程/系统软件/程序设计语言', 'A', 'ICSE', 'International Conference on Software Engineering', 'ACM/IEEE', 'http://dblp.uni-trier.de/db/conf/icse/'),
('conference', '软件工程/系统软件/程序设计语言', 'A', 'ISSTA', 'International Symposium on Software Testing and Analysis', 'ACM', 'http://dblp.uni-trier.de/db/conf/issta/'),
('conference', '软件工程/系统软件/程序设计语言', 'A', 'OSDI', 'USENIX Symposium on Operating Systems Design and Implementation', 'USENIX', 'http://dblp.uni-trier.de/db/conf/osdi/'),
('conference', '软件工程/系统软件/程序设计语言', 'B', 'ECOOP', 'European Conference on Object-Oriented Programming', 'AITO', 'http://dblp.uni-trier.de/db/conf/ecoop/'),
('conference', '软件工程/系统软件/程序设计语言', 'B', 'ICFP', 'ACM SIGPLAN International Conference on Function Programming', 'ACM', 'http://dblp.uni-trier.de/db/conf/icfp/'),
('conference', '软件工程/系统软件/程序设计语言', 'B', 'ICSME', 'International Conference on Software Maintenance and Evolution', 'IEEE', 'http://dblp.uni-trier.de/db/conf/icsm/'),
('conference', '软件工程/系统软件/程序设计语言', 'B', 'SANER', 'IEEE International Conference on Software Analysis, Evolution, and Reengineering', 'IEEE', 'http://dblp.uni-trier.de/db/conf/wcre/'),
('conference', '软件工程/系统软件/程序设计语言', 'B', 'ISSRE', 'IEEE International Symposium on Software Reliability Engineering', 'IEEE', 'http://dblp.uni-trier.de/db/conf/issre/'),
('conference', '软件工程/系统软件/程序设计语言', 'C', 'APSEC', 'Asia-Pacific Software Engineering Conference', 'IEEE', 'http://dblp.uni-trier.de/db/conf/apsec/'),
('conference', '软件工程/系统软件/程序设计语言', 'C', 'ICST', 'IEEE International Conference on Software Testing, Verification and Validation', 'IEEE', 'http://dblp.uni-trier.de/db/conf/icst/'),
('conference', '软件工程/系统软件/程序设计语言', 'C', 'COMPSAC', 'International Computer Software and Applications Conference', 'IEEE', 'http://dblp.uni-trier.de/db/conf/compsac/'),
('conference', '软件工程/系统软件/程序设计语言', 'C', 'QRS', 'International Conference on Software Quality, Reliability and Security', 'IEEE', 'https://dblp.uni-trier.de/db/conf/qrs/');

-- 数据库/数据挖掘 - 会议
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('conference', '数据库/数据挖掘/内容检索', 'A', 'SIGMOD', 'ACM SIGMOD Conference', 'ACM', 'http://dblp.uni-trier.de/db/conf/sigmod/'),
('conference', '数据库/数据挖掘/内容检索', 'A', 'SIGKDD', 'ACM SIGKDD Conference on Knowledge Discovery and Data Mining', 'ACM', 'http://dblp.uni-trier.de/db/conf/kdd/'),
('conference', '数据库/数据挖掘/内容检索', 'A', 'ICDE', 'IEEE International Conference on Data Engineering', 'IEEE', 'http://dblp.uni-trier.de/db/conf/icde/'),
('conference', '数据库/数据挖掘/内容检索', 'A', 'SIGIR', 'International ACM SIGIR Conference on Research and Development in Information Retrieval', 'ACM', 'http://dblp.uni-trier.de/db/conf/sigir/'),
('conference', '数据库/数据挖掘/内容检索', 'A', 'VLDB', 'International Conference on Very Large Data Bases', 'Morgan Kaufmann/ACM', 'http://dblp.uni-trier.de/db/conf/vldb/'),
('conference', '数据库/数据挖掘/内容检索', 'B', 'CIKM', 'ACM International Conference on Information and Knowledge Management', 'ACM', 'http://dblp.uni-trier.de/db/conf/cikm/'),
('conference', '数据库/数据挖掘/内容检索', 'B', 'WSDM', 'ACM International Conference on Web Search and Data Mining', 'ACM', 'http://dblp.uni-trier.de/db/conf/wsdm/'),
('conference', '数据库/数据挖掘/内容检索', 'B', 'ICDM', 'IEEE International Conference on Data Mining', 'IEEE', 'http://dblp.uni-trier.de/db/conf/icdm/'),
('conference', '数据库/数据挖掘/内容检索', 'B', 'RecSys', 'ACM Conference on Recommender Systems', 'ACM', 'https://dblp.org/db/conf/recsys/index.html');

-- 计算机科学理论 - 会议
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('conference', '计算机科学理论', 'A', 'STOC', 'ACM Symposium on the Theory of Computing', 'ACM', 'http://dblp.uni-trier.de/db/conf/stoc/'),
('conference', '计算机科学理论', 'A', 'SODA', 'ACM-SIAM Symposium on Discrete Algorithms', 'SIAM', 'http://dblp.uni-trier.de/db/conf/soda/'),
('conference', '计算机科学理论', 'A', 'CAV', 'International Conference on Computer Aided Verification', 'Springer', 'http://dblp.uni-trier.de/db/conf/cav/'),
('conference', '计算机科学理论', 'A', 'FOCS', 'IEEE Annual Symposium on Foundations of Computer Science', 'IEEE', 'http://dblp.uni-trier.de/db/conf/focs/'),
('conference', '计算机科学理论', 'A', 'LICS', 'ACM/IEEE Symposium on Logic in Computer Science', 'IEEE', 'http://dblp.uni-trier.de/db/conf/lics/'),
('conference', '计算机科学理论', 'B', 'ESA', 'European Symposium on Algorithms', 'Springer', 'http://dblp.uni-trier.de/db/conf/esa/'),
('conference', '计算机科学理论', 'B', 'ICALP', 'International Colloquium on Automata, Languages and Programming', 'Springer', 'http://dblp.uni-trier.de/db/conf/icalp/'),
('conference', '计算机科学理论', 'B', 'SAT', 'International Conference on Theory and Applications of Satisfiability Testing', 'Springer', 'http://dblp.uni-trier.de/db/conf/sat/'),
('conference', '计算机科学理论', 'C', 'ISAAC', 'International Symposium on Algorithms and Computation', 'Springer', 'http://dblp.uni-trier.de/db/conf/isaac/'),
('conference', '计算机科学理论', 'C', 'MFCS', 'International Conference on Mathematical Foundations of Computer Science', 'Springer', 'http://dblp.uni-trier.de/db/conf/mfcs/');

-- 计算机图形学与多媒体 - 会议
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('conference', '计算机图形学与多媒体', 'A', 'ACM MM', 'ACM International Conference on Multimedia', 'ACM', 'http://dblp.uni-trier.de/db/conf/mm/'),
('conference', '计算机图形学与多媒体', 'A', 'SIGGRAPH', 'ACM Special Interest Group on Computer Graphics', 'ACM', 'http://dblp.uni-trier.de/db/conf/siggraph/index.html'),
('conference', '计算机图形学与多媒体', 'A', 'VR', 'IEEE Virtual Reality', 'IEEE', 'http://dblp.uni-trier.de/db/conf/vr/'),
('conference', '计算机图形学与多媒体', 'A', 'IEEE VIS', 'IEEE Visualization Conference', 'IEEE', 'http://dblp.uni-trier.de/db/conf/visualization/index.html'),
('conference', '计算机图形学与多媒体', 'B', 'ICMR', 'ACM SIGMM International Conference on Multimedia Retrieval', 'ACM', 'http://dblp.uni-trier.de/db/conf/mir/'),
('conference', '计算机图形学与多媒体', 'B', 'ICASSP', 'IEEE International Conference on Acoustics, Speech and Signal Processing', 'IEEE', 'http://dblp.uni-trier.de/db/conf/icassp/'),
('conference', '计算机图形学与多媒体', 'B', 'ICME', 'IEEE International Conference on Multimedia & Expo', 'IEEE', 'http://dblp.uni-trier.de/db/conf/icmcs/'),
('conference', '计算机图形学与多媒体', 'C', 'ICIP', 'IEEE International Conference on Image Processing', 'IEEE', 'http://dblp.uni-trier.de/db/conf/icip/'),
('conference', '计算机图形学与多媒体', 'C', 'MMM', 'International Conference on Multimedia Modeling', 'Springer', 'http://dblp.uni-trier.de/db/conf/mmm/');
-- CCF 目录补充：人工智能、人机交互与普适计算、交叉/综合/新兴

-- 人工智能 - 期刊
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('journal', '人工智能', 'A', 'AI', 'Artificial Intelligence', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/ai/'),
('journal', '人工智能', 'A', 'TPAMI', 'IEEE Transactions on Pattern Analysis and Machine Intelligence', 'IEEE', 'http://dblp.uni-trier.de/db/journals/pami/'),
('journal', '人工智能', 'A', 'IJCV', 'International Journal of Computer Vision', 'Springer', 'http://dblp.uni-trier.de/db/journals/ijcv/'),
('journal', '人工智能', 'A', 'JMLR', 'Journal of Machine Learning Research', 'MIT Press', 'http://dblp.uni-trier.de/db/journals/jmlr/'),
('journal', '人工智能', 'B', 'TAP', 'ACM Transactions on Applied Perception', 'ACM', 'http://dblp.uni-trier.de/db/journals/tap/'),
('journal', '人工智能', 'B', 'TALLIP', 'ACM Transactions on Asian and Low-Resource Language Information Processing', 'ACM', 'http://dblp.uni-trier.de/db/journals/talip/'),
('journal', '人工智能', 'B', 'TASLP', 'IEEE/ACM Transactions on Audio, Speech and Language Processing', 'IEEE/ACM', 'http://dblp.uni-trier.de/db/journals/tasl/'),
('journal', '人工智能', 'B', 'Machine Learning', 'Machine Learning', 'Springer', 'http://dblp.uni-trier.de/db/journals/ml/'),
('journal', '人工智能', 'B', 'Neural Networks', 'Neural Networks', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/nn/'),
('journal', '人工智能', 'B', 'PR', 'Pattern Recognition', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/pr/'),
('journal', '人工智能', 'C', 'TACL', 'Transactions of the Association for Computational Linguistics', 'ACL', 'https://dblp.org/db/journals/tacl/index.html'),
('journal', '人工智能', 'C', 'AI Magazine', 'AI Magazine', 'AAAI', 'http://dblp.uni-trier.de/db/journals/aim/'),
('journal', '人工智能', 'C', 'AMAI', 'Annals of Mathematics and Artificial Intelligence', 'Springer', 'http://dblp.uni-trier.de/db/journals/amai/'),
('journal', '人工智能', 'C', 'AAMAS', 'Autonomous Agents and Multi-Agent Systems', 'Springer', 'http://dblp.uni-trier.de/db/journals/aamas/'),
('journal', '人工智能', 'C', 'CVIU', 'Computer Vision and Image Understanding', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/cviu/'),
('journal', '人工智能', 'C', 'DKE', 'Data & Knowledge Engineering', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/dke/'),
('journal', '人工智能', 'C', 'ECJ', 'Evolutionary Computation', 'MIT Press', 'http://dblp.uni-trier.de/db/journals/ec/'),
('journal', '人工智能', 'C', 'IEEE Intelligent Systems', 'IEEE Intelligent Systems', 'IEEE', 'http://dblp.uni-trier.de/db/journals/expert/'),
('journal', '人工智能', 'C', 'JAIR', 'Journal of Artificial Intelligence Research', 'AAAI', 'http://dblp.uni-trier.de/db/journals/jair/'),
('journal', '人工智能', 'C', 'KBS', 'Knowledge-Based Systems', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/kbs/'),
('journal', '人工智能', 'C', 'NLE', 'Natural Language Engineering', 'Cambridge University Press', 'http://dblp.uni-trier.de/db/journals/nle/'),
('journal', '人工智能', 'C', 'NC', 'Neurocomputing', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/ijon/'),
('journal', '人工智能', 'C', 'NPL', 'Neural Processing Letters', 'Springer', 'http://dblp.uni-trier.de/db/journals/npl/');

-- 人工智能 - 会议
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('conference', '人工智能', 'A', 'AAAI', 'AAAI Conference on Artificial Intelligence', 'AAAI', 'http://dblp.uni-trier.de/db/conf/aaai/'),
('conference', '人工智能', 'A', 'NeurIPS', 'Annual Conference on Neural Information Processing Systems', 'MIT Press', 'http://dblp.uni-trier.de/db/conf/nips/'),
('conference', '人工智能', 'A', 'ACL', 'Annual Meeting of the Association for Computational Linguistics', 'ACL', 'http://dblp.uni-trier.de/db/conf/acl/'),
('conference', '人工智能', 'A', 'CVPR', 'IEEE Conference on Computer Vision and Pattern Recognition', 'IEEE', 'http://dblp.uni-trier.de/db/conf/cvpr/'),
('conference', '人工智能', 'A', 'ICCV', 'International Conference on Computer Vision', 'IEEE', 'http://dblp.uni-trier.de/db/conf/iccv/'),
('conference', '人工智能', 'A', 'ICML', 'International Conference on Machine Learning', 'ACM', 'http://dblp.uni-trier.de/db/conf/icml/'),
('conference', '人工智能', 'A', 'IJCAI', 'International Joint Conference on Artificial Intelligence', 'Morgan Kaufmann', 'http://dblp.uni-trier.de/db/conf/ijcai/'),
('conference', '人工智能', 'B', 'COLT', 'Annual Conference on Computational Learning Theory', 'Springer', 'http://dblp.uni-trier.de/db/conf/colt/'),
('conference', '人工智能', 'B', 'EMNLP', 'Conference on Empirical Methods in Natural Language Processing', 'ACL', 'http://dblp.uni-trier.de/db/conf/emnlp/'),
('conference', '人工智能', 'B', 'ECAI', 'European Conference on Artificial Intelligence', 'IOS Press', 'http://dblp.uni-trier.de/db/conf/ecai/'),
('conference', '人工智能', 'B', 'ECCV', 'European Conference on Computer Vision', 'Springer', 'http://dblp.uni-trier.de/db/conf/eccv/'),
('conference', '人工智能', 'B', 'ICRA', 'IEEE International Conference on Robotics and Automation', 'IEEE', 'http://dblp.uni-trier.de/db/conf/icra/'),
('conference', '人工智能', 'B', 'ICAPS', 'International Conference on Automated Planning and Scheduling', 'AAAI', 'http://dblp.uni-trier.de/db/conf/icaps/'),
('conference', '人工智能', 'B', 'NAACL', 'Annual Conference of the North American Chapter of the ACL', 'ACL', 'http://dblp.uni-trier.de/db/conf/naacl/'),
('conference', '人工智能', 'B', 'UAI', 'Conference on Uncertainty in Artificial Intelligence', 'AUAI', 'http://dblp.uni-trier.de/db/conf/uai/'),
('conference', '人工智能', 'B', 'AAMAS', 'International Conference on Autonomous Agents and Multiagent Systems', 'IFAAMAS', 'http://dblp.uni-trier.de/db/conf/atal/'),
('conference', '人工智能', 'C', 'AISTATS', 'International Conference on Artificial Intelligence and Statistics', 'JMLR', 'http://dblp.uni-trier.de/db/conf/aistats/'),
('conference', '人工智能', 'C', 'ACCV', 'Asian Conference on Computer Vision', 'Springer', 'http://dblp.uni-trier.de/db/conf/accv/'),
('conference', '人工智能', 'C', 'CoNLL', 'Conference on Computational Natural Language Learning', 'ACL', 'http://dblp.uni-trier.de/db/conf/conll/'),
('conference', '人工智能', 'C', 'ICANN', 'International Conference on Artificial Neural Networks', 'Springer', 'http://dblp.uni-trier.de/db/conf/icann/'),
('conference', '人工智能', 'C', 'ICDAR', 'International Conference on Document Analysis and Recognition', 'IEEE', 'http://dblp.uni-trier.de/db/conf/icdar/'),
('conference', '人工智能', 'C', 'IJCNN', 'International Joint Conference on Neural Networks', 'IEEE', 'http://dblp.uni-trier.de/db/conf/ijcnn/'),
('conference', '人工智能', 'C', 'PRICAI', 'Pacific Rim International Conference on Artificial Intelligence', 'Springer', 'http://dblp.uni-trier.de/db/conf/pricai/');

-- 人机交互与普适计算 - 期刊
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('journal', '人机交互与普适计算', 'A', 'TOCHI', 'ACM Transactions on Computer-Human Interaction', 'ACM', 'http://dblp.uni-trier.de/db/journals/tochi/'),
('journal', '人机交互与普适计算', 'A', 'IJHCS', 'International Journal of Human-Computer Studies', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/ijmms/'),
('journal', '人机交互与普适计算', 'B', 'CSCW', 'Computer Supported Cooperative Work', 'Springer', 'http://dblp.uni-trier.de/db/journals/cscw/'),
('journal', '人机交互与普适计算', 'B', 'HCI', 'Human-Computer Interaction', 'Taylor & Francis', 'http://dblp.uni-trier.de/db/journals/hhci/'),
('journal', '人机交互与普适计算', 'B', 'IWC', 'Interacting with Computers', 'Oxford University Press', 'http://dblp.uni-trier.de/db/journals/iwc/'),
('journal', '人机交互与普适计算', 'B', 'UMUAI', 'User Modeling and User-Adapted Interaction', 'Springer', 'http://dblp.uni-trier.de/db/journals/umuai/'),
('journal', '人机交互与普适计算', 'C', 'PUC', 'Personal and Ubiquitous Computing', 'Springer', 'http://dblp.uni-trier.de/db/journals/puc/'),
('journal', '人机交互与普适计算', 'C', 'BIT', 'Behaviour & Information Technology', 'Taylor & Francis', 'http://dblp.uni-trier.de/db/journals/bit/');

-- 人机交互与普适计算 - 会议
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('conference', '人机交互与普适计算', 'A', 'CHI', 'ACM Conference on Human Factors in Computing Systems', 'ACM', 'http://dblp.uni-trier.de/db/conf/chi/'),
('conference', '人机交互与普适计算', 'A', 'CSCW', 'ACM Conference on Computer Supported Cooperative Work and Social Computing', 'ACM', 'http://dblp.uni-trier.de/db/conf/cscw/'),
('conference', '人机交互与普适计算', 'A', 'UbiComp', 'ACM International Joint Conference on Pervasive and Ubiquitous Computing', 'ACM', 'http://dblp.uni-trier.de/db/conf/huc/'),
('conference', '人机交互与普适计算', 'B', 'MobileHCI', 'International Conference on Human-Computer Interaction with Mobile Devices and Services', 'ACM', 'http://dblp.uni-trier.de/db/conf/mhci/'),
('conference', '人机交互与普适计算', 'B', 'IUI', 'ACM International Conference on Intelligent User Interfaces', 'ACM', 'http://dblp.uni-trier.de/db/conf/iui/'),
('conference', '人机交互与普适计算', 'B', 'ITS', 'ACM International Conference on Interactive Tabletops and Surfaces', 'ACM', 'http://dblp.uni-trier.de/db/conf/tabletop/'),
('conference', '人机交互与普适计算', 'B', 'GROUP', 'ACM International Conference on Supporting Group Work', 'ACM', 'http://dblp.uni-trier.de/db/conf/group/'),
('conference', '人机交互与普适计算', 'C', 'DIS', 'ACM Conference on Designing Interactive Systems', 'ACM', 'http://dblp.uni-trier.de/db/conf/ACMdis/'),
('conference', '人机交互与普适计算', 'C', 'INTERACT', 'IFIP Conference on Human-Computer Interaction', 'Springer', 'http://dblp.uni-trier.de/db/conf/interact/');

-- 交叉/综合/新兴 - 期刊
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('journal', '交叉/综合/新兴', 'A', 'JACM', 'Journal of the ACM', 'ACM', 'http://dblp.uni-trier.de/db/journals/jacm/'),
('journal', '交叉/综合/新兴', 'A', 'Proc. IEEE', 'Proceedings of the IEEE', 'IEEE', 'http://dblp.uni-trier.de/db/journals/pieee/'),
('journal', '交叉/综合/新兴', 'B', 'Bioinformatics', 'Bioinformatics', 'Oxford University Press', 'http://dblp.uni-trier.de/db/journals/bioinformatics/'),
('journal', '交叉/综合/新兴', 'B', 'TBIOM', 'IEEE Transactions on Biometrics, Behavior, and Identity Science', 'IEEE', 'https://dblp.org/db/journals/tbbis/index.html'),
('journal', '交叉/综合/新兴', 'B', 'TCBB', 'IEEE/ACM Transactions on Computational Biology and Bioinformatics', 'IEEE/ACM', 'http://dblp.uni-trier.de/db/journals/tcbb/'),
('journal', '交叉/综合/新兴', 'B', 'JCST', 'Journal of Computer Science and Technology', 'Springer', 'http://dblp.uni-trier.de/db/journals/jcst/'),
('journal', '交叉/综合/新兴', 'B', 'JPDC', 'Journal of Parallel and Distributed Computing', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/jpdc/'),
('journal', '交叉/综合/新兴', 'C', 'C&G', 'Computers & Graphics', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/cg/'),
('journal', '交叉/综合/新兴', 'C', 'FITEE', 'Frontiers of Information Technology & Electronic Engineering', 'Springer', 'https://dblp.org/db/journals/fitee/index.html'),
('journal', '交叉/综合/新兴', 'C', 'IET-CTA', 'IET Control Theory & Applications', 'IET', 'http://dblp.uni-trier.de/db/journals/iet-cta/'),
('journal', '交叉/综合/新兴', 'C', 'JBI', 'Journal of Biomedical Informatics', 'Elsevier', 'http://dblp.uni-trier.de/db/journals/jbi/'),
('journal', '交叉/综合/新兴', 'C', 'TC', 'IEEE Transactions on Computers', 'IEEE', 'http://dblp.uni-trier.de/db/journals/tc/index.html');

-- 交叉/综合/新兴 - 会议
INSERT INTO ccf_venue (venue_type, area, level, abbreviation, full_name, publisher, url) VALUES
('conference', '交叉/综合/新兴', 'A', 'WWW', 'International World Wide Web Conference', 'ACM', 'http://dblp.uni-trier.de/db/conf/www/'),
('conference', '交叉/综合/新兴', 'A', 'RTSS', 'IEEE Real-Time Systems Symposium', 'IEEE', 'http://dblp.uni-trier.de/db/conf/rtss/'),
('conference', '交叉/综合/新兴', 'B', 'EMSOFT', 'International Conference on Embedded Software', 'ACM/IEEE', 'http://dblp.uni-trier.de/db/conf/emsoft/'),
('conference', '交叉/综合/新兴', 'B', 'ISMIR', 'International Society for Music Information Retrieval Conference', 'ISMIR', 'http://dblp.uni-trier.de/db/conf/ismir/'),
('conference', '交叉/综合/新兴', 'B', 'ISMB', 'International Conference on Intelligent Systems for Molecular Biology', 'ISCB', 'http://dblp.uni-trier.de/db/conf/ismb/'),
('conference', '交叉/综合/新兴', 'C', 'AMIA', 'American Medical Informatics Association Annual Symposium', 'AMIA', 'http://dblp.uni-trier.de/db/conf/amia/'),
('conference', '交叉/综合/新兴', 'C', 'SIGSPATIAL', 'ACM SIGSPATIAL International Conference on Advances in Geographic Information Systems', 'ACM', 'http://dblp.uni-trier.de/db/conf/gis/'),
('conference', '交叉/综合/新兴', 'C', 'GECCO', 'Genetic and Evolutionary Computation Conference', 'ACM', 'http://dblp.uni-trier.de/db/conf/gecco/');
