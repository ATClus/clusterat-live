package com.clusterat.live.model;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PostgreSQLEnumType implements UserType<AnalysisType> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<AnalysisType> returnedClass() {
        return AnalysisType.class;
    }

    @Override
    public boolean equals(AnalysisType x, AnalysisType y) {
        return x == y;
    }

    @Override
    public int hashCode(AnalysisType x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public AnalysisType nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(position);
        if (value == null) {
            return null;
        }
        return AnalysisType.fromValue(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, AnalysisType value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.getValue(), Types.OTHER);
        }
    }

    @Override
    public AnalysisType deepCopy(AnalysisType value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(AnalysisType value) {
        return value;
    }

    @Override
    public AnalysisType assemble(Serializable cached, Object owner) {
        return (AnalysisType) cached;
    }

    @Override
    public AnalysisType replace(AnalysisType detached, AnalysisType managed, Object owner) {
        return detached;
    }
}

