package org.springframework.boot.launchscript.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author weibb
 */
public class DemoSpringApplicationRunListener implements SpringApplicationRunListener {

	private static final Logger log = LoggerFactory.getLogger(DemoSpringApplicationRunListener.class);

	public DemoSpringApplicationRunListener(final SpringApplication application, final String[] args) {

	}

	@Override
	public void starting() {
		log.info("############################# haha ###############");
		log.info("DemoSpringApplicationRunListener.starting");
	}

	@Override
	public void environmentPrepared(ConfigurableEnvironment environment) {

	}

	@Override
	public void contextPrepared(ConfigurableApplicationContext context) {

	}

	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {

	}

	@Override
	public void started(ConfigurableApplicationContext context) {

	}

	@Override
	public void running(ConfigurableApplicationContext context) {

	}

	@Override
	public void failed(ConfigurableApplicationContext context, Throwable exception) {

	}
}
