package xyz.ymtao.entity;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2021/1/8  9:51
 */
public class TemplateType implements Serializable {
    @Id
    private int id;
    /** 模板名称*/
    private String name;
    /** 准确率*/
    private double accuracyRate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(double accuracyRate) {
        this.accuracyRate = accuracyRate;
    }

    public TemplateType(String name) {
        this.name = name;
        this.id = name.hashCode();
        this.accuracyRate = 0.8;
    }

    public TemplateType(String name, double accuracyRate) {
        this.name = name;
        this.accuracyRate = accuracyRate;
        this.id = name.hashCode();
    }

    public TemplateType() {
    }
}
