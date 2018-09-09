package me.bbsarikaya.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

public class GasStationImpl implements GasStation {

	private ConcurrentHashMap<GasType, CopyOnWriteArrayList<GasPump>> pumpMap;
	private ConcurrentHashMap<GasType, Double> priceMap;
	private TransactionStats stats;

	public GasStationImpl() {
		pumpMap = new ConcurrentHashMap<GasType, CopyOnWriteArrayList<GasPump>>();
		priceMap = new ConcurrentHashMap<GasType, Double>();
		stats = new TransactionStats();
	}

	public void addGasPump(GasPump pump) {
		pumpMap.putIfAbsent(pump.getGasType(), new CopyOnWriteArrayList<GasPump>());
		pumpMap.get(pump.getGasType()).add(pump);
	}

	// returns pumps' clones to forbid manipulating actual data
	public Collection<GasPump> getGasPumps() {
		ArrayList<GasPump> clonePumpList = new ArrayList<GasPump>();
		for (CopyOnWriteArrayList<GasPump> pumpList : pumpMap.values()) {
			for (GasPump pump : pumpList) {
				GasPump clonePump = new GasPump(pump.getGasType(), pump.getRemainingAmount());
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
		GasPump pump = findAvailablePump(type, amountInLiters);
		if (null == pump) {
			stats.logCancellationNoGas();
			throw new NotEnoughGasException();
		} else {
			if (getPrice(type) <= maxPricePerLiter) {
				pump.pumpGas(amountInLiters);
				stats.logSale(getPrice(type) * amountInLiters);
				return getPrice(type) * amountInLiters;
			} else {
				stats.logCancellationTooExpensive();
				throw new GasTooExpensiveException();
			}
		}
	}

	private GasPump findAvailablePump(GasType type, double liters) {
		if (pumpMap.containsKey(type) && pumpMap.get(type).size() > 0) {
			for (GasPump pump : pumpMap.get(type)) {
				if (pump.getRemainingAmount() >= liters) {
					return pump;
				}
			}
		}
		return null;
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
	
}
