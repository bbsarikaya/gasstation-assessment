package me.bbsarikaya.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

public class GasStationImpl implements GasStation {

	private ConcurrentHashMap<GasType, CopyOnWriteArrayList<PumpHolder>> pumpHolderMap;
	private ConcurrentHashMap<GasType, Double> priceMap;
	private TransactionStats stats;

	public GasStationImpl() {
		pumpHolderMap = new ConcurrentHashMap<GasType, CopyOnWriteArrayList<PumpHolder>>();
		priceMap = new ConcurrentHashMap<GasType, Double>();
		stats = new TransactionStats();
	}

	public void addGasPump(GasPump pump) {
		pumpHolderMap.putIfAbsent(pump.getGasType(), new CopyOnWriteArrayList<PumpHolder>());
		pumpHolderMap.get(pump.getGasType()).add(new PumpHolder(pump));
	}

	// returns pumps' clones to forbid manipulating actual data
	public Collection<GasPump> getGasPumps() {
		ArrayList<GasPump> clonePumpList = new ArrayList<GasPump>();
		for (CopyOnWriteArrayList<PumpHolder> pumpHolderList : pumpHolderMap.values()) {
			for (PumpHolder pumpHolder : pumpHolderList) {
				GasPump clonePump = new GasPump(pumpHolder.pump.getGasType(), pumpHolder.pump.getRemainingAmount());
				clonePumpList.add(clonePump);
			}
		}
		return clonePumpList;
	}

	public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter)
			throws NotEnoughGasException, GasTooExpensiveException {
		if (amountInLiters <= 0 || maxPricePerLiter <= 0) {
			throw new IllegalArgumentException("Amount and price must be greater than zero!");
		}

		if (getPrice(type) > maxPricePerLiter) {
			stats.logCancellationTooExpensive();
			throw new GasTooExpensiveException();
		}

		if (pumpHolderMap.containsKey(type)) {
			CopyOnWriteArrayList<PumpHolder> pumpHolderList = pumpHolderMap.get(type);
			PumpHolder servingPumpHolder = null;
			synchronized (pumpHolderList) {
				servingPumpHolder = findPumpHolder(pumpHolderList, amountInLiters);
				updateRemaining(servingPumpHolder, amountInLiters);
			}

			if (null != servingPumpHolder) {
				GasPump servingPump = servingPumpHolder.pump;
				synchronized (servingPump) {
					servingPumpHolder.isAvailable = false;
					servingPump.pumpGas(amountInLiters);
					double totalPrice = amountInLiters * getPrice(type);
					stats.logSale(totalPrice);
					servingPumpHolder.isAvailable = true;
					return totalPrice;
				}
			}
		}
		stats.logCancellationNoGas();
		throw new NotEnoughGasException();
	}

	private void updateRemaining(PumpHolder pumpHolder, double amountInLiters) {
		if (null != pumpHolder) {
			pumpHolder.remainingAmount -= amountInLiters;
		}
	}

	private PumpHolder findPumpHolder(CopyOnWriteArrayList<PumpHolder> pumpHolderList, double amountInLiters) {
		Supplier<Stream<PumpHolder>> pumpsWithEnoughGas = () -> pumpHolderList.stream()
				.filter(status -> (status.remainingAmount >= amountInLiters))
				.sorted(Comparator.comparingDouble(holder -> holder.remainingAmount));

		return pumpsWithEnoughGas.get().filter(status -> (status.isAvailable)).findFirst()
				.orElse(pumpsWithEnoughGas.get().filter(status -> (!status.isAvailable)).findFirst()
				.orElse(null));
	}

	public double getRevenue() {
		return stats.getRevenue();
	}

	public int getNumberOfSales() {
		return stats.getNumberOfSales();
	}

	public int getNumberOfCancellationsNoGas() {
		return stats.getNumberOfCancellationNoGas();
	}

	public int getNumberOfCancellationsTooExpensive() {
		return stats.getNumberOfCancellationTooExpensive();
	}

	public double getPrice(GasType type) {
		if (priceMap.containsKey(type)) {
			return priceMap.get(type);
		}
		throw new IllegalStateException("Cannot get price of missing gas type!");
	}

	public void setPrice(GasType type, double price) {
		if (price > 0.0) {
			priceMap.put(type, price);
		} else {
			throw new IllegalArgumentException("Price must be greater than 0!");
		}
	}

	private class PumpHolder {
		boolean isAvailable = false;
		double remainingAmount = -1;
		GasPump pump = null;

		public PumpHolder(GasPump pump) {
			this.pump = pump;
			isAvailable = true;
			remainingAmount = pump.getRemainingAmount();
		}
	}
}
