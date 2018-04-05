package com.qbao.sdk.server.util.rsa;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSACoder extends Coder
{

	public static final String KEY_ALGORITHM = "RSA";

	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	private static final String PUBLIC_KEY = "RSAPublicKey";

	private static final String PRIVATE_KEY = "RSAPrivateKey";

	/**
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param privateKey
	 *            私钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception
	{
		// 解密由base64编码的私钥
		byte[] keyBytes = decryptBASE64(privateKey);

		// 构造PKCS8EncodedKeySpec对象
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取私钥匙对象
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);

		return encryptBASE64(signature.sign());
	}

	/**
	 * 校验数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            数字签名
	 * 
	 * @return 校验成功返回true 失败返回false
	 * @throws Exception
	 * 
	 */
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception
	{

		// 解密由base64编码的公钥
		byte[] keyBytes = decryptBASE64(publicKey);

		// 构造X509EncodedKeySpec对象
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取公钥匙对象
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pubKey);
		signature.update(data);

		// 验证签名是否正常
		return signature.verify(decryptBASE64(sign));
	}

	/**
	 * 解密<br>
	 * 用私钥解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception
	{
		// 对密钥解密
		byte[] keyBytes = decryptBASE64(key);

		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		return cipher.doFinal(data);
	}

	/**
	 * 解密<br>
	 * 用公钥解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception
	{
		// 对密钥解密
		byte[] keyBytes = decryptBASE64(key);

		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicKey = keyFactory.generatePublic(x509KeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);

		return cipher.doFinal(data);
	}

	/**
	 * 加密<br>
	 * 用公钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception
	{
		// 对公钥解密
		byte[] keyBytes = decryptBASE64(key);

		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicKey = keyFactory.generatePublic(x509KeySpec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		return cipher.doFinal(data);
	}

	/**
	 * 加密<br>
	 * 用私钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception
	{
		// 对密钥解密
		byte[] keyBytes = decryptBASE64(key);

		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);

		return cipher.doFinal(data);
	}

	/**
	 * 取得私钥
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static String getPrivateKey(Map<String, Object> keyMap) throws Exception
	{
		Key key = (Key) keyMap.get(PRIVATE_KEY);

		return encryptBASE64(key.getEncoded());
	}

	/**
	 * 取得公钥
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static String getPublicKey(Map<String, Object> keyMap) throws Exception
	{
		Key key = (Key) keyMap.get(PUBLIC_KEY);

		return encryptBASE64(key.getEncoded());
	}

	/**
	 * 初始化密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> initKey() throws Exception
	{
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);

		keyPairGen.initialize(1024);

		KeyPair keyPair = keyPairGen.generateKeyPair();

		// 公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

		// 私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		Map<String, Object> keyMap = new HashMap<String, Object>(2);

		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}

	public static void main(String[] args) throws Exception
	{

		String s = "wxt1987";
		String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDUn+hM/Ae5ptuusz4aFSaPQtZPucsA4wVoyoA+8hLMTHuEWcdjdKewcf9bFH2krwtpo6CG0CGFBj9ivf8nGzeCc6t68fM5InV0oOxUujXBXhXpEvvDu9F+Yadnv6lx8M0raWRx8uhKUhdzPKEbiaiMJPIh4IdLgzxOYlskYr9DwIDAQAB";
	    String pk = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMNSf6Ez8B7mm266zPhoVJo9C1k+5ywDjBWjKgD7yEsxMe4RZx2N0p7Bx/1sUfaSvC2mjoIbQIYUGP2K9/ycbN4Jzq3rx8zkidXSg7FS6NcFeFekS+8O70X5hp2e/qXHwzStpZHHy6EpSF3M8oRuJqIwk8iHgh0uDPE5iWyRiv0PAgMBAAECgYBx6hWBoNb0Tq5sIAoW/lIJOnz56dPNOaxjmiuPM0kXgXOLUx7+f45NBNtsk9YhpmaVgUQv4VD6YZJJnNkQvPJIb71joPYKVYiGUkzFPrUdmUtSPkuWcaGvx3TefmPibL9TjUAY3408Om6YO54oFIWt5y/sMrsdiFRa9Lu5bREwsQJBAO39sZ2aDQqPRieSPHwnyDHlunpSmQ2XzmjtssoTmwo6rAmG/q6lWp5SX4GZ06rW9BKl2Z0VA0ImzdmF+83lflMCQQDSGjxp3oiZI56BHYfIlFCs9f9omUW8KrQXUv5XJVw1Ba9qv8mpAJURrFdAfljYHa+BFWMMuRrqnR/fHjrzE1bVAkEA37g3SmbxUXbjxPkkILYo7Db/eFPDCtMktuCTzIBno1MKPB6JtVU9fU0D+MnI/3T3lbwQeCizmnDt20inL6NHfwJBAKRe8jTBbIyiWTcaK0i5AATQz+i9QNldb1dwDpuPFvxEXmBdex9E3VreQcSrFEa/sraCTON/TZePJYgg1m2lC6ECQQCuAZ8CRsDrEAGyFgVJRDfmoOfPZHHmKgb2OZXVpssPBT17vO3iP0VATbiaZ7yYymcppgBdL6ktcVChYTaCeojA";
		//String pk = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANrt8uiFjb9J9RJ3Z0S2Hxzhcs3wcNtPybtjBzzuVlUMQB36l6/Qx/+5nGy11Jmq0o52tDi8Mt2m2iv8xf7q3rFSn7DljESrNShgsaHQSEtTrbLXtJkj9VrdkRasYatxKrJbJbBH1Zht3E5tHXfygdHg2qwUBM/0Zl1h4gKjbS9bAgMBAAECgYBynTwcKz8oNAnPXx97Q3VKY2T9F7Gdv0m3hoKVjZk48S7oAvdCniT2Zi/UYeUphdqxpsd3JF/k1qNFgBIQANoQN06Vhn6a4b3/DNixBXI+COtHmRq8BPte7i2zywh+QENXhNyA370nX1jm1Uv4epXMjaYmUg3QuoDDScpDDSKHkQJBAPU2mtKVBBk58V6gukV/SnCbK2eF5L5kMpNOBPzq2RCTEHTSKmw0uFyfCN26x5osypxQ7jC/KBWq44xEAM4IWpUCQQDkj1teCpEiub4oQVwSrWZ1JDk9dR3nHbctzK/gSg8QAy6xyHWqOAyq8Rb0b7unALxy+PJHs4a1lEKtADUkfNYvAkByeHU6DPqG1DfLVvjd7JhRjP5sK2MnRhseDPPjJbcDN8Qc5OeM7+eIxW0cJU98t6A17Mnk07fgtY/YMP63L8spAkAJTfy+O1flPgKLyP1ilUSSiEL8SYRKIF+QscCHNPyf/kEsY6jGoTmCzAXPNa1CAzmoxJG4cYqOYdbFDDkdnydpAkEAvCk2wcbygtUpwxWcgKQAzcE1kk68BktWcMPperv0pOlGes7GN/PkZGi+byXgIi16QlP4wHcixl2alllqUSdApQ==";

//		String sign = RSACoder.sign(s.getBytes(), pk);
//		System.out.println(sign);
//		// sign = URLEncoder.encode(sign, "UTF-8");
//		System.out.println(sign);
//		//sign = URLDecoder.decode(sign, "utf-8");
//		System.out.println(sign);
//		// System.out.println(URLEncoder.encode(sign, "UTF-8"));
//		//
//		String m = "wxt1987";
//		boolean ok = RSACoder.verify(m.getBytes(), pubKey, sign);
//		System.out.println(ok);

		String s1 = "111111" + "Wm4k4C";
		String mima64 = encryptBASE64(encryptByPublicKey(s1.getBytes(), pubKey));
		System.out.println(mima64);
		
		System.out.println(new String(decryptByPrivateKey(RSACoder.decryptBASE64(mima64), pk)));


	}

}
