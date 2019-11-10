/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.i900;

import java.io.InputStream;
import java.util.Map;

/**
 *
 * @author reson
 */
public interface Mort900Parser {
    Map<String,Object> parse(InputStream stream) throws Mort900Exception;    
}
