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
		if (amount > 0 && price > 0) {
			return new GasTransaction(status, type, amount, price);
		} else {
			throw new IllegalArgumentException("Amount and price must be greater than zero!");
		}
	}

	private GasTransaction(Status status, GasType type, double amount, double price) {
		this.status = status;
		this.gasType = type;
		this.gasAmount = amount;
		this.gasPrice = price;
	}

	/**
	 * @return total price of successful transaction
	 */
	public double getTotalPrice() {
		if (status.equals(Status.SUCCESS)) {
			return gasAmount * gasPrice;
		} else {
			throw new IllegalStateException("Cancelled transaction cannot have total price!");
		}
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
