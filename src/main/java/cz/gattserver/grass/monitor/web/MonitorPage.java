package cz.gattserver.grass.monitor.web;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.flow.component.DetachEvent;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.services.MailService;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.TableLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.monitor.processor.item.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass.monitor.MonitorEmailNotifier;
import cz.gattserver.grass.monitor.MonitorSection;
import cz.gattserver.grass.monitor.services.MonitorService;
import cz.gattserver.grass.monitor.web.label.ErrorMonitorStateLabel;
import cz.gattserver.grass.monitor.web.label.MonitorOutputLabel;
import cz.gattserver.grass.monitor.web.label.SuccessMonitorStateLabel;
import elemental.json.JsonObject;
import elemental.json.JsonType;

@Route("system-monitor")
@PageTitle("System monitor")
public class MonitorPage extends OneColumnPage {

	private static final long serialVersionUID = 4862261730750923131L;

	@Autowired
	private MonitorService monitorFacade;

	@Autowired
	private MonitorEmailNotifier emailNotifier;

	@Autowired
	private MailService mailService;

	private final String TIMEOUTS_JS_ARRAY = "timeoutsArray";

	private VerticalLayout layout;
	private VerticalLayout serversLayout;
	private VerticalLayout uptimeLayout;
	private VerticalLayout memoryStatusLayout;
	private VerticalLayout systemSwapStatusLayout;

	private VerticalLayout jvmUptimeLayout;
	private VerticalLayout jvmPIDLayout;
	private VerticalLayout jvmThreadsLayout;
	private VerticalLayout jvmMemoryLayout;

	private VerticalLayout backupLayout;

	private VerticalLayout diskLayout;

	private VerticalLayout smartLayout;

	private VerticalLayout servicesLayout;

	public MonitorPage() {
		if (!SpringContextHelper.getBean(MonitorSection.class).isVisibleForRoles(getUser().getRoles()))
			throw new GrassPageException(403);
		loadCSS(getContextPath() + "/VAADIN/monitor/style.css");
		init();
	}

	private String humanFormat(long value) {
		return HumanBytesSizeFormatter.format(value, false);
	}

	private String constructUsedTotalFreeDescription(long used, float ratio, long total, long free) {
		return "obsazeno " + humanFormat(used) + " (" + NumberFormat.getIntegerInstance().format(ratio * 100) + "%) z "
				+ humanFormat(total) + "; volno " + humanFormat(free);
	}

	private ProgressBar constructProgressMonitor(float ration, String description) {
		ProgressBar pb = new ProgressBar();
		pb.setValue(ration);
		pb.setWidth("200px");
		return pb;
	}

	private void preparePartHeader(String header) {
		layout.add(new H2(header));
	}

	private TableLayout prepareTableLayout() {
		TableLayout tableLayout = new TableLayout();
		tableLayout.getElement().setAttribute("class", "monitor-table");
		return tableLayout;
	}

	private VerticalLayout preparePartLayout() {
		VerticalLayout partLayout = new VerticalLayout();
		partLayout.setMargin(false);
		partLayout.setPadding(false);
		partLayout.setSpacing(false);
		Image loadingImg = new Image("VAADIN/img/gattload_mini.gif", "loading...");
		loadingImg.getStyle().set("margin", "5px");
		partLayout.add(loadingImg);
		layout.add(partLayout);
		return partLayout;
	}

	private VerticalLayout preparePart(String header) {
		preparePartHeader(header);
		return preparePartLayout();
	}

	private void createServersPart(ServersPartItemTO data) {
		serversLayout.removeAll();
		TableLayout serversTableLayout = prepareTableLayout();
		serversLayout.add(serversTableLayout);
		if (data.getItems().isEmpty()) {
			serversTableLayout.newRow().add(new SuccessMonitorStateLabel()).add(data.getStateDetails());
			return;
		}
		for (URLMonitorItemTO to : data.getItems()) {
			String content = to.getName();
			Anchor anchor = new Anchor(to.getUrl(), to.getUrl());
			anchor.setTarget("_blank");
			switch (to.getMonitorState()) {
				case SUCCESS:
					serversTableLayout.newRow().add(new SuccessMonitorStateLabel()).add(content).add(anchor);
					break;
				case ERROR:
				default:
					serversTableLayout.newRow().add(new ErrorMonitorStateLabel()).add(content).add(anchor);
			}
		}
	}

