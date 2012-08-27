package com.velik.recommend.map;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StressMap implements Serializable {
	private static final long serialVersionUID = 0L;

	private static final Logger LOGGER = Logger.getLogger(StressMap.class.getName());

	static int[] squares = new int[1025];

	static {
		for (int i = 0; i < squares.length; i++) {
			squares[i] = i * i;
		}
	}

	public class Move {
		MapPosition from;
		MapPosition to;
		int radius;

		public Move(MapPosition from, MapPosition to, int radius) {
			this.from = from;
			this.to = to;
			this.radius = radius;
		}

		void move() {
			assert from.distance(to) > radius * 2;

			for (int x = -radius; x <= radius; x++)
				for (int y = -radius; y <= radius; y++) {
					int fromx = normalizeXCoordinate(from.x + x);
					int fromy = normalizeYCoordinate(from.y + y);
					int tox = normalizeXCoordinate(to.x + x);
					int toy = normalizeYCoordinate(to.y + y);

					int swap = map[toy][tox];

					map[toy][tox] = map[fromy][fromx];
					map[fromy][fromx] = swap;
				}
		}

		int calculateStressChange() {
			PositionSet positionSet = new PositionSet() {
				int minInterval = radius - forceReach;
				int maxInterval = radius + forceReach + 1;

				@Override
				public boolean contains(MapPosition pos) {
					int distanceFrom = pos.distance(from);

					if (distanceFrom >= minInterval && distanceFrom <= maxInterval) {
						return true;
					}

					int distanceTo = pos.distance(to);

					if (distanceTo >= minInterval && distanceTo <= maxInterval) {
						return true;
					}

					return false;
				}
			};

			MapArea boundary = from.area(to);

			boundary.grow(radius + forceReach);

			long before = StressMap.this.calculateStress(boundary, positionSet);

			move();

			long after = StressMap.this.calculateStress(boundary, positionSet);

			reverseMove().move();

			return (int) (after - before);
		}

		private Move reverseMove() {
			return StressMap.this.move(to, from, radius);
		}

		public String toString() {
			return "Swap " + from + " and " + to + ", radius " + radius;
		}
	}

	static final int START_TEMPERATURE = 1024 * 1024;

	public MapArea area(int x1, int y1, int x2, int y2) {
		return new MapArea(x1, y1, x2, y2);
	}

	public class MapArea {
		private int x1;
		private int x2;
		private int y1;
		private int y2;

		public MapArea(MapPosition p1, MapPosition p2) {
			this(p1.x, p1.y, p2.x, p2.y);
		}

		public MapArea(int x1, int y1, int x2, int y2) {
			this.x1 = normalizeXCoordinate(x1);
			this.y1 = normalizeYCoordinate(y1);
			this.x2 = normalizeXCoordinate(x2);
			this.y2 = normalizeYCoordinate(y2);

			assert this.x1 >= 0 && this.x1 < width;
			assert this.x2 >= 0 && this.x2 < width;
			assert this.y1 >= 0 && this.y1 < height;
			assert this.y2 >= 0 && this.y2 < height;
		}

		public void grow(int by) {
			if (((x2 - x1) & xMod) + 1 + by * 2 > width) {
				x1 = 1;
				x2 = 0;
			} else {
				x1 = normalizeXCoordinate(x1 - by);
				x2 = normalizeXCoordinate(x2 + by);
			}

			if (((y2 - y1) & yMod) + 1 + by * 2 > height) {
				y1 = 1;
				y2 = 0;
			} else {
				y1 = normalizeYCoordinate(y1 - by);
				y2 = normalizeYCoordinate(y2 + by);
			}
		}

		public MapArea(MapArea area) {
			this.x1 = area.x1;
			this.x2 = area.x2;
			this.y1 = area.y1;
			this.y2 = area.y2;
		}

		public MapArea intersection(MapArea area) {
			area = new MapArea(area);
			MapArea me = new MapArea(this);

			if (area.x2 < area.x1) {
				area.x2 += width;
			}

			if (me.x2 < me.x1) {
				me.x2 += width;
			}

			if (area.y2 < area.y1) {
				area.y2 += height;
			}

			if (me.y2 < me.y1) {
				me.y2 += height;
			}

			if (area.x2 < me.x1) {
				me.x1 -= width;
				me.x2 -= width;
			}

			if (area.y2 < me.y1) {
				me.y1 -= height;
				me.y2 -= height;
			}

			if (me.x2 < area.x1) {
				area.x1 -= width;
				area.x2 -= width;
			}

			if (me.y2 < area.y1) {
				area.y1 -= height;
				area.y2 -= height;
			}

			assert me.x1 <= area.x2 || area.x1 <= me.x2;
			assert me.y1 <= area.y2 || area.y1 <= me.y2;

			return new MapArea(max(me.x1, area.x1), max(me.y1, area.y1), min(me.x2, area.x2), min(me.y2, area.y2));
		}

		public int area() {
			int width = x2 - x1 + 1;

			if (width <= 0) {
				width += StressMap.this.width;
			}

			int height = y2 - y1 + 1;

			if (height <= 0) {
				height += StressMap.this.height;
			}

			return width * height;

		}

		public boolean contains(MapPosition point) {
			if (x2 > x1) {
				if (point.x > x2 || point.x < x1) {
					return false;
				}
			} else {
				if (point.x > x1 || point.x < x2) {
					return false;
				}
			}

			if (y2 > y1) {
				if (point.y > y2 || point.y < y1) {
					return false;
				}
			} else {
				if (point.y > y1 || point.y < y2) {
					return false;
				}
			}

			return true;
		}

		public String toString() {
			return "((" + x1 + "," + y1 + "),(" + x2 + "," + y2 + "))";
		}
	}

	public MapPosition pos(int x, int y) {
		return new MapPosition(x, y);
	}

	public int normalizeXCoordinate(int x) {
		return x & xMod;
	}

	public int normalizeYCoordinate(int y) {
		return y & yMod;
	}

	public class MapPosition {
		public MapPosition(int x, int y) {
			this.x = normalizeXCoordinate(x);
			this.y = normalizeYCoordinate(y);
		}

		public MapArea area(MapPosition to) {
			int fromx = min(x, to.x);
			int tox = max(x, to.x);

			int thisWidth = tox - fromx + 1;

			if (width - thisWidth < thisWidth) {
				int t = fromx;
				fromx = tox;
				tox = t;
			}

			int fromy = min(y, to.y);
			int toy = max(y, to.y);

			int thisHeight = toy - fromy + 1;

			if (height - thisHeight < thisHeight) {
				int t = fromy;
				fromy = toy;
				toy = t;
			}

			return new MapArea(fromx, fromy, tox, toy);
		}

		int x;
		int y;

		public int distance(MapPosition point) {
			int xdist = abs(x - point.x);

			if (xdist > halfWidth) {
				xdist = width - xdist;
			}

			int ydist = abs(y - point.y);

			if (ydist > halfHeight) {
				ydist = height - ydist;
			}

			return max(xdist, ydist);
		}

		public String toString() {
			return "(" + x + "," + y + ")";
		}

		public MapArea square(int radius) {
			return StressMap.this.area(x - radius, y - radius, x + radius, y + radius);
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof MapPosition && ((MapPosition) obj).x == x && ((MapPosition) obj).y == y;
		}

		@Override
		public int hashCode() {
			return x * 4711 + y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

	}

	private Stresses stresses;

	int energyLevel;

	int[][] map;

	private int height;

	private int width;

	private int yMod;

	private int xMod;

	private int halfHeight;

	private int halfWidth;

	RandomNumberGenerator random = new DefaultRandomNumberGenerator();

	public StressMap(Stresses stresses, int width, int height) {
		this.stresses = stresses;

		this.height = height;
		this.width = width;

		assert Integer.bitCount(height) == 1;
		assert Integer.bitCount(width) == 1;

		assert height > 1;
		assert width > 1;

		halfHeight = height / 2;
		halfWidth = width / 2;

		yMod = height - 1;
		xMod = width - 1;

		if (height * width != stresses.size()) {
			throw new RuntimeException("Cells don't add up to size: " + height + ", " + width + ", size "
					+ stresses.size());
		}

		map = new int[height][width];

		int i = 0;

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				map[row][col] = i++;
			}
		}
	}

	private int forceReach = 2;

	void anneal(int generations) {
		int temperature = START_TEMPERATURE;

		double step = (double) START_TEMPERATURE / generations;

		long currentStress = calculateStress();

		int generation = 0;

		long[] stressChangeByRadius = new long[min(width, height) / 2];
		int[] radiusCount = new int[stressChangeByRadius.length];

		while (generation < generations) {
			Move move = randomMove(temperature);

			int stressChange = move.calculateStressChange();

			stressChangeByRadius[move.radius] += abs(stressChange);
			radiusCount[move.radius]++;

			if (stressChange == 0) {
				continue;
			}

			if (stressChange < 0) {
				move.move();
				currentStress += stressChange;

				assert currentStress == calculateStress();
			} else {
				double changeRelativeToAverage = 10.0 * stressChange * radiusCount[move.radius]
						/ stressChangeByRadius[move.radius];

				double s = changeRelativeToAverage * changeRelativeToAverage;

				double t = (double) temperature / START_TEMPERATURE;

				double p = Math.exp(-s / t / t);

				assert p >= 0;
				assert p <= 1;

				if (random.nextInt(100) < (int) (100 * p)) {
					move.move();
					currentStress += stressChange;
					/*
					 * System.out.println(generation + ": Picking bad move: " +
					 * move + " (with probability " + ((int) (100 * p)) +
					 * "% because of change " + changeRelativeToAverage + "%): "
					 * + currentStress);
					 */
					assert currentStress == calculateStress();
				}
			}

			temperature = (int) (START_TEMPERATURE - (step * generation));
			generation++;

			if (generation % 10000 == 0) {
				LOGGER.log(Level.INFO, "Annealing " + (100 * generation / generations) + "%. Current stress: "
						+ currentStress + ".");
			}
		}

		LOGGER.log(Level.INFO, "Done annealing. Stress: " + currentStress);
	}

	public long calculateStress(MapPosition position) {
		return calculateStress(new MapArea(position, position), new PositionSet() {
			@Override
			public boolean contains(MapPosition pos) {
				return true;
			}
		});
	}

	long calculateStress() {
		return calculateStress(new MapArea(0, 0, width - 1, height - 1), new PositionSet() {
			@Override
			public boolean contains(MapPosition pos) {
				return true;
			}
		});
	}

	long calculateStress(MapArea area, PositionSet set) {
		long result = 0;

		int x = area.x1;

		do {
			int y = area.y1;

			do {
				MapPosition pos1 = new MapPosition(x, y);

				if (set.contains(pos1)) {
					for (int dx = -forceReach; dx <= 0; dx++) {
						for (int dy = -forceReach; dy <= (dx == 0 ? -1 : forceReach); dy++) {
							MapPosition pos2 = new MapPosition(x + dx, y + dy);

							if (set.contains(pos2)) {
								int distance = pos1.distance(pos2);

								assert distance <= forceReach && distance > 0;

								int force = (distance == 1 ? 2 : 1);

								result += force * stress(pos1, pos2);
							}
						}
					}
				}

				if (y == area.y2) {
					break;
				}

				y = normalizeYCoordinate(y + 1);
			} while (true);

			if (x == area.x2) {
				break;
			}

			x = normalizeXCoordinate(x + 1);
		} while (true);

		return result;
	}

	Move randomMove(int t) {
		int radius = maxPossibleRadiusForMove() * random.nextInt(t + 1) / START_TEMPERATURE;

		MapPosition from = randomPosition();

		int tox;
		int toy;

		int verticalFreedom = height - 2 * (2 * radius + 1) + 1;
		int horizontalFreedom = width - 2 * (2 * radius + 1) + 1;

		if (random.nextInt(max(verticalFreedom, 0) + max(horizontalFreedom, 0)) >= verticalFreedom) {
			tox = from.x + radius * 2 + 1 + random.nextInt(horizontalFreedom);
			toy = random.nextInt(width);
		} else {
			toy = from.y + radius * 2 + 1 + random.nextInt(verticalFreedom);
			tox = random.nextInt(height);
		}

		MapPosition to = pos(tox, toy);

		assert from.distance(to) > radius * 2;

		return new Move(from, to, radius);
	}

	private int sqrt(int value) {
		assert value <= squares[squares.length - 1];
		assert value >= 0;

		int i = Arrays.binarySearch(squares, value);

		if (i < 0) {
			i = -i - 1;
		}

		return i;
	}

	private int maxPossibleRadiusForMove() {
		return ((Math.max(height, width) / 2) - 1) / 2;
	}

	private MapPosition randomPosition() {
		return new MapPosition(random.nextInt(width), random.nextInt(height));
	}

	protected int stress(MapPosition point1, MapPosition point2) {
		int index1 = map[point1.y][point1.x];
		int index2 = map[point2.y][point2.x];

		int result = stresses.get(index1, index2);

		return result;
	}

	public void setForceReach(int forceReach) {
		this.forceReach = forceReach;
	}

	public Move move(MapPosition from, MapPosition to, int radius) {
		return new Move(from, to, radius);
	}

	public void print() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				System.out.print(pad(map[i][j]) + " ");
			}

			System.out.println();
		}
	}

	private String pad(int i) {
		String result = Integer.toString(i);

		while (result.length() < 3) {
			result = " " + result;
		}

		return result;
	}

	public Stresses getStresses() {
		return stresses;
	}

	public int getIndex(MapPosition position) {
		return map[position.y][position.x];
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}
