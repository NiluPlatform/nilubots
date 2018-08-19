package tech.nilu.bots.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.nilu.bots.ActionCodes;
import tech.nilu.bots.dto.response.BaseResponse;
import tech.nilu.bots.exception.ApplicationException;

@Component
public class ResponseErrorParser {
    @Autowired
    protected ObjectMapper mapper;


    public ResponseErrorParser() {
        System.out.println("ResponseErrorParser");
    }

    public ApplicationException throwException(int responseStatus, String errorBody, Exception defaultExc) {

        try {
            BaseResponse responseDto = mapper.readValue(errorBody, BaseResponse.class);
            throw new ApplicationException(responseStatus
                    , responseDto.getError()
                    , responseDto.getMessage());
        } catch (ApplicationException be) {
            throw be;
        } catch (Exception ife) {
            throw new ApplicationException(responseStatus, ActionCodes.UNKNOWN_ERROR, defaultExc);
        }

    }
}