	private void createSystemUptimePart(SystemUptimeMonitorItemTO uptimeTO) {
		uptimeLayout.removeAll();
		TableLayout uptimeTableLayout = prepareTableLayout();
		uptimeLayout.add(uptimeTableLayout);
		switch (uptimeTO.getMonitorState()) {
			case SUCCESS:
				uptimeTableLayout.newRow().add(new SuccessMonitorStateLabel()).add(uptimeTO.getStateDetails());
				break;
			case ERROR:
			default:
				uptimeTableLayout.newRow().add(new ErrorMonitorStateLabel()).add("System uptime info není dostupné");
		}
	}

	private void createSystemMemoryStatusPart(SystemMemoryMonitorItemTO memoryTO) {
		memoryStatusLayout.removeAll();
		TableLayout memoryStatusTableLayout = prepareTableLayout();
		memoryStatusLayout.add(memoryStatusTableLayout);
		switch (memoryTO.getMonitorState()) {
			case SUCCESS:
				memoryStatusTableLayout.newRow().add(new SuccessMonitorStateLabel())
						.add(constructProgressMonitor(memoryTO.getUsedRation(),
								constructUsedTotalFreeDescription(memoryTO.getUsed(), memoryTO.getUsedRation(),
										memoryTO.getTotal(), memoryTO.getFree())));
				break;
			case ERROR:
			default:
				memoryStatusTableLayout.newRow().add(new ErrorMonitorStateLabel()).add("System memory info není " +
						"dostupné");
		}
	}

	private void createSystemSwapStatusPart(SystemSwapMonitorItemTO swapTO) {
		systemSwapStatusLayout.removeAll();
		TableLayout systemSwapStatusTableLayout = prepareTableLayout();
		systemSwapStatusLayout.add(systemSwapStatusTableLayout);
		swapTO = monitorFacade.getSystemSwapStatus();
		switch (swapTO.getMonitorState()) {
			case SUCCESS:
				systemSwapStatusTableLayout.newRow().add(new SuccessMonitorStateLabel())
						.add(constructProgressMonitor(swapTO.getUsedRation(),
								constructUsedTotalFreeDescription(swapTO.getUsed(), swapTO.getUsedRation(),
										swapTO.getTotal(), swapTO.getFree())));
				break;
			case ERROR:
			default:
				systemSwapStatusTableLayout.newRow().add(new ErrorMonitorStateLabel())
						.add("System swap info není dostupné");
		}
	}

	private void createJVMUptimePart(JVMUptimeMonitorItemTO uptimeTO) {
		jvmUptimeLayout.removeAll();
		TableLayout jvmUptimeTableLayout = prepareTableLayout();
		jvmUptimeLayout.add(jvmUptimeTableLayout);
		switch (uptimeTO.getMonitorState()) {
			case SUCCESS:
				jvmUptimeTableLayout.newRow().add(new SuccessMonitorStateLabel())
						.add(String.format("JVM uptime: %d days, %d hours, %d minutes, %d seconds%n",
								uptimeTO.getElapsedDays(), uptimeTO.getElapsedHours(), uptimeTO.getElapsedMinutes(),
								uptimeTO.getElapsedSeconds()));
				break;
			case ERROR:
			default:
				jvmUptimeTableLayout.newRow().add(new ErrorMonitorStateLabel()).add("JVM uptime info není dostupné");
		}
	}

	private void createJVMPIDPart(JVMPIDMonitorItemTO pidTO) {
		jvmPIDLayout.removeAll();
		TableLayout jvmPIDTableLayout = prepareTableLayout();
		jvmPIDLayout.add(jvmPIDTableLayout);
		switch (pidTO.getMonitorState()) {
			case SUCCESS:
				jvmPIDTableLayout.newRow().add(new SuccessMonitorStateLabel()).add("JVM PID: " + pidTO.getPid());
				break;
			case ERROR:
			default:
				jvmPIDTableLayout.newRow().add(new ErrorMonitorStateLabel()).add("JVM PID info není dostupné");
		}
	}

	private void createJVMThreadsPart(JVMThreadsMonitorItemTO threadsTO) {
		jvmThreadsLayout.removeAll();
		TableLayout jvmThreadsTableLayout = prepareTableLayout();
		jvmThreadsLayout.add(jvmThreadsTableLayout);
		switch (threadsTO.getMonitorState()) {
			case SUCCESS:
				jvmThreadsTableLayout.newRow().add(new SuccessMonitorStateLabel())
						.add("Aktuální stav vláken: " + threadsTO.getCount() + " peak: " + threadsTO.getPeak());
				break;
			case ERROR:
			default:
				jvmThreadsTableLayout.newRow().add(new ErrorMonitorStateLabel()).add("JVM thread info není " +
						"dostupné");
		}
	}

