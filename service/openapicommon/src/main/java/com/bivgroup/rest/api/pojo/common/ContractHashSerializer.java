/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.rest.api.pojo.common;

import com.bivgroup.rest.api.system.crypto.ContractHashCrypter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 *
 * @author mmamaev
 */
public class ContractHashSerializer extends StdSerializer<ContractHash> {

    private final ContractHashCrypter contractHashCrypter = new ContractHashCrypter();

    public ContractHashSerializer() {
        this(null);
    }

    public ContractHashSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(ContractHash contractHash, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String contractEncryptedId = contractHashCrypter.makeContractEncryptedHash(contractHash);
        jsonGenerator.writeString(contractEncryptedId);
    }
}
