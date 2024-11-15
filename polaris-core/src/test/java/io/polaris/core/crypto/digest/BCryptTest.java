package io.polaris.core.crypto.digest;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptTest {

	static String test_vectors[][] = {
		{"",
			"$2a$06$DCq7YPn5Rq63x1Lad4cll.",
			"$2a$06$DCq7YPn5Rq63x1Lad4cll.TV4S6ytwfsfvkgY8jIucDrjc8deX1s."},
		{"",
			"$2a$08$HqWuK6/Ng6sg9gQzbLrgb.",
			"$2a$08$HqWuK6/Ng6sg9gQzbLrgb.Tl.ZHfXLhvt/SgVyWhQqgqcZ7ZuUtye"},
		{"",
			"$2a$10$k1wbIrmNyFAPwPVPSVa/ze",
			"$2a$10$k1wbIrmNyFAPwPVPSVa/zecw2BCEnBwVS2GbrmgzxFUOqW9dk4TCW"},
		{"",
			"$2a$12$k42ZFHFWqBp3vWli.nIn8u",
			"$2a$12$k42ZFHFWqBp3vWli.nIn8uYyIkbvYRvodzbfbK18SSsY.CsIQPlxO"},
		{"a",
			"$2a$06$m0CrhHm10qJ3lXRY.5zDGO",
			"$2a$06$m0CrhHm10qJ3lXRY.5zDGO3rS2KdeeWLuGmsfGlMfOxih58VYVfxe"},
		{"a",
			"$2a$08$cfcvVd2aQ8CMvoMpP2EBfe",
			"$2a$08$cfcvVd2aQ8CMvoMpP2EBfeodLEkkFJ9umNEfPD18.hUF62qqlC/V."},
		{"a",
			"$2a$10$k87L/MF28Q673VKh8/cPi.",
			"$2a$10$k87L/MF28Q673VKh8/cPi.SUl7MU/rWuSiIDDFayrKk/1tBsSQu4u"},
		{"a",
			"$2a$12$8NJH3LsPrANStV6XtBakCe",
			"$2a$12$8NJH3LsPrANStV6XtBakCez0cKHXVxmvxIlcz785vxAIZrihHZpeS"},
		{"abc",
			"$2a$06$If6bvum7DFjUnE9p2uDeDu",
			"$2a$06$If6bvum7DFjUnE9p2uDeDu0YHzrHM6tf.iqN8.yx.jNN1ILEf7h0i"},
		{"abc",
			"$2a$08$Ro0CUfOqk6cXEKf3dyaM7O",
			"$2a$08$Ro0CUfOqk6cXEKf3dyaM7OhSCvnwM9s4wIX9JeLapehKK5YdLxKcm"},
		{"abc",
			"$2a$10$WvvTPHKwdBJ3uk0Z37EMR.",
			"$2a$10$WvvTPHKwdBJ3uk0Z37EMR.hLA2W6N9AEBhEgrAOljy2Ae5MtaSIUi"},
		{"abc",
			"$2a$12$EXRkfkdmXn2gzds2SSitu.",
			"$2a$12$EXRkfkdmXn2gzds2SSitu.MW9.gAVqa9eLS1//RYtYCmB1eLHg.9q"},
		{"abcdefghijklmnopqrstuvwxyz",
			"$2a$06$.rCVZVOThsIa97pEDOxvGu",
			"$2a$06$.rCVZVOThsIa97pEDOxvGuRRgzG64bvtJ0938xuqzv18d3ZpQhstC"},
		{"abcdefghijklmnopqrstuvwxyz",
			"$2a$08$aTsUwsyowQuzRrDqFflhge",
			"$2a$08$aTsUwsyowQuzRrDqFflhgekJ8d9/7Z3GV3UcgvzQW3J5zMyrTvlz."},
		{"abcdefghijklmnopqrstuvwxyz",
			"$2a$10$fVH8e28OQRj9tqiDXs1e1u",
			"$2a$10$fVH8e28OQRj9tqiDXs1e1uxpsjN0c7II7YPKXua2NAKYvM6iQk7dq"},
		{"abcdefghijklmnopqrstuvwxyz",
			"$2a$12$D4G5f18o7aMMfwasBL7Gpu",
			"$2a$12$D4G5f18o7aMMfwasBL7GpuQWuP3pkrZrOAnqP.bmezbMng.QwJ/pG"},
		{"~!@#$%^&*()      ~!@#$%^&*()PNBFRD",
			"$2a$06$fPIsBO8qRqkjj273rfaOI.",
			"$2a$06$fPIsBO8qRqkjj273rfaOI.HtSV9jLDpTbZn782DC6/t7qT67P6FfO"},
		{"~!@#$%^&*()      ~!@#$%^&*()PNBFRD",
			"$2a$08$Eq2r4G/76Wv39MzSX262hu",
			"$2a$08$Eq2r4G/76Wv39MzSX262huzPz612MZiYHVUJe/OcOql2jo4.9UxTW"},
		{"~!@#$%^&*()      ~!@#$%^&*()PNBFRD",
			"$2a$10$LgfYWkbzEvQ4JakH7rOvHe",
			"$2a$10$LgfYWkbzEvQ4JakH7rOvHe0y8pHKF9OaFgwUZ2q7W2FFZmZzJYlfS"},
		{"~!@#$%^&*()      ~!@#$%^&*()PNBFRD",
			"$2a$12$WApznUOJfkEGSmYRfnkrPO",
			"$2a$12$WApznUOJfkEGSmYRfnkrPOr466oFDCaj4b6HY3EXGvfxm43seyhgC"},
	};

	@Test
	@DisplayName("BCrypt.hashpw(String, String)")
	public void testHashpw() {
		Consoles.println("BCrypt.hashpw(): ");
		for (int i = 0; i < test_vectors.length; i++) {
			String plain = test_vectors[i][0];
			String salt = test_vectors[i][1];
			String expected = test_vectors[i][2];
			String hashed = BCrypt.hashpw(plain, salt);
			Consoles.println("> plain:`{}`, salt:`{}`, expected:`{}`, hashed:`{}`", plain, salt, expected, hashed);
			assertEquals(hashed, expected);
		}
		Consoles.println("");
	}

	@Test
	@DisplayName("BCrypt.gensalt(int)")
	public void testGensaltInt() {
		Consoles.println("BCrypt.gensalt(log_rounds):");
		for (int i = 4; i <= 12; i++) {
			for (int j = 0; j < test_vectors.length; j += 4) {
				String plain = test_vectors[j][0];
				String salt = BCrypt.gensalt(i);
				String hashed1 = BCrypt.hashpw(plain, salt);
				String hashed2 = BCrypt.hashpw(plain, hashed1);
				Consoles.println("> plain:`{}`, salt:`{}`, hashed1:`{}`, hashed2:`{}`", plain, salt, hashed1, hashed2);
				assertEquals(hashed1, hashed2);
			}
		}
		Consoles.println("");
	}

	@Test
	@DisplayName("BCrypt.gensalt()")
	public void testGensalt() {
		Consoles.println("BCrypt.gensalt(): ");
		for (int i = 0; i < test_vectors.length; i += 4) {
			String plain = test_vectors[i][0];
			String salt = BCrypt.gensalt();
			String hashed1 = BCrypt.hashpw(plain, salt);
			String hashed2 = BCrypt.hashpw(plain, hashed1);
			Consoles.println("> plain:`{}`, salt:`{}`, hashed1:`{}`, hashed2:`{}`", plain, salt, hashed1, hashed2);
			assertEquals(hashed1, hashed2);
		}
		Consoles.println("");
	}

	@Test
	@DisplayName("BCrypt.checkpw(String, String)")
	public void testCheckpw_success() {
		Consoles.println("BCrypt.checkpw w/ good passwords: ");
		for (int i = 0; i < test_vectors.length; i++) {
			String plain = test_vectors[i][0];
			String expected = test_vectors[i][2];
			boolean checked = BCrypt.checkpw(plain, expected);
			Consoles.println("> plain:`{}`, expected:`{}`, checked:`{}`", plain, expected, checked);
			assertTrue(checked);
		}
		Consoles.println("");
	}

	@Test
	@DisplayName("BCrypt.checkpw(String, String)")
	public void testCheckpw_failure() {
		Consoles.print("BCrypt.checkpw w/ bad passwords: ");
		for (int i = 0; i < test_vectors.length; i++) {
			int broken_index = (i + 4) % test_vectors.length;
			String plain = test_vectors[i][0];
			String expected = test_vectors[broken_index][2];
			assertFalse(BCrypt.checkpw(plain, expected));
			Consoles.print(".");
		}
		Consoles.println("");
	}

	@Test
	@DisplayName("correct hashing of non-US-ASCII passwords")
	public void testInternationalChars() {
		Consoles.print("BCrypt.hashpw w/ international chars: ");
		String pw1 = "\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605";
		String pw2 = "????????";

		String h1 = BCrypt.hashpw(pw1, BCrypt.gensalt());
		assertFalse(BCrypt.checkpw(pw2, h1));
		Consoles.print(".");

		String h2 = BCrypt.hashpw(pw2, BCrypt.gensalt());
		assertFalse(BCrypt.checkpw(pw1, h2));
		Consoles.print(".");
		Consoles.println("");
	}

	@Test
	void test01() {
		String plain = "Abc@12345678";
		String hash0 = "$2a$10$Oi8tgz6XtGNLsrKIY8yVWecHIRrIS1AaJmWIgtIR8e.8buWG6Vx6q";
		String salt = BCrypt.gensalt();
		String hash = BCrypt.hashpw(plain, salt);

		Consoles.println("salt: {}", salt);
		Consoles.println("hash: {}", hash);
		Object[] args1 = new Object[]{BCrypt.checkpw(plain, hash0)};
		Consoles.println("checked: {}", args1);
		Object[] args = new Object[]{BCrypt.checkpw(plain, hash)};
		Consoles.println("checked: {}", args);
		Assertions.assertTrue(BCrypt.checkpw(plain, hash0));
		Assertions.assertTrue(BCrypt.checkpw(plain, hash));
	}
}
