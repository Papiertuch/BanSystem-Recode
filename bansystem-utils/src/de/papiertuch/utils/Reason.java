package de.papiertuch.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Reason {

    private final String name;
    private final int id;
    private final String duration;
    private final int points;
    private final boolean reportReason;
}