	private void createJVMMemoryPart(JVMMemoryMonitorItemTO memoryTO) {
		jvmMemoryLayout.removeAll();
		TableLayout jvmMemoryTableLayout = prepareTableLayout();
		jvmMemoryLayout.add(jvmMemoryTableLayout);
		switch (memoryTO.getMonitorState()) {
			case SUCCESS:
				float usedRatio = memoryTO.getUsedMemory() / (float) memoryTO.getTotalMemory();
				jvmMemoryTableLayout.newRow().add(new SuccessMonitorStateLabel()).add(constructProgressMonitor(usedRatio,
						constructUsedTotalFreeDescription(memoryTO.getUsedMemory(), usedRatio,
								memoryTO.getTotalMemory(),
								memoryTO.getFreeMemory())));
				jvmMemoryTableLayout.add(" Max memory: " + humanFormat(memoryTO.getMaxMemory()), false);
				break;
			case ERROR:
			default:
				jvmMemoryTableLayout.newRow().add(new ErrorMonitorStateLabel()).add("JVM thread info není dostupné");
		}
	}

	private void createBackupStatusPart(BackupStatusPartItemTO backupPartItemTO) {
		backupLayout.removeAll();
		TableLayout backupTableLayout = prepareTableLayout();
		backupLayout.add(backupTableLayout);

		switch (backupPartItemTO.getMonitorState()) {
			case SUCCESS:
				backupTableLayout.newRow().add(new SuccessMonitorStateLabel()).add("Backup disk je připojen");
				break;
			case ERROR:
				backupTableLayout.newRow().add(new ErrorMonitorStateLabel())
						.add("Backup disk není připojen nebo info" + " " + "není dostupné");
				break;
		}

		if (MonitorState.SUCCESS.equals(backupPartItemTO.getMonitorState())) {
			for (BackupStatusMonitorItemTO lastBackupTO : backupPartItemTO.getItems()) {
				switch (lastBackupTO.getMonitorState()) {
					case SUCCESS ->
							backupTableLayout.newRow().add(new SuccessMonitorStateLabel()).add(lastBackupTO.getValue());
					case ERROR -> backupTableLayout.newRow().add(new ErrorMonitorStateLabel())
							.add(lastBackupTO.getValue() + ": Nebyla provedena pravidelná záloha nebo je starší, než" +
									" " +
									"24h "
									+ "nebo info není dostupné");
				}
			}
		}
	}

	private void createDisksPart(DiskStatusPartItemTO data) {
		diskLayout.removeAll();
		TableLayout diskTableLayout = prepareTableLayout();
		diskLayout.add(diskTableLayout);

		if (MonitorState.ERROR == data.getMonitorState()) {
			diskTableLayout.add(new ErrorMonitorStateLabel()).add("Info není dostupné");
			return;
		}

		diskTableLayout.add(new MonitorOutputLabel("Stav"));
		diskTableLayout.add(new MonitorOutputLabel("Mount")).setColSpan(2);
		diskTableLayout.add(new MonitorOutputLabel("Disk"));
		diskTableLayout.add(new MonitorOutputLabel("Volno")).setColSpan(2);
		diskTableLayout.add(new MonitorOutputLabel("Obsazeno")).setColSpan(2);
		diskTableLayout.add(new MonitorOutputLabel("Velikost")).setColSpan(2);

		for (DiskStatusMonitorItemTO disk : data.getItems()) {
			diskTableLayout.newRow();
			switch (disk.getMonitorState()) {
				case SUCCESS:
					diskTableLayout.add(new SuccessMonitorStateLabel());

					diskTableLayout.add(disk.getMount());

					ProgressBar pb = new ProgressBar();
					pb.setValue(disk.getUsedRation());
					Div pbWrap = new Div();
					pbWrap.setWidth("200px");
					pbWrap.add(pb);
					diskTableLayout.add(pbWrap);

					diskTableLayout.add(disk.getName());

					String usableInfo[] = humanFormat(disk.getUsable()).split(" ");
					diskTableLayout.add(usableInfo[0]);
					diskTableLayout.add(usableInfo[1]);

					String usedInfo[] = humanFormat(disk.getUsed()).split(" ");
					diskTableLayout.add(usedInfo[0]);
					diskTableLayout.add(usedInfo[1]);

					String totalInfo[] = humanFormat(disk.getTotal()).split(" ");
					diskTableLayout.add(totalInfo[0]);
					diskTableLayout.add(totalInfo[1]);
					break;
				case ERROR:
					diskTableLayout.add(new ErrorMonitorStateLabel());
					diskTableLayout.add("Chyba disku nebo info není dostupné");
					break;
			}
		}
	}

