package io.polaris.core.lang.annotation;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public class MergedAnnotationsTest {


	public static void display(MergedAnnotations mergedAnnotations, PrintWriter pw) {
		for (Map.Entry<Integer, Set<MergedAnnotation>> entry : mergedAnnotations.getSortedAnnotations().entrySet()) {
			Integer key = entry.getKey();
			Set<MergedAnnotation> set = entry.getValue();
			for (int i = 0; i < key; i++) {
				pw.print("  ");
			}
			pw.println(key + ">>");
			for (MergedAnnotation mergedAnnotation : set) {
				for (int i = 0; i < key; i++) {
					pw.print("  ");
				}
				pw.print("["+mergedAnnotation.hashCode() + "]  " + mergedAnnotation);
				pw.println();

				SortedMap<Integer, Set<MergedAnnotation>> hierarchyAnnotations = mergedAnnotation.getHierarchyAnnotations();
				for (Map.Entry<Integer, Set<MergedAnnotation>> hierarchyEntry : hierarchyAnnotations.entrySet()) {
					Integer hierarchyEntryKey = hierarchyEntry.getKey();
					Set<MergedAnnotation> hierarchyEntryValue = hierarchyEntry.getValue();
					for (int i = 0; i < key+hierarchyEntryKey; i++) {
						pw.print("  ");
					}
					pw.println(key+"."+hierarchyEntryKey + ">>");
					for (MergedAnnotation annotation : hierarchyEntryValue) {
						for (int i = 0; i < key+hierarchyEntryKey; i++) {
							pw.print("  ");
						}
						pw.print("["+annotation.hashCode() + "]  " + annotation);
						pw.println();
					}
				}
			}
		}
		pw.flush();
	}

}
