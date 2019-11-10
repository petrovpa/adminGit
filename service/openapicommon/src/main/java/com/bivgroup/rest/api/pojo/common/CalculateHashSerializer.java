/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.rest.api.pojo.common;

import com.bivgroup.rest.api.system.crypto.CalculateHashCrypter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 *
 * @author mmamaev
 */
public class CalculateHashSerializer extends StdSerializer<CalculateHash> {

    private final CalculateHashCrypter calculateHashCrypter = new CalculateHashCrypter();

    public CalculateHashSerializer() {
        this(null);
    }

    public CalculateHashSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(CalculateHash calculateHash, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String calculateEncryptedId = calculateHashCrypter.makeCalculateEncryptedHash(calculateHash);
        jsonGenerator.writeString(calculateEncryptedId);
    }

}
