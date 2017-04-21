package org.baeldung.persistence;


import org.hibernate.dialect.PostgreSQL9Dialect;

/**
 * Created by Atanas Alexandrov on 21.04.17.
 */
public class CustomPostgreSQLDialect extends PostgreSQL9Dialect {
    @Override
    public boolean dropConstraints() {
        return false;
    }
}
