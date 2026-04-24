package com.sit.qb.dtos;

public class Menu_Qty {

	private long menuItemId;
	private long quantity;

	public Menu_Qty() {
	}

	public Menu_Qty(long menuItemId, long quantity) {
		super();
		this.menuItemId = menuItemId;
		this.quantity = quantity;
	}

	public long getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(long menuItemId) {
		this.menuItemId = menuItemId;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

}
