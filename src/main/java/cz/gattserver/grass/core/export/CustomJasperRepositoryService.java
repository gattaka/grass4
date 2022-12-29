package cz.gattserver.grass.core.export;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.repo.DefaultRepositoryService;

public class CustomJasperRepositoryService extends DefaultRepositoryService {
 
    public CustomJasperRepositoryService(JasperReportsContext parent) {
        super(parent);
        setClassLoader(getClass().getClassLoader());
    }
}