package test.bbsarikaya.solution;

import static org.junit.Assert.*;

import org.junit.Test;

import me.bbsarikaya.solution.TransactionStats;

public class TransactionStatsTest {

	@Test
	public void testLogSale() {
		TransactionStats stat = new TransactionStats();
		assertEquals(0, stat.getNumberOfSales());
		assertEquals(0.0, stat.getRevenue(), 0.0);

		stat.logSale(10.0);
		assertEquals(1, stat.getNumberOfSales());
		assertEquals(10.0, stat.getRevenue(), 0.0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testLogSaleWithInvalidPrice() {
		TransactionStats stat = new TransactionStats();
		stat.logSale(-10.0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testLogSaleWithZeroPrice() {
		TransactionStats stat = new TransactionStats();
		stat.logSale(0.0);
	}

	@Test
	public void testLogCancellationNoGas() {
		TransactionStats stat = new TransactionStats();
		stat.logCancellationNoGas(); // see if it works without an error
	}	

	@Test
	public void testLogCancellationTooExpensive() {
		TransactionStats stat = new TransactionStats();
		stat.logCancellationTooExpensive();  // see if it works without an error
	}	

	@Test
	public void testGetRevenue() {
		TransactionStats stat = new TransactionStats();

		stat.logSale(10.0);
		assertEquals(10.0, stat.getRevenue(), 0.0);
		
		stat.logSale(11.0);
		assertEquals(21.0, stat.getRevenue(), 0.0);
	}	

	@Test
	public void testGetNumberOfSales() {
		TransactionStats stat = new TransactionStats();

		stat.logSale(10.0);
		assertEquals(1, stat.getNumberOfSales());
		
		stat.logSale(11.0);
		assertEquals(2, stat.getNumberOfSales());
	}	

	@Test
	public void testGetNumberOfCancellationNoGas() {
		TransactionStats stat = new TransactionStats();
		assertEquals(0, stat.getNumberOfCancellationNoGas());

		stat.logCancellationNoGas();
		assertEquals(1, stat.getNumberOfCancellationNoGas());
	}	

	@Test
	public void testGetNumberOfCancellationTooExpensive() {
		TransactionStats stat = new TransactionStats();
		assertEquals(0, stat.getNumberOfCancellationTooExpensive());

		stat.logCancellationTooExpensive();
		assertEquals(1, stat.getNumberOfCancellationTooExpensive());
	}	

}