package Test;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cloud_servce.dao.gifjia.IGifjiaTagsDao;
import cloud_servce.entity.gifjia.GifjiaTags;
import cloud_servce.util.MD5Util;




public class CrawlerTest {
	@Autowired
	private IGifjiaTagsDao gifDao;

	public CrawlerTest() {
		// TODO 自动生成的构造函数存根
	}


	@Test
	public void jiexi() {
		Connection conn = Jsoup.connect("https://www.gifjia5.com/tags/");
		try {
			Document doc = conn.get();
			Element body = doc.body();
			Elements tagslist = body.getElementsByClass("tagslist");
			Elements allItem = tagslist.select("li");
//			//操作元素：
			for (Element p : allItem) {
				//操纵元素：这里就类似于jQuery
				// 获取名称
				Element titleEm = p.getElementsByClass("name").first();
				String tagName = titleEm.text();
				// 获取链接
				String tagHref = titleEm.attr("href");
				// 获取数量
				String tagNum = p.select("small").text();
				// 获取副标题
				Elements disEm = p.getElementsByClass("tit");
				String tagDis = disEm.first().text();
				// 获取副标题链接
				String tagDisHref = disEm.first().attr("href");

				// 计算唯一标识符
				String identification = MD5Util.encrypt(String.format("%s-%s", tagName,tagHref));
				System.out.println(gifDao);

				// 判断该数据是否已经存在
				GifjiaTags gifTag = gifDao.findByIdentification(identification);
				if (gifTag == null) { // 不存在
					gifTag = new GifjiaTags();
				}
				gifTag.setIdentification(identification);
				gifTag.setTagName(tagName);
				gifTag.setTagHref(tagHref);
				gifTag.setTagDis(tagDis);
				gifTag.setTagDisHref(tagDisHref);
				gifTag.setTagNum(tagNum);
				gifDao.save(gifTag);
				
				System.out.println("名称：" + tagName + "\t\t超链接：" + tagHref);
				System.out.println("数量：" + tagNum);
				System.out.println("副标题：" + tagDis);
				System.out.println("副标题超链接：" + tagDisHref);
				System.out.println("=====================");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	public Document getHtmlDoc() {
		//通过URL获得连接：Connection对象
		Connection conn = Jsoup.connect("https://www.gifjia5.com/tags/");
		//以下为主要方法，多数返回Connection
		//		conn.data("query", "Java");   // 请求参数
		//		conn.userAgent("I ’ m jsoup"); // 设置 User-Agent 
		//		conn.cookie("auth", "token"); // 设置 cookie 
				conn.timeout(3000);           // 设置连接超时时间
		//发送请求，获得HTML文档：Document对象
		try {
//			Document doc = conn.get();
			Document doc = conn.post();
			return doc;
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return null;
		}
		//		Document doc = conn.post();
	}


}
