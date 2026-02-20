package cz.gattserver.grass.articles.events.impl;

import cz.gattserver.grass.core.events.ProgressEvent;

public record ArticlesProcessProgressEvent(String description) implements ProgressEvent {
}