package io.polaris.builder.code;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.polaris.core.string.Strings;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Qt
 * @since  Oct 23, 2023
 */
public class SqlGen {


	@Test
	void test01() {
		String str = "客户渠道域\tA.客户渠道域\n" +
			"外部接口域\tB.外部接口域\n" +
			"员工渠道域\tC.员工渠道域\n" +
			"参与人管理域\tD.参与人管理域\n" +
			"信贷审批域\tE.信贷审批域\n" +
			"催收域\tF.催收域\n" +
			"公共支持域\tG.公共支持域\n" +
			"账务核算域\tH.账务核算域\n" +
			"清结算域\tI.清结算域\n" +
			"资金管理域\tJ.资金管理域\n" +
			"办公决策管理域\tK.办公决策管理域\n" +
			"监管报送域\tL.监管报送域\n" +
			"IT工具域\tM.IT工具域\n" +
			"终端安全域\tN.终端安全域";

		String[] arr = str.trim().split("\n");
		for (String s : arr) {
			String[] fs = s.trim().split("\t");
			System.out.println(Strings.format("delete from srm_arch_system_domain where domain_name = '{}';", fs[0].trim()));
			String sql = Strings.format("insert into srm_arch_system_domain" +
				"(domain_name, domain_desc, sort_key, crt_user, crt_dt, upt_user, upt_dt)\n" +
				"values('{}', '{}', '{}', 0, sysdate, 0, sysdate" +
				");", fs[0].trim(), fs[1].trim(), fs[1].trim().substring(0, 1));
			System.out.println(sql);
		}
	}