	private void createSMARTPart(SMARTPartItemTO data) {
		smartLayout.removeAll();
		TableLayout smartTableLayout = prepareTableLayout();
		smartLayout.add(smartTableLayout);

		if (MonitorState.SUCCESS == data.getMonitorState()) {
			smartTableLayout.newRow().add(new SuccessMonitorStateLabel());
			smartTableLayout.add(data.getStateDetails());
			return;
		} else if (MonitorState.ERROR == data.getMonitorState()) {
			smartTableLayout.newRow().add(new ErrorMonitorStateLabel());
			smartTableLayout.add(data.getStateDetails());
			return;
		}

		// Vyžaduje být ve skupině
		// sudo usermod -a -G systemd-journal tomcat8
		for (SMARTMonitorItemTO to : data.getItems()) {
			// https://www.freedesktop.org/software/systemd/man/journalctl.html
			switch (to.getMonitorState()) {
				case SUCCESS:
					// unused
					break;
				case ERROR:
					smartTableLayout.newRow().add(new ErrorMonitorStateLabel());
					smartTableLayout.add(to.getTime());
					smartTableLayout.add(to.getStateDetails());
					break;
			}
		}
	}

	private void createServicesPart(ServicesPartItemTO data) {
		servicesLayout.removeAll();
		TableLayout servicesTableLayout = prepareTableLayout();
		servicesLayout.add(servicesTableLayout);

		if (MonitorState.SUCCESS == data.getMonitorState()) {
			servicesTableLayout.newRow().add(new SuccessMonitorStateLabel());
			servicesTableLayout.add(data.getStateDetails());
			return;
		} else if (MonitorState.ERROR == data.getMonitorState()) {
			servicesTableLayout.newRow().add(new ErrorMonitorStateLabel());
			servicesTableLayout.add(data.getStateDetails());
			return;
		}

		servicesTableLayout.add(new MonitorOutputLabel("Stav"));
		servicesTableLayout.add(new MonitorOutputLabel("Unit"));
		servicesTableLayout.add(new MonitorOutputLabel("Load"));
		servicesTableLayout.add(new MonitorOutputLabel("Active"));
		servicesTableLayout.add(new MonitorOutputLabel("Sub"));
		servicesTableLayout.add(new MonitorOutputLabel("Description"));

		for (ServicesMonitorItemTO to : data.getItems()) {
			switch (to.getMonitorState()) {
				case SUCCESS:
					// unused
					break;
				case ERROR:
					servicesTableLayout.newRow().add(new ErrorMonitorStateLabel());
					servicesTableLayout.add(to.getUnit());
					servicesTableLayout.add(to.getLoad());
					servicesTableLayout.add(to.getActive());
					servicesTableLayout.add(to.getSub());
					servicesTableLayout.add(to.getStateDetails());
					break;
			}
		}
	}

	@Override
	protected void createColumnContent(Div layout) {
		this.layout = new VerticalLayout();
		this.layout.setSpacing(false);
		this.layout.setPadding(false);
		this.layout.addClassName("monitor-content");
		layout.add(this.layout);
		populateMonitor();
	}

