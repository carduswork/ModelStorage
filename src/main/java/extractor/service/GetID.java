package extractor.service;

import org.apache.commons.lang3.RandomStringUtils;

public class GetID {
	public static long getId() {
		String random = RandomStringUtils.random(10, false, true);
		return Long.parseLong(random);
	}
}
