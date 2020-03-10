package af.gov.anar.lib.cryto;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;

import static java.util.Arrays.copyOfRange;

/**
 * Crypto Util for common methods in various module
 */
public class CryptoUtility {

	/**
	 * Private Constructor for this class
	 */
	private CryptoUtility() {

	}

	/**
	 * Combine data,key and key splitter
	 * 
	 * @param data        encrypted Data
	 * @param key         encrypted Key
	 * @param keySplitter keySplitter
	 * @return byte array consisting data,key and key splitter
	 */
	public static byte[] combineByteArray(byte[] data, byte[] key, String keySplitter) {
		byte[] keySplitterBytes = keySplitter.getBytes();
		byte[] combinedArray = new byte[key.length + keySplitterBytes.length + data.length];
		System.arraycopy(key, 0, combinedArray, 0, key.length);
		System.arraycopy(keySplitterBytes, 0, combinedArray, key.length, keySplitterBytes.length);
		System.arraycopy(data, 0, combinedArray, key.length + keySplitterBytes.length, data.length);
		return combinedArray;
	}

	/**
	 * Get splitter index for detaching key splitter from key and data
	 * 
	 * @param encryptedData     whole encrypted data
	 * @param keyDemiliterIndex keySplitterindex initialization value
	 * @param keySplitter       keysplitter value
	 * @return keyDemiliterIndex
	 */
	public static int getSplitterIndex(byte[] encryptedData, int keyDemiliterIndex, String keySplitter) {
		final byte keySplitterFirstByte = keySplitter.getBytes()[0];
		final int keySplitterLength = keySplitter.length();
		for (byte data : encryptedData) {
			if (data == keySplitterFirstByte) {
				final String keySplit = new String(
						copyOfRange(encryptedData, keyDemiliterIndex, keyDemiliterIndex + keySplitterLength));
				if (keySplitter.equals(keySplit)) {
					break;
				}
			}
			keyDemiliterIndex++;
		}
		return keyDemiliterIndex;
	}

	/**
	 * Encodes to BASE64 URL Safe
	 * 
	 * @param data data to encode
	 * @return encoded data
	 */
	public static String encodeBase64(byte[] data) {
		return Base64.encodeBase64URLSafeString(data);
	}

	/**
	 * Encodes to BASE64 String
	 * 
	 * @param data data to encode
	 * @return encoded data
	 */
	public static String encodeBase64String(byte[] data) {
		return Base64.encodeBase64String(data);
	}

	/**
	 * Decodes from BASE64
	 * 
	 * @param data data to decode
	 * @return decoded data
	 */
	public static byte[] decodeBase64(String data) {
		return Base64.decodeBase64(data);
	}

}