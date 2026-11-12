package dev.javabybuilding.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The smallest useful web endpoint. Compare it with lesson 33's ShopHttpServer: there you read the method, split the path, set
 * the headers, wrote the bytes. Here you say WHICH request this method answers and return a plain Java object.
 */
@RestController // a class whose methods answer web requests; what they return is written as the body (JSON for an object)
public class HelloController {

    @GetMapping("/hello") // answers GET /hello
    public Greeting hello(@RequestParam(defaultValue = "world") String name) { // /hello?name=Ana fills name; "world" when missing
        return new Greeting("Hello, " + (name.isBlank() ? "world" : name.trim()) + "!");
    }
}
