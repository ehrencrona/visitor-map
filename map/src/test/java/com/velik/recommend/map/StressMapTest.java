package com.velik.recommend.map;

import org.junit.Assert;
import org.junit.Test;

import com.velik.recommend.map.StressMap.MapArea;
import com.velik.recommend.map.StressMap.Move;

public class StressMapTest {

	private final class ConstantStresses implements Stresses {
		private int size;

		ConstantStresses(int size) {
			this.size = size;
		}

		@Override
		public int get(int i, int j) {
			return 1;
		}

		@Override
		public int size() {
			return size;
		}
	}

	@Test
	public void testIntersection() {
		StressMap map = new StressMap(new ConstantStresses(64), 8, 8);

		Assert.assertEquals("((2,2),(2,2))", map.area(1, 1, 2, 2).intersection(map.area(2, 2, 3, 3)).toString());

		Assert.assertEquals("((1,1),(2,2))", map.area(1, 1, 2, 2).intersection(map.area(1, 1, 3, 3)).toString());

		Assert.assertEquals("((1,1),(4,2))", map.area(0, 1, 4, 2).intersection(map.area(1, 0, 6, 2)).toString());

		Assert.assertEquals("((2,2),(2,2))", map.area(2, 2, 3, 3).intersection(map.area(1, 1, 2, 2)).toString());

		Assert.assertEquals("((1,1),(2,2))", map.area(1, 1, 3, 3).intersection(map.area(1, 1, 2, 2)).toString());

		Assert.assertEquals("((1,1),(4,2))", map.area(1, 0, 6, 2).intersection(map.area(0, 1, 4, 2)).toString());
	}

	@Test
	public void testGrow() {
		StressMap map = new StressMap(new ConstantStresses(64), 8, 8);

		MapArea area = map.area(1, 1, 6, 6);
		area.grow(1);
		Assert.assertEquals("((0,0),(7,7))", area.toString());

		area = map.area(3, 2, 4, 5);
		area.grow(1);
		Assert.assertEquals("((2,1),(5,6))", area.toString());

		area = map.area(3, 2, 1, 2);
		area.grow(1);
		Assert.assertEquals("((1,1),(0,3))", area.toString());
	}

	@Test
	public void testArea() {
		StressMap map = new StressMap(new ConstantStresses(64), 8, 8);

		Assert.assertEquals("((1,1),(2,2))", map.pos(1, 1).area(map.pos(2, 2)).toString());

		Assert.assertEquals("((7,7),(0,0))", map.pos(0, 0).area(map.pos(7, 7)).toString());

		Assert.assertEquals("((2,2),(5,5))", map.pos(2, 2).area(map.pos(5, 5)).toString());
		Assert.assertEquals("((5,5),(1,1))", map.pos(1, 1).area(map.pos(5, 5)).toString());

		Assert.assertEquals("((7,2),(0,3))", map.pos(0, 2).area(map.pos(7, 3)).toString());
		Assert.assertEquals("((2,7),(3,0))", map.pos(2, 0).area(map.pos(3, 7)).toString());
	}

	@Test
	public void testAreaOverlappingOnlyToroidally() {
		StressMap map = new StressMap(new ConstantStresses(64), 8, 8);

		Assert.assertEquals("((6,7),(0,0))", map.area(-2, -1, 2, 1).intersection(map.area(6, 7, 8, 8)).toString());
		Assert.assertEquals("((7,7),(0,1))", map.area(-1, 6, 1, 9).intersection(map.area(7, -1, 8, 2)).toString());
		Assert.assertEquals("((6,7),(0,0))", map.area(6, 7, 8, 8).intersection(map.area(-2, -1, 2, 1)).toString());
		Assert.assertEquals("((7,7),(0,1))", map.area(7, -1, 8, 2).intersection(map.area(-1, 6, 1, 9)).toString());
	}

