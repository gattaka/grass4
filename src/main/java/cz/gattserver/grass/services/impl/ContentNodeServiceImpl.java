package cz.gattserver.grass.services.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryResults;

import cz.gattserver.grass.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass.interfaces.ContentNodeTO;
import cz.gattserver.grass.interfaces.UserInfoTO;
import cz.gattserver.grass.model.domain.ContentNode;
import cz.gattserver.grass.model.domain.Node;
import cz.gattserver.grass.model.domain.User;
import cz.gattserver.grass.model.repositories.ContentNodeRepository;
import cz.gattserver.grass.services.ContentNodeService;
import cz.gattserver.grass.services.ContentTagService;
import cz.gattserver.grass.services.CoreMapperService;
import cz.gattserver.grass.services.SecurityService;
import cz.gattserver.grass.services.UserService;

@Transactional
@Service
public class ContentNodeServiceImpl implements ContentNodeService {

	@Autowired
	private CoreMapperService mapper;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private ContentTagService contentTagService;

	@Autowired
	private UserService userService;

	@Autowired
	private ContentNodeRepository contentNodeRepository;

	@Override
	public long save(String contentModuleId, long contentId, String name, Collection<String> tags, boolean publicated,
			long nodeId, long authorId, boolean draft, LocalDateTime date, Long draftSourceId) {
		Validate.notNull(contentModuleId, "'contentModuleId' nesmí být null");
		Validate.notNull(name, "'name' nesmí být null");

		if (date == null)
			date = LocalDateTime.now();

		ContentNode contentNode = new ContentNode();
		contentNode.setContentId(contentId);
		contentNode.setContentReaderId(contentModuleId);
		contentNode.setCreationDate(date);
		contentNode.setName(name);
		contentNode.setDraft(draft);
		contentNode.setDraftSourceId(draftSourceId);
		contentNode.setPublicated(publicated);

		// Ulož contentNode
		Node parent = new Node();
		parent.setId(nodeId);
		contentNode.setParent(parent);

		User user = new User();
		user.setId(authorId);
		contentNode.setAuthor(user);

		contentNode = contentNodeRepository.save(contentNode);

		// aktualizace tagů
		contentTagService.saveTags(tags, contentNode);

		return contentNode.getId();
	}

	@Override
	public ContentNodeTO getByID(long contentNodeId) {
		ContentNode contentNode = contentNodeRepository.findById(contentNodeId).orElse(null);
		return mapper.mapContentNodeForDetail(contentNode);
	}

	@Override
	public void modify(long contentNodeId, String name, boolean publicated) {
		modify(contentNodeId, name, null, publicated);
	}

	@Override
	public void modify(long contentNodeId, String name, Collection<String> tags, boolean publicated) {
		modify(contentNodeId, name, tags, publicated, null);
	}

	@Override
	public void modify(long contentNodeId, String name, Collection<String> tags, boolean publicated,
			LocalDateTime creationDate) {
		Validate.notNull(name, "'name' nesmí být null");
		ContentNode contentNode = contentNodeRepository.findById(contentNodeId).orElse(null);

		contentNode.setLastModificationDate(LocalDateTime.now());
		contentNode.setName(name);
		contentNode.setPublicated(publicated);

		if (creationDate != null)
			contentNode.setCreationDate(creationDate);

		// Ulož změny v contentNode
		contentNodeRepository.save(contentNode);
		// aktualizace tagů
		contentTagService.saveTags(tags, contentNodeId);
	}

	@Override
	public void deleteByContentNodeId(long contentNodeId) {
		userService.removeContentFromAllUsersFavourites(contentNodeId);

		// vymaž tagy
		contentTagService.saveTags(null, contentNodeId);

		// vymaž content node
		ContentNode contentNode = contentNodeRepository.findById(contentNodeId).orElse(null);
		contentNodeRepository.delete(contentNode);
	}

	@Override
	public void deleteByContentId(String contentModuleId, long contentId) {
		Validate.notNull(contentModuleId, "'contentModuleId' nemůže být null");
		Long contentNodeId = contentNodeRepository.findIdByContentModuleAndContentId(contentModuleId, contentId);
		if (contentNodeId != null)
			deleteByContentNodeId(contentNodeId);
		else
			throw new IllegalStateException("Dle ID koncového obsahu nebyl nalezen obecný uzel obsahu");
	}

	@Override
	public void moveContent(long nodeId, long contentNodeId) {
		contentNodeRepository.moveContent(nodeId, contentNodeId);
	}

	/**
	 * Nedávné obsahy
	 */

	private QueryResults<ContentNodeOverviewTO> innerByUserAccess(int offset, int limit, String sortProperty) {
		UserInfoTO user = securityService.getCurrentUser();
		return contentNodeRepository.findByFilterAndUserAccess(new ContentNodeFilterTO(), user.getId(), user.isAdmin(),
				offset, limit, sortProperty);
	}

	@Override
	public int getCount() {
		int count = (int) innerByUserAccess(1, 1, "creationDate").getTotal();
		return count;
	}

	@Override
	public List<ContentNodeOverviewTO> getRecentAdded(int offset, int limit) {
		return innerByUserAccess(offset, limit, "creationDate").getResults();
	}

	@Override
	public List<ContentNodeOverviewTO> getRecentModified(int offset, int limit) {
		return innerByUserAccess(offset, limit, "lastModificationDate").getResults();
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

	private QueryResults<ContentNodeOverviewTO> innerByFilterAndUserAccess(ContentNodeFilterTO filter, int offset,
			int limit) {
		UserInfoTO user = securityService.getCurrentUser();
		return contentNodeRepository.findByFilterAndUserAccess(filter, user.getId(), user.isAdmin(), offset, limit,
				null);
	}

	@Override
	public int getCountByFilter(ContentNodeFilterTO filter) {
		return (int) innerByFilterAndUserAccess(filter, 1, 1).getTotal();
	}

	@Override
	public List<ContentNodeOverviewTO> getByFilter(ContentNodeFilterTO filter, int offset, int limit) {
		return innerByFilterAndUserAccess(filter, offset, limit).getResults();
	}

}
