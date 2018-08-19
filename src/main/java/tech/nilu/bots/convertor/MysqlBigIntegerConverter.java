package tech.nilu.bots.convertor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigInteger;

@Converter
public class MysqlBigIntegerConverter implements AttributeConverter<BigInteger,String> {
    @Override
    public String convertToDatabaseColumn(BigInteger attribute) {
        return attribute != null? attribute.toString() : null;
    }

    @Override
    public BigInteger convertToEntityAttribute(String dbData) {
        return dbData != null ? new BigInteger(dbData) : null;
    }
}
