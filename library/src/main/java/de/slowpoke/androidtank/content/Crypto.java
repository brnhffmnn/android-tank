/**
 *  Copyright 2014 Brian Hoffmann, slowpoke.de
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package de.slowpoke.androidtank.content;

import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Provider.Service;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.annotation.SuppressLint;
import android.util.Base64;
import android.util.Log;

/**
 * Collection of cryptography en-/decryption utilities.
 *
 * @see http://android-developers.blogspot.de/2013/02/using-cryptography-to-store-credentials.html
 * @see http://nelenkov.blogspot.de/2012/04/using-password-based-encryption-on.html
 * @see https://github.com/nelenkov/android-pbe
 */
public class Crypto {

	static {
		PRNGFixes.apply();
	}

	private static final String TAG = Crypto.class.getSimpleName();

	private static final String PKCS12_DERIVATION_ALGORITHM = "PBEWITHSHA256AND256BITAES-CBC-BC";
	private static final String PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";

	private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

	private static final String KEYSPEC_AES = "AES";
	private static final String BASE64_DELIMITER = ";";
	
	private static final byte[] EMPTY_BYTE = new byte[0];

	@SuppressLint("TrulyRandom")
	private static final SecureRandom random = new SecureRandom();

	/**
	 * Lists cryptographic algorithms that are available on the device in logcat.
	 * 
	 * @param algFilter
	 *            Name of a cryptographic algorithm to filter, e.g. "PBKDF2WithHmacSHA1"
	 */
	public static void listAlgorithms(final String algFilter) {
		Provider[] providers = Security.getProviders();
		for (Provider p : providers) {
			String providerStr = String.format(Locale.ENGLISH, "%s/%s/%f\n", p.getName(), p.getInfo(), p.getVersion());
			Log.d(TAG, providerStr);
			Set<Service> services = p.getServices();
			List<String> algs = new ArrayList<String>();
			for (Service s : services) {
				boolean match = true;
				if (algFilter != null) {
					match = s.getAlgorithm().toLowerCase(Locale.ENGLISH)
							.contains(algFilter.toLowerCase(Locale.ENGLISH));
				}

				if (match) {
					String algStr = String.format("\t%s/%s/%s", s.getType(), s.getAlgorithm(), s.getClassName());
					algs.add(algStr);
				}
			}

			Collections.sort(algs);
			for (String alg : algs) {
				Log.d(TAG, "\t" + alg);
			}
			Log.d(TAG, "");
		}
	}

	/**
	 * Generates a random salt of length <code>saltLenght</code>
	 * 
	 * @param saltLength
	 *            Number of bytes for salt
	 * @return
	 */
	public static byte[] generateSalt(int saltLength) {
		byte[] b = new byte[saltLength];
		random.nextBytes(b);

		return b;
	}

	/**
	 * Returns a 'somewhat cryptographically secure' pseudo-random integer in the range of <code>[a, b)</code>.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int generateInt(int a, int b) {
		return random.nextInt((b - a)) + a;
	}

	/**
	 * Returns a 'somewhat cryptographically secure' pseudo-random {@link UUID}.
	 * 
	 * @return
	 */
	public static UUID generateUUID() {
		return new UUID(random.nextLong(), random.nextLong());
	}

	/** An encrypted Message that can be persisted. */
	public static final class Message {

		public byte[] payload;
		public byte[] salt;
		public byte[] iv;
		public int iterationCount;

		/**
		 * Returns the String representation of this Mesage.
		 * 
		 * @return
		 */
		@Override
		public String toString() {

			StringBuilder builder = new StringBuilder();

			if (this.payload != null && this.payload.length > 0) {
				builder.append(Base64.encodeToString(this.payload, Base64.NO_WRAP));
			}
			builder.append(BASE64_DELIMITER);
			if (this.salt != null && this.salt.length > 0) {
				builder.append(Base64.encodeToString(this.salt, Base64.NO_WRAP));
			}
			builder.append(BASE64_DELIMITER);
			if (this.iv != null && this.iv.length > 0) {
				builder.append(Base64.encodeToString(this.iv, Base64.NO_WRAP));
			}
			builder.append(BASE64_DELIMITER);

			return builder.append(this.iterationCount).toString();
		}

