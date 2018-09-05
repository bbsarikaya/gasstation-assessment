package test.bbsarikaya.solution;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import me.bbsarikaya.solution.GasStationImpl;
import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;

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
	
}
