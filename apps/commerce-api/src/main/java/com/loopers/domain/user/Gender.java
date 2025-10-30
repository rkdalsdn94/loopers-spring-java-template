package com.loopers.domain.user;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("남자"), FEMALE("여자")
    ;

    private final String message;
    Gender(String name) {
        this.message = name;
    }
}
