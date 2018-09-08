package test.bbsarikaya.solution;

import static org.junit.Assert.*;

import org.junit.Test;

import me.bbsarikaya.solution.GasTransaction;
import net.bigpoint.assessment.gasstation.GasType;

public class GasTransactionTest {

	@Test
	public void testCreate() {
		GasTransaction transaction = GasTransaction.create(GasTransaction.Status.SUCCESS, GasType.DIESEL, 10.0, 5.0);
		assertEquals(GasTransaction.Status.SUCCESS, transaction.getStatus());
		assertEquals(GasType.DIESEL, transaction.getGasType());
		assertEquals(10.0, transaction.getGasAmount(), 0.0);
		assertEquals(5.0, transaction.getGasPrice(), 0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateWithInvalidAmount() {
		GasTransaction.create(GasTransaction.Status.SUCCESS, GasType.DIESEL, -10.0, 5.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateWithInvalidPrice() {
		GasTransaction.create(GasTransaction.Status.SUCCESS, GasType.DIESEL, 10.0, -5.0);
	}

	@Test
	public void testGetStatus() {
		GasTransaction transaction = GasTransaction.create(GasTransaction.Status.SUCCESS, GasType.DIESEL, 10.0, 5.0);
		assertEquals(GasTransaction.Status.SUCCESS, transaction.getStatus());
	}

	@Test
	public void testGetGasType() {
		GasTransaction transaction = GasTransaction.create(GasTransaction.Status.SUCCESS, GasType.DIESEL, 10.0, 5.0);
		assertEquals(GasType.DIESEL, transaction.getGasType());
	}

	@Test
	public void testGetAmount() {
		GasTransaction transaction = GasTransaction.create(GasTransaction.Status.SUCCESS, GasType.DIESEL, 10.0, 5.0);
		assertEquals(10.0, transaction.getGasAmount(), 0.0);
	}

	@Test
	public void testGetPrice() {
		GasTransaction transaction = GasTransaction.create(GasTransaction.Status.SUCCESS, GasType.DIESEL, 10.0, 5.0);
		assertEquals(5.0, transaction.getGasPrice(), 0.0);
	}

	@Test
	public void testGetTotalPrice() {
		GasTransaction transaction = GasTransaction.create(GasTransaction.Status.SUCCESS, GasType.DIESEL, 10.0, 5.0);
		assertEquals(50.0, transaction.getTotalPrice(), 0.0);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetTotalPriceForCancelledNoGas() {
		GasTransaction transaction = GasTransaction.create(GasTransaction.Status.CANCELLED_NOGAS, GasType.DIESEL, 10.0,
				5.0);
		transaction.getTotalPrice();
	}

	@Test(expected = IllegalStateException.class)
	public void testGetTotalPriceForCancelledTooExpensive() {
		GasTransaction transaction = GasTransaction.create(GasTransaction.Status.CANCELLED_TOOEXPENSIVE, GasType.DIESEL,
				10.0, 5.0);
		transaction.getTotalPrice();
	}
}
