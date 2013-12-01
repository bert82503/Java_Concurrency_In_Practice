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
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 基于 {@link FutureTask} 实现。
 * 
 * <p>FutureTask 表示一个计算的过程，这个过程可能已经计算完成，也可能正在进行。
 * 如果有结果可用，那么 {@link FutureTask#get()} 将立即返回结果，
 * 否则它会一直阻塞，直到结果计算出来再将其返回。
 * 
 * <pre>
 * 该实现几乎是完美的：
 *    它表现出了非常好的并发性（基本上是源于 ConcurrentHashMap 高效的并发性）。
 * 
 * 一个缺陷
 *    仍然存在两个线程计算出相同值的漏洞。（由于if代码块仍然是非原子的“先检查再执行”操作）
 *    即两个线程都没有在缓存中找到期望的值。
 * 问题根源
 *    复合操作（“若没有则添加”）是在底层的 Map 对象上执行的，而这个对象无法通过加锁来确保原子性。
 * </pre>
 *
 * @author	lihg
 * @version 2013-11-30
 */
public class Memoizer3<A, V> implements Computable<A, V> {

	private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
	private final Computable<A, V> c;
	
	public Memoizer3(Computable<A, V> c) {
		this.c = c;
	}

	/*
	 * 首先检查某个相应的计算是否已经开始。
	 * 如果还没有启动，那么就创建一个 FutureTask，并注册到 ConcurrentMap 中，然后启动计算；
	 * 如果已经启动，那么等待现有计算的结果。
	 */
	@Override
	public V compute(final A arg) throws InterruptedException {
		Future<V> f = cache.get(arg);
		if (f == null) { // 首先检查某个计算是否已经开始
			Callable<V> eval = new Callable<V>() {
				@Override
				public V call() throws Exception {
					return c.compute(arg);
				}
			};
			
			FutureTask<V> ft = new FutureTask<>(eval);
			f = ft;
			cache.put(arg, ft); // 漏洞！
			ft.run(); // 在这里将调用 c.compute
		}
		try {
			return f.get(); // 等待计算结果
		} catch (ExecutionException e) {
			cache.remove(arg);
			e.printStackTrace();
			throw new InterruptedException(e.getMessage());
		}
	}

}
