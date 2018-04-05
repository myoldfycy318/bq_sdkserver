package com.qbao.sdk.server.shangsu;

/**
 * TradeItem
 * 商肃交易行项数据
 *
 * @author Zhang ShanMin
 * @date 2016/5/16
 * @time 0:28
 */
public class TradeItem {

    //行项流水,要求业务侧保证唯一性
    private String itemTradeNo;
    //1：人民币，2：宝券
    private String payType;
    //金额
    private Long amount;
    //明细业务类型
    /**
     * 用户使用宝币对游戏充值(P090201)
     * 用户使用宝券对游戏充值(P090202)
     */
    private String itemBusiType;

    public String getItemTradeNo() {
        return itemTradeNo;
    }

    public void setItemTradeNo(String itemTradeNo) {
        this.itemTradeNo = itemTradeNo;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getItemBusiType() {
        return itemBusiType;
    }

    public void setItemBusiType(String itemBusiType) {
        this.itemBusiType = itemBusiType;
    }
}
