package org.example.catan;

import lombok.Getter;

@Getter
public abstract class Resource {
    public String name;
    public Resource(String name) {
        this.name = name;
    }
}
