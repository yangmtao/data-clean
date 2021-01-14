package xyz.ymtao.entity;

import org.springframework.data.annotation.Id;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2021/1/11  16:43
 */
public class ZtbSummary {
    @Id
    private int Id;

    private String entName;
    // 中标数量
    private int hitNum;
    // 中标总金额
    private String amount;

    public ZtbSummary(String entName, int hitNum, String amount) {
        this.entName = entName;
        this.hitNum = hitNum;
        this.amount = amount;
        if(this.entName != null){
            this.Id = entName.hashCode();
        }
    }

    public ZtbSummary() {}

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
        if(this.entName != null){
            this.Id = entName.hashCode();
        }
    }

    public int getHitNum() {
        return hitNum;
    }

    public void setHitNum(int hitNum) {
        this.hitNum = hitNum;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
