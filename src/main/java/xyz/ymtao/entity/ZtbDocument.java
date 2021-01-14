package xyz.ymtao.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import xyz.ymtao.util.Const;

import java.io.Serializable;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2020/12/21  12:55
 */
@ApiModel(value = "中标信息对象")
public class ZtbDocument implements Serializable {

    @ApiModelProperty(value = "企业名称")
    private String entName;
    @Id
    private Integer id;
    @ApiModelProperty(value = "招标公告标题")
    private String title;
    private String date;
    private String content;
    private String keyContent;
    @ApiModelProperty(value = "用户账号/手机号")
    private String phone;
    /** 解析状态*/
    @ApiModelProperty(value = "中标状态：0暂无法识别，1中标，2中标无金额，3单价中标，4按比例提成，5未中标，6废标，7重复，8入围")
    private String status;
    private Integer intStatus;
    /** 模板类型*/
    private String type;
    /** 模板识别准确率*/
    private Double accuracyRate;
    /** 提取的金额*/
    @ApiModelProperty(value = "中标金额")
    private String amount;
    // 是否已进行过汇总
    private Boolean summaryed;
    // 历史汇总金额
    private String summaryAmount;

    public ZtbDocument(String entName, String title, String date, String content,String phone) {
        this.entName = entName;
        this.title = title;
        this.date = date;
        this.content = content;
        this.id = title.hashCode();
        this.phone = phone;
    }

    public ZtbDocument(String entName, String title, String date, String content) {
        this.entName = entName;
        this.title = title;
        this.date = date;
        this.content = content;
        if(title != null){
            this.id = title.hashCode();
        }
    }

    public ZtbDocument(String entName, String title, String date, String content, String keyContent, String status, String type, double accuracyRate, String amount) {
        this.entName = entName;
        this.title = title;
        this.date = date;
        this.content = content;
        this.keyContent = keyContent;
        this.status = status;
        this.intStatus = transformStatus(status);
        this.type = type;
        this.accuracyRate = accuracyRate;
        this.amount = amount;
        if(title != null){
            this.id = title.hashCode();
        }
    }
    public ZtbDocument(String entName, String title, String date, String content, String keyContent, String status, String type, double accuracyRate, String amount,String phone) {
        this.entName = entName;
        this.title = title;
        this.date = date;
        this.content = content;
        this.keyContent = keyContent;
        this.status = status;
        this.intStatus = transformStatus(status);
        this.type = type;
        this.accuracyRate = accuracyRate;
        this.amount = amount;
        this.id = title.hashCode();
        this.phone = phone;
    }

    public ZtbDocument() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getKeyContent() {
        return keyContent;
    }

    public void setKeyContent(String keyContent) {
        this.keyContent = keyContent;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getIntStatus() {
        return intStatus;
    }

    public void setIntStatus(Integer intStatus) {
        this.intStatus = intStatus;
    }

    public boolean isSummaryed() {
        return summaryed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(double accuracyRate) {
        this.accuracyRate = accuracyRate;
    }

    public Boolean getSummaryed() {
        return summaryed;
    }

    public void setSummaryed(boolean summaryed) {
        this.summaryed = summaryed;
    }

    public String getSummaryAmount() {
        return summaryAmount;
    }

    public void setSummaryAmount(String summaryAmount) {
        this.summaryAmount = summaryAmount;
    }

    public static int transformStatus(String status){
        for(int i = 0;i< Const.HITSTATUS.length;i++){
            if(Const.HITSTATUS[i].equals(status)){
                return i;
            }
        }
        return 0;
    }
}
