package it.richkmeli.jframework.database;

import com.sun.tools.internal.ws.processor.model.ModelException;

import java.util.List;

public interface DatabaseModel {
    public boolean createSchema() throws ModelException;

    public boolean createTables() throws ModelException;
}
