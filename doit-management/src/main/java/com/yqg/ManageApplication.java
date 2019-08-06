/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg;

import com.yqg.common.converter.CustomerMappingJackson2HttpMessageConverter;
import com.yqg.manage.util.SpringContextUtil;
import com.yqg.service.third.Inforbip.InforbipCollectionService;
import com.yqg.service.third.twilio.request.TwilioCallResultRequest;
import com.yqg.system.entity.TwilioCallResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@EnableScheduling
@Slf4j
@EnableTransactionManagement(order = 10)
@ComponentScan(basePackages = "com.yqg",
		excludeFilters = @ComponentScan.Filter(type =FilterType.ASSIGNABLE_TYPE,value = {CustomerMappingJackson2HttpMessageConverter.class}))
public class ManageApplication {
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(ManageApplication.class, args);
		SpringContextUtil.setApplicationContext(ctx);
		String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
		for (String profile : activeProfiles) {
			log.warn("Spring Boot use profile :{}", profile);
		}
//		TwilioService twilioService1 = ctx.getBean(TwilioService.class);
//		TwilioWhatsAppRecordRequest request = new TwilioWhatsAppRecordRequest();
//		request.setBatchNo(String.valueOf(System.currentTimeMillis() + "D-1"));
//		request.setDays(-1);
//		request.setReplyContent("Halo  Nasabah  terhormat,  terima  kasih  anda  telah  mendaftar  di  aplikasi  Do-it.  Saat  ini  melalui  WA  kami  ingin  memastikan  kepada  anda  apakah  anda  benar-benar  meminjam  \\nSilahkan  menjawab  berdasarkan  pilihan  berikut\\nA:  Benar  saya  sendiri,  lanjutkan  meminjam\\nB:  Benar  saya  sendiri,  tapi  tidak  ingin  melanjutkan  meminjam\\nC:  Bukan  saya\\nD:  Saya  tidak  tahu  tentang  Do-it\\nApabila  anda  memiliki  pertanyaan  lain,  silahkan  langsung  membalas  pesan  ini.");
//		twilioService1.startWhatsAppTwilio(request);

//		InforbipCollectionService service = ctx.getBean(InforbipCollectionService.class);
//		TwilioCallResultRequest request = new TwilioCallResultRequest();
//		request.setCallPhase("D-1");
//		request.setCallPhaseType(TwilioCallResult.CallPhaseTypeEnum.CALL_PHASE_ALL.getCode());
////            request.setCallUrl(config.getUrl() + "D-1/twilioXml");
//		request.setCallUrl("http://h5.do-it.id/twilio/D-1.mp3");
//		request.setDays(-1);
//		request.setBatchNo(String.valueOf(System.currentTimeMillis() + "D-1"));
////            twilioService.startCallTwilio(request);
//		try {
//			service.startCallInfobip(request);
//		} catch (Exception e) {
//		}
	}
}