		/**
		 * Creates a new instance of Message from a String representation.
		 * 
		 * @param encoded
		 *            Encoded Message, previously generated by {@link Message#toString()}
		 * @return
		 */
		public static Message fromString(String encoded) {
			final String[] fields = encoded.split(BASE64_DELIMITER);

			final Message msg = new Message();
			msg.payload = (fields[0] != null) ? Base64.decode(fields[0], Base64.NO_WRAP) : EMPTY_BYTE;
			msg.salt = (fields[1] != null) ? Base64.decode(fields[1], Base64.NO_WRAP) : EMPTY_BYTE;
			msg.iv = (fields[2] != null) ? Base64.decode(fields[2], Base64.NO_WRAP) : EMPTY_BYTE;
			msg.iterationCount = Integer.valueOf(fields[3]);

			return msg;
		}
	}

	/**
	 * PKCS12 encryption, decryption, and key derivation.
	 *
	 * @link http://android-developers.blogspot.de/2013/12/changes-to-secretkeyfactory-api-in.html
	 */
	@Deprecated
	public static class Pkcs12 {

		public static final SecretKey deriveKey(final char[] passphrase, final int keyLength, final byte[] salt,
				final int iterationCount) throws GeneralSecurityException {
			long start = System.currentTimeMillis();

			KeySpec keySpec = new PBEKeySpec(passphrase, salt, iterationCount, keyLength);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PKCS12_DERIVATION_ALGORITHM);
			SecretKey result = keyFactory.generateSecret(keySpec);

			long elapsed = System.currentTimeMillis() - start;
			Log.d(TAG, String.format("PKCS#12 key derivation took %d [ms].", elapsed));

			return result;
		}

		public static final  Message encrypt(final byte[] data, final SecretKey key, final byte[] salt,
				final int iterationCount) throws GeneralSecurityException {

			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

			PBEParameterSpec pbeSpec = new PBEParameterSpec(salt, iterationCount);
			cipher.init(Cipher.ENCRYPT_MODE, key, pbeSpec);

			byte[] cipherText = cipher.doFinal(data);

			Message msg = new Message();
			msg.payload = cipherText;
			msg.iterationCount = iterationCount;
			msg.salt = salt;

			return msg;
		}

		public static final byte[] decrypt(final Message msg, final SecretKey key) throws GeneralSecurityException {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

			PBEParameterSpec pbeSpec = new PBEParameterSpec(msg.salt, msg.iterationCount);
			cipher.init(Cipher.DECRYPT_MODE, key, pbeSpec);

			return cipher.doFinal(msg.payload);
		}
	}

	/**
	 * PKBDF2 encryption, decryption, and key derivation.
	 */
	public static final class Pkbdf2 {

		public static final SecretKey deriveKey(final char[] passphrase, final int keyLength, final byte[] salt,
				final int iterationCount) throws GeneralSecurityException {
			long start = System.currentTimeMillis();

			KeySpec keySpec = new PBEKeySpec(passphrase, salt, iterationCount, keyLength);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM);
			
			byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
			SecretKey result = new SecretKeySpec(keyBytes, KEYSPEC_AES);

			long elapsed = System.currentTimeMillis() - start;
			Log.d(TAG, String.format("PBKDF2 key derivation took %d [ms].", elapsed));

			return result;
		}

		public static final Message encrypt(final byte[] data, final SecretKey key) throws GeneralSecurityException {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

			byte[] iv = generateSalt(cipher.getBlockSize());
			IvParameterSpec ivParams = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);

			byte[] cipherText = cipher.doFinal(data);

			Message msg = new Message();
			msg.payload = cipherText;
			msg.iv = iv;

			return msg;
		}

		public static final byte[] decrypt(final Message msg, final SecretKey key) throws GeneralSecurityException {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

			IvParameterSpec ivParams = new IvParameterSpec(msg.iv);
			cipher.init(Cipher.DECRYPT_MODE, key, ivParams);

			return cipher.doFinal(msg.payload);
		}
	}
}
