package io.polaris.core.lang.annotation;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import io.polaris.core.io.Consoles;
import io.polaris.core.lang.annotation.data.Anno1;
import io.polaris.core.lang.annotation.data.Anno1x1;
import org.junit.jupiter.api.Test;

class HierarchyMergedAnnotationTest {

	@Anno1x1(key = "I")
	public static interface I {
	}

	@Test
	void testMergeAnnotation() {
		@Anno1x1
		class A {
		}

		HierarchyMergedAnnotation mergedAnnotations = new HierarchyMergedAnnotation(A.class);
		HierarchyMergedAnnotationTest.display(mergedAnnotations, new PrintWriter(System.out));
	}

	@Test
	void testMergeClass() {
		@Anno1x1(key = "A")
		class A implements I {
		}
		@Anno1x1(key = "B")
		class B extends A implements I {
		}

		HierarchyMergedAnnotation mergedAnnotations = new HierarchyMergedAnnotation(B.class);
		Consoles.println(mergedAnnotations);
		List<Set<MergedAnnotation>> sortedAnnotations = mergedAnnotations.getSortedAnnotations();
		for (int k = 0; k < sortedAnnotations.size(); k++) {
			Set<MergedAnnotation> v = sortedAnnotations.get(k);
			Consoles.println(">>" + k + ">>");
			for (MergedAnnotation mergedAnnotation : v) {
				Consoles.println(mergedAnnotation);
			}
		}
		Consoles.println(mergedAnnotations.getMergedAnnotation(Anno1.class));
//		Consoles.println(mergedAnnotations.getMergedAnnotation(Anno1x1.class));
//		Consoles.println(mergedAnnotations.getMergedAnnotation(RepeatableAnno1.class));
	}


	static void display(HierarchyMergedAnnotation mergedAnnotations, PrintWriter pw) {
		List<Set<MergedAnnotation>> sortedAnnotations = mergedAnnotations.getSortedAnnotations();
		for (int key = 0; key < sortedAnnotations.size(); key++) {
			Set<MergedAnnotation> set = sortedAnnotations.get(key);
			for (int i = 0; i < key; i++) {
				pw.print("  ");
			}
			pw.println(key + ">>");
			for (MergedAnnotation mergedAnnotation : set) {
				for (int i = 0; i < key; i++) {
					pw.print("  ");
				}
				pw.print("[" + mergedAnnotation.hashCode() + "]  " + mergedAnnotation);
				pw.println();

				List<Set<MergedAnnotation>> hierarchyAnnotations = mergedAnnotation.getHierarchyAnnotations();
				for (int hierarchyEntryKey = 0; hierarchyEntryKey < hierarchyAnnotations.size(); hierarchyEntryKey++) {
					Set<MergedAnnotation> hierarchyEntryValue = hierarchyAnnotations.get(hierarchyEntryKey);
					for (int i = 0; i < key + hierarchyEntryKey; i++) {
						pw.print("  ");
					}
					pw.println(key + "." + hierarchyEntryKey + ">>");
					for (MergedAnnotation annotation : hierarchyEntryValue) {
						for (int i = 0; i < key + hierarchyEntryKey; i++) {
							pw.print("  ");
						}
						pw.print("[" + annotation.hashCode() + "]  " + annotation);
						pw.println();
					}
				}
			}
		}
		pw.flush();
	}

}
