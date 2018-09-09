package me.bbsarikaya.solution;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TransactionStats {

	private AtomicReference<Double> revenueRef;
	private AtomicInteger numberOfSales;
	private AtomicInteger numberOfCancellationNoGas;
	private AtomicInteger numberOfCancellationTooExpensive;

	public TransactionStats() {
		revenueRef = new AtomicReference<Double>(0.0);
		numberOfSales = new AtomicInteger(0);
		numberOfCancellationNoGas = new AtomicInteger(0);
		numberOfCancellationTooExpensive = new AtomicInteger(0);
	}

	public void logSale(final double price) {
		if (price <= 0.0) {
			throw new IllegalArgumentException("Price must be greater than zero!");
		}
		numberOfSales.incrementAndGet();
		revenueRef.updateAndGet(current -> current + price);
	}

	public void logCancellationNoGas() {
		numberOfCancellationNoGas.incrementAndGet();
	}

	public void logCancellationTooExpensive() {
		numberOfCancellationTooExpensive.incrementAndGet();
	}

	public double getRevenue() {
		return revenueRef.get();
	}

	public int getNumberOfSales() {
		return numberOfSales.get();
	}

	public int getNumberOfCancellationNoGas() {
		return numberOfCancellationNoGas.get();
	}

	public int getNumberOfCancellationTooExpensive() {
		return numberOfCancellationTooExpensive.get();
	}

}
