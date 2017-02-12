package z9.cloud;

import java.io.Serializable;

/**
 * Created by david on 2/8/17.
 */
public class Foo implements Serializable {
    private String name;

    private int age;

    public Foo(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
