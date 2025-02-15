package com.yll.better_gi_server.bgi.controller.req;

import lombok.Data;

/**
 *@项目名称: better_gi_server
 *@类名称: WebHookReq
 *@类描述:
 *@创建人: yll
 *@创建时间: 2025/2/14 19:55
 **/
@Data
public class WebHookReq {
	private String event;
	private String result;
	private String timestamp;
	private String screenshot;
	private String message;
}