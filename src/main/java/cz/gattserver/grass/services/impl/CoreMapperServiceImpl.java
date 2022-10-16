package cz.gattserver.grass.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import cz.gattserver.grass.interfaces.ContentNodeTO;
import cz.gattserver.grass.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.interfaces.NodeOverviewTO;
import cz.gattserver.grass.interfaces.NodeTO;
import cz.gattserver.grass.interfaces.QuoteTO;
import cz.gattserver.grass.interfaces.UserInfoTO;
import cz.gattserver.grass.model.domain.ContentNode;
import cz.gattserver.grass.model.domain.ContentTag;
import cz.gattserver.grass.model.domain.Node;
import cz.gattserver.grass.model.domain.Quote;
import cz.gattserver.grass.model.domain.User;
import cz.gattserver.grass.modules.register.ModuleRegister;
import cz.gattserver.grass.security.Role;
import cz.gattserver.grass.services.CoreMapperService;

@Service
public class CoreMapperServiceImpl implements CoreMapperService {

	@Lazy
	@Autowired
	protected ModuleRegister moduleRegister;

	@Override
	public UserInfoTO map(User e) {
		if (e == null)
			return null;

		UserInfoTO userInfoDTO = new UserInfoTO();

		userInfoDTO.setConfirmed(e.isConfirmed());
		userInfoDTO.setEmail(e.getEmail());
		userInfoDTO.setId(e.getId());
		userInfoDTO.setLastLoginDate(e.getLastLoginDate());
		userInfoDTO.setName(e.getName());
		userInfoDTO.setPassword(e.getPassword());
		userInfoDTO.setRegistrationDate(e.getRegistrationDate());

		Set<Role> set = new HashSet<>();
		for (String s : e.getRoles()) {
			Role role = moduleRegister.resolveRole(s);
			if (role != null)
				set.add(role);
		}
		userInfoDTO.setRoles(set);

		return userInfoDTO;
	}

	@Override
	public QuoteTO map(Quote e) {
		if (e == null)
			return null;

		QuoteTO quoteDTO = new QuoteTO();

		quoteDTO.setId(e.getId());
		quoteDTO.setName(e.getName());

		return quoteDTO;
	}

	@Override
	public ContentNodeTO mapContentNodeForDetail(ContentNode e) {
		if (e == null)
			return null;

		ContentNodeTO contentNodeDTO = new ContentNodeTO();

		contentNodeDTO.setAuthor(map(e.getAuthor()));
		contentNodeDTO.setContentID(e.getContentId());
		contentNodeDTO.setContentReaderID(e.getContentReaderId());
		contentNodeDTO.setCreationDate(e.getCreationDate());
		contentNodeDTO.setId(e.getId());
		contentNodeDTO.setLastModificationDate(e.getLastModificationDate());
		contentNodeDTO.setName(e.getName());
		contentNodeDTO.setPublicated(e.getPublicated());
		contentNodeDTO.setDraft(e.getDraft());
		contentNodeDTO.setDraftSourceId(e.getDraftSourceId());
		contentNodeDTO.setContentTags(mapContentTagCollectionForOverview(e.getContentTags()));
		contentNodeDTO.setParent(mapNodeForOverview(e.getParent()));

		return contentNodeDTO;
	}

	@Override
	public ContentTagOverviewTO mapContentTagForOverview(ContentTag e) {
		if (e == null)
			return null;

		ContentTagOverviewTO contentTagDTO = new ContentTagOverviewTO();

		contentTagDTO.setId(e.getId());
		contentTagDTO.setName(e.getName());

		return contentTagDTO;
	}

	@Override
	public List<ContentTagOverviewTO> mapContentTagCollection(Collection<ContentTag> contentTags) {
		if (contentTags == null)
			return new ArrayList<>();

		List<ContentTagOverviewTO> contentTagDTOs = new ArrayList<>();
		for (ContentTag contentTag : contentTags) {
			contentTagDTOs.add(mapContentTagForOverview(contentTag));
		}
		return contentTagDTOs;
	}

	@Override
	public Set<ContentTagOverviewTO> mapContentTagCollectionForOverview(Collection<ContentTag> contentTags) {
		if (contentTags == null)
			return new HashSet<>();

		Set<ContentTagOverviewTO> contentTagDTOs = new LinkedHashSet<>();
		for (ContentTag contentTag : contentTags) {
			contentTagDTOs.add(mapContentTagForOverview(contentTag));
		}
		return contentTagDTOs;
	}

	@Override
	public NodeTO mapNodeForDetail(Node e) {
		if (e == null)
			return null;

		NodeTO nodeDTO = new NodeTO();

		nodeDTO.setId(e.getId());
		nodeDTO.setName(e.getName());
		if (e.getParent() != null) {
			nodeDTO.setParentId(e.getParent().getId());
			nodeDTO.setParentName(e.getParent().getName());
			nodeDTO.setParent(mapNodeForDetail(e.getParent()));
		}

		return nodeDTO;
	}

	@Override
	public NodeOverviewTO mapNodeForOverview(Node e) {
		if (e == null)
			return null;

		NodeOverviewTO nodeDTO = new NodeOverviewTO();

		nodeDTO.setId(e.getId());
		nodeDTO.setName(e.getName());
		if (e.getParent() != null) {
			nodeDTO.setParentId(e.getParent().getId());
			nodeDTO.setParentName(e.getParent().getName());
		}

		return nodeDTO;
	}

	@Override
	public List<NodeOverviewTO> mapNodesForOverview(Collection<Node> nodes) {
		if (nodes == null)
			return new ArrayList<>();

		List<NodeOverviewTO> nodeDTOs = new ArrayList<>();
		for (Node node : nodes) {
			nodeDTOs.add(mapNodeForOverview(node));
		}
		return nodeDTOs;
	}

}