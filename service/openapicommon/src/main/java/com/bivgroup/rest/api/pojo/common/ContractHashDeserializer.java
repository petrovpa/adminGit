/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.rest.api.pojo.common;

import com.bivgroup.rest.api.system.crypto.ContractHashCrypter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 *
 * @author mmamaev
 *
 */
public class ContractHashDeserializer extends StdDeserializer<ContractHash> {

    private final ContractHashCrypter contractHashCrypter = new ContractHashCrypter();

    public ContractHashDeserializer() {
        super(ContractHash.class);
    }

    @Override
    public ContractHash deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        //String currentName = jsonParser.getCurrentName();
        String value = jsonParser.getValueAsString();
        ContractHash contractHash;
        try {
            contractHash = contractHashCrypter.makeContractDecryptedHash(value);
        } catch (SecurityException ex) {
            contractHash = new ContractHash();
            contractHash.setIsCorrect(Boolean.FALSE);
        }
        return contractHash;
    }

}
