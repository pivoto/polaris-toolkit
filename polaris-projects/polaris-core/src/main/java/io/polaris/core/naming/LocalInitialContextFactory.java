package io.polaris.core.naming;

import javax.naming.*;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * System.setProperty(Context.INITIAL_CONTEXT_FACTORY, LocalInitialContextFactory.class.getName());
 *
 * @author Qt
 * @since 1.8
 */
public class LocalInitialContextFactory implements InitialContextFactory {

	private static final Map<String, Object> globalCache = new ConcurrentHashMap<>();

	public static void init() {
		System.setProperty(javax.naming.Context.INITIAL_CONTEXT_FACTORY, LocalInitialContextFactory.class.getName());
	}

	@Override
	public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
		Context context = new LocalInitialContext();
		return context;
	}

	static class LocalInitialContext implements Context {

		@Override
		public Object lookup(Name name) throws NamingException {
			return globalCache.get(name.toString());
		}

		@Override
		public Object lookup(String name) throws NamingException {
			return globalCache.get(name);
		}

		@Override
		public void bind(Name name, Object obj) throws NamingException {
			globalCache.put(name.toString(), obj);
		}

		@Override
		public void bind(String name, Object obj) throws NamingException {
			globalCache.put(name, obj);
		}

		@Override
		public void rebind(Name name, Object obj) throws NamingException {
			globalCache.put(name.toString(), obj);
		}

		@Override
		public void rebind(String name, Object obj) throws NamingException {
			globalCache.put(name.toString(), obj);
		}

		@Override
		public void unbind(Name name) throws NamingException {
			globalCache.remove(name.toString());
		}

		@Override
		public void unbind(String name) throws NamingException {
			globalCache.remove(name.toString());
		}

		@Override
		public void rename(Name oldName, Name newName) throws NamingException {

		}

		@Override
		public void rename(String oldName, String newName) throws NamingException {

		}

		@Override
		public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
			return null;
		}

		@Override
		public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
			return null;
		}

		@Override
		public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
			return null;
		}

		@Override
		public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
			return null;
		}

		@Override
		public void destroySubcontext(Name name) throws NamingException {

		}

		@Override
		public void destroySubcontext(String name) throws NamingException {

		}

		@Override
		public Context createSubcontext(Name name) throws NamingException {
			return null;
		}

		@Override
		public Context createSubcontext(String name) throws NamingException {
			return null;
		}

		@Override
		public Object lookupLink(Name name) throws NamingException {
			return null;
		}

		@Override
		public Object lookupLink(String name) throws NamingException {
			return null;
		}

		@Override
		public NameParser getNameParser(Name name) throws NamingException {
			return null;
		}

		@Override
		public NameParser getNameParser(String name) throws NamingException {
			return null;
		}

		@Override
		public Name composeName(Name name, Name prefix) throws NamingException {
			return null;
		}

		@Override
		public String composeName(String name, String prefix) throws NamingException {
			return null;
		}

		@Override
		public Object addToEnvironment(String propName, Object propVal) throws NamingException {
			return null;
		}

		@Override
		public Object removeFromEnvironment(String propName) throws NamingException {
			return null;
		}

		@Override
		public Hashtable<?, ?> getEnvironment() throws NamingException {
			return null;
		}

		@Override
		public void close() throws NamingException {

		}

		@Override
		public String getNameInNamespace() throws NamingException {
			return null;
		}
	}
}
