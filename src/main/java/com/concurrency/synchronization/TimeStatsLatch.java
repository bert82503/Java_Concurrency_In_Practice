/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.concurrency.synchronization;

import java.util.concurrent.CountDownLatch;

/**
 * “应用程序执行时间统计”示例。
 *
 * @author	lihg
 * @version 2013-12-1
 */
public class TimeStatsLatch {

	/**
	 * 模拟真实并发应用程序，并进行执行时间性能统计。
	 * 
	 * <p>测试N个线程并发执行某个任务时需要的时间。
	 *
	 * @param nThreads
	 * @param task
	 * @return
	 * @throws InterruptedException
	 */
	public static long timeTasks(int nThreads, final Runnable task) 
			throws InterruptedException {
		final CountDownLatch startGate = new CountDownLatch(1); // 起始门
		final CountDownLatch endGate = new CountDownLatch(nThreads); // 结束门
		
		for (int i = 0; i < nThreads; i++) {
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						startGate.await(); // 在启动门上等待
						try {
							task.run();
						} finally {
							endGate.countDown();
						}
					} catch (InterruptedException ignored) {
						// ignore
					}
				}
			};
			t.start();
		}
		
		long startTime = System.currentTimeMillis();
		startGate.countDown(); // 确保所有线程都就绪后才开始执行
		endGate.await(); // 主线程高效地等待直到所有工作线程都执行完成
		long endTime = System.currentTimeMillis();
		return endTime - startTime;
	}

}
