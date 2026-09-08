package cz.gattserver.grass.core.services.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.model.domain.Node;
import cz.gattserver.grass.core.model.repositories.NodeRepository;
import cz.gattserver.grass.core.services.*;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryResults;

import cz.gattserver.grass.core.model.domain.ContentNode;
import cz.gattserver.grass.core.model.repositories.ContentNodeRepository;

@Transactional
@Service
public class ContentNodeServiceImpl implements ContentNodeService {

    private final CoreMapperService mapper;
    private final SecurityService securityService;
    private final ContentTagService contentTagService;
    private final UserService userService;
    private final ContentNodeRepository contentNodeRepository;
    private final NodeRepository nodeRepository;

    public ContentNodeServiceImpl(CoreMapperService mapper, SecurityService securityService,
                                  ContentTagService contentTagService, UserService userService,
                                  ContentNodeRepository contentNodeRepository, NodeRepository nodeRepository) {
        this.mapper = mapper;
        this.securityService = securityService;
        this.contentTagService = contentTagService;
        this.userService = userService;
        this.contentNodeRepository = contentNodeRepository;
        this.nodeRepository = nodeRepository;
    }

    @Override
    public long save(String contentModuleId, long contentId, String name, Collection<String> tags, boolean hidden,
                     long nodeId, long authorId, boolean draft, LocalDateTime date, Long draftSourceId) {
        Validate.notNull(contentModuleId, "'contentModuleId' nesmí být null");
        Validate.notNull(name, "'name' nesmí být null");

        if (date == null) date = LocalDateTime.now();

        ContentNode contentNode = new ContentNode();
        contentNode.setContentId(contentId);
        contentNode.setContentReaderId(contentModuleId);
        contentNode.setCreationDate(date);
        contentNode.setName(name);
        contentNode.setDraft(draft);
        contentNode.setDraftSourceId(draftSourceId);
        contentNode.setHidden(hidden);
        contentNode.setParentId(nodeId);
        contentNode.setAuthorId(authorId);

        nodeRepository.findById(nodeId)
                .ifPresent(node -> contentNode.setHiddenByParent(node.getHiddenByParent() || node.getHidden()));

        contentNode.setId(contentNodeRepository.save(contentNode).getId());

        // aktualizace tagů
        contentTagService.saveTags(tags, contentNode.getId());

        return contentNode.getId();
    }

    @Override
    public ContentNodeTO getById(long contentNodeId) {
        return contentNodeRepository.findByIdForDetail(contentNodeId);
    }

    @Override
    public void modify(long contentNodeId, String name, boolean hidden) {
        modify(contentNodeId, name, null, hidden);
    }

    @Override
    public void modify(long contentNodeId, String name, Collection<String> tags, boolean hidden) {
        modify(contentNodeId, name, tags, hidden, null);
    }

    @Override
    public void modify(long contentNodeId, @NotNull String name, Collection<String> tags, boolean hidden,
                       LocalDateTime creationDate) {
        Objects.requireNonNull(name);

        ContentNode contentNode = contentNodeRepository.findById(contentNodeId).orElseThrow();

        contentNode.setLastModificationDate(LocalDateTime.now());
        contentNode.setName(name);
        contentNode.setHidden(hidden);

        if (creationDate != null) contentNode.setCreationDate(creationDate);

        // Ulož změny v contentNode
        contentNodeRepository.save(contentNode);
        // aktualizace tagů
        contentTagService.saveTags(tags, contentNodeId);
    }

    @Override
    public void deleteByContentNodeId(long contentNodeId) {
        userService.removeContentFromAllUsersFavourites(contentNodeId);

        // vymaž tagy
        contentTagService.onContentNodeDelete(contentNodeId);

        // vymaž content node
        contentNodeRepository.deleteById(contentNodeId);
    }

