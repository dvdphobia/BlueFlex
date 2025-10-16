import com.fasterxml.jackson.databind.ObjectMapper;

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
    }
}

class TestUser {
    private String name;
    private int age;

    // Default constructor required
    public TestUser() {}

    public TestUser(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters and setters required
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}