package xyz.ymtao.entity;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2020/12/21  12:55
 */
public class ZtbDocument implements Serializable {
    private String entName;
    @Id
    private int id;
    private String title;
    private String date;
    private String content;
    private String keyContent;
    /** 解析状态*/
    private String status;
    /** 模板类型*/
    private String type;
    /** 模板识别准确率*/
    private double accuracyRate;
    /** 提取的金额*/
    private String amount;

    public ZtbDocument(String entName, String title, String date, String content) {
        this.entName = entName;
        this.title = title;
        this.date = date;
        this.content = content;
        this.id = title.hashCode();
    }

    public ZtbDocument(String entName, String title, String date, String content, String keyContent, String status, String type, double accuracyRate, String amount) {
        this.entName = entName;
        this.title = title;
        this.date = date;
        this.content = content;
        this.keyContent = keyContent;
        this.status = status;
        this.type = type;
        this.accuracyRate = accuracyRate;
        this.amount = amount;
        this.id = title.hashCode();
    }

    public ZtbDocument() {
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
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
