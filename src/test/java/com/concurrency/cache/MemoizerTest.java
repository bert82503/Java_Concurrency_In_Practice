/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.concurrency.cache;

import static java.lang.System.out;

import static org.testng.Assert.*;

import com.concurrency.time.TimeStats;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * {@link Memoizer} 漏洞测试。
 *
 * @author	lihg
 * @version 2013-12-1
 */
public class MemoizerTest {

	/**
	 * 基于相同的key，观察线程数与执行时间的关系：
	 *    300个线程并发，都能在15ms内返回结果；
	 *    1400个线程并发，能保证在50ms以内出结果；
	 *    2000开始性能就恶化明显，尤其是在2400这个分水岭。
	 *    
	 * [实验数据]
	 * 线程数	时间(ms)		异常
	 * 300		15
	 * 1000		35
	 * 1400		50
	 * 2000		75
	 * 2400		100
	 * 2600		130
	 * 3000		210
	 * 3500		335
	 * 3800		420
	 * 3900		450
	 * 3950					java.lang.OutOfMemoryError: unable to create new native thread
	 * 
	 * 
	 * 基于相同的线程数，观察不同的key与执行时间的关系：
	 *    1000个线程并发下，执行时间没多大影响；
	 *    但在2400个线程并发高负载下，执行时间有10~20%的影响。
	 *    
	 * 
	 * 实验环境：32位 Win 7 操作系统，i52450M CPU，4G 内存(2.66G 可用)
	 *
	 * @throws InterruptedException
	 */
	@Test(dataProvider = "compute")
	public void compute(String key, int nThreads, long expectedRunTime) throws InterruptedException {
		MemoizerTask mTask = new MemoizerTask(key);
		
		long time = TimeStats.timeTasks(nThreads, mTask);
		out.println("Thread Number: " + nThreads + "\t Run Time(ms): " + time);
		assertTrue(time <= expectedRunTime);
		assertEquals(mTask.cacheSize(), 1);
	}
	
	@DataProvider(name = "compute")
	protected static final Object[][] computeTestData() {
		Object[][] testData = new Object[][] {
				{"23", Integer.valueOf(300), Long.valueOf(15L)}, 
				{"23", Integer.valueOf(1000), Long.valueOf(35L)}, 
				{"23", Integer.valueOf(1400), Long.valueOf(50L)}, 	// 50
				{"23", Integer.valueOf(2000), Long.valueOf(75L)}, 
				{"23", Integer.valueOf(2400), Long.valueOf(100L)}, 	// 100
				{"23", Integer.valueOf(2600), Long.valueOf(130L)}, 	// 性能恶化明显
				{"23", Integer.valueOf(3000), Long.valueOf(210L)}, 
				{"23", Integer.valueOf(3500), Long.valueOf(335L)}, 
				{"23", Integer.valueOf(3800), Long.valueOf(420L)}, 
				{"23", Integer.valueOf(3900), Long.valueOf(450L)}, 
//				{"23", Integer.valueOf(3950), Long.valueOf(1000L)}, 
				
				{"10", Integer.valueOf(1000), Long.valueOf(35L)}, 
				{"7", Integer.valueOf(1000), Long.valueOf(35L)}, 
				{"3", Integer.valueOf(1000), Long.valueOf(35L)}, 
				{"10", Integer.valueOf(2400), Long.valueOf(110L)}, 
				{"7", Integer.valueOf(2400), Long.valueOf(110L)}, 
				{"3", Integer.valueOf(2400), Long.valueOf(120L)}, 
		};
		return testData;
	}

}

