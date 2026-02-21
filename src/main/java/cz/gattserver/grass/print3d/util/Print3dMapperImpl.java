package cz.gattserver.grass.print3d.util;

import cz.gattserver.grass.core.services.CoreMapperService;
import cz.gattserver.grass.print3d.interfaces.Print3dTO;
import cz.gattserver.grass.print3d.model.Print3d;
import org.springframework.stereotype.Component;

@Component
public class Print3dMapperImpl implements Print3dMapper {

	/**
	 * Core mapper
	 */
	private final CoreMapperService mapper;

    public Print3dMapperImpl(CoreMapperService mapper) {
        this.mapper = mapper;
    }

    /**
	 * Převede {@link Print3d} na {@link Print3dTO}
	 */
	public Print3dTO mapProjectForDetail(Print3d project) {
		if (project == null)
			return null;

		Print3dTO print3dTO = new Print3dTO();
		print3dTO.setId(project.getId());
		print3dTO.setProjectDir(project.getProjectDir());
		print3dTO.setContentNode(mapper.mapContentNodeForDetail(project.getContentNode()));
		return print3dTO;
	}
}