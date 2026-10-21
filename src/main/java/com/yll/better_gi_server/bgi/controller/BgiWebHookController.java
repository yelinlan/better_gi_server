package com.yll.better_gi_server.bgi.controller;

import cn.hutool.json.JSONUtil;
import com.yll.better_gi_server.bgi.config.LogFileConfig;
import com.yll.better_gi_server.bgi.controller.enums.EventEnum;
import com.yll.better_gi_server.bgi.controller.req.WebHookReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 *@项目名称: better_gi_server
 *@类名称: BgiWebHookController
 *@类描述: webHook 接口
 *@创建人: yll
 *@创建时间: 2025/2/13 19:36
 **/
@RequestMapping("/")
@RestController
@Slf4j
@RequiredArgsConstructor
public class BgiWebHookController {

	private final LogFileConfig logFileConfig;

	/**
	 * 接收webHook
	 *
	 * @param webHookReq 请求参数
	 * @return 响应结果
	 */
	@RequestMapping("/shutdown")
	public String shutdown(@RequestBody WebHookReq webHookReq) throws IOException, InterruptedException {
		EventEnum eventEnum = EventEnum.getEnumByCode(webHookReq.getEvent());
		if (eventEnum == null) {
			log.warn("收到未知事件！{}", webHookReq.getTimestamp() + ": " + JSONUtil.toJsonPrettyStr(webHookReq) + "\n");
		}
		if (!webHookReq.getResult().equals("0") || webHookReq.getEvent().contains("error")) {
			log.error("【{}】 收到失败事件！", eventEnum != null ? eventEnum.getDescription() : webHookReq.getEvent());
			exit();
		}
		if (eventEnum != null) {
			log.info("收到成功事件！{}", JSONUtil.toJsonPrettyStr(webHookReq));
			int index = EventEnum.index(webHookReq.getEvent(), logFileConfig.getEvent());
			if (index != -1) {
				if (index == logFileConfig.getEvent().length - 1) {
					//正常执行完成
					log.info(logFileConfig.getEvent()[index].getDescription() + " 执行完毕，恢复音量！");
					exit();
				} else {
					log.info("【{}】 执行完毕！", logFileConfig.getEvent()[index].getDescription());
				}
			}
		}
		return "test";
	}

	/**
	 * 退出
	 */
	private void exit() throws IOException, InterruptedException {
		Runtime.getRuntime().exec(logFileConfig.getEndScript()[1]);
		//睡眠3000ms 保证BGI被关闭
		Thread.sleep(3000);
		log.info("恢复音量！");
		Runtime.getRuntime().exec("net start \"Audiosrv\"");
		//启动BGI 下一条脚本
		cancelShutdown();
	}

	/**
	 * 启动取消关机线程
	 */
	private void cancelShutdown() {
		new Thread(() -> {
			try {
				//按下任意键后就执行取消关机命令
				log.info("按下任意键取消关机！");
				System.in.read();
				Runtime.getRuntime().exec("shutdown -a");
				log.info("{}s后退出应用！", logFileConfig.getCloseSecond());
				Thread.sleep(logFileConfig.getCloseSecond() * 1000);
				System.exit(0);
			} catch (Exception ignored) {
			}
		}).start();
	}
}