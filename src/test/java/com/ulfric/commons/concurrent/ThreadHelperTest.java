package com.ulfric.commons.concurrent;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.google.common.truth.Truth;

import com.ulfric.commons.test.HelperTestSuite;
import com.ulfric.veracity.Veracity;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RunWith(JUnitPlatform.class)
class ThreadHelperTest extends HelperTestSuite {

	ThreadHelperTest() {
		super(ThreadHelper.class);
	}

	@Test
	void testStartThread() {
		Thread thread = ThreadHelper.start(PauseForOneSecondThread::new);
		Veracity.assertThat(thread).isAlive();
	}

	@Test
	void testSleepWithoutInterrupt() throws Exception {
		Thread thread = ThreadHelper.start(() -> Truth.assertThat(ThreadHelper.sleep(Duration.ofMillis(1)).isSuccess()).isTrue());
		thread.join();
	}

	@Test
	void testSleepWithInterrupt() throws Exception {
		Thread thread = ThreadHelper.start(() -> Truth.assertThat(ThreadHelper.sleep(Duration.ofSeconds(1)).isSuccess()).isFalse());
		thread.interrupt();
	}

	private static final class PauseForOneSecondThread extends Thread {
		PauseForOneSecondThread() {
			super(PauseForOneSecondThread.class.getSimpleName());
		}

		@Override
		public void run() {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
			}
		}
	}

}