	@Test
	public void testDistance() {
		StressMap map = new StressMap(new ConstantStresses(16), 4, 4);

		Assert.assertEquals(1, map.pos(1, 1).distance(map.pos(2, 1)));
		Assert.assertEquals(1, map.pos(1, 1).distance(map.pos(2, 2)));
		Assert.assertEquals(2, map.pos(1, 1).distance(map.pos(2, 3)));

		Assert.assertEquals(2, map.pos(1, 1).distance(map.pos(3, 3)));
		Assert.assertEquals(1, map.pos(0, 0).distance(map.pos(3, 3)));
		Assert.assertEquals(1, map.pos(0, 2).distance(map.pos(3, 1)));
	}

	@Test
	public void testMoveConstantStress() {
		StressMap map = new StressMap(new ConstantStresses(64), 8, 8);

		map.setForceReach(1);

		Assert.assertEquals(0, map.move(map.pos(0, 0), map.pos(7, 4), 1).calculateStressChange());

		map = new StressMap(new ConstantStresses(256), 16, 16);

		map.setForceReach(1);

		Assert.assertEquals(0, map.move(map.pos(0, 0), map.pos(9, 8), 2).calculateStressChange());
	}

	@Test
	public void testBoundary() {
		StressMap map = new StressMap(new QuadrangleBasedStresses(64), 8, 8);

		Assert.assertEquals("((7,7),(1,1))", map.pos(1, 1).area(map.pos(7, 7)).toString());

		Assert.assertEquals("((4,2),(5,3))", map.pos(4, 2).area(map.pos(5, 3)).toString());
	}

	@Test
	public void testSimpleMoveStressChange() {
		StressMap map = new StressMap(new QuadrangleBasedStresses(64), 8, 8);

		map.setForceReach(1);

		Assert.assertEquals(32, map.move(map.pos(2, 1), map.pos(6, 2), 0).calculateStressChange());
	}

	@Test
	public void testCheckerboardMove() {
		StressMap map = new StressMap(new CheckerBoardStresses(64, 8), 8, 8);

		map.setForceReach(1);

		Assert.assertEquals(0, map.move(map.pos(1, 1), map.pos(5, 3), 0).calculateStressChange());
	}

	@Test
	public void testLinearStressMove() {
		StressMap map = new StressMap(new LinearStresses(64), 8, 8);

		map.setForceReach(1);

		int stressLeft = map.move(map.pos(3, 3), map.pos(2, 2), 0).calculateStressChange();
		int stressRight = map.move(map.pos(3, 3), map.pos(4, 4), 0).calculateStressChange();

		Assert.assertTrue(stressLeft > 0);
		Assert.assertEquals(stressRight, stressLeft);
	}

	@Test
	public void testRandomMove() {
		RandomNumberGenerator random = new RandomNumberGenerator() {
			@Override
			public int nextInt(int max) {
				assert max > 0;
				return max - 1;
			}
		};

		StressMap map = new StressMap(new LinearStresses(64), 8, 8);
		map.random = random;
		Move move = map.randomMove(StressMap.START_TEMPERATURE);

		Assert.assertEquals(1, move.radius);
		Assert.assertEquals("(7,7)", move.from.toString());
		Assert.assertEquals("(4,7)", move.to.toString());

		map = new StressMap(new LinearStresses(128), 16, 8);
		map.random = random;
		move = map.randomMove(StressMap.START_TEMPERATURE);

		Assert.assertEquals(3, move.radius);
		Assert.assertEquals("(15,7)", move.from.toString());
		Assert.assertEquals("(8,7)", move.to.toString());

		map = new StressMap(new LinearStresses(256), 16, 16);
		map.random = random;
		move = map.randomMove(StressMap.START_TEMPERATURE);

		Assert.assertEquals(3, move.radius);
		Assert.assertEquals("(15,15)", move.from.toString());
		Assert.assertEquals("(8,15)", move.to.toString());

		move = map.randomMove(0);

		Assert.assertEquals(0, move.radius);
		Assert.assertEquals("(15,15)", move.from.toString());
		Assert.assertEquals("(14,15)", move.to.toString());
	}

	@Test
	public void testCalculateStress() {
		StressMap map = new StressMap(new ConstantStresses(16), 4, 4);

		map.setForceReach(1);

		Assert.assertEquals(16 * 8, map.calculateStress());

		map.setForceReach(2);

		Assert.assertEquals(16 * 8 + 8 * 16, map.calculateStress());
	}

