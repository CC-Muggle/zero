package com.sunfintech.provider.api;

/**
 * 
 * @author yangcj
 *
 */
public interface AuthService {

	/**
	 * 登录
	 * @param userName
	 * @param password
	 * @return
	 */
	String login(String userName, String password);
}