	@Test
	void test02() {
		String str = "客户渠道域\t“借蛙”/“晋享钱包”公众号\tcommon-h5\tA327\t渠道类\n" +
			"客户渠道域\t“晋商消费金融”公众号\tcwx\tA320\t渠道类\n" +
			"客户渠道域\t全渠道客户服务系统\tcall\tA305\t渠道类\n" +
			"客户渠道域\t客户运营系统\tcust_oss\tA424\t业务发展类\n" +
			"客户渠道域\t晋享分期H5站点\tjxfq\tA334\t渠道类\n" +
			"客户渠道域\t晋商消费金融APP\tjxqb\tA422\t渠道类\n" +
			"客户渠道域\t智能客服机器人系统\tBOT\tA328\t业务发展类\n" +
			"客户渠道域\t门户网站\tjcfc-web\tA303\t渠道类\n" +
			"\t\t\t\t\n" +
			"外部接口域\tOPEN API服务平台\tiLoan\tA403\t渠道类\n" +
			"外部接口域\t导流平台\tiloan_diversion\tA336\t渠道类\n" +
			"\t\t\t\t\n" +
			"员工渠道域\t“晋件助手”微信小程序\tjjzs\tA335\t渠道类\n" +
			"员工渠道域\t“晋商消费商户助手”公众号\tmhelper\tA318\t渠道类\n" +
			"员工渠道域\t晋情学（APP）\ttrain\tC406\t经营管理类\n" +
			"员工渠道域\t晋情贷H5\tjqd-h5\tA337\t渠道类\n" +
			"\t\t\t\t\n" +
			"参与人管理域\t商户管理系统\tmerchant\tA308\t经营管理类\n" +
			"参与人管理域\t客户中心系统\tcust_app\tA402\t业务发展类\n" +
			"参与人管理域\t客户营销系统\tcust_market\tA418\t业务发展类\n" +
			"\t\t\t\t\n" +
			"信贷审批域\t三方征信管理系统\ttcms\tA416\t业务发展类\n" +
			"信贷审批域\t三方数据平台\t3rdata\tA307\t业务发展类\n" +
			"信贷审批域\t二代征信前置查询系统\tcredit\tA329\t业务发展类\n" +
			"信贷审批域\t信贷审批系统\tcmis\tA101\t业务发展类\n" +
			"信贷审批域\t信贷风险决策系统\trisk\tA332\t业务发展类\n" +
			"信贷审批域\t决策引擎平台\tdes\tA423\t业务发展类\n" +
			"信贷审批域\t押品权证管理系统\tcmis_guaranty\tA425\t业务发展类\n" +
			"信贷审批域\t额度系统\tcmis_lmt\tA106\t业务发展类\n" +
			"\t\t\t\t\n" +
			"催收域\t三方电子存证及网络仲裁\tevidence\tA309\t业务发展类\n" +
			"催收域\t催收手机项目\tjiling\tA338\t经营管理类\n" +
			"催收域\t催收系统\tcollection\tA105\t业务发展类\n" +
			"催收域\t掌中收（APP）\tzzs\tC301\t渠道类\n" +
			"\t\t\t\t\n" +
			"公共支持域\t人脸识别系统\tfacechek\tA315\t工具支撑类\n" +
			"公共支持域\t企业级影像平台\tEOMP\tA415\t工具支撑类\n" +
			"公共支持域\t公共基础服务系统\tcomm_services\tA420\t工具支撑类\n" +
			"公共支持域\t征信衍生变量管理服务\tzxdv\tA428\t经营管理类\n" +
			"公共支持域\t消息中心系统\tmsg\tA405\t业务发展类\n" +
			"公共支持域\t用户角色权限管理系统\tbasesv\tA409\t业务发展类\n" +
			"公共支持域\t电子签章系统\tca\tA404\t工具支撑类\n" +
			"公共支持域\t短信平台\tmessage\tA301\t工具支撑类\n" +
			"公共支持域\t综合影像系统\tsys_image\tA426\t经营管理类\n" +
			"\t\t\t\t\n" +
			"账务核算域\t会计引擎系统\tfas\tA210\t业务发展类\n" +
			"账务核算域\t合作机构借据管理\tcals\tA429\t经营管理类\n" +
			"账务核算域\t核算产品中心\tloans_repay_product\tA427\t业务发展类\n" +
			"账务核算域\t现金管理平台\tcmp\tA216\t经营管理类\n" +
			"账务核算域\t联合贷业务系统\tycloans-syndicated\tA208\t业务发展类\n" +
			"账务核算域\t计算税值系统\tctvs\tA214\t业务发展类\n" +
			"账务核算域\t账务核算系统\tycloan\tA102\t业务发展类\n" +
			"账务核算域\t账户系统\tfeas\tA213\t业务发展类\n" +
			"\t\t\t\t\n" +
			"清结算域\t对账系统\ttcheck\tC417\t经营管理类\n" +
			"清结算域\t支付平台\tpay\tA203\t业务发展类\n" +
			"清结算域\t结算系统\tsettle\tA204\t业务发展类\n" +
			"\t\t\t\t\n" +
			"资金管理域\t同业业务运营系统\tibls\tA215\t业务发展类\n" +
			"资金管理域\t成本收益系统\tcbms\tB418\t经营管理类\n" +
			"资金管理域\t资产证券化系统\tABS\tA202\t业务发展类\n" +
			"资金管理域\t退费计算系统\trems\tA217\t业务发展类\n" +
			"\t\t\t\t\n" +
			"办公决策管理域\tBI报表系统\tBIQC\tB408\t经营管理类\n" +
			"办公决策管理域\t人力资源管理系统\thr\tC401\t经营管理类\n" +
			"办公决策管理域\t企业邮箱系统\tmail\tB402\t工具支撑类\n" +
			"办公决策管理域\t协同办公系统-OA\toa\tB401\t经营管理类\n" +
			"办公决策管理域\t数据仓库\tdata-wh\tA406\t工具支撑类\n" +
			"办公决策管理域\t绩效管理系统\tpms\tB201\t经营管理类\n" +
			"办公决策管理域\t营改增系统\ttax\tA407\t经营管理类\n" +
			"办公决策管理域\t视频会议系统\tvc\tC402\t工具支撑类\n" +
			"办公决策管理域\t财务管理系统\tfinance\tA201\t经营管理类\n" +
			"办公决策管理域\t贷后管理平台\tafterloan\tA205\t经营管理类\n" +
			"办公决策管理域\t非现场审计系统\tautids\tC405\t经营管理类\n" +
			"\t\t\t\t\n" +
			"监管报送域\t1104监管报送系统\tCBRC1104\tB407\t经营管理类\n" +
			"监管报送域\t一代征信采集报送系统\tcredit\tB417\t经营管理类\n" +
			"监管报送域\t互金协会报送系统\tnifa\tB419\t经营管理类\n" +
			"监管报送域\t人民银行利率监测报备报送系统（IMAS）\timas\tB415\t经营管理类\n" +
			"监管报送域\t人行大集中报送系统\tdjz\tB420\t经营管理类\n" +
			"监管报送域\t反洗钱系统\tFCTF\tC407\t经营管理类\n" +
			"监管报送域\t大集中监管报送\tSupervision\tB405\t经营管理类\n" +
			"监管报送域\t监管数据标准化报送系统（EAST）\teast\tB414\t经营管理类\n" +
			"监管报送域\t金融基础数据报送系统\tfinreport\tB416\t经营管理类\n" +
			"\t\t\t\t\n" +
			"IT工具域\tIT资产自动化运维平台\tdevops\tC415\t工具支撑类\n" +
			"IT工具域\tIT运维堡垒机系统\tjumpserver\tA412\t工具支撑类\n" +
			"IT工具域\tZABBIX监控系统\tzabbix\tC403\t工具支撑类\n" +
			"IT工具域\t企业级业务监控系统\tbmp\tC420\t工具支撑类\n" +
			"IT工具域\t元数据管理系统\tMateOne\tC408\t工具支撑类\n" +
			"IT工具域\t分布式应用日志采集分析系统\telk\tB409\t工具支撑类\n" +
			"IT工具域\t分布式文件存储系统\tfastdfs\tA410\t工具支撑类\n" +
			"IT工具域\t告警平台\talarm\tC419\t工具支撑类\n" +
			"IT工具域\t微服务管理平台\tmmp\tC404\t工具支撑类\n" +
			"IT工具域\t知识库系统(wiki)\twiki\tC413\t工具支撑类\n" +
			"IT工具域\t统一任务调度平台\tUSE\tA421\t工具支撑类\n" +
			"IT工具域\t网络管理系统\tNMS\tB404\t工具支撑类\n" +
			"\t\t\t\t\n" +
			"终端安全域\tDLP防泄密系统\tDLP\tA411\t工具支撑类\n" +
			"终端安全域\t终端安全管控系统\t360 Optimus\tA414\t工具支撑类";

		Set<String> set = new HashSet<>();
		for (String s : str.split("\n")) {
			s = s.trim();
			String[] fs = s.split("\t");
			if (fs.length != 5 || fs[3].trim().length() != 4) {
				continue;
			}
			System.out.println(Strings.format("delete from srm_arch_system where sys_code = '{}';", fs[3].trim()));
			String sql = Strings.format("insert into srm_arch_system(sys_code, sys_name, sys_desc, domain_name, category_name, intro, crt_user, crt_dt, upt_user, upt_dt)\n" +
					"values('{}', '{}', '{}', '{}', '{}', '', 0, sysdate, 0, sysdate);\n", fs[3].trim(), fs[2].trim(), fs[1].trim(),
				fs[0].trim(), fs[4].trim());
			System.out.println(sql);
			set.add(fs[4].trim());
		}

		for (String s : set) {
			System.out.println(Strings.format("delete from srm_arch_system_category where category_name = '{}';", s));
			String sql = Strings.format("insert into srm_arch_system_category (category_name, category_desc, intro, crt_user, crt_dt, upt_user, upt_dt)\n" +
				"values('{}', '{}', '', 0, sysdate, 0, sysdate);\n", s, s);
			System.out.println(sql);
		}
	}


