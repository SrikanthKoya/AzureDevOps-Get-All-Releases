package getReleases;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import getReleases.GetReleaseIdDetails;

import java.util.Iterator;
import java.util.Scanner;

public class Main {

	static String currentDirectory = System.getProperty("user.dir");
	static String project = null;
	static String token = null;

	public static void main(String[] args) throws ParseException, IOException {

		Files.deleteIfExists(Paths.get(currentDirectory + "/" + "release"));
		Files.deleteIfExists(Paths.get(currentDirectory + "/" + "id"));
		Files.deleteIfExists(Paths.get(currentDirectory + "/" + "output.csv"));

		java.util.Date startdate = new java.util.Date();
		System.out.println("Job Started at : " + startdate);

		if (args.length != 1) {
			System.err.println("Usage: java -jar <jar-name> <propFile.properties>");
			System.err.println("The <propFile.properties> must contain the project=** and token=** details");
			System.exit(1);
		}
		File myObj = new File(currentDirectory + "/propFile.properties");
		Scanner myReader = new Scanner(myObj);
		String project1 = null;
		String token1 = null;
		while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			if (data.startsWith("project")) {
				project1 = data.trim().substring("project".length() + 1);
			}
			if (data.startsWith("token")) {
				token1 = data.trim().substring("token".length() + 1);
			}
		}
		myReader.close();

		project = project1;
		token = token1;

		try {
			System.out.println("Executing API to get all the release definitions for the project " + project + "....");
			URL url = new URL("https://vsrm.dev.azure.com/" + project + "/_apis/release/definitions?api-version=5.1");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", "Basic " + token);
			System.out.println("current directory is : "+ currentDirectory);
			System.out.println("https://vsrm.dev.azure.com/" + project + "/_apis/release/definitions?api-version=5.1");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output = br.readLine();

			// parsing file "JSONExample.json"
			Object obj = new JSONParser().parse(output);
			// typecasting obj to JSONObject
			JSONObject json = (JSONObject) obj;
			long count = (long) json.get("count");

			JSONArray value = (JSONArray) json.get("value");
			Iterator i = value.iterator();
			System.out.println("total number of release definitions found : " + count);
			String a[] = new String[500];

			while (i.hasNext()) {
				JSONObject innerObj = (JSONObject) i.next();
				String url1 = (String) innerObj.get("url");
				String name = (String) innerObj.get("name");
				String fileContent = name + "@" + url1;
//				System.out.println(fileContent);
				BufferedWriter out = new BufferedWriter(new FileWriter(currentDirectory +"/"+ "release", true));
				out.write(fileContent);
				out.write("\r\n");
				out.close();
			}

			conn.disconnect();

			File f = new File(currentDirectory + "/" + "release");

			// Check if the specified file Exists or not
			if (f.exists()) {
				GetReleaseIdDetails.main(args);
			}

			Files.deleteIfExists(Paths.get(currentDirectory + "/" + "release"));
			Files.deleteIfExists(Paths.get(currentDirectory + "/" + "id"));

			try {
				System.gc();
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			java.util.Date enddate = new java.util.Date();
			System.out.println("Job end at : " + enddate);

		} catch (MalformedURLException e) {

			e.printStackTrace();
			Files.deleteIfExists(Paths.get(currentDirectory + "/" + "release"));
			Files.deleteIfExists(Paths.get(currentDirectory + "/" + "id"));

		} catch (IOException e) {

			e.printStackTrace();
			Files.deleteIfExists(Paths.get(currentDirectory + "/" + "release"));
			Files.deleteIfExists(Paths.get(currentDirectory + "/" + "id"));

		}

	}

}
