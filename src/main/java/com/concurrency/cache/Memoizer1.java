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
package com.concurrency.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用 {@link HashMap} 和同步机制来初始化缓存。
 * 
 * <pre>
 * 同步方法实现，确保线程安全性。
 *    因为 HashMap 不是线程安全的，因此要确保两个线程不会同时访问 HashMap。
 * 不足
 *    可伸缩性问题：每次只有一个线程能够执行 compute。
 * </pre>
 *
 * @author	lihg
 * @version 2013-11-30
 */
public class Memoizer1<A, V> implements Computable<A, V> {

	private final Map<A, V> cache = new HashMap<>(); // 保存之前计算的结果
	private final Computable<A, V> c;
	
	public Memoizer1(Computable<A, V> c) {
		this.c = c;
	}

	/*
	 * 首先检查需要的结果是否已经在缓存中，如果存在则返回之前计算的值；
	 * 否则，将把计算结果缓存在 HashMap 中，然后再返回。
	 */
	@Override
	public synchronized V compute(A arg) throws InterruptedException {
		V result = cache.get(arg);
		if (result == null) {
			result = c.compute(arg);
			cache.put(arg, result);
		}
		return result;
	}

}