	@Test
	public void testRandomStressAndStressChangeAgree() {
		StressMap map = new StressMap(new LinearStresses(64), 8, 8);
		map.setForceReach(1);

		testRandomStressAndStressChangeAgree(map);
	}

	@Test
	public void testRandomStressAndStressChangeAgreeForce2() {
		StressMap map = new StressMap(new LinearStresses(64), 8, 8);
		map.setForceReach(2);

		testRandomStressAndStressChangeAgree(map);
	}

	@Test
	public void testRandomStressAndStressChangeAgreeRectangular() {
		StressMap map = new StressMap(new LinearStresses(128), 16, 8);
		map.setForceReach(1);

		testRandomStressAndStressChangeAgree(map);
	}

	@Test
	public void testStressAndStressChangeAgreeRectangular() {
		StressMap map = new StressMap(new LinearStresses(128), 16, 8);
		map.setForceReach(1);

		Move move = map.move(map.pos(7, 7), map.pos(2, 0), 1);

		int change = move.calculateStressChange();

		long stressBefore = map.calculateStress();
		move.move();

		long stressAfter = map.calculateStress();

		Assert.assertEquals(change, stressAfter - stressBefore);
	}

	@Test
	public void foo() {

		RecordingStressMap map = new RecordingStressMap(new Stresses() {
			@Override
			public int size() {
				return 64;
			}

			@Override
			public int get(int i, int j) {
				return Math.abs((i % 2) - (j % 2));
			}
		}, 8, 8);

		Move move = map.move(map.pos(3, 0), map.pos(4, 4), 0);

		int predictedChange = move.calculateStressChange();

		map.clear();

		long before = map.calculateStress();

		move.move();

		long after = map.calculateStress();

		Assert.assertEquals(predictedChange, after - before);
	}

	private void testRandomStressAndStressChangeAgree(StressMap map) {
		long stress = map.calculateStress();

		for (int i = 0; i < 100; i++) {
			Move move = map.randomMove(map.random.nextInt(1024) * 1024);

			int stressChange = move.calculateStressChange();

			System.out.println(move);
			move.move();

			stress += stressChange;

			Assert.assertEquals(stress, map.calculateStress());
		}
	}

	@Test
	public void testStressAndStressChangeAgree() {
		StressMap map = new StressMap(new LinearStresses(16), 4, 4);

		map.setForceReach(1);

		Move move = map.move(map.pos(1, 1), map.pos(1, 2), 0);

		int stressChange = move.calculateStressChange();

		long oldStress = map.calculateStress();

		move.move();

		long newStress = map.calculateStress();

		Assert.assertEquals(stressChange, newStress - oldStress);
	}

	@Test
	public void testAdjacentMovesStressChange() {
		StressMap map = new StressMap(new Stresses() {

			@Override
			public int get(int i, int j) {
				assert i != j;

				if (i > j) {
					int t = j;
					j = i;
					i = t;
				}

				if (i == 5 && j == 7) {
					return 2;
				}

				if (i == 6 && j == 7) {
					return 1;
				}
				return 0;
			}

			@Override
			public int size() {
				return 16;
			}
		}, 4, 4);

		map.setForceReach(1);

		Move move = map.move(map.pos(2, 3), map.pos(1, 1), 0);

		int change = move.calculateStressChange();

		long stressBefore = map.calculateStress();

		move.move();
		long stressAfter = map.calculateStress();

		Assert.assertEquals(stressAfter - stressBefore, change);
	}

	@Test
	public void testAdjacentMovesStressChangeRadius1() {
		StressMap map = new StressMap(new QuadrangleBasedStresses(64), 8, 8);

		map.setForceReach(1);

		Move move = map.move(map.pos(2, 2), map.pos(5, 5), 1);

		int change = move.calculateStressChange();

		System.out.println();
		System.out.println("end change");

		long before = map.calculateStress();

		move.move();

		long after = map.calculateStress();

		Assert.assertEquals(after - before, change);
	}
}
