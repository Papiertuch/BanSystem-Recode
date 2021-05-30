package de.papiertuch.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Reason {

    private String name;
    private int id;
    private String duration;
    private int points;
    private boolean reportReason;
}
