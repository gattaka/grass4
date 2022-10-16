package cz.gattserver.grass.interfaces;

public class NodeTO extends NodeOverviewTO {

	private NodeTO parent;

	public NodeTO getParent() {
		return parent;
	}

	public void setParent(NodeTO parent) {
		this.parent = parent;
	}

}
