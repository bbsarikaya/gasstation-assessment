package me.bbsarikaya.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import me.bbsarikaya.solution.GasTransaction.Status;
import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

public class GasStationImpl implements GasStation {

	private HashMap<GasType, ArrayList<GasPump>> pumpMap;
	private HashMap<GasType, Double> priceMap;
	private HashMap<GasTransaction.Status, ArrayList<GasTransaction>> transactionMap;

	public GasStationImpl() {
		pumpMap = new HashMap<GasType, ArrayList<GasPump>>();
		priceMap = new HashMap<GasType, Double>();
		transactionMap = new HashMap<GasTransaction.Status, ArrayList<GasTransaction>>();
	}

	public void addGasPump(GasPump pump) {
		if (!pumpMap.containsKey(pump.getGasType())) {
			pumpMap.put(pump.getGasType(), new ArrayList<GasPump>());
		}
		pumpMap.get(pump.getGasType()).add(pump);
	}

	public Collection<GasPump> getGasPumps() {
		ArrayList<GasPump> clonePumpList = new ArrayList<GasPump>();
		for (ArrayList<GasPump> pumpList : pumpMap.values()) {
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
			addTransaction(GasTransaction.Status.CANCELLED_NOGAS, type, amountInLiters);
			throw new NotEnoughGasException();
		} else {
			if (getPrice(type) <= maxPricePerLiter) {
				pump.pumpGas(amountInLiters);
				addTransaction(GasTransaction.Status.SUCCESS, type, amountInLiters);
				return getPrice(type) * amountInLiters;
			} else {
				addTransaction(GasTransaction.Status.CANCELLED_TOOEXPENSIVE, type, amountInLiters);
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

	private void addTransaction(Status status, GasType type, double amount) {
		if (!transactionMap.containsKey(status)) {
			transactionMap.put(status, new ArrayList<GasTransaction>());
		}
		transactionMap.get(status).add(GasTransaction.create(status, type, amount, getPrice(type)));
	}

	public double getRevenue() {
		double revenue = 0;
		if (transactionMap.containsKey(GasTransaction.Status.SUCCESS)) {
			for (GasTransaction transaction : transactionMap.get(GasTransaction.Status.SUCCESS)) {
				revenue += transaction.getTotalPrice();
			}
		}
		return revenue;
	}

	public int getNumberOfSales() {
		if (transactionMap.containsKey(GasTransaction.Status.SUCCESS)) {
			return transactionMap.get(GasTransaction.Status.SUCCESS).size();
		}
		return 0;
	}

	public int getNumberOfCancellationsNoGas() {
		if (transactionMap.containsKey(GasTransaction.Status.CANCELLED_NOGAS)) {
			return transactionMap.get(GasTransaction.Status.CANCELLED_NOGAS).size();
		}
		return 0;
	}

	public int getNumberOfCancellationsTooExpensive() {
		if (transactionMap.containsKey(GasTransaction.Status.CANCELLED_TOOEXPENSIVE)) {
			return transactionMap.get(GasTransaction.Status.CANCELLED_TOOEXPENSIVE).size();
		}
		return 0;
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
