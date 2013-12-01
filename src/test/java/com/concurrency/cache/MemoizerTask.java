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

import java.math.BigInteger;

/**
 * 本地Cache并发性能测试辅助任务。
 *
 * @author	lihg
 * @version 2013-12-1
 */
public class MemoizerTask implements Runnable {

	private final Memoizer<String, BigInteger> memoizer;
	private final String key;
	
	public MemoizerTask(String key) {
		Computable<String, BigInteger> computable = new ExpensiveFunction();
		this.memoizer = new Memoizer<>(computable);
		
		this.key = key;
	}

	@Override
	public void run() {
		try {
			memoizer.compute(key);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public int cacheSize() {
		return memoizer.cacheSize();
	}

}