	@Test
	void test03() {
		String str = "\n" +
			"1104监管报送系统\tcbrc1104_app\tB4070001\n" +
			"1104监管报送系统页面\tcbrc1104-op\tB4070002\n" +
			"高管短信汇报\t高管短信汇报\tB4080001\n" +
			"文件解析服务\tBIanlys\tB4080005\n" +
			"BI分析\tFineBI\tB4080002\n" +
			"大屏\tjcfc-BIScreen-server\tB4080004\n" +
			"报表系统FineReport\tjcfc-BIReport-server\tB4080003\n" +
			"大数据平台\t大数据平台\tA3280205\n" +
			"产品系统\t产品系统\tA3280204\n" +
			"产品管理系统\t产品管理系统\tA3280203\n" +
			"在线接入服务\tacc\tA3280201\n" +
			"在线计算服务\tcalc\tA3280006\n" +
			"oss配置系统\toss-config\tA3280202\n" +
			"manage\tmanage\tA3280103\n" +
			"文件服务\t文件服务器\tA3280105\n" +
			"im-java\tim-java\tA3280102\n" +
			"在线通讯服务\tbackend\tA3280101\n" +
			"wechat通信服务\twechat\tA3280104\n" +
			"esafenet\tesafenet\tA4110001\n" +
			"devops\tdevops\tC4150001\n" +
			"前置机\tqianzhiji\tA4120002\n" +
			"jumpserver\tjumpserver\tA4120001\n" +
			"流控控制台\tsentinel_dashboard\tA4030009\n" +
			"在线服务API(老在线)\tiloan\tA4030003\n" +
			"在线调度平台\tiloan_job\tA4030005\n" +
			"在线鉴权\tiloan_auth\tA4030006\n" +
			"自营对接平台\tiloan_ownchannel\tA4030012\n" +
			"机构网关\tiloan_channel_gateway\tB4030008\n" +
			"在线联合贷\tiloan_joint\tA4030004\n" +
			"自营网关\tiloan_own_gateway\tA4030013\n" +
			"在线后管服务端\tiloan_mgr\tA4030002\n" +
			"配置中心nacos\tnacos\tA4030014\n" +
			"流控服务端\tsentinel_server\tA4030010\n" +
			"维信曝光归因平台\tiloan_operation\tA4030011\n" +
			"在线后管前端\tiloan-op\tA4030001\n" +
			"机构对接平台\tiloan_channel\tA4030007\n" +
			"zabbix\tzabbix\tC4030001\n" +
			"监控插件Oracle\tZabbix_Plugin_Oracle\tC4030003\n" +
			"监控插件ClickHouse\tZabbix_Plugin_ClickHouse\tC4030002\n" +
			"通用H5\tcommonH5\tA3270001\n" +
			"晋件助手\tjjzs\tA3350001\n" +
			"晋商消费商户助手\tmhelper\tA3180001\n" +
			"官微\tcompany_wx\tA3200001\n" +
			"微信后管\tcmp\tA3200002\n" +
			"征信质量前置\tCQMS\tB4170002\n" +
			"人行征信报送\tZXPT\tB4170001\n" +
			"征信白盒（威豆）\torgcredit_baihe\tA4160003\n" +
			"征信白盒（维信）\tcredit_white_box\tA4160002\n" +
			"征信特征管理（八音盒）\tmusic_box\tA4160001\n" +
			"融合分（维信）\tFuseScore\tA4160005\n" +
			"征信报告\tcreditReport\tA4160004\n" +
			"执行中心前置\tdmp-service-web\tA3070004\n" +
			"管理平台\tdmp-admin-web\tA3070007\n" +
			"消息队列服务\tdmp_service_mq\tA3070006\n" +
			"客户平台\tdmp-user-web\tA3070003\n" +
			"任务调度\tdmp-scheduler\tA3070002\n" +
			"人行征信变量加工服务\tdmp_service_processing\tA3070001\n" +
			"执行中心服务\tdmp_service_provider\tA3070005\n" +
			"区块链节点\t区块链\tA3090003\n" +
			"存证管理端\teviapp-admin\tA3090002\n" +
			"存证服务端\teviapp\tA3090001\n" +
			"二代征信前置管理系统\tkcqs\tA3290001\n" +
			"签名验签服务\t签名验签服务\tA3290002\n" +
			"互金协会报送前端\tnifa-op\tB4190002\n" +
			"互金协会报送应用\tnifa_app\tB4190001\n" +
			"人力系统(hr)\t人力系统\tC4010001\n" +
			"人力云\t人力云\tC4010002\n" +
			"imas前端\timas-op\tB4150002\n" +
			"imas利率报送\timas_app\tB4150001\n" +
			"唇语识别\tlip\tA3150002\n" +
			"人脸识别主应用\tfacerec\tA3150001\n" +
			"人行大集中报送系统前端应用\tdjz-op\tB4200002\n" +
			"人行大集中报送系统应用\tdjz_app\tB4200001\n" +
			"企业业务监控前台页面\tbmp_front\tC4200001\n" +
			"企业业务监控后台\tbmp_core\tC4200002\n" +
			"影像管理平台\tSunECM\tA4150002\n" +
			"OCR识别服务\tSunFA\tA4150001\n" +
			"mail\tmail\tB4020001\n" +
			"会计引擎\tfas\tA2100001\n" +
			"金融会计引擎\tfaes\tA2100002\n" +
			"信贷综合查询\tcmis_web_bizQuery\tA1010005\n" +
			"信贷工作流\tcmis_workflow\tA1010002\n" +
			"信贷网关\tcmis_gateway\tA1010011\n" +
			"信贷综合业务\tcmis_postloan\tA1010008\n" +
			"呼叫页面\tcmispage\tA1010007\n" +
			"产品中心\tcmis_product\tA1010001\n" +
			"合约影像服务\tcmis_contract_image\tA1010010\n" +
			"合约中心\tcmis_contract\tA1010006\n" +
			"贷款场景短信消息通知服务\tcmis_loanmsg\tA1010012\n" +
			"信贷前端页面\tcmis-op\tA1010009\n" +
			"信贷日终\tcmis_eod\tA1010003\n" +
			"名单系统\trisk_list\tA3320002\n" +
			"风控决策服务\trisk_decision\tA3320001\n" +
			"贷后风险管理系统\trisk_postloan\tA3220004\n" +
			"风险变量\tplant\tA3320003\n" +
			"催收项目-主应用\tjiling_app\tA3380001\n" +
			"催收项目-网关应用\tjiling_gateway\tA3380002\n" +
			"催收智能语音\tcoll_job\tA1050002\n" +
			"催收外管\tcoll_outweb\tA1050013\n" +
			"新催收内管端\tcollweb\tA1050008\n" +
			"催收智能语音新\tcoll_intell\tA1050012\n" +
			"分群服务\tcoll_group\tA1050004\n" +
			"催收新内管前端\tcoll-inner-op\tA1050015\n" +
			"催收外管前端应用\tcoll-out-op\tA1050014\n" +
			"委外催收平台\tcoll-web\tA1050006\n" +
			"催收案件\tcoll_case\tA1050003\n" +
			"催收队列管理服务\tcoll_queue\tA1050010\n" +
			"催收日初跑批\tcoll_eod\tA1050011\n" +
			"同盾语音文件同步上传影像系统\tcmis-plcs-image\tA105010005\n" +
			"归户服务\tcoll_owner\tA1050007\n" +
			"催收回款\tcoll_repay\tA1050001\n" +
			"metaone\tmetaone\tC4080001\n" +
			"mong\tmong\tA4080003\n" +
			"mibserver\tmibserver\tA4080002\n" +
			"录音转码上传\tfilemanager\tA3050038\n" +
			"日志处理服务\tassembly_iomp-log-processing\tA3050002\n" +
			"dbsrv工作台后台接口服务\tdbsrv\tA3050019\n" +
			"数据代理服务\tedp-server\tA3050022\n" +
			"全渠道呼叫系统-RabbitMQ-1\tcall_rabbitmq\tA3050007\n" +
			"smartlink\tsmartlink\tA3050005\n" +
			"话务代理\ttproxy\tA3050044\n" +
			"外呼前端页面服务\tobd-web\tA3050055\n" +
			"外呼前端坐席管理服务\tobd-agent\tA3050053\n" +
			"文件托管服务\tassembly_ec_file_server\tA3050024\n" +
			"质检前端服务\tsso\tA3050067\n" +
			"ivr配置页面\tflowmanager\tA3050039\n" +
			"外呼后端管理服务\tocs-manager\tA3050058\n" +
			"Freeswitch代理服务\txserver\tA3050049\n" +
			"portal-server通用接口服务\tportal-server\tA3050020\n" +
			"单点登录管理服务\tsecurity\tA3050014\n" +
			"默认参数推送适配器\tassembly_eurm_url_cfg_adapter\tA3050012\n" +
			"内管系统UI静态资源服务\tassembly_eurm_ui\tA3050009\n" +
			"系统业务处理服务\tassembly_iosp_uiif\tA3050027\n" +
			"外呼执行服务\t外呼执行服务\tA3050057\n" +
			"外呼坐席状态监控服务\tocs-resource\tA3050059\n" +
			"排班前端\twfm-ui\tA3050063\n" +
			"文件传输服务\tfileserver\tA3050023\n" +
			"erip消息推送接口服务\terip-micro-starter\tA3050017\n" +
			"录音服务\txrecordserver\tA3050047\n" +
			"call\tcall\tA3050010\n" +
			"呼叫中心系统App\tcall_app\tA3050070\n" +
			"ddt后端\tddt-exam-api\tA3050016\n" +
			"客服操作平台\tcall-op\t\n" +
			"知识库前端\tkbp-client-3.2.0\tA3050033\n" +
			"freelink restapi\txconfig-api\tA3050046\n" +
			"freelink数据推送适配器\tassembly_eurm_freelink_cfg_adapter\tA3050013\n" +
			"多媒体坐席代理\tagentproxy\tA3050036\n" +
			"指标计算服务\tassembly_metrics_calc_rt\tA3050028\n" +
			"插件轮询采集服务\tassembly_iomp_collect_pluginpoll\tA3050032\n" +
			"内管系统业务处理服务\tassembly_eurm_uiif\tA3050011\n" +
			"监控\tmonitor\tA3050043\n" +
			"外呼接口服务\tocs-api\tA3050056\n" +
			"采集命令调度服务以及采集插件\tassembly_iomp_collect_plugincmd\tA3050031\n" +
			"外呼前端管理服务\tobd-manager\tA3050054\n" +
			"多媒体基表\tmmbaselog\tA3050042\n" +
			"全渠道呼叫系统-公共中间件-1\tcall_common_MW\tA3050008\n" +
			"质检后端服务\tserver\tA3050066\n" +
			"知识库后端\tkbp-server-3.2.0\tA3050035\n" +
			"多媒体网关\tuser-mgw\tA3050045\n" +
			"valuelink\tvaluelink\tA3050004\n" +
			"监控接口服务\tassembly_eurm_url_cfg_adapter\tA3050029\n" +
			"ivr服务\txvp\tA3050051\n" +
			"报表配置服务\treport-server\tA3050061\n" +
			"指标数据接收服务\tassembly_iosp_collect_index_receiver\tA3050026\n" +
			"qms前端\tqms-ui\tA3050064\n" +
			"电话软交换服务\tfreeswitch\tA3050052\n" +
			"指标数据汇总计算服务\tassembly_iosp_calc_perf\tA3050025\n" +
			"外呼任务调度服务\tocs-scheduler\tA3050060\n" +
			"消息代理\tmessage-proxy\tA3050040\n" +
			"客户信息管理\tportal-client\tA3050021\n" +
			"ddt前端\tddt-client\tA3050015\n" +
			"插件实时采集服务\tassembly_iomp_collect_pluginwait\tA3050068\n" +
			"erip\terip\tA3050006\n" +
			"文本语音通话处理服务\tmgw4xchat\tA3050041\n" +
			"坐席状态获取服务\txstart\tA3050050\n" +
			"排班后台\tservice\tA3050062\n" +
			"告警中心\tassembly_iomp_alarmcenter\tA3050030\n" +
			"话务路由\txrs\tA3050048\n" +
			"话务基表\tbaselog\tA3050037\n" +
			"质检获取事件服务\tevent\tA3050065\n" +
			"消息中心\tmessage-center\tA3050018\n" +
			"freelink\tfreelink\tA3050003\n" +
			"AAA认证服务\taaa_server\tA4200004\n" +
			"DNS服务器\tdns_server\tA4200001\n" +
			"LDAP系统\tldap_server\tA4200002\n" +
			"nexus3私服\tnexus3\tA4200003\n" +
			"模型服务\tholmes\tA4230010\n" +
			"三方对接服务\tfreyr\tA4230009\n" +
			"模型接口服务\tholmes_api\tA4230011\n" +
			"决策管理服务\tatreus\tA4230007\n" +
			"权限服务\tbifrost\tA4230005\n" +
			"业务监控服务\theimdallr\tA4230003\n" +
			"前置系统\tpreserver\tA4230004\n" +
			"消费应用\tconsumer\tA4230002\n" +
			"模型python服务\tholmes_python\tA4230012\n" +
			"规则引擎\tkratos-api\tA4230006\n" +
			"名单服务\triver\tA4230001\n" +
			"决策引擎服务\tatreus-engine\tA4230008\n" +
			"网络日志服务器\tnet_log_elk\tB4090003\n" +
			"华为大数据平台\tFusionInsight\tB4090002\n" +
			"logstash\tlogstash\tB4090001\n" +
			"fast-dfs\tfast_dfs\tA4100001\n" +
			"OA报表\tOA报表\tB4010003\n" +
			"OA内部小程序\toa_app\tB4010004\n" +
			"OA电脑端\toa_pcapp\tB4010002\n" +
			"OA移动端\toa_emobile\tB4010001\n" +
			"反洗钱应用\tantimoney\tC4070001\n" +
			"反洗钱道琼斯黑名单解析\tdowjoons\tC4070002\n" +
			"机构借据管理核心\tcals_loans\tA4290001\n" +
			"机构借据管理日终任务\tcals_job_etl\tA4290003\n" +
			"机构借据管理查询服务\tcals_query\tA4290002\n" +
			"同业报表查询\tibls_query\tA2150002\n" +
			"同业业务管理系统贷后工程\tiblsloans\tA2150006\n" +
			"同业业务系统管理端\tibls_manage\tA2150004\n" +
			"同业日终批处理\tibls_job_etl\tA2150001\n" +
			"同业放款工程\tibls_service_lend\tA2150003\n" +
			"同业业务系统前端页面\tibls-op\tA2150005\n" +
			"告警管理平台-页面\tmonitor-op\tC4190001\n" +
			"alarm_storm\talarm_storm\tC4190003\n" +
			"告警管理平台\tcmis_monitor\tC4190002\n" +
			"商户内管端\tcoopr_app\tA3080001\n" +
			"商户内管前端页面\tcoopr-op\tA3080005\n" +
			"商户独立流程\techain_server\tA3080003\n" +
			"商户外管端\tshpt\tA3080002\n" +
			"商户报表\tcooprplat-report\tA3080004\n" +
			"商户内管端旧\tcmis_cooprplat_integration\tA3080006\n" +
			"监管报送系统应用\tfaceid\tB4050001\n" +
			"旧版客户中心\tcmis_customer\tA4020101\n" +
			"新版客户中心\tcust_app\tA4020102\n" +
			"客户标签画像前端\tcust-op\tA4180107\n" +
			"客户标签画像\tcust_persona\tA4180105\n" +
			"营销系统\tcust_market\tA4180101\n" +
			"营销客群\tcust_market_group\tA4180103\n" +
			"营销账户系统\tcust_account\tA4180102\n" +
			"电销执行中心\tcust_market_tele\tA4180106\n" +
			"营销智能语音\tcust_market_intel\tA4180104\n" +
			"客户运营支撑系统\tcust_oss\tA4240002\n" +
			"客户运营支撑平台-前端页面\tcosp-op\tA4240001\n" +
			"对账平台主服务\ttcheck_core\tC4170001\n" +
			"对账平台调度服务\ttcheck_quartz\tC4170003\n" +
			"对账平台界面服务\ttcheck_boss\tC4170004\n" +
			"对账平台解析服务\ttcheck_prepose\tC4170002\n" +
			"导流平台\tiloan_diversion\tA3360001\n" +
			"征信衍生变量管理前台应用\tzxdv_op\tA4280002\n" +
			"征信衍生变量管理后台应用\tzxdv_app\tA4280001\n" +
			"微服务控制中心\tmmp_ctrl\tC4040009\n" +
			"微服务可观测监控平台\tmmp_oap_ui\tC4040008\n" +
			"配置中心主程序\tmmp_config\tC4040005\n" +
			"业务监控后台\tmmp_admin\tC4040001\n" +
			"集群流控服务端\tmmp_sentinel_cluster_server\tC4040007\n" +
			"监控中心健康值检查服务\tmmp_monitor_health\tC4040006\n" +
			"监控中心指标加工服务\tmmp_monitor\tC4040004\n" +
			"业务监控后台页面\tmmp-front\tC4040002\n" +
			"监控中心告警通知处理服务\tmmp_monitor_message\tC4040003\n" +
			"成本收益前端\tcbms-op\tB4180002\n" +
			"成本收益应用\tcbms\tB4180001\n" +
			"押品应用\tcmis_guaranty\tA4250001\n" +
			"掌中收APP\tzzs\tC3010001\n" +
			"机构订单\tjcfc_into_pay_hessian\tA2030103\n" +
			"统一配置页面\tyeepay_util_boss\tA2030804\n" +
			"支付回调服务\tjcfc_callback\tA2030204\n" +
			"收银台boss\tcashier-boss\t\n" +
			"对账服务配置页面\tdlb_fcheck_boss\tA2030901\n" +
			"定时任务\tjob_scheduling_hessian\tA2030301\n" +
			"交易记录页面（旧）\tjcfc_boss\tA2030903\n" +
			"支付SOA服务\tsoa_center_hessian\tA2030501\n" +
			"基础配置\tyeepay_config_server\tA2030302\n" +
			"账户服务页面配置\tjcfc_counter_boss\tA2030904\n" +
			"支付收付款通道\tjcfc_channel_pay_hessian\tA2030201\n" +
			"收银台proxy_下线\tcashier_trade_proxy\tA2030602\n" +
			"定时任务配置页面\tjob_scheduling_boss\tA2030803\n" +
			"机构路由配置页面\tjcfc_bankroute_boss\tA2030902\n" +
			"job-scheduling-boss\tjob-scheduling-boss\t\n" +
			"支付系统页面\temployee_boss\tA2030802\n" +
			"支付平台服务\temployee_hessian\tA2030503\n" +
			"dubbo页面服务\tdubbo_boss\tA2030801\n" +
			"支付账户服务\tjcfc_accounting_hessian\tA2030401\n" +
			"支付签约通道\tjcfc_channel_agreepay_hessian\tA2030202\n" +
			"jcfc_order_hessian\tjcfc_order_hessian\tA2030102\n" +
			"协议签约配置页面\tjcfc_secondfunction_boss\tA2030905\n" +
			"支付网关\tjcfc_api\tA2030101\n" +
			"支付对账通道\tjcfc_channel_check_hessian\tA2030203\n" +
			"收银台hessian\tcashier_hessian\tA2030603\n" +
			"对账服务\tfundscheck_hessian\tA2030502\n" +
			"收银台负载\tcashier_nginx\tA2030105\n" +
			"支付三方应用\tjcfc_third_hessian\tA2030000\n" +
			"收银台APP\tcashier_trade_app\tA2030601\n" +
			"jcfc_bankrouter_hessian\tjcfc_bankrouter_hessian\tA2030104\n" +
			"FEX\tFEX\tA4060002\n" +
			"实时数据采集系统\tedw_risk_variable\tA4060003\n" +
			"EIK\tEIK\tA4060001\n" +
			"晋享分期\tjxfq-wx\tA3340001\n" +
			"晋商消费金融APP(iOS)\tjxqb-ios\tA4220002\n" +
			"晋商消费金融APP(android)\tjxqb-android\tA4220001\n" +
			"快易贷H5\tkyd_h5\tA4220003\n" +
			"晋情学管理后台\ttrain\tC4060001\n" +
			"晋情学App\tjqx\tC4060002\n" +
			"晋情贷H5\tjqd-h5\tA3370001\n" +
			"核算产品中心\tloans_repay_product\tA4270001\n" +
			"cmis-message\tcmis_message\tA4050001\n" +
			"现金管理平台应用\tcash_manage\tA2160001\n" +
			"basesv\tbasesv\tA4090001\n" +
			"ca\tca\tA4040001\n" +
			"east前端页面\teast-op\tB4140002\n" +
			"east报送\teast_app\tB4140001\n" +
			"wiki\twiki\tC4130001\n" +
			"联动mgw\t联动mgw\tA3010004\n" +
			"亿美\tym_web\tA3010001\n" +
			"联动mgr/task\t联动mgr/task\tA3010003\n" +
			"梦网\tsmsapp\tA3010002\n" +
			"360 Optimus\t360 Optimus1\tA4140001\n" +
			"结算系统前置服务-回放\tsettlement_front_hf\tA2040099\n" +
			"结算公共jar包\tsettlement_common\tA2040009\n" +
			"结算主服务\tsettlement\tA2040001\n" +
			"结算核心系统\tsettlement_core\tA2040100\n" +
			"结算调度\tsettlement_job\tA2040012\n" +
			"结算系统产品中心\tsettlement_product\tA2040013\n" +
			"结算调度服务\tsettlement_quartz\tA2040008\n" +
			"文件生成服务\tsettlement_dispatch\tA2040006\n" +
			"结算系统计算服务tidb-回放\tsettlement_core_tidb\tA2040095\n" +
			"界面查询服务\tsettlement_boss\tA2040005\n" +
			"数据导入服务\tsettlement_importdata\tA2040003\n" +
			"账单服务\tsettlement_bill\tA2040004\n" +
			"结算系统计算服务-回放\tsettlement_core_hf\tA2040098\n" +
			"前置服务\tsettlement_front\tA2040007\n" +
			"结算系统账单-回放\tsettlement_newbill_hf\tA2040096\n" +
			"结算系统产品中心-回放\tsettlement_product_hf\tA2040097\n" +
			"USE\tUSE\tA4210001\n" +
			"绩效管理(pms)\t绩效管理\tB2010001\n" +
			"综合影像服务\timage_app\tA4260001\n" +
			"nms\tnms\tB4040001\n" +
			"联合贷款日终\tloans_union_ej\tA2080002\n" +
			"联合贷款查询\tloans_union_query\tA2080003\n" +
			"资金路由\tloans_frs\tA2080004\n" +
			"联合贷核心\tloans_syndicated\tA2080001\n" +
			"tax\ttax\tA4070001\n" +
			"视频会议\tVmeting\tC4020001\n" +
			"计税应用\tctvs\tA2140001\n" +
			"语音转换ASR\tASR\tA3280302\n" +
			"语音应答ivr\tIVR\tA3280304\n" +
			"文本转换TTS\tTTS\tA3280303\n" +
			"IVR接入MRCP\tMRCP\tA3280301\n" +
			"财务系统(finance)\t财务系统\tA2010001\n" +
			"核算自营业务\tlobs\tA1020007\n" +
			"核算账务转化\tloans_account_trans\tA1020014\n" +
			"核算查询回放\tloans-query-hf\t\n" +
			"还款服务\tloans_service_repay\tA1020013\n" +
			"核心服务\tycloans\tA1020001\n" +
			"调度系统\tycloans_job_dispatch\tA1020003\n" +
			"资产交换\tloans_les\tA1020011\n" +
			"核算回放\tloan_play_back\tA1020099\n" +
			"核算页面\tloans-op\tA1020005\n" +
			"日终任务\tycloans_job_etl\tA1020002\n" +
			"自营业务回放\tlobs-hf\t\n" +
			"核算查询\tloans_query\tA1020004\n" +
			"核算贷中系统\tloans_process\tA1020010\n" +
			"放款服务\tloans_service_lend\tA1020006\n" +
			"账户系统\tfeas\tA2130001\n" +
			"内部户系统\tfias\tA2130002\n" +
			"brsas\tbrsas\tA2050003\n" +
			"afterloan\tafterloan\tB2050001\n" +
			"资产证券化-导入\tabs_import\tA2020002\n" +
			"资产证券化-启动\tabs_start\tA2020001\n" +
			"退费计算服务\trems\tA2170001\n" +
			"金融基础数据报送\tfin_rep\tB4160001\n" +
			"金融基础数据前端页面\tfinrep-op\tB4160002\n" +
			"新门户后台\tjcfc_web_admin_new\tA3030003\n" +
			"新门户\tjcfc_web_new\tA3030002\n" +
			"jcfc-web\tjcfc_web\tA3030001\n" +
			"非现场审计(autids)\t非现场审计\tC4050001\n" +
			"新版额度\tcmis_lmt\tA1060001\n";

		int bitpos = 1;
		for (String s : str.split("\n")) {
			s = s.trim();
			String[] arr = s.split("\t");
			if (arr.length != 3) {
				continue;
			}
			String sysCode = arr[2].substring(0, 4);
			String subSysCode = Strings.padStart(arr[2].substring(4).replace("0", ""), 2, '0');
			subSysCode = subSysCode.substring(subSysCode.length() - 2);

			System.out.println(Strings.format("delete from srm_res_app where sys_id = '{}';", arr[1].trim()));
			String sql = Strings.format("insert into srm_res_app" +
				"(sys_id, bit_pos, sys_code, sys_sub_code, sys_name, sys_sts, authz_enabled, manager, intro, crt_user, crt_dt, upt_user, upt_dt)\n" +
				"values('{}', {}  , '{}', '{}', '{}', 'A' , 'N' , 0, '', 0, sysdate, 0, sysdate" +
				");\n", arr[1].trim(), bitpos, sysCode, subSysCode, arr[0].trim());
			System.out.println(sql);
			bitpos++;
		}
	}

