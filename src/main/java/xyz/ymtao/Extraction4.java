package xyz.ymtao;
import com.mongodb.*;
import com.sun.xml.internal.bind.v2.TODO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import xyz.ymtao.entity.TemplateType;
import xyz.ymtao.entity.ZtbDocument;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//
import static xyz.ymtao.Extraction3.writeExcel;


/**
 * @author ghj
 * @date 2020/12/17 20:17
 * @desc
 */
public class Extraction4 {

	private static ServerAddress serverAddress = new ServerAddress("192.168.1.150",27018 );
	private static MongoCredential credential = MongoCredential.createCredential("ymt", "ztb_db", "ymt123456".toCharArray());
	private static MongoClientOptions clientOptions = MongoClientOptions.builder().build();
	private static MongoClient mongoClient = new MongoClient(serverAddress,credential,clientOptions);

	private static MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "ztb_db");
	private static final String DIR_PATH="F:\\分类\\、中标信息";
	private static final String DIR_PATH2="F:\\分类\\、中标(成交)信息";
	private static final String DIR_PATH3="F:\\分类\\其它";

	public static void main(String[] args) {
		String[] types = {"暂无法识别"};
		for(int i=0;i<types.length;i++){
			mongoTemplate.insert(new TemplateType(types[i]));
		}
//		outPutDate(DIR_PATH);
	}

	public static void outPutDate(String dirPath){
		try {
			// 根据目录获取文件夹
			File dir=new File(dirPath);
			List<String[]> list=new ArrayList<>();
			// 遍历文件夹中的文件
			for (File file : dir.listFiles()) {
				// 将文件转换为dom文档
				Document doc=Jsoup.parse(file, "utf-8");
				String[] split=doc.body().text().split("#", 4);
				String targetEnt=split[0];
				String title=split[1];
				String date=split[2];
				String contents=doc.toString();
				String money="暂无法识别";
				String type = "暂未分类";
				double accuracyRate = 0.8;
				String fileName=file.getName().substring(0, file.getName().lastIndexOf("."));
				boolean hit = false;
				// 获取招投标内容表格
				Elements elements = null;
				String keyContent = "";
				BigDecimal price = new BigDecimal("0.00");
				if((elements=doc.getElementsByClass("vT_detail_content")) != null && elements.size()>0){
					// 获取所有招投标信息
					Elements tableList =  elements.get(0).getElementsByTag("table");
					Element table = tableList.size() > 0 ? tableList.get(0) : null;
					keyContent = table != null ? table.toString() : "";
					money = handleTable(table, targetEnt);
					if("非本方法处理".equals(money)){
						money = "暂无法识别";
					}
					if(!"暂无法识别".equals(money)){
						type = "VfTable";
						accuracyRate = 1;
					}

				} else if((elements=doc.getElementsByClass("vF_detail_content")) != null && elements.size()>0){
					// 如果该招投标信息为另一种模板
					Element element = elements.get(0);
					Elements pList = element.getElementsByTag("p");
					for(int i=0;i<pList.size();i++){
						Element p = pList.get(i);
						String content = p.text();
						if(content.equals("供应商名称：")){
							String[] arr;
							String entName = (arr=content.split("：")).length > 1 ? arr[1]: "";
                             entName = entName.replace('（','(');
							entName = entName.replace('）',')');
							entName = entName.replace('；',' ');
							entName = entName.trim();
							if(!targetEnt.equals(entName)){
//								System.out.println(targetEnt+ ":"+entName);
								// 该招投标信息中的公司与目标公司不符合，跳过
							} else {
								// 为目标公司，继续向下寻找中标金额
								hit = true;
								for(;i<pList.size();i++){
									// 找到中标金额
									String[] arrs = pList.get(i).text().split("：");
									if(pList.get(i).text().contains("金额：") && arrs.length > 1){

										String priceStr = arrs[1].trim();
										int index = priceStr.indexOf("万元");
										if(index >= 0){
                                           price = price.add(new BigDecimal(getNumberFromString(priceStr)).multiply(new BigDecimal("10000.00")));
										} else {
											price = price.add(new BigDecimal(getNumberFromString(priceStr)));
										}
										break;
									}
								}
							}
							if(hit){
								money = price.toString();
							} else {
								money = "疑似未中标";
							}
						} else if(content.contains("中标供应商名称、联系地址及中标金额：")){
							Elements tableList = element.getElementsByTag("table");
							if(tableList != null && tableList.size()>0){
								Element table = tableList.get(0);
								keyContent = table.toString();
								money = handleTable(table, targetEnt);
								if(!"暂无法识别".equals(money)){
									type = "VfTable";
									accuracyRate = 1;
								}
								System.out.println(money);
							} else {
								// 判断是否为分标段落
								boolean flag = false;
								boolean isTarget = false;
								for(;i<pList.size();i++){
									String pText = pList.get(i).text();
									if(pText.contains("分标")){
										flag = true;
										String[] arr = pList.get(i).text().split("：")[1].split("、");
										String entName = arr[0];
										entName = entName.replace('（','(');
										entName = entName.replace('）',')');
										entName = entName.replace('；',' ');
										entName = entName.trim();
										// 为目标企业
										if(targetEnt.equals(entName)){
											isTarget = true;
											String priceStr = arr[arr.length-1];
											priceStr = priceStr.substring(priceStr.indexOf("人"), priceStr.length());
											price = price.add(new BigDecimal(getNumberFromString(priceStr)));
										}
									} else if(pText.contains("公司")){
										String[] parr = pText.split("，");
										if(parr.length >1){
											flag = true;
											String entName = parr[0];
											entName = entName.replace('（','(');
											entName = entName.replace('）',')');
											entName = entName.replace('；',' ');
											entName = entName.trim();
											System.out.println(entName);
											if(entName.contains(targetEnt)){
												isTarget = true;
												String priceStr = parr[parr.length -1];
												if(priceStr.contains("/")){
													money = "单价中标，" + priceStr;
													flag = false;
												}
												price = price.add(new BigDecimal(getNumberFromString(priceStr)));
											}
										}
										break;
									}
								}
								// 说明非目标模板
								if(!flag){
									break;
								} else{
									if(!isTarget){
										money = "疑似未中标";
									} else{
										money = price.toString();
									}
								}
							}
						} else if(content.contains("入围供应商、价格调整规则或优惠条件：") && element.getElementsByTag("table").size() == 0){

							for(;i<pList.size();i++){
								String pContent = pList.get(i).text();
								if(pContent.contains("第一中标入围候选人:")){
									String entName = pContent.split(":")[1];
									entName = entName.replace('（','(');
									entName = entName.replace('）',')');
									entName = entName.replace('；',' ');
									entName = entName.trim();
									// 如果为目标企业
									if(targetEnt.equals(entName)){
										String priceStr = pList.get(++i).text();
										if(priceStr.contains("万")){
											price = price.add(new BigDecimal(getNumberFromString(priceStr)).multiply(new BigDecimal("10000")));
										} else {
											price = price.add(new BigDecimal(getNumberFromString(priceStr)));
										}
									}
								}
								// 已对招投标信息处理完毕
								if(pContent.contains("本项目招标代理费总金额")){
									break;
								}
							}
                          money = price.setScale(2,BigDecimal.ROUND_HALF_UP).toString();

						}
					}

				}
				ZtbDocument ztbDocument;
//				contents = contents.replaceAll(targetEnt,"<div style=\"background: yellow;\">" + targetEnt + "</div>");
				if(money.contains(".")){
					ztbDocument = new ZtbDocument(targetEnt,title,date,contents,keyContent,"中标",type,accuracyRate,money);
					list.add(new String[]{
							fileName,
							targetEnt,
							title,
							date,
							keyContent,
							money,
							"中标"
					});
				} else {
					ztbDocument = new ZtbDocument(targetEnt,title,date,contents,keyContent,money,type,accuracyRate,"0.00");
					list.add(new String[]{
							fileName,
							targetEnt,
							title,
							date,
							keyContent,
							"0.00",
							money
					});
				}
				mongoTemplate.insert(ztbDocument);

			}
//			  writeExcel(list,dirPath);

		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public static String getNumberFromString(String str){
		if(str == null || "".equals(str)){
			return "";
		}
		String regEx = "[^\\.0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	public static String handleTable(Element table, String targetEnt){
		BigDecimal price = new BigDecimal("0");
		if(table == null) {return "非本方法处理";}

		Elements trList = table.getElementsByTag("tr");
		// 获取表格头
		Element header = trList != null && trList.size()>0 ? trList.get(0) : null;
		// 根据表格头判断是否为目标表格类型
		if(header != null){
			Elements headerList = header.getElementsByTag("th").size() > 0 ?  header.getElementsByTag("th") : header.getElementsByTag("td");
			if(headerList.size() < 3 || !headerList.get(1).text().contains("中标供应商")){return "暂无法识别";}
            // 判断表头的金额中是否单位为万元
			String priceHeader = headerList.get(3).text();
			boolean isWan = false;
			if(priceHeader.indexOf("万") >= 0){
				isWan = true;
			}

			boolean isTarget = false;
			// 处理表格中的每一条招投标信息
			for(int i=1;i<trList.size();i++){
				Element element = trList.get(i);
				// 取出每一条招投标信息中的各个字段信息
				Elements td = element.getElementsByTag("td");
				if(td !=null && td.size()>=4){
					String entName = td.get(1).text();
					entName = entName.replace('（','(');
					entName = entName.replace('）',')');
					entName = entName.replace('；',' ');
					entName = entName.trim();
					// 如果为目标企业
					if(targetEnt.equals(entName)){
						isTarget =true;
						String priceStr = td.get(3).text();
						BigDecimal tempPrice;
						int index = priceStr.indexOf("万");
						if(isWan || index>=0){
							tempPrice = new BigDecimal(getNumberFromString(priceStr)).multiply(new BigDecimal("10000"));
						} else {
							tempPrice = new BigDecimal(getNumberFromString(priceStr));
						}
						price = price.add(tempPrice);
						System.out.println(targetEnt+ ":"+priceStr+":"+price);
					}
				}
			}
			if(!isTarget){
				return "疑似未中标";
			}
			// 保留两位小数返回
			return price.setScale(2,BigDecimal.ROUND_HALF_UP).toString();

		} else {
			return "非本方法处理";
		}

	}




}
