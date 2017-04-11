package util;
import java.util.HashSet;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public class GeneratorUtil {

	public HashSet<String> getRandomlyString(final int number, final int startInclusive, final int endExclusive) {
		HashSet<String> randomStrSet = new HashSet<String>();
		for (int i = 0; i < number; i++) {
			randomStrSet.add(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(startInclusive, endExclusive)));
		}
		return randomStrSet;
	}
}