	@Test
	void test04() {
		String str =
			"[\n" +
				"    {\n" +
				"        \"id\": \"开发测试区\",\n" +
				"        \"label\": \"开发测试区\",\n" +
				"        \"payload\": {}\n" +
				"    },\n" +
				"    {\n" +
				"        \"id\": \"下联区\",\n" +
				"        \"label\": \"下联区\",\n" +
				"        \"payload\": {}\n" +
				"    },\n" +
				"    {\n" +
				"        \"id\": \"互联网隔离区\",\n" +
				"        \"label\": \"互联网隔离区\",\n" +
				"        \"payload\": {}\n" +
				"    },\n" +
				"    {\n" +
				"        \"id\": \"互联网业务区\",\n" +
				"        \"label\": \"互联网业务区\",\n" +
				"        \"payload\": {}\n" +
				"    },\n" +
				"    {\n" +
				"        \"id\": \"互联网接入区\",\n" +
				"        \"label\": \"互联网接入区\",\n" +
				"        \"payload\": {}\n" +
				"    },\n" +
				"    {\n" +
				"        \"id\": \"外联区\",\n" +
				"        \"label\": \"外联区\",\n" +
				"        \"payload\": {}\n" +
				"    },\n" +
				"    {\n" +
				"        \"id\": \"核心交换区\",\n" +
				"        \"label\": \"核心交换区\",\n" +
				"        \"payload\": {}\n" +
				"    },\n" +
				"    {\n" +
				"        \"id\": \"核心业务区\",\n" +
				"        \"label\": \"核心业务区\",\n" +
				"        \"payload\": {}\n" +
				"    },\n" +
				"    {\n" +
				"        \"id\": \"基础服务区\",\n" +
				"        \"label\": \"基础服务区\",\n" +
				"        \"payload\": {}\n" +
				"    },\n" +
				"    {\n" +
				"        \"id\": \"管理业务区\",\n" +
				"        \"label\": \"管理业务区\",\n" +
				"        \"payload\": {}\n" +
				"    },\n" +
				"    {\n" +
				"        \"id\": \"运维管理区\",\n" +
				"        \"label\": \"运维管理区\",\n" +
				"        \"payload\": {}\n" +
				"    }\n" +
				"]";

		JSONArray array = JSON.parseArray(str);
		for (Object o : array) {
			JSONObject json = (JSONObject) o;
			String id = json.getString("id");
			String label = json.getString("label");
			System.out.println(Strings.format("delete from  srm_arch_net_zone where zone_id = '{}';", id));
			System.out.println(Strings.format("insert into srm_arch_net_zone(zone_id, super_zone_id, zone_name, zone_desc, intro, crt_user, crt_dt, upt_user, upt_dt)\n" +
				"values('{}', '', '{}', '{}', '', 0, sysdate, 0, sysdate);\n", id, label, label));
		}
	}

