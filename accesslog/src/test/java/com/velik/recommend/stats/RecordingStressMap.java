package com.velik.recommend.stats;

import java.util.HashMap;
import java.util.Map;

public class RecordingStressMap extends StressMap {
	private Map<PositionPair, Integer> stressByPositionPair = new HashMap<PositionPair, Integer>();
	private boolean comparing;

	public class PositionPair {
		private MapPosition pos1;
		private MapPosition pos2;

		public PositionPair(MapPosition pos1, MapPosition pos2) {
			this.pos1 = pos1;
			this.pos2 = pos2;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof PositionPair
					&& ((((PositionPair) obj).pos1.equals(pos2) && ((PositionPair) obj).pos2.equals(pos1)) ||

					(((PositionPair) obj).pos1.equals(pos1) && ((PositionPair) obj).pos2.equals(pos2)));
		}

		@Override
		public int hashCode() {
			return pos1.hashCode() + pos2.hashCode();
		}

		public String toString() {
			return pos1 + ", " + pos2;
		}
	}

	RecordingStressMap(Stresses stresses, int width, int height) {
		super(stresses, width, height);
	}

	protected int stress(MapPosition point1, MapPosition point2) {
		int result = super.stress(point1, point2);

		PositionPair pair = new PositionPair(point1, point2);

		if (comparing) {
			Integer oldResult = stressByPositionPair.get(pair);

			if (oldResult == null) {
				System.out.println("new: " + pair + " -> " + result);
			} else if (oldResult != result) {
				System.out.println("different: " + pair + " -> + " + result + " rather than " + oldResult);
			}
		} else {
			stressByPositionPair.put(pair, result);
		}

		return result;
	}

	public void compare() {
		comparing = true;
	}

	public void clear() {
		stressByPositionPair.clear();
		comparing = false;
	}

	public long calculateStress(MapArea area, PositionSet set) {
		try {
			return super.calculateStress(area, set);
		} finally {
			compare();
		}
	}

}
