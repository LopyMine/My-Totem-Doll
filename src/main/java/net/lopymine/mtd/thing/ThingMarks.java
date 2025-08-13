package net.lopymine.mtd.thing;

import lombok.*;

@Setter
@Getter
public class ThingMarks {

	public static final ThreadLocal<ThingMarks> WORLD_RENDERING = ThreadLocal.withInitial(ThingMarks::new);

	private boolean marked;

}
