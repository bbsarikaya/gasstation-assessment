package test.bbsarikaya.solution;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import me.bbsarikaya.solution.GasStationImpl;
import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

public class GasStationImplTest {

	private static final double PRICE_LOW = 5.0;
	private static final double PRICE_NORMAL = 10.0;
	private static final double PRICE_HIGH = 15.0;

	private static final double PUMP_LITERS_LOW = 1.0;
	private static final double PUMP_LITERS_MAX = 5.0;
	private static final double PUMP_LITERS_OVER = 15.0;

	@Test
	public void testGetGasPumpsWhenNoPump() {
		GasStationImpl station = new GasStationImpl();

		Collection<GasPump> pumps = station.getGasPumps();
		assertNotNull(pumps);
		assertEquals(0, pumps.size());
	}

	@Test
	public void testGasPumps() {
		GasStationImpl station = new GasStationImpl();
		GasPump pump = new GasPump(GasType.DIESEL, PUMP_LITERS_MAX);
		station.addGasPump(pump);

		Collection<GasPump> pumps = station.getGasPumps();
		assertEquals(1, pumps.size());
		assertFalse(pumps.contains(pump)); // to forbid manipulating actual data

		GasPump returnedPump = (GasPump) pumps.toArray()[0];
		assertEquals(GasType.DIESEL, returnedPump.getGasType());
		assertEquals(PUMP_LITERS_MAX, returnedPump.getRemainingAmount(), 0);
	}

	@Test
	public void testGasPumpsWithMorePumps() {
		GasStationImpl station = new GasStationImpl();
		GasPump pumpDiesel = new GasPump(GasType.DIESEL, PUMP_LITERS_MAX);
		GasPump pumpRegular = new GasPump(GasType.REGULAR, PUMP_LITERS_MAX);
		GasPump pumpSuper = new GasPump(GasType.SUPER, PUMP_LITERS_MAX);

		station.addGasPump(pumpDiesel);
		station.addGasPump(pumpRegular);
		station.addGasPump(pumpSuper);

		Collection<GasPump> pumps = station.getGasPumps();
		assertEquals(3, pumps.size());
	}

	@Test(expected = IllegalStateException.class)
	public void testPriceWhenNoPriceSet() {
		GasStationImpl station = new GasStationImpl();
		station.getPrice(GasType.DIESEL);
	}

	@Test
	public void testPrice() {
		GasStationImpl station = new GasStationImpl();
		station.setPrice(GasType.DIESEL, PRICE_LOW);
		station.setPrice(GasType.REGULAR, PRICE_NORMAL);
		station.setPrice(GasType.SUPER, PRICE_HIGH);

		assertEquals(PRICE_LOW, station.getPrice(GasType.DIESEL), 0);
		assertEquals(PRICE_NORMAL, station.getPrice(GasType.REGULAR), 0);
		assertEquals(PRICE_HIGH, station.getPrice(GasType.SUPER), 0);
	}

