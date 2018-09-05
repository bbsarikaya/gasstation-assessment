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

	public GasStationImpl() {
		pumpMap = new HashMap<GasType, LinkedList<GasPump>>();
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
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return 0;
	}

	public void setPrice(GasType type, double price) {
		// TODO Auto-generated method stub
		
	}

}
