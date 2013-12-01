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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用 {@link ConcurrentHashMap} 替换 {@code HashMap} 来改进 Memoizer1 中糟糕的并发行为。
 * 
 * <pre>
 * 由于 ConcurrentHashMap 是线程安全的，因此在访问底层 Map 时就不需要进行同步，
 * 因而避免了同步带来的串行性。
 * 
 * 不足
 *    当两个线程同时调用 compute 时存在一个漏洞，可能会导致计算得到相同的值。
 *    对于只提供单次初始化的对象缓存来说，这个漏洞就会带来安全风险。
 * 问题在于
 *    如果某个线程启动了一个开销很大的计算，而其他线程并不知道这个计算正在进行，
 *    那么很可能会重复这个计算。
 * </pre>
 *
 * @author	lihg
 * @version 2013-11-30
 */
public class Memoizer2<A, V> implements Computable<A, V> {

	private final Map<A, V> cache = new ConcurrentHashMap<>();
	private final Computable<A, V> c;
	
	public Memoizer2(Computable<A, V> c) {
		this.c = c;
	}

	/*
	 * 缓存的作用
	 *    避免相同的数据被计算多次。
	 */
	@Override
	public V compute(A arg) throws InterruptedException {
		V result = cache.get(arg);
		if (result == null) { // 首先判断某个计算是否已经完成
			result = c.compute(arg);
			cache.put(arg, result);
		}
		return result;
	}

}
