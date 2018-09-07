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
		GasPump pump = new GasPump(GasType.DIESEL, 100.0);
		station.addGasPump(pump);

		Collection<GasPump> pumps = station.getGasPumps();
		assertEquals(1, pumps.size());
		assertFalse(pumps.contains(pump)); // to forbid manipulating actual data

		GasPump returnedPump = (GasPump) pumps.toArray()[0];
		assertEquals(GasType.DIESEL, returnedPump.getGasType());
		assertEquals(100.0, returnedPump.getRemainingAmount(), 0);
	}

	@Test
	public void testGasPumpsWithMorePumps() {
		GasStationImpl station = new GasStationImpl();
		GasPump pumpDiesel = new GasPump(GasType.DIESEL, 100.0);
		GasPump pumpRegular = new GasPump(GasType.REGULAR, 90.0);
		GasPump pumpSuper = new GasPump(GasType.SUPER, 80.0);

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
		station.setPrice(GasType.DIESEL, 10.0);
		station.setPrice(GasType.REGULAR, 5.0);
		station.setPrice(GasType.SUPER, 15.0);

		assertEquals(10.0, station.getPrice(GasType.DIESEL), 0);
		assertEquals(5.0, station.getPrice(GasType.REGULAR), 0);
		assertEquals(15.0, station.getPrice(GasType.SUPER), 0);
	}

	@Test
	public void testPriceReset() {
		GasStationImpl station = new GasStationImpl();

		station.setPrice(GasType.DIESEL, 10.0);
		assertEquals(10.0, station.getPrice(GasType.DIESEL), 0);

		station.setPrice(GasType.DIESEL, 15.0);
		assertEquals(15.0, station.getPrice(GasType.DIESEL), 0);
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
	    double totalPrice = station.buyGas(GasType.DIESEL, 12.0, 5.0);
	    assertEquals(36.0, totalPrice, 0);
	}
	
	@Test(expected = NotEnoughGasException.class)
	public void testBuyGasForMultipleSales() throws NotEnoughGasException, GasTooExpensiveException {
		GasStationImpl station = createTestStation();
	    station.buyGas(GasType.DIESEL, 12.0, 5.0);
	    station.buyGas(GasType.DIESEL, 12.0, 5.0);
	}
	
	@Test(expected = NotEnoughGasException.class)
	public void testBuyGasWhenNotEnoughGas() throws NotEnoughGasException, GasTooExpensiveException {
		GasStationImpl station = createTestStation();
		station.buyGas(GasType.DIESEL, 20.0, 5.0);
	}
	
	@Test(expected = NotEnoughGasException.class)
	public void testBuyGasWhenNoGas() throws NotEnoughGasException, GasTooExpensiveException {
		GasStationImpl station = createTestStation();
		station.buyGas(GasType.SUPER, 10.0, 5.0);
	}
	
	@Test(expected = GasTooExpensiveException.class)
	public void testBuyGasWhenGasTooExpensive() throws NotEnoughGasException, GasTooExpensiveException {
		GasStationImpl station = createTestStation();
		station.buyGas(GasType.DIESEL, 5.0, 2.0);
	}

	private GasStationImpl createTestStation() {
		GasStationImpl station = new GasStationImpl();
		station.addGasPump(new GasPump(GasType.DIESEL, 10.0));
		station.addGasPump(new GasPump(GasType.DIESEL, 15.0));
		station.setPrice(GasType.DIESEL, 3.0);		
		return station;
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
		totalRevenue += station.buyGas(GasType.DIESEL, 10, 5.0);
		assertEquals(totalRevenue, station.getRevenue(), 0.0);
		totalRevenue += station.buyGas(GasType.DIESEL, 5, 5.0);
		assertEquals(totalRevenue, station.getRevenue(), 0.0);
	}
	
	@Test
	public void testGetRevenueWithException() {
		GasStationImpl station = createTestStation();
		double expectedRevenue = 30.0;
		try {
			station.buyGas(GasType.DIESEL, 10, 5.0);
			assertEquals(expectedRevenue, station.getRevenue(), 0.0);
			station.buyGas(GasType.DIESEL, 5000, 5.0);	
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
		station.buyGas(GasType.DIESEL, 10, 5.0);
		assertEquals(1, station.getNumberOfSales());
		station.buyGas(GasType.DIESEL, 5, 5.0);
		assertEquals(2, station.getNumberOfSales());
	}
	
	@Test
	public void testGetNumberOfSalesWithException() {
		GasStationImpl station = createTestStation();
		try {
			station.buyGas(GasType.DIESEL, 10, 5.0);
			assertEquals(1, station.getNumberOfSales());
			station.buyGas(GasType.DIESEL, 5000, 5.0);
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
			station.buyGas(GasType.DIESEL, 10, 5.0);
			assertEquals(0, station.getNumberOfCancellationsNoGas());
			station.buyGas(GasType.DIESEL, 5000, 5.0);
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
			station.buyGas(GasType.DIESEL, 10, 5.0);
			assertEquals(0, station.getNumberOfCancellationsTooExpensive());
			station.buyGas(GasType.DIESEL, 5, 2.0);
		} catch (GasTooExpensiveException e) {
			assertEquals(1, station.getNumberOfCancellationsTooExpensive());
		} catch (Exception e) {
			fail();
		}
	}
}
