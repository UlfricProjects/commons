package com.ulfric.commons.runtime;

public final class ShutdownHook {

	private final Thread shutdownHook;
	private volatile boolean registered;

	ShutdownHook(Thread shutdownHook) {
		this.shutdownHook = shutdownHook;
	}

	public synchronized boolean isRegistered() {
		return registered;
	}

	public synchronized void register() {
		if (this.registered) {
			return;
		}

		this.registered = true;
		RuntimeHelper.addShutdownHook(this.shutdownHook);
	}

	public synchronized void unregister() {
		if (!this.registered) {
			return;
		}

		RuntimeHelper.removeShutdownHook(this.shutdownHook);
		this.registered = false;
	}

}