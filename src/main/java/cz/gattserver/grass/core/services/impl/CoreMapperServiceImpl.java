package cz.gattserver.grass.core.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass.core.services.CoreMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import cz.gattserver.grass.core.interfaces.ContentTagTO;
import cz.gattserver.grass.core.interfaces.QuoteTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.model.domain.ContentTag;
import cz.gattserver.grass.core.model.domain.Quote;
import cz.gattserver.grass.core.model.domain.User;
import cz.gattserver.grass.core.modules.register.ModuleRegister;
import cz.gattserver.grass.core.security.Role;

@Service
public class CoreMapperServiceImpl implements CoreMapperService {

    @Lazy
    @Autowired
    protected ModuleRegister moduleRegister;

    @Override
    public UserInfoTO map(User e) {
        if (e == null) return null;

        UserInfoTO userInfoTO = new UserInfoTO();

        userInfoTO.setConfirmed(e.isConfirmed());
        userInfoTO.setEmail(e.getEmail());
        userInfoTO.setId(e.getId());
        userInfoTO.setLastLoginDate(e.getLastLoginDate());
        userInfoTO.setName(e.getName());
        userInfoTO.setPassword(e.getPassword());
        userInfoTO.setRegistrationDate(e.getRegistrationDate());

        Set<Role> set = new HashSet<>();
        for (String s : e.getRoles()) {
            Role role = moduleRegister.resolveRole(s);
            if (role != null) set.add(role);
        }
        userInfoTO.setRoles(set);

        return userInfoTO;
    }

    @Override
    public QuoteTO map(Quote e) {
        if (e == null) return null;

        QuoteTO quoteTO = new QuoteTO();

        quoteTO.setId(e.getId());
        quoteTO.setName(e.getName());

        return quoteTO;
    }

    @Override
    public ContentTag map(ContentTagTO contentTagTO) {
        ContentTag contentTag = new ContentTag();
        contentTag.setId(contentTagTO.getId());
        contentTag.setName(contentTagTO.getName());
        contentTag.setContentNodeCount(contentTagTO.getContentNodeCount());
        return contentTag;
    }

}