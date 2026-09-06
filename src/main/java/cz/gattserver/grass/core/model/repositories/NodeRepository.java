package cz.gattserver.grass.core.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.core.model.domain.Node;

public interface NodeRepository extends JpaRepository<Node, Long>, NodeRepositoryCustom {

    @Modifying
    @Query("update NODE n set n.name = ?2 where n.id = ?1")
    void rename(Long nodeId, String newName);

    @Modifying
    @Query("update NODE n set n.hidden = ?2 where n.id = ?1")
    void updateHidden(Long id, boolean b);

    @Modifying
    @Query("update NODE n set n.hiddenByParent = ?2 where n.id = ?1")
    void updateHiddenByParent(Long id, boolean b);
}