package me.bbsarikaya.solution;

import net.bigpoint.assessment.gasstation.GasType;

public class GasTransaction {

	public enum Status {
		SUCCESS, CANCELLED_NOGAS, CANCELLED_TOOEXPENSIVE
	}

	private Status status;
	private GasType gasType;
	private double gasAmount;
	private double gasPrice;

	public static GasTransaction create(Status status, GasType type, double amount, double price) {
		return new GasTransaction(status, type, amount, price);
	}

	private GasTransaction(Status status, GasType type, double amount, double price) {
		// TODO: Implement
	}

	/**
	 * @return total price for the transaction
	 */
	public double getTotalPrice() {
		return -1;
	}

	public Status getStatus() {
		return status;
	}

	public GasType getGasType() {
		return gasType;
	}

	public double getGasAmount() {
		return gasAmount;
	}

	/**
	 * @return price per liter
	 */
	public double getGasPrice() {
		return gasPrice;
	}
}
