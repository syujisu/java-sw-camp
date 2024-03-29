package com.guestbook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcContext {
    final ConnectionMaker connectionMaker;

    public JdbcContext(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    GuestBookVO queryForObject(String sql, Object[] params) throws SQLException {
        StatementStrategy statementStrategy = connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return preparedStatement;
        };
        return jdbcContextForGet(statementStrategy);
    }

    Integer insert(String sql, Object[] params) throws SQLException {
        StatementStrategy statementStrategy = connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"COMMENT_NO"});
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return preparedStatement;
        };
        return jdbcContextInsert(statementStrategy);
    }

    void update(String sql, Object[] params) throws SQLException {
        StatementStrategy statementStrategy = connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return preparedStatement;
        };
        jdbcContextForUpdate(statementStrategy);
    }

    List<GuestBookVO> findAll(String sql) throws SQLException{
        StatementStrategy statementStrategy = connection -> connection.prepareStatement(sql);
        return jdbcContextForFindAll(statementStrategy);
    }

    private GuestBookVO jdbcContextForGet(StatementStrategy statementStrategy) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        GuestBookVO guestBookVO = null;
        try {
            connection = connectionMaker.getConnection();
            preparedStatement = statementStrategy.makeConnection(connection);

            resultSet = preparedStatement.executeQuery();
            // 없을시 null값 반환 되도록
            if (resultSet.next()) {
                guestBookVO = new GuestBookVO();
                guestBookVO.setCommentNo(resultSet.getInt("COMMENT_NO"));
                guestBookVO.setContent(resultSet.getString("CONTENT"));
                guestBookVO.setUpdateDate(resultSet.getString("UPDATE_DATE"));
            }
        } finally {
            closeFinally(connection, preparedStatement, resultSet);
        }

        return guestBookVO;
    }

    private List<GuestBookVO> jdbcContextForFindAll(StatementStrategy statementStrategy) throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<GuestBookVO> list = new ArrayList<>();
        try {
            connection = connectionMaker.getConnection();
            preparedStatement = statementStrategy.makeConnection(connection);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                GuestBookVO guestBookVO = new GuestBookVO();
                guestBookVO.setCommentNo(resultSet.getInt("COMMENT_NO"));
                guestBookVO.setContent(resultSet.getString("CONTENT"));
                guestBookVO.setUpdateDate(resultSet.getString("UPDATE_DATE"));
                list.add(guestBookVO);
            }
        } finally {
            closeFinally(connection, preparedStatement, resultSet);
        }
        return list;
    }

    private Integer jdbcContextInsert(StatementStrategy statementStrategy) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Integer commentNo = null;
        ResultSet resultSet = null;
        try {
            connection = connectionMaker.getConnection();
            preparedStatement = statementStrategy.makeConnection(connection);

            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();

            commentNo = Math.toIntExact(resultSet.getLong(1));
        } finally {
            closeFinally(connection, preparedStatement, resultSet);
        }
        return commentNo;
    }

    private void jdbcContextForUpdate(StatementStrategy statementStrategy) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionMaker.getConnection();
            preparedStatement = statementStrategy.makeConnection(connection);

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null)
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    private void closeFinally(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
