package com.qbao.sdk.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 描述：
 * 配置文件加载
 * @author hexiaoyi
 */
public class PropertiesUtil {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	private static Properties properties = new Properties();

	private String filePath;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 初始化方法,如果初始化不成功，环境无法启动
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public void init() {
		InputStream in = PropertiesUtil.class.getResourceAsStream(filePath);
		BufferedReader bf=null;
		if (in == null) {
			log.info("properties not found");
		} else {
			try {
				 bf = new BufferedReader(new InputStreamReader(in));
				properties.load(bf);
			} catch (IOException e) {
				log.info("Error while processing properties file");
			} finally {
				if(null!=	bf){
					try {
						// 关闭流
						bf.close();
					} catch (IOException e) {
						// 关闭流失败
						log.error("close stream failed", e);
					}
				}
				if (null != in) {
					try {
						// 关闭流
						in.close();
					} catch (IOException e) {
						// 关闭流失败
						log.error("close stream failed", e);
					}
				}
			}
		}
	}

	/**
	 * get a properties value
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return properties.getProperty(key);
	}

	/**
	 * get a properties int value
	 * 
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		return Integer.parseInt(properties.getProperty(key));
	}

}
