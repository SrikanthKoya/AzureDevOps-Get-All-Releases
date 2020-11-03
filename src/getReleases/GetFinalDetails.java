package getReleases;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GetFinalDetails {

	static String project = null;
	static String token = null;
	static String releaseid = null;
	static String currentDirectory = System.getProperty("user.dir");

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

		BufferedReader reader;
		try {
			System.out.println("Extracting the required details form the release id....");
			reader = new BufferedReader(new FileReader(currentDirectory + "/release-id"));
			String line = reader.readLine();
			while (line != null) {
				// System.out.println(line);

				String[] split = line.split("@");
				project = split[0];
				releaseid = split[1];
				String mainsecondSubString = split[1];
				// System.out.println(mainsecondSubString);
				try {
					sendGET(mainsecondSubString);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					// Files.deleteIfExists(Paths.get(currentDirectory + "/" +
					// "release-file"));
					// Files.deleteIfExists(Paths.get(currentDirectory + "/" +
					// "release-id"));
					System.exit(1);
				}

				// System.out.println("GET DONE");
				// read next line
				// System.out.println("reading next line : "+ line);
				line = reader.readLine();
			}
			reader.close();

			System.out.println("Done !!!");

		} catch (IOException e) {
			Files.deleteIfExists(Paths.get(currentDirectory + "/" + "release-file"));
			Files.deleteIfExists(Paths.get(currentDirectory + "/" + "release-id"));
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

			// parsing file "JSONExample.json"
			Object obj1 = new JSONParser().parse(resp);
			// typecasting obj to JSONObject
			JSONObject json = (JSONObject) obj1;
			String releasename = (String) json.get("name");

			JSONArray value = (JSONArray) json.get("environments");
			Iterator i = value.iterator();

			while (i.hasNext()) {
				JSONObject innerObj = (JSONObject) i.next();
				String url1 = (String) innerObj.get("name");
				String name = (String) innerObj.get("status");
				String modifiedOn = (String) innerObj.get("modifiedOn");
				String fileContent = name + "," + url1;
				BufferedWriter out = new BufferedWriter(new FileWriter(currentDirectory + "/output.csv", true));
				out.write(releasename + "," + project + "," + fileContent + "," + modifiedOn);
				out.write("\r\n");
				out.close();

			}

		} else {
			System.out.println("GET request not worked");
		}

	}

}