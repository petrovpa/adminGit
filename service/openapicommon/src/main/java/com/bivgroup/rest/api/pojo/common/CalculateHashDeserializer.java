/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.rest.api.pojo.common;

import com.bivgroup.rest.api.system.crypto.CalculateHashCrypter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 *
 * @author mmamaev
 */
public class CalculateHashDeserializer extends StdDeserializer<CalculateHash> {

    private final CalculateHashCrypter calculateHashCrypter = new CalculateHashCrypter();

    public CalculateHashDeserializer() {
        super(CalculateHash.class);
    }

    @Override
    public CalculateHash deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        //String currentName = jsonParser.getCurrentName();
        String value = jsonParser.getValueAsString();
        CalculateHash calculateHash;
        try {
            calculateHash = calculateHashCrypter.makeCalculateDecryptedHash(value);
        } catch (SecurityException ex) {
            calculateHash = new CalculateHash();
            calculateHash.setIsCorrect(Boolean.FALSE);
        }
        return calculateHash;
    }

}