	@Test
	void test05() {
		String str = "[\n" +
			"    {\n" +
			"        \"id\": \"开发测试区01\",\n" +
			"        \"label\": \"虚拟化服务器（14台）\",\n" +
			"        \"comboId\": \"开发测试区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"开发测试区02\",\n" +
			"        \"label\": \"数仓/容器集群服务器（9台）\",\n" +
			"        \"comboId\": \"开发测试区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"下联区01\",\n" +
			"        \"label\": \"下联区服务器A\",\n" +
			"        \"comboId\": \"下联区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"下联区02\",\n" +
			"        \"label\": \"下联区服务器B\",\n" +
			"        \"comboId\": \"下联区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"互联网隔离区01\",\n" +
			"        \"label\": \"Open Api DB服务器（2台）\",\n" +
			"        \"comboId\": \"互联网隔离区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"互联网隔离区02\",\n" +
			"        \"label\": \"SRS50 DB服务器（2台）\",\n" +
			"        \"comboId\": \"互联网隔离区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"互联网业务区01\",\n" +
			"        \"label\": \"互联网业务区服务器A\",\n" +
			"        \"comboId\": \"互联网业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"互联网业务区02\",\n" +
			"        \"label\": \"互联网业务区服务器B\",\n" +
			"        \"comboId\": \"互联网业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"互联网接入区01\",\n" +
			"        \"label\": \"互联网接入区服务器A\",\n" +
			"        \"comboId\": \"互联网接入区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"互联网接入区02\",\n" +
			"        \"label\": \"互联网接入区服务器B\",\n" +
			"        \"comboId\": \"互联网接入区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"外联区01\",\n" +
			"        \"label\": \"外联区服务器A\",\n" +
			"        \"comboId\": \"外联区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"外联区02\",\n" +
			"        \"label\": \"外联区服务器B\",\n" +
			"        \"comboId\": \"外联区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心交换区01\",\n" +
			"        \"label\": \"核心交换区服务器A\",\n" +
			"        \"comboId\": \"核心交换区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心交换区02\",\n" +
			"        \"label\": \"核心交换区服务器B\",\n" +
			"        \"comboId\": \"核心交换区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心业务区01\",\n" +
			"        \"label\": \"核心区应用服务（6台）\",\n" +
			"        \"comboId\": \"核心业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心业务区02\",\n" +
			"        \"label\": \"核心区虚拟化服务（14台）\",\n" +
			"        \"comboId\": \"核心业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心业务区03\",\n" +
			"        \"label\": \"核心区虚拟化服务（14台）\",\n" +
			"        \"comboId\": \"核心业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心业务区04\",\n" +
			"        \"label\": \"核算DB服务（2台）\",\n" +
			"        \"comboId\": \"核心业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心业务区05\",\n" +
			"        \"label\": \"前置DB服务（2台）\",\n" +
			"        \"comboId\": \"核心业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心业务区06\",\n" +
			"        \"label\": \"智能客服机器人GPU服务器（2台）\",\n" +
			"        \"comboId\": \"核心业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心业务区07\",\n" +
			"        \"label\": \"信贷DB服务器（2台）\",\n" +
			"        \"comboId\": \"核心业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心业务区08\",\n" +
			"        \"label\": \"催收DB服务器（2台）\",\n" +
			"        \"comboId\": \"核心业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心业务区09\",\n" +
			"        \"label\": \"企业级影像服务器（2台）\",\n" +
			"        \"comboId\": \"核心业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心业务区10\",\n" +
			"        \"label\": \"联合贷、TiDB等服务器（27台）\",\n" +
			"        \"comboId\": \"核心业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"基础服务区01\",\n" +
			"        \"label\": \"基础服务区服务器A\",\n" +
			"        \"comboId\": \"基础服务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"基础服务区02\",\n" +
			"        \"label\": \"基础服务区服务器B\",\n" +
			"        \"comboId\": \"基础服务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"管理业务区01\",\n" +
			"        \"label\": \"备份系统服务器（5台）\",\n" +
			"        \"comboId\": \"管理业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"管理业务区02\",\n" +
			"        \"label\": \"信贷、支付、核算DG库服务器（3台）\",\n" +
			"        \"comboId\": \"管理业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"管理业务区03\",\n" +
			"        \"label\": \"BI报表DB服务器（3台）\",\n" +
			"        \"comboId\": \"管理业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"管理业务区04\",\n" +
			"        \"label\": \"日志平台服务器（3台）\",\n" +
			"        \"comboId\": \"管理业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"管理业务区05\",\n" +
			"        \"label\": \"数仓系统服务器（15台）\",\n" +
			"        \"comboId\": \"管理业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"运维管理区01\",\n" +
			"        \"label\": \"管理区虚拟化服务器（8台）\",\n" +
			"        \"comboId\": \"运维管理区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"运维管理区02\",\n" +
			"        \"label\": \"视频会议服务器（4台）\",\n" +
			"        \"comboId\": \"运维管理区\",\n" +
			"        \"payload\": {}\n" +
			"    }\n" +
			"]";

		JSONArray array = JSON.parseArray(str);
		for (Object o : array) {
			JSONObject json = (JSONObject) o;
			String id = json.getString("id");
			String comboId = json.getString("comboId");
			String label = json.getString("label");
			System.out.println(Strings.format("delete from  srm_arch_net_zone where zone_id = '{}';", id));
			System.out.println(Strings.format("insert into srm_arch_net_zone(zone_id, super_zone_id, zone_name, zone_desc, intro, crt_user, crt_dt, upt_user, upt_dt)\n" +
				"values('{}', '{}', '{}', '{}', '', 0, sysdate, 0, sysdate);\n", id, comboId, label, label));
		}
	}

