package me.bbsarikaya.solution;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

public class GasStationImpl implements GasStation {

	private HashMap<GasType, LinkedList<GasPump>> pumpMap;
	private HashMap<GasType, Double> priceMap;

	public GasStationImpl() {
		pumpMap = new HashMap<GasType, LinkedList<GasPump>>();
		priceMap = new HashMap<GasType, Double>();
	}

	public void addGasPump(GasPump pump) {
		if (!pumpMap.containsKey(pump.getGasType())) {
			pumpMap.put(pump.getGasType(), new LinkedList<GasPump>());
		}
		pumpMap.get(pump.getGasType()).add(pump);
	}

	public Collection<GasPump> getGasPumps() {
		LinkedList<GasPump> clonePumpList = new LinkedList<GasPump>();
		for (LinkedList<GasPump> pumpList : pumpMap.values()) {
			for (GasPump pump : pumpList) {
				GasPump clonePump = new GasPump(pump.getGasType(), pump.getRemainingAmount());
				clonePumpList.add(clonePump);
			}
		}
		return clonePumpList;
	}

	public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter)
			throws NotEnoughGasException, GasTooExpensiveException {
		GasPump pump = findAvailablePump(type, amountInLiters);
		if (null == pump) {
			throw new NotEnoughGasException();
		} else {
			if (getPrice(type) <= maxPricePerLiter) {
				pump.pumpGas(amountInLiters);
				return getPrice(type) * amountInLiters;
			} else {
				throw new GasTooExpensiveException();
			}
		}
	}

	private GasPump findAvailablePump(GasType type, double liters) {
		if (pumpMap.containsKey(type) && pumpMap.get(type).size() > 0) {
			for (GasPump pump : pumpMap.get(type)) {
				if (pump.getRemainingAmount() > liters) {
					return pump;
				}
			}
		}
		return null;
	}

	public double getRevenue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNumberOfSales() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNumberOfCancellationsNoGas() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNumberOfCancellationsTooExpensive() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getPrice(GasType type) {
		if (priceMap.containsKey(type)) {
			return priceMap.get(type);
		}
		throw new IllegalStateException();
	}

	public void setPrice(GasType type, double price) {
		if (price >= 0.0) {
			priceMap.put(type, price);
		} else {
			priceMap.put(type, 0.0);
		}
	}

}
