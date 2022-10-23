package cz.gattserver.grass.core.interfaces;

public class NodeTO extends NodeOverviewTO {

	private NodeTO parent;

	public NodeTO getParent() {
		return parent;
	}

	public void setParent(NodeTO parent) {
		this.parent = parent;
	}

}
