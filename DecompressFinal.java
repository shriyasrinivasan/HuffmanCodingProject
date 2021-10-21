import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class DecompressFinal {

	// Check parameters & get the file names from the parameters
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		if (args.length != 2) {
			System.out.println("Usage: java groupProject sourceFile targetFile");
			System.exit(1);
		}

		File sourceFile = new File(args[0]);
		if (!sourceFile.exists()) {
			System.out.println("File " + args[0] + " does not exist");
			System.exit(2);
		}

		FileInputStream input = new FileInputStream(args[0]);
		ObjectInputStream objectInput = new ObjectInputStream(input);
		String[] codes = (String[])(objectInput.readObject());
		StringBuilder encodedBuilder = new StringBuilder();
		// Read the rest of the file as ints.
		int value;
		while ((value = objectInput.read()) >= 0) {
			String bitstring = String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
			encodedBuilder.append(bitstring.toString());
		}
		objectInput.close();

		String encoded = encodedBuilder.toString();

		// Convert the codes array into a hashmap that stores the code as the key and the character as the value.
		HashMap<String, Character> codeMap = new HashMap<>();
		for (int i = 0; i < codes.length; i++) {
			String code = codes[i];
			Character c = (char)i;
			codeMap.put(code, c);
		}

		StringBuilder result = new StringBuilder();
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < encoded.length(); i++) {
			code.append(encoded.charAt(i));
			Character c = codeMap.get(code.toString());
			if (c != null) {
				// Found it.
				result.append(c);
				code = new StringBuilder();
			}
		}

		DataOutputStream output = new DataOutputStream(new FileOutputStream(args[1]));
		output.write(result.toString().getBytes());
		output.close();
	}

	public static String getBits(int value) {
		value = value % 256;
		String binaryInteger = "";
		int i = 0;
		int tmp = value >> i;

		for (int j = 0; j < 8; j++) {
			binaryInteger = (tmp & 1) + binaryInteger;
			i++;
			tmp = value >> i;
		} 
		return binaryInteger;
	}
}