	@Test
	void test06() {
		String str = "[\n" +
			"    {\n" +
			"        \"id\": \"开发测试区-下联区\",\n" +
			"        \"label\": \"\",\n" +
			"        \"source\": \"开发测试区\",\n" +
			"        \"target\": \"下联区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"下联区-核心交换区\",\n" +
			"        \"label\": \"\",\n" +
			"        \"source\": \"下联区\",\n" +
			"        \"target\": \"核心交换区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"互联网隔离区-互联网业务区\",\n" +
			"        \"label\": \"\",\n" +
			"        \"source\": \"互联网隔离区\",\n" +
			"        \"target\": \"互联网业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"互联网业务区-核心交换区\",\n" +
			"        \"label\": \"\",\n" +
			"        \"source\": \"互联网业务区\",\n" +
			"        \"target\": \"核心交换区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"互联网接入区-核心交换区\",\n" +
			"        \"label\": \"\",\n" +
			"        \"source\": \"互联网接入区\",\n" +
			"        \"target\": \"核心交换区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心交换区-外联区\",\n" +
			"        \"label\": \"\",\n" +
			"        \"source\": \"核心交换区\",\n" +
			"        \"target\": \"外联区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心交换区-核心业务区\",\n" +
			"        \"label\": \"\",\n" +
			"        \"source\": \"核心交换区\",\n" +
			"        \"target\": \"核心业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心交换区-基础服务区\",\n" +
			"        \"label\": \"\",\n" +
			"        \"source\": \"核心交换区\",\n" +
			"        \"target\": \"基础服务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心交换区-管理业务区\",\n" +
			"        \"label\": \"\",\n" +
			"        \"source\": \"核心交换区\",\n" +
			"        \"target\": \"管理业务区\",\n" +
			"        \"payload\": {}\n" +
			"    },\n" +
			"    {\n" +
			"        \"id\": \"核心交换区-运维管理区\",\n" +
			"        \"label\": \"\",\n" +
			"        \"source\": \"核心交换区\",\n" +
			"        \"target\": \"运维管理区\",\n" +
			"        \"payload\": {}\n" +
			"    }\n" +
			"]";

		JSONArray array = JSON.parseArray(str);
		for (Object o : array) {
			JSONObject json = (JSONObject) o;
			String source = json.getString("source");
			String target = json.getString("target");
			String id = json.getString("id");
			System.out.println(Strings.format("delete from  srm_arch_net_zone_rel where source_zone_id = '{}' and target_zone_id = '{}';", source, target));
			System.out.println(Strings.format("insert into srm_arch_net_zone_rel(source_zone_id, target_zone_id, rel_desc, intro, crt_user, crt_dt, upt_user, upt_dt)\n" +
				"values('{}', '{}', '{}', '', 0, sysdate, 0, sysdate);\n", source, target, id));
		}

	}
}
