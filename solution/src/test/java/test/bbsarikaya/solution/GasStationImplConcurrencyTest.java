package test.bbsarikaya.solution;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import me.bbsarikaya.solution.GasStationImpl;
import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

public class GasStationImplConcurrencyTest {

	@Test
	public void testBuyGas() throws NotEnoughGasException, GasTooExpensiveException, InterruptedException {
		GasStationImpl station = new GasStationImpl();
		station.setPrice(GasType.DIESEL, 5.0);
		addPumps(station, GasType.DIESEL, 1, 20.0);

		Thread t1 = createBuyThread(station, GasType.DIESEL, 4.5, 5.0);
		t1.start();
		Thread t2 = createBuyThread(station, GasType.DIESEL, 15.5, 5.0);
		t2.start();
		t1.join();
		t2.join();
		assertPumpsEmpty(station.getGasPumps());
	}

	@Test
	public void testBuyGasWithOneType() throws NotEnoughGasException, GasTooExpensiveException, InterruptedException {
		GasStationImpl station = new GasStationImpl();
		station.setPrice(GasType.DIESEL, 5.0);
		addPumps(station, GasType.DIESEL, 50, 20.0);

		ArrayList<Thread> threadList = new ArrayList<Thread>();
		for (int i = 0; i < 50; i++) {
			Thread t1 = createBuyThread(station, GasType.DIESEL, 10.0, 5.0);
			t1.start();
			threadList.add(t1);
			Thread t2 = createBuyThread(station, GasType.DIESEL, 10.0, 5.0);
			t2.start();
			threadList.add(t2);
		}
		for (Thread t : threadList) {
			t.join();
		}
		assertPumpsEmpty(station.getGasPumps());
	}

	@Test
	public void testBuyGasWithMoreType() throws NotEnoughGasException, GasTooExpensiveException, InterruptedException {
		GasStationImpl station = new GasStationImpl();
		station.setPrice(GasType.DIESEL, 5.0);
		station.setPrice(GasType.REGULAR, 5.0);
		addPumps(station, GasType.DIESEL, 50, 20.0);
		addPumps(station, GasType.REGULAR, 50, 20.0);

		ArrayList<Thread> threadList = new ArrayList<Thread>();
		for (int i = 0; i < 50; i++) {
			Thread t1 = createBuyThread(station, GasType.DIESEL, 20.0, 5.0);
			t1.start();
			threadList.add(t1);
			Thread t2 = createBuyThread(station, GasType.REGULAR, 20.0, 5.0);
			t2.start();
			threadList.add(t2);
		}
		for (Thread t : threadList) {
			t.join();
		}
		assertPumpsEmpty(station.getGasPumps());
	}

	private void addPumps(GasStationImpl station, GasType type, int pumpAmount, double liters) {
		for (int i = 0; i < pumpAmount; i++) {
			station.addGasPump(new GasPump(type, liters));
		}
	}

	private Thread createBuyThread(GasStationImpl station, GasType gasType, double literAmount, double price) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					station.buyGas(gasType, literAmount, price);
				} catch (NotEnoughGasException | GasTooExpensiveException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void assertPumpsEmpty(Collection<GasPump> pumps) {
		for (GasPump pump : pumps) {
			assertEquals(0.0, pump.getRemainingAmount(), 0.0);
		}
	}
}
