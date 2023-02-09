package it.thedarksword.essentialsvc.objets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Tuple<A, B> {

    private final A a;
    private final B b;
}
