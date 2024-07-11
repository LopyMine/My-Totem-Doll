package net.lopymine.mtd.doll.tag;

public class DollTag {

	private final char tag;

	public DollTag(char tag) {
		this.tag = tag;
	}

	public boolean is(char anotherTag) {
		return anotherTag == this.tag;
	}
}
