package in.skeh.gaeutils;

import java.io.Serializable;
import java.util.Collections;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

public class CacheUtils {
	static Cache cache = null;

	private static Cache getCache() {
		if (cache == null) {
			try {
				CacheFactory cacheFactory = CacheManager.getInstance()
						.getCacheFactory();
				cache = cacheFactory.createCache(Collections.emptyMap());
			} catch (CacheException e) {
				throw new RuntimeException("Couldn't get cache");
			}
		}
		return cache;
	}
	
	public static Object get(Object key) {
		try {
			return getCache().get(key);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void put(Object key, Object value) {
		try {
			getCache().put(key, value);
		} catch (Exception e) {
			return;
		}
	}
	
	public static boolean exists(Object key) {
		return getCache().containsKey(key);
	}
	
	public static void clear(Object key) {
		getCache().remove(key);
	}
}
