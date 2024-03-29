package com.guestbook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementStrategy {
    PreparedStatement makeConnection(Connection connection) throws SQLException;
}
