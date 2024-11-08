package top.nino.chatbilibili;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.nino.chatbilibili.hook.GlobalViewInterceptor;
import top.nino.chatbilibili.service.GlobalSettingFileService;
import top.nino.service.command.CommandService;


/**
 * @author nino
 */
@ServletComponentScan
@SpringBootApplication
public class ChatBilibiliApplication implements WebMvcConfigurer, CommandLineRunner{

	@Autowired
	private GlobalSettingFileService globalSettingFileService;

	@Autowired
	private GlobalViewInterceptor globalViewInterceptor;

	@Autowired
	private CommandService commandService;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		globalSettingFileService.createOrLoadSettingFile();
		registry.addInterceptor(globalViewInterceptor).addPathPatterns("/**");
	}

	public static void main(String[] args) {
		SpringApplication.run(ChatBilibiliApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		commandService.startApplication();
	}



}