	private void populateMonitor() {
		//layout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);

		// Servers
		serversLayout = preparePart("Servers");

		// System
		preparePartHeader("System");
		uptimeLayout = preparePartLayout();
		memoryStatusLayout = preparePartLayout();
		systemSwapStatusLayout = preparePartLayout();

		// Úložiště
		diskLayout = preparePart("Disk status");

		// Backup disk
		backupLayout = preparePart("Backup");

		// SMARTD
		smartLayout = preparePart("SMARTD");

		// Services
		servicesLayout = preparePart("Services");

		// JVM Overview
		preparePartHeader("JVM Overview");
		jvmUptimeLayout = preparePartLayout();
		jvmPIDLayout = preparePartLayout();
		jvmThreadsLayout = preparePartLayout();
		jvmMemoryLayout = preparePartLayout();

		String jsDivId = "monitor-js-div";
		Div jsDiv = new Div() {
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void monitorRefresh(JsonObject jsonObject) {
				System.out.println(jsonObject);
				if (!jsonObject.hasKey("type"))
					return;
				if (JsonType.NULL == jsonObject.get("type").getType())
					return;
				String type = jsonObject.getString("type");
				if (SystemUptimeMonitorItemTO.class.getName().equals(type)) {
					createSystemUptimePart(new SystemUptimeMonitorItemTO(jsonObject));
				} else if (SystemMemoryMonitorItemTO.class.getName().equals(type)) {
					createSystemMemoryStatusPart(new SystemMemoryMonitorItemTO(jsonObject));
				} else if (SystemSwapMonitorItemTO.class.getName().equals(type)) {
					createSystemSwapStatusPart(new SystemSwapMonitorItemTO(jsonObject));
				} else if (ServersPartItemTO.class.getName().equals(type)) {
					createServersPart(new ServersPartItemTO(jsonObject));
				} else if (BackupStatusPartItemTO.class.getName().equals(type)) {
					createBackupStatusPart(new BackupStatusPartItemTO(jsonObject));
				} else if (JVMMemoryMonitorItemTO.class.getName().equals(type)) {
					createJVMMemoryPart(new JVMMemoryMonitorItemTO(jsonObject));
				} else if (JVMPIDMonitorItemTO.class.getName().equals(type)) {
					createJVMPIDPart(new JVMPIDMonitorItemTO(jsonObject));
				} else if (JVMThreadsMonitorItemTO.class.getName().equals(type)) {
					createJVMThreadsPart(new JVMThreadsMonitorItemTO(jsonObject));
				} else if (JVMUptimeMonitorItemTO.class.getName().equals(type)) {
					createJVMUptimePart(new JVMUptimeMonitorItemTO(jsonObject));
				} else if (DiskStatusPartItemTO.class.getName().equals(type)) {
					createDisksPart(new DiskStatusPartItemTO(jsonObject));
				} else if (SMARTPartItemTO.class.getName().equals(type)) {
					createSMARTPart(new SMARTPartItemTO(jsonObject));
				} else if (ServicesPartItemTO.class.getName().equals(type)) {
					createServicesPart(new ServicesPartItemTO(jsonObject));
				}
			}
		};
		jsDiv.setId(jsDivId);
		jsDiv.getStyle().set("display", "none");
		layout.add(jsDiv);

		String url = UIUtils.getContextPath() + "/ws/system-monitor";

		Map<String, Integer> partsAndIntervals = new HashMap<>();
		partsAndIntervals.put("services", 10000);
		partsAndIntervals.put("servers", 10000);
		partsAndIntervals.put("system-uptime", 5000);
		partsAndIntervals.put("system-memory-status", 2000);
		partsAndIntervals.put("system-swap-status", 2000);
		partsAndIntervals.put("jvm-uptime", 5000);
		partsAndIntervals.put("jvm-pid", 10000);
		partsAndIntervals.put("jvm-threads", 5000);
		partsAndIntervals.put("jvm-memory", 2000);
		partsAndIntervals.put("backup-status", 10000);
		partsAndIntervals.put("disk-status", 10000);
		partsAndIntervals.put("smart-info", 10000);

		UI.getCurrent().getPage().executeJs("window." + TIMEOUTS_JS_ARRAY + " = [];");

		UI.getCurrent().getPage().executeJs("const delay = ms => new Promise(res => setTimeout(res, ms));");
		for (Entry<String, Integer> entry : partsAndIntervals.entrySet()) {
			String partUrl = url + "/" + entry.getKey();
			String js = """ 
					(() => {
						let func = () => {
							$.ajax({ 
								url: "##partUrl##", 
								type: "GET",
								data: "",
								beforeSend: xhr => {
									xhr.setRequestHeader("Accept", "application/json");
									xhr.setRequestHeader("Content-Type", "application/json");
								},
								success: data => {
									let jsDiv = document.getElementById("##jsDivId##");
									if (jsDiv) 
										jsDiv.$server.monitorRefresh(data);
									setTimeout(func, ##timeout##);
								}
							});							
						};
						func();
					})();""";
			js = js.replaceAll("##partUrl##", partUrl);
			js = js.replaceAll("##jsDivId##", jsDivId);
			js = js.replaceAll("##timeout##", entry.getValue().toString());
			UI.getCurrent().getPage().executeJs("window." + TIMEOUTS_JS_ARRAY + ".push(setTimeout(function(){" + js +
					"}," + 1000 + "));");
		}

		// Mail test
		layout.add(new H2("Email test"));
		Button testMailBtn = new Button("Send test email",
				e -> mailService.sendToAdmin("Grass email test", "Test message"));
		layout.add(testMailBtn);
		Button monitorMailBtn = new Button("Send monitor email", e -> emailNotifier.getTimerTask().run());
		layout.add(monitorMailBtn);
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		UI.getCurrent().getPage()
				.executeJs("window." + TIMEOUTS_JS_ARRAY + ".forEach(function(i) { clearTimeout(i) })");
		super.onDetach(detachEvent);
	}
}