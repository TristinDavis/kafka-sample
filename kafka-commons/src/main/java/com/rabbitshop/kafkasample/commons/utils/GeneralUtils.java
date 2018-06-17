package com.rabbitshop.kafkasample.commons.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class GeneralUtils {

	static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	private GeneralUtils() {
		// no-op
	}

	public static String bytesToHexString(final byte[] bytes) {

		log.debug("Converting byte to hexadecimal string...");

		final char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			final int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

}
