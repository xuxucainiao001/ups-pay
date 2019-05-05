package com.pgy.ups.pay.yeepay.servlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.alibaba.druid.support.http.StatViewServlet;

@WebServlet(urlPatterns = "/druid/*", initParams = { /*@WebInitParam(name = "loginUsername", value = "ups-pay"),
		@WebInitParam(name = "loginPassword", value = "123456"),*/ @WebInitParam(name = "resetEnable", value = "false") })
public class DruidStatViewServlet extends StatViewServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7852411221430592449L;

}
