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

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 基于 {@link ConcurrentMap} 和 {@link FutureTask} 的终结实现。
 * 
 * <p>FutureTask 表示一个计算的过程，这个过程可能已经计算完成，也可能正在进行。
 * 如果有结果可用，那么 {@link FutureTask#get()} 将立即返回结果，
 * 否则它会一直阻塞，直到结果计算出来再将其返回。
 * 
 * <p>使用 ConcurrentMap 中的原子方法 putIfAbsent，避免了 Memoizer3 中的漏洞。
 * 
 * <pre>
 * 缓存污染问题
 *    如果某个计算被取消或失败，那么在计算这个结果时将指明计算过程被取消或者失败。
 * 规避措施
 *    如果发现计算被取消，那么将把 Future 从缓存中移除。
 *    如果检测到 RuntimeException，那么也会移除 Future，这样将来的计算才会成功。
 * 
 * 缓存过期问题
 *    可以通过 FutureTask 的子类来解决，在子类中为每个结果指定一个过期时间，并定期扫描缓存中过期的元素。
 *    （同样，它也没有解决缓存清理的问题，即移除旧的计算结果以便为新的计算结果腾出空间，
 *    从而使缓存不会消耗过多的内存。）
 * </pre>
 *
 * @author	lihg
 * @version 2013-12-1
 */
public class Memoizer<A, V> implements Computable<A, V> {

	private final ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<>();
	private final Computable<A, V> c;
	
	public Memoizer(Computable<A, V> c) {
		this.c = c;
	}

	/*
	 * 首先检查某个相应的计算是否已经开始。
	 * 如果还没有启动，那么就创建一个 FutureTask，并注册到 ConcurrentMap 中，然后启动计算；
	 * 如果已经启动，那么等待现有计算的结果。
	 */
	@Override
	public V compute(final A arg) throws InterruptedException {
		while (true) { // 重试机制：无限次！
			Future<V> f = cache.get(arg);
			if (f == null) { // 首先检查某个计算是否已经开始
				Callable<V> eval = new Callable<V>() {
					@Override
					public V call() throws Exception {
						return c.compute(arg);
					}
				};
				
				FutureTask<V> ft = new FutureTask<>(eval);
				f = cache.putIfAbsent(arg, ft); // 先检查计算是否已经开始，避免漏洞！
				if (f == null) {
					f = ft;
					ft.run(); // 在这里将调用 c.compute
				}
			}
			
			try {
				return f.get(); // 等待计算结果
			} catch (CancellationException ce) {
				cache.remove(arg, f);
				// 任务被取消执行后，会重试
			} catch (ExecutionException e) {
				cache.remove(arg, f);
				e.printStackTrace();
				throw new InterruptedException(e.getMessage());
			}
		}
	}
	
//	@Test
	public int cacheSize() {
		return cache.size();
	}

}
