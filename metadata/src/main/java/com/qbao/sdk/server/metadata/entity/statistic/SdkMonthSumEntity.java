package com.qbao.sdk.server.metadata.entity.statistic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SdkMonthSumEntity implements Serializable {
    /**
     * 月结ID.
     */
    private long monthSumId;

    /**
     * 结算月份.
     */
    private String settleMonth;

    /**
     * 商户编码.
     */
    private String merchantCode;

    /**
     * 商户名称.
     */
    private String merchantName;

    /**
     * 应用名称.
     */
    private String appCode;

    /**
     * 应用名称.
     */
    private String appName;

    /**
     * 应用平台.
     */
    private int appSource;

    /**
     * 计费点编码.
     */
    private String chargingPointCode;

    /**
     * 计费点名称.
     */
    private String chargingPointName;

    /**
     * 待结算总金额.
     */
    private BigDecimal settleTotalAmount;

    /**
     * 宝币金额.
     */
    private BigDecimal bbTotalAmount;

    /**
     * 宝券金额.
     */
    private BigDecimal bqTotaAmount;

    /**
     * 商户应结算金额.
     */
    private BigDecimal shouldSettleAmount;

    /**
     * 实际支付金额.
     */
    private BigDecimal realSettleAmount;

    /**
     * 状态 10-待结算 20-待生效 30-生效.
     */
    private String settleStatus;

    /**
     * 结算日期.
     */
    private String settleDate;

    /**
     * 创建时间.
     */
    private Date createTime;

    /**
     * 更新时间.
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
	
	/**
	 * 付费用户数
	 */
	private int payUserCount;
	
	/**
	 * 付费次数
	 */
	private long payCount;
	
    /**
     * 下载次数.
     */
    private int downTimes;

    /**
     * 下载用户数.
     */
    private int userCounts;
    
    private double qbbCash;
    
    private double bqCash;
    
    private double cashSum;
    
    public long getMonthSumId() {
        return monthSumId;
    }

    public void setMonthSumId(long monthSumId) {
        this.monthSumId = monthSumId;
    }

    public String getSettleMonth() {
        return settleMonth;
    }

    public void setSettleMonth(String settleMonth) {
        this.settleMonth = settleMonth;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getAppSource() {
        return appSource;
    }

    public void setAppSource(int appSource) {
        this.appSource = appSource;
    }

    public String getChargingPointCode() {
        return chargingPointCode;
    }

    public void setChargingPointCode(String chargingPointCode) {
        this.chargingPointCode = chargingPointCode;
    }

    public String getChargingPointName() {
        return chargingPointName;
    }

    public void setChargingPointName(String chargingPointName) {
        this.chargingPointName = chargingPointName;
    }

    public BigDecimal getSettleTotalAmount() {
        return settleTotalAmount;
    }

    public void setSettleTotalAmount(BigDecimal settleTotalAmount) {
        this.settleTotalAmount = settleTotalAmount;
    }

    public BigDecimal getBbTotalAmount() {
        return bbTotalAmount;
    }

    public void setBbTotalAmount(BigDecimal bbTotalAmount) {
        this.bbTotalAmount = bbTotalAmount;
    }

    public BigDecimal getBqTotaAmount() {
        return bqTotaAmount;
    }

    public void setBqTotaAmount(BigDecimal bqTotaAmount) {
        this.bqTotaAmount = bqTotaAmount;
    }

    public BigDecimal getShouldSettleAmount() {
        return shouldSettleAmount;
    }

    public void setShouldSettleAmount(BigDecimal shouldSettleAmount) {
        this.shouldSettleAmount = shouldSettleAmount;
    }

    public BigDecimal getRealSettleAmount() {
        return realSettleAmount;
    }

    public void setRealSettleAmount(BigDecimal realSettleAmount) {
        this.realSettleAmount = realSettleAmount;
    }

    public String getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(String settleStatus) {
        this.settleStatus = settleStatus;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public int getPayUserCount() {
		return payUserCount;
	}

	public void setPayUserCount(int payUserCount) {
		this.payUserCount = payUserCount;
	}

	public long getPayCount() {
		return payCount;
	}

	public void setPayCount(long payCount) {
		this.payCount = payCount;
	}

	public int getDownTimes() {
		return downTimes;
	}

	public void setDownTimes(int downTimes) {
		this.downTimes = downTimes;
	}

	public int getUserCounts() {
		return userCounts;
	}

	public void setUserCounts(int userCounts) {
		this.userCounts = userCounts;
	}

	public double getQbbCash() {
		return qbbCash;
	}

	public void setQbbCash(double qbbCash) {
		this.qbbCash = qbbCash;
	}

	public double getBqCash() {
		return bqCash;
	}

	public void setBqCash(double bqCash) {
		this.bqCash = bqCash;
	}

	public double getCashSum() {
		return cashSum;
	}

	public void setCashSum(double cashSum) {
		this.cashSum = cashSum;
	}
    
}