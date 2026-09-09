package cz.gattserver.grass.print3d.model;

import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleTO;
import cz.gattserver.grass.print3d.interfaces.Print3dTO;
import cz.gattserver.grass.print3d.interfaces.Print3dViewItemTO;

import java.util.List;

public interface Print3dRepositoryCustom {

    Print3dTO findByForDetailId(Long id, Long userId, boolean isAdmin);
}