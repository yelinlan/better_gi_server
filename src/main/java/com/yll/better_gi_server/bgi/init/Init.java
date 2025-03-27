package com.yll.better_gi_server.bgi.init;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.yll.better_gi_server.bgi.config.LogFileConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;

/**
 *@项目名称: better_gi_server
 *@类名称: Init
 *@类描述:
 *@创建人: yll
 *@创建时间: 2025/2/14 20:19
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class Init implements CommandLineRunner {

	private final LogFileConfig logFileConfig;
	@Value("${server.port}")
	private int serverPort;
	@Value("${server.servlet.context-path}")
	private String contextPath;

	@Override
	public void run(String... args) throws Exception {
		String script = System.getProperty("user.dir") + File.separator + "nircmd.exe";
		if (!FileUtil.exist(script)) {
			log.info(script);
			log.info("正在生成...");
			try (InputStream inputStream = ResourceUtil.getResource("script/nircmd.exe").openStream();
					BufferedOutputStream outputStream = FileUtil.getOutputStream(script);) {
				IoUtil.copy(inputStream, outputStream);
			}
		}
		log.info("【better gi】 回调接口是：http://{}:{}{}/{}", logFileConfig.getIp(), serverPort, contextPath,
				"shutdown");
		//删除昨天的日志文件
		FileUtil.del(logFileConfig.getPath() + DateUtil.format(DateUtil.yesterday(), logFileConfig.getTimeStamp())
				+ logFileConfig.getSuffix());
		//获取当前日期，格式为 yyyymmdd
		String fileName = logFileConfig.getPath() + DateUtil.format(DateUtil.date(), logFileConfig.getTimeStamp())
				+ logFileConfig.getSuffix();
		//检查文件是否存在
		if (!FileUtil.exist(fileName)) {
			//不存在则创建
			FileUtil.touch(fileName);
			//检查文件是否成功创建
			if (!FileUtil.exist(fileName)) {
				//创建失败，抛出异常
				throw new RuntimeException("文件创建失败");
			}
			//文件fileName创建成功
			log.info("文件{}创建成功", fileName);
			//静音
			Runtime.getRuntime().exec("nircmd.exe mutesysvolume 1");
			//启动BGI 一条龙程序。"C:\Program Files\BetterGI\BetterGI.exe" startOneDragon
			Runtime.getRuntime().exec(logFileConfig.getStartScript());
		} else {
			// 文件存在，不静音
			Runtime.getRuntime().exec("nircmd.exe mutesysvolume 0");
			log.info("文件{}已存在（今日任务已经执行）", fileName);
			log.info("{}秒后，将自动关闭窗口", logFileConfig.getCloseSecond());
			//睡眠1min
			Thread.sleep(logFileConfig.getCloseSecond() * 1000);
			//退出应用
			System.exit(0);
		}
	}
}