    @Override
    public void deleteByContentId(String contentModuleId, long contentId) {
        Validate.notNull(contentModuleId, "'contentModuleId' nemůže být null");
        Long contentNodeId = contentNodeRepository.findIdByContentModuleAndContentId(contentModuleId, contentId);
        if (contentNodeId != null) deleteByContentNodeId(contentNodeId);
        else throw new IllegalStateException("Dle ID koncového obsahu nebyl nalezen obecný uzel obsahu");
    }

    @Override
    public void moveContent(long nodeId, long contentNodeId) {
        Node node = nodeRepository.findById(nodeId).orElse(null);
        contentNodeRepository.moveContent(contentNodeId, nodeId,
                node != null && (node.getHidden() || node.getHiddenByParent()));
    }

    /**
     * Nedávné obsahy
     */

    @Override
    public int getCount() {
        UserInfoTO user = securityService.getCurrentUser();
        return (int) contentNodeRepository.countByFilterAndUserAccess(new ContentNodeFilterTO(), user.getId(),
                user.isAdmin());
    }

    @Override
    public List<ContentNodeOverviewTO> getRecentAdded(int offset, int limit) {
        UserInfoTO user = securityService.getCurrentUser();
        return contentNodeRepository.findByFilterAndUserAccess(new ContentNodeFilterTO(), user.getId(), user.isAdmin(),
                offset, limit, "creationDate");
    }

    @Override
    public List<ContentNodeOverviewTO> getRecentModified(int offset, int limit) {
        UserInfoTO user = securityService.getCurrentUser();
        return contentNodeRepository.findByFilterAndUserAccess(new ContentNodeFilterTO(), user.getId(), user.isAdmin(),
                offset, limit, "lastModificationDate");
    }

    /**
     * Dle tagu
     */

    private QueryResults<ContentNodeOverviewTO> innerByTagAndUserAccess(long tagId, int offset, int limit) {
        UserInfoTO user = securityService.getCurrentUser();
        return contentNodeRepository.findByTagAndUserAccess(tagId, user.getId(), user.isAdmin(), offset, limit);
    }

    @Override
    public int getCountByTag(long tagId) {
        return (int) innerByTagAndUserAccess(tagId, 1, 1).getTotal();
    }

    @Override
    public List<ContentNodeOverviewTO> getByTag(long tagId, int offset, int limit) {
        return innerByTagAndUserAccess(tagId, offset, limit).getResults();
    }

    /**
     * Dle oblíbených uživatele
     */

    private QueryResults<ContentNodeOverviewTO> innerByUserFavouritesAndUserAccess(long userId, int offset, int limit) {
        UserInfoTO user = securityService.getCurrentUser();
        return contentNodeRepository.findByUserFavouritesAndUserAccess(userId, user.getId(), user.isAdmin(), offset,
                limit);
    }

    @Override
    public int getUserFavouriteCount(long userId) {
        return (int) innerByUserFavouritesAndUserAccess(userId, 1, 1).getTotal();
    }

    @Override
    public List<ContentNodeOverviewTO> getUserFavourite(long userId, int offset, int limit) {
        return innerByUserFavouritesAndUserAccess(userId, offset, limit).getResults();
    }

    /**
     * Dle filtru
     */

    @Override
    public int getCountByFilter(ContentNodeFilterTO filter) {
        UserInfoTO user = securityService.getCurrentUser();
        return (int) contentNodeRepository.countByFilterAndUserAccess(filter, user.getId(), user.isAdmin());
    }

    @Override
    public List<ContentNodeOverviewTO> getByFilter(ContentNodeFilterTO filter, int offset, int limit) {
        UserInfoTO user = securityService.getCurrentUser();
        return contentNodeRepository.findByFilterAndUserAccess(filter, user.getId(), user.isAdmin(), offset, limit,
                null);
    }

    @Override
    public List<String> getTagsByContentId(Long id) {
        return contentNodeRepository.findTagsByContentId(id);
    }
}