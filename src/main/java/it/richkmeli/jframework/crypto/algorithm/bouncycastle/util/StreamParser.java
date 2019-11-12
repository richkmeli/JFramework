package it.richkmeli.jframework.crypto.algorithm.bouncycastle.util;

import java.util.Collection;

public interface StreamParser {
    Object read() throws StreamParsingException;

    Collection readAll() throws StreamParsingException;
}
