package cz.gattserver.grass.monitor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("monitorSection")
public class MonitorSection implements SectionService {

	private static Logger logger = LoggerFactory.getLogger(MonitorSection.class);

	private static final long ONCE_PER_DAY = 1L * 1000 * 60 * 60 * 24;

	@Resource(name = "monitorPageFactory")
	private PageFactory monitorPageFactory;

	@Autowired
	private MonitorEmailNotifier emailNotifier;

	@PostConstruct
	private final void init() {
		logger.info("MonitorSection init");
		TimerTask fetchMail = emailNotifier.getTimerTask();
		Timer timer = new Timer();
		LocalDateTime ldt = LocalDateTime.now().plusDays(1).withHour(3);
		Date tomorrowMorning4am = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		timer.scheduleAtFixedRate(fetchMail, tomorrowMorning4am, ONCE_PER_DAY);
	}

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(CoreRole.ADMIN);
	}

	public PageFactory getSectionPageFactory() {
		return monitorPageFactory;
	}

	public String getSectionCaption() {
		return "System";
	}

	@Override
	public Role[] getSectionRoles() {
		return new Role[0];
	}

}
