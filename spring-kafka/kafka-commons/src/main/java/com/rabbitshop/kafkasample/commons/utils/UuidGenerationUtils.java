package com.rabbitshop.kafkasample.commons.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class UuidGenerationUtils {

	/*
	 * PLEASE NOTE: predefined UUID for namespaces
	 */
	// static String DEFAULT_NAMESPACE_DNS = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";
	// static String DEFAULT_NAMESPACE_URL = "6ba7b811-9dad-11d1-80b4-00c04fd430c8";
	// static String DEFAULT_NAMESPACE_OID = "6ba7b812-9dad-11d1-80b4-00c04fd430c8";
	// static String DEFAULT_NAMESPACE_X500 = "6ba7b814-9dad-11d1-80b4-00c04fd430c8";

	private UuidGenerationUtils() {
		// no-op
	}

	/**
	 * Unique key generation using message digest (e.g. SHA-256), charset (e.g. UTF-8) and Type 4 UUID
	 *
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String generateUuid(final String messageDigest, final String charsetName) throws NoSuchAlgorithmException, UnsupportedEncodingException {

		log.debug("Generating Type 4 UUID using message digest {} and charset {}...", messageDigest, charsetName);

		final MessageDigest salt = MessageDigest.getInstance(messageDigest);
		salt.update(
				UUID.randomUUID()
						.toString()
						.getBytes(charsetName)
		);
		return GeneralUtils.bytesToHexString(salt.digest());
	}

	/**
	 * Type 4 UUID generation
	 */
	public static UUID generateUuidType4() {

		log.debug("Generate Type 4 UUID...");

		return UUID.randomUUID();
	}

	/**
	 * Type 3 UUID generation
	 *
	 * @throws UnsupportedEncodingException
	 */
	public static UUID generateUuidType3(final String namespace, final String name) throws UnsupportedEncodingException {

		log.debug("Generate Type 3 UUID...");

		final String source = namespace + name;
		final byte[] bytes = source.getBytes("UTF-8");
		return UUID.nameUUIDFromBytes(bytes);
	}

	/**
	 * Type 5 UUID generation
	 *
	 * @throws UnsupportedEncodingException
	 */
	public static UUID generateUuidType5(final String namespace, final String name) throws UnsupportedEncodingException {

		log.debug("Generate Type 5 UUID...");

		final String source = namespace + name;
		final byte[] bytes = source.getBytes("UTF-8");
		return type5UUIDFromBytes(bytes);
	}

	private static UUID type5UUIDFromBytes(final byte[] name) {

		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (final NoSuchAlgorithmException nsae) {
			throw new InternalError("MD5 not supported", nsae);
		}
		final byte[] bytes = md.digest(name);
		bytes[6] &= 0x0f; /* clear version        */
		bytes[6] |= 0x50; /* set to version 5     */
		bytes[8] &= 0x3f; /* clear variant        */
		bytes[8] |= 0x80; /* set to IETF variant  */
		return constructType5UUID(bytes);
	}

	private static UUID constructType5UUID(final byte[] data) {

		long msb = 0;
		long lsb = 0;
		assert data.length == 16 : "data must be 16 bytes in length";
		for (int i = 0; i < 8; i++) {
			msb = (msb << 8) | (data[i] & 0xff);
		}
		for (int i = 8; i < 16; i++) {
			lsb = (lsb << 8) | (data[i] & 0xff);
		}
		return new UUID(msb, lsb);
	}

}