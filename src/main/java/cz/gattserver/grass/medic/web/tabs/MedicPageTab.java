package cz.gattserver.grass.medic.web.tabs;

import java.io.Serializable;
import java.util.Collection;

import cz.gattserver.common.Identifiable;
import cz.gattserver.grass.core.ui.components.GridOperationsTab;
import cz.gattserver.grass.medic.service.MedicService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class MedicPageTab<T extends Identifiable, F, C extends Collection<T> & Serializable>
		extends GridOperationsTab<T, F, C> {

	private static final long serialVersionUID = 2057957439013190170L;

	@Autowired
	protected MedicService medicService;

	public MedicPageTab(Class<T> clazz) {
		super(clazz);
	}
}
