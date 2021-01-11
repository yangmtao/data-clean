package xyz.ymtao;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author ghj
 * @date 2020/12/17 20:17
 * @desc
 */
public class Extraction3 {

	private static final String TABLE_HEADER="table#tabrow thead tr th";
	private static final String TABLE_TRS="table#tabrow tbody tr";
	private static final String DIR_PATH="F:\\分类\\、中标（成交）信息";

	public static void main(String[] args) {
		outPutDate(DIR_PATH);
	}

	public static void outPutDate(String dirPath){
		File dir=new File(dirPath);
		if(dir.isDirectory()){
			File[] files=dir.listFiles();
			List<String[]> list=new ArrayList<>();
			for (File f : files) {
				String[] info=getInfo(f);
				list.add(info);
			}
			writeExcel(list,dirPath);
		}


	}

	private static String[] getInfo(File file) {
		String fileName="";
		String targetEnt="";
		String title="";
		String date="";
		String money="";
		String ent="";
		Double amount=0d;
		try {
			Document doc=Jsoup.parse(file, "UTF-8");
			fileName=file.getName().substring(0, file.getName().lastIndexOf("."));
			String[] split=doc.body().text().split("#", 4);
			targetEnt=split[0];
			title=split[1];
			date=split[2];
			int i=0;
			int n=0;
			int m=0;
			Elements elements=doc.select("p");
			Elements header=doc.select(TABLE_HEADER);
			Elements trs=doc.select(TABLE_TRS);
			if(header.size()!=0){
				for (int j=0; j <header.size() ; j++) {
				if(!header.get(j).text().contains("地址")&&(header.get(j).text().contains("供应商"))){
					n=j;
				}else if(header.get(j).text().contains("金额")){
					m=j;
				}
				}
			}
			if(trs.size()!=0){
				for (int j=0; j <trs.size() ; j++) {
					String entName=trs.get(j).select("td").get(n).text();
					if(entName.equals(targetEnt)){
						ent=targetEnt;
						amount+=Double.valueOf(trs.get(j).select("td").get(m).text().replaceAll(",","").trim());
					}
				}
				money=amount.toString();
			}else if(elements.size()!=0){
				for (int j=0; j <elements.size() ; j++) {
					if(elements.get(j).text().contains("、中标（成交）信息")&& elements.get(j+1).text().contains("供应商名称")&&elements.get(j+3).text().contains("金额")){
						ent=elements.get(j+1).text().split("：")[1];
						if(ent.equals(targetEnt)){
							Double d=(Double.valueOf(elements.get(j+3).text().split("：")[1].replaceAll("（万元）",
									"").trim())*10000);
							money=d.toString();
						}else{
							money="--";
						}
						continue;
					}
				}
			}




		} catch (IOException e) {
			e.printStackTrace();
		}

		return new String[]{fileName,targetEnt,title,date,ent,money};
	}
	public static void writeExcel(List<String[]> list,String dirPath) {
		String[] split=dirPath.split("\\\\");
		String fileName=split[split.length - 1];
		// 创建空白工作簿
		XSSFWorkbook workbook=new XSSFWorkbook();
		// 创建一个空白页
		XSSFSheet spreadsheet=workbook.createSheet();
		// 创建行对象
		XSSFRow row;
		// 遍历数据并写入工作表
		for (int i=0; i < list.size(); i++) {
			//创建行
			row=spreadsheet.createRow(i + 1);
			//获取list中数据
			String[] objectArr=list.get(i);
			Cell cell=null;
			for (int j=0; j < objectArr.length; j++) {
				cell=row.createCell(j);
				cell.setCellValue((String) objectArr[j]);
			}

		}
		// 在文件系统中编写工作簿
		try (FileOutputStream out=new FileOutputStream(new File(fileName+".xlsx"))) {
			workbook.write(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
