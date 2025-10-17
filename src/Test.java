import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;

public class Test {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Test object
        TestUser user = new TestUser("John", 30);

        // Convert to JSON
        String json = mapper.writeValueAsString(user);
        System.out.println("JSON: " + json);

        // Convert back from JSON
        TestUser parsedUser = mapper.readValue(json, TestUser.class);
        System.out.println("Name: " + parsedUser.getName());
        user.readFromFileJson("/main.json");
        read("/main.json ");
    }

    public static void read(String fileName)
    {
        try {
            BufferedReader br = new BufferedReader(new java.io.FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}


class TestUser {
    private String name;
    private int age;
    private ObjectMapper mapper = new ObjectMapper();

    // Default constructor required
    public TestUser() {}

    public TestUser(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void readFromFileJson(String fileName) {
        // Input validation
        if (fileName == null || fileName.trim().isEmpty()) {
            System.err.println("File name cannot be null or empty");
            return;
        }

        File file = new File(fileName);
        if (!file.exists() || !file.isFile()) {
            System.err.println("File does not exist or is not a regular file: " + fileName);
            return;
        }

        try {
            TestUser temp = mapper.readValue(file, TestUser.class);

            // Validate the deserialized object
            if (temp != null) {
                this.name = temp.getName();
                this.age = temp.getAge();
                System.out.println("Name: " + this.name + " Age: " + this.age);
            } else {
                System.err.println("Deserialized object is null");
            }

        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON content: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading file '" + fileName + "': " + e.getMessage());
        }
    }

    // Getters and setters required
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
