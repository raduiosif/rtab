package generic;

import java.util.List;

public class Entity {
	
	public Entity(String name) { m_name = name; }
	
	public String name() { return m_name; }
	public String classname() { return "entity"; }
	public boolean check() { return true; }
	
	private String m_name;

	// error handling
	
	public void information(String message) {
		java.lang.System.out.println("# " + classname() + 
			(m_name != null ? " " + m_name : "") + " : " + message);
	} 
	public void error(String message) {
		java.lang.System.out.println("! " + classname() + 
				(m_name != null ? " " + m_name : "") + " : " + message);
	}
	public void error(String message, boolean abort) {
		error(message);
		if (abort) {
			java.lang.System.out.println("! abort.");
			java.lang.System.exit(-1);
		}
	} 
		
	// static functions
	
	public static <T> T find(List<T> list, String name) {
		for(T elem : list) 
			if ( ((Entity)elem).name().equals(name))
				return elem;
		return null;
	}

	public static <T> T findOrAdd(List<T> list, T elem) {
		T list_elem = find(list, ((Entity) elem).name());
		if (list_elem == null) {
			list.add(elem);
			list_elem = elem;
		}
		return list_elem;
	}
	
	public static <T> boolean checkAll(List<T> list) {
		boolean result = true;
		for(T elem : list)
			result &= ((Entity)elem).check();
		return result;
	}
	
	public static <T> boolean checkUniqueNames(List<T> list) {
		boolean result = true;
		for(T elem1 : list)
			for(T elem2 : list)
				if (elem1 != elem2)
					result &= !((Entity) elem1).name().equals(((Entity) elem2).name());
		return result;
	}
	
	public static <T> boolean checkDistinctNames(List<T> list1, List<T> list2) {
		boolean result = true;
		for(T elem1 : list1)
			for(T elem2 : list2)
				result &= !((Entity) elem1).name().equals(((Entity) elem2).name());
		return result;
	}

	
}
