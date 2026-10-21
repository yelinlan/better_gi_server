package com.yll.better_gi_server.bgi.controller.req;

import lombok.Data;

/**
 * webHook 请求参数
 */
@Data
public class WebHookReq {

	/**
	 * 事件类型
	 */
	private String event;

	/**
	 * 执行结果
	 */
	private String result;

	/**
	 * 时间戳
	 */
	private String timestamp;

	/**
	 * 截图信息
	 */
	private String screenshot;

	/**
	 * 消息内容
	 */
	private String message;

}
