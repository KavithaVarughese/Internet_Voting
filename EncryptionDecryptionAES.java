import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class EncryptionDecryptionAES {
	static Cipher cipher;

//	public static void main(String[] args) throws Exception
//	{
//		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");			//key generation done automatically. To be sent to voter using KeyEx
//		keyGenerator.init(256);
//		SecretKey secretKey = keyGenerator.generateKey();
//		cipher = Cipher.getInstance("AES");
//
//		String plainText = "cS PrOJecT WoWOwoWOwOwOwOw";
//		System.out.println("Plain Text Before Encryption: " + plainText);
//
//		String encryptedText = encrypt(plainText, secretKey);
//		System.out.println("Encrypted Text After Encryption: " + encryptedText);
//
//		String decryptedText = decrypt(encryptedText, secretKey);
//		System.out.println("Decrypted Text After Decryption: " + decryptedText);
//	}

	public static String encrypt(String plainText, SecretKey secretKey)
			throws Exception {
		cipher = Cipher.getInstance("AES");
		byte[] plainTextByte = plainText.getBytes();
//		System.out.println("Cipher call");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//		System.out.println("Cipher done");
		byte[] encryptedByte = cipher.doFinal(plainTextByte);
		Base64.Encoder encoder = Base64.getEncoder();
		String encryptedText = encoder.encodeToString(encryptedByte);
		return encryptedText;
	}

	public static String decrypt(String encryptedText, SecretKey secretKey)
			throws Exception {
		cipher = Cipher.getInstance("AES");
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] encryptedTextByte = decoder.decode(encryptedText);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
		String decryptedText = new String(decryptedByte);
		return decryptedText;
	}
}