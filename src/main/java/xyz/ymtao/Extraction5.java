package xyz.ymtao;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.data.mongodb.core.MongoTemplate;
import xyz.ymtao.entity.ZtbDocument;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static xyz.ymtao.Extraction3.writeExcel;


/**
 * @author ghj
 * @date 2020/12/17 20:17
 * @desc
 */
public class Extraction5 {
	private static ServerAddress serverAddress = new ServerAddress("192.168.1.150",27018 );
	private static MongoCredential credential = MongoCredential.createCredential("ymt", "ztb_db", "ymt123456".toCharArray());
	private static MongoClientOptions clientOptions = MongoClientOptions.builder().build();
	private static MongoClient mongoClient = new MongoClient(serverAddress,credential,clientOptions);

	private static MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "ztb_db");
	private static final String PRICE="span.price";
	private static final String HTML_FRAGMENT="div[style=\"float:left;margin-left: 5px;text-align:left; " +
			"width:80%\"]";
	private static final String DIR_PATH="F:\\分类\\按照网上询价成交原则，现将采购结果公示如下";


	public static void main(String[] args) throws IOException {
		outPutDate(DIR_PATH);
	}

	public static void outPutDate(String dirPath){
		try {
			File dir=new File(dirPath);
//			List<String[]> list=new ArrayList<>();
			for (File file : dir.listFiles()) {
				Document doc=Jsoup.parse(file, "utf-8");
				String[] split=doc.body().text().split("#", 4);
				String targetEnt=split[0];
				String title=split[1];
				String date=split[2];
				String contents=doc.toString();
				String money="";
				String fragment="";
				String status="";
				String type = "暂未分类";
				double accuracyRate = 0.8;
				String fileName=file.getName().substring(0, file.getName().lastIndexOf("."));
				Elements elements=doc.select("p.ng-scope");
				Elements htmlFragment=doc.select(HTML_FRAGMENT);
				Elements price=doc.select(PRICE);
				BigDecimal bigDecimal=new BigDecimal(0);
				if (elements.size() == 0) {
					status="无法识别";

				} else {
					type = "NgScope";
					accuracyRate = 1;
					int j=0;
					if(htmlFragment.size()!=0){
						fragment=htmlFragment.toString();
					}
					for (int i=0; i < elements.size(); i++) {
						if(!elements.text().contains("服务响应时间：")&&elements.get(i).select("label").text().contains(
								"成交供应商：")){
							if(elements.get(i).select("span").text().equals(targetEnt)){
								j++;
								String s1=
										elements.get(i + 2).select("span").text().replaceAll("￥", "").replaceAll(",",
										"").trim();

								bigDecimal=bigDecimal.add(new BigDecimal(s1));

							}
						}else if(elements.text().contains("服务响应时间：")&&elements.get(i).select("label").text().contains(
								"成交供应商：")){
							if(elements.get(i).select("span").text().equals(targetEnt)){
								j++;
								String s1=
										elements.get(i + 3).select("span").text().replaceAll("￥", "").replaceAll(",",
												"").trim();

								bigDecimal=bigDecimal.add(new BigDecimal(s1));

							}
						}
					}
					if(j!=0){
						status="中标";
					}else{
						status="未中标";
					}
					}
				ZtbDocument ztbDocument;
//				contents = contents.replaceAll(targetEnt,"<div style=\"background: yellow;\">" + targetEnt + "</div>");
				ztbDocument = new ZtbDocument(targetEnt,title,date,contents,fragment,status,type,accuracyRate,bigDecimal.toString());
//				list.add(new String[]{
//						fileName,
//						targetEnt,
//						title,
//						date,
//						fragment,
//						bigDecimal.toString(),
//						status
//				});
				try{
					mongoTemplate.insert(ztbDocument);
				} catch (Exception e){
					System.out.println(targetEnt + "," + title + "插入mongo失败");
				}

			}
//			writeExcel(list,dirPath);

		} catch (IOException e) {
			e.printStackTrace();
		}


	}




}
