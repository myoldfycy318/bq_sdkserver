/**
 * Copyright (C) 2006-2012 Qianbao All rights reserved
 * Author: xuefeihu
 * Date: 2015年9月30日
 * Description: StoreAwardConfSortUtils.java 
 * Version: 1.0
 * Since: 1.0
 * Location: NanJin China
 */
package com.qbao.sdk.server.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.qbao.sdk.server.metadata.entity.StoreAwardConfEntity;

/**
 * 宝券自动发放排序 （降序）
 */
public class StoreAwardConfSortUtils {

	public static void main(String[] args) {
		List<StoreAwardConfEntity> list = new ArrayList<StoreAwardConfEntity>();
		for (int i = 0; i <= 10; i++) {
			StoreAwardConfEntity temp = new StoreAwardConfEntity();
			temp.setAccountAmount(new BigDecimal(i));
			list.add(temp);
		}
		System.out.println("排序前：");
		print(list);
		sortArray(list);
		System.out.println("排序后：");
		print(list);
	}

	/**
	 * 排序行程信息
	 * 
	 * @param list
	 *            行程信息
	 */
	public static void sortArray(List<StoreAwardConfEntity> list) {
		for (int i = 0; i < list.size(); i++) {
			createMinHeap(list, list.size() - 1 - i);
			swap(list, 0, list.size() - 1 - i);
		}
	}

	/**
	 * 交换无序区最大值和最后一个元素
	 * 
	 * @param data
	 *            堆数据
	 * @param i
	 * @param j
	 */
	private static void swap(List<StoreAwardConfEntity> data, int i, int j) {
		if (i == j) {
			return;
		}
		StoreAwardConfEntity temp = data.get(i);
		data.set(i, data.get(j));
		data.set(j, temp);
	}

	/**
	 * 创建小根堆
	 * 
	 * @param data
	 * @param lastIndex
	 */
	private static void createMinHeap(List<StoreAwardConfEntity> data, int lastIndex) {
		for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
			// 保存当前正在判断的节点
			int k = i;
			// 若当前节点的子节点存在
			while (2 * k + 1 <= lastIndex) {
				// biggerIndex总是记录较大节点的值,先赋值为当前判断节点的左子节点
				int biggerIndex = 2 * k + 1;
				if (biggerIndex < lastIndex) {
					// 若右子节点存在，否则此时biggerIndex应该等于 lastIndex
					if (data.get(biggerIndex).getAccountAmount().doubleValue() > data.get(biggerIndex + 1)
							.getAccountAmount().doubleValue()) {
						biggerIndex++;// 若右子节点值比左子节点值大，则biggerIndex记录的是右子节点的值
					}
				}
				if (data.get(k).getAccountAmount().doubleValue() > data.get(biggerIndex).getAccountAmount()
						.doubleValue()) {
					swap(data, k, biggerIndex);// 若当前节点值比子节点最大值小，则交换2者得值，交换后将biggerIndex值赋值给k
					k = biggerIndex;
				} else {
					break;
				}
			}
		}
	}

	private static void print(List<StoreAwardConfEntity> data) {
		for (int i = 0; i < data.size(); i++) {
			System.out.print(data.get(i).getAccountAmount() + "\t");
		}
		System.out.println();
	}

}
