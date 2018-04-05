package com.qbao.sdk.server.response.statistic;

import java.math.BigDecimal;
import java.util.List;

/**
 * SOP 用户产出--响应
 * 
 * @author xuefeihu
 *
 */
public class SopUserOutputResponse {

	/** 近日概况 */
	private List<SdkTrans> currentList;

	/** 付费金额 */
	private List<Node> payAmountList;

	/** 付费次数 */
	private List<Node> payTimesList;

	/** 付费用户 */
	private List<Node> payUserList;

	/** 付费转化率 */
	private List<Node> convertRateList;

	/** arpuList */
	private List<Node> arpuList;

	/** arppuList */
	private List<Node> arppuList;

	public List<SdkTrans> getCurrentList() {
		return currentList;
	}

	public void setCurrentList(List<SdkTrans> currentList) {
		this.currentList = currentList;
	}

	public List<Node> getPayAmountList() {
		return payAmountList;
	}

	public void setPayAmountList(List<Node> payAmountList) {
		this.payAmountList = payAmountList;
	}

	public List<Node> getPayTimesList() {
		return payTimesList;
	}

	public void setPayTimesList(List<Node> payTimesList) {
		this.payTimesList = payTimesList;
	}

	public List<Node> getPayUserList() {
		return payUserList;
	}

	public void setPayUserList(List<Node> payUserList) {
		this.payUserList = payUserList;
	}

	public List<Node> getConvertRateList() {
		return convertRateList;
	}

	public void setConvertRateList(List<Node> convertRateList) {
		this.convertRateList = convertRateList;
	}

	public List<Node> getArpuList() {
		return arpuList;
	}

	public void setArpuList(List<Node> arpuList) {
		this.arpuList = arpuList;
	}

	public List<Node> getArppuList() {
		return arppuList;
	}

	public void setArppuList(List<Node> arppuList) {
		this.arppuList = arppuList;
	}

	/**
	 * 统计图表节点
	 */
	static class Node {

		/** 日期 */
		private String date;
		/** 值 */
		private Long value;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public Long getValue() {
			return value;
		}

		public void setValue(Long value) {
			this.value = value;
		}

	}

	/**
	 * 展示流水列表
	 */
	static class SdkTrans {

		/** 日期 */
		private String date;
		/** 宝币流水 */
		private BigDecimal accountAmount;
		/** 宝劵流水 */
		private BigDecimal bqAccountAmount;
		/** 宝币折现 */
		private BigDecimal bbTotalAmount;
		/** 宝劵折现 */
		private BigDecimal bqTotaAmount;
		/** 折现总额 */
		private BigDecimal totalAmount;
		/** 付费用户数 */
		private Integer payUserCount;
		/** 付费转化率 */
		private String payRate;
		/** ARPU */
		private String ARPU;
		/** ARPPU */
		private String ARPPU;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public BigDecimal getAccountAmount() {
			return accountAmount;
		}

		public void setAccountAmount(BigDecimal accountAmount) {
			this.accountAmount = accountAmount;
		}

		public BigDecimal getBqAccountAmount() {
			return bqAccountAmount;
		}

		public void setBqAccountAmount(BigDecimal bqAccountAmount) {
			this.bqAccountAmount = bqAccountAmount;
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

		public BigDecimal getTotalAmount() {
			return totalAmount;
		}

		public void setTotalAmount(BigDecimal totalAmount) {
			this.totalAmount = totalAmount;
		}

		public Integer getPayUserCount() {
			return payUserCount;
		}

		public void setPayUserCount(Integer payUserCount) {
			this.payUserCount = payUserCount;
		}

		public String getPayRate() {
			return payRate;
		}

		public void setPayRate(String payRate) {
			this.payRate = payRate;
		}

		public String getARPU() {
			return ARPU;
		}

		public void setARPU(String aRPU) {
			ARPU = aRPU;
		}

		public String getARPPU() {
			return ARPPU;
		}

		public void setARPPU(String aRPPU) {
			ARPPU = aRPPU;
		}

	}

}
