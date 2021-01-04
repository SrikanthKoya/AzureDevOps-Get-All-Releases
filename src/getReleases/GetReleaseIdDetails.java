package getReleases;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.parser.*;

import getReleases.GetFinalDetails;

import java.util.ArrayList;
import java.util.Scanner;

import java.io.FileReader;
import java.io.FileWriter;

public class GetReleaseIdDetails {

	static String project = null;
	static String token = null;
	static String currentDirectory = System.getProperty("user.dir");
	static String projectid = null;

	public static void main(String[] args) throws IOException, ParseException {

		File myObj = new File(currentDirectory + "/propFile.properties");
		Scanner myReader = new Scanner(myObj);
		String token1 = null;
		while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			if (data.startsWith("token")) {
				token1 = data.trim().substring("token".length() + 1);
			}
			// System.out.println(data);
		}
		myReader.close();

		token = token1;

		String line0 = Files.readAllLines(Paths.get(currentDirectory + "/release")).get(0);
		String line1 = line0.substring(0, line0.indexOf("_apis") - 1);
		String[] splits = line1.split("/");
		projectid = splits[3] + "/" + splits[4];
		System.out.println("projectid : " + projectid);

		BufferedReader reader;
		try {
			System.out.println("Executing API to get all the release ids for each definition....");
			reader = new BufferedReader(new FileReader(currentDirectory + "/release"));
			String line = reader.readLine();
			while (line != null) {
				// System.out.println(line);

				String[] split = line.split("@");
				project = split[0];
				String mainsecondSubString = split[1];
				// System.out.println(mainsecondSubString);
				sendGET(mainsecondSubString);
				// System.out.println("GET DONE");
				// read next line
				line = reader.readLine();
			}
			reader.close();
			System.out.println("Done !!!");
			// String[] arguments = new String[] { token };

			File f = new File(currentDirectory + "/" + "release");

			// Check if the specified file Exists or not

			try {
				if (f.exists()) {
					String fileContent = "RELEASE_NAME" + "," + "PROJECT" + "," + "STATUS" + "," + "ENVIRONMENT" + ","
							+ "DATETIME";
					BufferedWriter out = new BufferedWriter(new FileWriter(currentDirectory + "/output.csv", true));
					out.write(fileContent);
					out.write("\r\n");
					out.close();
					GetFinalDetails.main(args);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Files.deleteIfExists(Paths.get(currentDirectory + "/" + "release"));
				Files.deleteIfExists(Paths.get(currentDirectory + "/" + "id"));
				System.exit(1);
			}

		} catch (IOException e) {
			Files.deleteIfExists(Paths.get(currentDirectory + "/" + "release"));
			Files.deleteIfExists(Paths.get(currentDirectory + "/" + "id"));
			e.printStackTrace();
		}
	}

	private static void sendGET(String URL) throws IOException, ParseException {
		URL obj = new URL(URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Authorization", "Basic " + token);
		int responseCode = con.getResponseCode();
		// System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String resp = response.toString();

			ArrayList<Integer> indexes = new ArrayList<Integer>();
			ArrayList<String> releaseids = new ArrayList<String>();

			int index = resp.indexOf("currentRelease");
			while (index >= 0) {
				// System.out.println(index);
				indexes.add(index);
				index = resp.indexOf("currentRelease", index + 1);
			}

			for (Integer integer : indexes) {
				try {
					String a1 = resp.substring(integer + 22, integer + 35);
					int x = a1.indexOf(",");
					String y = a1.substring(0, x);
					if (y.equals("0")) {
					} else {
						releaseids.add(
								project + "@https://vsrm.dev.azure.com/" + projectid + "/_apis/Release/releases/" + y);
					}
				} catch (Exception e) {
					// TODO: handle exception
					continue;
				}

			}

			for (String string : releaseids) {
				// System.out.println(string);
				BufferedWriter out = new BufferedWriter(new FileWriter(currentDirectory + "/id", true));
				out.write(string);
				out.write("\r\n");
				out.close();
			}

		} else {
			System.out.println("GET request not worked");
		}

	}

}
