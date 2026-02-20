package cz.gattserver.grass.articles.events.impl;

import cz.gattserver.grass.core.events.StartEvent;

public record ArticlesProcessStartEvent(int steps) implements StartEvent {
}