	@Test
	public void testPriceReset() {
		GasStationImpl station = new GasStationImpl();

		station.setPrice(GasType.DIESEL, PRICE_LOW);
		assertEquals(PRICE_LOW, station.getPrice(GasType.DIESEL), 0);

		station.setPrice(GasType.DIESEL, PRICE_HIGH);
		assertEquals(PRICE_HIGH, station.getPrice(GasType.DIESEL), 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativePrice() {
		GasStationImpl station = new GasStationImpl();
		station.setPrice(GasType.DIESEL, -10.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testZeroPrice() {
		GasStationImpl station = new GasStationImpl();
		station.setPrice(GasType.DIESEL, 0.0);
	}

	@Test
	public void testBuyGas() throws NotEnoughGasException, GasTooExpensiveException {
		GasStationImpl station = createTestStation();
		double totalPrice = station.buyGas(GasType.DIESEL, PUMP_LITERS_LOW, PRICE_HIGH);
		assertEquals(PUMP_LITERS_LOW * PRICE_NORMAL, totalPrice, 0);
	}

	@Test
	public void testBuyGasForMultipleSales() throws NotEnoughGasException, GasTooExpensiveException {
		GasStationImpl station = createTestStation();
		station.buyGas(GasType.DIESEL, PUMP_LITERS_MAX, PRICE_NORMAL);
		station.buyGas(GasType.DIESEL, PUMP_LITERS_LOW, PRICE_NORMAL);
	}

	@Test(expected = NotEnoughGasException.class)
	public void testBuyGasWhenNotEnoughGas() throws NotEnoughGasException, GasTooExpensiveException {
		GasStationImpl station = createTestStation();
		station.buyGas(GasType.DIESEL, PUMP_LITERS_OVER, PRICE_NORMAL);
	}

	@Test(expected = NotEnoughGasException.class)
	public void testBuyGasWhenNoGas() throws NotEnoughGasException, GasTooExpensiveException {
		GasStationImpl station = createTestStation();
		station.buyGas(GasType.SUPER, PUMP_LITERS_MAX, PRICE_NORMAL);
	}

	@Test(expected = GasTooExpensiveException.class)
	public void testBuyGasWhenGasTooExpensive() throws NotEnoughGasException, GasTooExpensiveException {
		GasStationImpl station = createTestStation();
		station.buyGas(GasType.DIESEL, PUMP_LITERS_MAX, PRICE_LOW);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuyGasWithInvalidAmount() throws NotEnoughGasException, GasTooExpensiveException {
		GasStationImpl station = createTestStation();
		station.buyGas(GasType.DIESEL, -10.0, PRICE_LOW);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuyGasWithInvalidPrice() throws NotEnoughGasException, GasTooExpensiveException {
		GasStationImpl station = createTestStation();
		station.buyGas(GasType.DIESEL, PUMP_LITERS_MAX, -5.0);
	}

	@Test
	public void testGetInitialRevenue() {
		GasStationImpl station = createTestStation();
		assertEquals(0.0, station.getRevenue(), 0.0);
	}

	@Test
	public void testGetRevenue() throws NotEnoughGasException, GasTooExpensiveException {
		GasStationImpl station = createTestStation();
		double totalRevenue = 0.0;
		totalRevenue += station.buyGas(GasType.DIESEL, PUMP_LITERS_MAX, PRICE_NORMAL);
		assertEquals(totalRevenue, station.getRevenue(), 0.0);
		totalRevenue += station.buyGas(GasType.DIESEL, PUMP_LITERS_LOW, PRICE_HIGH);
		assertEquals(totalRevenue, station.getRevenue(), 0.0);
	}

	@Test
	public void testGetRevenueWithException() {
		GasStationImpl station = createTestStation();
		double expectedRevenue = PUMP_LITERS_MAX * PRICE_NORMAL;
		try {
			station.buyGas(GasType.DIESEL, PUMP_LITERS_MAX, PRICE_NORMAL);
			assertEquals(expectedRevenue, station.getRevenue(), 0.0);
			station.buyGas(GasType.DIESEL, PUMP_LITERS_OVER, PRICE_NORMAL);
		} catch (Exception e) {
			assertEquals(expectedRevenue, station.getRevenue(), 0.0);
		}
	}

	@Test
	public void testGetInitialNumberOfSales() {
		GasStationImpl station = createTestStation();
		assertEquals(0, station.getNumberOfSales());
	}

	@Test
	public void testGetNumberOfSales() throws NotEnoughGasException, GasTooExpensiveException {
		GasStationImpl station = createTestStation();
		station.buyGas(GasType.DIESEL, PUMP_LITERS_MAX, PRICE_NORMAL);
		assertEquals(1, station.getNumberOfSales());
		station.buyGas(GasType.DIESEL, PUMP_LITERS_MAX, PRICE_NORMAL);
		assertEquals(2, station.getNumberOfSales());
	}

	@Test
	public void testGetNumberOfSalesWithException() {
		GasStationImpl station = createTestStation();
		try {
			station.buyGas(GasType.DIESEL, PUMP_LITERS_MAX, PRICE_NORMAL);
			assertEquals(1, station.getNumberOfSales());
			station.buyGas(GasType.DIESEL, PUMP_LITERS_OVER, PRICE_NORMAL);
		} catch (Exception e) {
			assertEquals(1, station.getNumberOfSales());
		}
	}

	@Test
	public void testGetInitialNumberOfCancellationsNoGas() {
		GasStationImpl station = createTestStation();
		assertEquals(0, station.getNumberOfCancellationsNoGas());
	}

	@Test
	public void testGetNumberOfCancellationsNoGas() {
		GasStationImpl station = createTestStation();
		try {
			station.buyGas(GasType.DIESEL, PUMP_LITERS_MAX, PRICE_NORMAL);
			assertEquals(0, station.getNumberOfCancellationsNoGas());
			station.buyGas(GasType.DIESEL, PUMP_LITERS_OVER, PRICE_NORMAL);
		} catch (NotEnoughGasException e) {
			assertEquals(1, station.getNumberOfCancellationsNoGas());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetInitialNumberOfCancellationsTooExpensive() {
		GasStationImpl station = createTestStation();
		assertEquals(0, station.getNumberOfCancellationsTooExpensive());
	}

	@Test
	public void testGetNumberOfCancellationsTooExpensive() {
		GasStationImpl station = createTestStation();
		try {
			station.buyGas(GasType.DIESEL, PUMP_LITERS_MAX, PRICE_NORMAL);
			assertEquals(0, station.getNumberOfCancellationsTooExpensive());
			station.buyGas(GasType.DIESEL, PUMP_LITERS_MAX, PRICE_LOW);
		} catch (GasTooExpensiveException e) {
			assertEquals(1, station.getNumberOfCancellationsTooExpensive());
		} catch (Exception e) {
			fail();
		}
	}

	private GasStationImpl createTestStation() {
		GasStationImpl station = new GasStationImpl();
		station.addGasPump(new GasPump(GasType.DIESEL, PUMP_LITERS_MAX));
		station.addGasPump(new GasPump(GasType.DIESEL, PUMP_LITERS_MAX));
		station.setPrice(GasType.DIESEL, PRICE_NORMAL);
		station.setPrice(GasType.REGULAR, PRICE_NORMAL);
		station.setPrice(GasType.SUPER, PRICE_NORMAL);
		return station;
	}
}
