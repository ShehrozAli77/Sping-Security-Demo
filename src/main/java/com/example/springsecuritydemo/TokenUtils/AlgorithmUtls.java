package com.example.springsecuritydemo.TokenUtils;

import com.auth0.jwt.algorithms.Algorithm;

public class AlgorithmUtls {

    public static Algorithm getAlgorithm(){
        return Algorithm.HMAC256("Munch-ON");
    }

}
