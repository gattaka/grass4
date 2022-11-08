package cz.gattserver.grass.articles.plugins.register.impl;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.plugins.Plugin;
import cz.gattserver.grass.articles.plugins.PluginFamilyDescription;
import cz.gattserver.grass.articles.plugins.register.PluginRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Služba, která přes spring DI získává pluginy
 * 
 * @author gatt
 */
@Service
public class PluginRegisterServiceImpl implements PluginRegisterService {

	@Autowired(required = false)
	private List<Plugin> injectedPlugins;

	@Autowired(required = false)
	private List<PluginFamilyDescription> injectedPluginFamilyDescs;

	/**
	 * Pluginy dle skupin
	 */
	private Map<String, Map<String, EditorButtonResourcesTO>> editorCatalog;
	private Map<String, Plugin> plugins;
	private Map<String, String> familyDescs;

	@PostConstruct
	private void init() {
		// Ošetření null kolekcí
		if (injectedPlugins == null)
			injectedPlugins = new ArrayList<>();
		if (injectedPluginFamilyDescs == null)
			injectedPluginFamilyDescs = new ArrayList<>();

		editorCatalog = new HashMap<>();
		plugins = new HashMap<>();
		familyDescs = new HashMap<>();
		for (Plugin plugin : injectedPlugins) {
			registerPlugin(plugin);
			addButtonToGroup(plugin.getEditorButtonResources());
		}

		for (PluginFamilyDescription family : injectedPluginFamilyDescs)
			familyDescs.put(family.getFamily(), family.getDescription());
	}

	private Plugin registerPlugin(Plugin plugin) {
		return plugins.put(plugin.getTag(), plugin);
	}

	@Override
	public Set<String> getRegisteredTags() {
		return Collections.unmodifiableSet(plugins.keySet());
	}

	@Override
	public Set<String> getRegisteredFamilies() {
		return Collections.unmodifiableSet(editorCatalog.keySet());
	}

	@Override
	public String getFamilyDescription(String family) {
		return familyDescs.get(family);
	}

	@Override
	public Set<EditorButtonResourcesTO> getTagResourcesByFamily(String group) {
		Map<String, EditorButtonResourcesTO> resources = editorCatalog.get(group);
		if (resources == null)
			return new HashSet<>();
		else
			return new HashSet<>(resources.values());
	}

	private void addButtonToGroup(EditorButtonResourcesTO resources) {
		// existuje skupina ?
		if (editorCatalog.containsKey(resources.getTagFamily())) {
			editorCatalog.get(resources.getTagFamily()).put(resources.getTag(), resources);
		} else {
			// založ
			Map<String, EditorButtonResourcesTO> map = new HashMap<>();
			map.put(resources.getTag(), resources);
			editorCatalog.put(resources.getTagFamily(), map);
		}
	}

	@Override
	public Map<String, Plugin> createRegisterSnapshot() {
		return Collections.unmodifiableMap(plugins);
	}
}
