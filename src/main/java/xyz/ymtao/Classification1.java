package xyz.ymtao;

import java.io.*;

/**
 * @author ghj
 * @date 2020/12/15 14:42
 * @desc  分类1：
 */
public class Classification1 {
	public static final String SEPARATOR=File.separator;
	public static final String dirPath="F:/分类";
	public static final String TITLE_1="成交结果(入围供应商)";
	public static final String TITLE_2="、成交结果";
	public static final String TITLE_3="、中标信息";
	public static final String TITLE_4="中标结果(入围供应商)";
	public static final String TITLE_5="、中标结果";
	public static final String TITLE_6="、预中标结果";
	public static final String TITLE_7="、成交信息";
	public static final String TITLE_8="、中标（成交）信息";
	public static final String TITLE_9="按照重庆政府采购协议供货电子交易平台的定标原则，并经采购人确认，现将成交结果公布如下";
	public static final String TITLE_10="按照网上询价成交原则，现将采购结果公示如下";
	public static final String TITLE_11="采购结果公示";
	public static final String TITLE_12="采购结果公告";
	public static final String TITLE_13="采购结果预公示";
	public static final String TITLE_14="、中标(成交)信息";
	public static final String TITLE_15="、预成交结果";
	public static final String TITLE_16="按照重庆市网上政府采购系统的成交原则，并经采购人确认，现将成交结果公布如下";
//	public static final String TITLE_14="、中标情况";



	public static void main(String[] args) {

//		mkdir(dirPath);
//		readFile("F:\\k8s.log");
		test(214.6387639429376,1382.611884232928,true,7,0.1);
		test2(994.433096902277,105.56690309772199,true,7,0.1);
	}

	public static void test(double founds,double bond,boolean isUp,int times,double percent){
		double n = 1;
		if(isUp){
			for(int i=0;i<times;i++){
				n += n*percent;
				double temp = bond * percent;
				founds += founds * percent + temp;
				bond-=temp;
			}
		} else {
			for(int i=0;i<times;i++){
				n -= n*percent;
				founds-=founds * percent;
				bond += founds * percent;
				founds-=founds * percent;
			}
		}
		System.out.println("way1 final======founds:" + founds + "; bond:" + bond + "======total:" + (founds+bond) + "======grade:" + n);
	}
	public static void test2(double founds,double bond,boolean isUp,int times,double percent){
		if(isUp){
			for(int i=0;i<times;i++){
				double temp = founds * percent/2;
				founds += temp;
				bond+=temp;
			}
		} else {
			for(int i=0;i<times;i++){
				double temp = founds * percent/2;
				founds-=temp;
				bond -= temp;
			}
		}
		System.out.println("way2 final======founds:" + founds + "; bond:" + bond + "======total:" + (founds+bond));
	}

	public  static void readFile(String path){
		File file=new File(path);
		String lineTxt=null;
		if(file.isFile()&&file.exists()){
			try(InputStreamReader read=new InputStreamReader(new FileInputStream(file));
				BufferedReader reader=new BufferedReader(read);){
				int lineNum=0;
				while ((lineTxt=reader.readLine())!=null){
					lineNum++;
					if(lineTxt.contains(TITLE_1)){
						String dir=dirPath+SEPARATOR+TITLE_1;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_2)){
						String dir=dirPath+SEPARATOR+TITLE_2;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_3)){
						String dir=dirPath+SEPARATOR+TITLE_3;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_4)){
						String dir=dirPath+SEPARATOR+TITLE_4;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_5)){
						String dir=dirPath+SEPARATOR+TITLE_5;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_6)){
						String dir=dirPath+SEPARATOR+TITLE_6;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_7)){
						String dir=dirPath+SEPARATOR+TITLE_7;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_8)){
						String dir=dirPath+SEPARATOR+TITLE_8;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_9)){
						String dir=dirPath+SEPARATOR+TITLE_9;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_10)){
						String dir=dirPath+SEPARATOR+TITLE_10;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_15)){
						String dir=dirPath+SEPARATOR+TITLE_15;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					} else if(lineTxt.contains(TITLE_11)){
						String dir=dirPath+SEPARATOR+TITLE_11;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_12)){
						String dir=dirPath+SEPARATOR+TITLE_12;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_13)){
						String dir=dirPath+SEPARATOR+TITLE_13;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_14)){
						String dir=dirPath+SEPARATOR+TITLE_14;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else if(lineTxt.contains(TITLE_16)){
						String dir=dirPath+SEPARATOR+TITLE_16;
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}else{
						String dir=dirPath+SEPARATOR+"其它";
						mkdir(dir);
						writeFile(lineTxt,dir+SEPARATOR+lineNum+".txt");
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 将字符串信息写入文件
	 * @param info  字符串信息
	 * @param filePath 文件地址
	 */
	public static void writeFile(String info,String filePath){
		File file=new File(filePath);
		try(BufferedWriter writer=new BufferedWriter(new FileWriter(file))) {
			writer.write(info);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建文件夹
	 * @param path
	 */
	public static void mkdir(String path){
		File file=new File(path);
		if(!file.exists()){
			file.mkdir();
		}
	}
}
