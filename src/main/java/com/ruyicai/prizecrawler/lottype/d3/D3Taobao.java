package com.ruyicai.prizecrawler.lottype.d3;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.IAgency;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class D3Taobao implements IAgency{

	private static Logger logger = LoggerFactory.getLogger(D3Taobao.class);
	private static final String URL = "http://caipiao.taobao.com/lottery/awardresult/lottery_3d.htm";
	

	
	private void buildPrizeInfo(String batchcode, PrizeInfo prizeInfo,
			Document doc) {
		Elements lis = doc.select(".kk-a");
		Elements spans = lis.get(0).select("span");
		String[] winnum = new String[3];
		for(int i = 1;i < 4;i++) {
			winnum[i-1] = spans.get(i).text();
		}
		String wincode = "0"+winnum[0]+"0"+winnum[1]+"0"+winnum[2];
		prizeInfo.setBatchcode(batchcode);
		prizeInfo.setLotno("F47103");
		prizeInfo.setWinbasecode(wincode);
		prizeInfo.setWinspecialcode("");
	}


	private boolean isIssueExist(String batchcode, Document doc) {
		boolean flag = false;
		Element select = doc.select(".J_select_op").first();
		Elements options = select.select("option");
		for(Element option:options) {
			if(batchcode.equals(option.text())&&"selected".equals(option.attr("selected"))) {
				flag = true;
			}
		}
		return flag;
	}


	@Override
	public PrizeInfo crawlFromAgency(String lotno, String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从淘宝抓取开奖,彩种F47103,期号"+batchcode);
			Document doc = Jsoup.connect(URL).timeout(3500).get();

			if(isIssueExist(batchcode, doc)) {
				buildPrizeInfo(batchcode, prizeInfo, doc);
			}
			
			logger.info("从淘宝抓取开奖结束:"+prizeInfo.toString());
		}catch(Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从淘宝抓取第" + batchcode + "期3D开奖出错", e);
		}
		
		return prizeInfo;
	}
	
	public static void main(String[] args) {
		System.out.println(new D3Taobao().crawlFromAgency("F47103", "2012351").toString());
	}

}
